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
public class TeamMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	public TeamMessage(String sender, String recipient, byte[] message) {
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;

	}

	public final String sender;
	public final String recipient;
	public final byte[] message;

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			TeamMessage obj = (TeamMessage) object;
			final int s = serializer.sizeOf(obj.sender);
			final int r = serializer.sizeOf(obj.recipient);
			final int m = serializer.sizeOf(obj.message);

			return RbSerializer.SIZEOF_TYPEINFO + s + r + m;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			TeamMessage obj = (TeamMessage) object;

			serializer.serialize(buffer, obj.sender);
			serializer.serialize(buffer, obj.recipient);
			serializer.serialize(buffer, obj.message);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {
			String sender = serializer.deserializeString(buffer);
			String recipient = serializer.deserializeString(buffer);
			byte[] message = serializer.deserializeBytes(buffer);

			return new TeamMessage(sender, recipient, message);
		}
	}

}
