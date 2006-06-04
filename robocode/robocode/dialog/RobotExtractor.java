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
 *     - Added keyboard mnemonics to buttons
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *******************************************************************************/
package robocode.dialog;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import robocode.peer.robot.RobotClassManager;
import robocode.repository.*;
import robocode.util.*;
import robocode.manager.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
public class RobotExtractor extends JDialog implements WizardListener {
	String unusedrobotPath;

	private int minRobots = 1;
	private int maxRobots = 1; // 250;

	private JPanel robotImporterContentPane;

	private WizardCardPanel wizardPanel;
	private WizardController buttonsPanel;
	private RobotSelectionPanel robotSelectionPanel;
	
	public byte buf[] = new byte[4096];
	private StringWriter output;
	private RobotRepositoryManager robotManager;

	private EventHandler eventHandler = new EventHandler();

	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList();
			}
		}
	}

	/**
	 * Packager constructor comment.
	 */
	public RobotExtractor(JFrame owner, RobotRepositoryManager robotManager) {
		super(owner);
		this.robotManager = robotManager;
		initialize();
	}

	public void cancelButtonActionPerformed() {
		AWTEvent evt = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);

		this.dispatchEvent(evt);
		return;
	}

	public void finishButtonActionPerformed() {
		int rc = extractRobot();
		ConsoleDialog d;

		d = new ConsoleDialog(robotManager.getManager().getWindowManager().getRobocodeFrame(), "Extract results", false);
		d.setText(output.toString());
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
	 * @return JButton
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
	 * Return the newBattleDialogContentPane
	 * 
	 * @return JPanel
	 */
	private JPanel getRobotImporterContentPane() {
		if (robotImporterContentPane == null) {
			robotImporterContentPane = new JPanel();
			robotImporterContentPane.setLayout(new BorderLayout());
			robotImporterContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);
			robotImporterContentPane.add(getWizardPanel(), BorderLayout.CENTER);
			getWizardPanel().getWizardController().setFinishButtonTextAndMnemonic("Extract!", 'E', 0);
			robotImporterContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			robotImporterContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), JComponent.WHEN_FOCUSED);
		}
		return robotImporterContentPane;
	}

	/**
	 * Return the Page property value.
	 * 
	 * @return JPanel
	 */
	public RobotSelectionPanel getRobotSelectionPanel() {
		if (robotSelectionPanel == null) {
			robotSelectionPanel = new RobotSelectionPanel(robotManager, minRobots, maxRobots, false,
					"Select the robot you would like to extract to the robots directory.  Robots not shown do not include source.",
					true, true, true, false, true, true, null);
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
		}
		return wizardPanel;
	}

	public void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Robot Extract");
		setContentPane(getRobotImporterContentPane());
	}

	private int extractRobot() {
		robotManager.clearRobotList();
		int rv = 0;

		output = new StringWriter();
		PrintWriter out = new PrintWriter(output);

		out.println("Robot Extract");
		Vector selectedRobots = getRobotSelectionPanel().getSelectedRobots(); // <FileSpecification>
		FileSpecification spec = (FileSpecification) selectedRobots.elementAt(0);

		try {
			Utils.setStatusWriter(out);
			rv = robotManager.extractJar(spec.getJarFile(), robotManager.getRobotsDirectory(),
					"Extracting to " + robotManager.getRobotsDirectory(), false, true, false);
			Utils.setStatusWriter(null);
			Utils.setStatus("");
			if (rv == 0) {
				out.println("Robot extracted successfully.");
			} else if (rv == -1) {
				out.println("Cancelled.");
			}
		} catch (Exception e) {
			out.println(e);
			rv = 8;
		}
		return rv;
	}
}
