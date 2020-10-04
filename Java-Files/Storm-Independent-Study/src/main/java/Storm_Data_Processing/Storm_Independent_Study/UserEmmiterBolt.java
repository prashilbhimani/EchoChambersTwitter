package Storm_Data_Processing.Storm_Independent_Study;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class UserEmmiterBolt extends BaseRichBolt {
    private OutputCollector collector;

    public void prepare(Map stormConf, TopologyContext context,
                        OutputCollector collector) {
        this.collector = collector;

    }

    public void execute(Tuple input){
        try{
            JSONObject j=(JSONObject) input.getValueByField("tweetjson");
            if(j.has("user") && j.getJSONObject("user").has("id_str")) {
                String user_id=j.getJSONObject("user").getString("id_str");
                //System.out.println(user_id);
                collector.emit(new Values(user_id));
            }
            if(j.has("retweeted_status") && j.getJSONObject("retweeted_status").has("user")){
                String re_user_id=j.getJSONObject("retweeted_status").getJSONObject("user").getString("id_str");
                //System.out.println(re_user_id);
                collector.emit(new Values(re_user_id));
            }
            if(j.has("entities")&&j.getJSONObject("entities").has("user_mentions")){
                JSONArray x=j.getJSONObject("entities").getJSONArray("user_mentions");
                for(int i=0;i<x.length();i++){
                    JSONObject k= x.getJSONObject(i);
                    if(k.has("id_str")){
                        //System.out.println(k.getString("id_str"));
                        collector.emit(new Values(k.getString("id_str")));
                    }
                }
            }

        }catch (Exception e){
            System.out.println("Something wrong");
        }
    }
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("user_id"));
    }
}
