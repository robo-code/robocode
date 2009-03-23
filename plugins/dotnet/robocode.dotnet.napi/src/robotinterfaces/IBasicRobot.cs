using System.IO;
using robocode.robotinterfaces.peer;

namespace robocode.robotinterfaces
{
    public interface IRunnable
    {
        void run();
    }

    public interface IBasicRobot
    {
        IBasicEvents getBasicEventListener();
        IRunnable getRobotRunnable();
        void setOut(TextWriter ps);
        void setPeer(IBasicRobotPeer ibrp);
    }
}