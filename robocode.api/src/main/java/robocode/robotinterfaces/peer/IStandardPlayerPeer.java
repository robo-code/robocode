package robocode.robotinterfaces.peer;

public interface IStandardPlayerPeer extends IBasicPlayerPeer {
    void stop(boolean overwrite);
    void resume();
    void turnHead(double radians);
}
