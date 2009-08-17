using System;
using System.Collections.Generic;
using System.IO;
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
        private readonly MemoryStream byteStreamWriter;

        public TeamRobotProxy(IRobotRepositoryItem specification, IHostManager hostManager, IRobotPeer peer,
                              RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
            byteStreamWriter = new MemoryStream(MAX_MESSAGE_SIZE);
        }

        // team
        public string[] getTeammates()
        {
            getCall();
            throw new NotImplementedException();
            //TODO return statics.getTeammates();
        }

        public bool isTeammate(String name)
        {
            getCall();
            if (name == statics.getName())
            {
                return true;
            }
            throw new NotImplementedException();
            /* TODO 
            String[] teammates = statics.getTeammates();

            if (teammates != null) {
                for (String mate : teammates) {
                    if (mate.equals(name)) {
                        return true;
                    }
                }
            }
            return false;*/
        }

        public void broadcastMessage(object message)
        {
            sendMessage(null, message);
        }

        public void sendMessage(String name, object message)
        {
            setCall();

            throw new NotImplementedException();
            /* TODO 
            try {
                if (!statics.isTeamRobot()) {
                    throw new IOException("You are not on a team.");
                }
                byteStreamWriter.reset();
                ObjectOutputStream objectStreamWriter = new ObjectOutputStream(byteStreamWriter);

                objectStreamWriter.writeObject(message);
                objectStreamWriter.flush();
                byteStreamWriter.flush();
                final byte[] bytes = byteStreamWriter.toByteArray();

                objectStreamWriter.reset();
                if (bytes.length > MAX_MESSAGE_SIZE) {
                    throw new IOException("Message too big. " + bytes.length + ">" + MAX_MESSAGE_SIZE);
                }
                commands.getTeamMessages().add(new TeamMessage(getName(), name, bytes));
            } catch (IOException e) {
                out.printStackTrace(e);
                throw e;
            }*/
        }

        protected override sealed void loadTeamMessages(List<TeamMessage> teamMessages)
        {
            if (teamMessages == null)
            {
                return;
            }
            throw new NotImplementedException();
            /* TODO 
            for (TeamMessage teamMessage : teamMessages) {
                try {
                    ByteArrayInputStream byteStreamReader = new ByteArrayInputStream(teamMessage.message);

                    byteStreamReader.reset();
                    RobocodeObjectInputStream objectStreamReader = new RobocodeObjectInputStream(byteStreamReader,
                            (ClassLoader) robotClassLoader);
                    Serializable message = (Serializable) objectStreamReader.readObject();
                    final MessageEvent event = new MessageEvent(teamMessage.sender, message);

                    eventManager.add(event);
                } catch (IOException e) {
                    out.printStackTrace(e);
                } catch (ClassNotFoundException e) {
                    out.printStackTrace(e);
                }
            }*/
        }

        // events
        public IList<MessageEvent> getMessageEvents()
        {
            getCall();
            return eventManager.getMessageEvents();
        }
    }
}
