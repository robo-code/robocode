/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Matthew Reeder
 *     - Minor changes for UI keyboard accessibility
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Renamed 'enum' variables to allow compiling with Java 1.5
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Updated to use methods from the Logger, which replaces logger methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Moved the NoDuplicateJarOutputStream into the robocode.io package
 *     - Added codesize information using the new outputSizeClass() method
 *     - Added missing close() on FileInputStreams and FileOutputStreams
 *     - Changed the F5 key press for refreshing the list of available robots
 *       into 'modifier key' + R to comply with other OSes like e.g. Mac OS
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.packager;


import static robocode.ui.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

import javax.swing.*;

import robocode.dialog.*;
import robocode.io.Logger;
import robocode.io.NoDuplicateJarOutputStream;
import robocode.manager.RobotRepositoryManager;
import robocode.peer.robot.RobotClassManager;
import robocode.repository.FileSpecification;
import robocode.repository.RobotSpecification;
import robocode.repository.TeamSpecification;
import robocode.security.RobocodeSecurityManager;

import codesize.Codesize;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
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
		ConsoleDialog d = new ConsoleDialog(robotManager.getManager().getWindowManager().getRobocodeFrame(),
				"Packaging results", false);

		if (rc < 8) {
			outputSizeClass();
		}

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
		WindowUtil.packCenterShow(this, d);
		if (rc < 8) {
			dispose();
		}
	}

	/**
	 * Return the buttonsPanel
	 *
	 * @return JButton
	 */
	private WizardController getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = getWizardPanel().getWizardController();
		}
		return buttonsPanel;
	}

	public Set<String> getClasses(RobotClassManager robotClassManager) throws ClassNotFoundException {
		robotClassManager.getRobotClassLoader().loadRobotClass(robotClassManager.getFullClassName(), true);
		return robotClassManager.getReferencedClasses();
	}

	/**
	 * Return the buttonsPanel
	 *
	 * @return JButton
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
	 * @return JPanel
	 */
	private JPanel getRobotPackagerContentPane() {
		if (robotPackagerContentPane == null) {
			robotPackagerContentPane = new JPanel();
			robotPackagerContentPane.setLayout(new BorderLayout());
			robotPackagerContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);
			robotPackagerContentPane.add(getWizardPanel(), BorderLayout.CENTER);
			getWizardPanel().getWizardController().setFinishButtonTextAndMnemonic("Package!", 'P', 0);
			robotPackagerContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			robotPackagerContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK), JComponent.WHEN_FOCUSED);
		}
		return robotPackagerContentPane;
	}

	/**
	 * Return the Page property value.
	 *
	 * @return JPanel
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
	 * @return JTabbedPane
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
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Robot Packager");
		setContentPane(getRobotPackagerContentPane());
	}

	private int packageRobots() {
		robotManager.clearRobotList();
		int rv = 0;

		output = new StringWriter();
		PrintWriter out = new PrintWriter(output);

		out.println("Robot Packager");
		List<FileSpecification> robotSpecificationsList = robotManager.getRobotRepository().getRobotSpecificationsList(
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
		List<FileSpecification> selectedRobots = getRobotSelectionPanel().getSelectedRobots();

		// Create the jar file
		Manifest manifest = new Manifest();

		manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		StringBuilder robots = new StringBuilder();

		for (int i = 0; i < selectedRobots.size(); i++) {
			robots.append(selectedRobots.get(i).getFullClassName());
			if (i < selectedRobots.size() - 1) {
				robots.append(',');
			}
		}
		manifest.getMainAttributes().put(new Attributes.Name("robots"), robots.toString());
		NoDuplicateJarOutputStream jarout;

		FileOutputStream fos = null;

		try {
			out.println("Creating Jar file: " + f.getName());

			fos = new FileOutputStream(f);
			jarout = new NoDuplicateJarOutputStream(fos);
			jarout.setComment(robotManager.getManager().getVersionManager().getVersion() + " - Robocode version");

			for (FileSpecification fileSpecification : selectedRobots) {
				if (fileSpecification instanceof RobotSpecification) {
					RobotSpecification robotSpecification = (RobotSpecification) fileSpecification;

					if (robotSpecification.isDevelopmentVersion()) {
						robotSpecification.setRobotDescription(getPackagerOptionsPanel().getDescriptionArea().getText());
						robotSpecification.setRobotJavaSourceIncluded(
								getPackagerOptionsPanel().getIncludeSource().isSelected());
						robotSpecification.setRobotAuthorName(getPackagerOptionsPanel().getAuthorField().getText());
						URL u = null;
						String w = getPackagerOptionsPanel().getWebpageField().getText();

						if (w.length() > 0) {
							try {
								u = new URL(w);
							} catch (MalformedURLException e) {
								try {
									u = new URL("http://" + w);
									getPackagerOptionsPanel().getWebpageField().setText(u.toString());
								} catch (MalformedURLException e2) {}
							}
						}
						robotSpecification.setRobotWebpage(u);
						robotSpecification.setRobocodeVersion(robotManager.getManager().getVersionManager().getVersion());

						FileOutputStream fos2 = null;

						try {
							fos2 = new FileOutputStream(new File(robotSpecification.getThisFileName()));
							robotSpecification.store(fos2, "Robot Properties");
						} catch (IOException e) {
							rv = 4;
							out.println("Unable to save properties: " + e);
							out.println("Attempting to continue...");
						} finally {
							if (fos2 != null) {
								try {
									fos2.close();
								} catch (IOException e) {}
							}
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

					if (w.length() > 0) {
						try {
							u = new URL(w);
						} catch (MalformedURLException e) {
							try {
								u = new URL("http://" + w);
								getPackagerOptionsPanel().getWebpageField().setText(u.toString());
							} catch (MalformedURLException e2) {}
						}
					}
					teamSpecification.setTeamWebpage(u);
					teamSpecification.setTeamDescription(getPackagerOptionsPanel().getDescriptionArea().getText());
					teamSpecification.setTeamAuthorName(getPackagerOptionsPanel().getAuthorField().getText());
					teamSpecification.setRobocodeVersion(robotManager.getManager().getVersionManager().getVersion());

					FileOutputStream fos2 = null;

					try {
						fos2 = new FileOutputStream(new File(teamSpecification.getThisFileName()));
						teamSpecification.store(fos2, "Team Properties");
					} catch (IOException e) {
						rv = 4;
						out.println("Unable to save .team file: " + e);
						out.println("Attempting to continue...");
					} finally {
						if (fos2 != null) {
							try {
								fos2.close();
							} catch (IOException e) {}
						}
					}

					teamSpecification = (TeamSpecification) teamSpecification.clone();
					teamSpecification.setTeamVersion(getPackagerOptionsPanel().getVersionField().getText());

					StringTokenizer teamTokenizer = new StringTokenizer(teamSpecification.getMembers(), ",");
					String bot;
					String newMembers = "";

					while (teamTokenizer.hasMoreTokens()) {
						if (!(newMembers.length() == 0)) {
							newMembers += ",";
						}
						bot = teamTokenizer.nextToken();
						for (FileSpecification currentFileSpecification : robotSpecificationsList) {
							// Teams cannot include teams
							if (currentFileSpecification instanceof TeamSpecification) {
								continue;
							}
							if (currentFileSpecification.getNameManager().getUniqueFullClassNameWithVersion().equals(bot)) {
								// Found team member
								RobotSpecification current = (RobotSpecification) currentFileSpecification;

								// Skip nonversioned packaged
								if (!current.isDevelopmentVersion()
										&& (current.getVersion() == null || current.getVersion().length() == 0)) {
									continue;
								}
								if (current.isDevelopmentVersion()
										&& (current.getVersion() == null || current.getVersion().length() == 0)) {
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
							JarEntry entry = new JarEntry(
									teamSpecification.getFullClassName().replace('.', '/') + ".team");

							jarout.putNextEntry(entry);
							teamSpecification.store(jarout, "Robocode Robot Team");
							jarout.closeEntry();
							out.println("Added: " + entry);
						} catch (ZipException e) {
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
			jarout.close();
		} catch (IOException e) {
			out.println(e);
			return 8;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {}
			}
		}
		robotManager.clearRobotList();
		out.println("Packaging complete.");
		return rv;
	}

	public void outputSizeClass() {
		// Codesize must be called within a safe thread to prevent security exception
		
		final RobocodeSecurityManager securityManager = (RobocodeSecurityManager)System.getSecurityManager();

		Thread thread = new Thread() {
			@Override
			public void run() {
				File jarFile = new File(getFilenamePanel().getFilenameField().getText());

				Codesize.Item item = Codesize.processZipFile(jarFile);

				int codesize = item.getCodeSize();

				String weightClass;

				if (codesize >= 1500) {
					weightClass = "MegaBot  (codesize >= 1500 bytes)";
				} else if (codesize > 750) {
					weightClass = "MiniBot  (codesize < 1500 bytes)";
				} else if (codesize > 250) {
					weightClass = "MicroBot  (codesize < 750 bytes)";
				} else {
					weightClass = "NanoBot  (codesize < 250 bytes)";
				}

				StringBuffer out = output.getBuffer();

				out.append("\n\n---- Codesize ----\n");
				out.append("Codesize: ").append(codesize).append(" bytes\n");
				out.append("Robot weight class: ").append(weightClass).append('\n');

				synchronized (this) {
					notify();
				}
			}
		};

		securityManager.addSafeThread(thread);

		thread.start();

		synchronized (thread) {
			try {
				thread.wait();
			} catch (InterruptedException e) {}
		}

		securityManager.removeSafeThread(thread);
	}

	public String addRobotSpecification(PrintWriter out, NoDuplicateJarOutputStream jarout,
			RobotSpecification robotSpecification) {
		int rv = 0;

		if (!robotSpecification.isDevelopmentVersion()) {
			try {
				File inputJar = robotSpecification.getJarFile();

				FileInputStream input = null;

				try {
					JarEntry entry = new JarEntry(inputJar.getName());

					jarout.putNextEntry(entry);
					input = new FileInputStream(inputJar);

					copy(input, jarout);
					jarout.closeEntry();
					out.println("Added: " + entry);
				} catch (ZipException e) {
					if (e.getMessage().indexOf("duplicate entry") < 0) {
						throw e;
					}
					// ignore duplicate entry, fine, it's already there.
				} finally {
					if (input != null) {
						input.close();
					}
				}
			} catch (Throwable e) {
				rv = 8;
				Logger.log(e);
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
		RobotClassManager classManager = new RobotClassManager(robotSpecification);

		try {
			Iterator<String> classes = getClasses(classManager).iterator();
			String rootDirectory = classManager.getRobotClassLoader().getRootDirectory();

			// Save props:
			try {
				JarEntry entry = new JarEntry(classManager.getFullClassName().replace('.', '/') + ".properties");

				jarout.putNextEntry(entry);
				robotSpecification.store(jarout, "Robot Properties");
				jarout.closeEntry();
				out.println("Added: " + entry);
			} catch (ZipException e) {
				if (e.getMessage().indexOf("duplicate entry") < 0) {
					throw e;
				}
				// ignore duplicate entry, fine, it's already there.
			}

			File html = new File(rootDirectory, classManager.getFullClassName().replace('.', '/') + ".html");

			if (html.exists()) {
				FileInputStream input = null;

				try {
					JarEntry entry = new JarEntry(classManager.getFullClassName().replace('.', '/') + ".html");

					jarout.putNextEntry(entry);
					input = new FileInputStream(html);

					copy(input, jarout);
					jarout.closeEntry();
					out.println("Added: " + entry);
				} catch (ZipException e) {
					if (e.getMessage().indexOf("duplicate entry") < 0) {
						throw e;
					}
					// ignore duplicate entry, fine, it's already there.
				} finally {
					if (input != null) {
						input.close();
					}
				}
			}
			while (classes.hasNext()) {
				String className = classes.next();

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
							} catch (ZipException e) {
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
				FileInputStream input = null;

				try {
					JarEntry entry = new JarEntry(className.replace('.', '/') + ".class");

					jarout.putNextEntry(entry);

					input = new FileInputStream(
							new File(rootDirectory, className.replace('.', File.separatorChar) + ".class"));

					copy(input, jarout);
					jarout.closeEntry();
					out.println("Added: " + entry);
				} catch (ZipException e) {
					if (e.getMessage().indexOf("duplicate entry") < 0) {
						throw e;
					}
					// ignore duplicate entry, fine, it's already there.
				} finally {
					if (input != null) {
						input.close();
					}
				}
			}

			File dataDirectory = new File(rootDirectory, classManager.getFullClassName().replace('.', '/') + ".data");

			if (dataDirectory.exists()) {
				File files[] = dataDirectory.listFiles();

				for (File file : files) {
					FileInputStream input = null;

					try {
						JarEntry entry = new JarEntry(
								classManager.getFullClassName().replace('.', '/') + ".data/" + file.getName());

						jarout.putNextEntry(entry);

						input = new FileInputStream(file);

						copy(input, jarout);
						jarout.closeEntry();
						out.println("Added: " + entry);
					} catch (ZipException e) {
						if (e.getMessage().indexOf("duplicate entry") < 0) {
							throw e;
						}
						// ignore duplicate entry, fine, it's already there.
					} finally {
						if (input != null) {
							input.close();
						}
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
