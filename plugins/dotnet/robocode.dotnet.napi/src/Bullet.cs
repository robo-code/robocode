using System.IO;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode.util;

namespace robocode
{
    public class Bullet
    {
        private readonly int bulletId;
        private readonly double headingRadians;
        private readonly string ownerName;
        private readonly double power;
        private bool isActive;
        private string victimName;
        private double x;
        private double y;

        public Bullet(double heading, double x, double y, double power, string ownerName, string victimName,
                      bool isActive, int bulletId)
        {
            headingRadians = heading;
            this.bulletId = bulletId;
            this.x = x;
            this.y = y;
            this.power = power;
            this.ownerName = ownerName;
            this.victimName = victimName;
            this.isActive = isActive;
        }

        internal static void access100(Bullet bullet1, double num1, double num2, string text1, bool flag1)
        {
            bullet1.update(num1, num2, text1, flag1);
        }

        internal static string access200(Bullet bullet1)
        {
            return bullet1.ownerName;
        }

        internal static string access300(Bullet bullet1)
        {
            return bullet1.victimName;
        }

        internal static double access400(Bullet bullet1)
        {
            return bullet1.headingRadians;
        }

        internal static double access500(Bullet bullet1)
        {
            return bullet1.x;
        }

        internal static double access600(Bullet bullet1)
        {
            return bullet1.y;
        }

        internal static double access700(Bullet bullet1)
        {
            return bullet1.power;
        }

        internal static bool access800(Bullet bullet1)
        {
            return bullet1.isActive;
        }

        internal static IHiddenBulletHelper createHiddenHelper()
        {
            return new HiddenBulletHelper(null);
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new HiddenBulletHelper(null);
        }

        internal virtual int getBulletId()
        {
            return bulletId;
        }

        public virtual double getHeading()
        {
            return Utils.toDegrees(headingRadians);
        }

        public virtual double getHeadingRadians()
        {
            return headingRadians;
        }

        public virtual string getName()
        {
            return ownerName;
        }

        public virtual double getPower()
        {
            return power;
        }

        public virtual double getVelocity()
        {
            return Rules.getBulletSpeed(power);
        }

        public virtual string getVictim()
        {
            return victimName;
        }

        public virtual double getX()
        {
            return x;
        }

        public virtual double getY()
        {
            return y;
        }

        public bool IsActive()
        {
            return isActive;
        }

        private void update(double num1, double num2, string text1, bool flag1)
        {
            x = num1;
            y = num2;
            victimName = text1;
            isActive = flag1;
        }

        #region Nested type: HiddenBulletHelper

        internal sealed class HiddenBulletHelper : IHiddenBulletHelper, ISerializableHelper
        {
            private HiddenBulletHelper()
            {
            }

            internal HiddenBulletHelper(Bullet a1) : this()
            {
            }

            #region IHiddenBulletHelper Members

            public void update(Bullet bullet1, double num1, double num2, string text1, bool flag1)
            {
                access100(bullet1, num1, num2, text1, flag1);
            }

            #endregion

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                double heading = br.ReadDouble();
                double x = br.ReadDouble();
                double y = br.ReadDouble();
                double power = br.ReadDouble();
                string ownerName = serializer.deserializeString(br);
                string victimName = serializer.deserializeString(br);
                bool active = serializer.deserializeBoolean(br);
                return new Bullet(heading, x, y, power, ownerName, victimName, active, -1);
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                var bullet = (Bullet) obj;
                serializer.serialize(bw, access400(bullet));
                serializer.serialize(bw, access500(bullet));
                serializer.serialize(bw, access600(bullet));
                serializer.serialize(bw, access700(bullet));
                serializer.serialize(bw, access200(bullet));
                serializer.serialize(bw, access300(bullet));
                serializer.serialize(bw, access800(bullet));
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var bullet = (Bullet) obj1;
                return (((0x21 + serializer1.sizeOf(access200(bullet))) + serializer1.sizeOf(access300(bullet))) + 1);
            }

            #endregion
        }

        #endregion
    }
}