using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    public class CustomEvent : Event
    {
        private const int DEFAULT_PRIORITY = 80;
        private const long serialVersionUID = 1L;
        private Condition condition;

        public CustomEvent(Condition condition)
        {
            this.condition = condition;
            if (condition != null)
            {
                base.setPriority(condition.getPriority());
            }
        }

        public CustomEvent(Condition condition, int priority)
        {
            this.condition = condition;
            base.setPriority(priority);
            if (condition != null)
            {
                condition.setPriority(getPriority());
            }
        }

        public override sealed int CompareTo(object obj)
        {
            return base.CompareTo(obj);
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, IGraphics graphics2D)
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

        public override sealed int getPriority()
        {
            return base.getPriority();
        }

        internal override byte getSerializationType()
        {
            throw new Exception("Serialization not supported on this event type");
        }

        internal override sealed bool isCriticalEvent()
        {
            return false;
        }
    }
}