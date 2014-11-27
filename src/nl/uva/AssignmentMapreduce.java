package nl.uva;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * @author S. Koulouzis
 */
public class AssignmentMapreduce extends Configured implements Tool {

    private static String dataset;
    private static String outputFolder;
    private static Integer maxMap = -1;
    Log log = LogFactory.getLog(AssignmentMapreduce.class);

    public static void main(String[] args) {
        try {
            if (args == null || args.length < 3 || args[0].equals("-help") || args[0].equals("help")) {
                printHelp();
                System.exit(-1);
            }

            dataset = args[0];
            outputFolder = args[1];
            if (args.length >= 3) {
                maxMap = Integer.valueOf(args[2]);
            }


            //Start the execution
            String[] myArgs = new String[]{};
            int res = ToolRunner.run(new Configuration(), new AssignmentMapreduce(), myArgs);

            System.exit(res);
        } catch (Exception ex) {
            Logger.getLogger(AssignmentMapreduce.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void printHelp() {
        System.out.println("Usage: <input dataset> <outpout folder> <max num. of mapred>\n");
        System.out.println("input dataset:\t\t\tThe path for the twitter dataset");
        System.out.println("outpout folder:\t\t\tThe location where the results will "
                + "be saved.");
        System.out.println("max num. of mapred:\t\tThe maximum number of map and "
                + "reduce tasks that will be run simultaneously by a task tracker.");
    }

    @Override
    /**
     * This method contains the execution logic:
     */
    public int run(String[] args) throws Exception {
        JobConf jobConf = configureJob();
        JobClient.runJob(jobConf);
        return 0;
    }

    /**
     * This method configures the job
     *
     * @param args
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private JobConf configureJob() throws IOException, URISyntaxException {
        JobConf conf = new JobConf(getConf(), AssignmentMapreduce.class);

//conf.addResource(new Path("/cm/shared/package/hadoop/hadoop-2.5.0/etc/hadoop/core-site.xml"));
//conf.addResource(new Path("/cm/shared/package/hadoop/hadoop-2.5.0/etc/hadoop/hdfs-site.xml"));
//conf.addResource(new Path("/cm/shared/package/hadoop/hadoop-2.5.0/etc/hadoop/httpfs-site.xml"));
//conf.addResource(new Path("/cm/shared/package/hadoop/hadoop-2.5.0/etc/hadoop/ mapred-site.xml"));
//conf.addResource(new Path("/cm/shared/package/hadoop/hadoop-2.5.0/etc/hadoop/yarn-site.xml"));

        conf.setJobName("WC");

        //Set the Maper and formats
        conf.setMapperClass(nl.uva.Map.class);
        conf.setMapOutputKeyClass(Text.class);
        conf.setMapOutputValueClass(IntWritable.class);
        //Set Input Format Format to apply to the input 
        //files for the mappers 
        conf.setInputFormat(TextInputFormat.class);


        //Set the Reducer and formats
        conf.setReducerClass(nl.uva.Reduce.class);
        //The data types emitted by the reducer are identified by 
        //setOutputKeyClass() and setOutputValueClass()
        //It is the class for the value and output key.
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);
        //Set output Format Format to apply to the output
        //files for the reducer 
        conf.setOutputFormat(TextOutputFormat.class);

        //Set the input path for the job
        Path localPath = new Path(dataset);
        FileInputFormat.setInputPaths(conf, localPath);
        FileOutputFormat.setOutputPath(conf, new Path(outputFolder));

        if (maxMap > -1) {
            conf.setNumReduceTasks(maxMap);
            conf.setNumMapTasks(maxMap);
        }

        return conf;
    }
}