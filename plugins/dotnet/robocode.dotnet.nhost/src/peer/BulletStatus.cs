using System;
using java.nio;
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

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, Object obje)
            {
                var obj = (BulletStatus) obje;

                return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.victimName)
                       + RbSerializer.SIZEOF_BOOL + 2*RbSerializer.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, Object obje)
            {
                var obj = (BulletStatus) obje;

                serializer.serialize(buffer, obj.bulletId);
                serializer.serialize(buffer, obj.victimName);
                serializer.serialize(buffer, obj.isActive);
                serializer.serialize(buffer, obj.x);
                serializer.serialize(buffer, obj.y);
            }

            public Object deserialize(RbSerializer serializer, IByteBuffer buffer)
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
