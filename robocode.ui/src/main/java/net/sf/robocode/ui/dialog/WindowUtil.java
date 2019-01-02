/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;


/**
 * This is a class for window utilization.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class WindowUtil {

	private static final Point origin = new Point(0, 0);
	private static final WindowPositionManager windowPositionManager = new WindowPositionManager();
	private static JLabel statusLabel;
	private static PrintWriter statusWriter;
	private static JLabel defaultStatusLabel;

	public static void center(Window w) {
		WindowUtil.center(null, w);
	}

	public static void center(Window main, Window w) {
		WindowUtil.center(main, w, true);
	}

	public static void center(Window main, Window w, boolean move) {
		Point location = null;
		Dimension size = null;

		Rectangle windowRect = windowPositionManager.getWindowRect(w);

		if (windowRect != null) {
			location = new Point(windowRect.x, windowRect.y);
			size = new Dimension(windowRect.width, windowRect.height);
		}
		if (!move) {
			size = null;
		}
		if (location == null || size == null) {
			// Center a window
			Dimension screenSize;

			if (main != null) {
				screenSize = main.getSize();
			} else {
				screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			}
			size = w.getSize();
			if (size.height > screenSize.height - 20 || size.width > screenSize.width - 20) {
				// Keep aspect ratio for the robocode frame.
				if (w.getName().equals("RobocodeFrame")) {
					int shrink = size.width - screenSize.width + 20;

					if (size.height - screenSize.height + 20 > shrink) {
						shrink = size.height - screenSize.height + 20;
					}

					size.width -= shrink;
					size.height -= shrink;
				} else {
					if (size.height > screenSize.height - 20) {
						size.height = screenSize.height - 20;
					}
					if (size.width > screenSize.width - 20) {
						size.width = screenSize.width - 20;
					}
				}
			}
			if (main != null) {
				location = main.getLocation();
				location.x += (screenSize.width - size.width) / 2;
				location.y += (screenSize.height - size.height) / 2;
			} else {
				location = new Point((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
			}
		}

		w.setSize(size);
		if (move) {
			w.setLocation(location);
		}
	}

	public static void centerShow(Window main, Window window) {
		center(main, window);
		window.setVisible(true);
	}

	public static void setFixedSize(JComponent component, Dimension size) {
		component.setPreferredSize(size);
		component.setMinimumSize(size);
		component.setMaximumSize(size);
	}

	public static void error(JFrame frame, String msg) {
		Object[] options = { "OK"};

		JOptionPane.showOptionDialog(frame, msg, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
				options, options[0]);
	}

	public static void fitWindow(Window w) {
		// We don't want to receive the resize event for this pack!
		// ... yes we do!
		// w.removeComponentListener(windowPositionManager);
		w.pack();

		center(null, w, false);
	}

	public static void packCenterShow(Window window) {
		// We don't want to receive the resize event for this pack!
		window.removeComponentListener(windowPositionManager);
		window.pack();
		center(window);
		window.setVisible(true);
	}

	public static void packCenterShow(Window main, Window window) {
		// We don't want to receive the resize event for this pack!
		window.removeComponentListener(windowPositionManager);
		window.pack();
		center(main, window);
		window.setVisible(true);
	}

	public static void packPlaceShow(Window window) {
		window.pack();
		WindowUtil.place(window);
		window.setVisible(true);
	}

	public static void place(Window w) {
		// Center a window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = w.getSize();

		if (size.height > screenSize.height) {
			size.height = screenSize.height;
		}
		if (size.width > screenSize.width) {
			size.width = screenSize.width;
		}

		w.setLocation(origin);
		origin.y += 150;
		if (origin.y + size.height > screenSize.height) {
			origin.y = 0;
			origin.x += 40;
		}
		if (origin.x + size.width > screenSize.width) {
			origin.x = 0;
		}
	}

	public static void saveWindowPositions() {
		windowPositionManager.saveWindowPositions();
	}

	public static void message(String s) {
		JOptionPane.showMessageDialog(null, s, "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void messageWarning(String s) {
		JOptionPane.showMessageDialog(null, s, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	public static void messageError(String s) {
		Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(null, s, "Message", JOptionPane.ERROR_MESSAGE);
	}

	public static void setStatus(String s) {
		if (statusWriter != null) {
			statusWriter.println(s);
		}
		if (statusLabel != null) {
			statusLabel.setText(s);
		} else if (defaultStatusLabel != null) {
			defaultStatusLabel.setText(s);
		}
	}

	public static void setStatusLabel(JLabel label) {
		statusLabel = label;
	}

	public static void setDefaultStatusLabel(JLabel label) {
		defaultStatusLabel = label;
	}

	public static void setStatusWriter(PrintWriter out) {
		statusWriter = out;
	}
}
