/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


import net.sf.robocode.security.IHiddenRulesHelper;


/**
 * Contains the battle rules returned by {@link robocode.control.events.BattleStartedEvent#getBattleRules()
 * BattleStartedEvent.getBattleRules()} when a battle is started and
 * {@link robocode.control.events.BattleCompletedEvent#getBattleRules() BattleCompletedEvent.getBattleRules()}
 * when a battle is completed.
 *
 * @see robocode.control.events.BattleStartedEvent BattleStartedEvent
 * @see robocode.control.events.BattleCompletedEvent BattleCompletedEvent
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6.2
 */
public final class BattleRules implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private final int battlefieldWidth;
	private final int battlefieldHeight;
	private final int numRounds;
	private final double gunCoolingRate;
	private final long inactivityTime;
	private final boolean hideEnemyNames;
	private final int sentryBorderSize;

	/**
	 * Returns the battlefield width.
	 *
	 * @return the battlefield width.
	 */
	public int getBattlefieldWidth() {
		return battlefieldWidth;
	}

	/**
	 * Returns the battlefield height.
	 *
	 * @return the battlefield height.
	 */
	public int getBattlefieldHeight() {
		return battlefieldHeight;
	}

	/**
	 * Returns the number of rounds.
	 *
	 * @return the number of rounds.
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Returns the rate at which the gun will cool down, i.e. the amount of heat the gun heat will drop per turn.
	 * <p>
	 * The gun cooling rate is default 0.1 per turn, but can be changed by the battle setup.
	 * So don't count on the cooling rate being 0.1!
	 *
	 * @return the gun cooling rate.
	 * @see Robot#getGunHeat()
	 * @see Robot#fire(double)
	 * @see Robot#fireBullet(double)
	 */
	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	/**
	 * Returns the allowed inactivity time, where the robot is not taking any action, before will begin to be zapped.
	 * The inactivity time is measured in turns, and is the allowed time that a robot is allowed to omit taking
	 * action before being punished by the game by zapping.
	 * <p>
	 * When a robot is zapped by the game, it will loose 0.1 energy points per turn. Eventually the robot will be
	 * killed by zapping until the robot takes action. When the robot takes action, the inactivity time counter is
	 * reset. 
	 * <p>
	 * The allowed inactivity time is per default 450 turns, but can be changed by the battle setup.
	 * So don't count on the inactivity time being 450 turns!
	 *
	 * @return the allowed inactivity time.
	 * @see Robot#doNothing()
	 * @see AdvancedRobot#execute()
	 */
	public long getInactivityTime() {
		return inactivityTime;
	}

	/**
	 * Returns true if the enemy names are hidden, i.e. anonymous; false otherwise.
	 * 
	 * @Since 1.7.3
	 */
	public boolean getHideEnemyNames() {
		return hideEnemyNames;
	}

	/**
	 * Returns the sentry border size for a {@link robocode.BorderSentry BorderSentry} that defines the how
	 * far a BorderSentry is allowed to move from the border edges measured in units.<br>
	 * Hence, the sentry border size defines the width/range of the border area surrounding the battlefield that
	 * BorderSentrys cannot leave (sentry robots robots must stay in the border area), but it also define the
	 * distance from the border edges where BorderSentrys are allowed/able to make damage to robots entering this
	 * border area.
	 * 
	 * @return the border size in units/pixels.
	 * 
	 * @since 1.9.0.0
	 */
	public int getSentryBorderSize() {
		return sentryBorderSize;
	}
	
	private BattleRules(int battlefieldWidth, int battlefieldHeight, int numRounds, double gunCoolingRate,
			long inactivityTime, boolean hideEnemyNames, int sentryBorderSize) {
		this.battlefieldWidth = battlefieldWidth;
		this.battlefieldHeight = battlefieldHeight;
		this.numRounds = numRounds;
		this.gunCoolingRate = gunCoolingRate;
		this.inactivityTime = inactivityTime;
		this.hideEnemyNames = hideEnemyNames;
		this.sentryBorderSize = sentryBorderSize;
	}

	static IHiddenRulesHelper createHiddenHelper() {
		return new HiddenHelper();
	}

	private static class HiddenHelper implements IHiddenRulesHelper {

		public BattleRules createRules(int battlefieldWidth, int battlefieldHeight, int numRounds, double gunCoolingRate, long inactivityTime, boolean hideEnemyNames, int sentryBorderSize) {
			return new BattleRules(battlefieldWidth, battlefieldHeight, numRounds, gunCoolingRate, inactivityTime,
					hideEnemyNames, sentryBorderSize);
		}
	}
}
