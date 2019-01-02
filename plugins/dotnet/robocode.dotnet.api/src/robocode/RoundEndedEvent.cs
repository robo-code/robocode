/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    ///   A RoundEndedEvent is sent to <see cref="Robot.OnRoundEnded(RoundEndedEvent)"/> when a round has ended.
    ///   You can use the information contained in this event to determine which round that has ended.
    ///   <seealso cref="Robot.OnRoundEnded(RoundEndedEvent)"/>
    /// </summary>
    public class RoundEndedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 110; // System event -> cannot be changed!

        private readonly int round;
        private readonly int turns;
        private readonly int totalTurns;


        /// <summary>
        ///   Called by the game to create a new RoundEndedEvent.
        /// </summary>
        /// <param name="round">The round that has ended (zero-indexed)</param>
        /// <param name="turns">The number of turns that this round reached</param>
        /// <param name="totalTurns">The total number of turns reached in the battle when this round ended</param>
        public RoundEndedEvent(int round, int turns, int totalTurns)
        {
            this.round = round;
            this.turns = turns;
            this.totalTurns = totalTurns;
        }

        /// <value>
        ///   The round that ended (zero-indexed).
        /// </value>
        public int Round
        {
            get { return round; }
        }

        /// <value>
        ///   The number of turns that this round reached.
        /// </value>
        public int Turns
        {
            get { return turns; }
        }

        /// <value>
        ///   The total number of turns reached in the battle when this round ended.
        /// </value>
        public int TotalTurns
        {
            get { return totalTurns; }
        }

        internal override sealed int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <inheritdoc />
        public override sealed int Priority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal override sealed void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (robot != null)
            {
                IBasicEvents3 listener = robot.GetBasicEventListener() as IBasicEvents3;


                if (listener != null)
                {
                    listener.OnRoundEnded(this);
                }
            }
        }

        internal override sealed bool IsCriticalEvent
        {
            get { return true; }
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.RoundEndedEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, Object obje)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + 3*RbSerializerN.SIZEOF_INT;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, Object obje)
            {
                RoundEndedEvent evnt = (RoundEndedEvent) obje;

                serializer.serialize(buffer, evnt.round);
                serializer.serialize(buffer, evnt.turns);
                serializer.serialize(buffer, evnt.totalTurns);
            }

            public Object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                int round = serializer.deserializeInt(buffer);
                int turns = serializer.deserializeInt(buffer);
                int totalTurns = serializer.deserializeInt(buffer);

                return new RoundEndedEvent(round, turns, totalTurns);
            }
        }
    }
}
//doc