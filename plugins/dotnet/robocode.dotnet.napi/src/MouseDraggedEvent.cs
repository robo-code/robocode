using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.lang;
using java.nio;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class MouseDraggedEvent : MouseEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {3, 0x67})]
        public MouseDraggedEvent(java.awt.@event.MouseEvent source) : base(source)
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x57)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x13, 0x6b, 140, 0x66, 0xac})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, Graphics2D graphics2D)
        {
            if (statics1.isInteractiveRobot())
            {
                IInteractiveEvents events = ((IInteractiveRobot) robot1).getInteractiveEventListener();
                if (events != null)
                {
                    events.onMouseDragged(base.getSourceEvent());
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x62;
        }

        internal override byte getSerializationType()
        {
            return 0x30;
        }

        #region Nested type: a1

        [Modifiers(Modifiers.Synthetic | Modifiers.Synchronized),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static),
         EnclosingMethod("robocode.MouseDraggedEvent", null, null), SourceFile("MouseDraggedEvent.java")]
        internal sealed class a1 : Object
        {
            /* private scope */

            private a1()
            {
                throw null;
            }
        }

        #endregion

        #region Nested type: SerializableHelper

        [Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}), SourceFile("MouseDraggedEvent.java"),
         InnerClass(null, Modifiers.Static | Modifiers.Private)]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 90)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), Modifiers(Modifiers.Synthetic), LineNumberTable((ushort) 90)]
            internal SerializableHelper(MouseDraggedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {60, 0x67, 0x67, 0x67, 0x67, 0x68, 0x68, 0x88})]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                int button = buffer1.getInt();
                int clickCount = buffer1.getInt();
                int x = buffer1.getInt();
                int y = buffer1.getInt();
                int id = buffer1.getInt();
                int modifiers = buffer1.getInt();
                long when = buffer1.getLong();
                return
                    new MouseDraggedEvent(new java.awt.@event.MouseEvent(SafeComponent.getSafeEventComponent(), id, when,
                                                                         modifiers, x, y, clickCount, false, button));
            }

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {0x2f, 0x67, 0x87, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                java.awt.@event.MouseEvent event3 = ((MouseDraggedEvent) obj1).getSourceEvent();
                serializer1.serialize(buffer1, event3.getButton());
                serializer1.serialize(buffer1, event3.getClickCount());
                serializer1.serialize(buffer1, event3.getX());
                serializer1.serialize(buffer1, event3.getY());
                serializer1.serialize(buffer1, event3.getID());
                serializer1.serialize(buffer1, event3.getModifiersEx());
                serializer1.serialize(buffer1, event3.getWhen());
            }

            public int sizeOf(RbSerializer serializer, object ob)
            {
                return 0x21;
            }

            #endregion
        }

        #endregion
    }
}