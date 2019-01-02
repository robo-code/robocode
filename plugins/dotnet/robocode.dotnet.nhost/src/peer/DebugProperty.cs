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
    public class DebugProperty
    {
        private String key;
        private String value;

        public DebugProperty()
        {
        }

        public DebugProperty(String key, String value)
        {
            setKey(key);
            setValue(value);
        }


        public String getKey()
        {
            return key;
        }

        public void setKey(String key)
        {
            this.key = key;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
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
                var obj = (DebugProperty) obje;

                return RbSerializerN.SIZEOF_TYPEINFO + serializer.sizeOf(obj.key) + serializer.sizeOf(obj.value);
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, Object obje)
            {
                var obj = (DebugProperty) obje;

                serializer.serialize(buffer, obj.key);
                serializer.serialize(buffer, obj.value);
            }

            public Object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                String key = serializer.deserializeString(buffer);
                String value = serializer.deserializeString(buffer);

                return new DebugProperty(key, value);
            }

            #endregion
        }

        #endregion
    }
}