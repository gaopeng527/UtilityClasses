
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeKit {

	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	public static Date parseEsTimestamp(String src) {
		
		try {
			return df.parse(src);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static DateFormat normal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static String normalFormat(Date date) {
		return normal.format(date);
	}
	
	public static void main(String[] args) {
		
		System.out.println(TimeKit.parseEsTimestamp("2015-12-31T10:00:00.000Z"));
	}
}
