package robocode.robotinterfaces;

import robocode.robotinterfaces.peer.IBasicPlayerPeer;

public interface IBasicPlayer {
    Runnable getPlayerRunnable();

    IBasicEvents getBasicEventListener();

    void setPeer(IBasicPlayerPeer peer);

    void setOut(java.io.PrintStream out);
}
