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
 *     - Added keyboard mnemonics to buttons
 *     Flemming N. Larsen
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Ported to Java 5
 *     - Updated to use methods from the WindowUtil, which replaces window methods
 *       that have been (re)moved from the robocode.util.Utils class
 *     - Changed the F5 key press for refreshing the list of available robots
 *       into 'modifier key' + R to comply with other OSes like e.g. Mac OS
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package robocode.dialog;


import robocode.manager.RobocodeManager;
import robocode.repository.INamedFileSpecification;
import static robocode.ui.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class RobotExtractor extends JDialog implements WizardListener {
	String unusedrobotPath;

	private final int minRobots = 1;
	private final int maxRobots = 1; // 250;

	private JPanel robotImporterContentPane;

	private WizardCardPanel wizardPanel;
	private WizardController buttonsPanel;
	private RobotSelectionPanel robotSelectionPanel;

	public byte buf[] = new byte[4096];
	private StringWriter output;
	private final RobocodeManager manager;

	private final EventHandler eventHandler = new EventHandler();

	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList(false);
			}
		}
	}

	public RobotExtractor(JFrame owner, RobocodeManager manager) {
		super(owner);
		this.manager = manager;
		initialize();
	}

	public void cancelButtonActionPerformed() {
		dispose();
	}

	public void finishButtonActionPerformed() {
		int rc = extractRobot();
		ConsoleDialog d;

		d = new ConsoleDialog(manager.getWindowManager().getRobocodeFrame(), "Extract results", false);
		d.setText(output.toString());
		d.pack();
		d.pack();
		WindowUtil.packCenterShow(this, d);
		if (rc < 8) {
			this.dispose();
		}
	}

	private WizardController getButtonsPanel() {
		if (buttonsPanel == null) {
			buttonsPanel = getWizardPanel().getWizardController();
		}
		return buttonsPanel;
	}

	private JPanel getRobotImporterContentPane() {
		if (robotImporterContentPane == null) {
			robotImporterContentPane = new JPanel();
			robotImporterContentPane.setLayout(new BorderLayout());
			robotImporterContentPane.add(getButtonsPanel(), BorderLayout.SOUTH);
			robotImporterContentPane.add(getWizardPanel(), BorderLayout.CENTER);
			getWizardPanel().getWizardController().setFinishButtonTextAndMnemonic("Extract!", 'E', 0);
			robotImporterContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK),
					JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
			robotImporterContentPane.registerKeyboardAction(eventHandler, "Refresh",
					KeyStroke.getKeyStroke(KeyEvent.VK_R, MENU_SHORTCUT_KEY_MASK), JComponent.WHEN_FOCUSED);
		}
		return robotImporterContentPane;
	}

	public RobotSelectionPanel getRobotSelectionPanel() {
		if (robotSelectionPanel == null) {
			robotSelectionPanel = new RobotSelectionPanel(manager, minRobots, maxRobots, false,
					"Select the robot you would like to extract to the robots directory.  Robots not shown do not include source.",
					true, true, true, false, true, true, null);
		}
		return robotSelectionPanel;
	}

	private WizardCardPanel getWizardPanel() {
		if (wizardPanel == null) {
			wizardPanel = new WizardCardPanel(this);
			wizardPanel.add(getRobotSelectionPanel(), "Select robot");
		}
		return wizardPanel;
	}

	private void initialize() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Robot Extract");
		setContentPane(getRobotImporterContentPane());
	}

	private int extractRobot() {
		manager.getRepositoryManager().clearRobotList();
		int rv;

		output = new StringWriter();
		PrintWriter out = new PrintWriter(output);

		out.println("Robot Extract");
		List<INamedFileSpecification> selectedRobots = getRobotSelectionPanel().getSelectedRobots();
		INamedFileSpecification spec = selectedRobots.get(0);

		try {
			WindowUtil.setStatusWriter(out);
			rv = manager.getRepositoryManager().extractJar(spec.getJarFile());
			WindowUtil.setStatusWriter(null);
			WindowUtil.setStatus("");
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
