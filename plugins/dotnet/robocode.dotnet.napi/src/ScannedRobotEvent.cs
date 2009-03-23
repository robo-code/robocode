using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class ScannedRobotEvent : Event
    {
        private const int DEFAULT_PRIORITY = 10;
        private const long serialVersionUID = 1L;
        private double bearing;
        private double distance;
        private double energy;
        private double heading;
        private string name;
        private double velocity;

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

        public override int CompareTo(object @event)
        {
            int num = base.CompareTo(@event);
            if (num != 0)
            {
                return num;
            }
            if (@event is ScannedRobotEvent)
            {
                return (int)(getDistance() - ((ScannedRobotEvent) @event).getDistance());
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
            return ((bearing*180.0)/3.1415926535897931);
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
            return ((heading*180.0)/3.1415926535897931);
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

            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                string name = serializer1.deserializeString(buffer1);
                double energy = buffer1.getDouble();
                double heading = buffer1.getDouble();
                double bearing = buffer1.getDouble();
                double distance = buffer1.getDouble();
                return new ScannedRobotEvent(name, energy, bearing, distance, heading, buffer1.getDouble());
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (ScannedRobotEvent) obj1;
                serializer1.serialize(buffer1, access100(event2));
                serializer1.serialize(buffer1, access200(event2));
                serializer1.serialize(buffer1, access300(event2));
                serializer1.serialize(buffer1, access400(event2));
                serializer1.serialize(buffer1, access500(event2));
                serializer1.serialize(buffer1, access600(event2));
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