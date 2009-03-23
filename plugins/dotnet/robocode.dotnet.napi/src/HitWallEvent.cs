using System;
using System.IO;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class HitWallEvent : Event
    {
        private const int DEFAULT_PRIORITY = 30;
        private const long serialVersionUID = 1L;
        private readonly double bearing;

        public HitWallEvent(double bearing)
        {
            this.bearing = bearing;
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        internal override void dispatch(IBasicRobot robot1, IRobotStatics statics, IGraphics graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onHitWall(this);
            }
        }

        public double getBearing()
        {
            return ((bearing*180.0)/Math.PI);
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
            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                return new HitWallEvent(br.ReadDouble());
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                var event2 = (HitWallEvent) obj;
                serializer.serialize(bw, event2.bearing);
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