import Utils.PropertyReader;
import org.javatuples.Pair;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.io.*;
import java.net.*;


public class TrafficController {
    private static final String COUNT_KEY = TrafficController.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, 5);

    private static final String READ_TIMEOUT_KEY = TrafficController.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    private static final String SNAPLEN_KEY = TrafficController.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]

    private static final String BUFFER_SIZE_KEY = TrafficController.class.getName() + ".bufferSize";
    private static final int BUFFER_SIZE = Integer.getInteger(BUFFER_SIZE_KEY, 1 * 1024 * 1024); // [bytes]

    private IpSearch ipSearch = IpSearch.NOTHING;
    private Inet4Address ipAddress;

    public TrafficController(Pair<String, String> argKeyValue) throws UnknownHostException {
        if (argKeyValue.getValue0().equals("-s")) {
            ipSearch = IpSearch.SOURCE;
            ipAddress = (Inet4Address) InetAddress.getByName(argKeyValue.getValue1());
        } else if (argKeyValue.getValue0().equals("-d")) {
            ipSearch = IpSearch.DESTINATION;
            ipAddress = (Inet4Address) InetAddress.getByName(argKeyValue.getValue1());
        }
    }

    public TrafficController() {

    }

    public void startTrafficMonitoring() throws PcapNativeException, NotOpenException, IOException, InterruptedException {
        System.out.println(COUNT_KEY + ": " + COUNT);
        System.out.println(READ_TIMEOUT_KEY + ": " + READ_TIMEOUT);
        System.out.println(SNAPLEN_KEY + ": " + SNAPLEN);
        System.out.println(BUFFER_SIZE_KEY + ": " + BUFFER_SIZE);
        System.out.println("\n");


        //Creating Spark
        SparkController sparkController = new SparkController();
        Thread t = new Thread(sparkController);
        t.start();

        int socketPort = Integer.parseInt(PropertyReader.getProperties().getProperty("socketPort"));
        ServerSocket serverSocket = new ServerSocket(socketPort);
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

        int num = 0;
        while (true) {
            Packet packet = handle.getNextPacket();
            if (packet != null) {
                boolean allowPacket = false;
                switch (ipSearch) {
                    case NOTHING -> allowPacket = true;
                    case SOURCE -> {
                        if (ipAddress == packet.get(IpV4Packet.class).getHeader().getSrcAddr()) {
                            allowPacket = true;
                        }
                    }
                    case DESTINATION -> {
                        if (ipAddress == packet.get(IpV4Packet.class).getHeader().getDstAddr()) {
                            allowPacket = true;
                        }
                    }
                }
                if (allowPacket) {
                    out.println(packet.length());
                    out.flush();
                    num++;
                    if (num >= COUNT) {
                        //break;
                    }
                }

            }
        }
    }
}

enum IpSearch {
    NOTHING,
    SOURCE,
    DESTINATION
}
