import Database.TrafficDatabaseImpl;
import Utils.Scheduler;
import org.pcap4j.core.NotOpenException;
import org.pcap4j.core.PcapNativeException;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        TrafficController getPackets = new TrafficController();
        getPackets.startTrafficMonitoring();
    }
}
