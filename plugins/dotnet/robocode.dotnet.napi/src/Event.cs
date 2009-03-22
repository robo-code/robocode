using System;
using System.ComponentModel;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.io;
using java.lang;
using java.util;
using net.sf.robocode.io;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using robocode.robotinterfaces;
using Object=java.lang.Object;

namespace robocode
{
    [Signature("Ljava/lang/Object;Ljava/lang/Comparable<Lrobocode/Event;>;Ljava/io/Serializable;"),
     Implements(new[] {"java.lang.Comparable", "java.io.Serializable"})]
    public abstract class Event : Object, Comparable, Serializable.__Interface
    {
        private const long serialVersionUID = 1L;
        [NonSerialized] private bool addedToQueue;
        private int priority;
        private long time;

        #region Comparable Members

        [HideFromJava]
        int IComparable.CompareTo(object obj1)
        {
            return compareTo(obj1);
        }

        #endregion

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x29),
         Modifiers(Modifiers.Synthetic | Modifiers.Static)]
        internal static void access100(Event event1, long num1)
        {
            event1.setTimeHidden(num1);
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(new byte[] {0x1c, 0x8f, 0x66, 0xc2, 0x8e, 0x66, 0xe2, 0x47})]
        public virtual int compareTo(Event @event)
        {
            var num = (int) (time - @event.time);
            if (num != 0)
            {
                return num;
            }
            int num2 = @event.getPriority() - getPriority();
            if (num2 != 0)
            {
                return num2;
            }
            return 0;
        }

        [MethodImpl(MethodImplOptions.NoInlining),
         Modifiers(Modifiers.Synthetic | Modifiers.Public | Modifiers.Volatile), LineNumberTable((ushort) 0x29),
         EditorBrowsable(EditorBrowsableState.Never)]
        public virtual int compareTo(object x0)
        {
            return compareTo((Event) x0);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xdd)]
        internal static IHiddenEventHelper createHiddenHelper()
        {
            return new HiddenEventHelper(null);
        }

        [Modifiers(Modifiers.Abstract)]
        internal virtual void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics2D graphics2D)
        {
            throw new AbstractMethodError(
                "robocode.Event.dispatch(Lrobocode.robotinterfaces.IBasicRobot;Lnet.sf.robocode.peer.IRobotStatics;Ljava.awt.Graphics2D;)V");
        }

        [Modifiers(Modifiers.Abstract)]
        internal virtual int getDefaultPriority()
        {
            throw new AbstractMethodError("robocode.Event.getDefaultPriority()I");
        }

        public virtual int getPriority()
        {
            return priority;
        }

        [Modifiers(Modifiers.Abstract)]
        internal virtual byte getSerializationType()
        {
            throw new AbstractMethodError("robocode.Event.getSerializationType()B");
        }

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

        [MethodImpl(MethodImplOptions.NoInlining),
         LineNumberTable(
             new byte[] {0x52, 0x6b, 0x6a, 0x83, 0x67, 0x6a, 0x7f, 20, 0x68, 0x68, 0x6a, 0x7f, 20, 0x84, 0x67})]
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
                        new StringBuilder().append("SYSTEM: Priority for ").append(base.getClass().getName()).append(
                            " will be 0").toString());
                    newPriority = 0;
                }
                else if (newPriority > 0x63)
                {
                    Logger.printlnToRobotsConsole("SYSTEM: Priority must be between 0 and 99");
                    Logger.printlnToRobotsConsole(
                        new StringBuilder().append("SYSTEM: Priority for ").append(base.getClass().getName()).append(
                            " will be 99").toString());
                    newPriority = 0x63;
                }
                priority = newPriority;
            }
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x77, 0x6b, 140, 0x8a})]
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

        [Signature("(Ljava/util/Hashtable<Ljava/lang/Integer;Lrobocode/Bullet;>;)V")]
        internal virtual void updateBullets(Hashtable h)
        {
        }

        #region Nested type: a1

        [Modifiers(Modifiers.Synthetic | Modifiers.Synchronized), EnclosingMethod("robocode.Event", null, null),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static), SourceFile("Event.java")]
        internal sealed class a1 :
            Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: HiddenEventHelper

        [InnerClass(null, Modifiers.Static | Modifiers.Private),
         Implements(new[] {"net.sf.robocode.security.IHiddenEventHelper"}), SourceFile("Event.java")]
        internal sealed class HiddenEventHelper : Object, IHiddenEventHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xe1)]
            private HiddenEventHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xe1), Modifiers(Modifiers.Synthetic)]
            internal HiddenEventHelper(Event a1) : this()
            {
            }

            #region IHiddenEventHelper Members

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 130, 0x6a})]
            public void dispatch(Event event1, IBasicRobot robot1, IRobotStatics statics1, Graphics2D graphicsd1)
            {
                event1.dispatch(robot1, statics1, graphicsd1);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0xf8)]
            public byte getSerializationType(Event event1)
            {
                return (byte) ((sbyte) event1.getSerializationType());
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 240)]
            public bool isCriticalEvent(Event event1)
            {
                return event1.isCriticalEvent();
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x76, 0x6c})]
            public void setDefaultPriority(Event event1)
            {
                event1.setPriority(event1.getDefaultPriority());
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x7a, 0x67})]
            public void setPriority(Event event1, int num1)
            {
                event1.setPriority(num1);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x72, 0x67})]
            public void setTime(Event event1, long num1)
            {
                access100(event1, num1);
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {160, 0x8a, 0x67}),
             Signature("(Lrobocode/Event;Ljava/util/Hashtable<Ljava/lang/Integer;Lrobocode/Bullet;>;)V")]
            public void updateBullets(Event event1, Hashtable hashtable1)
            {
                event1.updateBullets(hashtable1);
            }

            #endregion
        }

        #endregion
    }
}