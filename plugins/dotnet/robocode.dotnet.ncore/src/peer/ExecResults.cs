using System;
using System.Collections.Generic;
using System.IO;
using net.sf.robocode.serialization;

namespace robocode.dotnet.ncore.peer
{
    public class ExecResults
    {
        private List<BulletStatus> bulletUpdates;
        private ExecCommands commands;
        private List<Event> events;
        private bool halt;
        private bool paintEnabled;
        private bool shouldWait;
        private RobotStatus status;
        private List<TeamMessage> teamMessages;

        public ExecResults(ExecCommands commands, RobotStatus status, List<Event> events, List<TeamMessage> teamMessages,
                           List<BulletStatus> bulletUpdates, bool halt, bool shouldWait, bool paintEnabled)
        {
            this.commands = commands;
            this.status = status;
            this.events = events;
            this.teamMessages = teamMessages;
            this.bulletUpdates = bulletUpdates;
            this.halt = halt;
            this.shouldWait = shouldWait;
            this.paintEnabled = paintEnabled;
        }

        private ExecResults()
        {
        }

        /*public static long getSerialVersionUID() {
		return serialVersionUID;
	}*/

        public ExecCommands getCommands()
        {
            return commands;
        }

        public void setCommands(ExecCommands commands)
        {
            this.commands = commands;
        }

        public RobotStatus getStatus()
        {
            return status;
        }

        public void setStatus(RobotStatus status)
        {
            this.status = status;
        }

        public List<Event> getEvents()
        {
            return events;
        }

        public void setEvents(List<Event> events)
        {
            this.events = events;
        }

        public List<TeamMessage> getTeamMessages()
        {
            return teamMessages;
        }

        public void setTeamMessages(List<TeamMessage> teamMessages)
        {
            this.teamMessages = teamMessages;
        }

        public List<BulletStatus> getBulletUpdates()
        {
            return bulletUpdates;
        }

        public void setBulletUpdates(List<BulletStatus> bulletUpdates)
        {
            this.bulletUpdates = bulletUpdates;
        }

        public bool isHalt()
        {
            return halt;
        }

        public void setHalt(bool halt)
        {
            this.halt = halt;
        }

        public bool isShouldWait()
        {
            return shouldWait;
        }

        public void setShouldWait(bool shouldWait)
        {
            this.shouldWait = shouldWait;
        }

        public bool isPaintEnabled()
        {
            return paintEnabled;
        }

        public void setPaintEnabled(bool paintEnabled)
        {
            this.paintEnabled = paintEnabled;
        }

        private static ISerializableHelper createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        #region Nested type: SerializableHelper

        private class SerializableHelper : ISerializableHelper
        {
            #region ISerializableHelper Members

            public int sizeOf(RbSerializer serializer, Object object1)
            {
                var obj = (ExecResults) object1;
                int size = RbSerializer.SIZEOF_TYPEINFO + 3*RbSerializer.SIZEOF_BOOL;

                size += serializer.sizeOf(RbSerializer.ExecCommands_TYPE, obj.commands);
                size += serializer.sizeOf(RbSerializer.RobotStatus_TYPE, obj.status);

                // events
                foreach (Event evnt in obj.events)
                {
                    size += serializer.sizeOf(evnt);
                }
                size += 1;

                // messages
                foreach (TeamMessage m in obj.teamMessages)
                {
                    size += serializer.sizeOf(RbSerializer.TeamMessage_TYPE, m);
                }
                size += 1;

                // bullets
                foreach (BulletStatus b in obj.bulletUpdates)
                {
                    size += serializer.sizeOf(RbSerializer.BulletStatus_TYPE, b);
                }
                size += 1;

                return size;
            }

            public void serialize(RbSerializer serializer, BinaryWriter buffer, Object object1)
            {
                var obj = (ExecResults) object1;

                serializer.serialize(buffer, obj.halt);
                serializer.serialize(buffer, obj.shouldWait);
                serializer.serialize(buffer, obj.paintEnabled);

                serializer.serialize(buffer, RbSerializer.ExecCommands_TYPE, obj.commands);
                serializer.serialize(buffer, RbSerializer.RobotStatus_TYPE, obj.status);

                foreach (Event evnt in obj.events)
                {
                    serializer.serialize(buffer, evnt);
                }
                buffer.Write(RbSerializer.TERMINATOR_TYPE);
                foreach (TeamMessage message in obj.teamMessages)
                {
                    serializer.serialize(buffer, RbSerializer.TeamMessage_TYPE, message);
                }
                buffer.Write(RbSerializer.TERMINATOR_TYPE);
                foreach (BulletStatus bulletStatus in obj.bulletUpdates)
                {
                    serializer.serialize(buffer, RbSerializer.BulletStatus_TYPE, bulletStatus);
                }
                buffer.Write(RbSerializer.TERMINATOR_TYPE);
            }

            public Object deserialize(RbSerializer serializer, BinaryReader buffer)
            {
                var res = new ExecResults();

                res.halt = serializer.deserializeBoolean(buffer);
                res.shouldWait = serializer.deserializeBoolean(buffer);
                res.paintEnabled = serializer.deserializeBoolean(buffer);

                res.commands = (ExecCommands) serializer.deserializeAny(buffer);
                res.status = (RobotStatus) serializer.deserializeAny(buffer);

                Object item = serializer.deserializeAny(buffer);

                res.events = new List<Event>();
                res.teamMessages = new List<TeamMessage>();
                res.bulletUpdates = new List<BulletStatus>();
                while (item != null)
                {
                    if (item is Event)
                    {
                        res.events.Add((Event) item);
                    }
                    item = serializer.deserializeAny(buffer);
                }
                item = serializer.deserializeAny(buffer);
                while (item != null)
                {
                    if (item is TeamMessage)
                    {
                        res.teamMessages.Add((TeamMessage) item);
                    }
                    item = serializer.deserializeAny(buffer);
                }
                item = serializer.deserializeAny(buffer);
                while (item != null)
                {
                    if (item is BulletStatus)
                    {
                        res.bulletUpdates.Add((BulletStatus) item);
                    }
                    item = serializer.deserializeAny(buffer);
                }
                return res;
            }

            #endregion
        }

        #endregion
    }
}