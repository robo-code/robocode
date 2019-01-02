/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using net.sf.robocode.nio;
using net.sf.robocode.serialization;

namespace net.sf.robocode.dotnet.peer
{
    public class BulletStatus
    {
        public readonly int bulletId;
        public readonly bool isActive;
        public readonly String victimName;
        public readonly double x;
        public readonly double y;

        public BulletStatus(int bulletId, double x, double y, String victimName, bool isActive)
        {
            this.bulletId = bulletId;
            this.x = x;
            this.y = y;
            this.isActive = isActive;
            this.victimName = victimName;
        }

        // ReSharper disable UnusedMember.Local
        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        #region Nested type: SerializableHelper

        private class SerializableHelper : ISerializableHelperN
        {
            #region ISerializableHelperN Members

            public int sizeOf(RbSerializerN serializer, Object obje)
            {
                var obj = (BulletStatus) obje;

                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_INT + serializer.sizeOf(obj.victimName)
                       + RbSerializerN.SIZEOF_BOOL + 2*RbSerializerN.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, Object obje)
            {
                var obj = (BulletStatus) obje;

                serializer.serialize(buffer, obj.bulletId);
                serializer.serialize(buffer, obj.victimName);
                serializer.serialize(buffer, obj.isActive);
                serializer.serialize(buffer, obj.x);
                serializer.serialize(buffer, obj.y);
            }

            public Object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                int bulletId = buffer.getInt();
                String victimName = serializer.deserializeString(buffer);
                bool isActive = serializer.deserializeBoolean(buffer);
                double x = buffer.getDouble();
                double y = buffer.getDouble();

                return new BulletStatus(bulletId, x, y, victimName, isActive);
            }

            #endregion
        }

        #endregion
    }
}