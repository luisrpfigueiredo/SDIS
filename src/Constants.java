import java.io.File;
import java.security.MessageDigest;

/**
 * Created by Luís on 17/03/2016.
 */
public class Constants {
    public static String PROTOCOL_VERSION = "1.0";
    public static int PACKET_BUFFER_SIZE = 65536;
    public static String FILE_PATH = System.getProperty("user.dir") + File.separator + "src" + File.separator + "Local Files" + File.separator;
    public static int chunkSize = 64000; // 64KB
    public static int delay = 400; // 64KB


    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}