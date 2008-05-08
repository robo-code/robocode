package robocode.battle.events;


public abstract class BattleAdaptor implements IBattleListener {

	/**
	 * {@inheritDoc}
	 */
	public void onBattleStarted(BattleStartedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onBattleEnded(BattleEndedEvent event) {}
	
	/**
	 * {@inheritDoc}
	 */
	public void onBattlePaused(BattlePausedEvent event) {}
	
	/**
	 * {@inheritDoc}
	 */
	public void onBattleResumed(BattleResumedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onRoundStarted(RoundStartedEvent event) {}
	
	/**
	 * {@inheritDoc}
	 */
	public void onRoundEnded(RoundEndedEvent event) {}

	/**
	 * {@inheritDoc}
	 */
	public void onTurnStarted(TurnStartedEvent event) {}
	
	/**
	 * {@inheritDoc}
	 */
	public void onTurnEnded(TurnEndedEvent event) {}
}
