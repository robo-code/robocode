/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.battle.BattleRankingTableModel;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.ui.IWindowManager;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.ITurnSnapshot;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Frame to display the battle results or ranking during battles.
 *
 * @author Mathew A. Nelson (original)
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (contributor)
 * @author Nathaniel Troutman (contributor)
 */
@SuppressWarnings("serial")
public class RankingDialog extends BaseScoreDialog {
	private final BattleRankingTableModel tableModel;
	private final Timer timerTask;
	private final BattleObserver battleObserver;
	private final AtomicReference<ITurnSnapshot> snapshot;
	private ITurnSnapshot lastSnapshot;
	private int lastRows;
	private final MenuBar menu;
	private final IWindowManager windowManager;

	public RankingDialog(IWindowManager windowManager, ISettingsManager settingsManager, MenuBar menu) {
		super(settingsManager.getOptionsCommonDontHideRankings() ? null : windowManager, false);

		initialize();

		this.windowManager = windowManager;
		battleObserver = new BattleObserver();
		timerTask = new Timer(1000 / 2, new TimerTask());
		snapshot = new AtomicReference<ITurnSnapshot>();
		lastRows = 0;
		tableModel = new BattleRankingTableModel();
		this.menu = menu;
		setTitle("Ranking");
	}

	@Override
	protected AbstractTableModel getTableModel() {
		return tableModel;
	}

	private void update() {
		final ITurnSnapshot current = snapshot.get();

		if (lastSnapshot != current) {
			setResultsData();

			lastSnapshot = current;
			tableModel.updateSource(lastSnapshot);
			if (table.getModel().getRowCount() != lastRows) {
				lastRows = table.getModel().getRowCount();

				table.setPreferredSize(
						new Dimension(table.getColumnModel().getTotalColumnWidth(),
						table.getModel().getRowCount() * table.getRowHeight()));
				table.setPreferredScrollableViewportSize(table.getPreferredSize());
				pack();
			}
			repaint();
		}
	}

	protected void onDialogShown() {
		windowManager.addBattleListener(battleObserver);
		timerTask.start();
	}

	protected void onDialogHidden() {
		menu.getOptionsShowRankingCheckBoxMenuItem().setState(false);
		timerTask.stop();
		windowManager.removeBattleListener(battleObserver);
		dispose();
	}

	private class BattleObserver extends BattleAdaptor {
		@Override
		public void onBattleFinished(BattleFinishedEvent event) {
			snapshot.set(null);
		}

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			snapshot.set(event.getTurnSnapshot());
		}
	}


	private class TimerTask implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			update();
		}
	}
}
