using System;

namespace robocode.exception
{
    public class EventInterruptedException : Exception
    {
        private const long serialVersionUID = 1L;
        private readonly int priority = -2147483648;

        public EventInterruptedException(int priority)
        {
            this.priority = priority;
        }

        public virtual int getPriority()
        {
            return priority;
        }
    }
}