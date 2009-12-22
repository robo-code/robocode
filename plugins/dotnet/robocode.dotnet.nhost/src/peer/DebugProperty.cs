using System;
using net.sf.robocode.serialization;

namespace net.sf.robocode.dotnet.peer
{
    [Serializable]
    public class DebugProperty
    {
        public DebugProperty()
        {
        }

        public DebugProperty(String key, String value)
        {
            setKey(key);
            setValue(value);
        }

        private String key;
        private String value;


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

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, Object obje)
            {
                var obj = (DebugProperty) obje;

                return RbSerializerN.SIZEOF_TYPEINFO + serializer.sizeOf(obj.key) + serializer.sizeOf(obj.value);
            }

            public void serialize(RbSerializerN serializer, nio.ByteBuffer buffer, Object obje)
            {
                var obj = (DebugProperty) obje;

                serializer.serialize(buffer, obj.key);
                serializer.serialize(buffer, obj.value);
            }

            public Object deserialize(RbSerializerN serializer, nio.ByteBuffer buffer)
            {
                String key = serializer.deserializeString(buffer);
                String value = serializer.deserializeString(buffer);

                return new DebugProperty(key, value);
            }
        }
    }
}
