using IKVM.Attributes;
using java.io;

namespace net.sf.robocode.security
{
    public interface IThreadManagerBase
    {
        [Throws(new[] {"java.io.IOException"})]
        FileOutputStream createRobotFileStream(string str, bool b);

        bool isSafeThread();
    }
}