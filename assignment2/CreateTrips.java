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
                otherInfo += "," + itr.nextToken();
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

    public static class DummyReducer extends Reducer<TextPair, Text, Text, Text> {

        public void reduce(TextPair key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            
            Boolean firstSegment = true;
            Boolean tripRecoding = false;
            String tripStartLongitude = "";
            String tripStartLatitude = "";
            String tripStopLongitude = "";
            String tripStopLatitude = "";

            for (Text value : values) {
                String line = value.toString();
                StringTokenizer itr = new StringTokenizer(line, ",'");

                String startTimestamp = itr.nextToken();
                String startLatitude = itr.nextToken();
                String startLongitude = itr.nextToken();
                String startState = itr.nextToken();
                String stopTimestamp = itr.nextToken();
                String stopLatitude = itr.nextToken();
                String stopLongitude = itr.nextToken();
                String stopState = itr.nextToken();

                //if (firstSegment) {
                //    tripRecoding = stopState.equals("M");
                //    firstSegment = false;
                //}
                
                if (tripRecoding) {
                    if (startState.equals(stopState)) {
                        // taxi keeps driving with a passenger...
                        continue;
                    } else if (startState == "E" && stopState == "M") {
                        // cannot happen
                        context.write(new Text("ERROR"), new Text("1"));
                    } else if (startState.equals("M") && stopState.equals("E")) {
                        // End the current trip, record the stop position and emit output key-value.
                        tripStopLatitude = stopLatitude;
                        tripStopLongitude = stopLongitude;
                        tripRecoding = false;
                        context.write(new Text("Trip by taxi " + key.getFirst()),
                        new Text(
                        "from " + tripStartLatitude + " " + tripStartLongitude +
                        " to " + tripStopLatitude + " " + tripStopLongitude)
                        );
                    } else {
                        // cannot happen
                    }
                } else {
                    if (startState.equals(stopState)) {
                        // taxi still empty, waiting for passenger...
                        continue;
                    } else if (startState.equals("E") && stopState.equals("M")) {
                        // Start a new trip. Record the start position.
                        tripStartLatitude = startLatitude;
                        tripStartLongitude = startLongitude;
                        tripRecoding = true;
                    } else if (startState.equals("M") && stopState.equals("E")) {
                        // cannot happen
                        context.write(new Text("ERROR in taxi " + key.getFirst() +
                        "start time " + startTimestamp), new Text("3"));
                    } else {
                        // cannot happen
                        context.write(new Text("ERROR"), new Text("4"));
                    }
                }
            }
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
