package robocode;

import java.awt.Graphics2D;
import java.nio.ByteBuffer;

import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.robotinterfaces.IBasicEvents;
import robocode.robotinterfaces.IBasicEvents4;
import robocode.robotinterfaces.IBasicRobot;

/**
 * A ScannedProjectileEvent is sent to
 * {@link Robot#onScannedProjectile(ScannedProjectileEvent)
 * onScannedProjectile(ScannedProjectileEvent)} when you
 * scan/found an incoming projectile. You can use the information
 * contained in this event to determine how to react.
 * 
 * @author Thales B.V. / Jiri Waning
 * @version 0.1
 * @since 1.8.3.0 Alpha 1
 * 
 * This function was deprecated since it destroys the fun of
 * calculating where the enemy bullets might be.
 */
@Deprecated
public class ScannedProjectileEvent extends Event {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_PRIORITY = 30; // 0 = low, 99 = high, default = 80, hitByBullet = 20

	/** The energy with which the projectile has been fired. **/
	private final double energy;
	
	/** The velocity of the projectile. **/
	private final double velocity;
	
	/** The distance from the projectile. **/
	private final double distance;
	
	/** The bearing from the front canon to the projectile. **/
	private final double bearingFront;
	
	/** The bearing from the back canon to the projectile. **/
	private final double bearingBack;
	
	
	/**
	 * Creates a new scanned projectile event.
	 * @param energy The energy at which the projectile has been fired.
	 * @param velocity The velocity at which the projectile is traveling.
	 * @param distance The distance between the vehicle and the projectile.
	 * @param bearingFront The bearing from the front canon to the projectile.
	 * @param bearingBack The bearing from the back canon to the projectile.
	 * 
	 */
	public ScannedProjectileEvent(double energy, double velocity, double distance, double bearingFront, double bearingBack ) {
		super();
		this.energy = energy;
		this.velocity = velocity;
		this.distance = distance;
		this.bearingFront = bearingFront;
		this.bearingBack = bearingBack;
	}
	
	/**
	 * Get the amount of energy the projectile has been fired with.
	 * @return The amount of energy the projectile has been fired with.
	 */
	public double getEnergy() {
		return energy;
	}
	
	/**
	 * Get the velocity at which the projectile is traveling.
	 * @return The velocity at which the projectile is traveling.
	 */
	public double getVelocity() {
		return velocity;
	}
	
	/**
	 * Get the distance between the projectile and the vehicle.
	 * @return The distance between the projectile and the vehicle.
	 */
	public double getDistance() {
		return distance;
	}
	
	/**
	 * Get the bearing from the front canon to the projectile.
	 * @return The bearing from the front canon to the projectile.
	 */
	public double getBearingFrontCanon() {
		return Math.toDegrees(bearingFront);
	}
	
	/**
	 * Get the bearing from the back canon to the projectile.
	 * @return The bearing from the back canon to the projectile.
	 */
	public double getBearingBackCanon() {
		return Math.toDegrees(bearingBack);
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
		if (robot != null) {
			IBasicEvents listener = robot.getBasicEventListener();

			if (listener != null && IBasicEvents4.class.isAssignableFrom(listener.getClass())) {
//				((IBasicEvents4) listener).onScannedProjectile(this);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Event event) {
		int ret = super.compareTo(event);
		
		// Do the default event matches.
		if (ret != 0) {
			return ret;
		}
		
		// Do this event specific matches...
		if (event instanceof ScannedProjectileEvent) {
			ScannedProjectileEvent pEvent = (ScannedProjectileEvent) event;
			return ((pEvent.energy == energy) &&
					(pEvent.velocity == velocity) &&
					(pEvent.distance == distance) &&
					(pEvent.bearingFront == bearingFront) &&
					(pEvent.bearingBack == bearingBack)) ?
							/* no difference */ 0 : /* they differ */ 1;
		}

		return 0;
	}
	
	
	
	
	
	// ======================================================================
	//  Serialization
	// ======================================================================
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	byte getSerializationType() {
		return RbSerializer.ScannedProjectileEvent_TYPE;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		/**
		 * Get the size of the event when it has been serialized.
		 * <p/>
		 * <i>This size is used to allocate memory to store the event into.</i>
		 */
		public int sizeOf(RbSerializer serializer, Object object) {
			int retVal = RbSerializer.SIZEOF_TYPEINFO; // The byte that distinguishes this event from others.
			retVal += (5 * RbSerializer.SIZEOF_DOUBLE); // The fields of the event.
			return retVal;
		}

		/**
		 * Serialize the event into the buffer.
		 */
		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			ScannedProjectileEvent event = (ScannedProjectileEvent) object;
			
			serializer.serialize(buffer, event.energy);
			serializer.serialize(buffer, event.velocity);
			serializer.serialize(buffer, event.distance);
			serializer.serialize(buffer, event.bearingFront);
			serializer.serialize(buffer, event.bearingBack);
		}

		/**
		 * De-serialize the event from the buffer.
		 */
		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double energy = serializer.deserializeDouble(buffer);
			double velocity = serializer.deserializeDouble(buffer);
			double distance = serializer.deserializeDouble(buffer);
			double bearingFront = serializer.deserializeDouble(buffer);
			double bearingBack = serializer.deserializeDouble(buffer);
			return new ScannedProjectileEvent(energy, velocity, distance, bearingFront, bearingBack);
		}
	}
}
