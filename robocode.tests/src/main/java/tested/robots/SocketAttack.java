/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package tested.robots;


import robocode.AdvancedRobot;
import java.io.*;
import java.net.*;


/**
 * @author Flemming N. Larsen (original)
 */
public class SocketAttack extends AdvancedRobot {

	public void run() {
		ServerSocket server = null;
		Socket client = null;

		try {
			server = new ServerSocket(9999);
			client = server.accept();

			InputStream is = new DataInputStream(client.getInputStream());
			OutputStream os = new PrintStream(client.getOutputStream());

			os.write(1);
			is.read();
		} catch (IOException e) {
			e.printStackTrace(out);
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {}
			}
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {}
			}
		}
	}
}
