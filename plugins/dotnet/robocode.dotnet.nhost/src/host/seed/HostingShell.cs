using System;
using net.sf.jni4net;
using net.sf.jni4net.jni;
using net.sf.jni4net.nio;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode;
using robocode.control;
using ByteBuffer = java.nio.ByteBuffer;
using Object=java.lang.Object;

namespace net.sf.robocode.dotnet.host.seed
{
    class HostingShell : AppDomainShell, IHostingRobotProxy
    {
        RbSerializer serializer=new RbSerializer();

        public HostingShell(RobotSpecification robotSpecification, IRobotRepositoryItem itemSpecification, IHostManager hostManager, IRobotPeer peer,
                                  RobotStatics statics, string dllFileName) 
        {
            Init(dllFileName);
            domain.SetData("hostManager", ((IJvmProxy) hostManager).JvmHandle);
            domain.SetData("peer", ((IJvmProxy) peer).JvmHandle);
            domain.SetData("statics", ((IJvmProxy) statics).JvmHandle);
            domain.SetData("specification", ((IJvmProxy)robotSpecification).JvmHandle);
            domain.SetData("item", ((IJvmProxy)itemSpecification).JvmHandle);
            domain.DoCallBack(HostingSeed.Construct);
        }

        public void startRound(Object aCommands, Object aStatus)
        {
            ExecCommands commands = serializer.ConvertJ2C<ExecCommands>(RbSerializerN.ExecCommands_TYPE, aCommands);
            RobotStatus status = serializer.ConvertJ2C<RobotStatus>(RbSerializerN.RobotStatus_TYPE, aStatus);

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
            Dispose();
        }
    }
}
