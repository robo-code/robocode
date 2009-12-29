#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;

// ReSharper disable InconsistentNaming

namespace net.sf.robocode.nio
{
    /**
     * A read/write HeapByteBuffer.
     */

    internal class HeapByteBuffer : ByteBuffer
    {
        internal HeapByteBuffer(int cap, int lim)
            : base(-1, 0, lim, cap, new byte[cap], 0)
        {
        }

        internal HeapByteBuffer(byte[] buf, int off, int len)
            : base(-1, off, off + len, buf.Length, buf, 0)
        {
        }

        protected HeapByteBuffer(byte[] buf,
                                 int mark, int pos, int lim, int cap,
                                 int off)
            : base(mark, pos, lim, cap, buf, off)
        {
        }

        public override ByteBuffer slice()
        {
            return new HeapByteBuffer(hb,
                                      -1,
                                      0,
                                      remaining(),
                                      remaining(),
                                      position() + _offset);
        }

        public override ByteBuffer duplicate()
        {
            return new HeapByteBuffer(hb,
                                      markValue(),
                                      position(),
                                      limit(),
                                      capacity(),
                                      _offset);
        }

        public override ByteBuffer asReadOnlyBuffer()
        {
            /*return new HeapByteBufferR(hb,
				     this.markValue(),
				     this.position(),
				     this.limit(),
				     this.capacity(),
                     _offset);*/


            throw new NotImplementedException();
        }


        protected int ix(int i)
        {
            return i + _offset;
        }

        public override byte get()
        {
            return hb[ix(nextGetIndex())];
        }

        public override byte get(int i)
        {
            return hb[ix(checkIndex(i))];
        }

        public override ByteBuffer get(byte[] dst, int offset, int length)
        {
            checkBounds(offset, length, dst.Length);
            if (length > remaining())
                throw new BufferUnderflowException();
            Array.Copy(hb, ix(position()), dst, offset, length);
            position(position() + length);
            return this;
        }

        public override bool isDirect()
        {
            return false;
        }


        public override bool isReadOnly()
        {
            return false;
        }

        public override ByteBuffer put(byte x)
        {
            hb[ix(nextPutIndex())] = x;
            return this;
        }

        public override ByteBuffer put(int i, byte x)
        {
            hb[ix(checkIndex(i))] = x;
            return this;
        }

        public override ByteBuffer put(byte[] src, int offset, int length)
        {
            checkBounds(offset, length, src.Length);
            if (length > remaining())
                throw new BufferOverflowException();
            Array.Copy(src, offset, hb, ix(position()), length);
            position(position() + length);
            return this;
        }

        public override ByteBuffer put(ByteBuffer src)
        {
            if (src is HeapByteBuffer)
            {
                if (src == this)
                    throw new ArgumentException();
                var sb = (HeapByteBuffer) src;
                int n = sb.remaining();
                if (n > remaining())
                    throw new BufferOverflowException();
                Array.Copy(sb.hb, sb.ix(sb.position()),
                           hb, ix(position()), n);
                sb.position(sb.position() + n);
                position(position() + n);
            }
            else if (src.isDirect())
            {
                int n = src.remaining();
                if (n > remaining())
                    throw new BufferOverflowException();
                src.get(hb, ix(position()), n);
                position(position() + n);
            }
            else
            {
                base.put(src);
            }
            return this;
        }

        public override ByteBuffer compact()
        {
            Array.Copy(hb, ix(position()), hb, ix(0), remaining());
            position(remaining());
            limit(capacity());
            discardMark();
            return this;
        }


        internal override byte _get(int i)
        {
            return hb[i];
        }

        internal override void _put(int i, byte b)
        {
            hb[i] = b;
        }

        // char


        public override char getChar()
        {
            return BitConverter.ToChar(hb, ix(nextGetIndex(2)));
        }

        public override char getChar(int i)
        {
            return BitConverter.ToChar(hb, ix(checkIndex(i, 2)));
        }


        public override ByteBuffer putChar(char x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(nextPutIndex(2));
            Array.Copy(bytes, 0, hb, index, 2);
            return this;
        }

        public override ByteBuffer putChar(int i, char x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(checkIndex(i, 2));
            Array.Copy(bytes, 0, hb, index, 2);
            return this;
        }

        public override Buffer asCharBuffer()
        {
            /*int size = this.remaining() >> 1;
	int off = offset + position();
	return (bigEndian
		? (CharBuffer)(new ByteBufferAsCharBufferB(this,
							       -1,
							       0,
							       size,
							       size,
							       off))
		: (CharBuffer)(new ByteBufferAsCharBufferL(this,
							       -1,
							       0,
							       size,
							       size,
							       off)));
     */
            throw new NotImplementedException();
        }


        // short


        public override short getShort()
        {
            return BitConverter.ToInt16(hb, ix(nextGetIndex(2)));
        }

        public override short getShort(int i)
        {
            return BitConverter.ToInt16(hb, ix(checkIndex(i, 2)));
        }


