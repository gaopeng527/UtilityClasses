
import java.util.Properties;

public class PropertiesHelper {

	private final Properties properties;

	public PropertiesHelper(Properties properties) {
		this.properties = properties;
	}
	
	public String getValue(String key) {
		
		String val = properties.getProperty(key);
		return val;
	}
	
	public boolean contains(String key) {
		return properties.getProperty(key) != null;
	}
	
	public String getStr(String key, String defaultValue) {
		String val = getValue(key);
		return val != null ? val : defaultValue;
	}
	
	public boolean getBoolean(String key, boolean defautlValue) {
		String val = getValue(key);
		return val != null ? Boolean.parseBoolean(val) : defautlValue;
	}
	
	public long getLong(String key, long defaultValue) {
		String val = getValue(key);
		return val != null ? Long.parseLong(val) : defaultValue;
	}
}
