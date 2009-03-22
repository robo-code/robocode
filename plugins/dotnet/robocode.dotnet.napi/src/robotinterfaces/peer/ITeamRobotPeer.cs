using IKVM.Attributes;
using java.io;
using java.util;

namespace robocode.robotinterfaces.peer
{
    [Implements(new[] {"robocode.robotinterfaces.peer.IAdvancedRobotPeer"})]
    public interface ITeamRobotPeer : IBasicRobotPeer, IStandardRobotPeer, IAdvancedRobotPeer
    {
        [Throws(new[] {"java.io.IOException"})]
        void broadcastMessage(Serializable s);

        [Signature("()Ljava/util/List<Lrobocode/MessageEvent;>;")]
        List getMessageEvents();

        string[] getTeammates();
        bool isTeammate(string str);

        [Throws(new[] {"java.io.IOException"})]
        void sendMessage(string str, Serializable s);
    }
}