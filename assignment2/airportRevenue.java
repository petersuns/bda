import java.io.IOException;

import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
// import Haversine;

public class airportRevenue {

    public static class SegmentMapper extends Mapper<Object, Text, TextPair, Text> {

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            ////////////////////////////////////////////////////////////////////////////
            // String line = value.toString();
            // StringTokenizer itr = new StringTokenizer(line, ",‘");
            // String taxiNumber = itr.nextToken();
            // String startTimestamp = itr.nextToken();
            // context.write(new TextPair(taxiNumber, startTimestamp), new Text(line));
            ////////////////////////////////////////////////////////////////////////////



            // Date tripStartTime = new Date();
            // Double tripStartLongitude = 0.0;
            // Double tripStartLatitude = 0.0;
            // String tripStartState = "";

            // Double tripEndLongitude = 0.0;
            // Double tripEndLatitude = 0.0;
            // Date tripEndTime;
            // String tripEndState = "";

            // Double distance = 0.0;
            // Double distance_sim = 0.0;
            // Double distance_segments = 0.0;

            // Double distance_two = 0.0;
            // boolean airport_trip = false;
            // boolean airport_trip_start = false;
            // boolean airport_trip_between = false;

            Date date;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("America/Los Angeles"));

            // Integer segmentRecording = 1;
            // Date segmentStartTime, segmentEndTime;
            // segmentStartTime = new Date();
            // segmentEndTime = new Date();
            // String segmentStartState, segmentEndState;
            // segmentStartState = segmentEndState = "";
            // Double segmentStartLatitude, segmentStartLongitude, segmentEndLatitude, segmentEndLongitude;
            // segmentStartLatitude = segmentStartLongitude = segmentEndLatitude = segmentEndLongitude = 0.0;

            // // for (Text value2 : value) {
            String line = value.toString();
            // context.write(new TextPair(line, "1"), new Text(line));

            StringTokenizer itr = new StringTokenizer(line, ",'");

            // // String taxiNumber = itr.nextToken();
            // // String startTimestamp = itr.nextToken();
            // context.write(new TextPair(startTimestamp, "1"), new Text(line));




            Date startTimestamp, endTimestamp;
            String startState, endState, taxiNumber,startTimestamp_string;
            Double startLatitude, startLongitude, endLatitude, endLongitude,distance,distance_sim;
            // double time_diff, speed;
            try {
                taxiNumber = itr.nextToken(); // not used
                // startTimestamp = formatter.parse(itr.nextToken());
                startTimestamp_string = itr.nextToken();
                startTimestamp_string=startTimestamp_string.substring(0,11);
                // startTimestamp = formatter.parse(startTimestamp_string);
                startLatitude = Double.parseDouble(itr.nextToken());
                startLongitude = Double.parseDouble(itr.nextToken());
                startState = itr.nextToken();

                endTimestamp = formatter.parse(itr.nextToken());
                endLatitude = Double.parseDouble(itr.nextToken());
                endLongitude = Double.parseDouble(itr.nextToken());
                endState = itr.nextToken();
                distance = Double.parseDouble(itr.nextToken());
                distance_sim = Double.parseDouble(itr.nextToken());

            } catch (Exception e) {
                System.out.println("Error on input: " + e.toString());
                e.printStackTrace();
                return;
            }

            // context.write(new TextPair(startTimestamp_string, "1"), new Text("line"));

            // context.write(new TextPair(taxiNumber, startState), new Text(line+"2"));

            if (airport_square_1km(startLatitude, startLongitude) || airport_square_1km(endLatitude, endLongitude)){
                if (airport_circle_1km(startLatitude, startLongitude) || airport_circle_1km(endLatitude, endLongitude)){
                    double revenue = revenue(distance);
                    context.write(new TextPair(startTimestamp_string, ","), new Text(startTimestamp_string+","+revenue));
                }
            }

 

            // if (tripRecoding == false) {
            //     airport_trip = airport_trip_between = airport_trip_start = false;
            // }

            // if (firstSegment) {
            //     if (!(startState.equals("E") && endState.equals("M"))) {
            //         // context.write(new TextPair(taxiNumber, startState), new Text("1"));
            //         return;
            //     } else {
            //         firstSegment = false;
            //         tripStartTime = startTimestamp;
            //         tripStartLatitude = startLatitude;
            //         tripStartLongitude = startLongitude;
            //         tripStartState = startState;
            //         // distance += haversine(startLatitude, startLongitude, endLatitude,
            //         // endLongitude);
            //         // distance_sim += distanceSimplify(startLatitude, startLongitude, endLatitude, endLongitude);

