import java.io.IOException;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Partitioner;

public class CreateTrips {

    public static class SegmentMapper extends Mapper<Object, Text, TextPair, Text> {
        
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            StringTokenizer itr = new StringTokenizer(line, ",'");

            String taxiNumber = itr.nextToken();
            String startTimestamp = itr.nextToken();
            String otherInfo = "";

            otherInfo += startTimestamp;
            for (int i = 0; i < 7; i++) {
                otherInfo += " " + itr.nextToken();
            }

            context.write(new TextPair(taxiNumber, startTimestamp), new Text(otherInfo));
        }
    }

    public static class NaturalKeyPartitioner extends Partitioner<TextPair, Text> {
        
        public int getPartition(TextPair key, Text value, int numPartitions) {
            return Math.abs(key.getFirst().hashCode() & Integer.MAX_VALUE) % numPartitions;
        }
    }

    public static class GroupComparator extends WritableComparator {
        
        protected GroupComparator() {
            super(TextPair.class, true);
        }

        public int compare(WritableComparable wc1, WritableComparable wc2) {
            TextPair tp1 = (TextPair) wc1;
            TextPair tp2 = (TextPair) wc2;
            return tp1.getFirst().compareTo(tp2.getFirst());
        }
    }

    public static class DummyReducer extends Reducer<TextPair, Text, TextPair, IntWritable> {
        
        private IntWritable result = new IntWritable();

        public void reduce(TextPair key, Iterable<IntWritable> values, Context context)
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
        job.setPartitionerClass(NaturalKeyPartitioner.class);
        job.setGroupingComparatorClass(GroupComparator.class);
        job.setReducerClass(DummyReducer.class);
        job.setOutputKeyClass(TextPair.class);
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
