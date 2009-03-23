using System;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class HitWallEvent : Event
    {
        private const int DEFAULT_PRIORITY = 30;
        private const long serialVersionUID = 1L;
        private double bearing;

        public HitWallEvent(double bearing)
        {
            this.bearing = bearing;
        }

        internal static double access100(HitWallEvent event1)
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
                events.onHitWall(this);
            }
        }

        public double getBearing()
        {
            return ((bearing*180.0)/3.1415926535897931);
        }

        public double getBearingDegrees()
        {
            return getBearing();
        }

        public double getBearingRadians()
        {
            return bearing;
        }

        internal override sealed int getDefaultPriority()
        {
            return 30;
        }

        internal override byte getSerializationType()
        {
            return 0x26;
        }

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(HitWallEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                return new HitWallEvent(buffer1.getDouble());
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (HitWallEvent) obj1;
                serializer1.serialize(buffer1, access100(event2));
            }

            public int sizeOf(RbSerializer serializer, object obj)
            {
                return 9;
            }

            #endregion
        }

        #endregion
    }
}