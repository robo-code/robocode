package robocode;
/**
 * A prebuilt condition you can use that indicates your ship has finished
 * turning.
 *
 * @see Condition
 * @author Thales B.V. / Thomas Hakkers
 */
public class NavalTurnCompleteCondition extends Condition {
	private Ship ship;

	/**
	 * Creates a new TurnCompleteCondition with default priority.
	 * The default priority is 80.
	 *
	 * @param ship your ship, which must be a {@link Ship}
	 */
	public NavalTurnCompleteCondition(Ship ship) {
		super();
		this.ship = ship;
	}

	/**
	 * Creates a new NavalTurnCompleteCondition with the specified priority.
	 * A condition priority is a value from 0 - 99. The higher value, the
	 * higher priority. The default priority is 80.
	 *
	 * @param ship your ship, which must be a {@link Ship}
	 * @param priority the priority of this condition
	 * @see Condition#setPriority(int)
	 */
	public NavalTurnCompleteCondition(Ship ship, int priority) {
		super();
		this.ship = ship;
		this.priority = priority;
	}

	/**
	 * Tests if the ship has finished turning.
	 *
	 * @return {@code true} if the ship has stopped turning; {@code false}
	 *         otherwise
	 */
	@Override
	public boolean test() {
		return (ship.getBodyTurnRemainingDegrees() == 0);
	}

	/**
	 * Called by the system in order to clean up references to internal objects.
	 *
	 * @since 1.4.3
	 */
	@Override
	public void cleanup() {
		ship = null;
	}
}
