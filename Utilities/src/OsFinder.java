public class OsFinder {

	public static void main(String[] args) {
		System.out.println(System.getProperty("os.name"));
		String osType = System.getProperty("os.name");
		
		if (osType.contains("Windows")) {
			System.out.println("Windso");
		}

	}

}
