/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.battle;


/**
 * Used for controlling battle from the e.g. the UI.
 *
 * @author Flemming N. Larsen
 *
 * @since 1.6.1
 */
public interface IBattleControl {

	/**
	 * Stop the battle if it is running.
	 */
	void stop();

	/**
	 * Restart the battle.
	 */
	void restart();

	/**
	 * Start replaying the battle. This is only possible when the battle has stopped.
	 */
	void replay();

	/**
	 * Pauses the battle.
	 */
	void pause();

	/**
	 * Resumes the battle.
	 */
	void resume();

	/**
	 * Pauses the battle, if it has been resumed.
	 */
	void pauseIfResumed();

	/**
	 * Resumes the battle, if it has been paused.
	 */
	void resumeIfPaused();

	/**
	 * If the battle is paused, then resume; otherwise pause the battle. 
	 */
	void togglePauseResume();

	/**
	 * Steps to the next battle turn when the game is paused.
	 * The battle will remain paused after this call has been made.
	 */
	void nextTurn();
}
