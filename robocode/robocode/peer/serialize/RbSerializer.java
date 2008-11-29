/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.peer.serialize;


import robocode.peer.ExecCommands;
import robocode.peer.BulletCommand;
import robocode.peer.DebugProperty;
import robocode.peer.robot.TeamMessage;
import robocode.io.Logger;
import robocode.manager.VersionManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


/**
 * @author Pavel Savara (original)
 */
public class RbSerializer {
	public static int SIZEOF_TYPEINFO = 1;
	public static int SIZEOF_BYTE = 1;
	public static int SIZEOF_BOOL = 1;
	public static int SIZEOF_INT = 4;
	public static int SIZEOF_DOUBLE = 8;

	public static byte LIST_TERMINATOR_TYPE = -128;
	public static byte ExecCommands_TYPE = 1;
	public static byte BulletCommand_TYPE = 2;
	public static byte TeamMessage_TYPE = 3;
	public static byte DebugProperty_TYPE = 4;

	private static final Charset charset;
	private final CharsetEncoder encoder;
	private final CharsetDecoder decoder;
	private static ISerializableHelper[] typeToHelper = new ISerializableHelper[256];

	private int currentVersion;

	static {
		charset = Charset.forName("UTF8"); // we will use it as UCS-2
		register(null, LIST_TERMINATOR_TYPE); // reserved for end of (list) element
		register(ExecCommands.class, ExecCommands_TYPE);
		register(BulletCommand.class, BulletCommand_TYPE);
		register(TeamMessage.class, TeamMessage_TYPE);
		register(DebugProperty.class, DebugProperty_TYPE);
	}

	public RbSerializer() {
		this.currentVersion = VersionManager.getVersionInt();
		encoder = charset.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPORT);
		encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		// throw away forst bom
		ByteBuffer buffer = ByteBuffer.allocate(8);

		encoder.encode(CharBuffer.wrap("BOM"), buffer, false);

		decoder = charset.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
	}

	public void serialize(OutputStream target, byte type, Object object) throws IOException {
		int length = sizeOf(type, object);

		// header
		ByteBuffer buffer = ByteBuffer.allocate(SIZEOF_INT + SIZEOF_INT);

		buffer.putInt(currentVersion);
		buffer.putInt(length);
		target.write(buffer.array());

		// body
		buffer = ByteBuffer.allocate(length);
		serialize(buffer, type, object);
		if (buffer.remaining() != 0) {
			throw new IOException("Serialization failed"); 
		}
		target.write(buffer.array());
	}

	public Object deserialize(InputStream source) throws IOException {
		// header
		ByteBuffer buffer = ByteBuffer.allocate(SIZEOF_INT + SIZEOF_INT);

		fillBuffer(source, buffer);
		buffer.flip();
		int version = buffer.getInt();

		if (version != currentVersion) {
			throw new IOException("Version of data is not supported. We support only strong match");
		}
		int length = buffer.getInt();

		// body
		buffer = ByteBuffer.allocate(length);
		fillBuffer(source, buffer);
		buffer.flip();
		final Object res = deserialize(buffer);

		if (buffer.remaining() != 0) {
			throw new IOException("Serialization failed");
		}
		return res;
	}

	public void serialize(ByteBuffer buffer, byte type, Object object) {
		ISerializableHelper helper = getHelper(type);

		buffer.put(type);
		helper.serialize(this, buffer, object);
	}

	public void serialize(ByteBuffer buffer, String data) {
		if (data == null) {
			buffer.putInt(-1);
		} else {
			ByteBuffer slice = encode(data);

			buffer.putInt(slice.limit());
			buffer.put(slice);
		}
	}

	public void serialize(ByteBuffer buffer, byte[] data) {
		if (data == null) {
			buffer.putInt(-1);
		} else {
			buffer.putInt(data.length);
			buffer.put(data);
		}
	}

	public void serialize(ByteBuffer buffer, boolean value) {
		buffer.put((byte) (value ? 1 : 0));
	}

	public Object deserialize(ByteBuffer buffer) {
		final byte type = buffer.get();

		if (type == LIST_TERMINATOR_TYPE) {
			return null;
		}
		return getHelper(type).deserialize(this, buffer);
	}

	public String deserializeString(ByteBuffer buffer) {
		final int bytes = buffer.getInt();

		if (bytes == -1) {
			return null;
		}
		final ByteBuffer slice = buffer.slice();

		slice.limit(bytes);
		final String res;

		try {
			res = decoder.decode(slice).toString();
		} catch (CharacterCodingException e) {
			throw new Error("Bad character", e);
		}
		buffer.position(buffer.position() + bytes);
		return res;
	}

	public byte[] deserializeBytes(ByteBuffer buffer) {
		final int bytes = buffer.getInt();

		if (bytes == -1) {
			return null;
		}
		byte[] res = new byte[bytes];

		buffer.get(res);
		return res;
	}

	public boolean deserializeBoolean(ByteBuffer buffer) {
		return buffer.get() != 0;
	}

	public int sizeOf(String data) {
		return (data == null) ? SIZEOF_INT : SIZEOF_INT + encode(data).limit();
	}

	public int sizeOf(byte[] data) {
		return (data == null) ? SIZEOF_INT : SIZEOF_INT + data.length;
	}

	public int sizeOf(byte type, Object object) {
		return getHelper(type).size(this, object);
	}

	private ISerializableHelper getHelper(byte type) {
		return typeToHelper[type];
	}

	private ByteBuffer encode(String data) {
		final ByteBuffer slice = ByteBuffer.allocate(data.length() * 3);

		encoder.encode(CharBuffer.wrap(data), slice, false);
		slice.flip();
		return slice;
	}

	private void fillBuffer(InputStream source, ByteBuffer buffer) throws IOException {
		int res;

		do {
			res = source.read(buffer.array(), buffer.position(), buffer.remaining());
			if (res == -1) {
				throw new IOException("Unexpected EOF");
			}
			buffer.position(buffer.position() + res);
		} while (buffer.remaining() != 0);
	}

	private static void register(Class<?> realClass, byte type) {
		try {
			if (realClass != null) {
				Method method = realClass.getDeclaredMethod("createHiddenHelper");

				method.setAccessible(true);
				ISerializableHelper helper = (ISerializableHelper) method.invoke(null);

				method.setAccessible(false);
				typeToHelper[type] = helper;
			}
		} catch (NoSuchMethodException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		}
	}
}
