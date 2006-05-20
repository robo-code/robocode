package sampleteam;


import robocode.*;
import java.awt.Color;
import java.io.*;


/**
 * MyFirstLeader - a sample team robot by Mathew Nelson
 * 
 * Looks around for enemies, and orders teammates to fire
 */
public class MyFirstLeader extends TeamRobot {

	/**
	 * run: Leader's default behavior
	 */
	public void run() {
		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:
		setColors(Color.red, Color.red, Color.red);
		try {
			// Send RobotColors object to our entire team
			broadcastMessage(new RobotColors(Color.red, Color.red, Color.red));
		} catch (IOException e) {}
		// Normal behavior
		while (true) {
			setTurnRadarRight(10000);
			ahead(100);
			back(100);
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Don't fire on teammates
		if (isTeammate(e.getName())) {
			return;
		}
		// Calculate enemy bearing
		double enemyBearing = this.getHeading() + e.getBearing();
		// Calculate enemy's position
		double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
		double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

		try {
			// Send enemy position to teammates
			broadcastMessage(new Point(enemyX, enemyY));
		} catch (IOException ex) {
			System.out.println("Unable to send order: " + ex);
		}
	}

	/**
	 * onHitByBullet: Turn perpendicular to bullet path
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}
}
