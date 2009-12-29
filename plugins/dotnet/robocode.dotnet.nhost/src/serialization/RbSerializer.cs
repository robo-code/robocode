#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System.IO;
using java.lang;
using net.sf.jni4net.nio;
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