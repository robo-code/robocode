using System.IO;
using java.lang;
using net.sf.jni4net;
using net.sf.jni4net.nio;
using net.sf.robocode.core;
using net.sf.robocode.manager;
using ByteBuffer = net.sf.robocode.nio.ByteBuffer;

namespace net.sf.robocode.serialization
{
    partial class RbSerializer
    {
        private const int byteOrder = -1059135839; //0xC0DEDEA1
        private static int currentVersion;

        public static void Init(int version)
        {
            if (currentVersion == 0)
            {
                currentVersion = version;
            }
        }

        public ByteBufferClr serializeN(byte type, Object obj)
        {
            int length = sizeOf(type, obj);

            // header
            int size = SIZEOF_INT + SIZEOF_INT + SIZEOF_INT + length;
            var sharedBuffer = new byte[size];
            var buffer = new ByteBufferClr(sharedBuffer);

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

        public T ConvertJ2C<T>(byte type, Object javaObject)
        {
            ByteBufferClr commandBuffer = serializeN(type, javaObject);
            ByteBuffer byteBuffer = ByteBuffer.wrap(commandBuffer.GetSharedBuffer());
            return RbSerializerN.deserializeFromBuffer<T>(byteBuffer);
        }

       
    }
}
