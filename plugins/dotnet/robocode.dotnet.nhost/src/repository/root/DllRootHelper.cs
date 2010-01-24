#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.IO;
using net.sf.robocode.dotnet.host;
using net.sf.robocode.dotnet.host.seed;
using net.sf.robocode.repository;

namespace net.sf.robocode.dotnet.repository.root
{
    public static class DllRootHelper
    {
        private static AppDomainShell shell;

        public static void Refresh()
        {
            if (shell!=null)
            {
                shell.Dispose();
            }
            shell = new AppDomainShell();
            shell.Init(false);
        }

        public static string[] findItems(string dllPath)
        {
            string file = dllPath.Substring("file:/".Length);
            if (!File.Exists(file))
            {
                throw new ArgumentException();
            }

            if (shell != null)
            {
                shell.Open(file);
                return shell.FindRobots();
            }
            using (AppDomainShell localshell = new AppDomainShell())
            {
                localshell.Init(false);
                localshell.Open(file);
                return localshell.FindRobots();
            }
        }

        public static RobotType GetRobotType(IRobotRepositoryItem robotRepositoryItem)
        {
            string file = GetDllFileName(robotRepositoryItem);
            if (!File.Exists(file))
            {
                return RobotType.Invalid;
            }
            if (shell != null)
            {
                shell.Open(file);
                return shell.GetRobotType(robotRepositoryItem.getFullClassName());
            }
            using (AppDomainShell localshell = new AppDomainShell())
            {
                localshell.Init(false);
                localshell.Open(file);
                return localshell.GetRobotType(robotRepositoryItem.getFullClassName());
            }
        }

        public static string GetDllFileName(IRobotRepositoryItem robotRepositoryItem)
        {
            string url = robotRepositoryItem.getClassPathURL().getFile();
            return url.Substring(1, url.LastIndexOf(".dll!/") + 3);
        }
    }
}