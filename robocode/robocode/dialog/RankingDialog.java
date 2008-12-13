/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Luis Crespo & Mathew A. Nelson
 *     - Original implementation
 *     Flemming N. Larsen
 *     - Totally rewritten to contain the functionality for both the
 *       RankingDialog and ResultsDialog (code reuse)
 *     - Changed to be a independent frame instead of a dialog
 *     - Changed to pack the dialog to fit the table with the rankings
 *     Nathaniel Troutman
 *     - Bugfix: Added cleanup to prevent memory leaks with the battle object in
 *       okButtonActionPerformed()
 *******************************************************************************/
package robocode.dialog;


import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleFinishedEvent;
import robocode.control.events.TurnEndedEvent;
import robocode.control.snapshot.ITurnSnapshot;
import robocode.manager.RobocodeManager;
import robocode.ui.BattleRankingTableModel;

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

	public RankingDialog(RobocodeManager manager) {
		super(manager, false);
		battleObserver = new BattleObserver();
		timerTask = new Timer(1000 / 2, new TimerTask());
		snapshot = new AtomicReference<ITurnSnapshot>();
		lastRows = 0;
		tableModel = new BattleRankingTableModel();
		initialize();
		setTitle("Ranking");
	}

	@Override
	protected AbstractTableModel getTableModel() {
		return tableModel;
	}

	private void update() {
		final ITurnSnapshot current = snapshot.get();

		if (lastSnapshot != current) {
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
		manager.getWindowManager().addBattleListener(battleObserver);
		timerTask.start();
	}

	protected void onDialogHidden() {
		manager.getWindowManager().getRobocodeFrame().getRobocodeMenuBar().getOptionsShowRankingCheckBoxMenuItem().setState(
				false);
		timerTask.stop();
		manager.getWindowManager().removeBattleListener(battleObserver);
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
