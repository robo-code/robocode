package robocode.battle.events;


import robocode.control.BattleSpecification;


public interface IBattleListener {

	public void onBattleStarted(BattleStartedEvent event);

	public void onBattleEnded(BattleEndedEvent event);

	public void onBattleCompleted(BattleCompletedEvent event);

	public void onBattlePaused(BattlePausedEvent event);

	public void onBattleResumed(BattleResumedEvent event);

	public void onRoundStarted(RoundStartedEvent event);

	public void onRoundEnded(RoundEndedEvent event);

	public void onTurnStarted(TurnStartedEvent event);

	public void onTurnEnded(TurnEndedEvent event);

	/**
	 * This method is called when the game logs messages that is normally
	 * written out to the console.
	 * <p/>
	 * Note: may be called from multiple threads
	 *
	 * @param event the message logged by the game
	 * @see robocode.control.RobocodeEngine#runBattle(BattleSpecification)
	 */
	void onBattleMessage(BattleMessageEvent event);

	/**
	 * This method is called when the game logs messages that is normally
	 * written out to the err console.
	 * <p/>
	 * Note: may be called from multiple threads
	 *
	 * @param event the message logged by the game
	 * @see robocode.control.RobocodeEngine#runBattle(BattleSpecification)
	 */
	void onBattleError(BattleErrorEvent event);
}
