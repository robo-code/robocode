package net.sf.robocode.peer;

import java.nio.ByteBuffer;

import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

/**
 * A class that holds a command to place a mine for a ShipPeer.
 * @author Thales B.V. / Thomas Hakkers
 * @since 1.9.2.2
 */
public class MineCommand {
	
	private final double power;
	private final int mineId;
	
	public MineCommand(double power, int mineId) {
		this.mineId = mineId;
		this.power = power;
	}
	
	public int getMineId() {
		return mineId;
	}

	public double getPower() {
		return power;
	}
	
	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_INT;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			MineCommand obj = (MineCommand) object;

			serializer.serialize(buffer, obj.power);
			serializer.serialize(buffer, obj.mineId);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			double power = buffer.getDouble();
			int bulletId = buffer.getInt();

			return new MineCommand(power, bulletId);
		}
	}

}
