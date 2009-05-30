package robocode.control.snapshot;

import java.awt.geom.Rectangle2D;

import robocode.Robject;

public interface IRobjectSnapshot {
	public double getHeight();
	
	public String getType();
	
	public double getWidth();

	public double getX();

	public double getY();

	public Rectangle2D getPaintRect();
}
