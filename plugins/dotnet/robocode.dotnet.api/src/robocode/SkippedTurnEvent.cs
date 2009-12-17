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
    /// A SkippedTurnEvent is sent to {@link AdvancedRobot#onSkippedTurn(SkippedTurnEvent)
    /// onSkippedTurn()} when your robot is forced to skipping a turn.
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
    ///         doNothing(); // or perhaps scan();
    ///     }
    /// </pre>
    /// <p/>
    /// This evnt may also be generated if you are simply doing too much processing
    /// between actions, that is using too much processing power for the calculations
    /// etc. in your robot.
    ///
    /// @author Mathew A. Nelson (original)
    /// @see AdvancedRobot#onSkippedTurn(SkippedTurnEvent)
    /// @see SkippedTurnEvent
    /// </summary>
    public sealed class SkippedTurnEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100; // System evnt -> cannot be changed!;

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public override int getPriority()
        {
            return DEFAULT_PRIORITY;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override int getDefaultPriority()
        {
            return DEFAULT_PRIORITY;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override void dispatch(IBasicRobot robot, IRobotStatics statics, Graphics graphics)
        {
            if (statics.isAdvancedRobot())
            {
                IAdvancedEvents listener = ((IAdvancedRobot) robot).getAdvancedEventListener();

                if (listener != null)
                {
                    listener.onSkippedTurn(this);
                }
            }
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override bool isCriticalEvent()
        {
            return true;
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        internal override byte getSerializationType()
        {
            return RbnSerializer.SkippedTurnEvent_TYPE;
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelper
        {
            public int sizeOf(RbnSerializer serializer, object objec)
            {
                return RbnSerializer.SIZEOF_TYPEINFO;
            }

            public void serialize(RbnSerializer serializer, ByteBuffer buffer, object objec)
            {
            }

            public object deserialize(RbnSerializer serializer, ByteBuffer buffer)
            {
                return new SkippedTurnEvent();
            }
        }
    }
}
//happy