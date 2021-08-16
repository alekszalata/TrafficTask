import Kafka.SampleKafkaProducer;
import Utils.PropertyReader;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;

import java.util.Arrays;

public class SparkController implements Runnable {

    @Override
    public void run() {
        String topicName = PropertyReader.getProperties().getProperty("topicName");
        // Create a local StreamingContext with two working thread and batch interval of 1 second
        SparkConf conf = new SparkConf().setAppName("SparkApp").setMaster("local[*]");

        int jsscSeconds = Integer.parseInt(PropertyReader.getProperties().getProperty("jsscSeconds"));
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(jsscSeconds));
        int socketPort = Integer.parseInt(PropertyReader.getProperties().getProperty("socketPort"));
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", socketPort);

        //Kafka broadcast try
        //SampleKafkaProducer sampleKafkaProducer = new SampleKafkaProducer();
        //<SampleKafkaProducer> broadcastKafkaProducer = jssc.sparkContext().broadcast(sampleKafkaProducer);


        //Data processing
        int windowsDurationSeconds = Integer.parseInt(PropertyReader.getProperties().getProperty("windowsDurationSeconds"));
        JavaDStream<String> words = lines.flatMap(x -> Arrays.asList(x.split(" ")).iterator());
        JavaDStream<String> newData = words.reduceByWindow((String x, String y) ->
                String.valueOf(Integer.parseInt(x) + Integer.parseInt(y)),
                Durations.seconds(windowsDurationSeconds),
                Durations.seconds(windowsDurationSeconds));

        newData.foreachRDD(sum -> {
            //Kafka broadcast try
            //SampleKafkaProducer kafkaProducer = broadcastKafkaProducer.getValue();
            //System.out.println(String.format("Kafka producer was created"));
            sum.foreach(dataSum -> {
                System.out.println(String.format("MIN IS: %s", ValueStorage.min));
                System.out.println(String.format("MAX IS: %s", ValueStorage.max));
                System.out.println(String.format("DATA SUM: %s", dataSum));
                if (Integer.parseInt(dataSum) < ValueStorage.min) {  // < min
                    SampleKafkaProducer.staticSendMessage(new ProducerRecord(topicName, "alert", "minAlert"));
                    System.out.println("Send min alert");
                } else if (Integer.parseInt(dataSum) >  ValueStorage.max) {  // > max
                    SampleKafkaProducer.staticSendMessage(new ProducerRecord(topicName, "alert", "maxAlert"));
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
