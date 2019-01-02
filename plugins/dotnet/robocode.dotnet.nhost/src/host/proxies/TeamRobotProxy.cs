/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Collections.Generic;
using System.IO;
using System.Runtime.Serialization;
using System.Runtime.Serialization.Formatters.Binary;
using System.Security.Permissions;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using Robocode;
using Robocode.RobotInterfaces.Peer;

namespace net.sf.robocode.dotnet.host.proxies
{
    internal class TeamRobotProxy : AdvancedRobotProxy, ITeamRobotPeer
    {
        private const int MAX_MESSAGE_SIZE = 32768;

        public TeamRobotProxy(IRobotItem specification, IHostManager hostManager, IRobotPeer peer,
                              RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
        }

        // team

        #region ITeamRobotPeer Members

        public string[] GetTeammates()
        {
            GetCall();
            return statics.getTeammates();
        }

        public bool IsTeammate(String name)
        {
            GetCall();
            if (name == statics.getName())
            {
                return true;
            }
            String[] teammates = statics.getTeammates();

            if (teammates != null)
            {
                foreach (string mate in teammates)
                {
                    if (mate == name)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        public void BroadcastMessage(object message)
        {
            SendMessage(null, message);
        }

        [SecurityPermission(SecurityAction.Assert, Flags = SecurityPermissionFlag.SerializationFormatter)]
        public void SendMessage(String name, object message)
        {
            SetCall();

            try
            {
                if (!statics.IsTeamRobot())
                {
                    throw new IOException("You are not on a team.");
                }
                using (var byteStreamWriter = new MemoryStream(MAX_MESSAGE_SIZE))
                {
                    var bf = new BinaryFormatter(new SerializationGuard(), new StreamingContext());
                    bf.Serialize(byteStreamWriter, message);
                    byte[] bytes = byteStreamWriter.ToArray();
                    commands.getTeamMessages().Add(new TeamMessage(GetName(), name, bytes));
                }
            }
            catch (Exception e)
            {
                output.WriteLine(e);
                throw;
            }
        }

        // events
        public IList<MessageEvent> GetMessageEvents()
        {
            GetCall();
            return eventManager.GetMessageEvents();
        }

        #endregion

        [SecurityPermission(SecurityAction.Assert, Flags = SecurityPermissionFlag.SerializationFormatter)]
        protected override sealed void loadTeamMessages(List<TeamMessage> teamMessages)
        {
            if (teamMessages == null)
            {
                return;
            }
            foreach (TeamMessage teamMessage in teamMessages)
            {
                try
                {
                    using (var ms = new MemoryStream(teamMessage.message))
                    {
                        var bf = new BinaryFormatter();
                        object message = bf.Deserialize(ms);
                        var evnt = new MessageEvent(teamMessage.sender, message);

                        eventManager.Add(evnt);
                    }
                }
                catch (Exception e)
                {
                    output.WriteLine(e);
                }
            }
        }

        #region Nested type: SerializationGuard

        private class SerializationGuard : SurrogateSelector
        {
            public override ISerializationSurrogate GetSurrogate(Type type, StreamingContext context,
                                                                 out ISurrogateSelector selector)
            {
                if (typeof (MarshalByRefObject).IsAssignableFrom(type))
                {
                    throw new AccessViolationException("Messages should not contain MarshalByRefObject objects");
                }
                return base.GetSurrogate(type, context, out selector);
            }
        }

        #endregion
    }
}
