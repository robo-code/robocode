using System.Collections.Generic;
using net.sf.robocode.peer;
using robocode;
using robocode.robotinterfaces;

namespace net.sf.robocode.security
{
    public interface IHiddenEventHelper
    {
        void dispatch(Event e, IBasicRobot ibr, IRobotStatics irs, IGraphics gd);
        byte getSerializationType(Event e);
        bool isCriticalEvent(Event e);
        void setDefaultPriority(Event e);
        void setPriority(Event e, int i);
        void setTime(Event e, long l);

        void updateBullets(Event e, Dictionary<int, Bullet> h);
    }
}