            //         if (!airport_trip && airport_1km(startLatitude, startLongitude)) {
            //             airport_trip = true;
            //             airport_trip_start = true;
            //         }
            //         // context.write(new TextPair(taxiNumber, startState), new Text("2"));
            //         // return;
            //     }
            // }

            // tripStartTime = startTimestamp;
            // tripStartLatitude = startLatitude;
            // tripStartLongitude = startLongitude;
            // tripStartState = startState;
            // tripEndTime = endTimestamp;
            // tripEndLatitude = endLatitude;
            // tripEndLongitude = endLongitude;
            // tripEndState = endState;
            // context.write(new Text("taxi: " + key.getFirst()) ,new Text(line));

            // airport_trip = (airport_square_1km( tripStartLatitude, tripStartLongitude) &&
            // airport_square_1km(tripEndLatitude, tripEndLongitude));
            // boolean moving = (startState.equals("M"));
            // if(airport_trip && moving){
            // if (airport_circle_1km(tripStartLatitude, tripStartLongitude) &&
            // airport_circle_1km(tripEndLatitude, tripEndLongitude)){
            // // distance_two = haversine(tripStartLatitude, tripStartLongitude,
            // tripEndLatitude, tripEndLongitude);
            // context.write(new Text("taxi: " + key.getFirst()), new Text(tripStartTime
            // +" "));
            // }}

            // if (tripRecoding) {
            //     if (startState.equals("M") && endState.equals("M")) {
            //         // taxi keeps driving with or without a passenger...
            //         if (!airport_trip && airport_1km(startLatitude, startLongitude)) {
            //             airport_trip = true;
            //             airport_trip_between = true;
            //         }
            //         // distance += haversine(startLatitude, startLongitude, endLatitude,
            //         // endLongitude);
            //         context.write(new TextPair(taxiNumber, startState), new Text("2"));
            //         distance_sim += distanceSimplify(startLatitude, startLongitude, endLatitude, endLongitude);
            //         return;
            //     } else if (startState.equals("M") && endState.equals("E")) {
            //         // End the current trip, record the stop position and emit output key-value.
            //         tripEndTime = startTimestamp;
            //         tripEndLatitude = startLatitude;
            //         tripEndLongitude = startLongitude;
            //         tripEndState = startState;
            //         tripRecoding = false;

            //         if (!airport_trip && airport_1km(startLatitude, startLongitude)) {
            //             airport_trip = true;
            //             airport_trip_start = true;
            //         }
            //         if (airport_trip) {
            //             double revenue = revenue(distance_sim);
            //             // context.write(new TextPair(taxiNumber, startState), new Text(revenue+"revenue "));

                        
            //         }
            //         distance = distance_sim = 0.0;
            //         airport_trip = airport_trip_start = airport_trip_between = false;
            //         return;

            //     } else if (startState.equals("E") && endState.equals("M")) {
            //         // cannot happen
            //         tripRecoding = false;
            //         // context.write(new Text("ERROR 1"), new Text(line));
            //         return;
            //     } else {
            //         // cannot happen
            //         tripRecoding = false;
            //         // context.write(new Text("ERROR 2"), new Text(line));
            //         return;

            //     }
            // } else {
            //     if (startState.equals("E") && endState.equals("M")) {
            //         // Start a new trip. Record the start position.
            //         tripStartTime = startTimestamp;
            //         tripStartLatitude = startLatitude;
            //         tripStartLongitude = startLongitude;
            //         tripStartState = startState;
            //         tripRecoding = true;

