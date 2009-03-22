using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.lang;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class StatusEvent : Event
    {
        private const int DEFAULT_PRIORITY = 0x63;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private RobotStatus status;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xb8, 0x88, 0x67})]
        public StatusEvent(RobotStatus status)
        {
            this.status = status;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {20, 0x87, 0x66, 0x87})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, Graphics2D graphics2D)
        {
            IBasicEvents events = robot1.getBasicEventListener();
            if (events != null)
            {
                events.onStatus(this);
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x63;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x52)]
        internal override byte getSerializationType()
        {
            throw new Error("Serialization of this type is not supported");
        }

        public RobotStatus getStatus()
        {
            return status;
        }
    }
}