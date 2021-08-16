import com.google.common.net.InetAddresses;

public class ArgsHandler {

    public static boolean ipArgs(String[] args) {
        boolean rightArgs = false;
        if (args.length == 2) {
            if (args[0].equals("-s") && InetAddresses.isInetAddress(args[1])) {
                rightArgs = true;
                System.out.print("The source IP address should be" + args[1]);
            } else if (args[0] == "-d" && InetAddresses.isInetAddress(args[1])) {
                rightArgs = true;
                System.out.print("The destination IP address should be" + args[1]);
            }
        }
        return rightArgs;
    }
}
