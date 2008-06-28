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
 *     Flemming N. Larsen
 *     - Rewritten to reuse RankingDialog, which contains buttons etc.
 *******************************************************************************/
package robocode.dialog;


import robocode.BattleResults;
import robocode.manager.RobocodeManager;
import robocode.ui.BattleResultsTableModel;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * Dialog to display results (scores) of a battle.
 * <p/>
 * This class is just a wrapper class used for storing the window position and
 * dimension.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class ResultsDialog extends BaseScoreDialog {

	private JPanel buttonPanel;
	private JButton okButton;
	private JButton saveButton;
	private BattleResultsTableModel tableModel;
	private ButtonEventHandler buttonEventHandler;

	/**
	 * RankingDialog constructor
	 */
	public ResultsDialog(RobocodeManager manager, BattleResults[] results, int numRounds) {
		super(manager, true);
		tableModel = new BattleResultsTableModel(results, numRounds);
		buttonEventHandler = new ButtonEventHandler();
		initialize();
		setTitle(((BattleResultsTableModel) getTableModel()).getTitle());
		addCancelByEscapeKey();
	}

	private void saveButtonActionPerformed() {
		manager.getWindowManager().showSaveResultsDialog(tableModel);
	}

	private void okButtonActionPerformed() {
		setVisible(false);
	}

	@Override
	protected AbstractTableModel getTableModel() {
		return tableModel;
	}

	/**
	 * Return the content pane.
	 *
	 * @return JPanel
	 */
	@Override
	protected JPanel getDialogContentPane() {
		if (contentPane == null) {
			contentPane = new JPanel();
			contentPane.setLayout(new BorderLayout());
			contentPane.add(getScrollPane(), "Center");
			contentPane.add(getButtonPanel(), "South");
		}
		return contentPane;
	}

	/**
	 * Return the buttonPanel.
	 *
	 * @return JPanel
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new BorderLayout());
			buttonPanel.add(getOkButton(), "East");
			buttonPanel.add(getSaveButton(), "West");
		}
		return buttonPanel;
	}

	/**
	 * Return the okButton
	 *
	 * @return JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText("OK");
			okButton.addActionListener(buttonEventHandler);
			WindowUtil.setFixedSize(okButton, new Dimension(80, 25));
		}
		return okButton;
	}

	/**
	 * Return the saveButton
	 *
	 * @return JButton
	 */
	private JButton getSaveButton() {
		if (saveButton == null) {
			saveButton = new JButton();
			saveButton.setText("Save");
			saveButton.addActionListener(buttonEventHandler);
			WindowUtil.setFixedSize(saveButton, new Dimension(80, 25));
		}
		return saveButton;
	}

	private void addCancelByEscapeKey() {
		String CANCEL_ACTION_KEY = "CANCEL_ACTION_KEY";
		int noModifiers = 0;
		KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, noModifiers, false);
		InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

		inputMap.put(escapeKey, CANCEL_ACTION_KEY);
		AbstractAction cancelAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed();
			}
		};

		getRootPane().getActionMap().put(CANCEL_ACTION_KEY, cancelAction);
	}

	private class ButtonEventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();

			if (source == ResultsDialog.this.getOkButton()) {
				okButtonActionPerformed();
			} else if (source == ResultsDialog.this.getSaveButton()) {
				saveButtonActionPerformed();
			}
		}
	}
}
