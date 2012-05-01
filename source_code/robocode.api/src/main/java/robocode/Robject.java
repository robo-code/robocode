package robocode;


import java.awt.geom.Rectangle2D;


public class Robject {

	private double x;
	private double y;
	private double width;
	private double height;
	private String type;
	private boolean robotStopper;
	private boolean bulletStopper;
	private boolean scanStopper;
	
	private boolean scannable;
	private boolean robotConscious;
	
	private boolean dynamic;
	protected int teamNumber;
	
	public Robject(String type, double x, double y, double width, double height, boolean stopsRobots,
			boolean stopsBullets, boolean stopsScans, boolean scannable, boolean robotConscious,
			boolean dynamic) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.type = type;
		robotStopper = stopsRobots;
		bulletStopper = stopsBullets;
		scanStopper = stopsScans;
		this.robotConscious = robotConscious;
		this.scannable = scannable;
		this.dynamic = dynamic;
	}

	public Rectangle2D.Double getBoundaryRect() {
		return new Rectangle2D.Double(x, y, width, height);
	}
	
	// public void setX(double x) {
	// this.x = x;
	// }
	public double getX() {
		return x;
	}

	// public void setY(double y) {
	// this.y = y;
	// }
	public double getY() {
		return y;
	}

	// public void setWidth(double d) {
	// this.width = d;
	// }
	public double getWidth() {
		return width;
	}

	// public void setHeight(double height) {
	// this.height = height;
	// }
	public double getHeight() {
		return height;
	}

	// public void setType(String type) {
	// this.type = type;
	// }
	public String getType() {
		return type;
	}

	// public void setScannable(boolean scannable) {
	// this.scannable = scannable;
	// }

	public boolean isScannable() {
		return scannable;
	}

	// public void setScanStopper(boolean scanStopper) {
	// this.scanStopper = scanStopper;
	// }

	public boolean isScanStopper() {
		return scanStopper;
	}

	// public void setBulletStopper(boolean bulletStopper) {
	// this.bulletStopper = bulletStopper;
	// }

	public boolean isBulletStopper() {
		return bulletStopper;
	}

	// public void setRobotStopper(boolean robotStopper) {
	// this.robotStopper = robotStopper;
	// }

	public boolean isRobotStopper() {
		return robotStopper;
	}

	// public void setRobotConscious(boolean robotConscious) {
	// this.robotConscious = robotConscious;
	// }

	public boolean isRobotConscious() {
		return robotConscious;
	}

	// public void setDynamic(boolean dynamic) {
	// this.dynamic = dynamic;
	// }

	public boolean isDynamic() {
		return dynamic;
	}
	
	// public void setTeam(int teamNumber) {
	// this.teamNumber = teamNumber;
	// }
	
	public int getTeam() {
		// by default, return 0 to indicate no team ownership
		return 0;
	}
}
