using System.IO;
using System.Text;
using robocode.exception;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;

namespace robocode
{
    public abstract class _RobotBase : IBasicRobot, IRunnable
    {
        private TextWriter @out;

        internal IBasicRobotPeer peer;

        internal _RobotBase()
        {
        }

        public TextWriter Out
        {
            get { return @out; }
        }

        #region IBasicRobot Members

        public abstract IBasicEvents getBasicEventListener();

        public abstract IRunnable getRobotRunnable();

        public void setOut(TextWriter @out)
        {
            this.@out = @out;
        }

        public void setPeer(IBasicRobotPeer peer)
        {
            this.peer = peer;
        }

        #endregion

        #region IRunnable Members

        public abstract void run();

        #endregion

        internal static void uninitializedException()
        {
            var sb = new StringBuilder();
            string str = "TODO methodname"; //TODO System.Threading.CompressedStack.Capture().
            throw new RobotException(
                sb.Append("You cannot call the ").Append(str).Append(
                    "() method before your run() method is called, or you are using a Robot object that the game doesn't know about.")
                    .ToString());
        }
    }
}