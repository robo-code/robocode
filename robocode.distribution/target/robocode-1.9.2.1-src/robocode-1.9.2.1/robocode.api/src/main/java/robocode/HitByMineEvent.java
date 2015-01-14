package robocode;

import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents4;
import robocode.robotinterfaces.IBasicRobot;

/**
 * This event is sent to {@link Ship#onHitByMine(HitByMineEvent)
 * onHitByMine} when you get hit by a mine (Which can be your own).
 *
 * @author Thales B.V. / Thomas Hakkers (original)
 * Figured I'd add you in the credits, since most of it is copied from {@link HitByBulletEvent}
 * @author Mathew A. Nelson (contributor)			
 * @author Flemming N. Larsen (contributor)
 */
public class HitByMineEvent extends Event{
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_PRIORITY = 20;

	private final Mine mine;

	/**
	 * Called by the game to create a new {@code HitByBulletEvent}.
	 *
	 * @param mine  the mine that has hit your ship
	 */
	public HitByMineEvent(Mine mine) {
		super();
		this.mine = mine;
	}

	/**
	 * Returns the mine that hit your ship.
	 *
	 * @return the mine that hit your ship
	 */
	public Mine getMine() {
		return mine;
	}

	/**
	 * Returns the name of the ship that placed the mine.
	 *
	 * @return the name of the ship that placed the mine
	 */
	public String getName() {
		return mine.getName();
	}

	/**
	 * Returns the power of this mine. The damage you take (in fact, already
	 * took) is 3 * power, plus 5 if power == 15. The robot that placed
	 * the mine receives 3 * power back.
	 *
	 * @return the power of the mine
	 */
	public double getPower() {
		return mine.getPower();
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
				listener.onHitByMine(this);
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
		return RbSerializer.HitByMineEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			HitByMineEvent obj = (HitByMineEvent) object;
			return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(RbSerializer.Mine_TYPE, obj.mine);
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			HitByMineEvent obj = (HitByMineEvent) object;

			serializer.serialize(buffer, RbSerializer.Mine_TYPE, obj.mine);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			Mine mine = (Mine) serializer.deserializeAny(buffer);

			return new HitByMineEvent(mine);
		}
	}
}
