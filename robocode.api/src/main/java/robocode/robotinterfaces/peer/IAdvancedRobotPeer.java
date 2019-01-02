/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode.robotinterfaces.peer;


import robocode.*;

import java.io.File;
import java.util.List;


/**
 * The advanced robot peer for advanced robot types like
 * {@link robocode.AdvancedRobot} and {@link robocode.TeamRobot}.
 * <p>
 * A robot peer is the object that deals with game mechanics and rules, and
 * makes sure your robot abides by them.
 *
 * @see IBasicRobotPeer
 * @see IStandardRobotPeer
 * @see ITeamRobotPeer
 * @see IJuniorRobotPeer
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 *
 * @since 1.6
 */
public interface IAdvancedRobotPeer extends IStandardRobotPeer {

	/**
	 * Checks if the gun is set to adjust for the robot turning, i.e. to turn
	 * independent from the robot's body turn.
	 * <p>
	 * This call returns {@code true} if the gun is set to turn independent of
	 * the turn of the robot's body. Otherwise, {@code false} is returned,
	 * meaning that the gun is set to turn with the robot's body turn.
	 *
	 * @return {@code true} if the gun is set to turn independent of the robot
	 *         turning; {@code false} if the gun is set to turn with the robot
	 *         turning
	 * @see #setAdjustGunForBodyTurn(boolean) setAdjustGunForBodyTurn(boolean)
	 * @see #isAdjustRadarForBodyTurn()
	 * @see #isAdjustRadarForGunTurn()
	 */
	boolean isAdjustGunForBodyTurn();

	/**
	 * Checks if the radar is set to adjust for the robot turning, i.e. to turn
	 * independent from the robot's body turn.
	 * <p>
	 * This call returns {@code true} if the radar is set to turn independent of
	 * the turn of the robot. Otherwise, {@code false} is returned, meaning that
	 * the radar is set to turn with the robot's turn.
	 *
	 * @return {@code true} if the radar is set to turn independent of the robot
	 *         turning; {@code false} if the radar is set to turn with the robot
	 *         turning
	 * @see #setAdjustRadarForBodyTurn(boolean) setAdjustRadarForBodyTurn(boolean)
	 * @see #isAdjustGunForBodyTurn()
	 * @see #isAdjustRadarForGunTurn()
	 */
	boolean isAdjustRadarForGunTurn();

	/**
	 * Checks if the radar is set to adjust for the gun turning, i.e. to turn
	 * independent from the gun's turn.
	 * <p>
	 * This call returns {@code true} if the radar is set to turn independent of
	 * the turn of the gun. Otherwise, {@code false} is returned, meaning that
	 * the radar is set to turn with the gun's turn.
	 *
	 * @return {@code true} if the radar is set to turn independent of the gun
	 *         turning; {@code false} if the radar is set to turn with the gun
	 *         turning
	 * @see #setAdjustRadarForGunTurn(boolean) setAdjustRadarForGunTurn(boolean)
	 * @see #isAdjustGunForBodyTurn()
	 * @see #isAdjustRadarForBodyTurn()
	 */
	boolean isAdjustRadarForBodyTurn();

	/**
	 * This call is identical to {@link IStandardRobotPeer#stop(boolean)
	 * stop(boolean)}, but returns immediately, and will not execute until you
	 * call {@link IBasicRobotPeer#execute() execute()} or take an action that executes.
	 * <p>
	 * If there is already movement saved from a previous stop, you can
	 * overwrite it by calling {@code setStop(true)}.
	 *
	 * @param overwrite {@code true} if the movement saved from a previous stop
	 *                  should be overwritten; {@code false} otherwise.
	 * @see IStandardRobotPeer#stop(boolean) stop(boolean)
	 * @see IStandardRobotPeer#resume() resume()
	 * @see #setResume()
	 * @see IBasicRobotPeer#execute() execute()
	 */
	void setStop(boolean overwrite);

