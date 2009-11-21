using System;
using java.nio;
using net.sf.jni4net;
using net.sf.jni4net.jni;
using net.sf.robocode.dotnet.peer;
using net.sf.robocode.host;
using net.sf.robocode.host.proxies;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode;
using robocode.control;
using Object=java.lang.Object;

namespace net.sf.robocode.dotnet.host.seed
{
    class HostingShell : AppDomainShell, IHostingRobotProxy
    {
        public HostingShell(RobotSpecification robotSpecification, IHostManager hostManager, IRobotPeer peer,
                                  RobotStatics statics, string dllFileName) 
        {
            Object s = HiddenAccess.getFileSpecification(robotSpecification);
            Init(dllFileName);
            domain.SetData("hostManager", ((IJvmProxy) hostManager).JvmHandle);
            domain.SetData("peer", ((IJvmProxy) peer).JvmHandle);
            domain.SetData("statics", ((IJvmProxy) statics).JvmHandle);
            domain.SetData("specification", ((IJvmProxy) s).JvmHandle);
        }

        /*public void StartRound(ExecCommands commands, RobotStatus status, string typeFullName)
        {
            domain.SetData("loadRobot", typeFullName);
            domain.SetData("commands", commands);
            domain.SetData("status", status);
            domain.DoCallBack(AppDomainSeed.StartRound);
        }*/


        public void startRound(Object par0, Object par1)
        {
            /*var commands = Bridge.Cast<ISerializableHelper>(par0);
            var status = Bridge.Cast<ISerializableHelper>(par0);
            domain.SetData("commands", commands.serialize());
            domain.SetData("status", status);
            domain.DoCallBack(HostingSeed.StartRound);*/
            ByteBuffer bb = ByteBuffer.allocateDirect(10);
            //JNIEnv.ThreadEnv.NewDirectByteBuffer();
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
