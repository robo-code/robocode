﻿#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using java.lang;
using net.sf.jni4net.jni;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.serialization;
using robocode;

namespace net.sf.robocode.dotnet.host.seed
{
    internal class HostingShell : AppDomainShell, IHostingRobotProxy
    {
        private readonly RbSerializer serializer = new RbSerializer();

        public HostingShell(IRobotRepositoryItem itemSpecification,
                            IHostManager hostManager, IRobotPeer peer,
                            IRobotStatics jstatics, string dllFileName)
        {
            Init(true);
            Open(dllFileName);
            domain.SetData("hostManager", ((IJvmProxy) hostManager).JvmHandle);
            domain.SetData("peer", ((IJvmProxy) peer).JvmHandle);

            var statics = serializer.ConvertJ2C<RobotStatics>(RbSerializerN.RobotStatics_TYPE, (Object) jstatics);

            domain.SetData("statics", statics);
            //domain.SetData("specification", ((IJvmProxy)robotSpecification).JvmHandle);
            domain.SetData("item", ((IJvmProxy) itemSpecification).JvmHandle);
            domain.DoCallBack(HostingSeed.Construct);
            robotPeer = peer;
        }

        #region IHostingRobotProxy Members

        public void startRound(Object aCommands, Object aStatus)
        {
            var commands = serializer.ConvertJ2C<ExecCommands>(RbSerializerN.ExecCommands_TYPE, aCommands);
            var status = serializer.ConvertJ2C<RobotStatus>(RbSerializerN.RobotStatus_TYPE, aStatus);

            domain.SetData("commands", commands);
            domain.SetData("status", status);
            domain.DoCallBack(HostingSeed.StartRound);
        }

        public void forceStopThread()
        {
            domain.DoCallBack(HostingSeed.ForceStopThread);
        }

        public void waitForStopThread()
        {
            domain.DoCallBack(HostingSeed.WaitForStopThread);
        }

        public void cleanup()
        {
            domain.DoCallBack(HostingSeed.Cleanup);
            Dispose();
        }

        #endregion
    }
}