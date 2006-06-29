/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Added setFixedSize()
 *     - Removed setLocationFix()
 *     - Added quoteFileName()
 *******************************************************************************/
package robocode.util;


import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

import robocode.control.RobocodeListener;
import robocode.security.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (current)
 */
public class Utils {
	private static Point origin = new Point(0, 0);
	private static WindowPositionManager windowPositionManager = new WindowPositionManager();
	private static JLabel statusLabel;
	private static PrintWriter statusWriter;
	private static JLabel defaultStatusLabel;
	private static RobocodeListener logListener;
	private static String logBuffer = "";
	private static Point locationFixer;

	private final static double TWO_PI = 2 * Math.PI;
	private final static double THREE_PI_OVER_TWO = 3 * Math.PI / 2;
	private final static double PI_OVER_TWO = Math.PI / 2;

	public static void checkAccess(String s) {
		SecurityManager securityManager = System.getSecurityManager();

		if (securityManager != null) {
			securityManager.checkPermission(new RobocodePermission(s));
		}
	}

	public static void setLogListener(RobocodeListener logListener) {
		checkAccess("Set log listener");
		Utils.logListener = logListener;
	}

	public static void center(Window w) {
		center(null, w);
	}

	public static void center(Window main, Window w) {
		center(main, w, true);
	}

	public static void center(Window main, Window w, boolean move) {
		Point location = null;
		Dimension size = null;

		Rectangle windowPosition = windowPositionManager.getWindowRect(w);

		if (windowPosition != null) {
			location = new Point(windowPosition.x, windowPosition.y);
			size = new Dimension(windowPosition.width, windowPosition.height);
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
				// Keep aspect ratio for robocodeframe.
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

			// windowPositionManager.setWindowRect(w.getName(),location,size);
		}

		w.setSize(size);
		if (move) {
			w.setLocation(fixLocation(w, location));
		}
	}

	public static void centerShow(Window main, Window window) {

		center(main, window);

		window.setVisible(true);
		// Second time to fix bug with menus in some jres
		window.setVisible(true);
	}

	public static void setFixedSize(JComponent component, Dimension size) {
		component.setPreferredSize(size);
		component.setMinimumSize(size);
		component.setMaximumSize(size);
	}

