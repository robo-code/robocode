using System;
using System.Runtime.CompilerServices;
using IKVM.Attributes;
using java.io;
using java.util;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;

namespace robocode
{
    [Implements(new[] {"robocode.robotinterfaces.ITeamRobot", "robocode.robotinterfaces.ITeamEvents"})]
    public class TeamRobot : AdvancedRobot, IBasicRobot, IAdvancedRobot, ITeamRobot, ITeamEvents
    {
        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable((ushort) 0x2e)]
        public TeamRobot()
        {
            GC.KeepAlive(this);
        }

        #region ITeamEvents Members

        public virtual void onMessageReceived(MessageEvent @event)
        {
        }

        #endregion

        #region ITeamRobot Members

        public ITeamEvents getTeamEventListener()
        {
            return this;
        }

        #endregion

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0x7e, 0x8b, 0x6b, 0x9f, 7, 0x85}),
         Throws(new[] {"java.io.IOException"})]
        public virtual void broadcastMessage(Serializable message)
        {
            /*Serializable serializable = message;
            object obj2 = serializable.__<ref>;
            if (base.peer != null)
            {
                Serializable serializable2;
                object obj3 = obj2;
                serializable2.__<ref> = obj3;
                ((ITeamRobotPeer) base.peer).broadcastMessage(serializable2);
            }
            else
            {
                _RobotBase.uninitializedException();
            }*/
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x29, 0x6b, 0x98, 0x65}),
         Signature("()Ljava/util/Vector<Lrobocode/MessageEvent;>;")]
        public virtual Vector getMessageEvents()
        {
            if (base.peer != null)
            {
                return new Vector(((ITeamRobotPeer) base.peer).getMessageEvents());
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x52, 0x6b, 0x93, 0x65})]
        public virtual string[] getTeammates()
        {
            if (base.peer != null)
            {
                return ((ITeamRobotPeer) base.peer).getTeammates();
            }
            uninitializedException();
            return null;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {110, 0x6b, 0x94, 0x65})]
        public virtual bool isTeammate(string name)
        {
            if (base.peer != null)
            {
                return ((ITeamRobotPeer) base.peer).isTeammate(name);
            }
            uninitializedException();
            return false;
        }

        [MethodImpl(MethodImplOptions.NoInlining), LineNumberTable(new byte[] {0x9f, 0x5f, 0x8b, 0x6b, 0x9f, 8, 0x85}),
         Throws(new[] {"java.io.IOException"})]
        public virtual void sendMessage(string name, Serializable message)
        {
            /*Serializable serializable = message;
            object obj2 = serializable.__<ref>;
            if (base.peer != null)
            {
                Serializable serializable2;
                object obj3 = obj2;
                serializable2.__<ref> = obj3;
                ((ITeamRobotPeer) base.peer).sendMessage(name, serializable2);
            }
            else
            {
                _RobotBase.uninitializedException();
            }*/
        }
    }
}