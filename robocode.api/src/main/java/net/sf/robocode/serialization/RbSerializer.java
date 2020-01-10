/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.serialization;


import net.sf.robocode.core.ContainerBase;
import net.sf.robocode.io.Logger;
import net.sf.robocode.manager.IVersionManagerBase;
import net.sf.robocode.security.HiddenAccess;
import robocode.Event;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Pavel Savara (original)
 */
public final class RbSerializer {
	public final static int SIZEOF_TYPEINFO = 1;
	public final static int SIZEOF_BYTE = 1;
	public final static int SIZEOF_BOOL = 1;
	public final static int SIZEOF_CHAR = 2;
	public final static int SIZEOF_INT = 4;
	public final static int SIZEOF_LONG = 8;
	public final static int SIZEOF_DOUBLE = 8;

	public final static byte TERMINATOR_TYPE = -1;
	public final static byte ExecCommands_TYPE = 1;
	public final static byte BulletCommand_TYPE = 2;
	public final static byte TeamMessage_TYPE = 3;
	public final static byte DebugProperty_TYPE = 4;
	public final static byte ExecResults_TYPE = 5;
	public final static byte RobotStatus_TYPE = 6;
	public final static byte BulletStatus_TYPE = 7;
	public final static byte BattleResults_TYPE = 8;
	public final static byte Bullet_TYPE = 9;
	public final static byte RobotStatics_TYPE = 10;

	public final static byte BattleEndedEvent_TYPE = 32;
	public final static byte BulletHitBulletEvent_TYPE = 33;
	public final static byte BulletHitEvent_TYPE = 34;
	public final static byte BulletMissedEvent_TYPE = 35;
	public final static byte DeathEvent_TYPE = 36;
	public final static byte WinEvent_TYPE = 37;
	public final static byte HitWallEvent_TYPE = 38;
	public final static byte RobotDeathEvent_TYPE = 39;
	public final static byte SkippedTurnEvent_TYPE = 40;
	public final static byte ScannedRobotEvent_TYPE = 41;
	public final static byte HitByBulletEvent_TYPE = 42;
	public final static byte HitRobotEvent_TYPE = 43;
	public final static byte KeyPressedEvent_TYPE = 44;
	public final static byte KeyReleasedEvent_TYPE = 45;
	public final static byte KeyTypedEvent_TYPE = 46;
	public final static byte MouseClickedEvent_TYPE = 47;
	public final static byte MouseDraggedEvent_TYPE = 48;
	public final static byte MouseEnteredEvent_TYPE = 49;
	public final static byte MouseExitedEvent_TYPE = 50;
	public final static byte MouseMovedEvent_TYPE = 51;
	public final static byte MousePressedEvent_TYPE = 52;
	public final static byte MouseReleasedEvent_TYPE = 53;
	public final static byte MouseWheelMovedEvent_TYPE = 54;
	public final static byte RoundEndedEvent_TYPE = 55;

	private final static ISerializableHelper[] typeToHelper = new ISerializableHelper[256];
	private static Map<Class<?>, Byte> classToType = new HashMap<Class<?>, Byte>();
	private final static Charset charset;
	private final CharsetEncoder encoder;
	private final CharsetDecoder decoder;

	private static final int BYTE_ORDER = 0xC0DEDEA1;
	private final int currentVersion;

	static {
		charset = Charset.forName("UTF8"); // we will use it as UCS-2
		register(null, TERMINATOR_TYPE); // reserved for end of (list) element
	}

	public RbSerializer() {
		this.currentVersion = ContainerBase.getComponent(IVersionManagerBase.class).getVersionAsInt();
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
		ByteBuffer buffer = ByteBuffer.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT);

		buffer.putInt(BYTE_ORDER);
		buffer.putInt(currentVersion);
		buffer.putInt(length);
		target.write(buffer.array());

