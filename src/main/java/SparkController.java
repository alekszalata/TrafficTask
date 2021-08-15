import Database.TrafficDatabaseImpl;
import Kafka.SampleKafkaProducer;
//import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.*;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;

import java.util.Arrays;

public class SparkController implements Runnable {

    @Override
    public void run() {
        //Database and Kafka



        // Create a local StreamingContext with two working thread and batch interval of 1 second
        SparkConf conf = new SparkConf().setAppName("SparkApp").setMaster("local[*]");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(30));
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9010);

        //Data processing
        lines.print();
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(x.split(" ")).iterator());
        JavaDStream<String> newData = words.reduceByWindow((String x, String y) -> String.valueOf(Integer.parseInt(x) + Integer.parseInt(y)), Durations.seconds(30), Durations.seconds(30));

        newData.foreachRDD(sum -> {
            TrafficDatabaseImpl trafficDatabase = new TrafficDatabaseImpl();
            SampleKafkaProducer kafkaProducer = new SampleKafkaProducer();
            kafkaProducer.createProducer();
            sum.foreach(str -> {
//                long min = trafficDatabase.getMin();
//                long max = trafficDatabase.getMax();
                //System.out.println(String.format("MIN IS: %s", min));
                //System.out.println(String.format("MAX IS: %s", max));
                if (Integer.parseInt(str) < 0) {
                    //kafkaProducer.createMessage(new ProducerRecord("alerts", "alert", "minAlert"));
                    System.out.println("Send min alert");
                } else if (Integer.parseInt(str) > 0) {
                    //kafkaProducer.createMessage(new ProducerRecord("alerts", "alert", "maxAlert"));
                    System.out.println("Send max alert");
                }
            });
        });

        jssc.start();
        try {
            jssc.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jssc.close();
    }

}
