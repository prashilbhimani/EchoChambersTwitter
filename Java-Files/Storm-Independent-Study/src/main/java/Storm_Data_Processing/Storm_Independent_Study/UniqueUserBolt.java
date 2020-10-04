package Storm_Data_Processing.Storm_Independent_Study;

import java.util.HashMap;
import java.util.Map;
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

public class UniqueUserBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;
	Map<String, Integer> counts = new HashMap<String, Integer>();
	private OutputCollector collector;

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;

	}


	public void execute(Tuple input) {		
		try {		
			String id = input.getValueByField("user_id").toString();
			Integer count = counts.get(id);
			if (count == null){
				count = 0;  
			}
			count++;
			counts.put(id, count);
			if(count == 1) {
				//System.out.println(id);
				collector.emit(new Values(id));
			}															
		} catch (Exception e) {
			// TODO Auto-generated catch block	
			System.out.println("Couldnt parse!!!!");
			e.printStackTrace();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("uniqueusers"));
	}

}
