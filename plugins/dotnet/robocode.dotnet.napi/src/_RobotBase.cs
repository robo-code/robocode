using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.io;
using java.lang;
using robocode.exception;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;

namespace robocode
{
    [Implements(new[] {"robocode.robotinterfaces.IBasicRobot", "java.lang.Runnable"})]
    public abstract class _RobotBase : Object, IBasicRobot, Runnable
    {
        public PrintStream @out;
        internal IBasicRobotPeer peer;

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x2c)]
        internal _RobotBase()
        {
        }

        #region IBasicRobot Members

        [HideFromReflection]
        public abstract IBasicEvents getBasicEventListener();

        [HideFromReflection]
        public abstract Runnable getRobotRunnable();

        public void setOut(PrintStream @out)
        {
            this.@out = @out;
        }

        public void setPeer(IBasicRobotPeer peer)
        {
            this.peer = peer;
        }

        #endregion

        #region Runnable Members

        [HideFromReflection]
        public abstract void run();

        #endregion

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x21, 0x6b, 0x89})]
        internal static void uninitializedException()
        {
            string str = Thread.currentThread().getStackTrace()[2].getMethodName();
            throw new RobotException(
                new StringBuilder().append("You cannot call the ").append(str).append(
                    "() method before your run() method is called, or you are using a Robot object that the game doesn't know about.")
                    .toString());
        }
    }
}