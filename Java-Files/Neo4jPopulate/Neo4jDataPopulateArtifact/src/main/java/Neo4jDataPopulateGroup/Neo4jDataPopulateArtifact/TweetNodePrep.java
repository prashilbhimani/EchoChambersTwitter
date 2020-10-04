package Neo4jDataPopulateGroup.Neo4jDataPopulateArtifact;

import java.awt.event.TextEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.protobuf.IpcConnectionContextProtos.UserInformationProto;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.json.JSONArray;
import org.json.JSONObject;

public class TweetNodePrep {

	public static String getUserId(JSONObject tweet) {
		if (tweet.has("user")) {
			String id_str = tweet.getJSONObject("user").getString("id_str");
			return id_str;
		} else {
			System.out.println("Didnt find any userId");
			return "-1";
		}
	}

	public static String getTweetText(JSONObject tweetJson) {
		String text = "";
		if (tweetJson.has("extended_tweet")) {
			text += tweetJson.getJSONObject("extended_tweet").getString("full_text");
		} else {
			if (tweetJson.has("text")) {
				text += tweetJson.getString("text");
			}
		}

		return text;
	}

	public static String getTweetId(JSONObject tweetJson) {
		if (tweetJson.has("id_str")) {
			return tweetJson.getString("id_str");
		}
		return null;
	}

	public static JSONObject getUserInfo(JSONObject tweet) {
		JSONObject output = new JSONObject();
		if (tweet.has("user")) {
			output.put("id_str", tweet.getJSONObject("user").getString("id_str"));
			output.put("screen_name", tweet.getJSONObject("user").getString("screen_name"));
			output.put("verified", tweet.getJSONObject("user").getBoolean("verified"));
			output.put("followers_count", tweet.getJSONObject("user").getInt("followers_count"));
			output.put("friends_count", tweet.getJSONObject("user").getInt("friends_count"));
			output.put("statuses_count", tweet.getJSONObject("user").getInt("statuses_count"));
			if (tweet.getJSONObject("user").has("derived")
					&& tweet.getJSONObject("user").getJSONObject("derived").has("locations")
					&& tweet.getJSONObject("user").getJSONObject("derived").getJSONArray("locations").getJSONObject(0)
					.has("country_code")) {
				output.put("country_code", tweet.getJSONObject("user").getJSONObject("derived")
						.getJSONArray("locations").getJSONObject(0).getString("country_code"));
			} else {
				output.put("country_code", "null");
			}
		} else {
			System.out.println("Unable to fetch userinfo for: " + tweet.toString());
		}
		return output;
	}

	public static JSONObject getTweet(JSONObject tweet, String tweetkey) {
		// gets the tweetJson for keys like "retweets"
		if (tweet.has(tweetkey)) {
			return tweet.getJSONObject(tweetkey);
		} else {
			return null;
		}
	}

	public static TreeSet<String> getHashtags(JSONObject tweetJson) {
		TreeSet<String> hashtags = new TreeSet<String>();
		if (tweetJson.has("extended_tweet") && tweetJson.getJSONObject("extended_tweet").has("entities")
				&& tweetJson.getJSONObject("extended_tweet").getJSONObject("entities").has("hashtags")) {
			JSONArray ht = tweetJson.getJSONObject("extended_tweet").getJSONObject("entities").getJSONArray("hashtags");
			for (int i = 0; i < ht.length(); i++) {
				JSONObject h = ht.getJSONObject(i);
				if (h.has("text") && !h.isNull("text")) {
					hashtags.add(h.getString("text"));
				}
			}
		} else {
			if (tweetJson.has("entities") && tweetJson.getJSONObject("entities").has("hashtags")) {
				JSONArray ht = tweetJson.getJSONObject("entities").getJSONArray("hashtags");
				for (int i = 0; i < ht.length(); i++) {
					JSONObject h = ht.getJSONObject(i);
					if (h.has("text") && !h.isNull("text")) {
						hashtags.add(h.getString("text"));
					}
				}
			}
		}
		return hashtags;
	}

	public static JSONObject formatTweet(JSONObject tweetJson) {
		if(tweetJson != null) {
			String tweetId = TweetNodePrep.getTweetId(tweetJson);
			String tweetText = TweetNodePrep.getTweetText(tweetJson);
			TreeSet<String> hashtags = TweetNodePrep.getHashtags(tweetJson);
			String userId = TweetNodePrep.getUserId(tweetJson);
			TreeSet<String> urls = TweetNodePrep.getUrls(tweetJson);
			TreeSet<String> mentions = TweetNodePrep.getUserMentions(tweetJson);
			JSONObject result = new JSONObject();
			result.put("tweetId", tweetId);
			result.put("tweetText", tweetText);
			result.put("hashtags", hashtags);
			result.put("userId", userId);
			result.put("urls", urls);
			result.put("mentions", mentions);
			return result;
		} 
		return null;

	}

