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
    [Serializable]
    public class BulletCommand
    {
        private readonly int bulletId;
        private readonly double fireAssistAngle;
        private readonly bool fireAssistValid;
        private readonly double power;

        public BulletCommand(double power, bool fireAssistValid, double fireAssistAngle, int bulletId)
        {
            this.fireAssistValid = fireAssistValid;
            this.fireAssistAngle = fireAssistAngle;
            this.bulletId = bulletId;
            this.power = power;
        }

        public bool isFireAssistValid()
        {
            return fireAssistValid;
        }

        public int getBulletId()
        {
            return bulletId;
        }

        public double getPower()
        {
            return power;
        }

        public double getFireAssistAngle()
        {
            return fireAssistAngle;
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
                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_DOUBLE + RbSerializerN.SIZEOF_BOOL
                       + RbSerializerN.SIZEOF_DOUBLE + RbSerializerN.SIZEOF_INT;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, Object obje)
            {
                var obj = (BulletCommand) obje;

                serializer.serialize(buffer, obj.power);
                serializer.serialize(buffer, obj.fireAssistValid);
                serializer.serialize(buffer, obj.fireAssistAngle);
                serializer.serialize(buffer, obj.bulletId);
            }

            public Object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                double power = buffer.getDouble();
                bool fireAssistValid = serializer.deserializeBoolean(buffer);
                double fireAssistAngle = buffer.getDouble();
                int bulletId = buffer.getInt();

                return new BulletCommand(power, fireAssistValid, fireAssistAngle, bulletId);
            }

            #endregion
        }

        #endregion
    }
}