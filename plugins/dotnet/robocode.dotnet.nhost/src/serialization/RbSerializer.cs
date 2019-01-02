/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

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

        public DirectByteBuffer serializeN(byte type, Object obj)
        {
            int length = sizeOf(type, obj);

            // header
            int size = RbSerializerN.SIZEOF_INT*3 + length;
            var sharedBuffer = new byte[size];
            var buffer = new DirectByteBuffer(sharedBuffer);

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
            DirectByteBuffer commandBuffer = serializeN(type, javaObject);
            ByteBuffer byteBuffer = ByteBuffer.wrap(commandBuffer.GetSharedBuffer());
            return RbSerializerN.deserializeFromBuffer<T>(byteBuffer);
        }
    }
}