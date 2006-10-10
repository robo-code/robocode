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
import robocode.manager.RobocodeManager;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
@SuppressWarnings("serial")
public class RobotDescriptionPanel extends JPanel {
	JLabel robotNameLabel;
	JLabel descriptionLabel[] = new JLabel[3];
	JPanel descriptionPanel;
	JButton detailsButton;
	JLabel authorNameLabel;
	JLabel authorEmailLabel;
	JLabel authorWebsiteLabel;
	JLabel javaSourceIncludedLabel;
	JLabel robotVersionLabel;
	JLabel robocodeVersionLabel;
	JLabel filePathLabel;
	String blankString;
	public FileSpecification currentRobotSpecification;
	private RobocodeManager manager;

	EventManager eventManager = new EventManager();

	class EventManager implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getDetailsButton()) {
				if (currentRobotSpecification != null) {
					URL htmlFile = currentRobotSpecification.getWebpage();

					if (htmlFile != null && !htmlFile.equals("")) {
						try {
							manager.getBrowserManager().openURL(htmlFile.toString());
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

	public JLabel getAuthorEmailLabel() {
		if (authorEmailLabel == null) {
			authorEmailLabel = new JLabel();
		}
		return authorEmailLabel;
	}

	public JLabel getFilePathLabel() {
		if (filePathLabel == null) {
			filePathLabel = new JLabel();
		}
		return filePathLabel;
	}

	public JLabel getAuthorNameLabel() {
		if (authorNameLabel == null) {
			authorNameLabel = new JLabel();
		}
		return authorNameLabel;
	}

	public JLabel getAuthorWebsiteLabel() {
		if (authorWebsiteLabel == null) {
			authorWebsiteLabel = new JLabel();
		}
		return authorWebsiteLabel;
	}

	public JLabel getDescriptionLabel(int index) {
		if (descriptionLabel[index] == null) {
			descriptionLabel[index] = new JLabel();
			descriptionLabel[index].setFont(new Font("Monospaced", Font.PLAIN, 10));
			descriptionLabel[index].setHorizontalAlignment(JLabel.LEFT);
			descriptionLabel[index].setText(blankString);
		}
		return descriptionLabel[index];
	}

	public JPanel getDescriptionPanel() {
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

	public JButton getDetailsButton() {
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

	public JLabel getJavaSourceIncludedLabel() {
		if (javaSourceIncludedLabel == null) {
			javaSourceIncludedLabel = new JLabel();
		}
		return javaSourceIncludedLabel;
	}

	public JLabel getRobocodeVersionLabel() {
		if (robocodeVersionLabel == null) {
			robocodeVersionLabel = new JLabel();
		}
		return robocodeVersionLabel;
	}

	public JLabel getRobotNameLabel() {
		if (robotNameLabel == null) {
			robotNameLabel = new JLabel();
			robotNameLabel.setHorizontalAlignment(JLabel.CENTER);
		}
		return robotNameLabel;
	}

	public JLabel getRobotVersionLabel() {
		if (robotVersionLabel == null) {
			robotVersionLabel = new JLabel();
		}
		return robotVersionLabel;
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
			} else {// descriptionPanel.setBorder(null);
			}
			for (int i = count; i < 3; i++) {
				getDescriptionLabel(i).setText(blankString);
			}

			URL u = robotSpecification.getWebpage();

			if (u != null && !u.equals("")) {
				getDetailsButton().setVisible(true);
			} else {
				getDetailsButton().setVisible(false);
			}

			String v = robotSpecification.getRobocodeVersion();

			if (v == null) {
				getRobocodeVersionLabel().setText("");
			} else {
				getRobocodeVersionLabel().setText("Built for " + v);
			}
		}
		getDescriptionPanel().setMaximumSize(getDescriptionPanel().getPreferredSize());
	}
}
