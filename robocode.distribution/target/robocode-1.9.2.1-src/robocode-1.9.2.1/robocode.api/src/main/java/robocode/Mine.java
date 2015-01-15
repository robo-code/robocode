package robocode;

import java.io.Serializable;
import java.nio.ByteBuffer;

import net.sf.robocode.security.IHiddenMineHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

/**
 * Represents a Mine that is returned when placing one with {@link Ship#placeMine(double)}
 * and all Mine related events
 *
 * @author Thales B.V. / Thomas Hakkers (original)
 * @author Mathew A. Nelson (contributor)
 * @author Flemming N. Larsen (contributor)
 * (Added you guys because a lot from this class is copied from Bullet)
 */

public class Mine implements Serializable{

	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private final double power;
	private final String ownerName;
	private String victimName;
	private boolean isActive;
	private final int mineId;

	/**
	 * Called by the game to create a new {@code Mine} object
	 *
	 * @param x		 the X position of the mine.
	 * @param y		 the Y position of the mine.
	 * @param power	 the power of the mine.
	 * @param ownerName the name of the owner ship that owns the mine.
	 * @param victimName the name of the ship hit by the mine.
	 * @param isActive {@code true} if the mine is not exploding; {@code false} otherwise.
	 * @param mineId unique id of mine for owner robot.
	 */
	public Mine(double x, double y, double power, String ownerName, String victimName, boolean isActive, int mineId) {
		this.x = x;
		this.y = y;
		this.power = power;
		this.ownerName = ownerName;
		this.victimName = victimName;
		this.isActive = isActive; 
		this.mineId = mineId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return mineId == ((Mine) obj).mineId;
	}

	@Override
	public int hashCode() {
		return mineId;
	}

	/**
	 * Returns the name of the ship that placed this mine.
	 *
	 * @return the name of the ship that placed this mine
	 */
	public String getName() {
		return ownerName;
	}

	/**
	 * Returns the power of this mine.
	 * <p/>
	 * The mine will do (3 * power) damage if it hits another ship.
	 * If power is equal to 15, it will do an additional 5
	 * damage. You will get (3 * power) back if you hit the other robot.
	 *
	 * @return the power of the mine
	 */
	public double getPower() {
		return power;
	}

	/**
	 * Returns the name of the ship that this mine hit, or {@code null} if
	 * the mine has not hit a ship.
	 *
	 * @return the name of the ship that this mine hit, or {@code null} if
	 *         the mine has not hit a ship.
	 */
	public String getVictim() {
		return victimName;
	}

	/**
	 * Returns the X position of the mine.
	 *
	 * @return the X position of the mine
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the Y position of the mine.
	 *
	 * @return the Y position of the mine
	 */
	public double getY() {
		return y;
	}

	/**
	 * Checks if this mine is still active on the battlefield.
	 *
	 * @return {@code true} if the mine is still active on the battlefield;
	 *         {@code false} otherwise
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Updates this mine based on the specified mine status.
	 *
	 * @param victimName the name if the victim that has been hit by this mine.
	 * @param isActive {@code true} if the mine is still active; {@code false} otherwise.
	 */
	// this method is invisible on RobotAPI
	private void update(String victimName, boolean isActive) {
		this.victimName = victimName;
		this.isActive = isActive;
	}

	// this method is invisible on RobotAPI
	/**
	 * @return unique id of mine for owner ship
	 */
	int getMineId() {
		return mineId;
	}
	
	/**
	 * Creates a hidden mine helper for accessing hidden methods on this object.
	 * 
	 * @return a hidden mine helper.
	 */
	// this method is invisible on RobotAPI
	static IHiddenMineHelper createHiddenHelper() {
		return new HiddenMineHelper();
	}

	/**
	 * Creates a hidden mine helper for accessing hidden methods on this object.
	 *
	 * @return a hidden mine helper.
	 */
	// this class is invisible on RobotAPI
	static ISerializableHelper createHiddenSerializer() {
		return new HiddenMineHelper();
	}

	// this class is invisible on RobotAPI
	private static class HiddenMineHelper implements IHiddenMineHelper, ISerializableHelper {

		public void update(Mine mine, String victimName, boolean isActive) {
			mine.update(victimName, isActive);
		}

		public int sizeOf(RbSerializer serializer, Object object) {
			Mine obj = (Mine) object;

			return RbSerializer.SIZEOF_TYPEINFO + 3 * RbSerializer.SIZEOF_DOUBLE + serializer.sizeOf(obj.ownerName)
					+ serializer.sizeOf(obj.victimName) + RbSerializer.SIZEOF_BOOL + RbSerializer.SIZEOF_INT;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			Mine obj = (Mine) object;

			serializer.serialize(buffer, obj.x);
			serializer.serialize(buffer, obj.y);
			serializer.serialize(buffer, obj.power);
			serializer.serialize(buffer, obj.ownerName);
			serializer.serialize(buffer, obj.victimName);
			serializer.serialize(buffer, obj.isActive);
			serializer.serialize(buffer, obj.mineId);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double x = buffer.getDouble();
			double y = buffer.getDouble();
			double power = buffer.getDouble();
			String ownerName = serializer.deserializeString(buffer);
			String victimName = serializer.deserializeString(buffer);
			boolean isActive = serializer.deserializeBoolean(buffer);
			int bulletId = serializer.deserializeInt(buffer);

			return new Mine(x, y, power, ownerName, victimName, isActive, bulletId);
		}
	}

}
