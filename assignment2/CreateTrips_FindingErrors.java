import java.io.IOException;

import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
// import org.joda.time.format.DateTimeFormat;



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

public class CreateTrips_FindingErrors{

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
            Boolean tripRecoding = true;

            String tripStartTime = "";
            Double tripStartLongitude = 0.0;
            Double tripStartLatitude = 0.0;
            String tripStartState = "";

            Double tripEndLongitude = 0.0;
            Double tripEndLatitude = 0.0;
            String tripEndTime = "";
            String tripEndState = "";

            Double distance = 0.0;
            Double distance_sim = 0.0;

            Double distance_two = 0.0;
            boolean airport_trip = false;




            Integer segmentRecording = 1;
            String segmentStartState,segmentEndState,segmentStartTime,segmentEndTime;
            segmentStartState=segmentEndState=segmentStartTime=segmentEndTime="";
            Double segmentStartLatitude,segmentStartLongitude,segmentEndLatitude,segmentEndLongitude;
            segmentStartLatitude=segmentStartLongitude=segmentEndLatitude=segmentEndLongitude=0.0;




            for (Text value : values) {
                String line = value.toString();
                StringTokenizer itr = new StringTokenizer(line, ",'");
                String startTimestamp, startState, endTimestamp, endState;
                Double startLatitude, startLongitude, endLatitude, endLongitude;
                try {
                    startTimestamp = itr.nextToken();
                    // String startLatitude = itr.nextToken();
                    startLatitude = Double.parseDouble(itr.nextToken());
                    // String startLongitude = itr.nextToken();
                    startLongitude = Double.parseDouble(itr.nextToken());
                    startState = itr.nextToken();
                    endTimestamp = itr.nextToken();
                    // String endLatitude = itr.nextToken();
                    endLatitude = Double.parseDouble(itr.nextToken());
                    // String endLongitude = itr.nextToken();
                    endLongitude = Double.parseDouble(itr.nextToken());
                    endState = itr.nextToken();
                } catch (Exception e) {
                    System.out.println("Error on input: " + e.toString());
                    e.printStackTrace();
                    continue;
                }
                
                // String secondState = endState;
                // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd", Locale.ENGLISH);
                // Date date =  formatter.parse(startTimestamp);
                // DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
                // Date date = formatter.parse(startTimestamp);

                // String string = "January 2, 2010";
                // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                // LocalDateTime date = LocalDateTime.parse(string, formatter);
                // System.out.println(date); // 2010-01-02

                // String str_date = "11-June-07";
                DateFormat formatter;
                Date date;
                formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                formatter.setTimeZone(TimeZone.getTimeZone("America/Los Angeles"));
                try {
                    date = formatter.parse(startTimestamp);
                } catch (Exception e) {
                    System.out.println("Error on date: " + e.toString());
                    e.printStackTrace();
                    continue;
                }
                
                if (segmentRecording==1) {
                    segmentStartTime = startTimestamp;
                    segmentStartLatitude = startLatitude;
                    segmentStartLongitude = startLongitude;
                    segmentStartState = startState;
                    segmentRecording = 2;
                    continue;
                }else if (segmentRecording==2){
                    segmentEndTime = startTimestamp;
                    segmentEndLatitude = startLatitude;
                    segmentEndLongitude = startLongitude;
                    segmentEndState = startState;
                }else{
                    context.write(new Text("ERROR E"), new Text(line));
                }

                startTimestamp = segmentStartTime;
                startLatitude = segmentStartLatitude;
                startLongitude = segmentStartLongitude;
                startState=segmentStartState;
                endState=segmentEndState;
                segmentRecording=2;

                segmentStartTime = segmentEndTime;
                segmentStartLatitude = segmentEndLatitude;
                segmentStartLongitude = segmentEndLongitude;
                segmentStartState = segmentEndState;


                
                if (firstSegment) {
                    if (!(startState.equals("E") && endState.equals("M"))) {
                        continue;
                    } else {
                         firstSegment = false;
                         tripStartTime = startTimestamp;
                         tripStartLatitude = startLatitude;
                         tripStartLongitude = startLongitude;
                         tripStartState = startState;
 
                        //  distance += haversine(startLatitude, startLongitude, endLatitude, endLongitude);
                         distance_sim += distanceSimplify(startLatitude, startLongitude, endLatitude, endLongitude);
                         continue;
                    }
                 }

                if (tripRecoding) {
                    if (startState.equals("M") && endState.equals("M")) {
                        // taxi keeps driving with or without a passenger...
                        // distance += haversine(startLatitude, startLongitude, endLatitude, endLongitude);
                        distance_sim += distanceSimplify(startLatitude, startLongitude, endLatitude, endLongitude);
                        continue;
                    } else if (startState.equals("E") && endState.equals("M")) {
                        // cannot happen
                        // tripRecoding = false;
                        context.write(new Text("ERROR 1"), new Text(line));

                    } else if (startState.equals("M") && endState.equals("E")) {
                        // End the current trip, record the stop position and emit output key-value.
                        tripEndTime = startTimestamp;
                        tripEndLatitude = startLatitude;
                        tripEndLongitude = startLongitude;
                        tripEndState = startState;
                        tripRecoding = false;
                        airport_trip = (airport_square_1km( tripStartLatitude, tripStartLongitude) || airport_square_1km(tripEndLatitude, tripEndLongitude));

                        if(airport_trip){
                            if (airport_circle_1km(tripStartLatitude, tripStartLongitude) || airport_circle_1km(tripEndLatitude, tripEndLongitude)){
                                //distance_two = haversine(tripStartLatitude, tripStartLongitude, tripEndLatitude, tripEndLongitude);
                                context.write(new Text("taxi: " + key.getFirst()),
                                new Text(
                                    tripStartTime + " "+ tripStartLatitude + " " + tripStartLongitude + " " + tripStartState + " "+
                                    tripEndTime + " " + tripEndLatitude + " " + tripEndLongitude + " " + tripEndState+" "+
                                    /* distance_two+" " + */ distance+" "+distance_sim+" "+airport_trip+" "+date)
                                );
                            }
                        }
                        // distance=0.0;
                        distance_sim=0.0;
                        continue;
 
                    } else {
                        // cannot happen
                        context.write(new Text("ERROR 2"), new Text(line));

                    }
                } else {
                    if (startState.equals("E") && endState.equals("E")) {
                        // taxi still empty, waiting for passenger...
                        continue;
                    } else if (startState.equals("E") && endState.equals("M")) {
                        // Start a new trip. Record the start position.
                        tripStartTime = startTimestamp;
                        tripStartLatitude = startLatitude;
                        tripStartLongitude = startLongitude;
                        tripStartState = startState;
                        tripRecoding = true;

                        // distance += haversine(startLatitude, startLongitude, endLatitude, endLongitude);
                        distance_sim += distanceSimplify(startLatitude, startLongitude, endLatitude, endLongitude);
                        continue;
                    } else if (startState.equals("M") && endState.equals("E")) {
                        // cannot happen

                        context.write(new Text("ERROR 3"), new Text(line));
                    } else {
                        // cannot happen
                        context.write(new Text("ERROR 4"), new Text(line));
                    }
                }
            }
        }
    }

    /* --- main methods ---------------------------------------------------- */

    public static Job runCreateTrips(Path input, Path output) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(CreateTrips_FindingErrors.class);
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

    public static final double R = 6371; // In kilometers
    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
    public static double distanceSimplify(double lat1, double lng1, double lat2, double lng2) {
        double dx =lng1-lng2;// 经度差值
        double dy=lat1-lat2;// 纬度差值
        double b=(lat1+lat2)/2.0;
        double Lx=Math.toRadians(dx)*6371.0*Math.cos(Math.toRadians(b));// 东西距离
        double Ly=6371.0*Math.toRadians(dy);// 南北距离
        return Math.sqrt(Lx *Lx+Ly*Ly);// 用平面的矩形对角距离公式计算总距离
    }
    public static double distanceSimplify3(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);
        double R = 6371.0;
        double x = (lon2 - lon1) * Math.cos( 0.5*(lat2+lat1));
        double y = lat2 - lat1;
        return R * Math.sqrt( x*x + y*y );
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
        return (lat1 < airport_lat_plus_1km && lat1 > airport_lat_minus_1km &&
                lon1 < airport_lon_plus_1km && lon1 > airport_lon_minus_1km);
    }

    public static boolean airport_circle_1km(double lat1, double lon1) {
        return haversine(lat1,lon1,airport_lat,airport_lon) <=1.0; 
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