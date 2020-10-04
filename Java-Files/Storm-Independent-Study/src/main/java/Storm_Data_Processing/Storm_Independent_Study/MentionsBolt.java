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
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class MentionsBolt extends BaseRichBolt {
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
			if(tweetJson.has("id_str") && tweetJson.has("entities") && tweetJson.getJSONObject("entities").has("user_mentions") && tweetJson.getJSONObject("entities").getJSONArray("user_mentions").length() > 0) {
				JSONArray usermentions = tweetJson.getJSONObject("entities").getJSONArray("user_mentions");
				for(int i=0; i< usermentions.length(); i++) {
					JSONObject usermention = usermentions.getJSONObject(i);
					String id = usermention.getString("id_str");
					if(id==null) continue;
					if(id.length()<1) continue;
					JSONObject result = new JSONObject();
					result.put("from", tweetJson.getJSONObject("user").getString("id_str"));
					result.put("to", id);
					result.put("tweet", tweetJson.getString("id_str"));
					//System.out.println("MentionsBolt: " + result.toString());
					collector.emit(new Values(result));
				}												
								
			}
			
			if(tweetJson.has("retweeted_status") && tweetJson.getJSONObject("retweeted_status").has("entities") && tweetJson.getJSONObject("retweeted_status").getJSONObject("entities").has("user_mentions") && tweetJson.getJSONObject("retweeted_status").getJSONObject("entities").getJSONArray("user_mentions").length() > 0) {
				JSONArray usermentions = tweetJson.getJSONObject("retweeted_status").getJSONObject("entities").getJSONArray("user_mentions");
				for(int i=0; i< usermentions.length(); i++) {
					JSONObject usermention = usermentions.getJSONObject(i);
					String id = usermention.getString("id_str");
					if(id==null) continue;
					if(id.length()<1) continue;
					JSONObject result = new JSONObject();
					String id_str = tweetJson.getJSONObject("retweeted_status").getJSONObject("user").getString("id_str");
					result.put("from", id_str);
					result.put("to", id);
					result.put("tweet", tweetJson.getString("id_str"));
					//System.out.println("MentionsBolt: " + result.toString());
					collector.emit(new Values(result));
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block	
			System.out.println("Couldnt parse!!!!");
			e.printStackTrace();
		}			
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {

		declarer.declare(new Fields("usermentions"));
	}

}
