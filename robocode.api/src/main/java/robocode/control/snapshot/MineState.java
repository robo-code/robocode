package robocode.control.snapshot;


public enum MineState {

	/** The bullet has just been placed this turn and hence just been created. This state only last one turn. */
	PLACED(0),
	
	FLOATING(1),

	HIT_VICTIM(2),

	/** The mine currently represents a ship explosion, i.e. a ship death. */
	EXPLODING(3),

	HIT_MINE(4),
	
	/** The mine is currently inactive. Hence, it is not active or visible on the battlefield. */
	INACTIVE(5);

	private final int value;

	private MineState(int value) {
		this.value = value;
	}

	/**
	 * Returns the state as an integer value.
	 *
	 * @return an integer value representing this state.
	 *
	 * @see #toState(int)
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns a BulletState based on an integer value that represents a BulletState.
	 *
	 * @param value the integer value that represents a specific BulletState.
	 * @return a BulletState that corresponds to the specific integer value.
	 *
	 * @see #getValue()
	 *
	 * @throws IllegalArgumentException if the specified value does not correspond
	 *                                  to a BulletState and hence is invalid.
	 */
	public static MineState toState(int value) {
		switch (value) {
		case 0:
			return PLACED;
		case 1:
			return FLOATING;
			
		case 2:
			return HIT_VICTIM;

		case 3:
			return EXPLODING;

		case 4:
			return HIT_MINE;

		case 5:
			return INACTIVE;

		default:
			throw new IllegalArgumentException("unknown value");
		}
	}
	
	public boolean isActive() {
		return this == PLACED || this == FLOATING || this == HIT_MINE || this == HIT_VICTIM;
	}
}
