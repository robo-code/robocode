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
    public class TeamMessage
    {
        public byte[] message;
        public String recipient;
        public String sender;

        public TeamMessage(String sender, String recipient, byte[] message)
        {
            this.sender = sender;
            this.recipient = recipient;
            this.message = message;
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
                var obj = (TeamMessage) obje;
                int s = serializer.sizeOf(obj.sender);
                int r = serializer.sizeOf(obj.recipient);
                int m = serializer.sizeOf(obj.message);

                return RbSerializerN.SIZEOF_TYPEINFO + s + r + m;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, Object obje)
            {
                var obj = (TeamMessage) obje;

                serializer.serialize(buffer, obj.sender);
                serializer.serialize(buffer, obj.recipient);
                serializer.serialize(buffer, obj.message);
            }

            public Object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                String sender = serializer.deserializeString(buffer);
                String recipient = serializer.deserializeString(buffer);
                byte[] message = serializer.deserializeBytes(buffer);

                return new TeamMessage(sender, recipient, message);
            }

            #endregion
        }

        #endregion
    }
}