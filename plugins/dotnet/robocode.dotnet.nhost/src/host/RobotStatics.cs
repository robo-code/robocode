#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Collections.Generic;
using net.sf.robocode.nio;
using net.sf.robocode.peer;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using Robocode;

namespace net.sf.robocode.host
{
    [Serializable]
    public class RobotStatics : IRobotStaticsN
    {
        private readonly BattleRules battleRules;
        private readonly int contestantIndex;
        private readonly string fullClassName;
        private readonly int index;
        private readonly bool isAdvancedRobot;
        private readonly bool isDroid;
        private readonly bool isInteractiveRobot;
        private readonly bool isJuniorRobot;
        private readonly bool isPaintRobot;
        private readonly bool isTeamLeader;
        private readonly bool isTeamRobot;
        private readonly string name;
        private readonly string shortClassName;
        private readonly string shortName;
        private readonly string teamName;
        private readonly string[] teammates;
        private readonly string veryShortName;

        public RobotStatics(bool isJuniorRobot, bool isInteractiveRobot, bool isPaintRobot, bool isAdvancedRobot,
                            bool isTeamRobot, bool isTeamLeader, bool isDroid, string name, string shortName,
                            string veryShortName, string fullClassName, string shortClassName, BattleRules battleRules,
                            string[] teammates, string teamName, int index, int contestantIndex)
        {
            this.isJuniorRobot = isJuniorRobot;
            this.isInteractiveRobot = isInteractiveRobot;
            this.isPaintRobot = isPaintRobot;
            this.isAdvancedRobot = isAdvancedRobot;
            this.isTeamRobot = isTeamRobot;
            this.isTeamLeader = isTeamLeader;
            this.isDroid = isDroid;
            this.name = name;
            this.shortName = shortName;
            this.veryShortName = veryShortName;
            this.fullClassName = fullClassName;
            this.shortClassName = shortClassName;
            this.battleRules = battleRules;
            this.teammates = teammates;
            this.teamName = teamName;
            this.index = index;
            this.contestantIndex = contestantIndex;
        }

        #region IRobotStaticsN Members

        public bool IsInteractiveRobot()
        {
            return isInteractiveRobot;
        }

        public bool IsPaintRobot()
        {
            return isPaintRobot;
        }

        public bool IsAdvancedRobot()
        {
            return isAdvancedRobot;
        }

        public bool IsTeamRobot()
        {
            return isTeamRobot;
        }

        #endregion

        public bool IsJuniorRobot()
        {
            return isJuniorRobot;
        }

        public bool IsTeamLeader()
        {
            return isTeamLeader;
        }

        public bool IsDroid()
        {
            return isDroid;
        }

        public string getName()
        {
            return name;
        }

        public string getShortName()
        {
            return shortName;
        }

        public string getVeryShortName()
        {
            return veryShortName;
        }

        public string getFullClassName()
        {
            return fullClassName;
        }

        public string getShortClassName()
        {
            return shortClassName;
        }

        public BattleRules getBattleRules()
        {
            return battleRules;
        }

        public string[] getTeammates()
        {
            if (teammates == null)
            {
                return null;
            }
            var copy = new string[teammates.Length];
            Array.Copy(teammates, copy, teammates.Length);
            return copy;
        }

        public string getTeamName()
        {
            return teamName;
        }

        public int getIndex()
        {
            return index;
        }

