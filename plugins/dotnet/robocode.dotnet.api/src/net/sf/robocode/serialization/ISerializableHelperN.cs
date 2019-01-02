/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using net.sf.robocode.nio;

namespace net.sf.robocode.serialization
{
#pragma warning disable 1591
    /// <exclude/>
    public interface ISerializableHelperN
    {
        int sizeOf(RbSerializerN serializer, object obj);
        void serialize(RbSerializerN serializer, ByteBuffer buffer, object obj);
        object deserialize(RbSerializerN serializer, ByteBuffer buffer);
    }
}

//happy