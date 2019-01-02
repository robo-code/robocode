/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.io.Logger;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlWriter;
import net.sf.robocode.ui.IWindowManager;
import robocode.control.events.*;
import robocode.control.snapshot.ITurnSnapshot;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringWriter;


/**
 * @author Pavel Savara (original)
 */
public class BattleDialog extends JFrame {
	private static final long serialVersionUID = 1L;

	private final BattleObserver battleObserver = new BattleObserver();

	private ConsoleScrollPane consoleScrollPane;
	private ConsoleScrollPane turnSnapshotScrollPane;
	private JTabbedPane tabbedPane;
	private JButton okButton;
	private JButton clearButton;
	private JPanel battleDialogContentPane;
	private JPanel buttonPanel;

	private final IWindowManager windowManager;
	private boolean isListening;
	private ITurnSnapshot lastSnapshot;
	private boolean paintSnapshot;

	public BattleDialog(IWindowManager windowManager) {
		this.windowManager = windowManager;
		initialize();
	}

	private void initialize() {
		this.setTitle("Main battle log");
		this.add(getBattleDialogContentPane());
		pack();
	}

	public void detach() {
		if (isListening) {
			windowManager.removeBattleListener(battleObserver);
			isListening = false;
		}
	}

	public void reset() {
		getConsoleScrollPane().setText(null);
		getTurnSnapshotScrollPane().setText(null);
		lastSnapshot = null;
	}

	public void attach() {
		if (!isListening) {
			isListening = true;
			windowManager.addBattleListener(battleObserver);
		}
	}

	/**
	 * When robotDialog is packed, we want to set a reasonable size. However,
	 * after that, we need a null preferred size so the scroll pane will scroll.
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

	private final transient ActionListener eventHandler = new ActionListener() {
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
			tabbedPane.addTab("Turn Snapshot", getTurnSnapshotScrollPane());
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
		if (consoleScrollPane == null) {
			consoleScrollPane = new ConsoleScrollPane();
		}
		return consoleScrollPane;
	}

	private ConsoleScrollPane getTurnSnapshotScrollPane() {
		if (turnSnapshotScrollPane == null) {
			turnSnapshotScrollPane = new ConsoleScrollPane();
		}
		return turnSnapshotScrollPane;
	}

	private void okButtonActionPerformed() {
		dispose();
	}

	private void clearButtonActionPerformed() {
		reset();
	}

	private void paintSnapshot() {
		if (paintSnapshot) {
			String text = null;

			if (lastSnapshot != null) {
				final StringWriter writer = new StringWriter();
				final XmlWriter xmlWriter = new XmlWriter(writer, true);

				try {
					((IXmlSerializable) lastSnapshot).writeXml(xmlWriter, new SerializableOptions(false));
					writer.close();
				} catch (IOException e) {
					Logger.logError(e);
				}
				text = writer.toString();
			}
			getTurnSnapshotScrollPane().setText(text);
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

		public void onBattleStarted(final BattleStartedEvent event) {
			reset();
		}

		@Override
		public void onBattleFinished(BattleFinishedEvent event) {
			lastSnapshot = null;
			paintSnapshot();
		}
	}
}
