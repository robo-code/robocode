package robocode.control.snapshot;


// Bullet states: all states last one turn, except MOVING and DONE
public enum BulletState {
	FIRED(0), MOVING(1), HIT_VICTIM(2), HIT_BULLET(3), HIT_WALL(4), EXPLODED(5), INACTIVE(6);

	private final int value;

	private BulletState(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static BulletState toState(int value) {
		switch (value) {
		case 0:
			return FIRED;

		case 1:
			return MOVING;

		case 2:
			return HIT_VICTIM;

		case 3:
			return HIT_BULLET;

		case 4:
			return HIT_WALL;

		case 5:
			return EXPLODED;

		case 6:
			return INACTIVE;

		default:
			throw new IllegalArgumentException("unknown value");
		}
	}
}
