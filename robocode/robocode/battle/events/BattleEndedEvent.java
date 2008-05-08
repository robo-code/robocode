package robocode.battle.events;


public class BattleEndedEvent extends BattleEvent {

	private final boolean isAborted;
	
	public BattleEndedEvent(boolean isAborted) {
		super();

		this.isAborted = isAborted;
	}

	public boolean isAborted() {
		return isAborted;
	}
}
