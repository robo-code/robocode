using java.io;
using java.lang;
using java.lang.reflect;
using java.nio;
using java.nio.charset;
using java.util;
using net.sf.robocode.core;
using net.sf.robocode.io;
using net.sf.robocode.manager;
using net.sf.robocode.security;
using robocode;

namespace net.sf.robocode.serialization
{
    public class RbSerializer
    {
        private static readonly int byteOrder = (int) byteOrderu;
        private static readonly Charset charset;
        private static readonly Hashtable classToType = new Hashtable();
        private static readonly ISerializableHelper[] typeToHelper = new ISerializableHelper[256];
        public static byte BattleEndedEvent_TYPE = 32;
        public static byte BattleResults_TYPE = 8;
        public static byte Bullet_TYPE = 9;
        public static byte BulletCommand_TYPE = 2;

        public static byte BulletHitBulletEvent_TYPE = 33;
        public static byte BulletHitEvent_TYPE = 34;
        public static byte BulletMissedEvent_TYPE = 35;
        public static byte BulletStatus_TYPE = 7;
        private static uint byteOrderu = 0xC0DEDEA1;
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
        public static byte TERMINATOR_TYPE = 0xff; //TODO check
        public static byte WinEvent_TYPE = 37;

        private readonly int currentVersion;
        private readonly CharsetDecoder decoder;
        private readonly CharsetEncoder encoder;

        static RbSerializer()
        {
            charset = Charset.forName("UTF8"); // we will use it as UCS-2
            register(null, TERMINATOR_TYPE); // reserved for end of (list) element
        }

        public RbSerializer()
        {
            currentVersion = ContainerBase.getComponent<IVersionManagerBase>().getVersionAsInt();
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

        public void serialize(OutputStream target, byte type, object @object)
        {
            int length = sizeOf(type, @object);

            // header
            ByteBuffer buffer = ByteBuffer.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT);

            buffer.putInt(byteOrder);
            buffer.putInt(currentVersion);
            buffer.putInt(length);
            target.write(buffer.array());

            // body
            buffer = ByteBuffer.allocate(length);
            serialize(buffer, type, @object);
            if (buffer.remaining() != 0)
            {
                throw new IOException("Serialization failed: bad size");
            }
            target.write(buffer.array());
        }

        public ByteBuffer serialize(byte type, object @object)
        {
            int length = sizeOf(type, @object);

            // header
            ByteBuffer buffer = ByteBuffer.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT + length);

            buffer.putInt(byteOrder);
            buffer.putInt(currentVersion);
            buffer.putInt(length);

            // body
            serialize(buffer, type, @object);
            if (buffer.remaining() != 0)
            {
                throw new IOException("Serialization failed: bad size");
            }
            return buffer;
        }

        public object deserialize(InputStream source)
        {
            // header
            ByteBuffer buffer = ByteBuffer.allocate(SIZEOF_INT + SIZEOF_INT + SIZEOF_INT);

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
            buffer = ByteBuffer.allocate(length);
            fillBuffer(source, buffer);
            buffer.flip();
            object res = deserializeAny(buffer);

            if (buffer.remaining() != 0)
            {
                throw new IOException("Serialization failed");
            }
            return res;
        }

        public object deserialize(ByteBuffer buffer)
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
            object res = deserializeAny(buffer);

            if (buffer.remaining() != 0)
            {
                throw new IOException("Serialization failed");
            }
            return res;
        }

        public void serialize(ByteBuffer buffer, byte type, object @object)
        {
            ISerializableHelper helper = getHelper(type);

            // FOR-DEBUG int expect = sizeOf(type, @object) + buffer.position();

            if (@object != null)
            {
                buffer.put(type);
                helper.serialize(this, buffer, @object);
            }
            else
            {
                buffer.put(TERMINATOR_TYPE);
            }
            // FOR-DEBUG if (expect != buffer.position()) {
            // FOR-DEBUG 	throw new Error("Bad size");
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
                ByteBuffer slice = encode(data);

                buffer.putInt(slice.limit());
                buffer.put(slice);
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

        public void serialize(ByteBuffer buffer, Event @event)
        {
            byte type = HiddenAccess.getSerializationType(@event);

            serialize(buffer, type, @event);
        }

        public object deserializeAny(ByteBuffer buffer)
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
                res = decoder.decode(slice).toString();
            }
            catch (CharacterCodingException e)
            {
                throw new Error("Bad character", e);
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

        public int[] deserializeIntegers(ByteBuffer buffer)
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
            return (data == null) ? SIZEOF_INT : SIZEOF_INT + encode(data).limit();
        }

        public int sizeOf(byte[] data)
        {
            return (data == null) ? SIZEOF_INT : SIZEOF_INT + data.Length;
        }

        public int sizeOf(byte type, object @object)
        {
            return getHelper(type).sizeOf(this, @object);
        }

        public int sizeOf(Event @event)
        {
            return sizeOf(HiddenAccess.getSerializationType(@event), @event);
        }

        private ISerializableHelper getHelper(byte type)
        {
            ISerializableHelper helper = typeToHelper[type];

            if (helper == null)
            {
                throw new Error("Unknownd or unsupported data type");
            }
            return helper;
        }

        private ByteBuffer encode(string data)
        {
            ByteBuffer slice = ByteBuffer.allocate(data.Length*3);

            encoder.encode(CharBuffer.wrap(data), slice, false);
            slice.flip();
            return slice;
        }

        private void fillBuffer(InputStream source, ByteBuffer buffer)
        {
            int res;

            do
            {
                res = source.read(buffer.array(), buffer.position(), buffer.remaining());
                if (res == -1)
                {
                    throw new IOException("Unexpected EOF");
                }
                buffer.position(buffer.position() + res);
            } while (buffer.remaining() != 0);
        }

        public static void register(Class realClass, byte type)
        {
            try
            {
                if (realClass != null)
                {
                    Method method = realClass.getDeclaredMethod("createHiddenSerializer");

                    method.setAccessible(true);
                    var helper = (ISerializableHelper) method.invoke(null);

                    method.setAccessible(false);
                    typeToHelper[type] = helper;
                    classToType.put(realClass, type);
                }
            }
            catch (NoSuchMethodException e)
            {
                Logger.logError(e);
            }
            catch (InvocationTargetException e)
            {
                Logger.logError(e);
            }
            catch (IllegalAccessException e)
            {
                Logger.logError(e);
            }
        }

        public static ByteBuffer serializeToBuffer(object src)
        {
            var rbs = new RbSerializer();
            var type = (byte) classToType.get(src.GetType());

            return rbs.serialize(type, src);
        }

        public static T deserializeFromBuffer<T>(ByteBuffer buffer)
        {
            var rbs = new RbSerializer();
            object res = rbs.deserialize(buffer);

            return (T) res;
        }

        public static object deepCopy(byte type, Object src)
        {
            var @out = new ByteArrayOutputStream(1024);
            var rbs = new RbSerializer();

            try
            {
                rbs.serialize(@out, type, src);
                var @in = new ByteArrayInputStream(@out.toByteArray());

                return rbs.deserialize(@in);
            }
            catch (IOException e)
            {
                Logger.logError(e);
                return null;
            }
        }
    }
}