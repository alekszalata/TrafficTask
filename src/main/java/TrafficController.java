import Database.TrafficDatabaseImpl;
import Kafka.SampleKafkaProducer;
import Utils.Scheduler;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;


public class TrafficController {
    private static final String COUNT_KEY = TrafficController.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, 5);

    private static final String READ_TIMEOUT_KEY = TrafficController.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    private static final String SNAPLEN_KEY = TrafficController.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]

    private static final String BUFFER_SIZE_KEY = TrafficController.class.getName() + ".bufferSize";
    private static final int BUFFER_SIZE = Integer.getInteger(BUFFER_SIZE_KEY, 1 * 1024 * 1024); // [bytes]

    public void startTrafficMonitoring() throws PcapNativeException, NotOpenException, IOException, InterruptedException {
        System.out.println(COUNT_KEY + ": " + COUNT);
        System.out.println(READ_TIMEOUT_KEY + ": " + READ_TIMEOUT);
        System.out.println(SNAPLEN_KEY + ": " + SNAPLEN);
        System.out.println(BUFFER_SIZE_KEY + ": " + BUFFER_SIZE);
        System.out.println("\n");

        String filter = "";
        //String filter = args.length != 0 ? args[0] : "";

        //Creating Spark
        SparkController sparkController = new SparkController();
        Thread t = new Thread(sparkController);
        t.start();

        int port = 9010;
        ServerSocket serverSocket = new ServerSocket(port);
        Socket clientSocket = serverSocket.accept();

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);


        PcapNetworkInterface nif;
        try {
            nif = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (nif == null) {
            return;
        }
        System.out.println(nif.getName() + " (" + nif.getDescription() + ")");

        PcapHandle.Builder phb = new PcapHandle.Builder(nif.getName())
                .snaplen(SNAPLEN)
                .promiscuousMode(PcapNetworkInterface.PromiscuousMode.PROMISCUOUS)
                .timeoutMillis(READ_TIMEOUT)
                .bufferSize(BUFFER_SIZE);
        PcapHandle handle = phb.build();
        handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

        int num = 0;
        while (true) {
            Packet packet = handle.getNextPacket();
            if (packet != null) {
                out.println(packet.length());
                out.flush();
                //packet.get(IpV4Packet.class).getHeader().getDstAddr();
                //byte[] message = packet.getRawData();
                //dOut.writeInt(message.length); // write length of the message
                //dOut.write(message);
                //System.out.println(String.format("packet %s size: %s bytes", num + 1, packet.length()));
                num++;
                if (num >= COUNT) {
                    break;
                }
            }
        }

//        PcapStat ps = handle.getStats();
//        System.out.println("ps_recv: " + ps.getNumPacketsReceived());
//        handle.close();
    }


}
