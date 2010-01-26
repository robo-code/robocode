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
    /// 
    ///<summary>
    ///  A BattleEndedEvent is sent to {@link Robot#OnBattleEnded(BattleEndedEvent)
    ///  OnBattleEnded()} when the battle is ended.
    ///  You can use the information contained in this evnt to determine if the
    ///  battle was aborted and also get the results of the battle.
    ///
    ///  @author Pavel Savara (original)
    ///  @see BattleResults
    ///  @see Robot#OnBattleEnded(BattleEndedEvent)
    ///  @since 1.6.1
    ///</summary>
    [Serializable]
    public sealed class BattleEndedEvent : Event
    {
        private const int DEFAULT_PRIORITY = 100; // System evnt -> cannot be changed!;

        private readonly bool aborted;
        private readonly BattleResults results;

        /// 
        ///<summary>
        ///  Called by the game to create a new BattleEndedEvent.
        ///
        ///  @param aborted {@code true} if the battle was aborted; {@code false} otherwise.
        ///  @param results the battle results
        ///</summary>
        public BattleEndedEvent(bool aborted, BattleResults results)
        {
            this.aborted = aborted;
            this.results = results;
        }

        /// 
        ///<summary>
        ///  Checks if this battle was aborted.
        ///
        ///  @return {@code true} if the battle was aborted; {@code false} otherwise.
        ///</summary>
        public bool IsAborted
        {
            get { return aborted; }
        }

        /// 
        ///<summary>
        ///  Returns the battle results.
        ///
        ///  @return the battle results.
        ///</summary>
        public BattleResults Results
        {
            get { return results; }
        }

        /// <inheritdoc />
        internal override int DefaultPriority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <inheritdoc />
        public override int Priority
        {
            get { return DEFAULT_PRIORITY; }
        }

        /// <inheritdoc />
        internal override void Dispatch(IBasicRobot robot, IRobotStaticsN statics, IGraphics graphics)
        {
            if (robot != null)
            {
                var listener = robot.GetBasicEventListener() as IBasicEvents2;
                if (listener != null)
                {
                    listener.OnBattleEnded(this);
                }
            }
        }

        /// <inheritdoc />
        internal override bool IsCriticalEvent
        {
            get { return true; }
        }

        /// <inheritdoc />
        internal override byte SerializationType
        {
            get { return RbSerializerN.BattleEndedEvent_TYPE; }
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                var obj = (BattleEndedEvent) objec;

                return RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_BOOL
                       + serializer.sizeOf(RbSerializerN.BattleResults_TYPE, obj.results);
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (BattleEndedEvent) objec;

                serializer.serialize(buffer, obj.aborted);
                serializer.serialize(buffer, RbSerializerN.BattleResults_TYPE, obj.results);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                bool aborted = serializer.deserializeBoolean(buffer);
                var results = (BattleResults) serializer.deserializeAny(buffer);

                return new BattleEndedEvent(aborted, results);
            }
        }
    }
}

//happpy