using java.io;
using java.lang;
using robocode.robotinterfaces.peer;

namespace robocode.robotinterfaces
{
    public interface IBasicRobot
    {
        IBasicEvents getBasicEventListener();
        Runnable getRobotRunnable();
        void setOut(PrintStream ps);
        void setPeer(IBasicRobotPeer ibrp);
    }
}