package Kafka;

import Utils.PropertyReader;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class SampleKafkaProducer {
    private KafkaProducer kafkaProducer;

    public void createProducer() {
        Properties properties = new Properties();
        String kafkaIp = PropertyReader.getProperties().getProperty("kafkaIp");
        String kafkaPort = PropertyReader.getProperties().getProperty("=kafkaPort");
        properties.put("bootstrap.servers", "localhost:9092");
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
}
