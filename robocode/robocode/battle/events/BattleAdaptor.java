package robocode.battle.events;


import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.BattleProperties;
import robocode.BattleResults;


public class BattleAdaptor implements IBattleListener {

	public void onBattleStarted(TurnSnapshot start, BattleProperties battleProperties, boolean isReplay) {}

	public void onBattleEnded(boolean isAborted) {}

	public void onBattleCompleted(BattleProperties battleProperties, BattleResults[] results) {}

	public void onBattlePaused() {}

	public void onBattleResumed() {}

	public void onRoundStarted(int round) {}

	public void onRoundEnded() {}

	public void onTurnStarted() {}
    
	public void onTurnEnded(TurnSnapshot turnSnapshot) {}

	public void onBattleMessage(String message) {}

	public void onBattleError(String error) {}
}
