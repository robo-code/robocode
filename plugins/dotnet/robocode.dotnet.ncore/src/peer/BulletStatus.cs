using System;
using System.IO;
using net.sf.robocode.serialization;

namespace robocode.dotnet.ncore.peer
{
    public class BulletStatus
    {
        public int bulletId;
        public bool isActive;
        public String victimName;
        public double x;
        public double y;

        public BulletStatus(int bulletId, double x, double y, String victimName, bool isActive)
        {
            this.bulletId = bulletId;
            this.x = x;
            this.y = y;
            this.isActive = isActive;
            this.victimName = victimName;
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
                var obj = (BulletStatus) object1;

                return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_INT + serializer.sizeOf(obj.victimName)
                       + RbSerializer.SIZEOF_BOOL + 2*RbSerializer.SIZEOF_DOUBLE;
            }

            public void serialize(RbSerializer serializer, BinaryWriter buffer, Object object1)
            {
                var obj = (BulletStatus) object1;

                serializer.serialize(buffer, obj.bulletId);
                serializer.serialize(buffer, obj.victimName);
                serializer.serialize(buffer, obj.isActive);
                serializer.serialize(buffer, obj.x);
                serializer.serialize(buffer, obj.y);
            }

            public Object deserialize(RbSerializer serializer, BinaryReader buffer)
            {
                int bulletId = buffer.ReadInt32();
                String victimName = serializer.deserializeString(buffer);
                bool isActive = serializer.deserializeBoolean(buffer);
                double x = buffer.ReadDouble();
                double y = buffer.ReadDouble();

                return new BulletStatus(bulletId, x, y, victimName, isActive);
            }

            #endregion
        }

        #endregion
    }
}