package robocode.battle.events;


import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.BattleProperties;


public interface IBattleListener {

	public void onBattleStarted(BattleProperties properties);

	public void onBattleEnded(boolean isAborted);

	public void onBattlePaused();

	public void onBattleResumed();

	public void onRoundStarted(int round);

	public void onRoundEnded();

    public void onTurnStarted();
    
    public void onTurnEnded(TurnSnapshot turnSnapshot);
}
