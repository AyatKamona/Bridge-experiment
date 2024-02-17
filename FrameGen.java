import java.io.*;
import java.util.Random;
import java.util.ArrayList;

/*Ayat Kamona - B00858784
This code generates 100 random frames, each having a source and destination mac address and both are randomly selected from BridgeFDB.txt, along 
with destination port(1-4) I use an ArrayList to store the mac addresses and a simple array to store ports 1-4 and use the Random operator to 
choose 2 mac addresses and a port number for each frame*/
public class FrameGen {
    public static void main(String[] args) {
        try {
            BufferedReader fdbReader = new BufferedReader(new FileReader("BridgeFDB.txt"));
            BufferedWriter randomFrameWriter = new BufferedWriter(new FileWriter("RandomFrames.txt"));

            String line;
            ArrayList<String> macAddresses = new ArrayList<>(); // Use ArrayList to store MAC addresses
            int numPorts = 4; // The number of ports is fixed at 4

            // Read MAC addresses from BridgeFDB.txt
            while ((line = fdbReader.readLine()) != null) {
                macAddresses.add(line);
                fdbReader.readLine(); // Consume the port line, but don't store it
            }

            if (macAddresses.size() < numPorts) {
                System.out.println("Not enough entries in BridgeFDB.txt");
                return;
            }

            // Generate 100 random frames
            Random rand = new Random();
            for (int i = 0; i < 100; i++) {
                int sourceIndex = rand.nextInt(macAddresses.size());
                int destIndex;
                do {
                    destIndex = rand.nextInt(macAddresses.size());
                } while (destIndex == sourceIndex);

                String sourceMAC = macAddresses.get(sourceIndex);
                String destMAC = macAddresses.get(destIndex);
                int arrivalPort = rand.nextInt(numPorts) + 1;

                randomFrameWriter.write(sourceMAC);
                randomFrameWriter.newLine();
                randomFrameWriter.write(destMAC);
                randomFrameWriter.newLine();
                randomFrameWriter.write(Integer.toString(arrivalPort));
                randomFrameWriter.newLine();
            }

            fdbReader.close();
            randomFrameWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
