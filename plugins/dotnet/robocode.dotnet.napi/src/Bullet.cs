using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.io;
using java.lang;
using java.nio;
using net.sf.robocode.security;
using net.sf.robocode.serialization;

namespace robocode
{
    [Implements(new[] {"java.io.Serializable"})]
    public class Bullet : Object, Serializable.__Interface
    {
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private int bulletId;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double headingRadians;
        private bool isActive;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string ownerName;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private double power;
        private string victimName;
        private double x;
        private double y;

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x9f, 0x7f, 0x63, 0x68, 0x69, 0x68, 0x69, 0x69, 0x6a, 0x68, 0x68, 0x67})]
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

        [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic | Modifiers.Static),
         LineNumberTable((ushort) 0x25)]
        internal static void access100(Bullet bullet1, double num1, double num2, string text1, bool flag1)
        {
            bullet1.update(num1, num2, text1, flag1);
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x25)]
        internal static string access200(Bullet bullet1)
        {
            return bullet1.ownerName;
        }

        [LineNumberTable((ushort) 0x25), Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static string access300(Bullet bullet1)
        {
            return bullet1.victimName;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x25)]
        internal static double access400(Bullet bullet1)
        {
            return bullet1.headingRadians;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x25)]
        internal static double access500(Bullet bullet1)
        {
            return bullet1.x;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x25)]
        internal static double access600(Bullet bullet1)
        {
            return bullet1.y;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x25)]
        internal static double access700(Bullet bullet1)
        {
            return bullet1.power;
        }

        [Modifiers(Modifiers.Synthetic | Modifiers.Static), LineNumberTable((ushort) 0x25)]
        internal static bool access800(Bullet bullet1)
        {
            return bullet1.isActive;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xc4)]
        internal static IHiddenBulletHelper createHiddenHelper()
        {
            return new HiddenBulletHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xce)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new HiddenBulletHelper(null);
        }

        internal virtual int getBulletId()
        {
            return bulletId;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 80)]
        public virtual double getHeading()
        {
            return Math.toDegrees(headingRadians);
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

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x7b)]
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

        #region Nested type: a1

        [Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), SourceFile("Bullet.java"),
         EnclosingMethod("robocode.Bullet", null, null), InnerClass(null, Modifiers.Synthetic | Modifiers.Static)]
        internal sealed class a1 : Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: HiddenBulletHelper

        [Implements(
            new[] {"net.sf.robocode.security.IHiddenBulletHelper", "net.sf.robocode.serialization.ISerializableHelper"})
        , SourceFile("Bullet.java"), InnerClass(null, Modifiers.Static | Modifiers.Private)]
        internal sealed class HiddenBulletHelper : Object, IHiddenBulletHelper, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 210)]
            private HiddenBulletHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 210)]
            internal HiddenBulletHelper(Bullet a1) : this()
            {
            }

            #region IHiddenBulletHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0x59, 0x63, 0x6d})]
            public void update(Bullet bullet1, double num1, double num2, string text1, bool flag1)
            {
                access100(bullet1, num1, num2, text1, flag1);
            }

            #endregion

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {160, 0x7a, 0x68, 0x68, 0x68, 0x68, 0x69, 0x69, 0x89})]
            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                double heading = buffer1.getDouble();
                double x = buffer1.getDouble();
                double y = buffer1.getDouble();
                double power = buffer1.getDouble();
                string ownerName = serializer1.deserializeString(buffer1);
                string victimName = serializer1.deserializeString(buffer1);
                return new Bullet(heading, x, y, power, ownerName, victimName, serializer1.deserializeBoolean(buffer1),
                                  -1);
            }

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {160, 110, 0x87, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var bullet = (Bullet) obj1;
                serializer1.serialize(buffer1, access400(bullet));
                serializer1.serialize(buffer1, access500(bullet));
                serializer1.serialize(buffer1, access600(bullet));
                serializer1.serialize(buffer1, access700(bullet));
                serializer1.serialize(buffer1, access200(bullet));
                serializer1.serialize(buffer1, access300(bullet));
                serializer1.serialize(buffer1, access800(bullet));
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x67, 0x87})]
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