package Storm_Data_Processing.Storm_Independent_Study;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class HashtagCounterBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;
	Map<String, Integer> counts = new HashMap<String, Integer>();
	private OutputCollector collector;

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;

	}

	public void execute(Tuple input, BasicOutputCollector collector) {		
		try {			
			JSONObject tweetJson = (JSONObject) input.getValueByField("tweetjson");						
			if(tweetJson.has("id_str") && tweetJson.has("entities") && tweetJson.getJSONObject("entities").has("hashtags") && tweetJson.getJSONObject("entities").getJSONArray("hashtags").length() > 0) {
				JSONArray hashtags = tweetJson.getJSONObject("entities").getJSONArray("hashtags");
				for(int i=0; i< hashtags.length(); i++) {
					String hashtag = hashtags.getJSONObject(i).getString("text");					
					JSONObject result = new JSONObject();															
					Integer count = counts.get(hashtag);
					if (count == null){
						count = 0;  
					}
					count++;
					counts.put(hashtag, count);
					result.put(hashtag, count);
					System.out.println("HashtagCounterBolt: " + result.toString());
					collector.emit(new Values(result.toString()));
				}												
								
			}
			
			if(tweetJson.has("retweeted_status") && tweetJson.has("id_str") && tweetJson.getJSONObject("retweeted_status").has("entities") && tweetJson.getJSONObject("retweeted_status").getJSONObject("entities").has("hashtags") && tweetJson.getJSONObject("retweeted_status").getJSONObject("entities").getJSONArray("hashtags").length() > 0) {
				JSONArray usermentions = tweetJson.getJSONObject("retweeted_status").getJSONObject("entities").getJSONArray("hashtags");
				for(int i=0; i< usermentions.length(); i++) {
					String hashtag = usermentions.getJSONObject(i).getString("text");					
					JSONObject result = new JSONObject();															
					Integer count = counts.get(hashtag);
					if (count == null){
						count = 0;  
					}
					count++;					
					counts.put(hashtag, count);
					result.put(hashtag, count);
					System.out.println("HashtagCounterBolt: " + result.toString());
					collector.emit(new Values(result.toString()));
				}												
								
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block	
			System.out.println("Couldnt parse!!!!");
			e.printStackTrace();
		}			
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("hashtags"));
	}

}
