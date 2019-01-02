/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.packager;


import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.ui.dialog.WizardPanel;
import net.sf.robocode.ui.util.LimitedDocument;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class PackagerOptionsPanel extends WizardPanel {
	private final RobotPackager robotPackager;

	private final EventHandler eventHandler = new EventHandler();

	private JCheckBox includeSource;
	private JCheckBox includeData;
	private JLabel authorLabel;
	private JTextField authorField;
	private JLabel descriptionLabel;
	private JTextArea descriptionArea;
	private JLabel versionLabel;
	private JTextField versionField;
	private JLabel versionHelpLabel;
	private JLabel webpageLabel;
	private JTextField webpageField;
	private JLabel webpageHelpLabel;

	private List<IRobotSpecItem> currentSelectedRobots;

	private class EventHandler implements ComponentListener, KeyListener, DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void changedUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void removeUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		public void componentMoved(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {}

		public void componentShown(ComponentEvent e) {
			List<IRobotSpecItem> selectedRobots = robotPackager.getRobotSelectionPanel().getSelectedRobots();

			// Make sure we don't reset content. Bug fix [3026856]
			if (selectedRobots == null || selectedRobots.equals(currentSelectedRobots)) {
				return;
			}
			currentSelectedRobots = selectedRobots; // Bug fix [3026856]

			if (selectedRobots.size() == 1) {
				IRobotSpecItem robotSpecItem = selectedRobots.get(0);

				getIncludeSource().setSelected(robotSpecItem.getIncludeSource());
				
				getIncludeData().setSelected(robotSpecItem.getIncludeData());

				String ver = robotSpecItem.getVersion();
				if (ver == null || ver.length() == 0) {
					getVersionHelpLabel().setVisible(false);
					ver = "1.0";
				} else {
					if (ver.length() == 10) {
						ver = ver.substring(0, 9);
					}
					ver += "*";
					getVersionHelpLabel().setVisible(true);
				}
				getVersionField().setText(ver);

				String desc = robotSpecItem.getDescription();
				if (desc == null) {
					desc = "";
				}
				getDescriptionArea().setText(desc);

				String author = robotSpecItem.getAuthorName();
				if (author == null) {
					author = "";
				}
				getAuthorField().setText(author);

				URL url = robotSpecItem.getWebpage();
				String webPage = (url != null) ? url.toString() : "";		
				getWebpageField().setText(webPage);

				String fullPackage = robotSpecItem.getFullPackage();

				String text = "";
				if (fullPackage != null && fullPackage.indexOf(".") != -1) {
					String htmlFileName = fullPackage.substring(0, fullPackage.lastIndexOf(".")) + ".html";

					text = "(You may also leave this blank, and simply create the file: " + htmlFileName + ")";
				}
				getWebpageHelpLabel().setText(text);

				getVersionLabel().setVisible(true);
				getVersionField().setVisible(true);
				getAuthorLabel().setVisible(true);
				getAuthorField().setVisible(true);
				getWebpageLabel().setVisible(true);
				getWebpageField().setVisible(true);
				getWebpageHelpLabel().setVisible(true);
				getDescriptionLabel().setText(
						"Please enter a short description of your robot (up to 3 lines of 72 chars each).");
			} else if (selectedRobots.size() > 1) {
				getVersionLabel().setVisible(false);
				getVersionField().setVisible(false);
				getVersionHelpLabel().setVisible(false);
				getAuthorLabel().setVisible(false);
				getAuthorField().setVisible(false);
				getWebpageLabel().setVisible(false);
				getWebpageField().setVisible(false);
				getWebpageHelpLabel().setVisible(false);
				getDescriptionLabel().setText(
						"Please enter a short description of this robot collection (up to 3 lines of 72 chars each).");
				if (getDescriptionArea().getText() == null || getDescriptionArea().getText().length() == 0) {
					getDescriptionArea().setText("(Example)This robot comes from the ... robot collection\n");
				}
			}
		}

		public void componentResized(ComponentEvent e) {}

		public void keyPressed(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}

		public void keyTyped(KeyEvent e) {}
	}

	public JPanel robotListPanel;

	public PackagerOptionsPanel(RobotPackager robotPackager) {
		super();
		this.robotPackager = robotPackager;
		initialize();
	}

	private void initialize() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel label = new JLabel(
				"It is up to you whether or not to include the source files when you distribute your robot.");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		label = new JLabel(
				"If you include the source files, other people will be able to look at your code and learn from it.");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		getIncludeSource().setAlignmentX(Component.LEFT_ALIGNMENT);
		add(getIncludeSource());

		label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		label = new JLabel("You may include the source files too if they are available?");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		getIncludeData().setAlignmentX(Component.LEFT_ALIGNMENT);
		add(getIncludeData());

		label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		add(getVersionLabel());

		JPanel versionPanel = new JPanel();
		versionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		versionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		getVersionField().setAlignmentX(Component.LEFT_ALIGNMENT);
		getVersionField().setMaximumSize(getVersionField().getPreferredSize());
		versionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, getVersionField().getPreferredSize().height));
		versionPanel.add(getVersionField());
		versionPanel.add(getVersionHelpLabel());
		add(versionPanel);

		label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		add(getDescriptionLabel());

		JScrollPane scrollPane = new JScrollPane(getDescriptionArea(), ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPane.setMaximumSize(scrollPane.getPreferredSize());
		scrollPane.setMinimumSize(new Dimension(100, scrollPane.getPreferredSize().height));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(scrollPane);

		label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		add(getAuthorLabel());

		getAuthorField().setAlignmentX(Component.LEFT_ALIGNMENT);
		getAuthorField().setMaximumSize(getAuthorField().getPreferredSize());
		add(getAuthorField());

		label = new JLabel(" ");
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);

		add(getWebpageLabel());

		getWebpageField().setAlignmentX(Component.LEFT_ALIGNMENT);
		getWebpageField().setMaximumSize(getWebpageField().getPreferredSize());
		add(getWebpageField());

		getWebpageHelpLabel().setAlignmentX(Component.LEFT_ALIGNMENT);
		add(getWebpageHelpLabel());

		JPanel panel = new JPanel();

		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(panel);
		addComponentListener(eventHandler);
	}

	@Override
	public boolean isReady() {
		if (getVersionLabel().isVisible()) {
			String text = getVersionField().getText();

			if (text.length() == 0 || !text.matches("([a-zA-Z0-9.])*[a-zA-Z0-9]+")) {
				return false;
			}
		}
		return getDescriptionArea().getText().length() != 0;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("options");

		frame.setSize(new Dimension(500, 300));
		frame.getContentPane().add(new PackagerOptionsPanel(null));

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});

		try {
			SwingUtilities.invokeAndWait(new PackAndShowFrameWorker(frame));
		} catch (InterruptedException e) {
			// Immediately reasserts the exception by interrupting the caller thread itself
			Thread.currentThread().interrupt();

			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public JCheckBox getIncludeSource() {
		if (includeSource == null) {
			includeSource = new JCheckBox("Include source files", true);
		}
		return includeSource;
	}

	public JCheckBox getIncludeData() {
		if (includeData == null) {
			includeData = new JCheckBox("Include data files", true);
		}
		return includeData;
	}

	private JLabel getAuthorLabel() {
		if (authorLabel == null) {
			authorLabel = new JLabel("Please enter your name. (optional)");
			authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return authorLabel;
	}

	public JTextField getAuthorField() {
		if (authorField == null) {
			authorField = new JTextField(40);
		}
		return authorField;
	}

	public JLabel getDescriptionLabel() {
		if (descriptionLabel == null) {
			descriptionLabel = new JLabel("");
			descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return descriptionLabel;
	}

	public JTextArea getDescriptionArea() {
		if (descriptionArea == null) {
			LimitedDocument doc = new LimitedDocument(3, 72);

			descriptionArea = new JTextArea(doc, null, 3, 72);
			doc.addDocumentListener(eventHandler);
		}
		return descriptionArea;
	}

	private JLabel getVersionLabel() {
		if (versionLabel == null) {
			versionLabel = new JLabel(
					"Please enter a version number for this robot (up to 10 word chars: letters, digits, dots, but no spaces).");
			versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return versionLabel;
	}

	public JTextField getVersionField() {
		if (versionField == null) {
			LimitedDocument doc = new LimitedDocument(1, 10);

			versionField = new JTextField(doc, null, 10);
			doc.addDocumentListener(eventHandler);
		}
		return versionField;
	}

	public JLabel getVersionHelpLabel() {
		if (versionHelpLabel == null) {
			versionHelpLabel = new JLabel("<-- Make sure to delete the asterisk and type in a new version number");
		}
		return versionHelpLabel;
	}

	public JLabel getWebpageLabel() {
		if (webpageLabel == null) {
			webpageLabel = new JLabel("Please enter a URL for your robot's webpage. (optional)");
			webpageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		}
		return webpageLabel;
	}

	public JTextField getWebpageField() {
		if (webpageField == null) {
			webpageField = new JTextField(40);
		}
		return webpageField;
	}

	public JLabel getWebpageHelpLabel() {
		if (webpageHelpLabel == null) {
			webpageHelpLabel = new JLabel("");
		}
		return webpageHelpLabel;
	}

	static class PackAndShowFrameWorker implements Runnable {
		final JFrame frame;

		public PackAndShowFrameWorker(JFrame frame) {
			this.frame = frame;
		}

		public void run() {
			if (frame != null) {
				frame.pack();
				frame.setVisible(true);
			}
		}
	}
}
