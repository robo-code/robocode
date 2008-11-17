/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.dialog;


import robocode.battle.events.*;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.manager.RobocodeManager;
import robocode.util.XmlWriter;
import robocode.io.Logger;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.StringWriter;
import java.io.IOException;


/**
 * @author Pavel Savara (original)
 */
public class BattleDialog extends JFrame {
	private static final long serialVersionUID = 1L;

	private BattleObserver battleObserver = new BattleObserver();

	private ConsoleScrollPane scrollPane;
	private ConsoleScrollPane xmlPane;
	private JTabbedPane tabbedPane;
	private JButton okButton;
	private JButton clearButton;
	private JPanel battleDialogContentPane;
	private JPanel buttonPanel;

	private RobocodeManager manager;
	private BattleButton battleButton;
	private boolean isListening;
	private TurnSnapshot lastSnapshot;
	private boolean paintSnapshot;

	public BattleDialog(RobocodeManager manager, BattleButton battleButton) {
		this.manager = manager;
		this.battleButton = battleButton;
		initialize();
	}

	private void initialize() {
		this.setTitle("Main battle log");
		this.add(getBattleDialogContentPane());
		pack();
	}

	public void detach() {
		if (isListening) {
			manager.getWindowManager().removeBattleListener(battleObserver);
			isListening = false;
		}
		battleButton.detach();
	}

	public void reset() {
		getConsoleScrollPane().setText(null);
		getTurnScrollPane().setText(null);
		lastSnapshot = null;
	}

	public void attach() {
		if (!isListening) {
			isListening = true;
			manager.getWindowManager().addBattleListener(battleObserver);
		}
	}

	/**
	 * When robotDialog is packed, we want to set a reasonable size. However,
	 * after that, we need a null preferred size so the scrollpane will scroll.
	 * (preferred size should be based on the text inside)
	 */
	@Override
	public void pack() {
		getConsoleScrollPane().setPreferredSize(new Dimension(426, 200));
		super.pack();
		getTabbedPane().setPreferredSize(null);
	}

	private JPanel getBattleDialogContentPane() {
		if (battleDialogContentPane == null) {
			battleDialogContentPane = new JPanel();
			battleDialogContentPane.setLayout(new BorderLayout());
			battleDialogContentPane.add(getTabbedPane());
			battleDialogContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
		}
		return battleDialogContentPane;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
			buttonPanel.add(getOkButton());
			buttonPanel.add(getClearButton());
		}
		return buttonPanel;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			okButton = getNewButton("OK");
		}
		return okButton;
	}

	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = getNewButton("Clear");
		}
		return clearButton;
	}

	private JButton getNewButton(String text) {
		JButton button = new JButton(text);

		button.addActionListener(eventHandler);
		return button;
	}

	private ActionListener eventHandler = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();

			if (src == BattleDialog.this.getOkButton()) {
				okButtonActionPerformed();
			} else if (src == BattleDialog.this.getClearButton()) {
				clearButtonActionPerformed();
			}
		}
	};

	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.setLayout(new BorderLayout());
			tabbedPane.addTab("Console", getConsoleScrollPane());
			tabbedPane.addTab("Turn Snapshot", getTurnScrollPane());
			// tabbedPane.setSelectedIndex(0);
			tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

			tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					paintSnapshot = (tabbedPane.getSelectedIndex() == 1);
					paintSnapshot();
				}
			});
		}
		return tabbedPane;
	}

	private ConsoleScrollPane getConsoleScrollPane() {
		if (scrollPane == null) {
			scrollPane = new ConsoleScrollPane();
			JTextArea textPane = scrollPane.getTextPane();

			textPane.setBackground(Color.DARK_GRAY);
			textPane.setForeground(Color.WHITE);
		}
		return scrollPane;
	}

	private ConsoleScrollPane getTurnScrollPane() {
		if (xmlPane == null) {
			xmlPane = new ConsoleScrollPane();
		}
		return xmlPane;
	}

	private void okButtonActionPerformed() {
		dispose();
	}

	private void clearButtonActionPerformed() {
		reset();
	}

	private void paintSnapshot() {
		if (paintSnapshot) {
			if (lastSnapshot != null) {
				final StringWriter writer = new StringWriter();
				final XmlWriter xmlWriter = new XmlWriter(writer, true);

				try {
					lastSnapshot.writeXml(xmlWriter);
					writer.close();
				} catch (IOException e) {
					Logger.logError(e);
				}
				getTurnScrollPane().setText(writer.toString());
			} else {
				getTurnScrollPane().setText(null);
			}
		}
	}

	private class BattleObserver extends BattleAdaptor {

		@Override
		public void onBattleMessage(BattleMessageEvent event) {
			final String text = event.getMessage();

			if (text != null && text.length() > 0) {
				getConsoleScrollPane().append(text + "\n");
				getConsoleScrollPane().scrollToBottom();
			}
		}

		@Override
		public void onBattleError(BattleErrorEvent event) {
			final String text = event.getError();

			if (text != null && text.length() > 0) {
				getConsoleScrollPane().append(text + "\n");
				getConsoleScrollPane().scrollToBottom();
			}
		}

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			lastSnapshot = event.getTurnSnapshot();
			paintSnapshot();
		}

		@Override
		public void onBattleEnded(BattleEndedEvent event) {
			lastSnapshot = null;
			paintSnapshot();
		}
	}
}
