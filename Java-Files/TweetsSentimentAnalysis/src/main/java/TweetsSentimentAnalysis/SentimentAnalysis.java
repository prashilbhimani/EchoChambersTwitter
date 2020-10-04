package TweetsSentimentAnalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
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
import org.json.JSONObject;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;


public class SentimentAnalysis {
    private static ArrayList<JSONObject> getSentiments(JSONObject tweetJson) {
        ArrayList<JSONObject> tweets=new ArrayList<JSONObject>();
        if(tweetJson!=null){
        	if(getTweet(tweetJson,"retweeted_status")==null)
        		tweets.add(formatTweet(tweetJson));
            tweets.addAll(getSentiments(getTweet(tweetJson,"retweeted_status")));
            tweets.addAll(getSentiments(getTweet(tweetJson,"quoted_status")));
        }
        return tweets;
    }
	public static JSONObject formatTweet(JSONObject tweetJson) {
		JSONObject result = new JSONObject();
		if(tweetJson != null) {
			result.put("sentiment", (double) getSentiment(SentimentAnalysis.getTweetText(tweetJson)));
			result.put("tweets_text", SentimentAnalysis.getTweetText(tweetJson));
			result.put("tweets_ids", SentimentAnalysis.getTweetId(tweetJson));
			return result;
		} 
		return null;

	}	
	public static JSONObject getTweet(JSONObject tweet, String tweetkey) {
		// gets the tweetJson for keys like "retweets"
		if (tweet.has(tweetkey)) {
			return tweet.getJSONObject(tweetkey);
		} else {
			return null;
		}
	}
	public static String getTweetText(JSONObject tweetJson) {
		String text = "";		
		if(tweetJson.has("extended_tweet") && !tweetJson.getJSONObject("extended_tweet").isNull("full_text")) {			
			text += tweetJson.getJSONObject("extended_tweet").getString("full_text");
		} else {			
			if(tweetJson.has("text") && !tweetJson.isNull("text")) {				
				text += tweetJson.getString("text");
			}
		}		
		return text;
	}
	public static String getTweetId(JSONObject tweetJson)
	{
		if (tweetJson.has("id") && !tweetJson.isNull("id"))
		{
			String id_str = tweetJson.getString("id_str");
			return id_str;
		}
		else{
			System.out.println("Didnt find any tweetID");
			return "-1";
		}


	}
	public static double getSentiment(String tweet_text)
	{	//"Very negative" = 0 "Negative" = 1 "Neutral" = 2 "Positive" = 3 "Very positive" = 4
		Long textLength = 0L;
		int sumOfValues = 0;

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		int mainSentiment = 0;
		if (tweet_text != null && tweet_text.length() > 0) {
			int longest = 0;
			Annotation annotation = pipeline.process(tweet_text);
			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					textLength += partText.length();
					sumOfValues = sumOfValues + sentiment * partText.length();
				}
			}
		}
		double sentiment = sumOfValues/textLength;
		System.out.println("Overall sentiment: " +sentiment);
		return sentiment;
	}
	public static class TokenizeMapper extends Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {		
			try {																
                JSONObject tweetJson=new JSONObject(value.toString());
                ArrayList<JSONObject> tweets= getSentiments(tweetJson);
                for(JSONObject j:tweets){
                    context.write(new Text(j.toString()),new Text(""));				
                }
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

	}
	public static class SentimentReducer extends Reducer<Text, Text, Text, Text>
	{
		public void reduce(Text term, Iterable<Text> ones, Context context) throws IOException, InterruptedException 
		{
			context.write(term, new Text(""));				
		}

	}
	public static void main(String []args) throws IOException, ClassNotFoundException, InterruptedException {
		{
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
			job.setJarByClass(SentimentAnalysis.class);
			job.setMapperClass(TokenizeMapper.class);
			job.setReducerClass(SentimentReducer.class);

			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setNumReduceTasks(3);

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

}
