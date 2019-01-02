/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Collections.Generic;
using System.IO;
using System.Security.Permissions;
using net.sf.robocode.dotnet.host.proxies;
using net.sf.robocode.io;

namespace net.sf.robocode.dotnet.host.security
{
    [FileIOPermission(SecurityAction.Assert, Unrestricted = true)]
    public class RobotFileSystemManager
    {
        private long maxQuota;
        private string readableRootDirectory;
        private List<QuotaStream> streams = new List<QuotaStream>();
        private long usedQuota;
        private string writableRootDirectory;
        private HostingRobotProxy hostingProxy;

        public RobotFileSystemManager(HostingRobotProxy hostingProxy , int maxQuota, string writableRootDirectory, string readableRootDirectory)
        {
            this.hostingProxy = hostingProxy;
            this.maxQuota = maxQuota;
            this.writableRootDirectory = Path.GetFullPath(writableRootDirectory);
            this.readableRootDirectory = Path.GetFullPath(readableRootDirectory);

            if (Directory.Exists(writableRootDirectory))
            {
                var di = new DirectoryInfo(writableRootDirectory);
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
                if (usedQuota + bytes > maxQuota)
                {
                    string message = "You have reached your filesystem quota: " + maxQuota + " bytes per robot.";
                    if (hostingProxy != null)
                    {
                        hostingProxy.punishSecurityViolation(message);
                    }
                    throw new AccessViolationException(message);
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
                if (Path.GetDirectoryName(filename).ToLower() != writableRootDirectory.ToLower())
                {
                    throw new AccessViolationException("Access is allowed only in " + writableRootDirectory + ". Use ");
                }
                if (!Directory.Exists(writableRootDirectory))
                {
                    Directory.CreateDirectory(writableRootDirectory);
                }
                var quotaStream = new QuotaStream(this, filename, FileMode.OpenOrCreate, FileAccess.ReadWrite,
                                                  FileShare.ReadWrite);
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