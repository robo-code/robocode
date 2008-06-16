package robocode.battle.events;


import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.BattleProperties;
import robocode.control.RobotResults;
import robocode.control.BattleSpecification;


public class BattleAdaptor implements IBattleListener {

	public void onBattleStarted(TurnSnapshot start, BattleProperties battleProperties, boolean isReplay) {}

	public void onBattleEnded(boolean isAborted) {}

	public void onBattleCompleted(BattleProperties battleProperties, RobotResults[] results) {}

	public void onBattlePaused() {}

	public void onBattleResumed() {}

	public void onRoundStarted(int round) {}

	public void onRoundEnded() {}

	public void onTurnStarted() {}
    
	public void onTurnEnded(TurnSnapshot turnSnapshot) {}

	public void onBattleMessage(String message) {}

	public void onBattleError(String error) {}
}
