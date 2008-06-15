/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen & Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.battle.events;


import static robocode.io.Logger.logError;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.control.BattleSpecification;
import robocode.control.RobotResults;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;


/**
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (original)
 */
public class BattleEventDispatcher implements IBattleListener {
	// This list is guaranteed to be thread-safe, which is necessary as it will be accessed
	// by both the battle thread and battle manager thread. If this list is not thread-safe
	// then ConcurentModificationExceptions will occur from time to time.
	private List<IBattleListener> listeners = new CopyOnWriteArrayList<IBattleListener>();

	public BattleEventDispatcher() {}

	public void addListener(IBattleListener listener) {
		assert (listener != null);

		listeners.add(listener);
	}

	public void removeListener(IBattleListener listener) {
		assert (listener != null);
		listeners.remove(listener);
	}

	public void onBattleStarted(BattleSpecification battleSpecification, boolean isReplay) {
		for (IBattleListener listener : listeners) {
            try{
                listener.onBattleStarted(battleSpecification, isReplay);
            }
            catch (Throwable ex){
                logError("onBattleStarted", ex);
            }
		}
	}

	public void onBattleCompleted(BattleSpecification battleSpecification, RobotResults[] results) {
		for (IBattleListener listener : listeners) {
            try{
                listener.onBattleCompleted(battleSpecification, results);
            }
            catch (Throwable ex){
                logError("onBattleCompleted", ex);
            }
		}
	}

	public void onBattleEnded(boolean isAborted) {
		for (IBattleListener listener : listeners) {
            try{
                listener.onBattleEnded(isAborted);
            }
            catch (Throwable ex){
                logError("onBattleEnded", ex);
            }
		}
	}

	public void onBattlePaused() {
		for (IBattleListener listener : listeners) {
            try{
                listener.onBattlePaused();
            }
            catch (Throwable ex){
                logError("onBattlePaused", ex);
            }
		}
	}

	public void onBattleResumed() {
		for (IBattleListener listener : listeners) {
            try{
                listener.onBattleResumed();
            }
            catch (Throwable ex){
                logError("onBattleResumed", ex);
            }
		}
	}

	public void onRoundStarted(int round) {
		for (IBattleListener listener : listeners) {
            try{
                listener.onRoundStarted(round);
            }
            catch (Throwable ex){
                logError("onRoundStarted", ex);
            }
		}
	}

	public void onRoundEnded() {
		for (IBattleListener listener : listeners) {
            try{
                listener.onRoundEnded();
            }
            catch (Throwable ex){
                logError("onRoundEnded", ex);
            }
		}
	}

	public void onTurnStarted() {
		for (IBattleListener listener : listeners) {
            try{
                listener.onTurnStarted();
            }
            catch (Throwable ex){
                logError("onTurnStarted", ex);
            }
		}
	}

	public void onTurnEnded(TurnSnapshot turnSnapshot) {
        for (IBattleListener listener : listeners) {
            try{
                listener.onTurnEnded(turnSnapshot);
            }
            catch (Throwable ex){
                logError("onTurnEnded", ex);
            }
        }
	}

	public void onBattleMessage(String message) {
		for (IBattleListener listener : listeners) {
            try{
                listener.onBattleMessage(message);
            }
            catch (Throwable ex){
                logError("onBattleMessage", ex);
            }
		}
	}

	public void onBattleError(String error) {
		for (IBattleListener listener : listeners) {
            try{
                listener.onBattleError(error);
            }
            catch (Throwable ex){
                logError("onBattleError", ex);
            }
		}
	}
}
