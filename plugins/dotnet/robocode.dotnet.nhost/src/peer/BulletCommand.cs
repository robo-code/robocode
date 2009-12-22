using System;
using net.sf.robocode.serialization;

namespace net.sf.robocode.dotnet.peer
{
    [Serializable]
    public class BulletCommand
    {
        public BulletCommand(double power, bool fireAssistValid, double fireAssistAngle, int bulletId)
        {
            this.fireAssistValid = fireAssistValid;
            this.fireAssistAngle = fireAssistAngle;
            this.bulletId = bulletId;
            this.power = power;
        }

        private readonly double power;
        private readonly bool fireAssistValid;
        private readonly double fireAssistAngle;
        private readonly int bulletId;

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

        // ReSharper disable UnusedMember.Local
        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, Object obje)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_DOUBLE + RbSerializerN.SIZEOF_BOOL
                       + RbSerializerN.SIZEOF_DOUBLE + RbSerializerN.SIZEOF_INT;
            }

            public void serialize(RbSerializerN serializer, nio.ByteBuffer buffer, Object obje)
            {
                var obj = (BulletCommand) obje;

                serializer.serialize(buffer, obj.power);
                serializer.serialize(buffer, obj.fireAssistValid);
                serializer.serialize(buffer, obj.fireAssistAngle);
                serializer.serialize(buffer, obj.bulletId);
            }

            public Object deserialize(RbSerializerN serializer, nio.ByteBuffer buffer)
            {
                double power = buffer.getDouble();
                bool fireAssistValid = serializer.deserializeBoolean(buffer);
                double fireAssistAngle = buffer.getDouble();
                int bulletId = buffer.getInt();

                return new BulletCommand(power, fireAssistValid, fireAssistAngle, bulletId);
            }
        }
    }
}
