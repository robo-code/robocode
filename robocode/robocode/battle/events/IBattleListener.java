package robocode.battle.events;


import robocode.battle.snapshot.TurnSnapshot;
import robocode.control.RobotResults;
import robocode.control.BattleSpecification;


public interface IBattleListener {

	public void onBattleStarted(BattleSpecification battleSpecification);

	public void onBattleEnded(boolean isAborted);

	public void onBattleCompleted(BattleSpecification battleSpecification, RobotResults[] results);

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
     * @param message the message logged by the game
     * @see robocode.control.RobocodeEngine#runBattle(BattleSpecification)
     */
    void onBattleMessage(String message);

    /**
     * This method is called when the game logs messages that is normally
     * written out to the err console.
     *
     * @param error the message logged by the game
     * @see robocode.control.RobocodeEngine#runBattle(BattleSpecification)
     */
    void onBattleError(String error);
}
