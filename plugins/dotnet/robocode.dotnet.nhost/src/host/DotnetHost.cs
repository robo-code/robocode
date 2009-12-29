#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System.IO;
using java.lang;
using net.sf.jni4net;
using net.sf.robocode.dotnet.host.seed;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using robocode.control;

namespace net.sf.robocode.dotnet.host
{
    public class DotnetHost : IHost
    {
        #region IHost Members

        public IHostingRobotProxy createRobotProxy(IHostManager hostManager, RobotSpecification robotSpecification,
                                                   IRobotStatics statics, IRobotPeer peer)
        {
            Object s = HiddenAccess.getFileSpecification(robotSpecification);
            var itemSpecification = Bridge.Cast<IRobotRepositoryItem>(s);
            string file = GetDllFileName(itemSpecification);
            return new HostingShell(itemSpecification, hostManager, peer, statics, file);
        }

        public String[] getReferencedClasses(IRobotRepositoryItem par0)
        {
            return new String[] {};
        }

        public RobotType getRobotType(IRobotRepositoryItem robotRepositoryItem, bool resolve, bool message)
        {
            string file = GetDllFileName(robotRepositoryItem);
            if (!File.Exists(file))
            {
                return RobotType.Invalid;
            }
            using (var shell = new AppDomainShell(file))
            {
                return shell.GetRobotType(robotRepositoryItem.getFullClassName());
            }
        }

        #endregion

        private static string GetDllFileName(IRobotRepositoryItem robotRepositoryItem)
        {
            string url = robotRepositoryItem.getRobotClassPath().getFile();
            return url.Substring(1, url.LastIndexOf(".dll!/") + 3);
        }
    }
}