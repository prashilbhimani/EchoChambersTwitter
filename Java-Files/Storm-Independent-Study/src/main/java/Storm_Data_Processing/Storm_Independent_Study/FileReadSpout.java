package Storm_Data_Processing.Storm_Independent_Study;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileReadSpout extends BaseRichSpout {
    public static final String FIELDS = "sentence";
    private static final long serialVersionUID = -2582705611472467172L;
    private transient FileReader reader;
    private ArrayList<String> files;
    private boolean ackEnabled = true;
    private SpoutOutputCollector collector;

    private long count = 0;


    public FileReadSpout(ArrayList<String> files) {
        this.files = files;
    }
    public FileReadSpout(String file) {
        ArrayList<String> files=new ArrayList<String>();
        files.add(file);
        this.files = files;
    }

    
    // For testing
    FileReadSpout(FileReader reader) {
        this.reader = reader;
    }
    
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        Object ackObj = conf.get("topology.acker.executors");
        if (ackObj != null && ackObj.equals(0)) {
            this.ackEnabled = false;
        }
        // for tests, reader will not be null
        if (this.reader == null) {
            this.reader = new FileReader(this.files);
        }
    }
    
    public void nextTuple() {
    	String k=reader.nextLine();
    	if(k.equals("")) {
    		return;
    	}
    	if (ackEnabled) {
            collector.emit(new Values(k), count);
            count++;
        } else {
        	collector.emit(new Values(k));
        }
    }

    
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(FIELDS));
    }

    public static List<String> readLines(InputStream input) {
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                throw new RuntimeException("Reading file failed", e);
            } finally {
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error closing reader", e);
        }
        return lines;
    }


    public static class FileReader implements Serializable {

        private static final long serialVersionUID = -7012334600647556267L;

        public final ArrayList<String> files;
        private List<String> contents = new ArrayList<String>();
        private int index = 0;
        private int limit = 0;
        private int curr_index=0;

        public FileReader(ArrayList<String> files) {
            this.files=files;
            this.limit = contents.size();
        }


        public String nextLine() {
            if (index >= limit) {
                if (curr_index<files.size()) {
                    try {
                        System.out.println(curr_index);
                        this.contents = readLines(new FileInputStream(files.get(curr_index)));
                        this.index=0;
                        this.limit=contents.size();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new IllegalArgumentException("Cannot open file " + files.get(curr_index), e);
                    }
                    curr_index++;
                } else {
                    return "";
                }
            }
            String line = contents.get(index);
            index++;
            if(line== null)
                return "";
            return line;
        }


    }
}
