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
 *     - Code cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.*;
import robocode.repository.*;
import robocode.manager.BrowserManager;
import robocode.manager.RobocodeManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class RobotDescriptionPanel extends JPanel {
	private JLabel robotNameLabel;
	private JLabel descriptionLabel[] = new JLabel[3];
	private JPanel descriptionPanel;
	private JButton detailsButton;
	private JLabel robocodeVersionLabel;
	private JLabel filePathLabel;
	private String blankString;
	public FileSpecification currentRobotSpecification;
	private RobocodeManager manager;

	private EventManager eventManager = new EventManager();

	private class EventManager implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getDetailsButton()) {
				if (currentRobotSpecification != null) {
					URL htmlFile = currentRobotSpecification.getWebpage();

					if (htmlFile != null && !htmlFile.equals("")) {
						try {
							BrowserManager.openURL(htmlFile.toString());
						} catch (IOException ex) {}
					}
				}
			}
		}
	}

	/**
	 * NewBattleRobotsTabDescriptionPanel constructor.
	 */
	public RobotDescriptionPanel(RobocodeManager manager) {
		super();
		this.manager = manager;
		initialize();
		blankString = "";
		for (int i = 0; i < 72; i++) {
			blankString += " ";
		}
	}

	private JLabel getFilePathLabel() {
		if (filePathLabel == null) {
			filePathLabel = new JLabel();
		}
		return filePathLabel;
	}

	private JLabel getDescriptionLabel(int index) {
		if (descriptionLabel[index] == null) {
			descriptionLabel[index] = new JLabel();
			descriptionLabel[index].setFont(new Font("Monospaced", Font.PLAIN, 10));
			descriptionLabel[index].setHorizontalAlignment(JLabel.LEFT);
			descriptionLabel[index].setText(blankString);
		}
		return descriptionLabel[index];
	}

	private JPanel getDescriptionPanel() {
		if (descriptionPanel == null) {
			descriptionPanel = new JPanel();
			descriptionPanel.setAlignmentY(JPanel.CENTER_ALIGNMENT);
			descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));
			descriptionPanel.setBorder(BorderFactory.createEtchedBorder());
			for (int i = 0; i < 3; i++) {
				descriptionPanel.add(getDescriptionLabel(i));
			}
		}
		return descriptionPanel;
	}

	private JButton getDetailsButton() {
		if (detailsButton == null) {
			detailsButton = new JButton("Webpage");
			detailsButton.setMnemonic('W');
			detailsButton.setDisplayedMnemonicIndex(0);
			detailsButton.setVisible(false);
			detailsButton.setAlignmentY(JButton.CENTER_ALIGNMENT);
			detailsButton.addActionListener(eventManager);
		}
		return detailsButton;
	}

	private JLabel getRobocodeVersionLabel() {
		if (robocodeVersionLabel == null) {
			robocodeVersionLabel = new JLabel();
		}
		return robocodeVersionLabel;
	}

	private JLabel getRobotNameLabel() {
		if (robotNameLabel == null) {
			robotNameLabel = new JLabel();
			robotNameLabel.setHorizontalAlignment(JLabel.CENTER);
		}
		return robotNameLabel;
	}

	/**
	 * Return the Page property value.
	 * 
	 * @return JPanel
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		JPanel p = new JPanel();

		p.setLayout(new BorderLayout());
		p.add(getRobotNameLabel(), BorderLayout.CENTER);

		JPanel q = new JPanel();

		q.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		q.add(getRobocodeVersionLabel());

		p.add(q, BorderLayout.EAST);
		q = new JPanel();
		q.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		p.add(q, BorderLayout.WEST);

		add(p, BorderLayout.NORTH);

		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(getDetailsButton());
		add(p, BorderLayout.WEST);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		p.add(getDescriptionPanel()); // ,BorderLayout.CENTER);
		add(p, BorderLayout.CENTER);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		p.add(getFilePathLabel());
		add(p, BorderLayout.SOUTH);

		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS)); // new
		add(p, BorderLayout.EAST);
	}

	public void showDescription(FileSpecification robotSpecification) {
		this.currentRobotSpecification = robotSpecification;
		if (robotSpecification == null) {
			getRobotNameLabel().setText(" ");
			for (int i = 0; i < 3; i++) {
				getDescriptionLabel(i).setText(blankString);
			}
			getDetailsButton().setVisible(false);
			getRobocodeVersionLabel().setText("");
			getFilePathLabel().setText("");
		} else {
			String name = robotSpecification.getNameManager().getUniqueFullClassNameWithVersion();

			if (name.charAt(name.length() - 1) == '*') {
				name = name.substring(0, name.length() - 1) + " (development version)";
			}

			String s = robotSpecification.getAuthorName();

			if (s != null && !s.equals("")) {
				name += " by " + s;
			}
			getRobotNameLabel().setText(name);
			if (robotSpecification.getJarFile() != null) {
				getFilePathLabel().setText(robotSpecification.getJarFile().getPath());
			} else {
				getFilePathLabel().setText(robotSpecification.getFilePath());
			}

			String desc = robotSpecification.getDescription();
			int count = 0;

			if (desc != null) {
				StringTokenizer tok = new StringTokenizer(desc, "\n");

				while (tok.hasMoreTokens() && count < 3) {
					String line = tok.nextToken();

					if (line != null) {
						if (line.length() > 72) {
							line = line.substring(0, 72);
						}
						for (int i = line.length(); i < 72; i++) {
							line += " ";
						}
						getDescriptionLabel(count).setText(line);
					}
					count++;
				}
			}
			for (int i = count; i < 3; i++) {
				getDescriptionLabel(i).setText(blankString);
			}

			URL u = robotSpecification.getWebpage();

			getDetailsButton().setVisible(u != null && !u.equals(""));

			String v = robotSpecification.getRobocodeVersion();

			getRobocodeVersionLabel().setText(v == null ? "" : "Built for " + v);
		}
		getDescriptionPanel().setMaximumSize(getDescriptionPanel().getPreferredSize());
	}
}