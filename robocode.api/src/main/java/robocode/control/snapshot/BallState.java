package robocode.control.snapshot;

public enum BallState {
    /** The ball has just been kicked this turn from player. */
    KICKED(0),

    /** The ball is now moving across the soccerfield. */
    MOVING(1);

    private final int value;

    private BallState(int value) {
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
     * Returns a BallState based on an integer value that represents a BallState.
     *
     * @param value the integer value that represents a specific BallState.
     * @return a BallState that corresponds to the specific integer value.
     *
     * @see #getValue()
     *
     * @throws IllegalArgumentException if the specified value does not correspond
     *                                  to a BulletState and hence is invalid.
     */
    public static BallState toState(int value) {
        switch (value) {
            case 0:
                return KICKED;

            case 1:
                return MOVING;

            default:
                throw new IllegalArgumentException("unknown value");
        }
    }
}
