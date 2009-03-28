using System.Collections.Generic;
using System.Runtime.Serialization;

namespace robocode.robotinterfaces.peer
{
    public interface ITeamRobotPeer : IAdvancedRobotPeer
    {
        void broadcastMessage(ISerializable s);

        List<MessageEvent> getMessageEvents();

        List<string> getTeammates();
        bool isTeammate(string str);

        void sendMessage(string str, ISerializable s);
    }
}