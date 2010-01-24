#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    ///   A RoundEndedEvent is sent to {@link Robot#onRoundEnded(RoundEndedEvent)
    ///   onRoundEnded()} when a round has ended.
    ///   You can use the information contained in this event to determine which round that has ended.
    ///   @author Flemming N. Larsen (original)
    ///   @see Robot#onRoundEnded(RoundEndedEvent)
    ///   @since 1.7.2
    /// </summary>
    public class RoundEndedEvent : Event
    {
        private static long serialVersionUID = 1L;
        private static int DEFAULT_PRIORITY = 110; // System event -> cannot be changed!

        private int round;
        private int turns;
        private int totalTurns;


        /// <summary>
        ///   Called by the game to create a new RoundEndedEvent.
        /// </summary>
        /// <param name="round">
        ///   the round that has ended (zero-indexed)
        /// </param>
        /// <param name="turns">
        ///   the number of turns that this round reached
        /// </param>
        /// <param name="totalTurns">
        ///   the total number of turns reached in the battle when this round ended
        /// </param>
        public RoundEndedEvent(int round, int turns, int totalTurns)
        {
            this.round = round;
            this.turns = turns;
            this.totalTurns = totalTurns;
        }

        /// <returns>
        ///   the round that ended (zero-indexed).
        /// </returns>
        public int getRound()
        {
            return round;
        }

        /// <returns>
        ///   the number of turns that this round reached.
        /// </returns>
        public int getTurns()
        {
            return turns;
        }

        /// <returns>
        ///   the total number of turns reached in the battle when this round ended.
        /// </returns>
        public int getTotalTurns()
        {
            return totalTurns;
        }

        internal override sealed int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

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
