package net.sf.robocode.battle.snapshot;

import java.awt.geom.Rectangle2D;

import robocode.Robject;
import robocode.control.snapshot.IRobjectSnapshot;

public class RobjectSnapshot implements IRobjectSnapshot {

	private double x;
	private double y;
	private double width;
	private double height;
	private String type;

	/**
	 * {@inheritDoc}
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getType() {
		return type; 
	}

	/**
	 * {@inheritDoc}
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getX() {
		return x;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getY() {
		return y;
	}
	
	public RobjectSnapshot() {}

	public RobjectSnapshot(Robject robject) {
		this.x = robject.getX();
		this.y = robject.getY();
		this.width = robject.getWidth();
		this.height = robject.getHeight();
		this.type = robject.getType();
	}

	public Rectangle2D getPaintRect() {
		return new Rectangle2D.Double(0, 0, width, height);
	}

}
