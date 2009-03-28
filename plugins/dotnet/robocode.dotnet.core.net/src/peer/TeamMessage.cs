using System;
using System.IO;
using net.sf.robocode.serialization;

namespace robocode.dotnet.core.net.peer
{
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

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        #region Nested type: SerializableHelper

        private class SerializableHelper : ISerializableHelper
        {
            #region ISerializableHelper Members

            public int sizeOf(RbSerializer serializer, Object object1)
            {
                var obj = (TeamMessage) object1;
                int s = serializer.sizeOf(obj.sender);
                int r = serializer.sizeOf(obj.recipient);
                int m = serializer.sizeOf(obj.message);

                return RbSerializer.SIZEOF_TYPEINFO + s + r + m;
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object object1)
            {
                var obj = (TeamMessage) object1;

                serializer.serialize(bw, obj.sender);
                serializer.serialize(bw, obj.recipient);
                serializer.serialize(bw, obj.message);
            }

            public Object deserialize(RbSerializer serializer, BinaryReader br)
            {
                String sender = serializer.deserializeString(br);
                String recipient = serializer.deserializeString(br);
                byte[] message = serializer.deserializeBytes(br);

                return new TeamMessage(sender, recipient, message);
            }

            #endregion
        }

        #endregion
    }
}