	/**
	 * Sets the robot to resume the movement stopped by
	 * {@link IStandardRobotPeer#stop(boolean) stop(boolean)} or
	 * {@link #setStop(boolean)}, if any.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * {@link IBasicRobotPeer#execute() execute()} or take an action that executes.
	 *
	 * @see IStandardRobotPeer#resume() resume()
	 * @see IStandardRobotPeer#stop(boolean) stop(boolean)
	 * @see #setStop(boolean)
	 * @see IBasicRobotPeer#execute() execute()
	 */
	void setResume();

	/**
	 * Sets the robot to move forward or backward by distance measured in pixels
	 * when the next execution takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * {@link IBasicRobotPeer#execute() execute()} or take an action that executes.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot is set to move forward, and negative
	 * values means that the robot is set to move backward. If 0 is given as
	 * input, the robot will stop its movement, but will have to decelerate
	 * till it stands still, and will thus not be able to stop its movement
	 * immediately, but eventually.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot to move 50 pixels forward
	 *   setMove(50);
	 *
	 *   // Set the robot to move 100 pixels backward
	 *   // (overrides the previous order)
	 *   setMove(-100);
	 *
	 *   ...
	 *   // Executes the last setMove()
	 *   execute();
	 * </pre>
	 *
	 * @param distance the distance to move measured in pixels.
	 *                 If {@code distance} > 0 the robot is set to move forward.
	 *                 If {@code distance} < 0 the robot is set to move backward.
	 *                 If {@code distance} = 0 the robot is set to stop its movement.
	 * @see IBasicRobotPeer#move(double) move(double)
	 * @see #setMaxVelocity(double)
	 * @see #setTurnBody(double)
	 * @see #setTurnGun(double)
	 * @see #setTurnRadar(double)
	 */
	void setMove(double distance);

	/**
	 * Sets the robot's body to turn right or left by radians when the next
	 * execution takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * {@link IBasicRobotPeer#execute() execute()} or take an action that
	 * executes.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot's body is set to turn right, and
	 * negative values means that the robot's body is set to turn left.
	 * If 0 is given as input, the robot's body will stop turning.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot's body to turn 180 degrees to the right
	 *   setTurnBody(Math.PI);
	 *
	 *   // Set the robot's body to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnBody(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnBody()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's body.
	 *                If {@code radians} > 0 the robot's body is set to turn right.
	 *                If {@code radians} < 0 the robot's body is set to turn left.
	 *                If {@code radians} = 0 the robot's body is set to stop turning.
	 * @see IBasicRobotPeer#turnBody(double) turnBody(double)
	 * @see #setTurnGun(double)
	 * @see #setTurnRadar(double)
	 * @see #setMaxTurnRate(double)
	 * @see #setMove(double)
	 */
	void setTurnBody(double radians);

	/**
	 * Sets the robot's gun to turn right or left by radians when the next
	 * execution takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * {@link IBasicRobotPeer#execute() execute()} or take an action that
	 * executes.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot's gun is set to turn right, and
	 * negative values means that the robot's gun is set to turn left.
	 * If 0 is given as input, the robot's gun will stop turning.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot's gun to turn 180 degrees to the right
	 *   setTurnGun(Math.PI);
	 *
	 *   // Set the robot's gun to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnGun(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnFun()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's gun.
	 *                If {@code radians} > 0 the robot's gun is set to turn right.
	 *                If {@code radians} < 0 the robot's gun is set to turn left.
	 *                If {@code radians} = 0 the robot's gun is set to stop turning.
	 * @see IBasicRobotPeer#turnGun(double) turnGun(double)
	 * @see #setTurnBody(double)
	 * @see #setTurnRadar(double)
	 * @see #setMove(double)
	 */
	void setTurnGun(double radians);

