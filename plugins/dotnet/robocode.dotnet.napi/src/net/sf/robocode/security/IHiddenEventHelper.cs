using IKVM.Attributes;
using java.awt;
using java.util;
using net.sf.robocode.peer;
using robocode.robotinterfaces;
using Event=robocode.Event;

namespace net.sf.robocode.security
{
    public interface IHiddenEventHelper
    {
        void dispatch(Event e, IBasicRobot ibr, IRobotStatics irs, Graphics2D gd);
        byte getSerializationType(Event e);
        bool isCriticalEvent(Event e);
        void setDefaultPriority(Event e);
        void setPriority(Event e, int i);
        void setTime(Event e, long l);

        [Signature("(Lrobocode/Event;Ljava/util/Hashtable<Ljava/lang/Integer;Lrobocode/Bullet;>;)V")]
        void updateBullets(Event e, Hashtable h);
    }
}