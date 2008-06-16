package robocode.battle.events;


import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.BattleProperties;
import robocode.control.BattleSpecification;
import robocode.BattleResults;


public interface IBattleListener {

	public void onBattleStarted(TurnSnapshot start, BattleProperties battleProperties, boolean isReplay);

	public void onBattleEnded(boolean isAborted);

	public void onBattleCompleted(BattleProperties battleProperties, BattleResults[] results);

	public void onBattlePaused();

	public void onBattleResumed();

	public void onRoundStarted(int round);

	public void onRoundEnded();

	public void onTurnStarted();
    
	public void onTurnEnded(TurnSnapshot turnSnapshot);

	/**
	 * This method is called when the game logs messages that is normally
	 * written out to the console.
	 *
	 * Note: may be called from multiple threads
	 *
	 * @param message the message logged by the game
	 * @see robocode.control.RobocodeEngine#runBattle(BattleSpecification)
	 */
	void onBattleMessage(String message);

	/**
	 * This method is called when the game logs messages that is normally
	 * written out to the err console.
	 *
	 * Note: may be called from multiple threads
	 *
	 * @param error the message logged by the game
	 * @see robocode.control.RobocodeEngine#runBattle(BattleSpecification)
	 */
	void onBattleError(String error);
}
