﻿#region Copyright 2000-2008 Sun Microsystems, Inc.  All Rights Reserved.

/*
 * Copyright 2000-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

#endregion
#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Security.Permissions;
using net.sf.robocode.security;

// ReSharper disable InconsistentNaming

namespace net.sf.robocode.nio
{
#pragma warning disable 1591
#pragma warning disable 1570

    /*
     * A container for data of a specific primitive type.
     *
     * <p> A buffer is a linear, finite sequence of elements of a specific
     * primitive type.  Aside from its content, the essential properties of a
     * buffer are its capacity, limit, and position: </p>
     *
     * <blockquote>
     *
     *   <p> A buffer's <i>capacity</i> is the number of elements it contains.  The
     *   capacity of a buffer is never negative and never changes.  </p>
     *
     *   <p> A buffer's <i>limit</i> is the index of the first element that should
     *   not be read or written.  A buffer's limit is never negative and is never
     *   greater than its capacity.  </p>
     *
     *   <p> A buffer's <i>position</i> is the index of the next element to be
     *   read or written.  A buffer's position is never negative and is never
     *   greater than its limit.  </p>
     *
     * </blockquote>
     *
     * <p> There is one subclass of this class for each non-boolean primitive type.
     *
     * 
     * <h4> Transferring data </h4>
     *
     * <p> Each subclass of this class defines two categories of <i>get</i> and
     * <i>put</i> operations: </p>
     *
     * <blockquote>
     *
     *   <p> <i>Relative</i> operations read or write one or more elements starting
     *   at the current position and then increment the position by the number of
     *   elements transferred.  If the requested transfer exceeds the limit then a
     *   relative <i>get</i> operation throws a {@link BufferUnderflowException}
     *   and a relative <i>put</i> operation throws a {@link
     *   BufferOverflowException}; in either case, no data is transferred.  </p>
     *
     *   <p> <i>Absolute</i> operations take an explicit element index and do not
     *   affect the position.  Absolute <i>get</i> and <i>put</i> operations throw
     *   an {@link IndexOutOfBoundsException} if the index argument exceeds the
     *   limit.  </p>
     *
     * </blockquote>
     *
     * <p> Data may also, of course, be transferred in to or out of a buffer by the
     * I/O operations of an appropriate channel, which are always relative to the
     * current position.
     *
     *
     * <h4> Marking and resetting </h4>
     *
     * <p> A buffer's <i>mark</i> is the index to which its position will be reset
     * when the {@link #reset reset} method is invoked.  The mark is not always
     * defined, but when it is defined it is never negative and is never greater
     * than the position.  If the mark is defined then it is discarded when the
     * position or the limit is adjusted to a value smaller than the mark.  If the
     * mark is not defined then invoking the {@link #reset reset} method causes an
     * {@link InvalidMarkException} to be thrown.
     *
     *
     * <h4> Invariants </h4>
     *
     * <p> The following invariant holds for the mark, position, limit, and
     * capacity values:
     *
     * <blockquote>
     *     <tt>0</tt> <tt>&lt;=</tt>
     *     <i>mark</i> <tt>&lt;=</tt>
     *     <i>position</i> <tt>&lt;=</tt>
     *     <i>limit</i> <tt>&lt;=</tt>
     *     <i>capacity</i>
     * </blockquote>
     *
     * <p> A newly-created buffer always has a position of zero and a mark that is
     * undefined.  The initial limit may be zero, or it may be some other value
     * that depends upon the type of the buffer and the manner in which it is
     * constructed.  The initial content of a buffer is, in general,
     * undefined.
     *
     *
     * <h4> Clearing, flipping, and rewinding </h4>
     *
     * <p> In addition to methods for accessing the position, limit, and capacity
     * values and for marking and resetting, this class also defines the following
     * operations upon buffers:
     *
     * <ul>
     *
     *   <li><p> {@link #clear} makes a buffer ready for a new sequence of
     *   channel-read or relative <i>put</i> operations: It sets the limit to the
     *   capacity and the position to zero.  </p></li>
     *
     *   <li><p> {@link #flip} makes a buffer ready for a new sequence of
     *   channel-write or relative <i>get</i> operations: It sets the limit to the
     *   current position and then sets the position to zero.  </p></li>
     *
     *   <li><p> {@link #rewind} makes a buffer ready for re-reading the data that
     *   it already contains: It leaves the limit unchanged and sets the position
     *   to zero.  </p></li>
     *
     * </ul>
     *
     *
     * <h4> Read-only buffers </h4>
     *
     * <p> Every buffer is readable, but not every buffer is writable.  The
     * mutation methods of each buffer class are specified as <i>optional
     * operations</i> that will throw a {@link ReadOnlyBufferException} when
     * invoked upon a read-only buffer.  A read-only buffer does not allow its
     * content to be changed, but its mark, position, and limit values are mutable.
     * Whether or not a buffer is read-only may be determined by invoking its
     * {@link #isReadOnly isReadOnly} method.
     *
     *
     * <h4> Thread safety </h4>
     *
     * <p> Buffers are not safe for use by multiple concurrent threads.  If a
     * buffer is to be used by more than one thread then access to the buffer
     * should be controlled by appropriate synchronization.
     *
     *
     * <h4> Invocation chaining </h4>
     *
     * <p> Methods in this class that do not otherwise have a value to return are
     * specified to return the buffer upon which they are invoked.  This allows
     * method invocations to be chained; for example, the sequence of statements
     *
     * <blockquote><pre>
     * b.flip();
     * b.position(23);
     * b.limit(42);</pre></blockquote>
     *
     * can be replaced by the single, more compact statement
     *
     * <blockquote><pre>
     * b.flip().position(23).limit(42);</pre></blockquote>
     *
     *
     * @author Mark Reinhold
     * @author JSR-51 Expert Group
     * @version 1.35, 06/08/11
     * @since 1.4
     */

    /// <exclude/>
    [RobocodeInternalPermission(SecurityAction.LinkDemand)]
    public abstract class Buffer
    {
        // Invariants: mark <= position <= limit <= capacity
        private readonly int _capacity;
        private int _limit;
        private int _mark = -1;
        private int _position;

        // Creates a new buffer with the given mark, position, limit, and capacity,
        // after checking invariants.
        //
        protected Buffer(int mark, int pos, int lim, int cap)
        {
            // package-private
            if (cap < 0)
                throw new ArgumentException();
            _capacity = cap;
            limit(lim);
            position(pos);
            if (mark >= 0)
            {
                if (mark > pos)
                    throw new ArgumentException();
                _mark = mark;
            }
        }

        /*
         * Returns this buffer's capacity. </p>
         *
         * @return  The capacity of this buffer
         */
        public int capacity()
        {
            return _capacity;
        }

        /*
         * Returns this buffer's position. </p>
         *
         * @return  The position of this buffer
         */
        public int position()
        {
            return _position;
        }

        /*
         * Sets this buffer's position.  If the mark is defined and larger than the
         * new position then it is discarded. </p>
         *
         * @param  newPosition
         *         The new position value; must be non-negative
         *         and no larger than the current limit
         *
         * @return  This buffer
         *
         * @throws  ArgumentException
         *          If the preconditions on <tt>newPosition</tt> do not hold
         */
        public Buffer position(int newPosition)
        {
            if ((newPosition > _limit) || (newPosition < 0))
                throw new ArgumentException();
            _position = newPosition;
            if (_mark > _position) _mark = -1;
            return this;
        }

        /*
         * Returns this buffer's limit. </p>
         *
         * @return  The limit of this buffer
         */
        public int limit()
        {
            return _limit;
        }

        /*
         * Sets this buffer's limit.  If the position is larger than the new limit
         * then it is set to the new limit.  If the mark is defined and larger than
         * the new limit then it is discarded. </p>
         *
         * @param  newLimit
         *         The new limit value; must be non-negative
         *         and no larger than this buffer's capacity
         *
         * @return  This buffer
         *
         * @throws  ArgumentException
         *          If the preconditions on <tt>newLimit</tt> do not hold
         */
        public Buffer limit(int newLimit)
        {
            if ((newLimit > _capacity) || (newLimit < 0))
                throw new ArgumentException();
            _limit = newLimit;
            if (_position > _limit) _position = _limit;
            if (_mark > _limit) _mark = -1;
            return this;
        }

        /*
         * Sets this buffer's mark at its position. </p>
         *
         * @return  This buffer
         */
        public Buffer mark()
        {
            _mark = _position;
            return this;
        }

        /*
         * Resets this buffer's position to the previously-marked position.
         *
         * <p> Invoking this method neither changes nor discards the mark's
         * value. </p>
         *
         * @return  This buffer
         *
         * @throws  InvalidMarkException
         *          If the mark has not been set
         */
        public Buffer reset()
        {
            int m = _mark;
            if (m < 0)
                throw new InvalidMarkException();
            _position = m;
            return this;
        }

        /*
         * Clears this buffer.  The position is set to zero, the limit is set to
         * the capacity, and the mark is discarded.
         *
         * <p> Invoke this method before using a sequence of channel-read or
         * <i>put</i> operations to fill this buffer.  For example:
         *
         * <blockquote><pre>
         * buf.clear();     // Prepare buffer for reading
         * in.read(buf);    // Read data</pre></blockquote>
         *
         * <p> This method does not actually erase the data in the buffer, but it
         * is named as if it did because it will most often be used in situations
         * in which that might as well be the case. </p>
         *
         * @return  This buffer
         */
        public Buffer clear()
        {
            _position = 0;
            _limit = _capacity;
            _mark = -1;
            return this;
        }

        /*
         * Flips this buffer.  The limit is set to the current position and then
         * the position is set to zero.  If the mark is defined then it is
         * discarded.
         *
         * <p> After a sequence of channel-read or <i>put</i> operations, invoke
         * this method to prepare for a sequence of channel-write or relative
         * <i>get</i> operations.  For example:
         *
         * <blockquote><pre>
         * buf.put(magic);    // Prepend header
         * in.read(buf);      // Read data into rest of buffer
         * buf.flip();        // Flip buffer
         * out.write(buf);    // Write header + data to channel</pre></blockquote>
         *
         * <p> This method is often used in conjunction with the {@link
         * java.nio.ByteBuffer#compact compact} method when transferring data from
         * one place to another.  </p>
         *
         * @return  This buffer
         */
        public Buffer flip()
        {
            _limit = _position;
            _position = 0;
            _mark = -1;
            return this;
        }

        /*
         * Rewinds this buffer.  The position is set to zero and the mark is
         * discarded.
         *
         * <p> Invoke this method before a sequence of channel-write or <i>get</i>
         * operations, assuming that the limit has already been set
         * appropriately.  For example:
         *
         * <blockquote><pre>
         * out.write(buf);    // Write remaining data
         * buf.rewind();      // Rewind buffer
         * buf.get(array);    // Copy data into array</pre></blockquote>
         *
         * @return  This buffer
         */
        public Buffer rewind()
        {
            _position = 0;
            _mark = -1;
            return this;
        }

        /*
         * Returns the number of elements between the current position and the
         * limit. </p>
         *
         * @return  The number of elements remaining in this buffer
         */
        public int remaining()
        {
            return _limit - _position;
        }

        /*
         * Tells whether there are any elements between the current position and
         * the limit. </p>
         *
         * @return  <tt>true</tt> if, and only if, there is at least one element
         *          remaining in this buffer
         */
        public bool hasRemaining()
        {
            return _position < _limit;
        }

        /*
         * Tells whether or not this buffer is read-only. </p>
         *
         * @return  <tt>true</tt> if, and only if, this buffer is read-only
         */
        public abstract bool isReadOnly();


        // -- Package-private methods for bounds checking, etc. --

        /*
         * Checks the current position against the limit, throwing a {@link
         * BufferUnderflowException} if it is not smaller than the limit, and then
         * increments the position. </p>
         *
         * @return  The current position value, before it is incremented
         */
        internal int nextGetIndex()
        {
            // package-private
            if (_position >= _limit)
                throw new BufferUnderflowException();
            return _position++;
        }

        internal int nextGetIndex(int nb)
        {
            // package-private
            if (_limit - _position < nb)
                throw new BufferUnderflowException();
            int p = _position;
            _position += nb;
            return p;
        }

        /*
         * Checks the current position against the limit, throwing a {@link
         * BufferOverflowException} if it is not smaller than the limit, and then
         * increments the position. </p>
         *
         * @return  The current position value, before it is incremented
         */
        internal int nextPutIndex()
        {
            // package-private
            if (_position >= _limit)
                throw new BufferOverflowException();
            return _position++;
        }

        internal int nextPutIndex(int nb)
        {
            // package-private
            if (_limit - _position < nb)
                throw new BufferOverflowException();
            int p = _position;
            _position += nb;
            return p;
        }

        /*
         * Checks the given index against the limit, throwing an {@link
         * IndexOutOfBoundsException} if it is not smaller than the limit
         * or is smaller than zero.
         */
        internal int checkIndex(int i)
        {
            // package-private
            if ((i < 0) || (i >= _limit))
                throw new IndexOutOfRangeException();
            return i;
        }

        internal int checkIndex(int i, int nb)
        {
            // package-private
            if ((i < 0) || (nb > _limit - i))
                throw new IndexOutOfRangeException();
            return i;
        }

        internal int markValue()
        {
            // package-private
            return _mark;
        }

        internal void discardMark()
        {
            // package-private
            _mark = -1;
        }

        internal static void checkBounds(int off, int len, int size)
        {
            // package-private
            if ((off | len | (off + len) | (size - (off + len))) < 0)
                throw new IndexOutOfRangeException();
        }
    }
#pragma warning restore 1591
}

// ReSharper restore InconsistentNaming