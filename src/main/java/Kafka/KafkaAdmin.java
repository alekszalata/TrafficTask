package Kafka;

import Utils.PropertyReader;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaAdmin {

    private Admin admin;

    public KafkaAdmin() {

        Properties properties = new Properties();
        String kafkaIp = PropertyReader.getProperties().getProperty("kafkaIp");
        String kafkaPort = PropertyReader.getProperties().getProperty("kafkaPort");
        properties.put("bootstrap.servers", String.format("%s:%s", kafkaIp, kafkaPort));
        try {
            admin = Admin.create(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeAdmin() {
        admin.close();
    }

    public void createTopic(String topicName) {
        int partitions = 1;
        short replicationFactor = 1;
        NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

        CreateTopicsResult result = admin.createTopics(
                Collections.singleton(newTopic)
        );

        KafkaFuture<Void> future = result.values().get(topicName);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }



}
