/*******************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Ported to Java 5
 *     - Updated Javadocs
 *     - The uninitializedException() method does not need a method name as input
 *       parameter anymore
 *     - Changed the priority of the DeathEvent from 100 to -1 in order to let
 *       robots process events before they die
 *     - Added getStatusEvents()
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *******************************************************************************/
using System;
using System.Collections.Generic;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
    /// <summary>
    /// A more advanced type of robot than Robot that allows non-blocking calls,
    /// custom events, and writes to the filesystem.
    /// <p/>
    /// If you have not already, you should create a {@link Robot} first.
    ///
    /// @author Mathew A. Nelson (original)
    /// @author Flemming N. Larsen (contributor)
    /// @author Robert D. Maupin (contributor)
    /// @author Pavel Savara (contributor)
    /// @see <a target="_top" href="http://robocode.sourceforge.net">
    ///      robocode.sourceforge.net</a>
    /// @see <a href="http://robocode.sourceforge.net/myfirstrobot/MyFirstRobot.html">
    ///      Building your first robot<a>
    /// @see JuniorRobot
    /// @see Robot
    /// @see TeamRobot
    /// @see Droid
    /// </summary>
    public abstract class AdvancedRobot : Robot, IAdvancedRobot, IAdvancedEvents
    {
        /// <summary>
        /// Returns the distance remaining in the robot's current move measured in
        /// pixels.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the robot is currently moving forwards. Negative values means
        /// that the robot is currently moving backwards. If the returned value is 0,
        /// the robot currently stands still.
        ///
        /// @return the distance remaining in the robot's current move measured in
        ///         pixels.
        /// @see #getTurnRemaining() getTurnRemaining()
        /// @see #getTurnRemainingRadians() getTurnRemainingRadians()
        /// @see #getGunTurnRemaining() getGunTurnRemaining()
        /// @see #getGunTurnRemainingRadians() getGunTurnRemainingRadians()
        /// @see #getRadarTurnRemaining() getRadarTurnRemaining()
        /// @see #getRadarTurnRemainingRadians() getRadarTurnRemainingRadians()
        /// </summary>
        public double getDistanceRemaining()
        {
            if (peer != null)
            {
                return peer.getDistanceRemaining();
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Returns the angle remaining in the robots's turn, in degrees.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the robot is currently turning to the right. Negative values
        /// means that the robot is currently turning to the left. If the returned
        /// value is 0, the robot is currently not turning.
        ///
        /// @return the angle remaining in the robots's turn, in degrees
        /// @see #getTurnRemainingRadians() getTurnRemainingRadians()
        /// @see #getDistanceRemaining() getDistanceRemaining()
        /// @see #getGunTurnRemaining() getGunTurnRemaining()
        /// @see #getGunTurnRemainingRadians() getGunTurnRemainingRadians()
        /// @see #getRadarTurnRemaining() getRadarTurnRemaining()
        /// @see #getRadarTurnRemainingRadians() getRadarTurnRemainingRadians()
        /// </summary>
        public double getTurnRemaining()
        {
            if (peer != null)
            {
                return Utils.toDegrees(peer.getBodyTurnRemaining());
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Returns the angle remaining in the gun's turn, in degrees.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the gun is currently turning to the right. Negative values
        /// means that the gun is currently turning to the left. If the returned
        /// value is 0, the gun is currently not turning.
        ///
        /// @return the angle remaining in the gun's turn, in degrees
        /// @see #getGunTurnRemainingRadians() getGunTurnRemainingRadians()
        /// @see #getDistanceRemaining() getDistanceRemaining()
        /// @see #getTurnRemaining() getTurnRemaining()
        /// @see #getTurnRemainingRadians() getTurnRemainingRadians()
        /// @see #getRadarTurnRemaining() getRadarTurnRemaining()
        /// @see #getRadarTurnRemainingRadians() getRadarTurnRemainingRadians()
        /// </summary>
        public double getGunTurnRemaining()
        {
            if (peer != null)
            {
                return Utils.toDegrees(peer.getGunTurnRemaining());
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Returns the angle remaining in the radar's turn, in degrees.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the radar is currently turning to the right. Negative values
        /// means that the radar is currently turning to the left. If the returned
        /// value is 0, the radar is currently not turning.
        ///
        /// @return the angle remaining in the radar's turn, in degrees
        /// @see #getRadarTurnRemainingRadians() getRadarTurnRemainingRadians()
        /// @see #getDistanceRemaining() getDistanceRemaining()
        /// @see #getGunTurnRemaining() getGunTurnRemaining()
        /// @see #getGunTurnRemainingRadians() getGunTurnRemainingRadians()
        /// @see #getRadarTurnRemaining() getRadarTurnRemaining()
        /// @see #getRadarTurnRemainingRadians() getRadarTurnRemainingRadians()
        /// </summary>
        public double getRadarTurnRemaining()
        {
            if (peer != null)
            {
                return Utils.toDegrees(peer.getRadarTurnRemaining());
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Sets the robot to move ahead (forward) by distance measured in pixels
        /// when the next execution takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// {@link #execute()} or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot is set to move ahead, and negative
        /// values means that the robot is set to move back. If 0 is given as input,
        /// the robot will stop its movement, but will have to decelerate
        /// till it stands still, and will thus not be able to stop its movement
        /// immediately, but eventually.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to move 50 pixels ahead
        ///   setAhead(50);
        /// <p/>
        ///   // Set the robot to move 100 pixels back
        ///   // (overrides the previous order)
        ///   setAhead(-100);
        /// <p/>
        ///   ...
        ///   // Executes the last setAhead()
        ///   execute();
        /// </pre>
        ///
        /// @param distance the distance to move measured in pixels.
        ///                 If {@code distance} > 0 the robot is set to move ahead.
        ///                 If {@code distance} < 0 the robot is set to move back.
        ///                 If {@code distance} = 0 the robot is set to stop its movement.
        /// @see #ahead(double) ahead(double)
        /// @see #back(double) back(double)
        /// @see #setBack(double)
        /// </summary>
        public void setAhead(double distance)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setMove(distance);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot to move back by distance measured in pixels when the next
        /// execution takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// {@link #execute()} or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot is set to move back, and negative
        /// values means that the robot is set to move ahead. If 0 is given as input,
        /// the robot will stop its movement, but will have to decelerate
        /// till it stands still, and will thus not be able to stop its movement
        /// immediately, but eventually.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to move 50 pixels back
        ///   setBack(50);
        /// <p/>
        ///   // Set the robot to move 100 pixels ahead
        ///   // (overrides the previous order)
        ///   setBack(-100);
        /// <p/>
        ///   ...
        ///   // Executes the last setBack()
        ///   execute();
        /// </pre>
        ///
        /// @param distance the distance to move measured in pixels.
        ///                 If {@code distance} > 0 the robot is set to move back.
        ///                 If {@code distance} < 0 the robot is set to move ahead.
        ///                 If {@code distance} = 0 the robot is set to stop its movement.
        /// @see #back(double) back(double)
        /// @see #ahead(double) ahead(double)
        /// @see #setAhead(double)
        /// </summary>
        public void setBack(double distance)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setMove(-distance);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's body to turn left by degrees when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's body is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to turn 180 degrees to the left
        ///   setTurnLeft(180);
        /// <p/>
        ///   // Set the robot to turn 90 degrees to the right instead of left
        ///   // (overrides the previous order)
        ///   setTurnLeft(-90);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnLeft()
        ///   execute();
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's body to the left.
        ///                If {@code degrees} > 0 the robot is set to turn left.
        ///                If {@code degrees} < 0 the robot is set to turn right.
        ///                If {@code degrees} = 0 the robot is set to stop turning.
        /// @see #setTurnLeftRadians(double) setTurnLeftRadians(double)
        /// @see #turnLeft(double) turnLeft(double)
        /// @see #turnLeftRadians(double) turnLeftRadians(double)
        /// @see #turnRight(double) turnRight(double)
        /// @see #turnRightRadians(double) turnRightRadians(double)
        /// @see #setTurnRight(double) setTurnRight(double)
        /// @see #setTurnRightRadians(double) setTurnRightRadians(double)
        /// </summary>
        public void setTurnLeft(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnBody(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's body to turn right by degrees when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's body is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to turn 180 degrees to the right
        ///   setTurnRight(180);
        /// <p/>
        ///   // Set the robot to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   setTurnRight(-90);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnRight()
        ///   execute();
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's body to the right.
        ///                If {@code degrees} > 0 the robot is set to turn right.
        ///                If {@code degrees} < 0 the robot is set to turn left.
        ///                If {@code degrees} = 0 the robot is set to stop turning.
        /// @see #setTurnRightRadians(double) setTurnRightRadians(double)
        /// @see #turnRight(double) turnRight(double)
        /// @see #turnRightRadians(double) turnRightRadians(double)
        /// @see #turnLeft(double) turnLeft(double)
        /// @see #turnLeftRadians(double) turnLeftRadians(double)
        /// @see #setTurnLeft(double) setTurnLeft(double)
        /// @see #setTurnLeftRadians(double) setTurnLeftRadians(double)
        /// </summary>
        public void setTurnRight(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnBody(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the gun to fire a bullet when the next execution takes place.
        /// The bullet will travel in the direction the gun is pointing.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// The specified bullet power is an amount of energy that will be taken from
        /// the robot's energy. Hence, the more power you want to spend on the
        /// bullet, the more energy is taken from your robot.
        /// <p/>
        /// The bullet will do (4 * power) damage if it hits another robot. If power
        /// is greater than 1, it will do an additional 2 * (power - 1) damage.
        /// You will get (3 * power) back if you hit the other robot. You can call
        /// Rules#getBulletDamage(double)} for getting the damage that a
        /// bullet with a specific bullet power will do.
        /// <p/>
        /// The specified bullet power should be between
        /// {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
        /// <p/>
        /// Note that the gun cannot fire if the gun is overheated, meaning that
        /// {@link #getGunHeat()} returns a value > 0.
        /// <p/>
        /// An evnt is generated when the bullet hits a robot, wall, or another
        /// bullet.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Fire a bullet with maximum power if the gun is ready
        ///   if (getGunHeat() == 0) {
        ///       setFire(Rules.MAX_BULLET_POWER);
        ///   }
        ///   ...
        ///   execute();
        /// </pre>
        ///
        /// @param power the amount of energy given to the bullet, and subtracted
        ///              from the robot's energy.
        /// @see #setFireBullet(double)
        /// @see #fire(double) fire(double)
        /// @see #fireBullet(double) fireBullet(double)
        /// @see #getGunHeat() getGunHeat()
        /// @see #getGunCoolingRate() getGunCoolingRate()
        /// @see #onBulletHit(BulletHitEvent) onBulletHit(BulletHitEvent)
        /// @see #onBulletHitBullet(BulletHitBulletEvent) onBulletHitBullet(BulletHitBulletEvent)
        /// @see #onBulletMissed(BulletMissedEvent) onBulletMissed(BulletMissedEvent)
        /// </summary>
        public void setFire(double power)
        {
            if (peer != null)
            {
                peer.setFire(power);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the gun to fire a bullet when the next execution takes place.
        /// The bullet will travel in the direction the gun is pointing.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// The specified bullet power is an amount of energy that will be taken from
        /// the robot's energy. Hence, the more power you want to spend on the
        /// bullet, the more energy is taken from your robot.
        /// <p/>
        /// The bullet will do (4 * power) damage if it hits another robot. If power
        /// is greater than 1, it will do an additional 2 * (power - 1) damage.
        /// You will get (3 * power) back if you hit the other robot. You can call
        /// {@link Rules#getBulletDamage(double)} for getting the damage that a
        /// bullet with a specific bullet power will do.
        /// <p/>
        /// The specified bullet power should be between
        /// {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
        /// <p/>
        /// Note that the gun cannot fire if the gun is overheated, meaning that
        /// {@link #getGunHeat()} returns a value > 0.
        /// <p/>
        /// A evnt is generated when the bullet hits a robot
        /// ({@link BulletHitEvent}), wall ({@link BulletMissedEvent}), or another
        /// bullet ({@link BulletHitBulletEvent}).
        /// <p/>
        /// Example:
        /// <pre>
        ///   Bullet bullet = null;
        /// <p/>
        ///   // Fire a bullet with maximum power if the gun is ready
        ///   if (getGunHeat() == 0) {
        ///       bullet = setFireBullet(Rules.MAX_BULLET_POWER);
        ///   }
        ///   ...
        ///   execute();
        ///   ...
        ///   // Get the velocity of the bullet
        ///   if (bullet != null) {
        ///       double bulletVelocity = bullet.getVelocity();
        ///   }
        /// </pre>
        ///
        /// @param power the amount of energy given to the bullet, and subtracted
        ///              from the robot's energy.
        /// @return a {@link Bullet} that contains information about the bullet if it
        ///         was actually fired, which can be used for tracking the bullet after it
        ///         has been fired. If the bullet was not fired, {@code null} is returned.
        /// @see #setFire(double)
        /// @see Bullet
        /// @see #fire(double) fire(double)
        /// @see #fireBullet(double) fireBullet(double)
        /// @see #getGunHeat() getGunHeat()
        /// @see #getGunCoolingRate() getGunCoolingRate()
        /// @see #onBulletHit(BulletHitEvent) onBulletHit(BulletHitEvent)
        /// @see #onBulletHitBullet(BulletHitBulletEvent) onBulletHitBullet(BulletHitBulletEvent)
        /// @see #onBulletMissed(BulletMissedEvent) onBulletMissed(BulletMissedEvent)
        /// </summary>
        public Bullet setFireBullet(double power)
        {
            if (peer != null)
            {
                return peer.setFire(power);
            }
            uninitializedException();
            return null;
        }

        /// <summary>
        /// Registers a custom evnt to be called when a condition is met.
        /// When you are finished with your condition or just want to remove it you
        /// must call {@link #removeCustomEvent(Condition)}.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Create the condition for our custom event
        ///   Condition triggerHitCondition = new Condition("triggerhit") {
        ///       public bool test() {
        ///           return (getEnergy() <= trigger);
        ///       };
        ///   }
        /// <p/>
        ///   // Add our custom evnt based on our condition
        ///   <b>addCustomEvent(triggerHitCondition);</b>
        /// </pre>
        ///
        /// @param condition the condition that must be met.
        /// @throws NullPointerException if the condition parameter has been set to
        ///                              {@code null}.
        /// @see Condition
        /// @see #removeCustomEvent(Condition)
        /// </summary>
        public void addCustomEvent(Condition condition)
        {
            if (condition == null)
            {
                throw new ArgumentException("the condition cannot be null");
            }
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).addCustomEvent(condition);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Removes a custom evnt that was previously added by calling
        /// {@link #addCustomEvent(Condition)}.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Create the condition for our custom event
        ///   Condition triggerHitCondition = new Condition("triggerhit") {
        ///       public bool test() {
        ///           return (getEnergy() <= trigger);
        ///       };
        ///   }
        /// <p/>
        ///   // Add our custom evnt based on our condition
        ///   addCustomEvent(triggerHitCondition);
        ///   ...
        ///   <i>do something with your robot</i>
        ///   ...
        ///   // Remove the custom evnt based on our condition
        ///   <b>removeCustomEvent(triggerHitCondition);</b>
        /// </pre>
        ///
        /// @param condition the condition that was previous added and that must be
        ///                  removed now.
        /// @throws NullPointerException if the condition parameter has been set to
        ///                              {@code null}.
        /// @see Condition
        /// @see #addCustomEvent(Condition)
        /// </summary>
        public void removeCustomEvent(Condition condition)
        {
            if (condition == null)
            {
                throw new ArgumentException("the condition cannot be null");
            }
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).removeCustomEvent(condition);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Clears output any pending events in the robot's evnt queue immediately.
        ///
        /// @see #getAllEvents()
        /// </summary>
        public void clearAllEvents()
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).clearAllEvents();
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Executes any pending actions, or continues executing actions that are
        /// in process. This call returns after the actions have been started.
        /// <p/>
        /// Note that advanced robots <em>must</em> call this function in order to
        /// execute pending set* calls like e.g. {@link #setAhead(double)},
        /// {@link #setFire(double)}, {@link #setTurnLeft(double)} etc. Otherwise,
        /// these calls will never get executed.
        /// <p/>
        /// In this example the robot will move while turning:
        /// <pre>
        ///   setTurnRight(90);
        ///   setAhead(100);
        ///   execute();
        /// <p/>
        ///   while (getDistanceRemaining() > 0 && getTurnRemaining() > 0) {
        ///       execute();
        ///   }
        /// </pre>
        /// </summary>
        public virtual void execute()
        {
            if (peer != null)
            {
                peer.execute();
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Returns a vector containing all events currently in the robot's queue.
        /// You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (Event evnt : getAllEvents()) {
        ///       if (event instanceof HitRobotEvent) {
        ///           <i>// do something with the event</i>
        ///       } else if (event instanceof HitByBulletEvent) {
        ///           <i>// do something with the event</i>
        ///       }
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all events currently in the robot's queue
        /// @see Event
        /// @see #clearAllEvents()
        /// @see #getStatusEvents()
        /// @see #getScannedRobotEvents()
        /// @see #getBulletHitEvents()
        /// @see #getBulletMissedEvents()
        /// @see #getBulletHitBulletEvents()
        /// @see #getRobotDeathEvents()
        /// </summary>
        public IList<Event> getAllEvents()
        {
            if (peer != null)
            {
                return new List<Event>(((IAdvancedRobotPeer) peer).getAllEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a vector containing all BulletHitBulletEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (BulletHitBulletEvent evnt : getBulletHitBulletEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all BulletHitBulletEvents currently in the
        ///         robot's queue
        /// @see #onBulletHitBullet(BulletHitBulletEvent) onBulletHitBullet(BulletHitBulletEvent)
        /// @see BulletHitBulletEvent
        /// @see #getAllEvents()
        /// </summary>
        public IList<BulletHitBulletEvent> getBulletHitBulletEvents()
        {
            if (peer != null)
            {
                return new List<BulletHitBulletEvent>(((IAdvancedRobotPeer) peer).getBulletHitBulletEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a vector containing all BulletHitEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (BulletHitEvent event: getBulletHitEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all BulletHitEvents currently in the robot's
        ///         queue
        /// @see #onBulletHit(BulletHitEvent) onBulletHit(BulletHitEvent)
        /// @see BulletHitEvent
        /// @see #getAllEvents()
        /// </summary>
        public IList<BulletHitEvent> getBulletHitEvents()
        {
            if (peer != null)
            {
                return new List<BulletHitEvent>(((IAdvancedRobotPeer) peer).getBulletHitEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a vector containing all BulletMissedEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (BulletMissedEvent evnt : getBulletMissedEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all BulletMissedEvents currently in the
        ///         robot's queue
        /// @see #onBulletMissed(BulletMissedEvent) onBulletMissed(BulletMissedEvent)
        /// @see BulletMissedEvent
        /// @see #getAllEvents()
        /// </summary>
        public IList<BulletMissedEvent> getBulletMissedEvents()
        {
            if (peer != null)
            {
                return new List<BulletMissedEvent>(((IAdvancedRobotPeer) peer).getBulletMissedEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a file representing a data directory for the robot, which can be
        /// written to using {@link RobocodeFileOutputStream} or
        /// {@link RobocodeFileWriter}.
        /// <p/>
        /// The system will automatically create the directory for you, so you do not
        /// need to create it by yourself.
        ///
        /// @return a file representing the data directory for your robot
        /// @see #getDataFile(string)
        /// @see RobocodeFileOutputStream
        /// @see RobocodeFileWriter
        /// </summary>
        public string getDataDirectory()
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).getDataDirectory();
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a file in your data directory that you can write to using
        /// {@link RobocodeFileOutputStream} or {@link RobocodeFileWriter}.
        /// <p/>
        /// The system will automatically create the directory for you, so you do not
        /// need to create it by yourself.
        /// <p/>
        /// Please notice that the max. size of your data file is set to 200000
        /// (~195 KB).
        /// <p/>
        /// See the {@code sample.SittingDuck} to see an example of how to use this
        /// method.
        ///
        /// @param filename the file name of the data file for your robot
        /// @return a file representing the data file for your robot
        /// @see #getDataDirectory()
        /// @see RobocodeFileOutputStream
        /// @see RobocodeFileWriter
        /// </summary>
        public string getDataFile(string filename)
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).getDataFile(filename);
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns the data quota available in your data directory, i.e. the amount
        /// of bytes left in the data directory for the robot.
        ///
        /// @return the amount of bytes left in the robot's data directory
        /// @see #getDataDirectory()
        /// @see #getDataFile(string)
        /// </summary>
        public long getDataQuotaAvailable()
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).getDataQuotaAvailable();
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Returns the current priority of a class of events.
        /// An evnt priority is a value from 0 - 99. The higher value, the higher
        /// priority.
        /// <p/>
        /// Example:
        /// <pre>
        ///   int myHitRobotPriority = getEventPriority("HitRobotEvent");
        /// </pre>
        /// <p/>
        /// The default priorities are, from highest to lowest:
        /// <pre>
        ///   {@link BattleEndedEvent}:     100 (reserved)
        ///   {@link WinEvent}:             100 (reserved)
        ///   {@link SkippedTurnEvent}:     100 (reserved)
        ///   {@link StatusEvent}:           99
        ///   Key and mouse events:  98
        ///   {@link CustomEvent}:           80 (default value)
        ///   {@link MessageEvent}:          75
        ///   {@link RobotDeathEvent}:       70
        ///   {@link BulletMissedEvent}:     60
        ///   {@link BulletHitBulletEvent}:  55
        ///   {@link BulletHitEvent}:        50
        ///   {@link HitByBulletEvent}:      40
        ///   {@link HitWallEvent}:          30
        ///   {@link HitRobotEvent}:         20
        ///   {@link ScannedRobotEvent}:     10
        ///   {@link PaintEvent}:             5
        ///   {@link DeathEvent}:            -1 (reserved)
        /// </pre>
        ///
        /// @param eventClass the name of the evnt class (string)
        /// @return the current priority of a class of events
        /// @see #setEventPriority(string, int)
        /// </summary>
        public int getEventPriority(string eventClass)
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).getEventPriority(eventClass);
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Returns a vector containing all HitByBulletEvents currently in the
        /// robot's queue. You might, for example, call this while processing
        /// another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (HitByBulletEvent evnt : getHitByBulletEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all HitByBulletEvents currently in the
        ///         robot's queue
        /// @see #onHitByBullet(HitByBulletEvent) onHitByBullet(HitByBulletEvent)
        /// @see HitByBulletEvent
        /// @see #getAllEvents()
        /// </summary>
        public IList<HitByBulletEvent> getHitByBulletEvents()
        {
            if (peer != null)
            {
                return new List<HitByBulletEvent>(((IAdvancedRobotPeer) peer).getHitByBulletEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a vector containing all HitRobotEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (HitRobotEvent evnt : getHitRobotEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all HitRobotEvents currently in the robot's
        ///         queue
        /// @see #onHitRobot(HitRobotEvent) onHitRobot(HitRobotEvent)
        /// @see HitRobotEvent
        /// @see #getAllEvents()
        /// </summary>
        public IList<HitRobotEvent> getHitRobotEvents()
        {
            if (peer != null)
            {
                return new List<HitRobotEvent>(((IAdvancedRobotPeer) peer).getHitRobotEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a vector containing all HitWallEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (HitWallEvent evnt : getHitWallEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all HitWallEvents currently in the robot's
        ///         queue
        /// @see #onHitWall(HitWallEvent) onHitWall(HitWallEvent)
        /// @see HitWallEvent
        /// @see #getAllEvents()
        /// </summary>
        public IList<HitWallEvent> getHitWallEvents()
        {
            if (peer != null)
            {
                return new List<HitWallEvent>(((IAdvancedRobotPeer) peer).getHitWallEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a vector containing all RobotDeathEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (RobotDeathEvent evnt : getRobotDeathEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all RobotDeathEvents currently in the robot's
        ///         queue
        /// @see #onRobotDeath(RobotDeathEvent) onRobotDeath(RobotDeathEvent)
        /// @see RobotDeathEvent
        /// @see #getAllEvents()
        /// </summary>
        public IList<RobotDeathEvent> getRobotDeathEvents()
        {
            if (peer != null)
            {
                return new List<RobotDeathEvent>(((IAdvancedRobotPeer) peer).getRobotDeathEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a vector containing all ScannedRobotEvents currently in the
        /// robot's queue. You might, for example, call this while processing another
        /// evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (ScannedRobotEvent evnt : getScannedRobotEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all ScannedRobotEvents currently in the
        ///         robot's queue
        /// @see #onScannedRobot(ScannedRobotEvent) onScannedRobot(ScannedRobotEvent)
        /// @see ScannedRobotEvent
        /// @see #getAllEvents()
        /// </summary>
        public IList<ScannedRobotEvent> getScannedRobotEvents()
        {
            if (peer != null)
            {
                return new List<ScannedRobotEvent>(((IAdvancedRobotPeer) peer).getScannedRobotEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Returns a vector containing all StatusEvents currently in the robot's
        /// queue. You might, for example, call this while processing another evnt.
        /// <p/>
        /// Example:
        /// <pre>
        ///   for (StatusEvent evnt : getStatusEvents()) {
        ///       <i>// do something with the event</i>
        ///   }
        /// </pre>
        ///
        /// @return a vector containing all StatusEvents currently in the robot's queue
        /// @see #onStatus(StatusEvent) onStatus(StatusEvent)
        /// @see StatusEvent
        /// @see #getAllEvents()
        /// @since 1.6.1
        /// </summary>
        public IList<StatusEvent> getStatusEvents()
        {
            if (peer != null)
            {
                return new List<StatusEvent>(((IAdvancedRobotPeer) peer).getStatusEvents());
            }
            uninitializedException();
            return null; // never called
        }

        /// <summary>
        /// Checks if the gun is set to adjust for the robot turning, i.e. to turn
        /// independent from the robot's body turn.
        /// <p/>
        /// This call returns {@code true} if the gun is set to turn independent of
        /// the turn of the robot's body. Otherwise, {@code false} is returned,
        /// meaning that the gun is set to turn with the robot's body turn.
        ///
        /// @return {@code true} if the gun is set to turn independent of the robot
        ///         turning; {@code false} if the gun is set to turn with the robot
        ///         turning
        /// @see #setAdjustGunForRobotTurn(bool) setAdjustGunForRobotTurn(bool)
        /// @see #isAdjustRadarForRobotTurn()
        /// @see #isAdjustRadarForGunTurn()
        /// </summary>
        public bool isAdjustGunForRobotTurn()
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).isAdjustGunForBodyTurn();
            }
            uninitializedException();
            return false; // never called
        }

        /// <summary>
        /// Checks if the radar is set to adjust for the robot turning, i.e. to turn
        /// independent from the robot's body turn.
        /// <p/>
        /// This call returns {@code true} if the radar is set to turn independent of
        /// the turn of the robot. Otherwise, {@code false} is returned, meaning that
        /// the radar is set to turn with the robot's turn.
        ///
        /// @return {@code true} if the radar is set to turn independent of the robot
        ///         turning; {@code false} if the radar is set to turn with the robot
        ///         turning
        /// @see #setAdjustRadarForRobotTurn(bool) setAdjustRadarForRobotTurn(bool)
        /// @see #isAdjustGunForRobotTurn()
        /// @see #isAdjustRadarForGunTurn()
        /// </summary>
        public bool isAdjustRadarForRobotTurn()
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).isAdjustRadarForBodyTurn();
            }
            uninitializedException();
            return false; // never called
        }

        /// <summary>
        /// Checks if the radar is set to adjust for the gun turning, i.e. to turn
        /// independent from the gun's turn.
        /// <p/>
        /// This call returns {@code true} if the radar is set to turn independent of
        /// the turn of the gun. Otherwise, {@code false} is returned, meaning that
        /// the radar is set to turn with the gun's turn.
        ///
        /// @return {@code true} if the radar is set to turn independent of the gun
        ///         turning; {@code false} if the radar is set to turn with the gun
        ///         turning
        /// @see #setAdjustRadarForGunTurn(bool) setAdjustRadarForGunTurn(bool)
        /// @see #isAdjustGunForRobotTurn()
        /// @see #isAdjustRadarForRobotTurn()
        /// </summary>
        public bool isAdjustRadarForGunTurn()
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).isAdjustRadarForGunTurn();
            }
            uninitializedException();
            return false; // never called
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void onCustomEvent(CustomEvent evnt)
        {
        }

        /// <summary>
        /// Sets the priority of a class of events.
        /// <p/>
        /// Events are sent to the onXXX handlers in order of priority.
        /// Higher priority events can interrupt lower priority events.
        /// For events with the same priority, newer events are always sent first.
        /// Valid priorities are 0 - 99, where 100 is reserved and 80 is the default
        /// priority.
        /// <p/>
        /// Example:
        /// <pre>
        ///   setEventPriority("RobotDeathEvent", 15);
        /// </pre>
        /// <p/>
        /// The default priorities are, from highest to lowest:
        /// <pre>
        /// 	 {@link WinEvent}:             100 (reserved)
        /// 	 {@link SkippedTurnEvent}:     100 (reserved)
        ///   {@link StatusEvent}:           99
        /// 	 {@link CustomEvent}:           80
        /// 	 {@link MessageEvent}:          75
        /// 	 {@link RobotDeathEvent}:       70
        /// 	 {@link BulletMissedEvent}:     60
        /// 	 {@link BulletHitBulletEvent}:  55
        /// 	 {@link BulletHitEvent}:        50
        /// 	 {@link HitByBulletEvent}:      40
        /// 	 {@link HitWallEvent}:          30
        /// 	 {@link HitRobotEvent}:         20
        /// 	 {@link ScannedRobotEvent}:     10
        ///   {@link PaintEvent}:             5
        /// 	 {@link DeathEvent}:            -1 (reserved)
        /// </pre>
        /// <p/>
        /// Note that you cannot change the priority for events with the special
        /// priority value -1 or 100 (reserved) as these evnt are system events.
        /// Also note that you cannot change the priority of CustomEvent.
        /// Instead you must change the priority of the condition(s) for your custom
        /// event(s).
        ///
        /// @param eventClass the name of the evnt class (string) to set the
        ///                   priority for
        /// @param priority   the new priority for that evnt class
        /// @see #getEventPriority(string)
        /// @see #setInterruptible(bool)
        /// @since 1.5, the priority of DeathEvent was changed from 100 to -1 in
        ///        order to let robots process pending events on its evnt queue before
        ///        it dies. When the robot dies, it will not be able to process events.
        /// </summary>
        public void setEventPriority(string eventClass, int priority)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setEventPriority(eventClass, priority);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Call this during an evnt handler to allow new events of the same
        /// priority to restart the evnt handler.
        /// <p/>
        /// <p>Example:
        /// <pre>
        ///   public override void onScannedRobot(ScannedRobotEvent e) {
        ///       fire(1);
        ///       <b>setInterruptible(true);</b>
        ///       ahead(100); // If you see a robot while moving ahead,
        ///                   // this handler will start from the top
        ///                   // Without setInterruptible(true), we wouldn't
        ///                   // receive scan events at all!
        ///       // We'll only get here if we don't see a robot during the move.
        ///       output.println("Ok, I can't see anyone");
        ///   }
        /// </pre>
        ///
        /// @param interruptible {@code true} if the evnt handler should be
        ///                      interrupted if new events of the same priority occurs; {@code false}
        ///                      otherwise
        /// @see #setEventPriority(string, int)
        /// @see Robot#onScannedRobot(ScannedRobotEvent) onScannedRobot(ScannedRobotEvent)
        /// </summary>
        public void setInterruptible(bool interruptible)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setInterruptible(interruptible);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the maximum turn rate of the robot measured in degrees if the robot
        /// should turn slower than {@link Rules#MAX_TURN_RATE} (10 degress/turn).
        ///
        /// @param newMaxTurnRate the new maximum turn rate of the robot measured in
        ///                       degrees. Valid values are 0 - {@link Rules#MAX_TURN_RATE}
        /// @see #turnRight(double) turnRight(double)
        /// @see #turnLeft(double) turnLeft(double)
        /// @see #setTurnRight(double)
        /// @see #setTurnLeft(double)
        /// @see #setMaxVelocity(double)
        /// </summary>
        public void setMaxTurnRate(double newMaxTurnRate)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setMaxTurnRate(Utils.toRadians(newMaxTurnRate));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the maximum velocity of the robot measured in pixels/turn if the
        /// robot should move slower than {@link Rules#MAX_VELOCITY} (8 pixels/turn).
        ///
        /// @param newMaxVelocity the new maximum turn rate of the robot measured in
        ///                       pixels/turn. Valid values are 0 - {@link Rules#MAX_VELOCITY}
        /// @see #ahead(double)
        /// @see #setAhead(double)
        /// @see #back(double)
        /// @see #setBack(double)
        /// @see #setMaxTurnRate(double)
        /// </summary>
        public void setMaxVelocity(double newMaxVelocity)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setMaxVelocity(newMaxVelocity);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot to resume the movement stopped by {@link #stop() stop()}
        /// or {@link #setStop()}, if any.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// {@link #execute()} or take an action that executes.
        ///
        /// @see #resume() resume()
        /// @see #stop() stop()
        /// @see #stop(bool) stop(bool)
        /// @see #setStop()
        /// @see #setStop(bool)
        /// @see #execute()
        /// </summary>
        public void setResume()
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setResume();
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// This call is identical to {@link #stop() stop()}, but returns immediately, and
        /// will not execute until you call {@link #execute()} or take an action that
        /// executes.
        /// <p/>
        /// If there is already movement saved from a previous stop, this will have
        /// no effect.
        /// <p/>
        /// This call is equivalent to calling {@code setStop(false)};
        ///
        /// @see #stop() stop()
        /// @see #stop(bool) stop(bool)
        /// @see #resume() resume()
        /// @see #setResume()
        /// @see #setStop(bool)
        /// @see #execute()
        /// </summary>
        public void setStop()
        {
            setStop(false);
        }

        /// <summary>
        /// This call is identical to {@link #stop(bool) stop(bool)}, but
        /// returns immediately, and will not execute until you call
        /// {@link #execute()} or take an action that executes.
        /// <p/>
        /// If there is already movement saved from a previous stop, you can
        /// overwrite it by calling {@code setStop(true)}.
        ///
        /// @param overwrite {@code true} if the movement saved from a previous stop
        ///                  should be overwritten; {@code false} otherwise.
        /// @see #stop() stop()
        /// @see #stop(bool) stop(bool)
        /// @see #resume() resume()
        /// @see #setResume()
        /// @see #setStop()
        /// @see #execute()
        /// </summary>
        public void setStop(bool overwrite)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setStop(overwrite);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's gun to turn left by degrees when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's gun is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the gun to turn 180 degrees to the left
        ///   setTurnGunLeft(180);
        /// <p/>
        ///   // Set the gun to turn 90 degrees to the right instead of left
        ///   // (overrides the previous order)
        ///   setTurnGunLeft(-90);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnGunLeft()
        ///   execute();
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's gun to the left.
        ///                If {@code degrees} > 0 the robot's gun is set to turn left.
        ///                If {@code degrees} < 0 the robot's gun is set to turn right.
        ///                If {@code degrees} = 0 the robot's gun is set to stop turning.
        /// @see #setTurnGunLeftRadians(double) setTurnGunLeftRadians(double)
        /// @see #turnGunLeft(double) turnGunLeft(double)
        /// @see #turnGunLeftRadians(double) turnGunLeftRadians(double)
        /// @see #turnGunRight(double) turnGunRight(double)
        /// @see #turnGunRightRadians(double) turnGunRightRadians(double)
        /// @see #setTurnGunRight(double) setTurnGunRight(double)
        /// @see #setTurnGunRightRadians(double) setTurnGunRightRadians(double)
        /// @see #setAdjustGunForRobotTurn(bool) setAdjustGunForRobotTurn(bool)
        /// </summary>
        public void setTurnGunLeft(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnGun(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's gun to turn right by degrees when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's gun is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the gun to turn 180 degrees to the right
        ///   setTurnGunRight(180);
        /// <p/>
        ///   // Set the gun to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   setTurnGunRight(-90);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnGunRight()
        ///   execute();
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's gun to the right.
        ///                If {@code degrees} > 0 the robot's gun is set to turn right.
        ///                If {@code degrees} < 0 the robot's gun is set to turn left.
        ///                If {@code degrees} = 0 the robot's gun is set to stop turning.
        /// @see #setTurnGunRightRadians(double) setTurnGunRightRadians(double)
        /// @see #turnGunRight(double) turnGunRight(double)
        /// @see #turnGunRightRadians(double) turnGunRightRadians(double)
        /// @see #turnGunLeft(double) turnGunLeft(double)
        /// @see #turnGunLeftRadians(double) turnGunLeftRadians(double)
        /// @see #setTurnGunLeft(double) setTurnGunLeft(double)
        /// @see #setTurnGunLeftRadians(double) setTurnGunLeftRadians(double)
        /// @see #setAdjustGunForRobotTurn(bool) setAdjustGunForRobotTurn(bool)
        /// </summary>
        public void setTurnGunRight(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnGun(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's radar to turn left by degrees when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's radar is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the radar to turn 180 degrees to the left
        ///   setTurnRadarLeft(180);
        /// <p/>
        ///   // Set the radar to turn 90 degrees to the right instead of left
        ///   // (overrides the previous order)
        ///   setTurnRadarLeft(-90);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnRadarLeft()
        ///   execute();
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's radar to the left.
        ///                If {@code degrees} > 0 the robot's radar is set to turn left.
        ///                If {@code degrees} < 0 the robot's radar is set to turn right.
        ///                If {@code degrees} = 0 the robot's radar is set to stop turning.
        /// @see #setTurnRadarLeftRadians(double) setTurnRadarLeftRadians(double)
        /// @see #turnRadarLeft(double) turnRadarLeft(double)
        /// @see #turnRadarLeftRadians(double) turnRadarLeftRadians(double)
        /// @see #turnRadarRight(double) turnRadarRight(double)
        /// @see #turnRadarRightRadians(double) turnRadarRightRadians(double)
        /// @see #setTurnRadarRight(double) setTurnRadarRight(double)
        /// @see #setTurnRadarRightRadians(double) setTurnRadarRightRadians(double)
        /// @see #setAdjustRadarForRobotTurn(bool) setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool) setAdjustRadarForGunTurn(bool)
        /// </summary>
        public void setTurnRadarLeft(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnRadar(-Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's radar to turn right by degrees when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's radar is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the radar to turn 180 degrees to the right
        ///   setTurnRadarRight(180);
        /// <p/>
        ///   // Set the radar to turn 90 degrees to the right instead of right
        ///   // (overrides the previous order)
        ///   setTurnRadarRight(-90);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnRadarRight()
        ///   execute();
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's radar to the right.
        ///                If {@code degrees} > 0 the robot's radar is set to turn right.
        ///                If {@code degrees} < 0 the robot's radar is set to turn left.
        ///                If {@code degrees} = 0 the robot's radar is set to stop turning.
        /// @see #setTurnRadarRightRadians(double) setTurnRadarRightRadians(double)
        /// @see #turnRadarRight(double) turnRadarRight(double)
        /// @see #turnRadarRightRadians(double) turnRadarRightRadians(double)
        /// @see #turnRadarLeft(double) turnRadarLeft(double)
        /// @see #turnRadarLeftRadians(double) turnRadarLeftRadians(double)
        /// @see #setTurnRadarLeft(double) setTurnRadarLeft(double)
        /// @see #setTurnRadarLeftRadians(double) setTurnRadarLeftRadians(double)
        /// @see #setAdjustRadarForRobotTurn(bool) setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool) setAdjustRadarForGunTurn(bool)
        /// </summary>
        public void setTurnRadarRight(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnRadar(Utils.toRadians(degrees));
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Does not return until a condition is met, i.e. when a
        /// {@link Condition#test()} returns {@code true}.
        /// <p/>
        /// This call executes immediately.
        /// <p/>
        /// See the {@code sample.Crazy} robot for how this method can be used.
        ///
        /// @param condition the condition that must be met before this call returns
        /// @see Condition
        /// @see Condition#test()
        /// </summary>
        public void waitFor(Condition condition)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).waitFor(condition);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// This method is called if your robot dies.
        /// <p/>
        /// You should override it in your robot if you want to be informed of this
        /// evnt. Actions will have no effect if called from this section. The
        /// intent is to allow you to perform calculations or print something out
        /// when the robot is killed.
        ///
        /// @param evnt the death evnt set by the game
        /// @see DeathEvent
        /// @see Event
        /// </summary>
        public override void onDeath(DeathEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void onSkippedTurn(SkippedTurnEvent evnt)
        {
        }

        /// <summary>
        /// Returns the direction that the robot's body is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///
        /// @return the direction that the robot's body is facing, in radians.
        /// @see #getHeadingDegrees()
        /// @see #getGunHeadingRadians()
        /// @see #getRadarHeadingRadians()
        /// </summary>
        public double getHeadingRadians()
        {
            if (peer != null)
            {
                return peer.getBodyHeading();
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Sets the robot's body to turn left by radians when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's body is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to turn 180 degrees to the left
        ///   setTurnLeftRadians(Math.PI);
        /// <p/>
        ///   // Set the robot to turn 90 degrees to the right instead of left
        ///   // (overrides the previous order)
        ///   setTurnLeftRadians(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnLeftRadians()
        ///   execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's body to the left.
        ///                If {@code radians} > 0 the robot is set to turn left.
        ///                If {@code radians} < 0 the robot is set to turn right.
        ///                If {@code radians} = 0 the robot is set to stop turning.
        /// @see AdvancedRobot#setTurnLeft(double) setTurnLeft(double)
        /// @see #turnLeft(double)
        /// @see #turnLeftRadians(double)
        /// @see #turnRight(double)
        /// @see #turnRightRadians(double)
        /// @see AdvancedRobot#setTurnRight(double) setTurnRight(double)
        /// @see AdvancedRobot#setTurnRightRadians(double) setTurnRightRadians(double)
        /// </summary>
        public void setTurnLeftRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer)peer).setTurnBody(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's body to turn right by radians when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's body is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the robot to turn 180 degrees to the right
        ///   setTurnRightRadians(Math.PI);
        /// <p/>
        ///   // Set the robot to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   setTurnRightRadians(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnRightRadians()
        ///   execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's body to the right.
        ///                If {@code radians} > 0 the robot is set to turn right.
        ///                If {@code radians} < 0 the robot is set to turn left.
        ///                If {@code radians} = 0 the robot is set to stop turning.
        /// @see AdvancedRobot#setTurnRight(double) setTurnRight(double)
        /// @see #turnRight(double)
        /// @see #turnRightRadians(double)
        /// @see #turnLeft(double)
        /// @see #turnLeftRadians(double)
        /// @see AdvancedRobot#setTurnLeft(double) setTurnLeft(double)
        /// @see AdvancedRobot#setTurnLeftRadians(double) setTurnLeftRadians(double)
        /// </summary>
        public void setTurnRightRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer)peer).setTurnBody(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Immediately turns the robot's body to the left by radians.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the robot's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's body is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Turn the robot 180 degrees to the left
        ///   turnLeftRadians(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot 90 degrees to the right
        ///   turnLeftRadians(-Math.PI / 2);
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's body to the left.
        ///                If {@code radians} > 0 the robot will turn right.
        ///                If {@code radians} < 0 the robot will turn left.
        ///                If {@code radians} = 0 the robot will not turn, but execute.
        /// @see #turnLeft(double)
        /// @see #turnRight(double)
        /// @see #turnRightRadians(double)
        /// @see #turnGunLeft(double)
        /// @see #turnGunLeftRadians(double)
        /// @see #turnGunRight(double)
        /// @see #turnGunRightRadians(double)
        /// @see #turnRadarLeft(double)
        /// @see #turnRadarLeftRadians(double)
        /// @see #turnRadarRight(double)
        /// @see #turnRadarRightRadians(double)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
        public void turnLeftRadians(double radians)
        {
            if (peer != null)
            {
                peer.turnBody(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Immediately turns the robot's body to the right by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the robot's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's body is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Turn the robot 180 degrees to the right
        ///   turnRightRadians(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot 90 degrees to the left
        ///   turnRightRadians(-Math.PI / 2);
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's body to the right.
        ///                If {@code radians} > 0 the robot will turn right.
        ///                If {@code radians} < 0 the robot will turn left.
        ///                If {@code radians} = 0 the robot will not turn, but execute.
        /// @see #turnRight(double)
        /// @see #turnLeft(double)
        /// @see #turnLeftRadians(double)
        /// @see #turnGunLeft(double)
        /// @see #turnGunLeftRadians(double)
        /// @see #turnGunRight(double)
        /// @see #turnGunRightRadians(double)
        /// @see #turnRadarLeft(double)
        /// @see #turnRadarLeftRadians(double)
        /// @see #turnRadarRight(double)
        /// @see #turnRadarRightRadians(double)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
        public void turnRightRadians(double radians)
        {
            if (peer != null)
            {
                peer.turnBody(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Returns the direction that the robot's gun is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///
        /// @return the direction that the robot's gun is facing, in radians.
        /// @see #getGunHeadingDegrees()
        /// @see #getHeadingRadians()
        /// @see #getRadarHeadingRadians()
        /// </summary>
        public double getGunHeadingRadians()
        {
            if (peer != null)
            {
                return peer.getGunHeading();
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Returns the direction that the robot's radar is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///
        /// @return the direction that the robot's radar is facing, in radians.
        /// @see #getRadarHeadingDegrees()
        /// @see #getHeadingRadians()
        /// @see #getGunHeadingRadians()
        /// </summary>
        public double getRadarHeadingRadians()
        {
            if (peer != null)
            {
                return peer.getRadarHeading();
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Sets the robot's gun to turn left by radians when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's gun is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the gun to turn 180 degrees to the left
        ///   setTurnGunLeftRadians(Math.PI);
        /// <p/>
        ///   // Set the gun to turn 90 degrees to the right instead of left
        ///   // (overrides the previous order)
        ///   setTurnGunLeftRadians(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnGunLeftRadians()
        ///   execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's gun to the left.
        ///                If {@code radians} > 0 the robot's gun is set to turn left.
        ///                If {@code radians} < 0 the robot's gun is set to turn right.
        ///                If {@code radians} = 0 the robot's gun is set to stop turning.
        /// @see AdvancedRobot#setTurnGunLeft(double) setTurnGunLeft(double)
        /// @see #turnGunLeft(double)
        /// @see #turnGunLeftRadians(double)
        /// @see #turnGunRight(double)
        /// @see #turnGunRightRadians(double)
        /// @see AdvancedRobot#setTurnGunRight(double) setTurnGunRight(double)
        /// @see AdvancedRobot#setTurnGunRightRadians(double) setTurnGunRightRadians(double)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
        public void setTurnGunLeftRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer)peer).setTurnGun(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's gun to turn right by radians when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's gun is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the gun to turn 180 degrees to the right
        ///   setTurnGunRightRadians(Math.PI);
        /// <p/>
        ///   // Set the gun to turn 90 degrees to the left instead of right
        ///   // (overrides the previous order)
        ///   setTurnGunRightRadians(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnGunRightRadians()
        ///   execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's gun to the right.
        ///                If {@code radians} > 0 the robot's gun is set to turn left.
        ///                If {@code radians} < 0 the robot's gun is set to turn right.
        ///                If {@code radians} = 0 the robot's gun is set to stop turning.
        /// @see AdvancedRobot#setTurnGunRight(double) setTurnGunRight(double)
        /// @see #turnGunRight(double)
        /// @see #turnGunRightRadians(double)
        /// @see #turnGunLeft(double)
        /// @see #turnGunLeftRadians(double)
        /// @see AdvancedRobot#setTurnGunLeft(double) setTurnGunLeft(double)
        /// @see AdvancedRobot#setTurnGunLeftRadians(double) setTurnGunLeftRadians(double)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
        public void setTurnGunRightRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer)peer).setTurnGun(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's radar to turn left by radians when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's radar is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the radar to turn 180 degrees to the left
        ///   setTurnRadarLeftRadians(Math.PI);
        /// <p/>
        ///   // Set the radar to turn 90 degrees to the right instead of left
        ///   // (overrides the previous order)
        ///   setTurnRadarLeftRadians(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnRadarLeftRadians()
        ///   execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's radar to the left.
        ///                If {@code radians} > 0 the robot's radar is set to turn left.
        ///                If {@code radians} < 0 the robot's radar is set to turn right.
        ///                If {@code radians} = 0 the robot's radar is set to stop turning.
        /// @see AdvancedRobot#setTurnRadarLeft(double) setTurnRadarLeft(double)
        /// @see #turnRadarLeft(double)
        /// @see #turnRadarLeftRadians(double)
        /// @see #turnRadarRight(double)
        /// @see #turnRadarRightRadians(double)
        /// @see AdvancedRobot#setTurnRadarRight(double) setTurnRadarRight(double)
        /// @see AdvancedRobot#setTurnRadarRightRadians(double) setTurnRadarRightRadians(double)
        /// @see #setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool)
        /// </summary>
        public void setTurnRadarLeftRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer)peer).setTurnRadar(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Sets the robot's radar to turn right by radians when the next execution
        /// takes place.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// execute() or take an action that executes.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's radar is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Set the radar to turn 180 degrees to the right
        ///   setTurnRadarRightRadians(Math.PI);
        /// <p/>
        ///   // Set the radar to turn 90 degrees to the right instead of right
        ///   // (overrides the previous order)
        ///   setTurnRadarRightRadians(-Math.PI / 2);
        /// <p/>
        ///   ...
        ///   // Executes the last setTurnRadarRightRadians()
        ///   execute();
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's radar to the right.
        ///                If {@code radians} > 0 the robot's radar is set to turn left.
        ///                If {@code radians} < 0 the robot's radar is set to turn right.
        ///                If {@code radians} = 0 the robot's radar is set to stop turning.
        /// @see AdvancedRobot#setTurnRadarRight(double) setTurnRadarRight(double)
        /// @see #turnRadarRight(double)
        /// @see #turnRadarRightRadians(double)
        /// @see #turnRadarLeft(double)
        /// @see #turnRadarLeftRadians(double)
        /// @see AdvancedRobot#setTurnRadarLeft(double) setTurnRadarLeft(double)
        /// @see AdvancedRobot#setTurnRadarLeftRadians(double) setTurnRadarLeftRadians(double)
        /// @see #setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool)
        /// </summary>
        public void setTurnRadarRightRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer)peer).setTurnRadar(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Immediately turns the robot's gun to the left by radians.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the gun's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's gun is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Turn the robot's gun 180 degrees to the left
        ///   turnGunLeftRadians(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's gun 90 degrees to the right
        ///   turnGunLeftRadians(-Math.PI / 2);
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's gun to the left.
        ///                If {@code radians} > 0 the robot's gun will turn left.
        ///                If {@code radians} < 0 the robot's gun will turn right.
        ///                If {@code radians} = 0 the robot's gun will not turn, but execute.
        /// @see #turnGunLeft(double)
        /// @see #turnGunRight(double)
        /// @see #turnGunRightRadians(double)
        /// @see #turnLeft(double)
        /// @see #turnLeftRadians(double)
        /// @see #turnRight(double)
        /// @see #turnRightRadians(double)
        /// @see #turnRadarLeft(double)
        /// @see #turnRadarLeftRadians(double)
        /// @see #turnRadarRight(double)
        /// @see #turnRadarRightRadians(double)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
        public void turnGunLeftRadians(double radians)
        {
            if (peer != null)
            {
                peer.turnGun(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Immediately turns the robot's gun to the right by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the gun's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's gun is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Turn the robot's gun 180 degrees to the right
        ///   turnGunRightRadians(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's gun 90 degrees to the left
        ///   turnGunRightRadians(-Math.PI / 2);
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's gun to the right.
        ///                If {@code radians} > 0 the robot's gun will turn right.
        ///                If {@code radians} < 0 the robot's gun will turn left.
        ///                If {@code radians} = 0 the robot's gun will not turn, but execute.
        /// @see #turnGunRight(double)
        /// @see #turnGunLeft(double)
        /// @see #turnGunLeftRadians(double)
        /// @see #turnLeft(double)
        /// @see #turnLeftRadians(double)
        /// @see #turnRight(double)
        /// @see #turnRightRadians(double)
        /// @see #turnRadarLeft(double)
        /// @see #turnRadarLeftRadians(double)
        /// @see #turnRadarRight(double)
        /// @see #turnRadarRightRadians(double)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
        public void turnGunRightRadians(double radians)
        {
            if (peer != null)
            {
                peer.turnGun(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Immediately turns the robot's radar to the left by radians.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the radar's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's radar is set to turn right
        /// instead of left.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Turn the robot's radar 180 degrees to the left
        ///   turnRadarLeftRadians(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's radar 90 degrees to the right
        ///   turnRadarLeftRadians(-Math.PI / 2);
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's radar to the left.
        ///                If {@code radians} > 0 the robot's radar will turn left.
        ///                If {@code radians} < 0 the robot's radar will turn right.
        ///                If {@code radians} = 0 the robot's radar will not turn, but execute.
        /// @see #turnRadarLeft(double)
        /// @see #turnRadarRight(double)
        /// @see #turnGunRightRadians(double)
        /// @see #turnLeft(double)
        /// @see #turnLeftRadians(double)
        /// @see #turnRight(double)
        /// @see #turnRightRadians(double)
        /// @see #turnGunLeft(double)
        /// @see #turnGunLeftRadians(double)
        /// @see #turnGunRight(double)
        /// @see #turnGunRightRadians(double)
        /// @see #setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool)
        /// </summary>
        public void turnRadarLeftRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer)peer).turnRadar(-radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Immediately turns the robot's radar to the right by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the radar's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot's radar is set to turn left
        /// instead of right.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Turn the robot's radar 180 degrees to the right
        ///   turnRadarRightRadians(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's radar 90 degrees to the left
        ///   turnRadarRightRadians(-Math.PI / 2);
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's radar to the right.
        ///                If {@code radians} > 0 the robot's radar will turn right.
        ///                If {@code radians} < 0 the robot's radar will turn left.
        ///                If {@code radians} = 0 the robot's radar will not turn, but execute.
        /// @see #turnRadarRight(double)
        /// @see #turnRadarLeft(double)
        /// @see #turnGunLeftRadians(double)
        /// @see #turnLeft(double)
        /// @see #turnLeftRadians(double)
        /// @see #turnRight(double)
        /// @see #turnRightRadians(double)
        /// @see #turnGunLeft(double)
        /// @see #turnGunLeftRadians(double)
        /// @see #turnGunRight(double)
        /// @see #turnGunRightRadians(double)
        /// @see #setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool)
        /// </summary>
        public void turnRadarRightRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer)peer).turnRadar(radians);
            }
            else
            {
                uninitializedException();
            }
        }

        /// <summary>
        /// Returns the angle remaining in the gun's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the gun is currently turning to the right. Negative values
        /// means that the gun is currently turning to the left.
        ///
        /// @return the angle remaining in the gun's turn, in radians
        /// @see AdvancedRobot#getGunTurnRemaining()
        /// @see AdvancedRobot#getTurnRemaining() getTurnRemaining()
        /// @see #getTurnRemainingRadians()
        /// @see AdvancedRobot#getRadarTurnRemaining() getRadarTurnRemaining()
        /// @see #getRadarTurnRemainingRadians()
        /// </summary>
        public double getGunTurnRemainingRadians()
        {
            if (peer != null)
            {
                return peer.getGunTurnRemaining();
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Returns the angle remaining in the radar's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the radar is currently turning to the right. Negative values
        /// means that the radar is currently turning to the left.
        ///
        /// @return the angle remaining in the radar's turn, in radians
        /// @see AdvancedRobot#getRadarTurnRemaining()
        /// @see AdvancedRobot#getTurnRemaining() getTurnRemaining()
        /// @see #getTurnRemainingRadians()
        /// @see AdvancedRobot#getGunTurnRemaining() getGunTurnRemaining()
        /// @see #getGunTurnRemainingRadians()
        /// </summary>
        public double getRadarTurnRemainingRadians()
        {
            if (peer != null)
            {
                return peer.getRadarTurnRemaining();
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Returns the angle remaining in the robot's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the robot is currently turning to the right. Negative values
        /// means that the robot is currently turning to the left.
        ///
        /// @return the angle remaining in the robot's turn, in radians
        /// @see AdvancedRobot#getTurnRemaining()
        /// @see AdvancedRobot#getGunTurnRemaining() getGunTurnRemaining()
        /// @see #getGunTurnRemainingRadians()
        /// @see AdvancedRobot#getRadarTurnRemaining() getRadarTurnRemaining()
        /// @see #getRadarTurnRemainingRadians()
        /// </summary>
        public double getTurnRemainingRadians()
        {
            if (peer != null)
            {
                return peer.getBodyTurnRemaining();
            }
            uninitializedException();
            return 0; // never called
        }

        /// <summary>
        /// Do not call this method!
        /// <p/>
        /// {@inheritDoc}
        /// </summary>
        public IAdvancedEvents getAdvancedEventListener()
        {
            return this; // this robot is listening
        }
    }
}
//happy