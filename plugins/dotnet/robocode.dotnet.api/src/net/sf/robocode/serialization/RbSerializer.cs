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

using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;
using java.nio;
using net.sf.robocode.io;
using net.sf.robocode.security;
using robocode;

namespace net.sf.robocode.serialization
{
    /// <summary>
    /// @author Pavel Savara (original)
    /// </summary>
    internal sealed class RbSerializer
    {
        public static readonly int SIZEOF_TYPEINFO = 1;
        public static readonly int SIZEOF_BYTE = 1;
        public static readonly int SIZEOF_BOOL = 1;
        public static readonly int SIZEOF_CHAR = 2;
        public static readonly int SIZEOF_INT = 4;
        public static readonly int SIZEOF_LONG = 8;
        public static readonly int SIZEOF_DOUBLE = 8;

        public static readonly byte TERMINATOR_TYPE = 0xff;
        public static readonly byte ExecCommands_TYPE = 1;
        public static readonly byte BulletCommand_TYPE = 2;
        public static readonly byte TeamMessage_TYPE = 3;
        public static readonly byte DebugProperty_TYPE = 4;
        public static readonly byte ExecResults_TYPE = 5;
        public static readonly byte RobotStatus_TYPE = 6;
        public static readonly byte BulletStatus_TYPE = 7;
        public static readonly byte BattleResults_TYPE = 8;
        public static readonly byte Bullet_TYPE = 9;

        public static readonly byte BattleEndedEvent_TYPE = 32;
        public static readonly byte BulletHitBulletEvent_TYPE = 33;
        public static readonly byte BulletHitEvent_TYPE = 34;
        public static readonly byte BulletMissedEvent_TYPE = 35;
        public static readonly byte DeathEvent_TYPE = 36;
        public static readonly byte WinEvent_TYPE = 37;
        public static readonly byte HitWallEvent_TYPE = 38;
        public static readonly byte RobotDeathEvent_TYPE = 39;
        public static readonly byte SkippedTurnEvent_TYPE = 40;
        public static readonly byte ScannedRobotEvent_TYPE = 41;
        public static readonly byte HitByBulletEvent_TYPE = 42;
        public static readonly byte HitRobotEvent_TYPE = 43;
        public static readonly byte KeyPressedEvent_TYPE = 44;
        public static readonly byte KeyReleasedEvent_TYPE = 45;
        public static readonly byte KeyTypedEvent_TYPE = 46;
        public static readonly byte MouseClickedEvent_TYPE = 47;
        public static readonly byte MouseDraggedEvent_TYPE = 48;
        public static readonly byte MouseEnteredEvent_TYPE = 49;
        public static readonly byte MouseExitedEvent_TYPE = 50;
        public static readonly byte MouseMovedEvent_TYPE = 51;
        public static readonly byte MousePressedEvent_TYPE = 52;
        public static readonly byte MouseReleasedEvent_TYPE = 53;
        public static readonly byte MouseWheelMovedEvent_TYPE = 54;

        private static readonly IISerializableHelper[] typeToHelper = new IISerializableHelper[256];
        private static Dictionary<Type, byte> classToType = new Dictionary<Type, byte>();
        private static readonly Encoding charset;
        private readonly Encoder encoder;
        private readonly Decoder decoder;

        private readonly uint byteOrder = 0xC0DEDEA1;
        private readonly int currentVersion;

        static RbSerializer()
        {
            charset = Encoding.UTF8; // we will use it as UCS-2
            register(null, TERMINATOR_TYPE); // reserved for end of (list) element
        }

        public RbSerializer()
        {
            currentVersion = 0; //TODO ContainerBase.getComponent(IVersionManagerBase.class).getVersionAsInt();
            /*
		encoder = charset.newEncoder();
		encoder.onMalformedInput(CodingErrorAction.REPORT);
		encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
		// throw away forst bom
		ByteBuffer buffer = ByteBufferFactory.allocate(8);

		encoder.encode(CharBuffer.wrap("BOM"), buffer, false);

		decoder = charset.newDecoder();
		decoder.onMalformedInput(CodingErrorAction.REPORT);
		decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
         */
        }

        public void serialize(BinaryWriter target, byte type, object obj)
        {
            /*
		int length = sizeOf(type, object);

		// header
		ByteBuffer buffer = ByteBufferFactory.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT);

		buffer.putInt(byteOrder);
		buffer.putInt(currentVersion);
		buffer.putInt(length);
		target.write(buffer.array());

		// body
		buffer = ByteBufferFactory.allocate(length);
		serialize(buffer, type, object);
		if (buffer.remaining() != 0) {
			throw new IOException("Serialization failed: bad size"); 
		}
		target.write(buffer.array());
         */
        }

        public IByteBuffer serialize(byte type, object obj)
        {
            int length = sizeOf(type, obj);

            // header
            IByteBuffer buffer = ByteBufferFactory.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT + length);

            buffer.putUInt(byteOrder);
            buffer.putInt(currentVersion);
            buffer.putInt(length);

