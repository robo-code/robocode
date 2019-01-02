/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces.peer;


import robocode.*;
import robocode.robotinterfaces.IBasicEvents;

import java.awt.*;


/**
 * The basic robot peer for all robot types.<p>
 *
 * NOTE: This is private interface. You should build any external component (or robot)
 * based on it's current methods because it will change in the future.<p>
 *
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 *
 * @see IStandardRobotPeer
 * @see IAdvancedRobotPeer
 * @see ITeamRobotPeer
 * @see IJuniorRobotPeer
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IBasicRobotPeer {

	/**
	 * Returns the robot's name.
	 *
	 * @return the robot's name.
	 */
	String getName();

	/**
	 * Returns the game time of the current round, where the time is equal to
	 * the current turn in the round.
	 * <p>
	 * A battle consists of multiple rounds.
	 * <p>
	 * Time is reset to 0 at the beginning of every round.
	 *
	 * @return the game time/turn of the current round.
	 */
	long getTime();

	/**
	 * Returns the robot's current energy.
	 *
	 * @return the robot's current energy.
	 */
	double getEnergy();

	/**
	 * Returns the X position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the X position of the robot.
	 * @see #getY()
	 */
	double getX();

	/**
	 * Returns the Y position of the robot. (0,0) is at the bottom left of the
	 * battlefield.
	 *
	 * @return the Y position of the robot.
	 * @see #getX()
	 */
	double getY();

	/**
	 * Returns the velocity of the robot measured in pixels/turn.
	 * <p>
	 * The maximum velocity of a robot is defined by
	 * {@link Rules#MAX_VELOCITY} (8 pixels / turn).
	 *
	 * @return the velocity of the robot measured in pixels/turn.
	 * @see Rules#MAX_VELOCITY
	 */
	double getVelocity();

	/**
	 * Returns the direction that the robot's body is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 2 means West.
	 *
	 * @return the direction that the robot's body is facing, in radians.
	 * @see #getGunHeading()
	 * @see #getRadarHeading()
	 */
	double getBodyHeading();

	/**
	 * Returns the direction that the robot's gun is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 2 means West.
	 *
	 * @return the direction that the robot's gun is facing, in radians.
	 * @see #getBodyHeading()
	 * @see #getRadarHeading()
	 */
	double getGunHeading();

	/**
	 * Returns the direction that the robot's radar is facing, in radians.
	 * The value returned will be between 0 and 2 * PI (is excluded).
	 * <p>
	 * Note that the heading in Robocode is like a compass, where 0 means North,
	 * PI / 2 means East, PI means South, and 3 * PI / 2 means West.
	 *
	 * @return the direction that the robot's radar is facing, in radians.
	 * @see #getBodyHeading()
	 * @see #getGunHeading()
	 */
	double getRadarHeading();

	/**
	 * Returns the current heat of the gun. The gun cannot fire unless this is
	 * 0. (Calls to fire will succeed, but will not actually fire unless
	 * getGunHeat() == 0).
	 * <p>
	 * The amount of gun heat generated when the gun is fired is
	 * 1 + (firePower / 5). Each turn the gun heat drops by the amount returned
	 * by {@link #getGunCoolingRate()}, which is a battle setup.
	 * <p>
	 * Note that all guns are "hot" at the start of each round, where the gun
	 * heat is 3.
	 *
	 * @return the current gun heat
	 * @see #getGunCoolingRate()
	 * @see #setFire(double)
	 */
	double getGunHeat();

	/**
	 * Returns the width of the current battlefield measured in pixels.
	 *
	 * @return the width of the current battlefield measured in pixels.
	 */
	double getBattleFieldWidth();

	/**
	 * Returns the height of the current battlefield measured in pixels.
	 *
	 * @return the height of the current battlefield measured in pixels.
	 */
	double getBattleFieldHeight();

	/**
	 * Returns how many opponents that are left in the current round.
	 *
	 * @return how many opponents that are left in the current round.
	 * @see #getNumSentries()
	 */
	int getOthers();

	/**
	 * Returns how many sentry robots that are left in the current round.
	 *
	 * @return how many sentry robots that are left in the current round.
	 * @see #getOthers()
	 * @since 1.9.1.0
	 */
	int getNumSentries();

	/**
	 * Returns the number of rounds in the current battle.
	 *
	 * @return the number of rounds in the current battle.
	 * @see #getRoundNum()
	 */
	int getNumRounds();

	/**
	 * Returns the number of the current round (0 to {@link #getNumRounds()} - 1)
	 * in the battle.
	 *
	 * @return the number of the current round in the battle (zero indexed).
	 * @see #getNumRounds()
	 */
	int getRoundNum();

	/**
	 * Returns the sentry border size for a {@link robocode.BorderSentry BorderSentry} that defines the how
	 * far a BorderSentry is allowed to move from the border edges measured in units.<br>
	 * Hence, the sentry border size defines the width/range of the border area surrounding the battlefield that
	 * BorderSentrys cannot leave (sentry robots robots must stay in the border area), but it also define the
	 * distance from the border edges where BorderSentrys are allowed/able to make damage to robots entering this
	 * border area.
	 * 
	 * @return the border size in units/pixels.
	 * 
	 * @since 1.9.0.0
	 */
	int getSentryBorderSize();

	/**
	 * Returns the rate at which the gun will cool down, i.e. the amount of heat
	 * the gun heat will drop per turn.
	 * <p>
	 * The gun cooling rate is default 0.1 / turn, but can be changed by the
	 * battle setup. So don't count on the cooling rate being 0.1!
	 *
	 * @return the gun cooling rate
	 * @see #getGunHeat()
	 * @see #setFire(double)
	 */
	double getGunCoolingRate();

	/**
	 * Returns the distance remaining in the robot's current move measured in
	 * pixels.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently moving forwards. Negative values means
	 * that the robot is currently moving backwards. If the returned value is 0,
	 * the robot currently stands still.
	 *
	 * @return the distance remaining in the robot's current move measured in
	 *         pixels.
	 * @see #getBodyTurnRemaining()
	 * @see #getGunTurnRemaining()
	 * @see #getRadarTurnRemaining()
	 */
	double getDistanceRemaining();

	/**
	 * Returns the angle remaining in the robot's turn, in radians.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the robot is currently turning to the right. Negative values
	 * means that the robot is currently turning to the left.
	 *
	 * @return the angle remaining in the robot's turn, in radians
	 * @see #getDistanceRemaining()
	 * @see #getGunTurnRemaining()
	 * @see #getRadarTurnRemaining()
	 */
	double getBodyTurnRemaining();

	/**
	 * Returns the angle remaining in the gun's turn, in radians.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the gun is currently turning to the right. Negative values
	 * means that the gun is currently turning to the left.
	 *
	 * @return the angle remaining in the gun's turn, in radians
	 * @see #getDistanceRemaining()
	 * @see #getBodyTurnRemaining()
	 * @see #getRadarTurnRemaining()
	 */
	double getGunTurnRemaining();

	/**
	 * Returns the angle remaining in the radar's turn, in radians.
	 * <p>
	 * This call returns both positive and negative values. Positive values
	 * means that the radar is currently turning to the right. Negative values
	 * means that the radar is currently turning to the left.
	 *
	 * @return the angle remaining in the radar's turn, in radians
	 * @see #getDistanceRemaining()
	 * @see #getBodyTurnRemaining()
	 * @see #getGunTurnRemaining()
	 */
	double getRadarTurnRemaining();

	/**
	 * Executes any pending actions, or continues executing actions that are
	 * in process. This call returns after the actions have been started.
	 * <p>
	 * Note that advanced robots <em>must</em> call this function in order to
	 * execute pending set* calls like e.g. {@link
	 * IAdvancedRobotPeer#setMove(double) setMove(double)},
	 * {@link IAdvancedRobotPeer#setFire(double) setFire(double)}, {@link
	 * IAdvancedRobotPeer#setTurnBody(double) setTurnBody(double)} etc.
	 * Otherwise, these calls will never get executed.
	 * <p>
	 * In this example the robot will move while turning:
	 * <pre>
	 *   setTurnBody(90);
	 *   setMove(100);
	 *   execute();
	 * <p>
	 *   while (getDistanceRemaining() > 0 && getTurnRemaining() > 0) {
	 *       execute();
	 *   }
	 * </pre>
	 */
	void execute();

	/**
	 * Immediately moves your robot forward or backward by distance measured in
	 * pixels.
	 * <p>
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the remaining distance to move is 0.
	 * <p>
	 * If the robot collides with a wall, the move is complete, meaning that the
	 * robot will not move any further. If the robot collides with another
	 * robot, the move is complete if you are heading toward the other robot.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot is set to move forward, and negative
	 * values means that the robot is set to move backward.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Move the robot 100 pixels forward
	 *   ahead(100);
	 * <p>
	 *   // Afterwards, move the robot 50 pixels backward
	 *   ahead(-50);
	 * </pre>
	 *
	 * @param distance the distance to move measured in pixels.
	 *                 If {@code distance} > 0 the robot is set to move forward.
	 *                 If {@code distance} < 0 the robot is set to move backward.
	 *                 If {@code distance} = 0 the robot will not move anywhere, but just
	 *                 finish its turn.
	 * @see robocode.robotinterfaces.IBasicEvents#onHitWall(robocode.HitWallEvent)
	 * @see robocode.robotinterfaces.IBasicEvents#onHitRobot(robocode.HitRobotEvent)
	 */
	void move(double distance);

	/**
	 * Immediately turns the robot's body to the right or left by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the body's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot's body is set to turn right, and
	 * negative values means that the robot's body is set to turn left.
	 * If 0 is given as input, the robot's body will stop turning.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's body 180 degrees to the right
	 *   turnBody(Math.PI);
	 * <p>
	 *   // Afterwards, turn the robot's body 90 degrees to the left
	 *   turnBody(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body.
	 *                If {@code radians} > 0 the robot's body is set to turn right.
	 *                If {@code radians} < 0 the robot's body is set to turn left.
	 *                If {@code radians} = 0 the robot's body is set to stop turning.
	 * @see #turnGun(double)
	 * @see IStandardRobotPeer#turnRadar(double) turnRadar(double)
	 * @see #move(double)
	 */
	void turnBody(double radians);

	/**
	 * Immediately turns the robot's gun to the right or left by radians.
	 * This call executes immediately, and does not return until it is complete,
	 * i.e. when the angle remaining in the gun's turn is 0.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot's gun is set to turn right, and
	 * negative values means that the robot's gun is set to turn left.
	 * If 0 is given as input, the robot's gun will stop turning.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Turn the robot's gun 180 degrees to the right
	 *   turnGun(Math.PI);
	 * <p>
	 *   // Afterwards, turn the robot's gun 90 degrees to the left
	 *   turnGun(-Math.PI / 2);
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun.
	 *                If {@code radians} > 0 the robot's gun is set to turn right.
	 *                If {@code radians} < 0 the robot's gun is set to turn left.
	 *                If {@code radians} = 0 the robot's gun is set to stop turning.
	 * @see #turnBody(double)
	 * @see IStandardRobotPeer#turnRadar(double) turnRadar(double)
	 * @see #move(double)
	 */
	void turnGun(double radians);

	/**
	 * Immediately fires a bullet. The bullet will travel in the direction the
	 * gun is pointing.
	 * <p>
	 * The specified bullet power is an amount of energy that will be taken from
	 * the robot's energy. Hence, the more power you want to spend on the
	 * bullet, the more energy is taken from your robot.
	 * <p>
	 * The bullet will do (4 * power) damage if it hits another robot. If power
	 * is greater than 1, it will do an additional 2 * (power - 1) damage.
	 * You will get (3 * power) back if you hit the other robot. You can call
	 * {@link Rules#getBulletDamage(double)} for getting the damage that a
	 * bullet with a specific bullet power will do.
	 * <p>
	 * The specified bullet power should be between
	 * {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
	 * <p>
	 * Note that the gun cannot fire if the gun is overheated, meaning that
	 * {@link #getGunHeat()} returns a value > 0.
	 * <p>
	 * A event is generated when the bullet hits a robot
	 * ({@link BulletHitEvent}), wall ({@link BulletMissedEvent}), or another
	 * bullet ({@link BulletHitBulletEvent}).
	 * <p>
	 * Example:
	 * <pre>
	 *   // Fire a bullet with maximum power if the gun is ready
	 *   if (getGunHeat() == 0) {
	 *       Bullet bullet = fire(Rules.MAX_BULLET_POWER);
	 * <p>
	 *       // Get the velocity of the bullet
	 *       if (bullet != null) {
	 *           double bulletVelocity = bullet.getVelocity();
	 *       }
	 *   }
	 * </pre>
	 *
	 * @param power the amount of energy given to the bullet, and subtracted
	 *              from the robot's energy.
	 * @return a {@link Bullet} that contains information about the bullet if it
	 *         was actually fired, which can be used for tracking the bullet after it
	 *         has been fired. If the bullet was not fired, {@code null} is returned.
	 * @see #setFire(double)
	 * @see Bullet
	 * @see #getGunHeat()
	 * @see #getGunCoolingRate()
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletHit(BulletHitEvent)
	 *      onBulletHit(BulletHitEvent)
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletHitBullet(BulletHitBulletEvent)
	 *      onBulletHitBullet(BulletHitBulletEvent)
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletMissed(BulletMissedEvent)
	 *      onBulletMissed(BulletMissedEvent)
	 */
	Bullet fire(double power);

	/**
	 * Sets the gun to fire a bullet when the next execution takes place.
	 * The bullet will travel in the direction the gun is pointing.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * execute() or take an action that executes.
	 * <p>
	 * The specified bullet power is an amount of energy that will be taken from
	 * the robot's energy. Hence, the more power you want to spend on the
	 * bullet, the more energy is taken from your robot.
	 * <p>
	 * The bullet will do (4 * power) damage if it hits another robot. If power
	 * is greater than 1, it will do an additional 2 * (power - 1) damage.
	 * You will get (3 * power) back if you hit the other robot. You can call
	 * {@link Rules#getBulletDamage(double)} for getting the damage that a
	 * bullet with a specific bullet power will do.
	 * <p>
	 * The specified bullet power should be between
	 * {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
	 * <p>
	 * Note that the gun cannot fire if the gun is overheated, meaning that
	 * {@link #getGunHeat()} returns a value > 0.
	 * <p>
	 * A event is generated when the bullet hits a robot
	 * ({@link BulletHitEvent}), wall ({@link BulletMissedEvent}), or another
	 * bullet ({@link BulletHitBulletEvent}).
	 * <p>
	 * Example:
	 * <pre>
	 *   Bullet bullet = null;
	 * <p>
	 *   // Fire a bullet with maximum power if the gun is ready
	 *   if (getGunHeat() == 0) {
	 *       bullet = setFireBullet(Rules.MAX_BULLET_POWER);
	 *   }
	 *   ...
	 *   execute();
	 *   ...
	 *   // Get the velocity of the bullet
	 *   if (bullet != null) {
	 *       double bulletVelocity = bullet.getVelocity();
	 *   }
	 * </pre>
	 *
	 * @param power the amount of energy given to the bullet, and subtracted
	 *              from the robot's energy.
	 * @return a {@link Bullet} that contains information about the bullet if it
	 *         was actually fired, which can be used for tracking the bullet after it
	 *         has been fired. If the bullet was not fired, {@code null} is returned.
	 * @see #fire(double)
	 * @see Bullet
	 * @see #getGunHeat()
	 * @see #getGunCoolingRate()
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletHit(BulletHitEvent)
	 *      onBulletHit(BulletHitEvent)
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletHitBullet(BulletHitBulletEvent)
	 *      onBulletHitBullet(BulletHitBulletEvent)
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletMissed(BulletMissedEvent)
	 *      onBulletMissed(BulletMissedEvent)
	 */
	Bullet setFire(double power);

	/**
	 * Sets the color of the robot's body.
	 * <p>
	 * A {@code null} indicates the default (blue) color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 * <p>
	 *   public void run() {
	 *       setBodyColor(Color.BLACK);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new body color
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
	 * @since 1.1.2
	 */
	void setBodyColor(Color color);

	/**
	 * Sets the color of the robot's gun.
	 * <p>
	 * A {@code null} indicates the default (blue) color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 * <p>
	 *   public void run() {
	 *       setGunColor(Color.RED);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new gun color
	 * @see #setBodyColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
	 * @since 1.1.2
	 */
	void setGunColor(Color color);

	/**
	 * Sets the color of the robot's radar.
	 * <p>
	 * A {@code null} indicates the default (blue) color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 * <p>
	 *   public void run() {
	 *       setRadarColor(Color.YELLOW);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new radar color
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setBulletColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
	 * @since 1.1.2
	 */
	void setRadarColor(Color color);

	/**
	 * Sets the color of the robot's bullets.
	 * <p>
	 * A {@code null} indicates the default white color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 * <p>
	 *   public void run() {
	 *       setBulletColor(Color.GREEN);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new bullet color
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setScanColor(Color)
	 * @see Color
	 * @since 1.1.2
	 */
	void setBulletColor(Color color);

	/**
	 * Sets the color of the robot's scan arc.
	 * <p>
	 * A {@code null} indicates the default (blue) color.
	 * <p>
	 * <pre>
	 * Example:
	 *   // Don't forget to import java.awt.Color at the top...
	 *   import java.awt.Color;
	 *   ...
	 * <p>
	 *   public void run() {
	 *       setScanColor(Color.WHITE);
	 *       ...
	 *   }
	 * </pre>
	 *
	 * @param color the new scan arc color
	 * @see #setBodyColor(Color)
	 * @see #setGunColor(Color)
	 * @see #setRadarColor(Color)
	 * @see #setBulletColor(Color)
	 * @see Color
	 * @since 1.1.2
	 */
	void setScanColor(Color color);

	/**
	 * This call <em>must</em> be made from a robot call to inform the game
	 * that the robot made a {@code get*} call like e.g. {@link #getX()} or
	 * {@link #getVelocity()}.
	 * <p>
	 * This method is used by the game to determine if the robot is inactive or
	 * not. Note: You should only make this call once in a {@code get*} method!
	 *
	 * @see #setCall()
	 */
	void getCall();

	/**
	 * This call <em>must</em> be made from a robot call to inform the game
	 * that the robot made a {@code set*} call like e.g. {@link
	 * #setFire(double)} or {@link #setBodyColor(Color)}.
	 * <p>
	 * This method is used by the game to determine if the robot is inactive or
	 * not. Note: You should only make this call once in a {@code set*} method!
	 *
	 * @see #getCall()
	 */
	void setCall();

	/**
	 * Returns a graphics context used for painting graphical items for the robot.
	 * <p>
	 * This method is very useful for debugging your robot.
	 * <p>
	 * Note that the robot will only be painted if the "Paint" is enabled on the
	 * robot's console window; otherwise the robot will never get painted (the
	 * reason being that all robots might have graphical items that must be
	 * painted, and then you might not be able to tell what graphical items that
	 * have been painted for your robot).
	 * <p>
	 * Also note that the coordinate system for the graphical context where you
	 * paint items fits for the Robocode coordinate system where (0, 0) is at
	 * the bottom left corner of the battlefield, where X is towards right and Y
	 * is upwards.
	 *
	 * @return a graphics context used for painting graphical items for the robot.
	 * @see robocode.robotinterfaces.IPaintEvents#onPaint(Graphics2D)
	 * @since 1.6.1
	 */
	Graphics2D getGraphics();

	/**
	 * Sets the debug property with the specified key to the specified value.
	 * <p>
	 * This method is very useful when debugging or reviewing your robot as you
	 * will be able to see this property displayed in the robot console for your
	 * robots under the Debug Properties tab page.
	 *
	 * @param key the name/key of the debug property.
	 * @param value the new value of the debug property, where {@code null} or
	 *              the empty string is used for removing this debug property. 
	 * @since 1.6.2
	 */
	void setDebugProperty(String key, String value);

	/**
	 * Rescan for other robots. This method is called automatically by the game,
	 * as long as the robot is moving, turning its body, turning its gun, or
	 * turning its radar.
	 * <p>
	 * Rescan will cause {@link IBasicEvents#onScannedRobot(ScannedRobotEvent)
	 * onScannedRobot(ScannedRobotEvent)} to be called if you see a robot.
	 * <p>
	 * There are 2 reasons to call {@code rescan()} manually:
	 * <ol>
	 * <li>You want to scan after you stop moving.
	 * <li>You want to interrupt the {@code onScannedRobot} event. This is more
	 * likely. If you are in {@code onScannedRobot} and call {@code scan()},
	 * and you still see a robot, then the system will interrupt your
	 * {@code onScannedRobot} event immediately and start it from the top.
	 * </ol>
	 * <p>
	 * This call executes immediately.
	 *
	 * @see IBasicEvents#onScannedRobot(ScannedRobotEvent)
	 *      onScannedRobot(ScannedRobotEvent)
	 * @see ScannedRobotEvent
	 *
	 * @since 1.7.2
	 */
	void rescan();
}
