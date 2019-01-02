/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import static javax.swing.GroupLayout.Alignment.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Matthew Reeder (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class FindReplaceDialog extends JDialog implements ActionListener {
	private JTextField findTextField;
	private JTextField replaceTextField;
	private JButton findNextButton;
	private JButton replaceFindButton;
	private JButton replaceButton;
	private JButton replaceAllButton;
	private JButton closeButton;
	private JCheckBox caseSensitiveCheckBox;
	private JCheckBox wholeWordCheckBox;
	private JRadioButton regexButton;
	private JRadioButton wildCardsButton;
	private JRadioButton literalButton;
	private JLabel findLabel;
	private JLabel replaceLabel;
	private boolean initLoc;
	private final RobocodeEditor editor;

	public FindReplaceDialog(RobocodeEditor owner) {
		super(owner, false);
		editor = owner;

		GroupLayout layout = new GroupLayout(getContentPane());

		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		JPanel optionsPanel = new JPanel();

		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Options"));
		optionsPanel.add(getCaseSensitiveCheckBox());
		optionsPanel.add(getWholeWordCheckBox());
		optionsPanel.setAlignmentY(TOP_ALIGNMENT);

		JPanel usePanel = new JPanel();

		usePanel.setLayout(new BoxLayout(usePanel, BoxLayout.Y_AXIS));
		usePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Use"));
		usePanel.add(getLiteralButton());
		usePanel.add(getWildCardsButton());
		usePanel.add(getRegexButton());
		usePanel.setAlignmentY(TOP_ALIGNMENT);

		ButtonGroup buttonGroup = new ButtonGroup();

		buttonGroup.add(getLiteralButton());
		buttonGroup.add(getWildCardsButton());
		buttonGroup.add(getRegexButton());

		layout.setHorizontalGroup(
				layout.createSequentialGroup().addGroup(layout.createParallelGroup(LEADING).addGroup(layout.createSequentialGroup().addComponent(getFindLabel()).addComponent(getFindTextField())).addGroup(layout.createSequentialGroup().addComponent(getReplaceLabel()).addComponent(getReplaceTextField())).addGroup(layout.createSequentialGroup().addComponent(optionsPanel).addComponent(usePanel))).addGroup(
						layout.createParallelGroup(LEADING).addComponent(getFindNextButton()).addComponent(getReplaceFindButton()).addComponent(getReplaceButton()).addComponent(getReplaceAllButton()).addComponent(
								getCloseButton())));

		layout.linkSize(SwingConstants.HORIZONTAL, getFindLabel(), getReplaceLabel());
		layout.linkSize(SwingConstants.HORIZONTAL, getFindNextButton(), getReplaceFindButton(), getReplaceButton(),
				getReplaceAllButton(), getCloseButton());

		layout.setVerticalGroup(
				layout.createSequentialGroup().addGroup(layout.createParallelGroup(BASELINE).addComponent(getFindLabel()).addComponent(getFindTextField()).addComponent(getFindNextButton())).addGroup(layout.createParallelGroup(BASELINE).addComponent(getReplaceLabel()).addComponent(getReplaceTextField()).addComponent(getReplaceButton())).addGroup(
						layout.createParallelGroup(BASELINE).addComponent(optionsPanel).addComponent(usePanel).addGroup(
								layout.createSequentialGroup().addComponent(getReplaceFindButton()).addComponent(getReplaceAllButton()).addComponent(
										getCloseButton()))));

		pack();
		setResizable(false);
	}

	public void showDialog(boolean showReplace) {
		getReplaceLabel().setVisible(showReplace);
		getReplaceTextField().setVisible(showReplace);
		getReplaceButton().setVisible(showReplace);
		getReplaceAllButton().setVisible(showReplace);
		if (showReplace) {
			setTitle("Replace");
			getRootPane().setDefaultButton(getReplaceButton());
			getReplaceFindButton().setText("Find...");
			getReplaceFindButton().setMnemonic('d');
			getReplaceFindButton().setDisplayedMnemonicIndex(3);
		} else {
			setTitle("Find");
			getRootPane().setDefaultButton(getFindNextButton());
			getReplaceFindButton().setText("Replace...");
			getReplaceFindButton().setMnemonic('R');
			getReplaceFindButton().setDisplayedMnemonicIndex(0);
		}

		pack();

		if (!initLoc && editor != null) {
			Rectangle bounds = editor.getBounds();
			Dimension size = getSize();

			setLocation((int) (bounds.getX() + (bounds.getWidth() - size.getWidth()) / 2),
					(int) (bounds.getY() + (bounds.getHeight() - size.getHeight()) / 2));
			initLoc = true;
		}

		// Bugfix [2664844] - Editor: Find (set cursor position)
		getFindTextField().requestFocus();

		setVisible(true);
	}

	public JLabel getFindLabel() {
		if (findLabel == null) {
			findLabel = new JLabel();
			findLabel.setText("Find:");
			findLabel.setDisplayedMnemonicIndex(3);
		}
		return findLabel;
	}

	public JLabel getReplaceLabel() {
		if (replaceLabel == null) {
			replaceLabel = new JLabel();
			replaceLabel.setText("Replace:");
			replaceLabel.setDisplayedMnemonicIndex(3);
		}
		return replaceLabel;
	}

	public JTextField getFindTextField() {
		if (findTextField == null) {
			findTextField = new JTextField();
			findTextField.setFocusAccelerator('n');
			findTextField.addActionListener(this);
		}
		return findTextField;
	}

	public JTextField getReplaceTextField() {
		if (replaceTextField == null) {
			replaceTextField = new JTextField();
			replaceTextField.setFocusAccelerator('p');
			replaceTextField.addActionListener(this);
		}
		return replaceTextField;
	}

	public JButton getFindNextButton() {
		if (findNextButton == null) {
			findNextButton = new JButton();
			findNextButton.setText("Find Next");
			findNextButton.setMnemonic('F');
			findNextButton.setDefaultCapable(true);
			findNextButton.addActionListener(this);
		}
		return findNextButton;
	}

	public JButton getReplaceFindButton() {
		if (replaceFindButton == null) {
			replaceFindButton = new JButton("Replace...");
			replaceFindButton.setMnemonic('R');
			replaceFindButton.addActionListener(this);
		}
		return replaceFindButton;
	}

	public JButton getReplaceButton() {
		if (replaceButton == null) {
			replaceButton = new JButton();
			replaceButton.setText("Replace");
			replaceButton.setMnemonic('R');
			replaceButton.setDefaultCapable(true);
			replaceButton.addActionListener(this);
		}
		return replaceButton;
	}

	public JButton getReplaceAllButton() {
		if (replaceAllButton == null) {
			replaceAllButton = new JButton();
			replaceAllButton.setText("Replace All");
			replaceAllButton.setMnemonic('A');
			replaceAllButton.setDisplayedMnemonicIndex(8);
			replaceAllButton.addActionListener(this);
		}
		return replaceAllButton;
	}

	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton();
			closeButton.setText("Close");
			closeButton.setMnemonic('C');
			closeButton.addActionListener(this);
		}
		return closeButton;
	}

	public JCheckBox getCaseSensitiveCheckBox() {
		if (caseSensitiveCheckBox == null) {
			caseSensitiveCheckBox = new JCheckBox();
			caseSensitiveCheckBox.setText("Case Sensitive");
			caseSensitiveCheckBox.setMnemonic('v');
			caseSensitiveCheckBox.setDisplayedMnemonicIndex(12);
			caseSensitiveCheckBox.addActionListener(this);
		}
		return caseSensitiveCheckBox;
	}

	public JCheckBox getWholeWordCheckBox() {
		if (wholeWordCheckBox == null) {
			wholeWordCheckBox = new JCheckBox();
			wholeWordCheckBox.setText("Whole Words Only");
			wholeWordCheckBox.setMnemonic('W');
			wholeWordCheckBox.addActionListener(this);
		}
		return wholeWordCheckBox;
	}

	public JRadioButton getLiteralButton() {
		if (literalButton == null) {
			literalButton = new JRadioButton();
			literalButton.setText("Literal");
			literalButton.setMnemonic('L');
			literalButton.setSelected(true);
			literalButton.addActionListener(this);
		}
		return literalButton;
	}

	public JRadioButton getWildCardsButton() {
		if (wildCardsButton == null) {
			wildCardsButton = new JRadioButton();
			wildCardsButton.setText("Wild Cards");
			wildCardsButton.setMnemonic('i');
			wildCardsButton.setDisplayedMnemonicIndex(1);
			wildCardsButton.addActionListener(this);
		}
		return wildCardsButton;
	}

	public JRadioButton getRegexButton() {
		if (regexButton == null) {
			regexButton = new JRadioButton();
			regexButton.setText("Regular Expressions");
			regexButton.setMnemonic('x');
			regexButton.setDisplayedMnemonicIndex(9);
			regexButton.addActionListener(this);
		}
		return regexButton;
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == getFindNextButton()) {
			findNext();
		} else if (source == getReplaceButton()) {
			doReplacement();
			findNext();
		} else if (source == getReplaceAllButton()) {
			doReplaceAll();
		} else if (source == getCloseButton()) {
			setVisible(false);
		} else if (source == getReplaceFindButton()) {
			showDialog(!getReplaceButton().isVisible());
		} else if (source instanceof JTextField) {
			getRootPane().getDefaultButton().doClick();
		}
	}

	private Pattern getCurrentPattern() {
		String pattern = getFindTextField().getText();
		int flags = Pattern.DOTALL;

		if (!getRegexButton().isSelected()) {
			StringBuffer newpattern = new StringBuffer();

			// 'quote' the pattern
			for (int i = 0; i < pattern.length(); i++) {
				if ("\\[]^$&|().*+?{}".indexOf(pattern.charAt(i)) >= 0) {
					newpattern.append('\\');
				}
				newpattern.append(pattern.charAt(i));
			}
			pattern = newpattern.toString();

			// make "*" .* and "?" .
			if (getWildCardsButton().isSelected()) {
				pattern = pattern.replaceAll("\\\\\\*", ".+?");
				pattern = pattern.replaceAll("\\\\\\?", ".");
			}
		}
		if (!getCaseSensitiveCheckBox().isSelected()) {
			flags |= Pattern.CASE_INSENSITIVE;
		}
		if (getWholeWordCheckBox().isSelected()) {
			pattern = "\\b" + pattern + "\\b";
		}
		return Pattern.compile(pattern, flags);
	}

	public void findNext() {
		EditWindow currentWindow = editor.getActiveWindow();

		if (currentWindow == null || getFindTextField().getText().length() == 0) {
			// launch error dialog?
			return;
		}
		Pattern p = getCurrentPattern();
		JEditorPane editorPane = currentWindow.getEditorPane();

		// for some reason, getText() trims off \r but the indexes in
		// the editor pane don't.
		String text = editorPane.getText().replaceAll("\\r", "");
		Matcher m = p.matcher(text);
		int index = editorPane.getSelectionEnd();

		if (!(m.find(index) || m.find())) {
			return;
		}
		editorPane.setSelectionStart(m.start());
		editorPane.setSelectionEnd(m.end());
	}

	public void doReplacement() {
		EditWindow currentWindow = editor.getActiveWindow();

		if (currentWindow == null || getFindTextField().getText().length() == 0) {
			// launch error dialog?
			return;
		}
		JEditorPane editorPane = currentWindow.getEditorPane();
		String text = editorPane.getSelectedText();

		if (text == null) {
			// no selection
			return;
		}
		Matcher m = getCurrentPattern().matcher(text);

		if (m.matches()) {
			String replacement = getReplaceTextField().getText();

			if (getRegexButton().isSelected()) {
				replacement = m.replaceFirst(replacement);
			}
			editorPane.replaceSelection(replacement);
		}
	}

	public void doReplaceAll() {
		EditWindow currentWindow = editor.getActiveWindow();

		if (currentWindow == null || getFindTextField().getText().length() == 0) {
			// launch error dialog?
			return;
		}
		JEditorPane editorPane = currentWindow.getEditorPane();
		String text = editorPane.getText();

		String replacement = getReplaceTextField().getText();

		editorPane.setText(getCurrentPattern().matcher(text).replaceAll(replacement));
	}
}
