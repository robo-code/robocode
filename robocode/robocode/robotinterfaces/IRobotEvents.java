/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.robotinterfaces;


import robocode.*;


/**
 * @author Pavel Savara (original)
 */
public interface IRobotEvents {

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
	 * @see robocode.BulletHitEvent
	 * @see robocode.Event
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
	 * @see robocode.BulletHitBulletEvent
	 * @see robocode.Event
	 */
	void onBulletHitBullet(BulletHitBulletEvent event);

	/**
	 * This method is called when one of your bullets misses, i.e. hits a wall.
	 * You should override it in your robot if you want to be informed of this
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   public void onBulletHit(BulletMissedEvent event) {
	 *       out.println("Drat, I missed.");
	 *   }
	 * </pre>
	 *
	 * @param event the bullet-missed event set by the game
	 * @see robocode.BulletMissedEvent
	 * @see robocode.Event
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
	 *
	 * @see robocode.DeathEvent
	 * @see robocode.Event
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
	 * @see robocode.HitByBulletEvent
	 * @see robocode.Event
	 */
	public void onHitByBullet(HitByBulletEvent event);

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
	 *
	 *   -- or perhaps, for a more advanced robot --
	 *
	 *   public void onHitRobot(HitRobotEvent event) {
	 *       if (event.getBearing() > -90 && event.getBearing() <= 90) {
	 *           setBack(100);
	 *       } else {
	 *           setAhead(100);
	 *       }
	 *   }
	 * </pre>
	 *
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
	 * your heading, so: The bearing is such that turnRight(e.getBearing()) will
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
	 * This method is called when your robot sees another robot, i.e. when the
	 * robot's radar scan "hits" another robot.
	 * You should override it in your robot if you want to be informed of this
	 * event. (Almost all robots should override this!)
	 * <p>
	 * This event is automatically called if there is a robot in range of your
	 * radar.
	 * <p>
	 * Note that the robot's radar can only see robot within the range defined
	 * by {@link Rules#equals(Object)} (1200 pixels).
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
	 *
	 * Note:
	 *  The game assists Robots in firing, as follows:
	 *  If the gun and radar are aligned (and were aligned last turn),
	 *    and the event is current,
	 *    and you call fire() before taking any other actions,
	 *    fire() will fire directly at the robot.
	 *  In essence, this means that if you can see a robot, and it doesn't move,
	 *  then fire will hit it.
	 *  <br>
	 *  AdvancedRobots will NOT be assisted in this manner, and are expected to
	 *  examine the event to determine if fire() would hit. (i.e. you are
	 *  spinning your gun around, but by the time you get the event, your gun is
	 *  5 degrees past the robot).
	 *
	 * @param event the scanned-robot event set by the game
	 *
	 * @see ScannedRobotEvent
	 * @see Event
	 */
	void onScannedRobot(ScannedRobotEvent event);

	/**
	 * This method is called if your robot wins a battle.
	 * <p>
	 * Your robot could perform a victory dance here! :-)
	 *
	 * @param event the win event set by the game
	 * @see WinEvent
	 * @see Event
	 */
	void onWin(WinEvent event);

	/**
	 * This method is called when a custom condition is met.
	 * <p>
	 * See the sample robots for examples of use.
	 *
	 * @param event the custom event that occured
	 *
	 * @see AdvancedRobot#addCustomEvent
	 * @see CustomEvent
	 * @see Event
	 */
	void onCustomEvent(CustomEvent event);
}
