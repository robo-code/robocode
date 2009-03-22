using System.ComponentModel;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.lang;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    public class CustomEvent : Event
    {
        private const int DEFAULT_PRIORITY = 80;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private Condition condition;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0xbb, 0x68, 0x67, 0x66, 140})]
        public CustomEvent(Condition condition)
        {
            this.condition = condition;
            if (condition != null)
            {
                base.setPriority(condition.getPriority());
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {14, 0x68, 0x67, 0x67, 0x66, 140})]
        public CustomEvent(Condition condition, int priority)
        {
            this.condition = condition;
            base.setPriority(priority);
            if (condition != null)
            {
                condition.setPriority(getPriority());
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x77)]
        public override sealed int compareTo(Event @event)
        {
            return base.compareTo(@event);
        }

        [MethodImpl(MethodImplOptions.NoInlining), EditorBrowsable(EditorBrowsableState.Never),
         Modifiers(Modifiers.Synthetic | Modifiers.Public | Modifiers.Volatile), LineNumberTable((ushort) 0x22)]
        public override int compareTo(object x0)
        {
            return compareTo((Event) x0);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x36, 0x6b, 140, 0x66, 0xa7})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, Graphics2D graphics2D)
        {
            if (statics1.isAdvancedRobot())
            {
                IAdvancedEvents events = ((IAdvancedRobot) robot1).getAdvancedEventListener();
                if (events != null)
                {
                    events.onCustomEvent(this);
                }
            }
        }

        public virtual Condition getCondition()
        {
            return condition;
        }

        internal override sealed int getDefaultPriority()
        {
            return 80;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x89)]
        public override sealed int getPriority()
        {
            return base.getPriority();
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x91)]
        internal override byte getSerializationType()
        {
            throw new Error("Serialization not supported on this event type");
        }

        internal override sealed bool isCriticalEvent()
        {
            return false;
        }
    }
}