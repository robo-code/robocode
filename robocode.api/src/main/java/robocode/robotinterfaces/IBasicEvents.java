/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces;


import robocode.*;


/**
 * An event interface for receiving basic robot events with an
 * {@link IBasicRobot}.
 *
 * @see IBasicRobot
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IBasicEvents {

	/**
	 * This method is called every turn in a battle round in order to provide
	 * the robot status as a complete snapshot of the robot's current state at
	 * that specific time.
	 * <p>
	 * The main benefit of this method is that you'll automatically receive all
	 * current data values of the robot like e.g. the x and y coordinate,
	 * heading, gun heat etc., which are grouped into the exact same time/turn.
	 * <p>
	 * This is the only way to map the robots data values to a specific time.
	 * For example, it is not possible to determine the exact time of the
	 * robot's heading by calling first calling {@link Robot#getTime()} and then
	 * {@link Robot#getHeading()} afterwards, as the time <em>might</em> change
	 * after between the {@link Robot#getTime()} and {@link Robot#getHeading()}
	 * call.
	 *
	 * @param event the event containing the robot status at the time it occurred.
	 * @see StatusEvent
	 * @see Event
	 * @since 1.5
	 */
	void onStatus(StatusEvent event);

	/**
	 * This method is called when one of your bullets hits another robot.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onBulletHit(BulletHitEvent event) {
	 *       out.println("I hit " + event.getName() + "!");
	 *   }
	 * </pre>
	 *
	 * @param event the bullet-hit event set by the game
	 * @see BulletHitEvent
	 * @see Event
	 */
	void onBulletHit(BulletHitEvent event);

	/**
	 * This method is called when one of your bullets hits another bullet.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onBulletHitBullet(BulletHitBulletEvent event) {
	 *       out.println("I hit a bullet fired by " + event.getBullet().getName() + "!");
	 *   }
	 * </pre>
	 *
	 * @param event the bullet-hit-bullet event set by the game
	 * @see BulletHitBulletEvent
	 * @see Event
	 */
	void onBulletHitBullet(BulletHitBulletEvent event);

	/**
	 * This method is called when one of your bullets misses, i.e. hits a wall.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onBulletMissed(BulletMissedEvent event) {
	 *       out.println("Drat, I missed.");
	 *   }
	 * </pre>
	 *
	 * @param event the bullet-missed event set by the game
	 * @see BulletMissedEvent
	 * @see Event
	 */
	void onBulletMissed(BulletMissedEvent event);

	/**
	 * This method is called if your robot dies.
	 * <p>
	 * You should override it in your robot if you want to be informed of this
	 * event. Actions will have no effect if called from this section. The
	 * intent is to allow you to perform calculations or print something out
	 * when the robot is killed.
	 *
	 * @param event the death event set by the game
	 * @see DeathEvent
	 * @see WinEvent
	 * @see RoundEndedEvent
	 * @see BattleEndedEvent
	 * @see Event
	 */
	public void onDeath(DeathEvent event);

	/**
	 * This method is called when your robot is hit by a bullet.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   void onHitByBullet(HitByBulletEvent event) {
	 *       out.println(event.getRobotName() + " hit me!");
	 *   }
	 * </pre>
	 *
	 * @param event the hit-by-bullet event set by the game
	 * @see HitByBulletEvent
	 * @see Event
	 */
	void onHitByBullet(HitByBulletEvent event);

	/**
	 * This method is called when your robot collides with another robot.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   void onHitRobot(HitRobotEvent event) {
	 *       if (event.getBearing() > -90 && event.getBearing() <= 90) {
	 *           back(100);
	 *       } else {
	 *           ahead(100);
	 *       }
	 *   }
	 * <p>
	 *   -- or perhaps, for a more advanced robot --
	 * <p>
	 *   public void onHitRobot(HitRobotEvent event) {
	 *       if (event.getBearing() > -90 && event.getBearing() <= 90) {
	 *           setBack(100);
	 *       } else {
	 *           setAhead(100);
	 *       }
	 *   }
	 * </pre>
	 * <p>
	 * The angle is relative to your robot's facing. So 0 is straight ahead of
	 * you.
	 * <p>
	 * This event can be generated if another robot hits you, in which case
	 * {@link HitRobotEvent#isMyFault() event.isMyFault()} will return
	 * {@code false}. In this case, you will not be automatically stopped by the
	 * game -- but if you continue moving toward the robot you will hit it (and
	 * generate another event). If you are moving away, then you won't hit it.
	 *
	 * @param event the hit-robot event set by the game
	 * @see HitRobotEvent
	 * @see Event
	 */
	void onHitRobot(HitRobotEvent event);

	/**
	 * This method is called when your robot collides with a wall.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * The wall at the top of the screen is 0 degrees, right is 90 degrees,
	 * bottom is 180 degrees, left is 270 degrees. But this event is relative to
	 * your heading, so: The bearing is such that {@link Robot#turnRight(double)
	 * turnRight} {@link HitWallEvent#getBearing() (event.getBearing())} will
	 * point you perpendicular to the wall.
	 * <p>
	 * Example:
	 * <pre>
	 *   void onHitWall(HitWallEvent event) {
	 *       out.println("Ouch, I hit a wall bearing " + event.getBearing() + " degrees.");
	 *   }
	 * </pre>
	 *
	 * @param event the hit-wall event set by the game
	 * @see HitWallEvent
	 * @see Event
	 */
	void onHitWall(HitWallEvent event);

	/**
	 * This method is called when your robot sees another robot, i.e. when the
	 * robot's radar scan "hits" another robot.
	 * You should override it in your robot if you want to be informed of this
	 * event. (Almost all robots should override this!)
	 * <p>
	 * This event is automatically called if there is a robot in range of your
	 * radar.
	 * <p>
	 * Note that the robot's radar can only see robot within the range defined
	 * by {@link Rules#RADAR_SCAN_RADIUS} (1200 pixels).
	 * <p>
	 * Also not that the bearing of the scanned robot is relative to your
	 * robot's heading.
	 * <p>
	 * Example:
	 * <pre>
	 *   void onScannedRobot(ScannedRobotEvent event) {
	 *       // Assuming radar and gun are aligned...
	 *       if (event.getDistance() < 100) {
	 *           fire(3);
	 *       } else {
	 *           fire(1);
	 *       }
	 *   }
	 * </pre>
	 * <p>
	 * <b>Note:</b><br>
	 * The game assists Robots in firing, as follows:
	 * <ul>
	 * <li>If the gun and radar are aligned (and were aligned last turn),
	 * <li>and the event is current,
	 * <li>and you call fire() before taking any other actions, {@link
	 * Robot#fire(double) fire()} will fire directly at the robot.
	 * </ul>
	 * <p>
	 * In essence, this means that if you can see a robot, and it doesn't move,
	 * then fire will hit it.
	 * <p>
	 * AdvancedRobots will NOT be assisted in this manner, and are expected to
	 * examine the event to determine if {@link Robot#fire(double) fire()} would
	 * hit. (i.e. you are spinning your gun around, but by the time you get the
	 * event, your gun is 5 degrees past the robot).
	 *
	 * @param event the scanned-robot event set by the game
	 * @see ScannedRobotEvent
	 * @see Event
	 * @see Rules#RADAR_SCAN_RADIUS
	 */
	void onScannedRobot(ScannedRobotEvent event);

	/**
	 * This method is called when another robot dies.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 *
	 * @param event The robot-death event set by the game
	 * @see RobotDeathEvent
	 * @see Event
	 */
	void onRobotDeath(RobotDeathEvent event);

	/**
	 * This method is called if your robot wins a battle.
	 * <p>
	 * Your robot could perform a victory dance here! :-)
	 *
	 * @param event the win event set by the game
	 * @see DeathEvent
	 * @see RoundEndedEvent
	 * @see BattleEndedEvent
	 * @see Event
	 */
	void onWin(WinEvent event);
}
