using System;
using System.IO;
using net.sf.robocode.serialization;

namespace robocode.dotnet.core.net.peer
{
    public class BulletCommand
    {
        private readonly int bulletId;
        private readonly double fireAssistAngle;
        private readonly bool fireAssistValid;
        private readonly double power;

        public BulletCommand(double power, bool fireAssistValid, double fireAssistAngle, int bulletId)
        {
            this.fireAssistValid = fireAssistValid;
            this.fireAssistAngle = fireAssistAngle;
            this.bulletId = bulletId;
            this.power = power;
        }

        public bool isFireAssistValid()
        {
            return fireAssistValid;
        }

        public int getBulletId()
        {
            return bulletId;
        }

        public double getPower()
        {
            return power;
        }

        public double getFireAssistAngle()
        {
            return fireAssistAngle;
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
                return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_BOOL
                       + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_INT;
            }

            public void serialize(RbSerializer serializer, BinaryWriter buffer, Object object1)
            {
                var obj = (BulletCommand) object1;

                serializer.serialize(buffer, obj.power);
                serializer.serialize(buffer, obj.fireAssistValid);
                serializer.serialize(buffer, obj.fireAssistAngle);
                serializer.serialize(buffer, obj.bulletId);
            }

            public Object deserialize(RbSerializer serializer, BinaryReader buffer)
            {
                double power = buffer.ReadDouble();
                bool fireAssistValid = serializer.deserializeBoolean(buffer);
                double fireAssistAngle = buffer.ReadDouble();
                int bulletId = buffer.ReadInt32();

                return new BulletCommand(power, fireAssistValid, fireAssistAngle, bulletId);
            }

            #endregion
        }

        #endregion
    }
}