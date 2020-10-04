package Storm_Data_Processing.Storm_Independent_Study;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;
import org.elasticsearch.hadoop.cfg.ConfigurationOptions;
import org.elasticsearch.storm.EsBolt;
import SampleCode.SampleSpout;

//Export this JAR (not Runnable JAR)
// Then ssh on nimbus:
// ./apache-storm-1.2.1/bin/storm jar Test.jar Storm_Data_Processing.Storm_Independent_Study.SampleStormClusterTopology


public class SampleStormClusterTopology {
	public static void main(String[] args) throws AlreadyAliveException,InvalidTopologyException {
		TopologyBuilder builder = new TopologyBuilder();
		Map ESConfig=new HashMap();

		ESConfig.put(ConfigurationOptions.ES_INDEX_AUTO_CREATE,"true");
		ESConfig.put(ConfigurationOptions.ES_INPUT_JSON,"true");
		ESConfig.put(ConfigurationOptions.ES_NODES,"http://"+args[0]);

		Map ESConfig1=new HashMap();
		ESConfig1.put(ConfigurationOptions.ES_INDEX_AUTO_CREATE,"true");
		ESConfig1.put(ConfigurationOptions.ES_NODES,"http://"+args[0]);
		ArrayList<String> files=new ArrayList<String>();
		for(int i=Integer.parseInt(args[1]);i<Integer.parseInt(args[2]);i++){
			files.add("./tweets/"+i+".json");
		}
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
		builder.setSpout("CustomFileReaderSpout", new CustomFileReaderSpout(files));

		builder.setBolt("ConvertJsonBolt", new ConvertJsonBolt(), 4).shuffleGrouping("CustomFileReaderSpout");
//        builder.setBolt("ESJSONObject", new EsBolt("tweets/tweets",ESConfig), 4).addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2).shuffleGrouping("ConvertJsonBolt");

		builder.setBolt("UserEmitter", new UserEmmiterBolt(),4).shuffleGrouping("ConvertJsonBolt");
		builder.setBolt("UniqueUserBolt", new UniqueUserBolt(), 4).fieldsGrouping("UserEmitter",new Fields("user_id"));
		builder.setBolt("ESUniqueUsers", new EsBolt("users/users",ESConfig1), 4).addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2).shuffleGrouping("UniqueUserBolt");

		builder.setBolt("MentionsEmitter", new MentionsBolt(),5).shuffleGrouping("ConvertJsonBolt");
		builder.setBolt("ESMentionsBolt", new EsBolt("mentions/mentions",ESConfig),4).addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2).shuffleGrouping("MentionsEmitter");

		builder.setBolt("RetweetEmitter", new RetweetListBolt(),5).shuffleGrouping("ConvertJsonBolt");
		builder.setBolt("ESRetweetBolt", new EsBolt("retweets/retweets",ESConfig),4).addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2).shuffleGrouping("RetweetEmitter");

//		builder.setBolt("SampleBolt", new SampleBolt(), 4).shuffleGrouping("CustomFileReaderSpout");
//		TopologyBuilder builder = new TopologyBuilder();
//		builder.setSpout("spout", new SampleSpout(), 10);
//		builder.setBolt("es-bolt", new EsBolt("tindex/tdocs"), 5).addConfiguration(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 2).shuffleGrouping("spout");
		
		Config conf = new Config();	
		conf.setNumWorkers(1);	

		if(args.length == 0) {				
			try {
				StormSubmitter.submitTopology("myTopology", conf, builder.createTopology());
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		} else {
			// Give any random command line args for local 
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("myTopology", conf, builder.createTopology());
			Utils.sleep(1000000);
			cluster.killTopology("myTopology");
		}
	}

}
