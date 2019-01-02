/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.IO;
using net.sf.jni4net;
using net.sf.robocode.dotnet.host.seed;
using net.sf.robocode.dotnet.repository.root;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using robocode.control;
using Object = java.lang.Object;
using String = java.lang.String;

namespace net.sf.robocode.dotnet.host
{
    public class DotNetHost : IHost
    {
        #region IHost Members

        public IHostingRobotProxy createRobotProxy(IHostManager hostManager, Object robotSpecification, IRobotStatics statics, IRobotPeer peer)
        {
            Object s = HiddenAccess.getFileSpecification(robotSpecification);
            var itemSpecification = Bridge.Cast<IRobotItem>(s);
            string file = DllRootHelper.GetDllFileName(itemSpecification);
            HostingShell hostingShell = new HostingShell(itemSpecification, hostManager, peer, statics, file);
            return hostingShell;
        }

        public String[] getReferencedClasses(IRobotItem robotItem)
        {
            return new String[] {};
        }

        public RobotType getRobotType(IRobotItem robotItem, bool resolve, bool message)
        {
            return DllRootHelper.GetRobotType(robotItem);
        }

        #endregion

    }
}