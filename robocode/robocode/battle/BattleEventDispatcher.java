package robocode.battle;


import java.util.ArrayList;
import java.util.List;


public class BattleEventDispatcher {

	private List<IBattleListener> listeners = new ArrayList<IBattleListener>();


	public void addListener(IBattleListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	public void removeListener(IBattleListener listener) {
		if (listener != null) {
			listeners.remove(listener);
		}
	}

	public void notifyBattleStarted(Battle battle) {
		for (IBattleListener listener : listeners) {
			listener.onBattleStarted(battle);
		}
	}

	public void notifyBattleEnded() {
		for (IBattleListener listener : listeners) {
			listener.onBattleEnded();
		}
	}

	public void notifyRoundStarted() {
		for (IBattleListener listener : listeners) {
			listener.onRoundStarted();
		}
	}

	public void notifyRoundEnded() {
		for (IBattleListener listener : listeners) {
			listener.onRoundEnded();
		}
	}

	public void notifyTurnStarted() {
		for (IBattleListener listener : listeners) {
			listener.onTurnStarted();
		}
	}
	
	public void notifyTurnEnded() {
		for (IBattleListener listener : listeners) {
			listener.onTurnEnded();
		}
	}
}
