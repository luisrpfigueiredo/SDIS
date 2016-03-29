import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * Created by Luís on 25/03/2016.
 */
public class PutchunkProtocol extends Thread {
    private Peer peer;
    private String fileName;
    private int repDegree;

    public PutchunkProtocol(Peer peer, String fileName, int repDegree){
        this.peer = peer;
        this.fileName = fileName;
        this.repDegree = repDegree;
    }

    public void run(){
        //Get File to be saved. Ensures existance
        File f = getLocalFile(fileName);

        //Divide file into chunks and save them individually
        byte[] chunk = new byte[Constants.chunkSize];
        String hashedFileName = Constants.sha256(fileName);

        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            int readBytes;
            int chunkNumber = 0;
            int resends = 0;
            int chunkRepDegree = 0;
            int timeToSleep = 1000;

            System.out.println("Preparing to send chunks");
            while((readBytes = bis.read(chunk)) > 0 ) {
                System.out.println("Read a chunk. Sending chunk with size: " + chunk.length);
                for(; resends < 5 && chunkRepDegree < repDegree; resends++) {
                    Header messageHeader = new Header("PUTCHUNK", Constants.PROTOCOL_VERSION, peer.getServerID(), hashedFileName, chunkNumber, repDegree);
                    Message msg = new Message(messageHeader, chunk);
                    DatagramPacket requestPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, peer.getMdbAddress(), peer.getMdbPort());
                    MulticastSocket sendSocket = peer.getMDB();
                    sendSocket.send(requestPacket);

                    //Await peer responses
                    Thread.sleep(timeToSleep *(long) Math.pow(1, (double)resends));

                    //Check responses and update chunkRepDegree.

                }

                resends = 0;
                chunkRepDegree = 0;
                chunkNumber++;
            }

        } catch(Exception e) {

        }

    }

    /**
     * Get a local file. Makes sure it exists and is not a directory
     * @param fileName the name of the file
     * @return the file
     */
    public File getLocalFile(String fileName) {
        //Check if file exists
        File f = new File(Constants.FILE_PATH + fileName);
        if (!f.exists() || f.isDirectory()) {
            System.err.println("Please make sure a file exists before you try to back it up");
            return null;
        }

        return f;
    }
}