package Neo4jDataPopulateGroup.Neo4jDataPopulateArtifact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.json.JSONArray;
import org.json.JSONObject;

public class RelationshipMapReduce {

	public static String getUserId(JSONObject tweet) {			
		if(tweet.has("user")) {
			String id_str = tweet.getJSONObject("user").getString("id_str");
			return id_str;
		} else {
			System.out.println("Didnt find any userId");
			return "-1";
		}
	}



	public static JSONObject getTweet(JSONObject tweet, String tweetkey) {
		// gets the tweetJson for keys like "retweets"		
		if(tweet.has(tweetkey)) {
			return tweet.getJSONObject(tweetkey);
		} else {
			return null;
		}
	}

	private static ArrayList<JSONObject> getRelationships(JSONObject tweetJson) {
		ArrayList<JSONObject> relationships=new ArrayList<JSONObject>();
		if(tweetJson!=null){
			String userid = getUserId(tweetJson);
			JSONObject retweetedStatus = getTweet(tweetJson, "retweeted_status");
			JSONObject quotedStatus = getTweet(tweetJson, "quoted_status");
			JSONObject j=new JSONObject();
			if(retweetedStatus!=null) {
				String ogtweet = retweetedStatus.getString("id_str");
				j.put("user", userid);
				j.put("tweet", ogtweet);
				j.put("type", "RETWEETED");
				relationships.add(j);
				relationships.addAll(getRelationships(retweetedStatus));
			}else if(quotedStatus!=null){
				String ogtweet=quotedStatus.getString("id_str");
				String tweet_id=tweetJson.getString("id_str");
				j.put("ogtweet",ogtweet);
				j.put("tweet",tweet_id);
				j.put("type","QUOTED");
				relationships.add(j);
				relationships.addAll(getRelationships(quotedStatus));
			} else {
				String tweet_id = tweetJson.getString("id_str");
				j.put("user", userid);
				j.put("tweet", tweet_id);
				j.put("type", "CREATED");
				relationships.add(j);
			}
		}
		return relationships;
	}

	public static class TokenizeMapper extends Mapper<Object, Text, Text, Text> {		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			try {
				JSONObject tweetJson=new JSONObject(value.toString());
				ArrayList<JSONObject> relationships= getRelationships(tweetJson);
				for(JSONObject j:relationships){
					context.write(new Text(j.toString()),new Text(""));
				}

			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static class SumReducer extends Reducer<Text, Text, Text, Text> {		
		public void reduce(Text term, Iterable<Text> ones, Context context) throws IOException, InterruptedException {
			context.write(term, new Text(""));

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
		f.setNumReduceTasks(10);
		System.out.println("Setting Job");		
		Job job = Job.getInstance(conf, "Job name: WordCount");
		job.setJarByClass(RelationshipMapReduce.class);
		job.setMapperClass(TokenizeMapper.class);
		job.setReducerClass(SumReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
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