	/**
	 * Sets the robot's radar to turn right or left by radians when the next
	 * execution takes place.
	 * <p>
	 * This call returns immediately, and will not execute until you call
	 * {@link IBasicRobotPeer#execute() execute()} or take an action that
	 * executes.
	 * <p>
	 * Note that both positive and negative values can be given as input, where
	 * positive values means that the robot's radar is set to turn right, and
	 * negative values means that the robot's radar is set to turn left.
	 * If 0 is given as input, the robot's radar will stop turning.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Set the robot's radar to turn 180 degrees to the right
	 *   setTurnRadar(Math.PI);
	 *
	 *   // Set the robot's radar to turn 90 degrees to the left instead of right
	 *   // (overrides the previous order)
	 *   setTurnRadar(-Math.PI / 2);
	 *
	 *   ...
	 *   // Executes the last setTurnRadar()
	 *   execute();
	 * </pre>
	 *
	 * @param radians the amount of radians to turn the robot's radar.
	 *                If {@code radians} > 0 the robot's radar is set to turn right.
	 *                If {@code radians} < 0 the robot's radar is set to turn left.
	 *                If {@code radians} = 0 the robot's radar is set to stop turning.
	 * @see IStandardRobotPeer#turnRadar(double) turnRadar(double)
	 * @see #setTurnBody(double)
	 * @see #setTurnGun(double)
	 * @see #setMove(double)
	 */
	void setTurnRadar(double radians);

	/**
	 * Sets the maximum turn rate of the robot measured in degrees if the robot
	 * should turn slower than {@link Rules#MAX_TURN_RATE} (10 degress/turn).
	 *
	 * @param newMaxTurnRate the new maximum turn rate of the robot measured in
	 *                       degrees. Valid values are 0 - {@link Rules#MAX_TURN_RATE}
	 * @see IBasicRobotPeer#turnBody(double) turnBody(double)
	 * @see #setTurnBody(double)
	 * @see #setMaxVelocity(double)
	 */
	void setMaxTurnRate(double newMaxTurnRate);

	/**
	 * Sets the maximum velocity of the robot measured in pixels/turn if the
	 * robot should move slower than {@link Rules#MAX_VELOCITY} (8 pixels/turn).
	 *
	 * @param newMaxVelocity the new maximum turn rate of the robot measured in
	 *                       pixels/turn. Valid values are 0 - {@link Rules#MAX_VELOCITY}
	 * @see IBasicRobotPeer#move(double) move(double)
	 * @see #setMove(double)
	 * @see #setMaxTurnRate(double)
	 */
	void setMaxVelocity(double newMaxVelocity);

	/**
	 * Does not return until a condition is met, i.e. when a
	 * {@link Condition#test()} returns {@code true}.
	 * <p>
	 * This call executes immediately.
	 * <p>
	 * See the {@code sample.Crazy} robot for how this method can be used.
	 *
	 * @param condition the condition that must be met before this call returns
	 * @see Condition
	 * @see Condition#test()
	 */
	void waitFor(Condition condition);

	/**
	 * Call this during an event handler to allow new events of the same
	 * priority to restart the event handler.
	 * <p>
	 * <p>Example:
	 * <pre>
	 *   public void onScannedRobot(ScannedRobotEvent e) {
	 *       fire(1);
	 *       <b>setInterruptible(true);</b>
	 *       move(100);  // If you see a robot while moving ahead,
	 *                   // this handler will start from the top
	 *                   // Without setInterruptible(true), we wouldn't
	 *                   // receive scan events at all!
	 *       // We'll only get here if we don't see a robot during the move.
	 *       getOut().println("Ok, I can't see anyone");
	 *   }
	 * </pre>
	 *
	 * @param interruptible {@code true} if the event handler should be
	 *                      interrupted if new events of the same priority occurs; {@code false}
	 *                      otherwise
	 * @see #setEventPriority(String, int)
	 * @see robocode.robotinterfaces.IBasicEvents#onScannedRobot(ScannedRobotEvent)
	 *      onScannedRobot(ScannedRobotEvent)
	 */
	void setInterruptible(boolean interruptible);

