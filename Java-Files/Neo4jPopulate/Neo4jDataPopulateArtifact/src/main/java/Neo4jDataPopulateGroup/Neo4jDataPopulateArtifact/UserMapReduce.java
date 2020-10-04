package Neo4jDataPopulateGroup.Neo4jDataPopulateArtifact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

import com.google.gson.JsonObject;
import org.apache.avro.data.Json;
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

public class UserMapReduce {

    private static ArrayList<JSONObject> getUsers(JSONObject tweetJson) {
        ArrayList<JSONObject> users=new ArrayList<JSONObject>();
        if(tweetJson!=null){
            users.add(getUserInfo(tweetJson));
            users.addAll(getUsers(getTweet(tweetJson,"retweeted_status")));
            users.addAll(getUsers(getTweet(tweetJson,"quoted_status")));
        }
        return users;
    }
    public static JSONObject getTweet(JSONObject tweet, String tweetkey) {
        // gets the tweetJson for keys like "retweets"
        if(tweet.has(tweetkey)) {
            return tweet.getJSONObject(tweetkey);
        } else {
            return null;
        }
    }

    public static JSONObject getUserInfo(JSONObject tweet) {
        JSONObject output = new JSONObject();
        if(tweet.has("user")) {
            output.put("id_str", tweet.getJSONObject("user").getString("id_str"));
            output.put("screen_name", tweet.getJSONObject("user").getString("screen_name"));
            output.put("verified", tweet.getJSONObject("user").getBoolean("verified"));
            output.put("followers_count", tweet.getJSONObject("user").getInt("followers_count"));
            output.put("friends_count", tweet.getJSONObject("user").getInt("friends_count"));
            output.put("statuses_count", tweet.getJSONObject("user").getInt("statuses_count"));
            if(tweet.getJSONObject("user").has("derived") && tweet.getJSONObject("user").getJSONObject("derived").has("locations") && tweet.getJSONObject("user").getJSONObject("derived").getJSONArray("locations").getJSONObject(0).has("country_code"))
            {
                output.put("country_code", tweet.getJSONObject("user").getJSONObject("derived").getJSONArray("locations").getJSONObject(0).getString("country_code"));
            } else {
                output.put("country_code", "null");
            }
        } else {
            System.out.println("Unable to fetch userinfo for: " + tweet.toString());
        }
        return output;
    }


    public static class TokenizeMapper extends Mapper<Object, Text, Text, Text> {
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            try {
                JSONObject tweetJson=new JSONObject(value.toString());
                ArrayList<JSONObject> users= getUsers(tweetJson);
                for(JSONObject j:users){
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

    public static String getTweetId(JSONObject tweetJson) {
        if(tweetJson.has("id_str")) {
            return tweetJson.getString("id_str");
        }
        return null;
    }


}