            // body
            serialize(buffer, type, obj);
            if (buffer.remaining() != 0)
            {
                throw new IOException("Serialization failed: bad size");
            }
            return buffer;
        }

        public Object deserialize(BinaryReader source)
        {
            // header
            IByteBuffer buffer = ByteBufferFactory.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT);

            fillBuffer(source, buffer);
            buffer.flip();
            int bo = buffer.getInt();

            if (bo != byteOrder)
            {
                throw new IOException("Different byte order is not supported");
            }
            int version = buffer.getInt();

            if (version != currentVersion)
            {
                throw new IOException("Version of data is not supported. We support only strong match");
            }
            int length = buffer.getInt();

            // body
            buffer = ByteBufferFactory.allocate(length);
            fillBuffer(source, buffer);
            buffer.flip();
            Object res = deserializeAny(buffer);

            if (buffer.remaining() != 0)
            {
                throw new IOException("Serialization failed");
            }
            return res;
        }

        public Object deserialize(IByteBuffer buffer)
        {
            int bo = buffer.getInt();

            if (bo != byteOrder)
            {
                throw new IOException("Different byte order is not supported");
            }

            int version = buffer.getInt();

            if (version != currentVersion)
            {
                throw new IOException("Version of data is not supported. We support only strong match");
            }
            int length = buffer.getInt();

            if (length != buffer.remaining())
            {
                throw new IOException("Wrong buffer size, " + length + "expected but got " + buffer.remaining());
            }

            // body
            Object res = deserializeAny(buffer);

            if (buffer.remaining() != 0)
            {
                throw new IOException("Serialization failed");
            }
            return res;
        }

        public void serialize(IByteBuffer buffer, byte type, object obj)
        {
            IISerializableHelper helper = getHelper(type);

            // FOR-DEBUG int expect = sizeOf(type, object) + buffer.position();

            if (obj != null)
            {
                buffer.put(type);
                helper.serialize(this, buffer, obj);
            }
            else
            {
                buffer.put(TERMINATOR_TYPE);
            }
            // FOR-DEBUG if (expect != buffer.position()) {
            // FOR-DEBUG 	throw new Exception("Bad size");
            // FOR-DEBUG }
        }

        public void serialize(IByteBuffer buffer, string data)
        {
            if (data == null)
            {
                buffer.putInt(-1);
            }
            else
            {
                IByteBuffer slice = encode(data);

                buffer.putInt(slice.limit());
                buffer.put(slice);
            }
        }

        public void serialize(IByteBuffer buffer, byte[] data)
        {
            if (data == null)
            {
                buffer.putInt(-1);
            }
            else
            {
                buffer.putInt(data.Length);
                buffer.put(data);
            }
        }

        public void serialize(IByteBuffer buffer, int[] data)
        {
            if (data == null)
            {
                buffer.putInt(-1);
            }
            else
            {
                buffer.putInt(data.Length);
                foreach (int aData in data)
                {
                    buffer.putInt(aData);
                }
            }
        }

        public void serialize(IByteBuffer buffer, char[] data)
        {
            if (data == null)
            {
                buffer.putInt(-1);
            }
            else
            {
                buffer.putInt(data.Length);
                foreach (char aData in data)
                {
                    buffer.putChar(aData);
                }
            }
        }

        public void serialize(IByteBuffer buffer, double[] data)
        {
            if (data == null)
            {
                buffer.putInt(-1);
            }
            else
            {
                buffer.putInt(data.Length);
                foreach (double aData in data)
                {
                    buffer.putDouble(aData);
                }
            }
        }

        public void serialize(IByteBuffer buffer, float[] data)
        {
            if (data == null)
            {
                buffer.putInt(-1);
            }
            else
            {
                buffer.putInt(data.Length);
                foreach (float aData in data)
                {
                    buffer.putFloat(aData);
                }
            }
        }

        public void serialize(IByteBuffer buffer, bool value)
        {
            buffer.put((byte) (value ? 1 : 0));
        }

        public void serialize(IByteBuffer buffer, double value)
        {
            buffer.putDouble(value);
        }

        public void serialize(IByteBuffer buffer, char value)
        {
            buffer.putChar(value);
        }

        public void serialize(IByteBuffer buffer, long value)
        {
            buffer.putLong(value);
        }

        public void serialize(IByteBuffer buffer, int value)
        {
            buffer.putInt(value);
        }

        public void serialize(IByteBuffer buffer, Event evnt)
        {
            byte type = HiddenAccess.getSerializationType(evnt);

            serialize(buffer, type, evnt);
        }

        public Object deserializeAny(IByteBuffer buffer)
        {
            byte type = buffer.get();

            if (type == TERMINATOR_TYPE)
            {
                return null;
            }
            return getHelper(type).deserialize(this, buffer);
        }

        public string deserializestring(IByteBuffer buffer)
        {
            int bytes = buffer.getInt();

            if (bytes == -1)
            {
                return null;
            }
            IByteBuffer slice = buffer.slice();

            slice.limit(bytes);
            string res;

            try
            {
                //TODO res = decoder.GetChars() .decode(slice).tostring();
                throw new NotImplementedException();
            }
            catch (Exception e)
            {
                throw new Exception("Bad character", e);
            }
            buffer.position(buffer.position() + bytes);
            return res;
        }

        public byte[] deserializeBytes(IByteBuffer buffer)
        {
            int len = buffer.getInt();

            if (len == -1)
            {
                return null;
            }
            var res = new byte[len];

            buffer.get(res);
            return res;
        }

        public int[] deserializeints(IByteBuffer buffer)
        {
            int len = buffer.getInt();

            if (len == -1)
            {
                return null;
            }
            var res = new int[len];

            for (int i = 0; i < len; i++)
            {
                res[i] = buffer.getInt();
            }
            return res;
        }

        public float[] deserializeFloats(IByteBuffer buffer)
        {
            int len = buffer.getInt();

            if (len == -1)
            {
                return null;
            }
            var res = new float[len];

            for (int i = 0; i < len; i++)
            {
                res[i] = buffer.getFloat();
            }
            return res;
        }

        public char[] deserializeChars(IByteBuffer buffer)
        {
            int len = buffer.getInt();

            if (len == -1)
            {
                return null;
            }
            var res = new char[len];

            for (int i = 0; i < len; i++)
            {
                res[i] = buffer.getChar();
            }
            return res;
        }

        public double[] deserializeDoubles(IByteBuffer buffer)
        {
            int len = buffer.getInt();

            if (len == -1)
            {
                return null;
            }
            var res = new double[len];

            for (int i = 0; i < len; i++)
            {
                res[i] = buffer.getDouble();
            }
            return res;
        }

        public bool deserializeBoolean(IByteBuffer buffer)
        {
            return buffer.get() != 0;
        }

        public char deserializeChar(IByteBuffer buffer)
        {
            return buffer.getChar();
        }

        public int deserializeInt(IByteBuffer buffer)
        {
            return buffer.getInt();
        }

        public float deserializeFloat(IByteBuffer buffer)
        {
            return buffer.getFloat();
        }

        public double deserializeDouble(IByteBuffer buffer)
        {
            return buffer.getDouble();
        }

        public int sizeOf(string data)
        {
            return (data == null) ? SIZEOF_INT : SIZEOF_INT + encode(data).limit();
        }

        public int sizeOf(byte[] data)
        {
            return (data == null) ? SIZEOF_INT : SIZEOF_INT + data.Length;
        }

        public int sizeOf(byte type, object obj)
        {
            return getHelper(type).sizeOf(this, obj);
        }

        public int sizeOf(Event evnt)
        {
            return sizeOf(HiddenAccess.getSerializationType(evnt), evnt);
        }

        private IISerializableHelper getHelper(byte type)
        {
            IISerializableHelper helper = typeToHelper[type];

            if (helper == null)
            {
                throw new Exception("Unknownd or unsupported data type");
            }
            return helper;
        }

        private IByteBuffer encode(string data)
        {
            IByteBuffer slice = ByteBufferFactory.allocate(data.Length*3);
            throw new NotImplementedException();
            //encoder.encode(CharBuffer.wrap(data), slice, false);
            slice.flip();
            return slice;
        }

        private void fillBuffer(BinaryReader source, IByteBuffer buffer)
        {
            throw new NotImplementedException();
            /*
		int res;

		do {
			res = source.read(buffer.array(), buffer.position(), buffer.remaining());
			if (res == -1) {
				throw new IOException("Unexpected EOF");
			}
			buffer.position(buffer.position() + res);
		} while (buffer.remaining() != 0);
         */
        }

        public static void register(Type realClass, byte type)
        {
            try
            {
                if (realClass != null)
                {
                    MethodInfo method = realClass.GetMethod("createHiddenSerializer");

                    var helper = (IISerializableHelper) method.Invoke(null, null);

                    typeToHelper[type] = helper;
                    classToType.Add(realClass, type);
                }
            }
            catch (Exception e)
            {
                Logger.logError(e);
            }
        }

        public static IByteBuffer serializeToBuffer(Object src)
        {
            var rbs = new RbSerializer();
            Byte type = classToType[src.GetType()];

            return rbs.serialize(type, src);
        }

        public static T deserializeFromBuffer<T>(IByteBuffer buffer)
        {
            var rbs = new RbSerializer();
            Object res = rbs.deserialize(buffer);

            return (T) res;
        }

        public static Object deepCopy(byte type, Object src)
        {
            var output = new MemoryStream(1024);
            var rbs = new RbSerializer();

            try
            {
                throw new NotImplementedException();
                /*
			rbs.serialize(out, type, src);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			return rbs.deserialize(in);
            */
            }
            catch (IOException e)
            {
                Logger.logError(e);
                return null;
            }
        }
    }
}

//happy