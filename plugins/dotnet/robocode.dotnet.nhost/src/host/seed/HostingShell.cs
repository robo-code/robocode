/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using java.lang;
using net.sf.jni4net.jni;
using net.sf.jni4net.utils;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.serialization;
using Robocode;

namespace net.sf.robocode.dotnet.host.seed
{
    public class HostingShell : AppDomainShell, IHostingRobotProxy
    {
        private readonly RbSerializer serializer = new RbSerializer();

        public HostingShell(IRobotItem itemSpecification,
                            IHostManager hostManager, IRobotPeer peer,
                            IRobotStatics jstatics, string dllFileName)
        {
            robotPeer = peer;

            Init(true);
            Open(dllFileName);
            JniGlobalHandle hmHandle = ((IJvmProxy) hostManager).JvmHandle;
            JniGlobalHandle peerhandle = ((IJvmProxy)peer).JvmHandle;
            JniGlobalHandle itemHandle = ((IJvmProxy)itemSpecification).JvmHandle;

            domain.SetData("hostManager", hmHandle.DangerousGetHandle());
            domain.SetData("peer", peerhandle.DangerousGetHandle());
            domain.SetData("item", itemHandle.DangerousGetHandle());

            var statics = serializer.ConvertJ2C<RobotStatics>(RbSerializerN.RobotStatics_TYPE, (Object) jstatics);
            domain.SetData("statics", statics);
            domain.SetData("robotName", statics.getName());
            try
            {
                domain.DoCallBack(HostingSeed.Construct);
            }
            catch (Exception)
            {
                robotPeer.punishBadBehavior(BadBehavior.SECURITY_VIOLATION);
            }

            hmHandle.HoldThisHandle();
            peerhandle.HoldThisHandle();
            itemHandle.HoldThisHandle();
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