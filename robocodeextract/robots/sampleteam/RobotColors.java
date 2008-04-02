package sampleteam;


import java.awt.*;


/**
 * RobotColors - A serializable class to send Colors to teammates
 */
public class RobotColors implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    public Color bodyColor;
    public Color gunColor;
    public Color radarColor;
    public Color scanColor;
    public Color bulletColor;
}
