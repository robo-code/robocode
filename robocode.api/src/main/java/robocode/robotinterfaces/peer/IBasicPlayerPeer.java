package robocode.robotinterfaces.peer;

import java.awt.*;

public interface IBasicPlayerPeer {
    String getName();

    long getTime();

    double getEnergy();

    double getX();

    double getY();

    double getVelocity();

    double getBodyHeading();

    double getRadarHeading();

    double getSoccerFieldWidth();

    double getSoccerFieldHeight();

    double getDistanceRemaining();

    double getRadarTurnRemaining();

    void execute();

    void move(double distance);

    void turnBody(double radians);

    void setBodyColor(Color color);

    void getCall();

    void setCall();

    Graphics2D getGraphics();

    void setDebugProperty(String key, String value);

    void rescan();
}
