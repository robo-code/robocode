/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.ui.IWindowManager;
import static net.sf.robocode.ui.util.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

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

	private static final int MIN_ROBOTS = 1;
	private static final int MAX_ROBOTS = 1; // 250;

	private JPanel robotImporterContentPane;

	private WizardCardPanel wizardPanel;
	private WizardController buttonsPanel;
	private RobotSelectionPanel robotSelectionPanel;

	public byte buf[] = new byte[4096];
	private StringWriter output;
	private final IWindowManager windowManager;
	private final IRepositoryManager repositoryManager;

	private final EventHandler eventHandler = new EventHandler();

	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Refresh")) {
				getRobotSelectionPanel().refreshRobotList(false);
			}
		}
	}

	public RobotExtractor(JFrame owner, IWindowManager windowManager, IRepositoryManager repositoryManager) {
		super(owner);
		this.repositoryManager = repositoryManager;
		this.windowManager = windowManager;
		initialize();
	}

	public void update() {
		getRobotSelectionPanel().refreshRobotList(false);
	}

	public void cancelButtonActionPerformed() {
		dispose();
	}

	public void finishButtonActionPerformed() {
		int rc = extractRobot();
		ConsoleDialog d;

		d = new ConsoleDialog(windowManager.getRobocodeFrame(), "Extract results", false);
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
			robotSelectionPanel = net.sf.robocode.core.Container.createComponent(RobotSelectionPanel.class);
			robotSelectionPanel.setup(MIN_ROBOTS, MAX_ROBOTS, false,
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
		repositoryManager.refresh();
		int rv = 0;

		output = new StringWriter();
		PrintWriter out = new PrintWriter(output);

		out.println("Robot Extract");
		List<IRobotSpecItem> selectedRobots = getRobotSelectionPanel().getSelectedRobots();
		IRobotSpecItem spec = selectedRobots.get(0);

		try {
			WindowUtil.setStatusWriter(out);

			rv = repositoryManager.extractJar(spec);
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
