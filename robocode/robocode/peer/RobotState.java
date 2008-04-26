package robocode.peer;


// Robot States: all states last one turn, except ACTIVE and DEAD
public enum RobotState {
	ACTIVE(0), HIT_WALL(1), HIT_ROBOT(2), DEAD(3);

	private final int value;

	private RobotState(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static RobotState toState(int value) {
		switch (value) {
		case 0:
			return ACTIVE;

		case 1:
			return HIT_WALL;

		case 2:
			return HIT_ROBOT;

		case 3:
			return DEAD;

		default:
			throw new IllegalArgumentException("unknown value");
		}
	}

	public boolean isAlive() {
		return this != DEAD;
	}

	public boolean isDead() {
		return this == DEAD;
	}
}
