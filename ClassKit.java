
public class ClassKit {

	public static <T> T newInstance(Class<? extends T> clazz) {
		
		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		
	}
}