        public int getContestIndex()
        {
            return contestantIndex;
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        #region Nested type: SerializableHelper

        private class SerializableHelper : ISerializableHelperN
        {
            #region ISerializableHelperN Members

            public int sizeOf(RbSerializerN serializer, object obje)
            {
                var obj = (RobotStatics) obje;
                int size = RbSerializerN.SIZEOF_TYPEINFO + RbSerializerN.SIZEOF_BOOL*7
                           + serializer.sizeOf(obj.name)
                           + serializer.sizeOf(obj.shortName)
                           + serializer.sizeOf(obj.veryShortName)
                           + serializer.sizeOf(obj.fullClassName)
                           + serializer.sizeOf(obj.shortClassName)
                           + RbSerializerN.SIZEOF_INT*5
                           + RbSerializerN.SIZEOF_DOUBLE
                           + RbSerializerN.SIZEOF_LONG;
                if (obj.teammates != null)
                {
                    foreach (String mate in obj.teammates)
                    {
                        size += serializer.sizeOf(mate);
                    }
                }
                size += RbSerializerN.SIZEOF_INT;
                size += serializer.sizeOf(obj.teamName);

                return size;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object obje)
            {
                var obj = (RobotStatics) obje;

                serializer.serialize(buffer, obj.isJuniorRobot);
                serializer.serialize(buffer, obj.isInteractiveRobot);
                serializer.serialize(buffer, obj.isPaintRobot);
                serializer.serialize(buffer, obj.isAdvancedRobot);
                serializer.serialize(buffer, obj.isTeamRobot);
                serializer.serialize(buffer, obj.isTeamLeader);
                serializer.serialize(buffer, obj.isDroid);
                serializer.serialize(buffer, obj.name);
                serializer.serialize(buffer, obj.shortName);
                serializer.serialize(buffer, obj.veryShortName);
                serializer.serialize(buffer, obj.fullClassName);
                serializer.serialize(buffer, obj.shortClassName);
                serializer.serialize(buffer, obj.battleRules.BattlefieldWidth);
                serializer.serialize(buffer, obj.battleRules.BattlefieldHeight);
                serializer.serialize(buffer, obj.battleRules.NumRounds);
                serializer.serialize(buffer, obj.battleRules.GunCoolingRate);
                serializer.serialize(buffer, obj.battleRules.InactivityTime);
                if (obj.teammates != null)
                {
                    foreach (string mate in obj.teammates)
                    {
                        serializer.serialize(buffer, mate);
                    }
                }
                buffer.putInt(-1);
                serializer.serialize(buffer, obj.teamName);
                serializer.serialize(buffer, obj.index);
                serializer.serialize(buffer, obj.contestantIndex);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                bool isJuniorRobot = serializer.deserializeBoolean(buffer);
                bool isInteractiveRobot = serializer.deserializeBoolean(buffer);
                bool isPaintRobot = serializer.deserializeBoolean(buffer);
                bool isAdvancedRobot = serializer.deserializeBoolean(buffer);
                bool isTeamRobot = serializer.deserializeBoolean(buffer);
                bool isTeamLeader = serializer.deserializeBoolean(buffer);
                bool isDroid = serializer.deserializeBoolean(buffer);
                string name = serializer.deserializeString(buffer);
                string shortName = serializer.deserializeString(buffer);
                string veryShortName = serializer.deserializeString(buffer);
                string fullClassName = serializer.deserializeString(buffer);
                string shortClassName = serializer.deserializeString(buffer);
                BattleRules battleRules = HiddenAccessN.createRules(
                    serializer.deserializeInt(buffer),
                    serializer.deserializeInt(buffer),
                    serializer.deserializeInt(buffer),
                    serializer.deserializeDouble(buffer),
                    serializer.deserializeLong(buffer)
                    );

                var teammates = new List<string>();
                object item = serializer.deserializeString(buffer);
                while (item != null)
                {
                    if (item is string)
                    {
                        teammates.Add((string) item);
                    }
                    item = serializer.deserializeString(buffer);
                }

                string teamName = serializer.deserializeString(buffer);
                int index = serializer.deserializeInt(buffer);
                int contestantIndex = serializer.deserializeInt(buffer);


                return new RobotStatics(
                    isJuniorRobot,
                    isInteractiveRobot,
                    isPaintRobot,
                    isAdvancedRobot,
                    isTeamRobot,
                    isTeamLeader,
                    isDroid,
                    name,
                    shortName,
                    veryShortName,
                    fullClassName,
                    shortClassName,
                    battleRules,
                    teammates.ToArray(),
                    teamName,
                    index,
                    contestantIndex
                    );
            }

            #endregion
        }

        #endregion
    }
}