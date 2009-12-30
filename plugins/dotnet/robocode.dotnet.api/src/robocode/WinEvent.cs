using System;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    /// 
    ///<summary>
    ///  This evnt is sent to {@link Robot#OnWin(WinEvent) OnWin()} when your robot
    ///  wins the round in a battle.
    ///
    ///  @author Mathew A. Nelson (original)
    ///</summary>
    [Serializable]
    public sealed class WinEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100; // System evnt -> cannot be changed!;

        /// <summary>{@inheritDoc}</summary>
        public override int Priority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>{@inheritDoc}</summary>
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>{@inheritDoc}</summary>
        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            IBasicEvents listener = robot.GetBasicEventListener();

            if (listener != null)
            {
                listener.OnWin(this);
            }
        }

        /// <summary>{@inheritDoc}</summary>
        internal override bool IsCriticalEvent
        {
            get { return true; }
        }

        /// <summary>{@inheritDoc}</summary>
        internal override byte SerializationType
        {
            get { return RbSerializerN.WinEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                return new WinEvent();
            }
        }
    }
}

//happy