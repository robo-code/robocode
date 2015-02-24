package robocode;

import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents4;
import robocode.robotinterfaces.IBasicRobot;

/**
 * This event is sent to {@link Ship#onMineHit(MineHitEvent)
 * onMineHit} when one of your mines has hit a Ship.
 *
 * @author Thales B.V. / Thomas Hakkers (original)
 * Figured I'd add you in the credits, since most of it is copied from {@link BulletHitEvent }
 * @author Mathew A. Nelson (contributor)			
 * @author Flemming N. Larsen (contributor)
 */
public class MineHitEvent extends Event{
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 50;

	private final String name;
	private final double energy;
	private Mine mine;

	/**
	 * Called by the game to create a new {@code MineHitEvent}.
	 *
	 * @param name   the name of the ship your mine hit
	 * @param energy the remaining energy of the robot that your bullet has hit
	 * @param mine the mine that hit the robot
	 */
	public MineHitEvent(String name, double energy, Mine mine) {
		super();
		this.name = name;
		this.energy = energy;
		this.mine = mine;
	}

	/**
	 * Returns the mine of yours that hit the ship.
	 *
	 * @return the mine that hit the ship
	 */
	public Mine getMine() {
		return mine;
	}

	/**
	 * Returns the remaining energy of the ship your mine has hit (after the
	 * damage done by your mine).
	 *
	 * @return energy the remaining energy of the ship that your mine has hit
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * Returns the name of the ship your mine hit.
	 *
	 * @return the name of the ship your mine hit.
	 */
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final int getDefaultPriority() {
		return DEFAULT_PRIORITY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics) {
		if(robot instanceof Ship){
			IBasicEvents4 listener = (IBasicEvents4) robot.getBasicEventListener();
	
			if (listener != null) {
				listener.onMineHit(this);
			}
		}
		else{
			System.err.println("ERROR: MineHitMine.dispatch uses robot instead of Ship");
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.MineHitEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			MineHitEvent obj = (MineHitEvent) object;

			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.name)
					+ RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			MineHitEvent obj = (MineHitEvent) object;

			serializer.serialize(buffer, obj.mine.getMineId());
			serializer.serialize(buffer, obj.name);
			serializer.serialize(buffer, obj.energy);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			Mine mine = new Mine(0, 0, 0, null, null, false, buffer.getInt());
			String name = serializer.deserializeString(buffer);
			double energy = buffer.getDouble();

			return new MineHitEvent(name, energy, mine);
		}
	}
}
