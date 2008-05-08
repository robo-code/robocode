package robocode.battle.events;


public class BattleEvent implements Comparable<BattleEvent> {

	private final long time; // Measured in nanoseconds
	private int priority; // The higher value, the higher priority

	public BattleEvent() {
		time = System.nanoTime();
	}

	public long getTime() {
		return time;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int compareTo(BattleEvent e) {
		int priorityDiff = priority - e.priority;

		if (priorityDiff == 0) {
			return (e.time - time >= 0) ? 1 : -1; // The older time, the higher priority
		}

		return priorityDiff;
	}
}
