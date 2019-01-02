/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class WindowPositionManager implements ComponentListener {

	private Properties windowPositions;

	public WindowPositionManager() {
		super();
	}

	public Properties getWindowPositions() {
		if (windowPositions == null) {
			windowPositions = new Properties();

			FileInputStream in = null;

			try {
				in = new FileInputStream(FileUtil.getWindowConfigFile());
				windowPositions.load(in);
			} catch (FileNotFoundException e) {
				Logger.logMessage("Creating " + FileUtil.getWindowConfigFile().getName() + " file");
			} catch (Exception e) {
				Logger.logError(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ignored) {}
				}
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
		setWindowRect((Window) e.getComponent(), e.getComponent().getBounds());
	}

	public void componentResized(ComponentEvent e) {
		// Hack, because we cannot detect maximized frame in Java 1.3
		if (e.getComponent().getBounds().getWidth() >= Toolkit.getDefaultToolkit().getScreenSize().width
				|| e.getComponent().getBounds().getHeight() >= Toolkit.getDefaultToolkit().getScreenSize().height) {
			return;
		}
		setWindowRect((Window) e.getComponent(), e.getComponent().getBounds());
	}

	public void componentShown(ComponentEvent e) {}

	public void setWindowRect(Window w, Rectangle rect) {
		String rString = rect.x + "," + rect.y + "," + rect.width + "," + rect.height;

		getWindowPositions().put(w.getClass().getName(), rString);
	}

	public Rectangle getWindowRect(Window window) {
		window.addComponentListener(this);

		String rString = (String) getWindowPositions().get(window.getClass().getName());

		if (rString == null) {
			return null;
		}

		StringTokenizer tokenizer = new StringTokenizer(rString, ",");
		int x = Integer.parseInt(tokenizer.nextToken());
		int y = Integer.parseInt(tokenizer.nextToken());
		int width = Integer.parseInt(tokenizer.nextToken());
		int height = Integer.parseInt(tokenizer.nextToken());

		return fitWindowBoundsToScreen(new Rectangle(x, y, width, height));
	}

	public void saveWindowPositions() {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(FileUtil.getWindowConfigFile());

			getWindowPositions().store(out, "Robocode window sizes");
		} catch (IOException e) {
			Logger.logWarning("Unable to save window positions: " + e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignored) {}
			}
		}
	}

	private Rectangle fitWindowBoundsToScreen(Rectangle windowBounds) {
		if (windowBounds == null) {
			return null;
		}

		// Return the input window bounds, if we can find a screen device that contains these bounds

		final GraphicsEnvironment gfxEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final GraphicsDevice[] screenDevices = gfxEnv.getScreenDevices();

		for (int i = screenDevices.length - 1; i >= 0; i--) {
			GraphicsConfiguration[] gfxCfg = screenDevices[i].getConfigurations();

			for (int j = gfxCfg.length - 1; j >= 0; j--) {
				if (gfxCfg[j].getBounds().contains(windowBounds.getLocation())) {
					return windowBounds; // Found the bounds
				}
			}
		}

		// Otherwise, return window bounds at a location within the current screen

		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		int x = (screenSize.width - windowBounds.width) / 2;
		int y = (screenSize.height - windowBounds.height) / 2;

		return new Rectangle(x, y, windowBounds.width, windowBounds.height);
	}
}
