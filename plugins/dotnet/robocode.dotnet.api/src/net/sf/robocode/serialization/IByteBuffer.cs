using System;

namespace java.nio
{
    public interface IByteBuffer
    {
        byte get();

        IByteBuffer get(byte[] par0);

        IByteBuffer put(byte[] par0);

        IByteBuffer put(IByteBuffer par0);

        IByteBuffer put(byte par0);


        IByteBuffer putChar(char par0);


        IByteBuffer putInt(int par0);

        IByteBuffer putUInt(uint par0);


        IByteBuffer putLong(long par0);

        IByteBuffer putFloat(float par0);


        IByteBuffer putDouble(double par0);

        char getChar();

        int getInt();

        long getLong();

        float getFloat();

        double getDouble();

        IByteBuffer duplicate();

        IByteBuffer slice();

        IByteBuffer limit(int par0);

        int limit();

        IByteBuffer clear();

        int arrayOffset();

        bool hasArray();

        bool isDirect();

        int position();

        IByteBuffer position(int par0);

        int remaining();

        int capacity();

        IByteBuffer flip();

        bool hasRemaining();

        bool isReadOnly();

        IByteBuffer mark();

        IByteBuffer reset();

        IByteBuffer rewind();
    }


    public class ByteBufferFactory
    {
        public static IByteBuffer wrap(byte[] par0, int par1, int par2)
        {
            throw new NotImplementedException();
        }

        public static IByteBuffer wrap(byte[] par0)
        {
            throw new NotImplementedException();
        }

        public static IByteBuffer allocate(int par0)
        {
            throw new NotImplementedException();
        }

        public static IByteBuffer allocateDirect(int par0)
        {
            throw new NotImplementedException();
        }
    }
}

