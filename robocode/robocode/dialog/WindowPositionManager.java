/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Moved this class from the robocode.util package into the robocode.dialog
 *       package
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import robocode.io.FileUtil;
import robocode.io.Logger;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class WindowPositionManager implements ComponentListener {
	private Properties windowPositions = null;

	/**
	 * WindowPositionManager constructor comment.
	 */
	public WindowPositionManager() {
		super();
	}

	public Properties getWindowPositions() {
		if (windowPositions == null) {
			windowPositions = new Properties();
			try {
				FileInputStream in = new FileInputStream(new File(FileUtil.getCwd(), "window.properties"));

				windowPositions.load(in);
			} catch (FileNotFoundException e) {
				Logger.log("Creating window.properties file");
			} catch (Exception e) {
				Logger.log(e);
			}
		}
		return windowPositions;
	}

	public void componentHidden(ComponentEvent e) {}

	public void componentMoved(ComponentEvent e) {
		// Hack, because we cannot detect maximized frame in Java 1.3
		if (e.getComponent().getBounds().getWidth() >= Toolkit.getDefaultToolkit().getScreenSize().width
				|| e.getComponent().getBounds().getHeight() >= Toolkit.getDefaultToolkit().getScreenSize().height) {
			return;
		}
		setWindowRect(true, (Window) e.getComponent(), e.getComponent().getBounds());
	}

	public void componentResized(ComponentEvent e) {
		// Hack, because we cannot detect maximized frame in Java 1.3
		if (e.getComponent().getBounds().getWidth() >= Toolkit.getDefaultToolkit().getScreenSize().width
				|| e.getComponent().getBounds().getHeight() >= Toolkit.getDefaultToolkit().getScreenSize().height) {
			return;
		}
		setWindowRect(false, (Window) e.getComponent(), e.getComponent().getBounds());
	}

	public void componentShown(ComponentEvent e) {}

	public void setWindowRect(boolean move, Window w, Rectangle rect) {
		String rString = new String(rect.x + "," + rect.y + "," + rect.width + "," + rect.height);

		getWindowPositions().put(w.getClass().getName(), rString);
	}

	public Rectangle getWindowRect(Window window) {
		window.addComponentListener(this);

		String rString = (String) getWindowPositions().get(window.getClass().getName());

		if (rString == null) {
			return null;
		} else {
			StringTokenizer tokenizer = new StringTokenizer(rString, ",");
			int x = Integer.parseInt(tokenizer.nextToken());
			int y = Integer.parseInt(tokenizer.nextToken());
			int width = Integer.parseInt(tokenizer.nextToken());
			int height = Integer.parseInt(tokenizer.nextToken());

			return new Rectangle(x, y, width, height);
		}
	}

	public void saveWindowPositions() {
		try {
			FileOutputStream out = new FileOutputStream(new File(FileUtil.getCwd(), "window.properties"));

			getWindowPositions().store(out, "Robocode window sizes");
		} catch (IOException e) {
			Logger.log("Warning:  Unable to save window positions: " + e);
		}
	}
}
