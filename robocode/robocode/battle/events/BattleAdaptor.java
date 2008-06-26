package robocode.battle.events;


public class BattleAdaptor implements IBattleListener {

	public void onBattleStarted(BattleStartedEvent event) {}

	public void onBattleEnded(BattleEndedEvent event) {}

	public void onBattleCompleted(BattleCompletedEvent event) {}

	public void onBattlePaused(BattlePausedEvent event) {}

	public void onBattleResumed(BattleResumedEvent event) {}

	public void onRoundStarted(RoundStartedEvent event) {}

	public void onRoundEnded(RoundEndedEvent event) {}

	public void onTurnStarted(TurnStartedEvent event) {}

	public void onTurnEnded(TurnEndedEvent event) {}

	public void onBattleMessage(BattleMessageEvent event) {}

	public void onBattleError(BattleErrorEvent event) {}
}
