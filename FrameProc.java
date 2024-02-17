import java.io.*;
import java.util.HashMap;

/*Ayat Kamona - B00858784
This code simulates network bridge operations. It reads data from RandomFrames.txt and BridgeFDB.txt updating the FDB based on source mac address
and their arrival ports, it then decides whether to forward, discard or broadcast frames and writes the output into BridgeOutput.txt
*/
public class FrameProc {
    public static void main(String[] args) {
        try {
            BufferedReader fdbReader = new BufferedReader(new FileReader("BridgeFDB.txt"));
            BufferedReader randomFrameReader = new BufferedReader(new FileReader("RandomFrames.txt"));
            BufferedWriter bridgeOutputWriter = new BufferedWriter(new FileWriter("BridgeOutput.txt"));

            // Initialize forwarding database from BridgeFDB.txt
            HashMap<String, Integer> forwardingDB = new HashMap<>();
            String line;
            while ((line = fdbReader.readLine()) != null) {
                String macAddress = line;
                int port = Integer.parseInt(fdbReader.readLine());
                forwardingDB.put(macAddress, port);
            }

            // Process random frames
            while ((line = randomFrameReader.readLine()) != null) {
                String sourceMAC = line;
                String destMAC = randomFrameReader.readLine();
                int arrivalPort = Integer.parseInt(randomFrameReader.readLine());

                // Update the port of the source MAC address in the FDB based on the incoming
                // request
                if (forwardingDB.containsKey(sourceMAC)) {
                    int currentPort = forwardingDB.get(sourceMAC);
                    if (currentPort != arrivalPort) {
                        // Host moved to a new port; update the FDB
                        forwardingDB.put(sourceMAC, arrivalPort);

                        // Update the "BridgeFDB.txt" file as well
                        updateFDB("BridgeFDB.txt", sourceMAC, arrivalPort);
                    }
                } else {
                    // If the source MAC is not in the FDB, add it and update "BridgeFDB.txt"
                    forwardingDB.put(sourceMAC, arrivalPort);
                    updateFDB("BridgeFDB.txt", sourceMAC, arrivalPort);
                }

                if (forwardingDB.containsKey(destMAC)) {
                    int destPort = forwardingDB.get(destMAC);
                    if (arrivalPort != destPort) {
                        // Frame should be forwarded to the port of the destination MAC
                        bridgeOutputWriter.write(sourceMAC + "\t" + destMAC + "\t" + arrivalPort
                                + "\tForwarded on port " + destPort);
                    } else {
                        // Frame should be discarded
                        bridgeOutputWriter.write(sourceMAC + "\t" + destMAC + "\t" + arrivalPort + "\tDiscarded");
                    }
                } else {
                    // Destination MAC not found; broadcast
                    bridgeOutputWriter.write(sourceMAC + "\t" + destMAC + "\t" + arrivalPort + "\tBroadcast");
                }
                bridgeOutputWriter.newLine();
            }

            fdbReader.close();
            randomFrameReader.close();
            bridgeOutputWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateFDB(String fileName, String sourceMAC, int arrivalPort) {
        try {
            BufferedReader fdbReader = new BufferedReader(new FileReader(fileName));
            StringBuilder updatedContent = new StringBuilder();
            String line;
            while ((line = fdbReader.readLine()) != null) {
                String macAddress = line;
                int port = Integer.parseInt(fdbReader.readLine());
                if (macAddress.equals(sourceMAC)) {
                    updatedContent.append(sourceMAC).append("\n").append(arrivalPort).append("\n");
                } else {
                    updatedContent.append(macAddress).append("\n").append(port).append("\n");
                }
            }
            fdbReader.close();

            BufferedWriter fdbWriter = new BufferedWriter(new FileWriter(fileName));
            fdbWriter.write(updatedContent.toString());
            fdbWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
