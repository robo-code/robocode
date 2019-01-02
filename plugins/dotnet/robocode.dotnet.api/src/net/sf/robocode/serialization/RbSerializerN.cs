/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Security.Permissions;
using System.Text;
using net.sf.robocode.io;
using net.sf.robocode.nio;
using net.sf.robocode.security;
using Robocode;

namespace net.sf.robocode.serialization
{
#pragma warning disable 1591
    /// <exclude/>
    [RobocodeInternalPermission(SecurityAction.LinkDemand)]
    public sealed class RbSerializerN
    {
        private const int byteOrder = -1059135839; //0xC0DEDEA1
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
        public static readonly byte RobotStatics_TYPE = 10;

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
        public static readonly byte RoundEndedEvent_TYPE = 55;

        private static readonly ISerializableHelperN[] typeToHelper = new ISerializableHelperN[256];
        private static readonly Dictionary<Type, byte> classToType = new Dictionary<Type, byte>();
        private static readonly Encoding charset;

        private static int currentVersion;

        static RbSerializerN()
        {
            charset = Encoding.UTF8; // we will use it as UCS-2
            register(null, TERMINATOR_TYPE); // reserved for end of (list) element
        }

        public static void Init(int version)
        {
            if (currentVersion == 0)
            {
                currentVersion = version;
            }
        }

        public ByteBuffer serialize(byte type, object obj)
        {
            int length = sizeOf(type, obj);

            // header
            ByteBuffer buffer = ByteBuffer.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT + length);

            buffer.putInt(byteOrder);
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

        public ByteBuffer serializeToBuffer(ByteBuffer buffer, byte type, object obj)
        {
            int length = sizeOf(type, obj);
            buffer.limit(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT + length);

            buffer.putInt(byteOrder);
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

        public Object deserialize(ByteBuffer buffer)
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

        public void serialize(ByteBuffer buffer, byte type, object obj)
        {
            ISerializableHelperN helper = getHelper(type);

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

        public void serialize(ByteBuffer buffer, string data)
        {
            if (data == null)
            {
                buffer.putInt(-1);
            }
            else
            {
                int bytes = charset.GetBytes(data, 0, data.Length, buffer.array(), buffer.position() + 4);
                buffer.putInt(bytes);
                buffer.position(buffer.position() + bytes);
            }
        }

        public void serialize(ByteBuffer buffer, byte[] data)
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

        public void serialize(ByteBuffer buffer, int[] data)
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

        public void serialize(ByteBuffer buffer, char[] data)
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

        public void serialize(ByteBuffer buffer, double[] data)
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

        public void serialize(ByteBuffer buffer, float[] data)
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

        public void serialize(ByteBuffer buffer, bool value)
        {
            buffer.put((byte) (value ? 1 : 0));
        }

        public void serialize(ByteBuffer buffer, double value)
        {
            buffer.putDouble(value);
        }

        public void serialize(ByteBuffer buffer, char value)
        {
            buffer.putChar(value);
        }

        public void serialize(ByteBuffer buffer, long value)
        {
            buffer.putLong(value);
        }

        public void serialize(ByteBuffer buffer, int value)
        {
            buffer.putInt(value);
        }

        public void serialize(ByteBuffer buffer, Event evnt)
        {
            byte type = HiddenAccessN.GetSerializationType(evnt);

            serialize(buffer, type, evnt);
        }

        public Object deserializeAny(ByteBuffer buffer)
        {
            byte type = buffer.get();

            if (type == TERMINATOR_TYPE)
            {
                return null;
            }
            return getHelper(type).deserialize(this, buffer);
        }

        public string deserializeString(ByteBuffer buffer)
        {
            int bytes = buffer.getInt();

            if (bytes == -1)
            {
                return null;
            }
            ByteBuffer slice = buffer.slice();

            slice.limit(bytes);
            string res;

            try
            {
                byte[] array = buffer.array();
                char[] chars = charset.GetChars(array, buffer.position(), bytes);
                res = new string(chars);
            }
            catch (Exception e)
            {
                throw new Exception("Bad character", e);
            }
            buffer.position(buffer.position() + bytes);
            return res;
        }

        public byte[] deserializeBytes(ByteBuffer buffer)
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

        public int[] deserializeints(ByteBuffer buffer)
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

        public float[] deserializeFloats(ByteBuffer buffer)
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

        public char[] deserializeChars(ByteBuffer buffer)
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

        public double[] deserializeDoubles(ByteBuffer buffer)
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

        public bool deserializeBoolean(ByteBuffer buffer)
        {
            return buffer.get() != 0;
        }

        public char deserializeChar(ByteBuffer buffer)
        {
            return buffer.getChar();
        }

        public int deserializeInt(ByteBuffer buffer)
        {
            return buffer.getInt();
        }

        public long deserializeLong(ByteBuffer buffer)
        {
            return buffer.getLong();
        }

        public float deserializeFloat(ByteBuffer buffer)
        {
            return buffer.getFloat();
        }

        public double deserializeDouble(ByteBuffer buffer)
        {
            return buffer.getDouble();
        }

        public int sizeOf(string data)
        {
            return (data == null) ? SIZEOF_INT : SIZEOF_INT + charset.GetByteCount(data);
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
            return sizeOf(HiddenAccessN.GetSerializationType(evnt), evnt);
        }

        private static ISerializableHelperN getHelper(byte type)
        {
            ISerializableHelperN helper = typeToHelper[type];

            if (helper == null)
            {
                throw new Exception("Unknownd or unsupported data type");
            }
            return helper;
        }

        public static void register(Type realClass, byte type)
        {
            try
            {
                if (realClass != null)
                {
                    MethodInfo method = realClass.GetMethod("createHiddenSerializer",
                                                            BindingFlags.NonPublic | BindingFlags.Static);
                    var helper = (ISerializableHelperN) method.Invoke(null, null);

                    typeToHelper[type] = helper;
                    classToType.Add(realClass, type);
                }
            }
            catch (Exception e)
            {
                LoggerN.logError(e);
            }
        }

        public static ByteBuffer serializeToBuffer(Object src)
        {
            var rbs = new RbSerializerN();
            Byte type = classToType[src.GetType()];

            return rbs.serialize(type, src);
        }

        public static T deserializeFromBuffer<T>(ByteBuffer buffer)
        {
            var rbs = new RbSerializerN();
            Object res = rbs.deserialize(buffer);

            return (T) res;
        }
    }
}

//happy
