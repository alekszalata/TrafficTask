import org.apache.kafka.clients.producer.ProducerRecord;

public class Main {
    public static void main(String[] args) {
        //KafkaAdmin kafkaAdmin = new KafkaAdmin();
        SampleProducer kafkaProducer = new SampleProducer();

        //kafkaAdmin.createAdmin();
        kafkaProducer.createProducer();

        //kafkaAdmin.createTopic("alerts");
        kafkaProducer.createMessage(new ProducerRecord("alerts", "alert", "minOrMax"));

        //kafkaAdmin.closeAdmin();
        kafkaProducer.closeProducer();

    }
}