        public override ByteBuffer putShort(short x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(nextPutIndex(2));
            Array.Copy(bytes, 0, hb, index, 2);
            return this;
        }

        public override ByteBuffer putShort(int i, short x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(checkIndex(i, 2));
            Array.Copy(bytes, 0, hb, index, 2);
            return this;
        }

        public override Buffer asShortBuffer()
        {
            /*int size = this.remaining() >> 1;
	int off = offset + position();
	return (bigEndian
		? (ShortBuffer)(new ByteBufferAsShortBufferB(this,
								 -1,
								 0,
								 size,
								 size,
								 off))
		: (ShortBuffer)(new ByteBufferAsShortBufferL(this,
								 -1,
								 0,
								 size,
								 size,
								 off)));*/
            throw new NotImplementedException();
        }


        // int


        public override int getInt()
        {
            return BitConverter.ToInt32(hb, ix(nextGetIndex(4)));
        }

        public override int getInt(int i)
        {
            return BitConverter.ToInt32(hb, ix(checkIndex(i, 4)));
        }


        public override ByteBuffer putInt(int x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(nextPutIndex(4));
            Array.Copy(bytes, 0, hb, index, 4);
            return this;
        }

        public override ByteBuffer putInt(int i, int x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(checkIndex(i, 4));
            Array.Copy(bytes, 0, hb, index, 4);
            return this;
        }

        public override Buffer asIntBuffer()
        {
            /*int size = this.remaining() >> 2;
	int off = offset + position();
	return (bigEndian
		? (IntBuffer)(new ByteBufferAsIntBufferB(this,
							     -1,
							     0,
							     size,
							     size,
							     off))
		: (IntBuffer)(new ByteBufferAsIntBufferL(this,
							     -1,
							     0,
							     size,
							     size,
							     off)));*/
            throw new NotImplementedException();
        }


        // long


        public override long getLong()
        {
            return BitConverter.ToInt64(hb, ix(nextGetIndex(8)));
        }

        public override long getLong(int i)
        {
            return BitConverter.ToInt64(hb, ix(checkIndex(i, 8)));
        }


        public override ByteBuffer putLong(long x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(nextPutIndex(8));
            Array.Copy(bytes, 0, hb, index, 8);
            return this;
        }

        public override ByteBuffer putLong(int i, long x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(checkIndex(i, 8));
            Array.Copy(bytes, 0, hb, index, 8);
            return this;
        }

        public override Buffer asLongBuffer()
        {
            /*int size = this.remaining() >> 3;
	int off = offset + position();
	return (bigEndian
		? (LongBuffer)(new ByteBufferAsLongBufferB(this,
							       -1,
							       0,
							       size,
							       size,
							       off))
		: (LongBuffer)(new ByteBufferAsLongBufferL(this,
							       -1,
							       0,
							       size,
							       size,
							       off)));*/
            throw new NotImplementedException();
        }


        // float


        public override float getFloat()
        {
            return BitConverter.ToSingle(hb, ix(nextGetIndex(4)));
        }

        public override float getFloat(int i)
        {
            return BitConverter.ToSingle(hb, ix(checkIndex(i, 4)));
        }


        public override ByteBuffer putFloat(float x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(nextPutIndex(4));
            Array.Copy(bytes, 0, hb, index, 4);
            return this;
        }

        public override ByteBuffer putFloat(int i, float x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(checkIndex(i, 4));
            Array.Copy(bytes, 0, hb, index, 4);
            return this;
        }

        public override Buffer asFloatBuffer()
        {
            /*int size = this.remaining() >> 2;
	int off = offset + position();
	return (bigEndian
		? (FloatBuffer)(new ByteBufferAsFloatBufferB(this,
								 -1,
								 0,
								 size,
								 size,
								 off))
		: (FloatBuffer)(new ByteBufferAsFloatBufferL(this,
								 -1,
								 0,
								 size,
								 size,
								 off)));*/
            throw new NotImplementedException();
        }


        // double


        public override double getDouble()
        {
            return BitConverter.ToDouble(hb, ix(nextGetIndex(8)));
        }

        public override double getDouble(int i)
        {
            return BitConverter.ToDouble(hb, ix(checkIndex(i, 8)));
        }


        public override ByteBuffer putDouble(double x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(nextPutIndex(8));
            Array.Copy(bytes, 0, hb, index, 8);
            return this;
        }

        public override ByteBuffer putDouble(int i, double x)
        {
            byte[] bytes = BitConverter.GetBytes(x);
            int index = ix(checkIndex(i, 8));
            Array.Copy(bytes, 0, hb, index, 8);
            return this;
        }

        public override Buffer asDoubleBuffer()
        {
            /*int size = this.remaining() >> 3;
	int off = offset + position();
	return (bigEndian
		? (DoubleBuffer)(new ByteBufferAsDoubleBufferB(this,
								   -1,
								   0,
								   size,
								   size,
								   off))
		: (DoubleBuffer)(new ByteBufferAsDoubleBufferL(this,
								   -1,
								   0,
								   size,
								   size,
								   off)));*/
            throw new NotImplementedException();
        }
    }
}