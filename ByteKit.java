import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class ByteKit {

	public static String toHex(byte[] bytes) {
		
		StringBuilder sb = new StringBuilder();

		for (byte b : bytes) {
			byte high = (byte) ((b & 0xf0) >> 4);
			byte low = (byte) ((b & 0x0f));
			
			sb.append(toHex(high)).append(toHex(low));
		}
		
		return sb.toString();
	}
	
	private static char toHex(byte b) {
		
		return b < 10 ? (char) ('0' + b) : (char) ('A' + (b - 10));
	}
	
	public static byte[] randomBytes(int length) {
		
		Random random = new SecureRandom();
		byte[] bytes = new byte[length];
		random.nextBytes(bytes);
		
		return bytes;
	}
	
	public static byte[] inputStreamToBytes0(InputStream in, int size) throws IOException { 
		if (size <= 0) {
			size = 1024;
		}
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream(size);  
        byte[] buff = new byte[1024];  
        int rc;  
        while ((rc = in.read(buff, 0, 1024)) != -1) {  
            bytestream.write(buff, 0, rc);  
        }  
        byte data[] = bytestream.toByteArray();  
        bytestream.close();  
        
        return data;  
    }  
	
	public static byte[] inputStreamToBytes(InputStream in, int size) {
		try {
			return inputStreamToBytes0(in, size);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String randomBytesHex(int length) {
		
		return toHex(randomBytes(length));
	}
	
	public static MessageDigest md5Digest() {
		
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return md5;
	}
	
	public static void main(String[] args) {
		
		System.out.println(randomBytesHex(4));
	}
}
