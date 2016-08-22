
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.alibaba.fastjson.JSON;

public class FileKit {

	public static void remove(File path) {
		
		if (path.isFile()) {
			path.delete();
		}
		else {
			for (File subPath : path.listFiles()) {
				remove(subPath);
			}
			path.delete();
		}
	}
	
	public static void removeSubs(File path) {
		
		if (path.isDirectory()) {
			for (File subPath : path.listFiles()) {
				remove(subPath);
			}
		}
	}
	
	public static void cleanOrMkdirs(File dir) {
		
		if (dir.exists()) {
			
			if (dir.isFile()) {
				throw new IllegalArgumentException("" + dir + " is not directory");
			}
			
			// remove all content
			FileKit.removeSubs(dir);
			
		} else {
			
			boolean success = dir.mkdirs();
			if (!success) {
				throw new IllegalArgumentException("dir make error");
			}
		}
	}
	
	public static void write(File file, String text) {
		
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new RuntimeException("file " + file + " can not be dir");
			}
			file.delete();
		}
		
		FileOutputStream out = null;
		try {
			file.createNewFile();
			out = new FileOutputStream(file);
			
			byte[] bytes = text.getBytes();
			
			out.write(bytes);
			out.flush();
			
		} catch (IOException e) {
			throw new RuntimeException("error write file " + file, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		
	}
	
	public static final int MAX_FILE_LENGTH = 50 * 1024 * 1024;
	
	public static String read(File file) {
		
		long fileLength = file.length();
		if (fileLength >= MAX_FILE_LENGTH) {
			throw new IllegalArgumentException("file " + file + " is to long len " + fileLength + " > " + MAX_FILE_LENGTH);
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		byte[] bytes = ByteKit.inputStreamToBytes(in, (int) fileLength);
		String text = new String(bytes);
		
		return text;
	}
	
	public static void writeObject(File file, Object obj) {
		
		String text = JSON.toJSONString(obj);
		FileKit.write(file, text);
	}
	
	public static <T> T readObject(File file, Class<T> clazz) {
		
		String text = FileKit.read(file);
		return JSON.parseObject(text, clazz);
	}
	
}
