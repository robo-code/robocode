package robocode;


import java.nio.ByteBuffer;

import net.sf.robocode.security.IHiddenShipStatusHelper;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.naval.ComponentManager;

/**
 * Contains the status of a robot for a specific time/turn returned by
 * {@link StatusEvent#getStatus()}.
 * THOMA_NOTE: This class is starting to bug me. 
 * @author Thales B.V. / Thomas Hakkers
 *
 * @since 1.9.2.2
 */
public class ShipStatus extends RobotStatus{

	private static final long serialVersionUID = 369382459772925986L;
	private final ComponentManager components;
	
	
	public ShipStatus(double energy, double x, double y, double bodyHeading,
			double velocity, double bodyTurnRemaining, double distanceRemaining,
			int others, int roundNum, int numRounds, long time, ComponentManager components) {
		super(energy,x,y,bodyHeading,0,0,velocity,bodyTurnRemaining,0,0,distanceRemaining,0,others,0,roundNum,numRounds,time);
		this.components = components;
	}
	
	/**
	 * Returns the Components of the Ship.
	 * Might turn this into a copy of the components instead, so there is no way the ShipProxy directly manipulates the components
	 * @return The ComponentManager of the ShipPeer
	 */
	public ComponentManager getComponents(){
		return components;
	}
	
	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper, IHiddenShipStatusHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + 12 * RbSerializer.SIZEOF_DOUBLE + 4 * RbSerializer.SIZEOF_INT
					+ RbSerializer.SIZEOF_LONG;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			ShipStatus obj = (ShipStatus) object;

			serializer.serialize(buffer, obj.getEnergy());
			serializer.serialize(buffer, obj.getX());
			serializer.serialize(buffer, obj.getY());
			serializer.serialize(buffer, obj.getHeading());
			serializer.serialize(buffer, obj.getVelocity());
			serializer.serialize(buffer, obj.getTurnRemaining());
			serializer.serialize(buffer, obj.getDistanceRemaining());
			serializer.serialize(buffer, obj.getOthers());
			serializer.serialize(buffer, obj.getRoundNum());
			serializer.serialize(buffer, obj.getNumRounds());
			serializer.serialize(buffer, obj.getTime());
			serializer.serialize(buffer, RbSerializer.ComponentManager_TYPE, obj.components);
		};

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double energy = buffer.getDouble();
			double x = buffer.getDouble();
			double y = buffer.getDouble();
			double bodyHeading = buffer.getDouble();
			double velocity = buffer.getDouble();
			double bodyTurnRemaining = buffer.getDouble();
			double distanceRemaining = buffer.getDouble();
			int others = buffer.getInt();
			int roundNum = buffer.getInt();
			int numRounds = buffer.getInt();
			long time = buffer.getLong();
			ComponentManager componentManager = (ComponentManager)serializer.deserializeAny(buffer);

			return new ShipStatus(energy, x, y, bodyHeading,velocity, bodyTurnRemaining,
					distanceRemaining, others, roundNum, numRounds,
					time, componentManager);
		}

		public ShipStatus createStatus(double energy, double x, double y, double bodyHeading, double velocity,
				double bodyTurnRemaining, double distanceRemaining, int others,
				int roundNum, int numRounds, long time, ComponentManager components) {
			return new ShipStatus(energy, x, y, bodyHeading, velocity, bodyTurnRemaining,
					distanceRemaining, others, roundNum, numRounds,
					time, components);
		}
	}

}