	/**
	 * Sets the priority of a class of events.
	 * <p>
	 * Events are sent to the onXXX handlers in order of priority.
	 * Higher priority events can interrupt lower priority events.
	 * For events with the same priority, newer events are always sent first.
	 * Valid priorities are 0 - 99, where 100 is reserved and 80 is the default
	 * priority.
	 * <p>
	 * Example:
	 * <pre>
	 *   setEventPriority("RobotDeathEvent", 15);
	 * </pre>
	 * <p>
	 * The default priorities are, from highest to lowest:
	 * <pre>
	 *   {@link RoundEndedEvent}:      100 (reserved)
	 *   {@link BattleEndedEvent}:     100 (reserved)
	 *   {@link WinEvent}:             100 (reserved)
	 *   {@link SkippedTurnEvent}:     100 (reserved)
	 *   {@link StatusEvent}:           99
	 *   Key and mouse events:  98
	 *   {@link CustomEvent}:           80 (default value)
	 *   {@link MessageEvent}:          75
	 *   {@link RobotDeathEvent}:       70
	 *   {@link BulletMissedEvent}:     60
	 *   {@link BulletHitBulletEvent}:  55
	 *   {@link BulletHitEvent}:        50
	 *   {@link HitByBulletEvent}:      40
	 *   {@link HitWallEvent}:          30
	 *   {@link HitRobotEvent}:         20
	 *   {@link ScannedRobotEvent}:     10
	 *   {@link PaintEvent}:             5
	 *   {@link DeathEvent}:            -1 (reserved)
	 * </pre>
	 * <p>
	 * Note that you cannot change the priority for events with the special
	 * priority value -1 or 100 (reserved) as these event are system events.
	 * Also note that you cannot change the priority of CustomEvent.
	 * Instead you must change the priority of the condition(s) for your custom
	 * event(s).
	 *
	 * @param eventClass the name of the event class (string) to set the
	 *                   priority for
	 * @param priority   the new priority for that event class
	 * @see #getEventPriority(String)
	 * @see #setInterruptible(boolean)
	 * @since 1.5, the priority of DeathEvent was changed from 100 to -1 in
	 *        order to let robots process pending events on its event queue before
	 *        it dies. When the robot dies, it will not be able to process events.
	 */
	void setEventPriority(String eventClass, int priority);

	/**
	 * Returns the current priority of a class of events.
	 * An event priority is a value from 0 - 99. The higher value, the higher
	 * priority.
	 * <p>
	 * Example:
	 * <pre>
	 *   int myHitRobotPriority = getEventPriority("HitRobotEvent");
	 * </pre>
	 * <p>
	 * The default priorities are, from highest to lowest:
	 * <pre>
	 *   {@link RoundEndedEvent}:      100 (reserved)
	 *   {@link BattleEndedEvent}:     100 (reserved)
	 *   {@link WinEvent}:             100 (reserved)
	 *   {@link SkippedTurnEvent}:     100 (reserved)
	 *   {@link StatusEvent}:           99
	 *   Key and mouse events:  98
	 *   {@link CustomEvent}:           80 (default value)
	 *   {@link MessageEvent}:          75
	 *   {@link RobotDeathEvent}:       70
	 *   {@link BulletMissedEvent}:     60
	 *   {@link BulletHitBulletEvent}:  55
	 *   {@link BulletHitEvent}:        50
	 *   {@link HitByBulletEvent}:      40
	 *   {@link HitWallEvent}:          30
	 *   {@link HitRobotEvent}:         20
	 *   {@link ScannedRobotEvent}:     10
	 *   {@link PaintEvent}:             5
	 *   {@link DeathEvent}:            -1 (reserved)
	 * </pre>
	 *
	 * @param eventClass the name of the event class (string)
	 * @return the current priority of a class of events
	 * @see #setEventPriority(String, int)
	 */
	int getEventPriority(String eventClass);

	/**
	 * Registers a custom event to be called when a condition is met.
	 * When you are finished with your condition or just want to remove it you
	 * must call {@link #removeCustomEvent(Condition)}.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Create the condition for our custom event
	 *   Condition triggerHitCondition = new Condition("triggerhit") {
	 *       public boolean test() {
	 *           return (getEnergy() <= trigger);
	 *       };
	 *   }
	 *
	 *   // Add our custom event based on our condition
	 *   <b>addCustomEvent(triggerHitCondition);</b>
	 * </pre>
	 *
	 * @param condition the condition that must be met.
	 * @throws NullPointerException if the condition parameter has been set to
	 *                              {@code null}.
	 * @see Condition
	 * @see #removeCustomEvent(Condition)
	 */
	void addCustomEvent(Condition condition);

