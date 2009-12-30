/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
using System.Drawing;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.serialization;
using robocode.robotinterfaces;

namespace robocode
{
    /// <summary>
    /// A SkippedTurnEvent is sent to {@link AdvancedRobot#OnSkippedTurn(SkippedTurnEvent)
    /// OnSkippedTurn()} when your robot is forced to skipping a turn.
    /// You must take an action every turn in order to participate in the game.
    /// For example,
    /// <pre>
    ///    try {
    ///        Thread.sleep(1000);
    ///    } catch (InterruptedException e) {
    ///        // Immediately reasserts the exception by interrupting the caller thread
    ///        // itself.
    ///        Thread.currentThread().interrupt();
    ///    }
    /// </pre>
    /// will cause many SkippedTurnEvents, because you are not responding to the game.
    /// If you receive 30 SkippedTurnEvents, you will be removed from the round.
    /// <p/>
    /// Instead, you should do something such as:
    /// <pre>
    ///     for (int i = 0; i < 30; i++) {
    ///         DoNothing(); // or perhaps Scan();
    ///     }
    /// </pre>
    /// <p/>
    /// This evnt may also be generated if you are simply doing too much processing
    /// between actions, that is using too much processing power for the calculations
    /// etc. in your robot.
    ///
    /// @author Mathew A. Nelson (original)
    /// @see AdvancedRobot#OnSkippedTurn(SkippedTurnEvent)
    /// @see SkippedTurnEvent
    /// </summary>
    public sealed class SkippedTurnEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100; // System evnt -> cannot be changed!;

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public override int Priority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
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

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override bool IsCriticalEvent
        {
            get { return true; }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
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
                return RbSerializerN.SIZEOF_TYPEINFO;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                return new SkippedTurnEvent();
            }
        }
    }
}
//happy