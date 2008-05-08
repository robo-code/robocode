package robocode.battle.events;


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
		for (BattleObserverEventDispatcher observerEventDispather : eventDispatcherMap.values()) {
			observerEventDispather.notify(new BattleStartedEvent());
		}
	}

	public void notifyBattleEnded(boolean isAborted) {
		for (BattleObserverEventDispatcher observerEventDispather : eventDispatcherMap.values()) {
			observerEventDispather.notify(new BattleEndedEvent(isAborted));
		}
	}

	public void notifyBattlePaused() {
		for (BattleObserverEventDispatcher observerEventDispather : eventDispatcherMap.values()) {
			observerEventDispather.notify(new BattlePausedEvent());
		}
	}

	public void notifyBattleResumed() {
		for (BattleObserverEventDispatcher observerEventDispather : eventDispatcherMap.values()) {
			observerEventDispather.notify(new BattleResumedEvent());
		}
	}

	public void notifyRoundStarted() {
		for (BattleObserverEventDispatcher observerEventDispather : eventDispatcherMap.values()) {
			observerEventDispather.notify(new RoundStartedEvent());
		}
	}

	public void notifyRoundEnded() {
		for (BattleObserverEventDispatcher observerEventDispather : eventDispatcherMap.values()) {
			observerEventDispather.notify(new RoundEndedEvent());
		}
	}

	public void notifyTurnStarted() {
		for (BattleObserverEventDispatcher observerEventDispather : eventDispatcherMap.values()) {
			observerEventDispather.notify(new TurnStartedEvent());
		}
	}
	
	public void notifyTurnEnded() {
		for (BattleObserverEventDispatcher observerEventDispather : eventDispatcherMap.values()) {
			observerEventDispather.notify(new TurnEndedEvent());
		}
	}
}
