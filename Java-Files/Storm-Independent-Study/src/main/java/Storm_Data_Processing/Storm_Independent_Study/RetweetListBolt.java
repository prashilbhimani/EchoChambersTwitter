package Storm_Data_Processing.Storm_Independent_Study;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.JSONObject;

public class RetweetListBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;
	Map<String, Integer> counts = new HashMap<String, Integer>();
	private OutputCollector collector;

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;

	}

	public void execute(Tuple input) {		
		try {			
			JSONObject tweetJson = (JSONObject) input.getValueByField("tweetjson");
			if(tweetJson.has("retweeted_status") && tweetJson.has("id_str") && tweetJson.getJSONObject("retweeted_status").has("user") && tweetJson.getJSONObject("retweeted_status").getJSONObject("user").has("id_str")) {
				String retweeted_userid = tweetJson.getJSONObject("retweeted_status").getJSONObject("user").getString("id_str");
				String new_userid=tweetJson.getJSONObject("user").getString("id_str");
				JSONObject result = new JSONObject();
				result.put("from", new_userid);
				result.put("to", retweeted_userid);
				result.put("new_tweet_id", tweetJson.getString("id_str"));
				result.put("old_tweet_id",tweetJson.getJSONObject("retweeted_status").getString("id_str"));
				//System.out.println("Retweets: " + result.toString());
				collector.emit(new Values(result));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block	
			System.out.println("Couldnt parse!!!!");
			e.printStackTrace();
		}			
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("retweets"));
	}

}
