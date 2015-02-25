package net.sf.robocode.peer;

import java.io.Serializable;
import java.nio.ByteBuffer;

import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

/**
 * A complete copy of BulletStatus, but used by mines instead.
 * @author Thales B.V. / Thomas Hakkers
 *
 */
public class MineStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	public MineStatus(int mineId, double x, double y, String victimName, boolean isActive) {
		this.mineId = mineId;
		this.x = x;
		this.y = y;
		this.isActive = isActive;
		this.victimName = victimName;
	}

	public final int mineId;
	public final String victimName;
	public final boolean isActive;
	public final double x;
	public final double y;

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			MineStatus obj = (MineStatus) object;

			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.victimName)
					+ RbSerializer.SIZEOF_BOOL + 2 * RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			MineStatus obj = (MineStatus) object;

			serializer.serialize(buffer, obj.mineId);
			serializer.serialize(buffer, obj.victimName);
			serializer.serialize(buffer, obj.isActive);
			serializer.serialize(buffer, obj.x);
			serializer.serialize(buffer, obj.y);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			int mineId = buffer.getInt();
			String victimName = serializer.deserializeString(buffer);
			boolean isActive = serializer.deserializeBoolean(buffer);
			double x = buffer.getDouble();
			double y = buffer.getDouble();

			return new MineStatus(mineId, x, y, victimName, isActive);
		}
	}

}
