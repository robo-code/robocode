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
 *     - Code cleanup
 *     - Added setPaintEnabled() and setSGPaintEnabled() in constructor
 *     - Removed cleanup(), getRobotDialog(), and getRobotPeer() methods, which
 *       did nothing or was not being used
 *     - Updated to use methods from the WindowUtil, which replaces window methods
 *       that have been (re)moved from the robocode.util.Utils class
 *******************************************************************************/
package robocode.dialog;


import robocode.manager.RobocodeManager;
import robocode.battle.events.BattleAdaptor;
import robocode.battle.events.TurnEndedEvent;
import robocode.battle.events.BattleEndedEvent;
import robocode.battle.events.BattleCompletedEvent;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.snapshot.RobotSnapshot;
import robocode.battle.snapshot.ScoreSnapshot;
import robocode.BattleResults;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobotButton extends JButton implements ActionListener {

	private final RobocodeManager manager;
	private BattleObserver battleObserver = new BattleObserver();
	private RobotDialog robotDialog;
	private final String name;
	private final int robotIndex;
	private final int teamIndex;
	private int maxEnergy = 1;
	private int maxScore = 1;
	private int lastEnergy;
	private int lastScore;

	public RobotButton(RobocodeManager manager, String name, int maxEnergy, int robotIndex, int teamIndex, boolean attach) {
		this.manager = manager;
		this.name = name;
		this.robotIndex = robotIndex;
		this.teamIndex = teamIndex;
		lastEnergy = maxEnergy;
		this.maxEnergy = maxEnergy; 

		initialize();
		if (attach) {
			attach();
			robotDialog.reset();
			manager.getBattleManager().setPaintEnabled(robotIndex, robotDialog.isPaintEnabled());
			manager.getBattleManager().setSGPaintEnabled(robotIndex, robotDialog.isSGPaintEnabled());
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (robotDialog == null) {
			attach();
			if (!robotDialog.isVisible() || robotDialog.getState() != Frame.NORMAL) {
				WindowUtil.packPlaceShow(robotDialog);
			}
		} else {
			robotDialog.setVisible(true);
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (lastEnergy != 0) {
			int fraction = (lastEnergy * 100) / maxEnergy;

			if (fraction > 50) {
				g.setColor(Color.GREEN);
			} else if (fraction > 25) {
				g.setColor(Color.YELLOW);
			} else {
				g.setColor(Color.RED);
			}

			final int widthLife = ((getWidth() - 5) * lastEnergy) / maxEnergy;

			g.fillRect(2, getHeight() - 8, widthLife + 1, 4);
		}
		if (lastScore != 0) {
			g.setColor(Color.BLUE);
			final int whidthScore = ((getWidth() - 5) * lastScore) / maxScore;

			g.fillRect(2, getHeight() - 4, whidthScore + 1, 2);
		}
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		addActionListener(this);
		setPreferredSize(new Dimension(110, 25));
		setMinimumSize(new Dimension(110, 25));
		setMaximumSize(new Dimension(110, 25));
		setHorizontalAlignment(SwingConstants.LEFT);
		setMargin(new Insets(0, 0, 0, 0));
		setToolTipText(name);
	}

	public void attach() {
		if (robotDialog == null) {
			robotDialog = manager.getRobotDialogManager().getRobotDialog(this, name, true);
		}
		manager.getWindowManager().addBattleListener(battleObserver);
		robotDialog.attach();
	}

	public void detach() {
		robotDialog = null;
		manager.getWindowManager().removeBattleListener(battleObserver);
	}

	public int getRobotIndex() {
		return robotIndex;
	}

	public String getRobotName() {
		return name;
	}

	private class BattleObserver extends BattleAdaptor {

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			final TurnSnapshot turn = event.getTurnSnapshot();

			if (turn == null) {
				return;
			}
			final int newEnergy = (int) turn.getRobots().get(robotIndex).getEnergy();
			final java.util.List<ScoreSnapshot> scoreSnapshotList = event.getTurnSnapshot().getTeamScores();

			maxScore = 0;
			for (ScoreSnapshot team : scoreSnapshotList) {
				maxScore += team.getTotalScore();
			}
			if (maxScore == 0) {
				maxScore = 1;
			}
			final int newScore = (int) scoreSnapshotList.get(teamIndex).getTotalScore();
			boolean rep = (lastEnergy != newEnergy || lastScore != newScore);

			lastEnergy = newEnergy;
			lastScore = newScore;
			if (rep) {
				repaint();
			}
		}

		public void onBattleCompleted(final BattleCompletedEvent event) {
			maxScore = 0;
			for (BattleResults team : event.getResults()) {
				maxScore += team.getScore();
			}
			if (maxScore == 0) {
				maxScore = 1;
			}
			lastScore = event.getResults()[teamIndex].getScore();
			repaint();
		}
		
		public void onBattleEnded(final BattleEndedEvent event) {
			lastEnergy = 0;
			repaint();
		}
	}
}