            //         // distance += haversine(startLatitude, startLongitude, endLatitude,
            //         // endLongitude);
            //         distance_sim += distanceSimplify(startLatitude, startLongitude, endLatitude, endLongitude);
            //         if (!airport_trip && airport_1km(startLatitude, startLongitude)) {
            //             airport_trip = true;
            //             airport_trip_start = true;
            //         }
            //         return;
            //     } else if (startState.equals("M") && endState.equals("E")) {
            //         // cannot happen
            //         tripRecoding = false;
            //         // context.write(new Text("ERROR 3"), new Text(line));
            //         return;
            //     } else if (startState.equals("E") && endState.equals("E")) {
            //         // taxi still empty, waiting for passenger...
            //         return;
            //     } else {
            //         // cannot happen
            //         tripRecoding = false;
            //         // context.write(new Text("ERROR 4"), new Text(line));
            //         return;
            //     }
            // }
        }
        // }
    }

    // public static class NaturalKeyPartitioner extends Partitioner<TextPair, Text> {

    //     public int getPartition(TextPair key, Text value, int numPartitions) {
    //         return Math.abs(key.getFirst().hashCode() & Integer.MAX_VALUE) % numPartitions;
    //     }
    // }

    public static class GroupComparator extends WritableComparator {

        protected GroupComparator() {
            super(TextPair.class, true);
        }

        public int compare(WritableComparable wc1, WritableComparable wc2) {
            TextPair tp1 = (TextPair) wc1;
            TextPair tp2 = (TextPair) wc2;
            return tp1.getSecond().compareTo(tp2.getSecond());
        }
    }

    public static class DummyReducer extends Reducer<TextPair, Text, Text, Text> {

        public void reduce(TextPair key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

                // String line = values.toString();
                // context.write(new Text("000"), new Text("..."+line));

            double totalSum = 0.0, daySum = 0.0;
            String currentDate = "";
            for (Text value : values) {
                String line = value.toString();
                StringTokenizer itr = new StringTokenizer(line, ",");
                String tripStartDate = itr.nextToken();
                String revenue_string = itr.nextToken();

                if (tripStartDate.equals(currentDate)) {
                    daySum += Double.parseDouble(revenue_string);
                } else if (currentDate.equals("")) {
                    // For the first one, there is no previous day to finish.
                    // Just start the new day.
                    daySum = Double.parseDouble(revenue_string);
                    currentDate = tripStartDate;
                } else {
                    // Finish the previous day
                    totalSum += daySum;
                    context.write(new Text(currentDate), new Text("Day: " + Double.toString(daySum)));
                    // Start the new day
                    daySum = Double.parseDouble(revenue_string);
                    currentDate = tripStartDate;
                }
            }
            // Finish the last day as well.
            totalSum += daySum;
            context.write(new Text(currentDate), new Text("Day: " + Double.toString(daySum) + " Grand total: " + Double.toString(totalSum)));
        }
    }

    /* --- main methods ---------------------------------------------------- */

    public static Job runCreateTrips(Path input, Path output) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(airportRevenue.class);
        job.setMapperClass(SegmentMapper.class);
        // job.setPartitionerClass(NaturalKeyPartitioner.class);
        job.setGroupingComparatorClass(GroupComparator.class);
        job.setReducerClass(DummyReducer.class);
        job.setOutputKeyClass(TextPair.class);
        job.setOutputValueClass(Text.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setNumReduceTasks(1);

        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);
        FileInputFormat.setMaxInputSplitSize(job, 12800000);
        return job;
    }

    public static final double R = 6371; // In kilometers

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
        // return 1.0;

    }

    public static double distanceSimplify(double lat1, double lng1, double lat2, double lng2) {
        double dx = lng1 - lng2;//  经度差值
        double dy = lat1 - lat2;//  纬度差值
        double b = (lat1 + lat2) / 2.0;
        double Lx = Math.toRadians(dx) * 6371.0 * Math.cos(Math.toRadians(b));//  东西距离
        double Ly = 6371.0 * Math.toRadians(dy);//  南北距离
        return Math.sqrt(Lx * Lx + Ly * Ly);//  用平面的矩形对角距离公式计算总距离
    }

    public static double distanceSimplify3(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        double R = 6371.0;
        double x = (lon2 - lon1) * Math.cos(0.5 * (lat2 + lat1));
        double y = lat2 - lat1;
        return R * Math.sqrt(x * x + y * y);
    }

    public static final double airport_lat = 37.62131;
    public static final double airport_lon = -122.37896;
    public static final double diff_lat = 0.00909;
    public static final double diff_lon = 0.01136;
    public static final double airport_lat_plus_1km = airport_lat + diff_lat;
    public static final double airport_lat_minus_1km = airport_lat - diff_lat;
    public static final double airport_lon_plus_1km = airport_lon + diff_lon;
    public static final double airport_lon_minus_1km = airport_lon - diff_lon;

    public static boolean airport_square_1km(double lat1, double lon1) {
        return (lat1 < airport_lat_plus_1km && lat1 > airport_lat_minus_1km && lon1 < airport_lon_plus_1km
                && lon1 > airport_lon_minus_1km);
    }

    public static boolean airport_circle_1km(double lat1, double lon1) {
        return haversine(lat1, lon1, airport_lat, airport_lon) <= 1.0;
    }

    public static boolean airport_1km(double lat1, double lon1) {
        return airport_square_1km(lat1, lon1) && haversine(lat1, lon1, airport_lat, airport_lon) <= 1.0;
    }

    public static double revenue(double distance) {
        return 3.5 + 1.71 * distance;
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
