import java.io.IOException;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class CreateTrips {

    public static class SegmentMapper extends Mapper<Object, Text, Text, Text> {

        //private final static IntWritable one = new IntWritable(1);
        // String[] elements = line.split(",");
        //private Text segment = new Text();
        private Text txtTaxiNumber = new Text();
        private Text txtOtherInfo = new Text();

        // private Pattern punctuationPattern = Pattern.compile(",");
        // private Pattern wordPattern = Pattern.compile("[a-z]+");

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer itr = new StringTokenizer(line,",");
            
            String taxiNumber = itr.nextToken();
            String otherInfo = "";

            for (int i = 0; i < 8; i++) {
                otherInfo += itr.nextToken();
            }

            txtTaxiNumber.set(taxiNumber);
            txtOtherInfo.set(otherInfo);

            //while (itr.hasMoreTokens()) {
                //String token = itr.nextToken(); // .toLowerCase(); // convert words to lowercase
                // token = punctuationPattern.matcher(token).replaceAll(" "); // remove punctuation
                //segment.set(token);
                //context.write(txtTaxiNumber, txtOtherInfo);
                //IntWritable one = new IntWritable(1);
                context.write(txtTaxiNumber, txtOtherInfo);
                // if (wordPattern.matcher(token).matches()) { // remove non-latin-words
                // segment.set(token);
                // context.write(segment, one);
                // }
            // }

        //     for (String word : line.split(",")){ 
        //         if (word.length() > 0)
        //         { 
        //             output.collect(new Text(word), new IntWritable(1)); 
        //         } 
        // } 
    } 
} 
        
    

    public static class DummyReducer extends Reducer<Text, Text, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
            sum += val.get();
            }

            context.write(key, new IntWritable(sum));
        }
    }

    /* --- main methods ---------------------------------------------------- */

    public static Job runCreateTrips(Path input, Path output) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(CreateTrips.class);
        job.setMapperClass(SegmentMapper.class);
        job.setReducerClass(DummyReducer.class);
        job.setOutputKeyClass(Text.class);
        //job.setOutputValueClass(IntWritable.class);
        job.setOutputValueClass(Text.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);
        return job;
    }

    public static void main(String[] args) throws Exception {
        Path input = new Path(args[0]);
        Path output1 = new Path(args[1], "pass1");

        Job createTripsJob = runCreateTrips(input, output1);
        if (!createTripsJob.waitForCompletion(true)) {
            System.exit(1);
        }
    }
}
