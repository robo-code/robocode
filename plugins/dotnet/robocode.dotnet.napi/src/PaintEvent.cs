using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.lang;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class PaintEvent : Event
    {
        private const int DEFAULT_PRIORITY = 5;
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {7, 0x6b, 140, 0x66, 0xa7})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, Graphics2D graphicsd1)
        {
            if (statics1.isPaintRobot())
            {
                IPaintEvents events = ((IPaintRobot) robot1).getPaintEventListener();
                if (events != null)
                {
                    events.onPaint(graphicsd1);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 5;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x47)]
        internal override byte getSerializationType()
        {
            throw new Error("Serialization of this type is not supported");
        }
    }
}