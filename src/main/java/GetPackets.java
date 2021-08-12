import org.apache.kafka.clients.producer.ProducerRecord;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;
import java.io.IOException;


public class GetPackets {

    private static final String COUNT_KEY = GetPackets.class.getName() + ".count";
    private static final int COUNT = Integer.getInteger(COUNT_KEY, 5);

    private static final String READ_TIMEOUT_KEY = GetPackets.class.getName() + ".readTimeout";
    private static final int READ_TIMEOUT = Integer.getInteger(READ_TIMEOUT_KEY, 10); // [ms]

    private static final String SNAPLEN_KEY = GetPackets.class.getName() + ".snaplen";
    private static final int SNAPLEN = Integer.getInteger(SNAPLEN_KEY, 65536); // [bytes]

    private static final String BUFFER_SIZE_KEY = GetPackets.class.getName() + ".bufferSize";
    private static final int BUFFER_SIZE = Integer.getInteger(BUFFER_SIZE_KEY, 1 * 1024 * 1024); // [bytes]

    public static void main(String[] args) throws PcapNativeException, NotOpenException {

        String filter = args.length != 0 ? args[0] : "";

        System.out.println(COUNT_KEY + ": " + COUNT);
        System.out.println(READ_TIMEOUT_KEY + ": " + READ_TIMEOUT);
        System.out.println(SNAPLEN_KEY + ": " + SNAPLEN);
        System.out.println(BUFFER_SIZE_KEY + ": " + BUFFER_SIZE);
        System.out.println("\n");

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
        long byteSum = 0;
        while (true) {
            Packet packet = handle.getNextPacket();
            if (packet != null) {
                byteSum += packet.length();
                System.out.println(String.format("packet %s size: %s bytes", num + 1, packet.length()));
                num++;
                if (num >= COUNT) {
                    break;
                }
            }
        }

        PcapStat ps = handle.getStats();
        System.out.println("total sum: " + byteSum);
        System.out.println("ps_recv: " + ps.getNumPacketsReceived());

        handle.close();
    }

    public void kafkaCode() {
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