	public static boolean deleteDir(File f) {
		if (!f.isDirectory()) {
			return false;
		}

		File files[] = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				try {
					// Test for symlink and ignore.
					// Robocode won't create one, but just in case a user does...
					if (files[i].getCanonicalFile().getParentFile().equals(f.getCanonicalFile())) {
						deleteDir(files[i]);
						files[i].delete();
					} else {
						System.out.println("Warning: " + files[i] + " may be a symlink.  Ignoring.");
					}
				} catch (IOException e) {
					System.out.println("Warning: Cannot determine canonical file for " + files[i] + " - ignoring.");
				}
			} else {
				files[i].delete();
			}
		}
		f.delete();
		return true;
	}

	public static void error(JFrame frame, String msg) {
		Object[] options = { "OK" };

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

	public static String getClassName(String fileName) {
		int lastdot = fileName.lastIndexOf('.');

		if (lastdot < 0) {
			return fileName;
		}
		if (fileName.length() - 1 == lastdot) {
			return fileName.substring(0, fileName.length() - 1);
		}
		return fileName.substring(0, lastdot);
	}

	public static String getFileType(File f) {
		return getFileType(f.getName());
	}

	public static String getFileType(String fileName) {
		int lastdot = fileName.lastIndexOf('.');

		if (lastdot < 0) {
			return "";
		}
		return fileName.substring(lastdot);
	}

	public static String getPlacementString(int i) {
		String result = "" + i;

		if (i > 3 && i < 20) {
			result += "th";
		} else if (i % 10 == 1) {
			result += "st";
		} else if (i % 10 == 2) {
			result += "nd";
		} else if (i % 10 == 3) {
			result += "rd";
		} else {
			result += "th";
		}
		return result;
	}

	public static void log(String s) {
		if (logListener == null) {
			System.err.println(s);
		} else {
			logListener.battleMessage(s);
		}
	}

	public static void log(String s, Throwable e) {
		if (logListener == null) {
			System.err.println(s + ": " + e);
			e.printStackTrace(System.err);
		} else {
			logListener.battleMessage(s + ": " + e);
		}
	}

	public static void log(String s, boolean newline) {
		if (logListener == null) {
			if (newline) {
				System.err.println(s);
			} else {
				System.err.print(s);
				System.err.flush();
			}
		} else {
			if (newline) {
				logListener.battleMessage(logBuffer + s);
				logBuffer = "";
			} else {
				logBuffer += s;
			}
		}
	}

	public static void log(Throwable e) {
		if (logListener == null) {
			System.err.println(e);
			e.printStackTrace(System.err);
		} else {
			logListener.battleMessage("" + e);
		}
	}

	public static double normalAbsoluteAngle(double angle) {
		if (angle >= 0 && angle < 2.0 * Math.PI) {
			return angle;
		}
		double fixedAngle = angle;

		while (fixedAngle < 0) {
			fixedAngle += 2 * Math.PI;
		}
		while (fixedAngle >= 2 * Math.PI) {
			fixedAngle -= 2 * Math.PI;
		}
	
		return fixedAngle;
	}

	public static double normalNearAbsoluteAngle(double angle) {
		double fixedAngle = normalAbsoluteAngle(angle);

		if (isNear(fixedAngle, 0)) {
			fixedAngle = 0;
		} else if (isNear(fixedAngle, PI_OVER_TWO)) {
			fixedAngle = PI_OVER_TWO;
		} else if (isNear(fixedAngle, Math.PI)) {
			fixedAngle = Math.PI;
		} else if (isNear(fixedAngle, THREE_PI_OVER_TWO)) {
			fixedAngle = THREE_PI_OVER_TWO;
		} else if (isNear(fixedAngle, TWO_PI)) {
			fixedAngle = 0;
		}
	
		return fixedAngle;
	}

	private static boolean isNear(double angle1, double angle2) {
		if (Math.abs(angle1 - angle2) < .00001) {
			return true;
		}
		return false;
	}

	public static double normalRelativeAngle(double angle) {
		if (angle > -Math.PI && angle <= Math.PI) {
			return angle;
		}
		double fixedAngle = angle;

		while (fixedAngle <= -Math.PI) {
			fixedAngle += 2 * Math.PI;
		}
		while (fixedAngle > Math.PI) {
			fixedAngle -= 2 * Math.PI;
		}
		return fixedAngle;
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
		// Second time to fix bug with menus in some jres
		window.setVisible(true);
	}

	public static void packCenterShow(Window main, Window window, boolean pack) {
		// We don't want to receive the resize event for this pack!
		window.removeComponentListener(windowPositionManager);
		
		window.pack();

		center(main, window);

		window.setVisible(true);
		// Second time to fix bug with menus in some jres
		window.setVisible(true);
	}

	public static void packPlaceShow(Window window) {
		window.pack();

		place(window);

		window.setVisible(true);
		// Second time to fix bug with menus in some jres
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

		if (origin.x + size.width > screenSize.width) {
			origin.x = 0;
		}
		if (origin.y + size.height > screenSize.height) {
			origin.y = 0;
		}
		w.setLocation(fixLocation(w, origin));
		origin.x += 10;
		origin.y += 10;
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
		JOptionPane.showMessageDialog(null, s, "Message", JOptionPane.ERROR_MESSAGE);
	}

	public static void setStatus(String s) {
		checkAccess("setStatus");
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
		checkAccess("setStatusLabel");
		statusLabel = label;
	}

	public static void setStatusWriter(PrintWriter out) {
		checkAccess("setStatusWriter");
		statusWriter = out;
	}

	public static void setDefaultStatusLabel(JLabel label) {
		checkAccess("setDefaultStatusLabel");
		defaultStatusLabel = label;
	}

	public static void printRunningThreads() {
		ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();

		while (currentGroup.getParent() != null) {
			currentGroup = currentGroup.getParent();
		}
	
		ThreadGroup groups[] = new ThreadGroup[256];
		Thread threads[] = new Thread[256];
		int numGroups = currentGroup.enumerate(groups, true);

		for (int i = 0; i < numGroups; i++) {
			currentGroup = groups[i];
			if (currentGroup.isDaemon()) {
				System.out.print("  ");
			} else {
				System.out.print("* ");
			}
			System.out.println("In group: " + currentGroup.getName());
			int numThreads = currentGroup.enumerate(threads);

			for (int j = 0; j < numThreads; j++) {
				if (threads[j].isDaemon()) {
					System.out.print("  ");
				} else {
					System.out.print("* ");
				}
				System.out.println(threads[j].getName());
			}
			System.out.println("---------------");
		}
	}

	public static int compare(String p1, String c1, String v1, String p2, String c2, String v2) {
		if (p1 == null && p2 != null) {
			return 1;
		}
		if (p2 == null && p1 != null) {
			return -1;
		}
		
		if (p1 != null) // then p2 isn't either
		{
			// If packages are different, return
			int pc = p1.compareToIgnoreCase(p2);

			if (pc != 0) {
				return pc;
			}
		}
	
		// Ok, same package... compare class:
		int cc = c1.compareToIgnoreCase(c2);

		if (cc != 0) {
			// Different classes, return
			return cc;
		}

		// Ok, same robot... compare version
		if (v1 == null && v2 == null) {
			return 0;
		}
		if (v1 == null) {
			return 1;
		}
		if (v2 == null) {
			return -1;
		}

		if (v1.equals(v2)) {
			return 0;
		}
	
		if (v1.indexOf(".") < 0 || v2.indexOf(".") < 0) {
			return v1.compareToIgnoreCase(v2);
		}

		// Dot separated versions.
		StringTokenizer s1 = new StringTokenizer(v1, ".");
		StringTokenizer s2 = new StringTokenizer(v2, ".");

		while (s1.hasMoreTokens() && s2.hasMoreTokens()) {
			String tok1 = s1.nextToken();
			String tok2 = s2.nextToken();

			try {
				int i1 = Integer.parseInt(tok1);
				int i2 = Integer.parseInt(tok2);

				if (i1 != i2) {
					return i1 - i2;
				}
			} catch (NumberFormatException e) {
				int tc = tok1.compareToIgnoreCase(tok2);

				if (tc != 0) {
					return tc;
				}
			}
		}
		if (s1.hasMoreTokens()) {
			return 1;
		}
		if (s2.hasMoreTokens()) {
			return -1;
		}
		return 0;
	}

	public static boolean copy(File inFile, File outFile) throws IOException {
		if (inFile.equals(outFile)) {
			throw new IOException("You cannot copy a file onto itself");
		}
		byte buf[] = new byte[4096];
		FileInputStream in = new FileInputStream(inFile);
		FileOutputStream out = new FileOutputStream(outFile);

		while (in.available() > 0) {
			int count = in.read(buf, 0, 4096);

			out.write(buf, 0, count);
		}
		return true;
	}

	private static Point fixLocation(Window w, Point p) {
		return (locationFixer != null) ? new Point(p.x + locationFixer.x, p.y + locationFixer.y) : p;
	}
	
	public static String quoteFileName(String fileName) {
		if (fileName.matches(".*\\s+?.*")) {
			return '"' + fileName +'"';
		}	
		return fileName;
	}
}
