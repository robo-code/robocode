using System;

namespace java.nio
{
    public interface ByteBuffer
    {
        byte get(int par0);

        byte get();

        ByteBuffer get(byte[] par0);

        ByteBuffer get(byte[] par0, int par1, int par2);

        ByteBuffer put(byte[] par0);

        ByteBuffer put(int par0, byte par1);

        ByteBuffer put(ByteBuffer par0);

        ByteBuffer put(byte par0);

        ByteBuffer put(sbyte par0);

        ByteBuffer put(byte[] par0, int par1, int par2);

        int compareTo(ByteBuffer par0);

        short getShort();

        short getShort(int par0);

        ByteBuffer putShort(short par0);

        ByteBuffer putShort(int par0, short par1);

        char getChar();

        char getChar(int par0);

        ByteBuffer putChar(char par0);

        ByteBuffer putChar(int par0, char par1);

        int getInt(int par0);

        int getInt();

        ByteBuffer putInt(int par0, int par1);

        ByteBuffer putInt(int par0);

        ByteBuffer putUInt(uint par0);

        long getLong(int par0);

        long getLong();

        ByteBuffer putLong(int par0, long par1);

        ByteBuffer putLong(long par0);

        float getFloat(int par0);

        float getFloat();

        ByteBuffer putFloat(float par0);

        ByteBuffer putFloat(int par0, float par1);

        double getDouble();

        double getDouble(int par0);

        ByteBuffer putDouble(double par0);

        ByteBuffer putDouble(int par0, double par1);

        ByteBuffer duplicate();


        ByteBuffer slice();


        ByteBuffer limit(int par0);
        int limit();
        ByteBuffer clear();
        int arrayOffset();
        bool hasArray();
        bool isDirect();
        int position();
        ByteBuffer position(int par0);
        int remaining();
        int capacity();
        ByteBuffer flip();
        bool hasRemaining();
        bool isReadOnly();
        ByteBuffer mark();
        ByteBuffer reset();
        ByteBuffer rewind();
    }

    public class ByteBufferFactory
    {
        public static ByteBuffer wrap(byte[] par0, int par1, int par2)
        {
            throw new NotImplementedException();
        }

        public static ByteBuffer wrap(byte[] par0)
        {
            throw new NotImplementedException();
        }

        public static ByteBuffer allocate(int par0)
        {
            throw new NotImplementedException();
        }

        public static ByteBuffer allocateDirect(int par0)
        {
            throw new NotImplementedException();
        }
        
    }

}

