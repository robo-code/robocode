package robocode.battle.events;


import robocode.battle.snapshot.BattleSnapshot;


public class TurnEndedEvent extends BattleEvent {

	private final BattleSnapshot battleSnapshot;
	
	public TurnEndedEvent(BattleSnapshot battleSnapshot) {
		super();

		this.battleSnapshot = battleSnapshot;
	}

	public BattleSnapshot getBattleSnapshot() {
		return battleSnapshot;
	}
}
