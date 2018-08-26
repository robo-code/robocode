package robocode.robotinterfaces.peer;

import robocode.Condition;
import robocode.Event;
import robocode.StatusEvent;

import java.io.File;
import java.util.List;

public interface IAdvancedPlayerPeer extends IStandardPlayerPeer {
    void setStop(boolean overwrite);
    void setResume();
    void setMove(double distance);
    void setMaxVelocity(double newMaxVelocity);
    void waitFor(Condition condition);
    void setInterruptible(boolean interruptible);
    void setEventPriority(String eventClass, int priority);
    int getEventPriority(String eventClass);
    void addCustomEvent(Condition condition);
    void removeCustomEvent(Condition condition);
    void clearAllEvents();
    java.util.List<Event> getAllEvents();
    List<StatusEvent> getStatusEvents();
    File getDataDirectory();
    File getDataFile(String filename);
    long getDataQuotaAvailable();
}
