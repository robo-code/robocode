using System;
using System.IO;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class ScannedRobotEvent : Event
    {
        private const int DEFAULT_PRIORITY = 10;
        private const long serialVersionUID = 1L;
        private readonly double bearing;
        private readonly double distance;
        private readonly double energy;
        private readonly double heading;
        private readonly string name;
        private readonly double velocity;

        public ScannedRobotEvent(string name, double energy, double bearing, double distance, double heading,
                                 double velocity)
        {
            this.name = name;
            this.energy = energy;
            this.bearing = bearing;
            this.distance = distance;
            this.heading = heading;
            this.velocity = velocity;
        }

        internal static string access100(ScannedRobotEvent event1)
        {
            return event1.name;
        }

        internal static double access200(ScannedRobotEvent event1)
        {
            return event1.energy;
        }

        internal static double access300(ScannedRobotEvent event1)
        {
            return event1.heading;
        }

        internal static double access400(ScannedRobotEvent event1)
        {
            return event1.bearing;
        }

        internal static double access500(ScannedRobotEvent event1)
        {
            return event1.distance;
        }

        internal static double access600(ScannedRobotEvent event1)
        {
            return event1.velocity;
        }

        public override int CompareTo(object evnt)
        {
            int num = base.CompareTo(evnt);
            if (num != 0)
            {
                return num;
            }
            if (evnt is ScannedRobotEvent)
            {
                return (int) (getDistance() - ((ScannedRobotEvent) evnt).getDistance());
            }
            return 0;
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics robotStatics, IGraphics graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onScannedRobot(this);
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

        internal override sealed int getDefaultPriority()
        {
            return 10;
        }

        public double getDistance()
        {
            return distance;
        }

        public double getEnergy()
        {
            return energy;
        }

        public double getHeading()
        {
            return ((heading*180.0)/Math.PI);
        }

        public double getHeadingRadians()
        {
            return heading;
        }

        public string getName()
        {
            return name;
        }

        internal override byte getSerializationType()
        {
            return 0x29;
        }

        public double getVelocity()
        {
            return velocity;
        }

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(ScannedRobotEvent a1)
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                string name = serializer.deserializeString(br);
                double energy = br.ReadDouble();
                double heading = br.ReadDouble();
                double bearing = br.ReadDouble();
                double distance = br.ReadDouble();
                return new ScannedRobotEvent(name, energy, bearing, distance, heading, br.ReadDouble());
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                var event2 = (ScannedRobotEvent) obj;
                serializer.serialize(bw, access100(event2));
                serializer.serialize(bw, access200(event2));
                serializer.serialize(bw, access300(event2));
                serializer.serialize(bw, access400(event2));
                serializer.serialize(bw, access500(event2));
                serializer.serialize(bw, access600(event2));
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (ScannedRobotEvent) obj1;
                return ((1 + serializer1.sizeOf(access100(event2))) + 40);
            }

            #endregion
        }

        #endregion
    }
}