using System;
using net.sf.robocode.serialization;

namespace net.sf.robocode.dotnet.peer
{
    public class BulletStatus
    {
        public BulletStatus(int bulletId, double x, double y, String victimName, bool isActive)
        {
            this.bulletId = bulletId;
            this.x = x;
            this.y = y;
            this.isActive = isActive;
            this.victimName = victimName;
        }

        public readonly int bulletId;
        public readonly String victimName;
        public readonly bool isActive;
        public readonly double x;
        public readonly double y;

        // ReSharper disable UnusedMember.Local
        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, Object obje)
            {
                var obj = (BulletStatus) obje;

                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_INT + serializer.sizeOf(obj.victimName)
                       + RbSerializerN.SIZEOF_BOOL + 2*RbSerializerN.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializerN serializer, nio.ByteBuffer buffer, Object obje)
            {
                var obj = (BulletStatus) obje;

                serializer.serialize(buffer, obj.bulletId);
                serializer.serialize(buffer, obj.victimName);
                serializer.serialize(buffer, obj.isActive);
                serializer.serialize(buffer, obj.x);
                serializer.serialize(buffer, obj.y);
            }

            public Object deserialize(RbSerializerN serializer, nio.ByteBuffer buffer)
            {
                int bulletId = buffer.getInt();
                String victimName = serializer.deserializeString(buffer);
                bool isActive = serializer.deserializeBoolean(buffer);
                double x = buffer.getDouble();
                double y = buffer.getDouble();

                return new BulletStatus(bulletId, x, y, victimName, isActive);
            }
        }
    }
}