	public static TreeSet<String> getUserMentions(JSONObject tweetJson) {
		TreeSet<String> mentions = new TreeSet<String>();
		if (tweetJson.has("extended_tweet") && tweetJson.getJSONObject("extended_tweet").has("entities")
				&& tweetJson.getJSONObject("extended_tweet").getJSONObject("entities").has("user_mentions")) {
			JSONArray usermention = tweetJson.getJSONObject("extended_tweet").getJSONObject("entities")
					.getJSONArray("user_mentions");
			for (int i = 0; i < usermention.length(); i++) {
				JSONObject u = usermention.getJSONObject(i);
				if (u.has("id_str") && !u.isNull("id_str")) {
					mentions.add(u.getString("id_str"));
				}
			}
		} else {
			if (tweetJson.has("entities") && tweetJson.getJSONObject("entities").has("user_mentions")) {
				JSONArray usermention = tweetJson.getJSONObject("entities").getJSONArray("user_mentions");
				for (int i = 0; i < usermention.length(); i++) {
					JSONObject u = usermention.getJSONObject(i);
					if (u.has("id_str") && !u.isNull("id_str")) {
						mentions.add(u.getString("id_str"));
					}
				}
			}
		}
		return mentions;

	}

	public static TreeSet<String> getUrls(JSONObject tweetJson) {
		TreeSet<String> urls = new TreeSet<String>();
		if (tweetJson.has("extended_tweet") && tweetJson.getJSONObject("extended_tweet").has("entities")
				&& tweetJson.getJSONObject("extended_tweet").getJSONObject("entities").has("urls")) {
			JSONArray url = tweetJson.getJSONObject("extended_tweet").getJSONObject("entities").getJSONArray("urls");
			for (int i = 0; i < url.length(); i++) {
				JSONObject u = url.getJSONObject(i);
				if (u.has("url") && !u.isNull("url")) {
					urls.add(u.getString("url"));
				}
				if (u.has("expanded_url") && !u.isNull("expanded_url")) {
					urls.add(u.getString("expanded_url"));
				}
			}
		} else {
			if (tweetJson.has("entities") && tweetJson.getJSONObject("entities").has("urls")) {
				JSONArray url = tweetJson.getJSONObject("entities").getJSONArray("urls");
				for (int i = 0; i < url.length(); i++) {
					JSONObject u = url.getJSONObject(i);
					if (u.has("url") && !u.isNull("url")) {
						urls.add(u.getString("url"));
					}
					if (u.has("expanded_url") && !u.isNull("expanded_url")) {
						urls.add(u.getString("expanded_url"));
					}
				}
			}
		}
		return urls;

	}

    private static ArrayList<JSONObject> getTweets(JSONObject tweetJson) {
        ArrayList<JSONObject> tweets=new ArrayList<JSONObject>();
        if(tweetJson!=null){
        	if(getTweet(tweetJson,"retweeted_status")==null)
        		tweets.add(TweetNodePrep.formatTweet(tweetJson));
            tweets.addAll(getTweets(getTweet(tweetJson,"retweeted_status")));
            tweets.addAll(getTweets(getTweet(tweetJson,"quoted_status")));
        }
        return tweets;
    }

	public static class TokenizeMapper extends Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            try {
                JSONObject tweetJson=new JSONObject(value.toString());
                ArrayList<JSONObject> tweets= getTweets(tweetJson);
                for(JSONObject j:tweets){
                    context.write(new Text(j.toString()),new Text(""));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
		}

	}

	public static class SumReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text term, Iterable<Text> ones, Context context) throws IOException, InterruptedException {
			context.write(new Text(term.toString()), new Text(""));

		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		System.out.println("Starting ...");
		Configuration conf = new Configuration();

		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

		if (otherArgs.length != 2) {
			System.err.println("Usage: Wordcount <if> <dir>");
		}
		JobConf f = new JobConf();
		f.setNumReduceTasks(10);
		System.out.println("Setting Job");
		Job job = Job.getInstance(conf, "Job name: WordCount");
		job.setJarByClass(TweetNodePrep.class);
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

		if (status) {
			System.exit(0);
		} else {
			System.exit(1);
		}
		System.out.println("Done... with job 1");
	}





}