	/**
	 * Removes a custom event that was previously added by calling
	 * {@link #addCustomEvent(Condition)}.
	 * <p>
	 * Example:
	 * <pre>
	 *   // Create the condition for our custom event
	 *   Condition triggerHitCondition = new Condition("triggerhit") {
	 *       public boolean test() {
	 *           return (getEnergy() <= trigger);
	 *       };
	 *   }
	 *
	 *   // Add our custom event based on our condition
	 *   addCustomEvent(triggerHitCondition);
	 *   ...
	 *   <i>do something with your robot</i>
	 *   ...
	 *   // Remove the custom event based on our condition
	 *   <b>removeCustomEvent(triggerHitCondition);</b>
	 * </pre>
	 *
	 * @param condition the condition that was previous added and that must be
	 *                  removed now.
	 * @throws NullPointerException if the condition parameter has been set to
	 *                              {@code null}.
	 * @see Condition
	 * @see #addCustomEvent(Condition)
	 */
	void removeCustomEvent(Condition condition);

	/**
	 * Clears out any pending events in the robot's event queue immediately.
	 *
	 * @see #getAllEvents()
	 */
	void clearAllEvents();

	/**
	 * Returns a vector containing all events currently in the robot's queue.
	 * You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (Event event : getAllEvents()) {
	 *       if (event instanceof HitRobotEvent) {
	 *           <i>// do something with the event</i>
	 *       } else if (event instanceof HitByBulletEvent) {
	 *           <i>// do something with the event</i>
	 *       }
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all events currently in the robot's queue
	 * @see Event
	 * @see #clearAllEvents()
	 * @see #getStatusEvents()
	 * @see #getScannedRobotEvents()
	 * @see #getBulletHitEvents()
	 * @see #getBulletMissedEvents()
	 * @see #getBulletHitBulletEvents()
	 * @see #getRobotDeathEvents()
	 */
	java.util.List<Event> getAllEvents();

	/**
	 * Returns a vector containing all StatusEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (StatusEvent event : getStatusEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all StatusEvents currently in the robot's
	 *         queue
	 * @see robocode.robotinterfaces.IBasicEvents#onStatus(StatusEvent)
	 *      onStatus(StatusEvent)
	 * @see StatusEvent
	 * @see #getAllEvents()
	 * @since 1.6.1
	 */
	List<StatusEvent> getStatusEvents();

	/**
	 * Returns a vector containing all BulletMissedEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (BulletMissedEvent event : getBulletMissedEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletMissedEvents currently in the
	 *         robot's queue
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletMissed(BulletMissedEvent)
	 *      onBulletMissed(BulletMissedEvent)
	 * @see BulletMissedEvent
	 * @see #getAllEvents()
	 */
	List<BulletMissedEvent> getBulletMissedEvents();

	/**
	 * Returns a vector containing all BulletHitBulletEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (BulletHitBulletEvent event : getBulletHitBulletEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletHitBulletEvents currently in the
	 *         robot's queue
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletHitBullet(BulletHitBulletEvent)
	 *      onBulletHitBullet(BulletHitBulletEvent)
	 * @see BulletHitBulletEvent
	 * @see #getAllEvents()
	 */
	List<BulletHitBulletEvent> getBulletHitBulletEvents();

	/**
	 * Returns a vector containing all BulletHitEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (BulletHitEvent event: getBulletHitEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all BulletHitEvents currently in the robot's
	 *         queue
	 * @see robocode.robotinterfaces.IBasicEvents#onBulletHit(BulletHitEvent)
	 *      onBulletHit(BulletHitEvent)
	 * @see BulletHitEvent
	 * @see #getAllEvents()
	 */
	List<BulletHitEvent> getBulletHitEvents();

