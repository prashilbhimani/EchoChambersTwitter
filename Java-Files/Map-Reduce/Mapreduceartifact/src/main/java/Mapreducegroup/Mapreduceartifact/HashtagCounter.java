package Mapreducegroup.Mapreduceartifact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.json.JSONArray;
import org.json.JSONObject;



public class HashtagCounter {

	public static class HashTagMapper extends Mapper<Object, Text, Text, IntWritable> {		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException { 			
			String tweetJson = value.toString();			
			ArrayList<String> allHashtags=new ArrayList<String>();			
			try {	
				JSONObject tweet = new JSONObject(tweetJson);												
				if(tweet.has("entities") && tweet.getJSONObject("entities").has("hashtags")) {
					System.out.println("Got it in the outside entity!");
					JSONArray hashtags = tweet.getJSONObject("entities").getJSONArray("hashtags");
					System.out.println("hashtags: " + hashtags);
					for(int i =0; i < hashtags.length(); i++) {
						allHashtags.add(hashtags.getJSONObject(i).getString("text"));					
					}
				}

				if(tweet.has("retweeted_status")) {					
					JSONObject retweetedStatus = tweet.getJSONObject("retweeted_status");										
					if( retweetedStatus.has("extended_tweet") && retweetedStatus.getJSONObject("extended_tweet").has("entities") && retweetedStatus.getJSONObject("extended_tweet").getJSONObject("entities").has("hashtags")) {
						System.out.println("Got it from the retweeted status");
						JSONArray hashtags = retweetedStatus.getJSONObject("extended_tweet").getJSONObject("entities").getJSONArray("hashtags");												
						for(int i=0; i< hashtags.length(); i++) {
							JSONObject hashtagObj = hashtags.getJSONObject(i);
							//System.out.println(hashtagObj.getString("text"));
							allHashtags.add(hashtagObj.getString("text"));
						}
					}
				}

			} catch(Exception e) {
				e.printStackTrace();
			}

			Text wordOut = new Text();
			IntWritable one = new IntWritable(1);

			for(int i=0; i< allHashtags.size(); i++) {
				String modifiedHashTag = allHashtags.get(i).toLowerCase();
				if(!modifiedHashTag.isEmpty()) {
					wordOut.set(modifiedHashTag);
					context.write(wordOut, one);
				}

			}
		}

	}

	public static class HashtagReducer extends Reducer<Text, IntWritable, Text, IntWritable> {		
		public void reduce(Text term, Iterable<IntWritable> ones, Context context) throws IOException, InterruptedException {

			int count = 0;
			Iterator<IntWritable> iterator = ones.iterator();
			while (iterator.hasNext()) {
				count++;
				iterator.next();
			}
			IntWritable output = new IntWritable(count);
			context.write(term, output);
			/* countMap.put(term.toString(), output);
				System.out.println("Term: "+ term.toString() +" Added to countMap: "+ countMap); */
		}
	}

	public static void main(String []args) throws IOException, ClassNotFoundException, InterruptedException {
		System.out.println("Starting ...");
		Configuration conf = new Configuration();

		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		if(otherArgs.length != 2) {
			System.err.println("Usage: Wordcount <if> <dir>");
		}
		JobConf f=new JobConf();
		f.setNumReduceTasks(1);
		System.out.println("Setting Job");		
		Job job = Job.getInstance(conf, "Job name: WordCount");
		job.setJarByClass(HashtagCounter.class);
		job.setMapperClass(HashTagMapper.class);
		job.setReducerClass(HashtagReducer.class);
		FileInputFormat.setInputDirRecursive(job, true);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setNumReduceTasks(1);

		System.out.println("Setting files");
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.out.println("Finished Setting files.. Starting to wait for Status");

		boolean status = job.waitForCompletion(true);
		System.out.println("Staus is: " + status);

		if(status) {
			System.exit(0);
		} else {
			System.exit(1);
		}
		System.out.println("Done... with job 1");
	}


}