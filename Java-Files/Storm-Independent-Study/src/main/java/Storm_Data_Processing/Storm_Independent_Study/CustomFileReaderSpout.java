package Storm_Data_Processing.Storm_Independent_Study;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;

import java.util.ArrayList;


public class CustomFileReaderSpout extends FileReadSpout {

	public CustomFileReaderSpout(String file) {
		super(file);
	}
	public CustomFileReaderSpout(ArrayList<String> files){
		super(files);
	}
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

		// emit the tuple with field "site"
		declarer.declare(new Fields("tweets"));
	}
}

