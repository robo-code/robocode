/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using Robocode.RobotInterfaces;

namespace Robocode
{
    /// <summary>
    /// A SkippedTurnEvent is sent to <see cref="AdvancedRobot.OnSkippedTurn(SkippedTurnEvent)"/>
    /// OnSkippedTurn()} when your robot is forced to skipping a turn.
    /// You must take an action every turn in order to participate in the game.
    /// <example>
    ///   <code>
    ///   Thread.Sleep(1000);
    ///   </code>
    ///   will cause many SkippedTurnEvents, because you are not responding to the game.
    ///   If you receive 30 SkippedTurnEvents, you will be removed from the round.
    ///   <p/>
    ///   Instead, you should do something such as:
    ///   <code>
    ///   for (int i = 0; i &lt; 30; i++)
    ///   {
    ///       DoNothing(); // or perhaps Scan();
    ///   }
    ///   </code>
    /// </example>
    /// <p/>
    /// This event may also be generated if you are simply doing too much processing
    /// between actions, that is using too much processing power for the calculations
    /// etc. in your robot.
    /// <seealso cref="AdvancedRobot.OnSkippedTurn(SkippedTurnEvent)"/>
    /// <seealso cref="SkippedTurnEvent"/>
    /// </summary>
    public sealed class SkippedTurnEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100; // System event -> cannot be changed!;

        private readonly long skippedTurn;

        /// <summary>
        /// Called by the game to create a new SkippedTurnEvent.
        /// </summary>
        public SkippedTurnEvent(long skippedTurn)
        {
            this.skippedTurn = skippedTurn;
        }

        /// <summary>
        /// Returns the turn that was skipped.
        /// </summary>
        public long SkippedTurn
        {
            get { return skippedTurn; }
        }

        /// <inheritdoc />
        public override int Priority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (statics.IsAdvancedRobot())
            {
                IAdvancedEvents listener = ((IAdvancedRobot) robot).GetAdvancedEventListener();

                if (listener != null)
                {
                    listener.OnSkippedTurn(this);
                }
            }
        }

        internal override bool IsCriticalEvent
        {
            get { return true; }
        }

        internal override byte SerializationType
        {
            get { return RbSerializerN.SkippedTurnEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_LONG;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (SkippedTurnEvent)objec;

                serializer.serialize(buffer, obj.skippedTurn);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                long skippedTurn = buffer.getLong();

                return new SkippedTurnEvent(skippedTurn);
            }
        }
    }
}
//doc