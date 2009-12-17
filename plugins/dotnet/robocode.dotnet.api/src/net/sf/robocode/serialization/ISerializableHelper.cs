/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
using net.sf.robocode.nio;

namespace net.sf.robocode.serialization
{
    /// <summary>
    /// @author Pavel Savara (original)
    /// </summary>
    public interface ISerializableHelper
    {
        int sizeOf(RbnSerializer serializer, object obj);
        void serialize(RbnSerializer serializer, ByteBuffer buffer, object obj);
        object deserialize(RbnSerializer serializer, ByteBuffer buffer);
    }
}
//happy