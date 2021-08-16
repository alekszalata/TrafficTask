package Kafka;

import Utils.PropertyReader;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;
import java.io.Serializable;


public class SampleKafkaProducer implements Serializable {
    private KafkaProducer kafkaProducer;

    public SampleKafkaProducer() {
        Properties properties = new Properties();
        String kafkaIp = PropertyReader.getProperties().getProperty("kafkaIp");
        String kafkaPort = PropertyReader.getProperties().getProperty("kafkaPort");
        properties.put("bootstrap.servers", String.format("%s:%s", kafkaIp, kafkaPort));
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducer = new KafkaProducer(properties);
    }

    public void createMessage(ProducerRecord producerRecord) {
        kafkaProducer.send(producerRecord);
    }

    public void closeProducer() {
        kafkaProducer.close();
    }

    public static void staticSendMessage(ProducerRecord producerRecord) {
        Properties properties = new Properties();
        String kafkaIp = PropertyReader.getProperties().getProperty("kafkaIp");
        String kafkaPort = PropertyReader.getProperties().getProperty("kafkaPort");
        properties.put("bootstrap.servers", String.format("%s:%s", kafkaIp, kafkaPort));
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer staticKafkaProducer = new KafkaProducer(properties);
        staticKafkaProducer.send(producerRecord);
        staticKafkaProducer.close();
    }

}
