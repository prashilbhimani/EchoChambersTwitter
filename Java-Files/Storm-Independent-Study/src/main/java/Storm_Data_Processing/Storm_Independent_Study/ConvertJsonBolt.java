package Storm_Data_Processing.Storm_Independent_Study;


import java.util.Map;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.json.JSONObject;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

public class ConvertJsonBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}
	
	public void execute(Tuple input, BasicOutputCollector collector) {
		String tweet = input.getStringByField("tweets");
		try {			
			JSONObject tweetjson = new JSONObject(tweet);
			String user_id="";
			String tweet_id="";
			if(tweetjson.has("text")){
				if(tweetjson.has("user") && tweetjson.getJSONObject("user").has("id_str")) {
					user_id=tweetjson.getJSONObject("user").getString("id_str");
				}
				if(tweetjson.has("id_str")){
					tweet_id=tweetjson.getString("id_str");
				}
				collector.emit(new Values(tweetjson));
			}

		} catch (Exception e) {			
			System.out.println("Could not parse tweet: " + tweet);			
		}
	}
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweetjson"));
	}
}