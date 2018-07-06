package test.quickstart;

public class Lala {

	public static String getString() {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("com.google.code.eventsonfire.Events");
		} catch (ClassNotFoundException e) {
			// ignore
		}
		return String.valueOf(clazz);
	}
}
