using System;
using net.sf.robocode.nio;
using net.sf.robocode.serialization;

namespace net.sf.robocode.dotnet.peer
{
    [Serializable]
    public class TeamMessage
    {
        public TeamMessage(String sender, String recipient, byte[] message)
        {
            this.sender = sender;
            this.recipient = recipient;
            this.message = message;
        }

        public String sender;
        public String recipient;
        public byte[] message;

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, Object obje)
            {
                var obj = (TeamMessage) obje;
                int s = serializer.sizeOf(obj.sender);
                int r = serializer.sizeOf(obj.recipient);
                int m = serializer.sizeOf(obj.message);

                return RbSerializer.SIZEOF_TYPEINFO + s + r + m;
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
        }
    }
}
