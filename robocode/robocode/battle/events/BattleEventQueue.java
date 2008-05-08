package robocode.battle.events;


import java.util.concurrent.PriorityBlockingQueue;


// Thread-safe priority queue containing battle events
public class BattleEventQueue extends PriorityBlockingQueue<BattleEvent> {

	private static final long serialVersionUID = 1L;
}
