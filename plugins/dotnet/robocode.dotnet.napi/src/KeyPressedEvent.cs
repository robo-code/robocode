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
    public sealed class KeyPressedEvent : KeyEvent
    {
        private const int DEFAULT_PRIORITY = 0x62;
        private const long serialVersionUID = 1L;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 190, 0x67})]
        public KeyPressedEvent(java.awt.@event.KeyEvent source) : base(source)
        {
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x52)]
        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {14, 0x6b, 140, 0x66, 0xac})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, Graphics2D graphics2D)
        {
            if (statics1.isInteractiveRobot())
            {
                IInteractiveEvents events = ((IInteractiveRobot) robot1).getInteractiveEventListener();
                if (events != null)
                {
                    events.onKeyPressed(base.getSourceEvent());
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x62;
        }

        internal override byte getSerializationType()
        {
            return 0x2c;
        }

        #region Nested type: a1

        [EnclosingMethod("robocode.KeyPressedEvent", null, null),
         Modifiers(Modifiers.Synthetic | Modifiers.Synchronized),
         InnerClass(null, Modifiers.Synthetic | Modifiers.Static), SourceFile("KeyPressedEvent.java")]
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

        [InnerClass(null, Modifiers.Static | Modifiers.Private),
         Implements(new[] {"net.sf.robocode.serialization.ISerializableHelper"}), SourceFile("KeyPressedEvent.java")]
        internal sealed class SerializableHelper : Object, ISerializableHelper
        {
            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x55)]
            private SerializableHelper()
            {
            }

            [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x55), Modifiers(Modifiers.Synthetic)]
            internal SerializableHelper(KeyPressedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {0x37, 0x67, 0x67, 0x67, 0x67, 0x68, 0x88})]
            public object deserialize(RbSerializer serializer, ByteBuffer buffer1)
            {
                int num = buffer1.getChar();
                int keyCode = buffer1.getInt();
                int keyLocation = buffer1.getInt();
                int id = buffer1.getInt();
                int modifiers = buffer1.getInt();
                long when = buffer1.getLong();
                return
                    new KeyPressedEvent(new java.awt.@event.KeyEvent(SafeComponent.getSafeEventComponent(), id, when,
                                                                     modifiers, keyCode, (char) num, keyLocation));
            }

            [MethodImpl(MethodImplOptions.NoInlining),
             LineNumberTable(new byte[] {0x2b, 0x67, 0x87, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d, 0x6d})]
            public void serialize(RbSerializer serializer1, ByteBuffer buffer1, object obj1)
            {
                java.awt.@event.KeyEvent event3 = ((KeyPressedEvent) obj1).getSourceEvent();
                serializer1.serialize(buffer1, event3.getKeyChar());
                serializer1.serialize(buffer1, event3.getKeyCode());
                serializer1.serialize(buffer1, event3.getKeyLocation());
                serializer1.serialize(buffer1, event3.getID());
                serializer1.serialize(buffer1, event3.getModifiersEx());
                serializer1.serialize(buffer1, event3.getWhen());
            }

            public int sizeOf(RbSerializer serializer, object o)
            {
                return 0x1b;
            }

            #endregion
        }

        #endregion
    }
}