package sampleteam;
import java.awt.Color;

/**
 * RobotColors - A serializable class to send Colors to teammates
 */
public class RobotColors implements java.io.Serializable
{
	private Color bodyColor;
	private Color gunColor;
	private Color radarColor;
	
	public RobotColors(Color bodyColor, Color gunColor, Color radarColor)
	{
		this.bodyColor = bodyColor;
		this.gunColor = gunColor;
		this.radarColor = radarColor;
	}
	
	public Color getBodyColor()
	{
		return bodyColor;
	}
	public Color getGunColor()
	{
		return gunColor;
	}
	public Color getRadarColor()
	{
		return radarColor;
	}
}
