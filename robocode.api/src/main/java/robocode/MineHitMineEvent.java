package robocode;

import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents4;
import robocode.robotinterfaces.IBasicRobot;

/**
 * This event is sent to {@link Ship#onMineHitMine(MineHitMineEvent)
 * onMineHitMine} when one of your mines has hit another mine.
 *
 * @author Thales B.V. / Thomas Hakkers (original)
 * Figured I'd add you in the credits, since most of it is copied from {@link BulletHitBulletEvent} 
 * @author Mathew A. Nelson (contributor)			
 * @author Flemming N. Larsen (contributor)
 */
public class MineHitMineEvent extends Event{

	private static final long serialVersionUID = 1L;

	private final static int DEFAULT_PRIORITY = 55;

	private Mine mine;
	private final Mine hitMine;

	/**
	 * Called by the game to create a new {@code MineHitMineEvent}.
	 *
	 * @param mine	your mine that hit another mine
	 * @param hitMine the mine that was hit by your mine
	 */
	public MineHitMineEvent(Mine mine, Mine hitMine) {
		super();
		this.mine = mine;
		this.hitMine = hitMine;
	}

	/**
	 * Returns your mine that hit another mine.
	 *
	 * @return your mine
	 */
	public Mine getMine() {
		return mine;
	}

	/**
	 * Returns the mine that was hit by your mine.
	 *
	 * @return the mine that was hit
	 */
	public Mine getHitMine() {
		return hitMine;
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
				listener.onMineHitMine(this);
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
		return RbSerializer.MineHitMineEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			MineHitMineEvent obj = (MineHitMineEvent) object;

			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT
					+ serializer.sizeOf(RbSerializer.Mine_TYPE, obj.hitMine);
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			MineHitMineEvent obj = (MineHitMineEvent) object;

			// no need to transmit whole bullet, rest of it is already known to proxy side
			serializer.serialize(buffer, obj.mine.getMineId());
			serializer.serialize(buffer, RbSerializer.Mine_TYPE, obj.hitMine);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			Mine mine = new Mine(0, 0, 0, null, null, false, buffer.getInt());
			Mine hitMine = (Mine) serializer.deserializeAny(buffer);

			return new MineHitMineEvent(mine, hitMine);
		}
	}
}
