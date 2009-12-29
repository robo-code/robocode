using System;
using System.Collections.Generic;
using System.IO;
using System.Security.Permissions;
using net.sf.robocode.io;

namespace net.sf.robocode.dotnet.host.security
{
    [FileIOPermission(SecurityAction.Assert, Unrestricted = true)]
    public class RobotFileSystemManager
    {
        private long maxQuota;
        private long usedQuota;
        private string writableRootDirectory;
        private string readableRootDirectory;
        List<QuotaStream> streams=new List<QuotaStream>();

        public RobotFileSystemManager(int maxQuota, string writableRootDirectory, string readableRootDirectory)
        {
            this.maxQuota = maxQuota;
            this.writableRootDirectory = Path.GetFullPath(writableRootDirectory);
            this.readableRootDirectory = Path.GetFullPath(readableRootDirectory);

            if (Directory.Exists(writableRootDirectory))
            {
                DirectoryInfo di=new DirectoryInfo(writableRootDirectory);
                foreach (FileInfo file in di.GetFiles())
                {
                    usedQuota += file.Length;
                }
            }
        }

        public long getMaxQuota()
        {
            return maxQuota;
        }

        public long getQuotaUsed()
        {
            lock (this)
            {
                return usedQuota;
            }
        }

        public long appendQuotaUsed(long bytes)
        {
            lock (this)
            {
                if (usedQuota+bytes>maxQuota)
                {
                    throw new AccessViolationException("File too big. Max " + maxQuota + " bytes per robot.");
                }
                usedQuota += bytes;
                return usedQuota;
            }
        }

        public long getDataQuotaAvailable()
        {
            return maxQuota - getQuotaUsed();
        }

        public Stream getDataFile(string filename)
        {
            lock (this)
            {
                if (Path.IsPathRooted(filename))
                {
                    filename = Path.GetFullPath(filename);
                }
                else
                {
                    filename = Path.GetFullPath(Path.Combine(writableRootDirectory, Path.GetFileName(filename)));
                }
                if (Path.GetDirectoryName(filename) != writableRootDirectory)
                {
                    throw new AccessViolationException("Access is allowed only in " + writableRootDirectory + ". Use ");
                }
                if (!Directory.Exists(writableRootDirectory))
                {
                    Directory.CreateDirectory(writableRootDirectory);
                }
                QuotaStream quotaStream = new QuotaStream(this, filename, FileMode.OpenOrCreate, FileAccess.ReadWrite, FileShare.ReadWrite);
                streams.Add(quotaStream);
                return quotaStream;
            }
        }

        public string getReadableDirectory()
        {
            return readableRootDirectory;
        }

        public string getWritableDirectory()
        {
            return writableRootDirectory;
        }

        public void cleanup()
        {
            foreach (QuotaStream stream in streams)
            {
                try
                {
                    stream.Dispose();
                }
                catch (Exception ex)
                {
                    LoggerN.logError(ex);
                }
            }
        }
    }
}
