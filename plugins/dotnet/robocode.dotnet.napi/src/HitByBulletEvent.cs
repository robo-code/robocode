using System;
using System.IO;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class HitByBulletEvent : Event
    {
        private const int DEFAULT_PRIORITY = 20;
        private const long serialVersionUID = 1L;
        private readonly double bearing;
        private readonly Bullet bullet;

        public HitByBulletEvent(double bearing, Bullet bullet)
        {
            this.bearing = bearing;
            this.bullet = bullet;
        }

        internal static Bullet access100(HitByBulletEvent event1)
        {
            return event1.bullet;
        }

        internal static double access200(HitByBulletEvent event1)
        {
            return event1.bearing;
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, IGraphics graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onHitByBullet(this);
            }
        }

        public double getBearing()
        {
            return ((bearing*180.0)/Math.PI);
        }

        public double getBearingRadians()
        {
            return bearing;
        }

        public Bullet getBullet()
        {
            return bullet;
        }

        internal override sealed int getDefaultPriority()
        {
            return 20;
        }

        public double getHeading()
        {
            return bullet.getHeading();
        }

        public double getHeadingDegrees()
        {
            return getHeading();
        }

        public double getHeadingRadians()
        {
            return bullet.getHeadingRadians();
        }

        public string getName()
        {
            return bullet.getName();
        }

        public double getPower()
        {
            return bullet.getPower();
        }

        internal override byte getSerializationType()
        {
            return 0x2a;
        }

        public double getVelocity()
        {
            return bullet.getVelocity();
        }

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(HitByBulletEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                var bullet = (Bullet) serializer.deserializeAny(br);
                return new HitByBulletEvent(br.ReadDouble(), bullet);
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                var event2 = (HitByBulletEvent) obj;
                serializer.serialize(bw, 9, access100(event2));
                serializer.serialize(bw, access200(event2));
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (HitByBulletEvent) obj1;
                return ((1 + serializer1.sizeOf(9, access100(event2))) + 8);
            }

            #endregion
        }

        #endregion
    }
}