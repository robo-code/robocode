package robocode;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;


public class Robject {

	private int x;
	private int y;
	private int width;
	private int height;
	private String type;	//TODO: change to enum
	private boolean tankStopper;
	private boolean bulletStopper;
	private boolean scanStopper;
	
	private boolean scannable;
	
	public Robject(String type, int x, int y, int width, int height, boolean stopsTanks,
			boolean stopsBullets, boolean stopsScans, boolean scannable)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		tankStopper = stopsTanks;
		bulletStopper = stopsBullets;
		scanStopper = stopsScans;
		this.scannable = scannable;
	}

	public Rectangle2D.Float getBoundaryRect()
	{
		return new Rectangle2D.Float(x, y, width, height);
	}
	
	public void setX(int x) {
		this.x = x;
	}
	public int getX() {
		return x;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getY() {
		return y;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getWidth() {
		return width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getHeight() {
		return height;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}

	public void setScannable(boolean scannable) {
		this.scannable = scannable;
	}

	public boolean isScannable() {
		return scannable;
	}

	public void setScanStopper(boolean scanStopper) {
		this.scanStopper = scanStopper;
	}

	public boolean isScanStopper() {
		return scanStopper;
	}

	public void setBulletStopper(boolean bulletStopper) {
		this.bulletStopper = bulletStopper;
	}

	public boolean isBulletStopper() {
		return bulletStopper;
	}

	public void setTankStopper(boolean tankStopper) {
		this.tankStopper = tankStopper;
	}

	public boolean isTankStopper() {
		return tankStopper;
	}
	
}
