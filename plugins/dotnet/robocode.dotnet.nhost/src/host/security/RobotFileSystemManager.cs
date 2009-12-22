using System.IO;

namespace net.sf.robocode.dotnet.host.security
{
    public class RobotFileSystemManager
    {
        private int maxQuota;
        private string writableRootDirectory;
        private string readableRootDirectory;

        public RobotFileSystemManager(int maxQuota, string writableRootDirectory, string readableRootDirectory, string rootFile)
        {
            this.maxQuota = maxQuota;
            this.writableRootDirectory = Path.GetFullPath(writableRootDirectory);
            this.readableRootDirectory = Path.GetFullPath(readableRootDirectory);
        }

        public int getMaxQuota()
        {
            return maxQuota;
        }

        public int getQuotaUsed()
        {
            return 0;
        }

        public string getReadableDirectory()
        {
            return readableRootDirectory;
        }

        public string getWritableDirectory()
        {
            return writableRootDirectory;
        }
    }
}
