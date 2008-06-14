package robocode.battle.events;


import robocode.battle.snapshot.TurnSnapshot;
import robocode.control.RobotResults;
import robocode.control.BattleSpecification;


public class BattleAdaptor implements IBattleListener {

	public void onBattleStarted(BattleSpecification battleSpecification, boolean isReplay) {}

	public void onBattleEnded(boolean isAborted) {}

	public void onBattleCompleted(BattleSpecification battleSpecification, RobotResults[] results) {}

	public void onBattlePaused() {}

	public void onBattleResumed() {}

	public void onRoundStarted(int round) {}

	public void onRoundEnded() {}

	public void onTurnStarted() {}
    
	public void onTurnEnded(TurnSnapshot turnSnapshot) {}

	public void onBattleMessage(String message) {}

	public void onBattleError(String error) {}
}
