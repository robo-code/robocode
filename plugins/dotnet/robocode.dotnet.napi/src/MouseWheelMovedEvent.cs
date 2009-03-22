using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.awt.@event;
using java.lang;
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class MouseWheelMovedEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {3, 0x67})]
        public MouseWheelMovedEvent(java.awt.@event.MouseEvent source) : base(source)
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x57)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x13, 0x6b, 140, 0x66, 0xb1})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, Graphics2D graphics2D)
        {
            if (statics1.isInteractiveRobot())
            {
                IInteractiveEvents events = ((IInteractiveRobot) robot1).getInteractiveEventListener();
                if (events != null)
                {
                    events.onMouseWheelMoved((MouseWheelEvent) base.getSourceEvent());
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x62;
        }

        internal override byte getSerializationType()
        {
            return 0x36;
        }

        #region Nested type: a1

        [SourceFile("MouseWheelMovedEvent.java"), Modifiers(Modifiers.Synthetic | Modifiers.Synchronized),
         EnclosingMethod("robocode.MouseWheelMovedEvent", null, null),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static)]
        internal sealed class a1 : Object
        {
            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: SerializableHelper

        [SourceFile("MouseWheelMovedEvent.java"), InnerClass(null, Modifiers.Static | Modifiers.Private),
         Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"})]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 90)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 90), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(MouseWheelMovedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {0x3e, 0x67, 0x67, 0x67, 0x67, 0x68, 0x68, 0x68, 0x68, 0x88})]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                int clickCount = buffer1.getInt();
                int x = buffer1.getInt();
                int y = buffer1.getInt();
                int scrollType = buffer1.getInt();
                int scrollAmount = buffer1.getInt();
                int wheelRotation = buffer1.getInt();
                int id = buffer1.getInt();
                int modifiers = buffer1.getInt();
                long when = buffer1.getLong();
                //MouseWheelEvent.__<clinit>();
                return
                    new MouseWheelMovedEvent(new MouseWheelEvent(SafeComponent.getSafeEventComponent(), id, when,
                                                                 modifiers, x, y, clickCount, false, scrollType,
                                                                 scrollAmount, wheelRotation));
            }

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {0x2f, 0x67, 140, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                var event2 = (MouseWheelMovedEvent) obj1;
                var event3 = (MouseWheelEvent) event2.getSourceEvent();
                serializer1.serialize(buffer1, event3.getClickCount());
                serializer1.serialize(buffer1, event3.getX());
                serializer1.serialize(buffer1, event3.getY());
                serializer1.serialize(buffer1, event3.getScrollType());
                serializer1.serialize(buffer1, event3.getScrollAmount());
                serializer1.serialize(buffer1, event3.getWheelRotation());
                serializer1.serialize(buffer1, event3.getID());
                serializer1.serialize(buffer1, event3.getModifiersEx());
                serializer1.serialize(buffer1, event3.getWhen());
            }

            public int sizeOf(RbSerializer serializer, object obj)
            {
                return 0x29;
            }

            #endregion
        }

        #endregion
    }
}