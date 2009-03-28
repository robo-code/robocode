using System;
using System.IO;
using net.sf.robocode.serialization;

namespace robocode.dotnet.ncore.peer
{
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

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        #region Nested type: SerializableHelper

        private class SerializableHelper : ISerializableHelper
        {
            #region ISerializableHelper Members

            public int sizeOf(RbSerializer serializer, Object obj1)
            {
                var obj = (DebugProperty) obj1;

                return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.key) + serializer.sizeOf(obj.value);
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                var obj2 = (DebugProperty) obj;

                serializer.serialize(bw, obj2.key);
                serializer.serialize(bw, obj2.value);
            }

            public Object deserialize(RbSerializer serializer, BinaryReader br)
            {
                String key = serializer.deserializeString(br);
                String value = serializer.deserializeString(br);

                return new DebugProperty(key, value);
            }

            #endregion
        }

        #endregion
    }
}