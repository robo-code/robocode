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
 *     Matthew Reeder
 *     - Minor changes for UI keyboard accessibility
 *     Flemming N. Larsen
 *     - Renamed 'enum' variables to allow compiling with Java 1.5
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Code cleanup
 *******************************************************************************/
package robocode.packager;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.jar.*;
import java.io.*;
import java.net.*;

import robocode.peer.robot.RobotClassManager;
import robocode.repository.*;
import robocode.dialog.*;
import robocode.util.NoDuplicateJarOutputStream;
import robocode.manager.*;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
public class RobotPackager extends JDialog implements WizardListener {

	private int minRobots = 1;
	private int maxRobots = 1; // 250;

	private JPanel robotPackagerContentPane;
	private WizardCardPanel wizardPanel;
	private WizardController buttonsPanel;
	private FilenamePanel filenamePanel;
	private ConfirmPanel confirmPanel;
	private RobotSelectionPanel robotSelectionPanel;
	private PackagerOptionsPanel packagerOptionsPanel;
	
	public byte buf[] = new byte[4096];
	private StringWriter output;
	private RobotRepositoryManager robotManager;

	private EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList();
			}
		}
	}

	/**
	 * Packager constructor comment.
	 */
	public RobotPackager(RobotRepositoryManager robotManager, boolean isTeamPackager) {
		super(robotManager.getManager().getWindowManager().getRobocodeFrame());
		this.robotManager = robotManager;
		initialize();
	}

	public void cancelButtonActionPerformed() {
		AWTEvent evt = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);

		this.dispatchEvent(evt);
	}

	public void copy(FileInputStream in, NoDuplicateJarOutputStream out) throws IOException {
		while (in.available() > 0) {
			int count = in.read(buf, 0, 4096);

			out.write(buf, 0, count);
		}
	}
		
	public void finishButtonActionPerformed() {
		String resultsString;
	
		int rc = packageRobots();
		ConsoleDialog d;

		d = new ConsoleDialog(robotManager.getManager().getWindowManager().getRobocodeFrame(), "Packaging results",
				false);
		if (rc == 0) {
			resultsString = "Robots Packaged Successfully.\n" + output.toString();
		} else if (rc == 4) {
			resultsString = "Robots Packaged, but with warnings.\n" + output.toString();
		} else if (rc == 8) {
			resultsString = "Robots Packaging failed.\n" + output.toString();
		} else {
			resultsString = "FATAL: Unknown return code " + rc + " from packager.\n" + output.toString();
		}
		d.setText(resultsString);
		d.pack();
		d.pack();
		Utils.packCenterShow(this, d);
		if (rc < 8) {
			this.dispose();
		}
	}

	/**
	 * Return the buttonsPanel
	 * 
	 * @return javax.swing.JButton
	 */
	private WizardController getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = getWizardPanel().getWizardController();
		}
		return buttonsPanel;
	}

	public Enumeration getClasses(RobotClassManager robotClassManager) throws ClassNotFoundException {
		robotClassManager.getRobotClassLoader().loadRobotClass(robotClassManager.getFullClassName(), true);
		return robotClassManager.getReferencedClasses();
	}

	/**
	 * Return the buttonsPanel
	 * 
	 * @return javax.swing.JButton
	 */
	private ConfirmPanel getConfirmPanel() {
		if (confirmPanel == null) {
			confirmPanel = new ConfirmPanel(this);
		}
		return confirmPanel;
	}

	/**
	 * Return the optionsPanel
	 * 
	 * @return robocode.packager.PackagerOptionsPanel
	 */
	protected FilenamePanel getFilenamePanel() {
		if (filenamePanel == null) {
			filenamePanel = new FilenamePanel(this);
		}
		return filenamePanel;
	}

	/**
	 * Return the optionsPanel
	 * 
	 * @return robocode.packager.PackagerOptionsPanel
	 */
	protected PackagerOptionsPanel getPackagerOptionsPanel() {
		if (packagerOptionsPanel == null) {
			packagerOptionsPanel = new PackagerOptionsPanel(this);
		}
		return packagerOptionsPanel;
	}

	/**
	 * Return the newBattleDialogContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getRobotPackagerContentPane() {
		if (robotPackagerContentPane == null) {
			robotPackagerContentPane = new javax.swing.JPanel();
			robotPackagerContentPane.setLayout(new BorderLayout());
			robotPackagerContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);
			robotPackagerContentPane.add(getWizardPanel(), BorderLayout.CENTER);
			getWizardPanel().getWizardController().setFinishButtonTextAndMnemonic("Package!", 'P', 0);
			robotPackagerContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			robotPackagerContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_FOCUSED);
		}
		return robotPackagerContentPane;
	}

	/**
	 * Return the Page property value.
	 * 
	 * @return javax.swing.JPanel
	 */
	public RobotSelectionPanel getRobotSelectionPanel() {
		if (robotSelectionPanel == null) {
			robotSelectionPanel = new RobotSelectionPanel(robotManager, minRobots, maxRobots, false,
					"Select the robot or team you would like to package.", /* true */false, false, false/* true */, true,
					false, true, null);
		}
		return robotSelectionPanel;
	}

	/**
	 * Return the tabbedPane.
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private WizardCardPanel getWizardPanel() {
		if (wizardPanel == null) {
			wizardPanel = new WizardCardPanel(this);
			wizardPanel.add(getRobotSelectionPanel(), "Select robot");
			wizardPanel.add(getPackagerOptionsPanel(), "Select options");
			wizardPanel.add(getFilenamePanel(), "Select filename");
			wizardPanel.add(getConfirmPanel(), "Confirm");
		}
		return wizardPanel;
	}

	private void initialize() {
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Robot Packager");
		setContentPane(getRobotPackagerContentPane());
	}

	private int packageRobots() {
		robotManager.clearRobotList();
		int rv = 0;

		output = new StringWriter();
		PrintWriter out = new PrintWriter(output);

		out.println("Robot Packager");
		Vector robotSpecificationsVector = robotManager.getRobotRepository().getRobotSpecificationsVector(// <FileSpecification>
				false, false, false, false, false, false);
		String jarFilename = getFilenamePanel().getFilenameField().getText();
		File f = new File(jarFilename);

		if (f.exists()) {
			int ok = JOptionPane.showConfirmDialog(this,
					jarFilename + " already exists.  Are you sure you want to replace it?", "Warning",
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.NO_OPTION) {
				out.println("Cancelled by user.");
				return -1;
			}
			if (ok == JOptionPane.CANCEL_OPTION) {
				out.println("Cancelled by user.");
				return -1;
			}
			out.println("Overwriting " + jarFilename);
		}
		Vector selectedRobots = getRobotSelectionPanel().getSelectedRobots(); // <FileSpecification>

		// Create the jar file
		Manifest manifest = new Manifest();

		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		String robots = "";

		for (int i = 0; i < selectedRobots.size(); i++) {
			robots += ((FileSpecification) selectedRobots.elementAt(i)).getFullClassName();
			if (i < selectedRobots.size() - 1) {
				robots += ",";
			}
		}
		manifest.getMainAttributes().put(new Attributes.Name("robots"), robots);
		NoDuplicateJarOutputStream jarout;

		try {
			out.println("Creating Jar file: " + f.getName());
			jarout = new NoDuplicateJarOutputStream(new FileOutputStream(f));
			jarout.setComment(robotManager.getManager().getVersionManager().getVersion() + " - Robocode version");
		} catch (Exception e) {
			out.println(e);
			return 8;
		}
		for (int i = 0; i < selectedRobots.size(); i++) {
			FileSpecification fileSpecification = (FileSpecification) selectedRobots.elementAt(i);

			if (fileSpecification instanceof RobotSpecification) {
				RobotSpecification robotSpecification = (RobotSpecification) fileSpecification;

				if (robotSpecification.isDevelopmentVersion()) {
					robotSpecification.setRobotDescription(getPackagerOptionsPanel().getDescriptionArea().getText());
					robotSpecification.setRobotJavaSourceIncluded(
							getPackagerOptionsPanel().getIncludeSource().isSelected());
					robotSpecification.setRobotAuthorName(getPackagerOptionsPanel().getAuthorField().getText());
					URL u = null;
					String w = getPackagerOptionsPanel().getWebpageField().getText();

					if (w.equals("")) {
						u = null;
					} else {
						try {
							u = new URL(w);
						} catch (MalformedURLException e) {
							try {
								u = new URL("http://" + w);
								getPackagerOptionsPanel().getWebpageField().setText(u.toString());
							} catch (MalformedURLException e2) {
								u = null;
							}
						}
					}
					robotSpecification.setRobotWebpage(u);
					robotSpecification.setRobocodeVersion(robotManager.getManager().getVersionManager().getVersion());
					try {
						robotSpecification.store(new FileOutputStream(new File(robotSpecification.getThisFileName())),
								"Robot Properties");
					} catch (IOException e) {
						rv = 4;
						out.println("Unable to save properties: " + e);
						out.println("Attempting to continue...");
					}
					// Create clone with version for jar
					robotSpecification = (RobotSpecification) robotSpecification.clone();
					robotSpecification.setRobotVersion(getPackagerOptionsPanel().getVersionField().getText());
					addRobotSpecification(out, jarout, robotSpecification);
				} else {
					out.println("You Cannot package a packaged robot!");
				}
			} else if (fileSpecification instanceof TeamSpecification) {
				TeamSpecification teamSpecification = (TeamSpecification) fileSpecification;
				URL u = null;
				String w = getPackagerOptionsPanel().getWebpageField().getText();

				if (w.equals("")) {
					u = null;
				} else {
					try {
						u = new URL(w);
					} catch (MalformedURLException e) {
						try {
							u = new URL("http://" + w);
							getPackagerOptionsPanel().getWebpageField().setText(u.toString());
						} catch (MalformedURLException e2) {
							u = null;
						}
					}
				}
				teamSpecification.setTeamWebpage(u);
				teamSpecification.setTeamDescription(getPackagerOptionsPanel().getDescriptionArea().getText());
				teamSpecification.setTeamAuthorName(getPackagerOptionsPanel().getAuthorField().getText());
				teamSpecification.setRobocodeVersion(robotManager.getManager().getVersionManager().getVersion());
				try {
					teamSpecification.store(new FileOutputStream(new File(teamSpecification.getThisFileName())),
							"Team Properties");
				} catch (IOException e) {
					rv = 4;
					out.println("Unable to save .team file: " + e);
					out.println("Attempting to continue...");
				}
			
				teamSpecification = (TeamSpecification) teamSpecification.clone();
				teamSpecification.setTeamVersion(getPackagerOptionsPanel().getVersionField().getText());

				StringTokenizer teamTokenizer;
				String bot;
				FileSpecification currentFileSpecification;

				teamTokenizer = new StringTokenizer(teamSpecification.getMembers(), ",");
				String newMembers = "";

				while (teamTokenizer.hasMoreTokens()) {
					if (!(newMembers.equals(""))) {
						newMembers += ",";
					}
					bot = teamTokenizer.nextToken();
					for (int j = 0; j < robotSpecificationsVector.size(); j++) {
						currentFileSpecification = (FileSpecification) robotSpecificationsVector.elementAt(j);
						// Teams cannot include teams
						if (currentFileSpecification instanceof TeamSpecification) {
							continue;
						}
						if (currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
							// Found team member
							RobotSpecification current = (RobotSpecification) currentFileSpecification;

							// Skip nonversioned packaged
							if (!current.isDevelopmentVersion()
									&& (current.getVersion() == null || current.getVersion().equals(""))) {
								continue;
							}
							if (current.isDevelopmentVersion()
									&& (current.getVersion() == null || current.getVersion().equals(""))) {
								current = (RobotSpecification) current.clone();
								current.setRobotVersion(
										"[" + getPackagerOptionsPanel().getVersionField().getText() + "]");
							}
							// Package this member
							newMembers += addRobotSpecification(out, jarout, current);
							break;
						}
					}
				}
				teamSpecification.setMembers(newMembers);
				try {
					try {
						JarEntry entry = new JarEntry(teamSpecification.getFullClassName().replace('.', '/') + ".team");

						jarout.putNextEntry(entry);
						teamSpecification.store(jarout, "Robocode Robot Team");
						jarout.closeEntry();
						out.println("Added: " + entry);
					} catch (java.util.zip.ZipException e) {
						if (e.getMessage().indexOf("duplicate entry") < 0) {
							throw e;
						}
						// ignore duplicate entry, fine, it's already there.
					}
				} catch (Throwable e) {
					rv = 8;
					out.println(e);
				}
			}
		}
		try {
			jarout.close();
		} catch (IOException e) {
			out.println(e);
			return 8;
		}
		robotManager.clearRobotList();
		out.println("Packaging complete.");
		return rv;
	}

	public String addRobotSpecification(PrintWriter out, NoDuplicateJarOutputStream jarout,
			RobotSpecification robotSpecification) {
		int rv = 0;

		if (!robotSpecification.isDevelopmentVersion()) {
			try {
				File inputJar = robotSpecification.getJarFile();

				try {
					JarEntry entry = new JarEntry(inputJar.getName());

					jarout.putNextEntry(entry);
					FileInputStream input = new FileInputStream(inputJar);

					copy(input, jarout);
					jarout.closeEntry();
					out.println("Added: " + entry);
				} catch (java.util.zip.ZipException e) {
					if (e.getMessage().indexOf("duplicate entry") < 0) {
						throw e;
					}
					// ignore duplicate entry, fine, it's already there.
				}
			} catch (Throwable e) {
				rv = 8;
				Utils.log(e);
				out.println(e);
			}
		} else {
			addToJar(out, jarout, robotSpecification);
		}
		String name = robotSpecification.getName() + " " + robotSpecification.getVersion();

		if (rv != 0) {
			return null;
		}
		return name;
	}

	public int addToJar(PrintWriter out, NoDuplicateJarOutputStream jarout, RobotSpecification robotSpecification) {
		int rv = 0;
		RobotClassManager classManager = new RobotClassManager((RobotSpecification) robotSpecification);

		try {
			Enumeration classes = getClasses(classManager);
			String rootDirectory = classManager.getRobotClassLoader().getRootDirectory();

			// Save props:
			try {
				JarEntry entry = new JarEntry(classManager.getFullClassName().replace('.', '/') + ".properties");

				jarout.putNextEntry(entry);
				robotSpecification.store(jarout, "Robot Properties");
				jarout.closeEntry();
				out.println("Added: " + entry);
			} catch (java.util.zip.ZipException e) {
				if (e.getMessage().indexOf("duplicate entry") < 0) {
					throw e;
				}
				// ignore duplicate entry, fine, it's already there.
			}
		
			File html = new File(rootDirectory, classManager.getFullClassName().replace('.', '/') + ".html");

			if (html.exists()) {
				try {
					JarEntry entry = new JarEntry(classManager.getFullClassName().replace('.', '/') + ".html");

					jarout.putNextEntry(entry);
					FileInputStream input = new FileInputStream(html);

					copy(input, jarout);
					jarout.closeEntry();
					out.println("Added: " + entry);
				} catch (java.util.zip.ZipException e) {
					if (e.getMessage().indexOf("duplicate entry") < 0) {
						throw e;
					}
					// ignore duplicate entry, fine, it's already there.
				}
			}
			while (classes.hasMoreElements()) {
				String className = (String) classes.nextElement();

				// Add source file if selected (not inner classes of course)
				if (getPackagerOptionsPanel().getIncludeSource().isSelected()) {
					if (className.indexOf("$") < 0) {
						File javaFile = new File(rootDirectory, className.replace('.', File.separatorChar) + ".java");

						if (javaFile.exists()) {
							try {
								JarEntry entry = new JarEntry(className.replace('.', '/') + ".java");

								jarout.putNextEntry(entry);
								FileInputStream input = new FileInputStream(javaFile);

								copy(input, jarout);
								jarout.closeEntry();
								out.println("Added: " + entry);
							} catch (java.util.zip.ZipException e) {
								if (e.getMessage().indexOf("duplicate entry") < 0) {
									throw e;
								}
								// ignore duplicate entry, fine, it's already there.
							}
						} else {
							out.println(className.replace('.', '/') + ".java does not exist.");
						}
					}
				}
				// Add class file
				try {
					JarEntry entry = new JarEntry(className.replace('.', '/') + ".class");

					jarout.putNextEntry(entry);
					FileInputStream input = new FileInputStream(
							new File(rootDirectory, className.replace('.', File.separatorChar) + ".class"));

					copy(input, jarout);
					jarout.closeEntry();
					out.println("Added: " + entry);
				} catch (java.util.zip.ZipException e) {
					if (e.getMessage().indexOf("duplicate entry") < 0) {
						throw e;
					}
					// ignore duplicate entry, fine, it's already there.
				}
			}
	
			File dataDirectory = new File(rootDirectory, classManager.getFullClassName().replace('.', '/') + ".data");

			if (dataDirectory.exists()) {
				File files[] = dataDirectory.listFiles();

				for (int j = 0; j < files.length; j++) {
					try {
						JarEntry entry = new JarEntry(
								classManager.getFullClassName().replace('.', '/') + ".data/" + files[j].getName());

						jarout.putNextEntry(entry);
						FileInputStream input = new FileInputStream(files[j]);

						copy(input, jarout);
						jarout.closeEntry();
						out.println("Added: " + entry);
					} catch (java.util.zip.ZipException e) {
						if (e.getMessage().indexOf("duplicate entry") < 0) {
							throw e;
						}
						// ignore duplicate entry, fine, it's already there.
					}
				}
			}
		} catch (Throwable e) {
			rv = 8;
			out.println(e);
		}
		return rv;
	}
}
