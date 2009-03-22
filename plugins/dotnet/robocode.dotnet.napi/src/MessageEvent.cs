using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.awt;
using java.io;
using java.lang;
using net.sf.robocode.peer;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class MessageEvent : Event
    {
        private const int DEFAULT_PRIORITY = 0x4b;
        private const long serialVersionUID = 1L;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private Serializable message;
        [Modifiers(Modifiers.Private | Modifiers.Final)] private string sender;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0x83, 0x8b, 0x68, 0x67, 0x77})]
        public MessageEvent(string sender, Serializable message)
        {
            Serializable serializable2;
            Serializable serializable = message;
            this.sender = sender;
            /*object obj2 = serializable.__<ref>;
            object obj3 = obj2;
            serializable2.__<ref> = obj3;
            this.message = serializable2;*/
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x20, 0x6b, 140, 0x66, 0xa7})]
        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics1, Graphics2D graphics2D)
        {
            if (statics1.isTeamRobot())
            {
                ITeamEvents events = ((ITeamRobot) robot1).getTeamEventListener();
                if (events != null)
                {
                    events.onMessageReceived(this);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 0x4b;
        }

        /*public Serializable getMessage()
        {
            Serializable serializable2;
            serializable2.__<ref> = this.message.__<ref>;
            return serializable2;
        }*/

        public string getSender()
        {
            return sender;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x60)]
        internal override byte getSerializationType()
        {
            throw new Error("Serialization of event type not supported");
        }
    }
}