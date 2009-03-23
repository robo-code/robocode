using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class HitRobotEvent : Event
    {
        private const int DEFAULT_PRIORITY = 40;
        private const long serialVersionUID = 1L;
        private bool atFault;
        private double bearing;
        private double energy;
        private string robotName;

        public HitRobotEvent(string name, double bearing, double energy, bool atFault)
        {
            robotName = name;
            this.bearing = bearing;
            this.energy = energy;
            this.atFault = atFault;
        }

        internal static string access100(HitRobotEvent event1)
        {
            return event1.robotName;
        }

        internal static double access200(HitRobotEvent event1)
        {
            return event1.bearing;
        }

        internal static double access300(HitRobotEvent event1)
        {
            return event1.energy;
        }

        internal static bool access400(HitRobotEvent event1)
        {
            return event1.atFault;
        }

        public override int CompareTo(object @event)
        {
            int num = base.CompareTo(@event);
            if (num != 0)
            {
                return num;
            }
            if (@event is HitRobotEvent)
            {
                int num2 = !isMyFault() ? 0 : -1;
                int num3 = !((HitRobotEvent) @event).isMyFault() ? 0 : -1;
                return (num2 - num3);
            }
            return 0;
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
                events.onHitRobot(this);
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
            return 40;
        }

        public double getEnergy()
        {
            return energy;
        }

        public string getName()
        {
            return robotName;
        }

        internal override byte getSerializationType()
        {
            return 0x2b;
        }

        public bool isMyFault()
        {
            return atFault;
        }

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(HitRobotEvent a1)
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer1, ByteBuffer buffer1)
            {
                string name = serializer1.deserializeString(buffer1);
                double bearing = buffer1.getDouble();
                double energy = buffer1.getDouble();
                return new HitRobotEvent(name, bearing, energy, serializer1.deserializeBoolean(buffer1));
            }

            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (HitRobotEvent) obj1;
                serializer1.serialize(buffer1, access100(event2));
                serializer1.serialize(buffer1, access200(event2));
                serializer1.serialize(buffer1, access300(event2));
                serializer1.serialize(buffer1, access400(event2));
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (HitRobotEvent) obj1;
                return (((1 + serializer1.sizeOf(access100(event2))) + 0x10) + 1);
            }

            #endregion
        }

        #endregion
    }
}