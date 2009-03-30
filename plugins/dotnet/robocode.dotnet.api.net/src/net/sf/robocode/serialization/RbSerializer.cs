using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Text;
using net.sf.robocode.io;
using net.sf.robocode.security;
using robocode;

namespace net.sf.robocode.serialization
{
    public class RbSerializer
    {
        private const uint byteOrder = 0xC0DEDEA1;
        private static readonly Encoding charset;
        private static readonly Dictionary<Type, byte> classToType = new Dictionary<Type, byte>();
        private static readonly ISerializableHelper[] typeToHelper = new ISerializableHelper[256];
        public static byte BattleEndedEvent_TYPE = 32;
        public static byte BattleResults_TYPE = 8;
        public static byte Bullet_TYPE = 9;
        public static byte BulletCommand_TYPE = 2;

        public static byte BulletHitBulletEvent_TYPE = 33;
        public static byte BulletHitEvent_TYPE = 34;
        public static byte BulletMissedEvent_TYPE = 35;
        public static byte BulletStatus_TYPE = 7;
        public static byte DeathEvent_TYPE = 36;
        public static byte DebugProperty_TYPE = 4;
        public static byte ExecCommands_TYPE = 1;
        public static byte ExecResults_TYPE = 5;
        public static byte HitByBulletEvent_TYPE = 42;
        public static byte HitRobotEvent_TYPE = 43;
        public static byte HitWallEvent_TYPE = 38;
        public static byte KeyPressedEvent_TYPE = 44;
        public static byte KeyReleasedEvent_TYPE = 45;
        public static byte KeyTypedEvent_TYPE = 46;
        public static byte MouseClickedEvent_TYPE = 47;
        public static byte MouseDraggedEvent_TYPE = 48;
        public static byte MouseEnteredEvent_TYPE = 49;
        public static byte MouseExitedEvent_TYPE = 50;
        public static byte MouseMovedEvent_TYPE = 51;
        public static byte MousePressedEvent_TYPE = 52;
        public static byte MouseReleasedEvent_TYPE = 53;
        public static byte MouseWheelMovedEvent_TYPE = 54;
        public static byte RobotDeathEvent_TYPE = 39;
        public static byte RobotStatus_TYPE = 6;
        public static byte ScannedRobotEvent_TYPE = 41;

        public static int SIZEOF_BOOL = 1;
        public static int SIZEOF_BYTE = 1;
        public static int SIZEOF_CHAR = 2;
        public static int SIZEOF_DOUBLE = 8;
        public static int SIZEOF_INT = 4;
        public static int SIZEOF_LONG = 8;
        public static int SIZEOF_TYPEINFO = 1;
        public static byte SkippedTurnEvent_TYPE = 40;
        public static byte TeamMessage_TYPE = 3;
        public static byte TERMINATOR_TYPE = 0x80;
        public static byte WinEvent_TYPE = 37;

        private readonly int currentVersion;
        private readonly Decoder decoder;
        private readonly Encoder encoder;

        static RbSerializer()
        {
            charset = Encoding.UTF8; // we will use it as UCS-2
            register(null, TERMINATOR_TYPE); // reserved for end of (list) element
        }

        public RbSerializer()
        {
            currentVersion = 0x01070200; //TODO ContainerBase.getComponent<IVersionManagerBase>().getVersionAsInt();
            encoder = charset.GetEncoder();
            decoder = charset.GetDecoder();

            var bytesData = new byte[8];
            encoder.GetBytes("BOM".ToCharArray(), 0, "BOM".Length, bytesData, 0, false);
        }

        public int serialize(Stream target, byte type, object obj)
        {
            long offset = target.Position;
            int length = sizeOf(type, obj);

            var bw = new BinaryWriter(target);

            bw.Write(byteOrder);
            bw.Write(currentVersion);
            bw.Write(length);

            serialize(bw, type, obj);
            if (bw.BaseStream.Position - offset != length+3*SIZEOF_INT)
            {
                throw new IOException("Serialization failed: bad size");
            }
            bw.Flush();
            return length + 3*SIZEOF_INT;
        }

        public object deserialize(Stream source)
        {
            // header
            var br = new BinaryReader(source);
            uint bo = br.ReadUInt32();
            if (bo != byteOrder)
            {
                throw new IOException("Different byte order is not supported");
            }
            int version = br.ReadInt32();

            if (version != currentVersion)
            {
                throw new IOException("Version of data is not supported. We support only strong match");
            }
            int length = br.ReadInt32();

            // body
            object res = deserializeAny(br);

            if (source.Length - source.Position != 0)
            {
                throw new IOException("Serialization failed");
            }
            return res;
        }

        public object deserialize(BinaryReader br)
        {
            long offset = br.BaseStream.Position;
            uint bo = br.ReadUInt32();
            if (bo != byteOrder)
            {
                throw new IOException("Different byte order is not supported");
            }

            int version = br.ReadInt32();

            if (version != currentVersion)
            {
                throw new IOException("Version of data is not supported. We support only strong match");
            }
            int length = br.ReadInt32();

            long remaining = br.BaseStream.Length - offset;
            if (length != remaining)
            {
                throw new IOException("Wrong buffer size, " + length + "expected but got " + remaining);
            }

            // body
            object res = deserializeAny(br);

            remaining = br.BaseStream.Length - offset;
            if (remaining != 0)
            {
                throw new IOException("Serialization failed");
            }
            return res;
        }

        public void serialize(BinaryWriter bw, byte type, object obj)
        {
            ISerializableHelper helper = getHelper(type);

            // FOR-DEBUG long expect = sizeOf(type, obj) + bw.BaseStream.Position;

            if (obj != null)
            {
                bw.Write(type);
                helper.serialize(this, bw, obj);
            }
            else
            {
                bw.Write(TERMINATOR_TYPE);
            }
            
            // FOR-DEBUG if (expect != bw.BaseStream.Position) {
            // FOR-DEBUG     throw new InvalidProgramException("Bad size");
            // FOR-DEBUG }
        }

