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
using robocode;
using robocode.robotinterfaces.peer;

namespace net.sf.robocode.dotnet.host.proxies
{
    internal class TeamRobotProxy : AdvancedRobotProxy, ITeamRobotPeer
    {
        private const int MAX_MESSAGE_SIZE = 32768;

        public TeamRobotProxy(IRobotRepositoryItem specification, IHostManager hostManager, IRobotPeer peer,
                              RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
        }

        // team
        public string[] getTeammates()
        {
            getCall();
            return statics.getTeammates();
        }

        public bool isTeammate(String name)
        {
            getCall();
            if (name == statics.getName())
            {
                return true;
            }
            String[] teammates = statics.getTeammates();

            if (teammates != null) {
                foreach (string mate in teammates) {
                    if (mate == name) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void broadcastMessage(object message)
        {
            sendMessage(null, message);
        }

        private class SerializationGuard : SurrogateSelector
        {
            public override ISerializationSurrogate GetSurrogate(Type type, StreamingContext context, out ISurrogateSelector selector)
            {
                if (typeof(MarshalByRefObject).IsAssignableFrom(type))
                {
                    throw new AccessViolationException("Messages should not contain MarshalByRefObject objects");
                }
                return base.GetSurrogate(type, context, out selector);
            }
        }

        [SecurityPermission(SecurityAction.Assert, Flags = SecurityPermissionFlag.SerializationFormatter)]
        public void sendMessage(String name, object message)
        {
            setCall();

            try {
                if (!statics.IsTeamRobot()) {
                    throw new IOException("You are not on a team.");
                }
                using (MemoryStream byteStreamWriter = new MemoryStream(MAX_MESSAGE_SIZE))
                {
                    BinaryFormatter bf=new BinaryFormatter(new SerializationGuard(), new StreamingContext());
                    bf.Serialize(byteStreamWriter, message);
                    byte[] bytes = byteStreamWriter.ToArray();
                    commands.getTeamMessages().Add(new TeamMessage(getName(), name, bytes));
                }
            } catch (Exception e) {
                output.WriteLine(e);
                throw;
            }
        }

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

                        eventManager.add(evnt);
                    }
                }
                catch (Exception e)
                {
                    output.WriteLine(e);
                }
            }
        }

        // events
        public IList<MessageEvent> getMessageEvents()
        {
            getCall();
            return eventManager.getMessageEvents();
        }
    }
}
