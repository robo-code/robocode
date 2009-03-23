using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Runtime.CompilerServices;
using System.Runtime.Serialization;
using System.Text;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using robocode.robotinterfaces;

namespace robocode
{
    public abstract class Event : IComparable
    {
        private const long serialVersionUID = 1L;
        [NonSerialized] private bool addedToQueue;
        private int priority;
        private long time;

        internal static void access100(Event event1, long num1)
        {
            event1.setTimeHidden(num1);
        }

        internal static IHiddenEventHelper createHiddenHelper()
        {
            return new HiddenEventHelper(null);
        }

        internal abstract void dispatch(IBasicRobot robot, IRobotStatics statics, IGraphics graphics2D);

        internal abstract int getDefaultPriority();

        public virtual int getPriority()
        {
            return priority;
        }

        internal abstract byte getSerializationType();

        public long getTime()
        {
            return time;
        }

        internal virtual bool isCriticalEvent()
        {
            return false;
        }

        /*public static implicit operator Serializable(Event event1)
        {
            /*Serializable serializable;
            serializable.__<ref> = event1;
            return serializable;
        }*/

        public void setPriority(int newPriority)
        {
            if (addedToQueue)
            {
                Logger.printlnToRobotsConsole("SYSTEM: After the event was added to queue, priority can't be changed.");
            }
            else
            {
                if (newPriority < 0)
                {
                    Logger.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                    Logger.printlnToRobotsConsole(
                        new StringBuilder().Append("SYSTEM: Priority for ").Append(GetType().Name).Append(" will be 0").ToString());
                    newPriority = 0;
                }
                else if (newPriority > 0x63)
                {
                    Logger.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                    Logger.printlnToRobotsConsole(
                        new StringBuilder().Append("SYSTEM: Priority for ").Append(base.GetType().Name).Append(
                            " will be 99").ToString());
                    newPriority = 0x63;
                }
                priority = newPriority;
            }
        }

        public virtual void setTime(long newTime)
        {
            if (!addedToQueue)
            {
                time = newTime;
            }
            else
            {
                Logger.printlnToRobotsConsole("SYSTEM: After the event was added to queue, time can't be changed.");
            }
        }

        private void setTimeHidden(long num1)
        {
            if (time < num1)
            {
                time = num1;
            }
            addedToQueue = true;
        }

        internal virtual void updateBullets(Dictionary<int, Bullet> h)
        {
        }
        
        #region Nested type: HiddenEventHelper

        internal sealed class HiddenEventHelper : IHiddenEventHelper
        {
            private HiddenEventHelper()
            {
            }

            internal HiddenEventHelper(Event a1) : this()
            {
            }

            #region IHiddenEventHelper Members

            public void dispatch(Event event1, IBasicRobot robot1, IRobotStatics statics1, IGraphics graphicsd1)
            {
                event1.dispatch(robot1, statics1, graphicsd1);
            }

            public byte getSerializationType(Event event1)
            {
                return (byte) ((sbyte) event1.getSerializationType());
            }

            public bool isCriticalEvent(Event event1)
            {
                return event1.isCriticalEvent();
            }

            public void setDefaultPriority(Event event1)
            {
                event1.setPriority(event1.getDefaultPriority());
            }

            public void setPriority(Event event1, int num1)
            {
                event1.setPriority(num1);
            }

            public void setTime(Event event1, long num1)
            {
                access100(event1, num1);
            }

            public void updateBullets(Event event1, Dictionary<int, Bullet> hashtable1)
            {
                event1.updateBullets(hashtable1);
            }

            #endregion
        }

        #endregion

        public virtual int CompareTo(object obj)
        {
            Event evt = obj as Event;
            if (evt==null)
            {
                return -1;
            }

            var num = (int)(time - evt.time);
            if (num != 0)
            {
                return num;
            }
            int num2 = evt.getPriority() - getPriority();
            if (num2 != 0)
            {
                return num2;
            }
            return 0;
        }
    }
}