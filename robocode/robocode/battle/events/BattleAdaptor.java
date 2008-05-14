package robocode.battle.events;


import robocode.battle.snapshot.BattleSnapshot;


public class BattleAdaptor implements IBattleListener {

	public void onBattleStarted() {}

	public void onBattleEnded(boolean isAborted) {}

	public void onBattlePaused() {}

	public void onBattleResumed() {}

	public void onRoundStarted() {}

	public void onRoundEnded() {}

	public void onTurnEnded(BattleSnapshot battleSnapshot) {}
}
