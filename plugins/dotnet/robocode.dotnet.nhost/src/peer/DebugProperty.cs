using System;
using java.nio;
using net.sf.robocode.serialization;

namespace net.sf.robocode.dotnet.peer
{
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

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, Object obje)
            {
                var obj = (DebugProperty) obje;

                return RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.key) + serializer.sizeOf(obj.value);
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, Object obje)
            {
                var obj = (DebugProperty) obje;

                serializer.serialize(buffer, obj.key);
                serializer.serialize(buffer, obj.value);
            }

            public Object deserialize(RbSerializer serializer, IByteBuffer buffer)
            {
                String key = serializer.deserializeString(buffer);
                String value = serializer.deserializeString(buffer);

                return new DebugProperty(key, value);
            }
        }
    }
}
