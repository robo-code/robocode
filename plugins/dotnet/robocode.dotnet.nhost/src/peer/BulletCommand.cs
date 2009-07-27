using System;
using java.nio;
using net.sf.robocode.serialization;

namespace net.sf.robocode.dotnet.peer
{
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

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbSerializer serializer, Object obje)
            {
                return RbSerializer.SIZEOF_TYPEINFO + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_BOOL
                       + RbSerializer.SIZEOF_DOUBLE + RbSerializer.SIZEOF_INT;
            }

            public void serialize(RbSerializer serializer, IByteBuffer buffer, Object obje)
            {
                var obj = (BulletCommand) obje;

                serializer.serialize(buffer, obj.power);
                serializer.serialize(buffer, obj.fireAssistValid);
                serializer.serialize(buffer, obj.fireAssistAngle);
                serializer.serialize(buffer, obj.bulletId);
            }

            public Object deserialize(RbSerializer serializer, IByteBuffer buffer)
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
