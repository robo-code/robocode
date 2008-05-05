package robocode.battle;


public interface IBattleListener {

	public void onBattleStarted(Battle battle);
	
	public void onBattleEnded();
	
	public void onRoundStarted();
	
	public void onRoundEnded();

	public void onTurnStarted();
	
	public void onTurnEnded();
}
