/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.peer;


import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;

import java.io.Serializable;
import java.nio.ByteBuffer;


/**
 * @author Pavel Savara (original)
 */
public class BulletStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	public BulletStatus(int bulletId, double x, double y, String victimName, boolean isActive) {
		this.bulletId = bulletId;
		this.x = x;
		this.y = y;
		this.isActive = isActive;
		this.victimName = victimName;
	}

	public final int bulletId;
	public final String victimName;
	public final boolean isActive;
	public final double x;
	public final double y;

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			BulletStatus obj = (BulletStatus) object;

			return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.victimName)
					+ RbSerializer.SIZEOF_BOOL + 2 * RbSerializer.SIZEOF_DOUBLE;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			BulletStatus obj = (BulletStatus) object;

			serializer.serialize(buffer, obj.bulletId);
			serializer.serialize(buffer, obj.victimName);
			serializer.serialize(buffer, obj.isActive);
			serializer.serialize(buffer, obj.x);
			serializer.serialize(buffer, obj.y);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			int bulletId = buffer.getInt();
			String victimName = serializer.deserializeString(buffer);
			boolean isActive = serializer.deserializeBoolean(buffer);
			double x = buffer.getDouble();
			double y = buffer.getDouble();

			return new BulletStatus(bulletId, x, y, victimName, isActive);
		}
	}

}