	/**
	 * Returns a vector containing all HitByBulletEvents currently in the
	 * robot's queue. You might, for example, call this while processing
	 * another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (HitByBulletEvent event : getHitByBulletEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitByBulletEvents currently in the
	 *         robot's queue
	 * @see robocode.robotinterfaces.IBasicEvents#onHitByBullet(HitByBulletEvent)
	 *      onHitByBullet(HitByBulletEvent)
	 * @see HitByBulletEvent
	 * @see #getAllEvents()
	 */
	List<HitByBulletEvent> getHitByBulletEvents();

	/**
	 * Returns a vector containing all HitRobotEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (HitRobotEvent event : getHitRobotEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitRobotEvents currently in the robot's
	 *         queue
	 * @see robocode.robotinterfaces.IBasicEvents#onHitRobot(HitRobotEvent)
	 *      onHitRobot(HitRobotEvent)
	 * @see HitRobotEvent
	 * @see #getAllEvents()
	 */
	List<HitRobotEvent> getHitRobotEvents();

	/**
	 * Returns a vector containing all HitWallEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (HitWallEvent event : getHitWallEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all HitWallEvents currently in the robot's
	 *         queue
	 * @see robocode.robotinterfaces.IBasicEvents#onHitWall(HitWallEvent)
	 *      onHitWall(HitWallEvent)
	 * @see HitWallEvent
	 * @see #getAllEvents()
	 */
	List<HitWallEvent> getHitWallEvents();

	/**
	 * Returns a vector containing all RobotDeathEvents currently in the robot's
	 * queue. You might, for example, call this while processing another event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (RobotDeathEvent event : getRobotDeathEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all RobotDeathEvents currently in the robot's
	 *         queue
	 * @see robocode.robotinterfaces.IBasicEvents#onRobotDeath(RobotDeathEvent)
	 *      onRobotDeath(RobotDeathEvent)
	 * @see RobotDeathEvent
	 * @see #getAllEvents()
	 */
	List<RobotDeathEvent> getRobotDeathEvents();

	/**
	 * Returns a vector containing all ScannedRobotEvents currently in the
	 * robot's queue. You might, for example, call this while processing another
	 * event.
	 * <p>
	 * Example:
	 * <pre>
	 *   for (ScannedRobotEvent event : getScannedRobotEvents()) {
	 *       <i>// do something with the event</i>
	 *   }
	 * </pre>
	 *
	 * @return a vector containing all ScannedRobotEvents currently in the
	 *         robot's queue
	 * @see robocode.robotinterfaces.IBasicEvents#onScannedRobot(ScannedRobotEvent)
	 *      onScannedRobot(ScannedRobotEvent)
	 * @see ScannedRobotEvent
	 * @see #getAllEvents()
	 */
	List<ScannedRobotEvent> getScannedRobotEvents();

	/**
	 * Returns a file representing a data directory for the robot, which can be
	 * written to using {@link RobocodeFileOutputStream} or
	 * {@link RobocodeFileWriter}.
	 * <p>
	 * The system will automatically create the directory for you, so you do not
	 * need to create it by yourself.
	 *
	 * @return a file representing the data directory for your robot
	 * @see #getDataFile(String)
	 * @see RobocodeFileOutputStream
	 * @see RobocodeFileWriter
	 */
	File getDataDirectory();

	/**
	 * Returns a file in your data directory that you can write to using
	 * {@link RobocodeFileOutputStream} or {@link RobocodeFileWriter}.
	 * <p>
	 * The system will automatically create the directory for you, so you do not
	 * need to create it by yourself.
	 * <p>
	 * Please notice that the max. size of your data file is set to 200000
	 * (~195 KB).
	 * <p>
	 * See the {@code sample.SittingDuck} to see an example of how to use this
	 * method.
	 *
	 * @param filename the file name of the data file for your robot
	 * @return a file representing the data file for your robot
	 * @see #getDataDirectory()
	 * @see RobocodeFileOutputStream
	 * @see RobocodeFileWriter
	 */
	File getDataFile(String filename);

	/**
	 * Returns the data quota available in your data directory, i.e. the amount
	 * of bytes left in the data directory for the robot.
	 *
	 * @return the amount of bytes left in the robot's data directory
	 * @see #getDataDirectory()
	 * @see #getDataFile(String)
	 */
	long getDataQuotaAvailable();
}
