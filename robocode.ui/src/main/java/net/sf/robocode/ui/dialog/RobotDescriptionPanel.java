/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.ui.BrowserManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.StringTokenizer;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobotDescriptionPanel extends JPanel {
	private JLabel robotNameLabel;
	private final JLabel[] descriptionLabel = new JLabel[3];
	private JPanel descriptionPanel;
	private JButton webpageButton;
	private JLabel robocodeVersionLabel;
	private JLabel filePathLabel;
	private IRobotSpecItem currentRobotSpecification;

	private final static String BLANK_STRING = "                                                                        ";

	private final EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == getWebpageButton()) {
				if (currentRobotSpecification != null) {
					URL htmlFile = currentRobotSpecification.getWebpage();

					if (htmlFile != null && htmlFile.toString().length() > 0) {
						try {
							BrowserManager.openURL(htmlFile.toString());
						} catch (IOException ignored) {}
					}
				}
			}
		}
	}

	public RobotDescriptionPanel() {
		super();
		initialize();
	}

	private JLabel getFilePathLabel() {
		if (filePathLabel == null) {
			filePathLabel = new JLabel();
			filePathLabel.setText(" ");
		}
		return filePathLabel;
	}

	private JLabel getDescriptionLabel(int index) {
		if (descriptionLabel[index] == null) {
			descriptionLabel[index] = new JLabel();
			descriptionLabel[index].setFont(new Font("Monospaced", Font.PLAIN, 12));
			descriptionLabel[index].setHorizontalAlignment(SwingConstants.LEFT);
			descriptionLabel[index].setText(BLANK_STRING);
		}
		return descriptionLabel[index];
	}

	private JPanel getDescriptionPanel() {
		if (descriptionPanel == null) {
			descriptionPanel = new JPanel();
			descriptionPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
			descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));
			descriptionPanel.setBorder(BorderFactory.createEtchedBorder());
			for (int i = 0; i < 3; i++) {
				descriptionPanel.add(getDescriptionLabel(i));
			}
		}
		return descriptionPanel;
	}

	private JButton getWebpageButton() {
		if (webpageButton == null) {
			webpageButton = new JButton("Webpage");
			webpageButton.setMnemonic('W');
			webpageButton.setVisible(false);
			webpageButton.setAlignmentY(Component.CENTER_ALIGNMENT);
			webpageButton.addActionListener(eventHandler);
		}
		return webpageButton;
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
			robotNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
			robotNameLabel.setText(" ");
		}
		return robotNameLabel;
	}

	private void initialize() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

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
		add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		p.add(getDescriptionPanel());
		add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		p.add(getWebpageButton());
		add(p);

		p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		p.add(getFilePathLabel());
		add(p);
	}

	public void showDescription(IRobotSpecItem robotSpecification) {
		this.currentRobotSpecification = robotSpecification;
		if (robotSpecification == null) {
			getRobotNameLabel().setText(" ");
			for (int i = 0; i < 3; i++) {
				getDescriptionLabel(i).setText(BLANK_STRING);
			}
			getWebpageButton().setVisible(false);
			getRobocodeVersionLabel().setText(" ");
			getFilePathLabel().setText(" ");
		} else {
			String name = robotSpecification.getUniqueFullClassNameWithVersion();

			if (name.charAt(name.length() - 1) == '*') {
				name = name.substring(0, name.length() - 1) + " (development version)";
			}

			String s = robotSpecification.getAuthorName();

			if (s != null && s.length() > 0) {
				name += " by " + s;
			}
			getRobotNameLabel().setText(name);
			final URL url = robotSpecification.getItemURL();

			if (url != null) {
				String path = url.toString();

				try {
					path = URLDecoder.decode(url.toString(), "UTF-8");
				} catch (UnsupportedEncodingException ignore) {}

				getFilePathLabel().setText(path);
			} else {
				getFilePathLabel().setText("");
			}

			String desc = robotSpecification.getDescription();
			int count = 0;

			if (desc != null) {
				StringTokenizer tok = new StringTokenizer(desc, "\n");

				while (tok.hasMoreTokens() && count < 3) {
					StringBuffer line = new StringBuffer(tok.nextToken());

					if (line != null) {
						if (line.length() > BLANK_STRING.length()) {						
							line.delete(BLANK_STRING.length(), line.length());
						}
						for (int i = line.length(); i < BLANK_STRING.length(); i++) {
							line.append(' ');
						}
						getDescriptionLabel(count).setText(line.toString());
					}
					count++;
				}
			}
			for (int i = count; i < 3; i++) {
				getDescriptionLabel(i).setText(BLANK_STRING);
			}

			URL u = robotSpecification.getWebpage();

			getWebpageButton().setVisible(u != null && u.toString().length() > 0);

			String v = robotSpecification.getRobocodeVersion();

			getRobocodeVersionLabel().setText(v == null ? "" : "Built for " + v);
		}
		getDescriptionPanel().setMaximumSize(getDescriptionPanel().getPreferredSize());
	}
}
