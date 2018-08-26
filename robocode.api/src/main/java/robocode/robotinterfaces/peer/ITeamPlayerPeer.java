package robocode.robotinterfaces.peer;

import robocode.MessageEvent;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface ITeamPlayerPeer extends IAdvancedPlayerPeer {
    String[] getTeammates();
    boolean isTeammate(String name);
    void broadcastMessage(Serializable message) throws IOException;
    void sendMessage(String name, Serializable message) throws IOException;
    List<MessageEvent> getMessageEvents();
}