		// body
		buffer = ByteBuffer.allocate(length);
		serialize(buffer, type, object);
		if (buffer.remaining() != 0) {
			throw new IOException("Serialization failed: bad size"); 
		}
		target.write(buffer.array());
	}

	public ByteBuffer serialize(byte type, Object object) throws IOException {
		int length = sizeOf(type, object);

		// header
		ByteBuffer buffer = ByteBuffer.allocateDirect(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT + length);

		buffer.putInt(BYTE_ORDER);
		buffer.putInt(currentVersion);
		buffer.putInt(length);

		// body
		serialize(buffer, type, object);
		if (buffer.remaining() != 0) {
			throw new IOException("Serialization failed: bad size");
		}
		return buffer;
	}

	public ByteBuffer serializeToBuffer(ByteBuffer buffer, byte type, Object object) throws IOException {
		int length = sizeOf(type, object);

		buffer.limit(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT + length);

		buffer.putInt(BYTE_ORDER);
		buffer.putInt(currentVersion);
		buffer.putInt(length);

		// body
		serialize(buffer, type, object);
		if (buffer.remaining() != 0) {
			throw new IOException("Serialization failed: bad size");
		}
		return buffer;
	}

	public Object deserialize(InputStream source) throws IOException {
		// header
		ByteBuffer buffer = ByteBuffer.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT);

		fillBuffer(source, buffer);
		buffer.flip();
		int bo = buffer.getInt();

		if (bo != BYTE_ORDER) {
			throw new IOException("Different byte order is not supported");
		}
		int version = buffer.getInt();

		if (version != currentVersion) {
			throw new IOException("Version of data is not supported. We support only strong match");
		}
		int length = buffer.getInt();

		// body
		buffer = ByteBuffer.allocate(length);
		fillBuffer(source, buffer);
		buffer.flip();
		final Object res = deserializeAny(buffer);

		if (buffer.remaining() != 0) {
			throw new IOException("Serialization failed");
		}
		return res;
	}

	public Object deserialize(final ByteBuffer buffer) throws IOException {
		int bo = buffer.getInt();

		if (bo != BYTE_ORDER) {
			throw new IOException("Different byte order is not supported");
		}

		int version = buffer.getInt();

		if (version != currentVersion) {
			throw new IOException("Version of data is not supported. We support only strong match");
		}
		int length = buffer.getInt();

		if (length != buffer.remaining()) {
			throw new IOException("Wrong buffer size, " + length + "expected but got " + buffer.remaining());
		}

		// body
		final Object res = deserializeAny(buffer);

		if (buffer.remaining() != 0) {
			throw new IOException("Serialization failed");
		}
		return res;
	}

	public void serialize(ByteBuffer buffer, byte type, Object object) {
		ISerializableHelper helper = getHelper(type);

		// FOR-DEBUG int expect = sizeOf(type, object) + buffer.position();

		if (object != null) {
			buffer.put(type);
			helper.serialize(this, buffer, object);
		} else {
			buffer.put(TERMINATOR_TYPE);
		}
		// FOR-DEBUG if (expect != buffer.position()) {
		// FOR-DEBUG 	throw new Error("Bad size");
		// FOR-DEBUG }
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

	public void serialize(ByteBuffer buffer, int[] data) {
		if (data == null) {
			buffer.putInt(-1);
		} else {
			buffer.putInt(data.length);
			for (int aData : data) {
				buffer.putInt(aData);
			}
		}
	}

	public void serialize(ByteBuffer buffer, char[] data) {
		if (data == null) {
			buffer.putInt(-1);
		} else {
			buffer.putInt(data.length);
			for (char aData : data) {
				buffer.putChar(aData);
			}
		}
	}

	public void serialize(ByteBuffer buffer, double[] data) {
		if (data == null) {
			buffer.putInt(-1);
		} else {
			buffer.putInt(data.length);
			for (double aData : data) {
				buffer.putDouble(aData);
			}
		}
	}

	public void serialize(ByteBuffer buffer, float[] data) {
		if (data == null) {
			buffer.putInt(-1);
		} else {
			buffer.putInt(data.length);
			for (float aData : data) {
				buffer.putFloat(aData);
			}
		}
	}

	public void serialize(ByteBuffer buffer, boolean value) {
		buffer.put((byte) (value ? 1 : 0));
	}

	public void serialize(ByteBuffer buffer, double value) {
		buffer.putDouble(value);
	}

	public void serialize(ByteBuffer buffer, char value) {
		buffer.putChar(value);
	}

	public void serialize(ByteBuffer buffer, long value) {
		buffer.putLong(value);
	}

	public void serialize(ByteBuffer buffer, int value) {
		buffer.putInt(value);
	}

	public void serialize(ByteBuffer buffer, Event event) {
		final byte type = HiddenAccess.getSerializationType(event);

		serialize(buffer, type, event);
	}

	public Object deserializeAny(ByteBuffer buffer) {
		final byte type = buffer.get();

		if (type == TERMINATOR_TYPE) {
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
		final int len = buffer.getInt();

		if (len == -1) {
			return null;
		}
		byte[] res = new byte[len];

		buffer.get(res);
		return res;
	}

	public int[] deserializeIntegers(ByteBuffer buffer) {
		final int len = buffer.getInt();

		if (len == -1) {
			return null;
		}
		int[] res = new int[len];

		for (int i = 0; i < len; i++) {
			res[i] = buffer.getInt();
		}
		return res;
	}

	public float[] deserializeFloats(ByteBuffer buffer) {
		final int len = buffer.getInt();

		if (len == -1) {
			return null;
		}
		float[] res = new float[len];

		for (int i = 0; i < len; i++) {
			res[i] = buffer.getFloat();
		}
		return res;
	}

	public char[] deserializeChars(ByteBuffer buffer) {
		final int len = buffer.getInt();

		if (len == -1) {
			return null;
		}
		char[] res = new char[len];

		for (int i = 0; i < len; i++) {
			res[i] = buffer.getChar();
		}
		return res;
	}

	public double[] deserializeDoubles(ByteBuffer buffer) {
		final int len = buffer.getInt();

		if (len == -1) {
			return null;
		}
		double[] res = new double[len];

		for (int i = 0; i < len; i++) {
			res[i] = buffer.getDouble();
		}
		return res;
	}

	public boolean deserializeBoolean(ByteBuffer buffer) {
		return buffer.get() != 0;
	}

	public char deserializeChar(ByteBuffer buffer) {
		return buffer.getChar();
	}

	public int deserializeInt(ByteBuffer buffer) {
		return buffer.getInt();
	}

	public Float deserializeFloat(ByteBuffer buffer) {
		return buffer.getFloat();
	}

	public double deserializeDouble(ByteBuffer buffer) {
		return buffer.getDouble();
	}

	public long deserializeLong(ByteBuffer buffer) {
		return buffer.getLong();
	}

	public int sizeOf(String data) {
		return (data == null) ? SIZEOF_INT : SIZEOF_INT + encode(data).limit();
	}

	public int sizeOf(byte[] data) {
		return (data == null) ? SIZEOF_INT : SIZEOF_INT + data.length;
	}

	public int sizeOf(byte type, Object object) {
		return getHelper(type).sizeOf(this, object);
	}

	public int sizeOf(Event event) {
		return sizeOf(HiddenAccess.getSerializationType(event), event);
	}

	private ISerializableHelper getHelper(byte type) {
		final ISerializableHelper helper = typeToHelper[type];

		if (helper == null) {
			throw new Error("Unknownd or unsupported data type");
		}
		return helper;
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

	public static void register(Class<?> realClass, byte type) {
		try {
			if (realClass != null) {
				Method method = realClass.getDeclaredMethod("createHiddenSerializer");

				method.setAccessible(true);
				ISerializableHelper helper = (ISerializableHelper) method.invoke(null);

				method.setAccessible(false);
				typeToHelper[type] = helper;
				classToType.put(realClass, type);
			}
		} catch (NoSuchMethodException e) {
			Logger.logError(e);
		} catch (InvocationTargetException e) {
			Logger.logError(e);
		} catch (IllegalAccessException e) {
			Logger.logError(e);
		}
	}

	public static ByteBuffer serializeToBuffer(Object src) throws IOException {
		RbSerializer rbs = new RbSerializer();
		final Byte type = classToType.get(src.getClass());

		return rbs.serialize(type, src);
	}

	@SuppressWarnings({ "unchecked"})
	public static <T> T deserializeFromBuffer(ByteBuffer buffer) throws IOException {
		RbSerializer rbs = new RbSerializer();
		final Object res = rbs.deserialize(buffer);

		return (T) res;
	}

	public static Object deepCopy(byte type, Object src) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		RbSerializer rbs = new RbSerializer();

		try {
			rbs.serialize(out, type, src);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			return rbs.deserialize(in);
		} catch (IOException e) {
			Logger.logError(e);
			return null;
		}
	}
}
