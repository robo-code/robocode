using System.IO;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    public sealed class BattleEndedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100;
        private const long serialVersionUID = 1L;
        private readonly bool aborted;
        private readonly BattleResults results;

        public BattleEndedEvent(bool aborted, BattleResults results)
        {
            this.aborted = aborted;
            this.results = results;
        }

        internal static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper(null);
        }

        internal override sealed void dispatch(IBasicRobot robot1, IRobotStatics statics, IGraphics graphics2D)
        {
            if (robot1 != null)
            {
                var listener = robot1.getBasicEventListener() as IBasicEvents2;
                if (listener != null)
                {
                    listener.onBattleEnded(this);
                }
            }
        }

        internal override sealed int getDefaultPriority()
        {
            return 100;
        }

        public override sealed int getPriority()
        {
            return 100;
        }

        public BattleResults getResults()
        {
            return results;
        }

        internal override byte getSerializationType()
        {
            return 0x20;
        }

        public bool isAborted()
        {
            return aborted;
        }

        internal override sealed bool isCriticalEvent()
        {
            return true;
        }

        #region Nested type: SerializableHelper

        internal sealed class SerializableHelper : ISerializableHelper
        {
            private SerializableHelper()
            {
            }

            internal SerializableHelper(BattleEndedEvent a1) : this()
            {
            }

            #region ISerializableHelper Members

            public object deserialize(RbSerializer serializer, BinaryReader br)
            {
                return new BattleEndedEvent(serializer.deserializeBoolean(br),
                                            (BattleResults) serializer.deserializeAny(br));
            }

            public void serialize(RbSerializer serializer, BinaryWriter bw, object obj)
            {
                var event2 = (BattleEndedEvent) obj;
                serializer.serialize(bw, event2.aborted);
                serializer.serialize(bw, 8, event2.results);
            }

            public int sizeOf(RbSerializer serializer1, object obj1)
            {
                var event2 = (BattleEndedEvent) obj1;
                return (2 + serializer1.sizeOf(8, event2.results));
            }

            #endregion
        }

        #endregion
    }
}