import Database.TrafficDatabaseImpl;
import Kafka.KafkaAdmin;
import Utils.PropertyReader;
import Utils.Scheduler;
import org.javatuples.Pair;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws PcapNativeException, NotOpenException, IOException, InterruptedException {
        Init();
        Scheduler scheduler = new Scheduler();
        TrafficDatabaseImpl trafficDatabase = new TrafficDatabaseImpl();
        Runnable computationPoints = () -> {
            ValueStorage.min = trafficDatabase.getMin();
            ValueStorage.max = trafficDatabase.getMax();
        };

        int valueUpdateSeconds = Integer.parseInt(PropertyReader.getProperties().getProperty("valuesUpdateSeconds"));
        scheduler.startRunnable(computationPoints, 0, valueUpdateSeconds, TimeUnit.SECONDS);


        TrafficController getPackets = ArgsHandler.ipArgs(args) ?
                new TrafficController(new Pair<>(args[0], args[1])) : new TrafficController();
        getPackets.startTrafficMonitoring();
    }

    public static void Init() {
        String topicName = PropertyReader.getProperties().getProperty("topicName");
        KafkaAdmin kafkaAdmin = new KafkaAdmin();
        kafkaAdmin.createTopic(topicName);
        kafkaAdmin.closeAdmin();
    }
}
