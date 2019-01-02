/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package tested.robots;


import robocode.AdvancedRobot;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author Flemming N. Larsen (original)
 */
public class ConstructorHttpAttack extends AdvancedRobot {

	static final String HTTP_ADDR = "https://robocode.sourceforge.io/";

	public ConstructorHttpAttack() {
		try {
			URL url = new URL(HTTP_ADDR);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();

				is.read();
			}
		} catch (IOException e) {
			e.printStackTrace(out);
		}
	}
}
