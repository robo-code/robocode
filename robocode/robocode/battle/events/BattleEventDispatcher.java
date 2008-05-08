package robocode.battle.events;


import robocode.battle.snapshot.BattleSnapshot;

import java.util.HashMap;


public class BattleEventDispatcher {

	private HashMap<IBattleObserver, BattleObserverEventDispatcher> eventDispatcherMap = new HashMap<IBattleObserver, BattleObserverEventDispatcher>();
	
	public void addObserver(IBattleObserver observer) {
		assert(observer != null);

		eventDispatcherMap.put(observer, new BattleObserverEventDispatcher(observer));
	}

	public void removeObserver(IBattleObserver observer) {
		assert(observer != null);

		eventDispatcherMap.remove(observer);
	}

	public void notifyBattleStarted() {
		for (BattleObserverEventDispatcher observerEventDispatcher : eventDispatcherMap.values()) {
			observerEventDispatcher.notify(new BattleStartedEvent());
		}
	}

	public void notifyBattleEnded(boolean isAborted) {
		for (BattleObserverEventDispatcher observerEventDispatcher : eventDispatcherMap.values()) {
			observerEventDispatcher.notify(new BattleEndedEvent(isAborted));
		}
	}

	public void notifyBattlePaused() {
		for (BattleObserverEventDispatcher observerEventDispatcher : eventDispatcherMap.values()) {
			observerEventDispatcher.notify(new BattlePausedEvent());
		}
	}

	public void notifyBattleResumed() {
		for (BattleObserverEventDispatcher observerEventDispatcher : eventDispatcherMap.values()) {
			observerEventDispatcher.notify(new BattleResumedEvent());
		}
	}

	public void notifyRoundStarted() {
		for (BattleObserverEventDispatcher observerEventDispatcher : eventDispatcherMap.values()) {
			observerEventDispatcher.notify(new RoundStartedEvent());
		}
	}

	public void notifyRoundEnded() {
		for (BattleObserverEventDispatcher observerEventDispatcher : eventDispatcherMap.values()) {
			observerEventDispatcher.notify(new RoundEndedEvent());
		}
	}

	public void notifyTurnStarted() {
		for (BattleObserverEventDispatcher observerEventDispatcher : eventDispatcherMap.values()) {
			observerEventDispatcher.notify(new TurnStartedEvent());
		}
	}
	
	public void notifyTurnEnded(BattleSnapshot battleSnapshot) {
		for (BattleObserverEventDispatcher observerEventDispatcher : eventDispatcherMap.values()) {
			observerEventDispatcher.notify(new TurnEndedEvent(battleSnapshot));
		}
	}
}