        public void serialize(BinaryWriter bw, string data)
        {
            if (data == null)
            {
                bw.Write(-1);
            }
            else
            {
                var bytesData = new byte[data.Length*3];
                int bytes = encoder.GetBytes(data.ToCharArray(), 0, data.Length, bytesData, 0, false);
                bw.Write(bytes);
                bw.Write(bytesData, 0, bytes);
            }
        }

        public void serialize(BinaryWriter bw, byte[] data)
        {
            if (data == null)
            {
                bw.Write(-1);
            }
            else
            {
                bw.Write(data.Length);
                bw.Write(data);
            }
        }

        public void serialize(BinaryWriter bw, int[] data)
        {
            if (data == null)
            {
                bw.Write(-1);
            }
            else
            {
                bw.Write(data.Length);
                foreach (int aData in data)
                {
                    bw.Write(aData);
                }
            }
        }

        public void serialize(BinaryWriter bw, char[] data)
        {
            if (data == null)
            {
                bw.Write(-1);
            }
            else
            {
                bw.Write(data.Length);
                foreach (char aData in data)
                {
                    bw.Write(aData);
                }
            }
        }

        public void serialize(BinaryWriter bw, double[] data)
        {
            if (data == null)
            {
                bw.Write(-1);
            }
            else
            {
                bw.Write(data.Length);
                foreach (double aData in data)
                {
                    bw.Write(aData);
                }
            }
        }

        public void serialize(BinaryWriter bw, float[] data)
        {
            if (data == null)
            {
                bw.Write(-1);
            }
            else
            {
                bw.Write(data.Length);
                foreach (float aData in data)
                {
                    bw.Write((double) aData);
                }
            }
        }

        public void serialize(BinaryWriter bw, bool value)
        {
            bw.Write((byte) (value ? 1 : 0));
        }

        public void serialize(BinaryWriter bw, double value)
        {
            bw.Write(value);
        }

        public void serialize(BinaryWriter bw, char value)
        {
            bw.Write(value);
        }

        public void serialize(BinaryWriter bw, long value)
        {
            bw.Write(value);
        }

        public void serialize(BinaryWriter bw, int value)
        {
            bw.Write(value);
        }

        public void serialize(BinaryWriter bw, Event evnt)
        {
            byte type = HiddenAccess.getSerializationType(evnt);

            serialize(bw, type, evnt);
        }

        public object deserializeAny(BinaryReader br)
        {
            byte type = br.ReadByte();

            if (type == TERMINATOR_TYPE)
            {
                return null;
            }
            return getHelper(type).deserialize(this, br);
        }

        public string deserializeString(BinaryReader br)
        {
            int bytes = br.ReadInt32();

            if (bytes == -1)
            {
                return null;
            }

            byte[] readBytes = br.ReadBytes(bytes);
            var chars = new char[bytes*3];
            int len = decoder.GetChars(readBytes, 0, bytes, chars, 0, false);
            return new string(chars, 0, len);
        }

        public byte[] deserializeBytes(BinaryReader br)
        {
            int len = br.ReadInt32();

            if (len == -1)
            {
                return null;
            }

            return br.ReadBytes(len);
        }

        public int[] deserializeIntegers(BinaryReader br)
        {
            int len = br.ReadInt32();

            if (len == -1)
            {
                return null;
            }
            var res = new int[len];

            for (int i = 0; i < len; i++)
            {
                res[i] = br.ReadInt32();
            }
            return res;
        }

        public float[] deserializeFloats(BinaryReader br)
        {
            int len = br.ReadInt32();

            if (len == -1)
            {
                return null;
            }
            var res = new float[len];

            for (int i = 0; i < len; i++)
            {
                res[i] = (float) br.ReadDouble();
            }
            return res;
        }

        public char[] deserializeChars(BinaryReader br)
        {
            int len = br.ReadInt32();

            if (len == -1)
            {
                return null;
            }
            var res = new char[len];

            for (int i = 0; i < len; i++)
            {
                res[i] = br.ReadChar();
            }
            return res;
        }

        public double[] deserializeDoubles(BinaryReader br)
        {
            int len = br.ReadInt32();

            if (len == -1)
            {
                return null;
            }
            var res = new double[len];

            for (int i = 0; i < len; i++)
            {
                res[i] = br.ReadDouble();
            }
            return res;
        }

        public bool deserializeBoolean(BinaryReader br)
        {
            return br.ReadByte() != 0;
        }

        public char deserializeChar(BinaryReader br)
        {
            return br.ReadChar();
        }

        public int deserializeInt(BinaryReader br)
        {
            return br.ReadInt32();
        }

        public double deserializeDouble(BinaryReader br)
        {
            return br.ReadDouble();
        }

        public int sizeOf(string data)
        {
            return (data == null) ? SIZEOF_INT : SIZEOF_INT + encoder.GetByteCount(data.ToCharArray(), 0, data.Length, false);
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

        private ISerializableHelper getHelper(byte type)
        {
            ISerializableHelper helper = typeToHelper[type];

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
                    MethodInfo[] methods = realClass.GetMethods();
                    MethodInfo method = realClass.GetMethod("createHiddenSerializer",
                                                            BindingFlags.Static | BindingFlags.NonPublic);
                    var helper = (ISerializableHelper) method.Invoke(null, null);

                    typeToHelper[type] = helper;
                    classToType.Add(realClass, type);
                }
            }
            catch (Exception e)
            {
                Logger.logError(e);
            }
        }
    }
}