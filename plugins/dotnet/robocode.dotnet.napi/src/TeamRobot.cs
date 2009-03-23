using System.Collections.Generic;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;

namespace robocode
{
    public class TeamRobot : AdvancedRobot, ITeamRobot, ITeamEvents
    {
        #region ITeamEvents Members

        public virtual void onMessageReceived(MessageEvent evnt)
        {
        }

        #endregion

        #region ITeamRobot Members

        public ITeamEvents getTeamEventListener()
        {
            return this;
        }

        #endregion

        public virtual void broadcastMessage(object message)
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

        public virtual List<MessageEvent> getMessageEvents()
        {
            if (base.peer != null)
            {
                return ((ITeamRobotPeer) base.peer).getMessageEvents();
            }
            uninitializedException();
            return null;
        }

        public virtual List<string> getTeammates()
        {
            if (base.peer != null)
            {
                return ((ITeamRobotPeer) base.peer).getTeammates();
            }
            uninitializedException();
            return null;
        }

        public virtual bool isTeammate(string name)
        {
            if (base.peer != null)
            {
                return ((ITeamRobotPeer) base.peer).isTeammate(name);
            }
            uninitializedException();
            return false;
        }

        public virtual void sendMessage(string name, object message)
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