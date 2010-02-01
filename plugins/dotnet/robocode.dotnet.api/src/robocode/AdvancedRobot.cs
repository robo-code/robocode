#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System;
using System.Collections.Generic;
using System.IO;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
    /// <summary>
    ///   A more advanced type of robot than Robot that allows non-blocking calls,
    ///   custom events, and writes to the filesystem.
    ///   <p />
    ///   If you have not already, you should create a <see cref="Robot"/> first.
    ///   <see href="http://robocode.sourceforge.net" />
    ///   <see href="http://robocode.sourceforge.net/myfirstrobot/MyFirstRobot.html">
    ///     Building your first robot
    ///   </see>
    ///   <see cref="JuniorRobot" />
    ///   <see cref="Robot" />
    ///   <see cref="TeamRobot" />
    ///   <see cref="IDroid" />
    /// </summary>
    public abstract class AdvancedRobot : Robot, IAdvancedRobot, IAdvancedEvents
    {
        ///<summary>
        ///  Returns the distance remaining in the robot's current move measured in
        ///  pixels.
        ///  <p />
        ///  This call returns both positive and negative values. Positive values
        ///  means that the robot is currently moving forwards. Negative values means
        ///  that the robot is currently moving backwards. If the returned value is 0,
        ///  the robot currently stands still.
        ///  <seealso cref="TurnRemaining" />
        ///  <seealso cref="TurnRemainingRadians" />
        ///  <seealso cref="GunTurnRemaining" />
        ///  <seealso cref="GunTurnRemainingRadians" />
        ///  <seealso cref="RadarTurnRemaining" />
        ///  <seealso cref="RadarTurnRemainingRadians" />
        ///</summary>
        public double DistanceRemaining
        {
            get
            {
                if (peer != null)
                {
                    return peer.getDistanceRemaining();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>
        ///  Returns the angle remaining in the robots's turn, in degrees.
        ///  <p />
        ///  This call returns both positive and negative values. Positive values
        ///  means that the robot is currently turning to the right. Negative values
        ///  means that the robot is currently turning to the left. If the returned
        ///  value is 0, the robot is currently not turning.
        ///  <seealso cref="TurnRemainingRadians" />
        ///  <seealso cref="DistanceRemaining" />
        ///  <seealso cref="GunTurnRemaining" />
        ///  <seealso cref="GunTurnRemainingRadians" />
        ///  <seealso cref="RadarTurnRemaining" />
        ///  <seealso cref="RadarTurnRemainingRadians" />
        ///</summary>
        public double TurnRemaining
        {
            get
            {
                if (peer != null)
                {
                    return Utils.ToDegrees(peer.getBodyTurnRemaining());
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>
        ///  Returns the angle remaining in the gun's turn, in degrees.
        ///  <p />
        ///  This call returns both positive and negative values. Positive values
        ///  means that the gun is currently turning to the right. Negative values
        ///  means that the gun is currently turning to the left. If the returned
        ///  value is 0, the gun is currently not turning.
        ///  <seealso cref="GunTurnRemainingRadians" />
        ///  <seealso cref="DistanceRemaining" />
        ///  <seealso cref="TurnRemaining" />
        ///  <seealso cref="TurnRemainingRadians" />
        ///  <seealso cref="RadarTurnRemaining" />
        ///  <seealso cref="RadarTurnRemainingRadians" />
        ///</summary>
        public double GunTurnRemaining
        {
            get
            {
                if (peer != null)
                {
                    return Utils.ToDegrees(peer.getGunTurnRemaining());
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>
        ///  Returns the angle remaining in the radar's turn, in degrees.
        ///  <p />
        ///  This call returns both positive and negative values. Positive values
        ///  means that the radar is currently turning to the right. Negative values
        ///  means that the radar is currently turning to the left. If the returned
        ///  value is 0, the radar is currently not turning.
        ///  <seealso cref="RadarTurnRemainingRadians" />
        ///  <seealso cref="DistanceRemaining" />
        ///  <seealso cref="GunTurnRemaining" />
        ///  <seealso cref="GunTurnRemainingRadians" />
        ///  <seealso cref="RadarTurnRemaining" />
        ///  <seealso cref="RadarTurnRemainingRadians" />
        ///</summary>
        public double RadarTurnRemaining
        {
            get
            {
                if (peer != null)
                {
                    return Utils.ToDegrees(peer.getRadarTurnRemaining());
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Sets the robot to move Ahead (forward) by distance measured in 
        ///   pixels when the next execution takes place.
        ///   <br />
        ///   This call returns immediately, and will not Execute until you 
        ///   call
        ///   <see cref="Execute" />
        ///   or take an action that executes.
        ///   <br />
        ///   Note that both positive and negative values can be given as input, 
        ///   where positive values means that the robot is set to move Ahead, and negative 
        ///   values means that the robot is set to move Back. If 0 is given as input, the 
        ///   robot will Stop its movement, but will have to decelerate till it stands still, 
        ///   and will thus not be able to Stop its movement immediately, but eventually.
        /// </summary>
        /// <param name="distance">
        ///   distance the distance to move measured in pixels.
        ///   <br />
        ///   If
        ///   <code>distance</code>
        ///   &gt; 0 the robot is set to move Ahead.
        ///   <br />
        ///   If
        ///   <code>distance</code>
        ///   &lt; 0 the robot is set to move Back.
        ///   <br />
        ///   If
        ///   <code>distance</code>
        ///   = 0 the robot is set to Stop its movement.
        /// </param>
        /// <example>
        ///   <code>
        ///     // Set the robot to move 50 pixels Ahead
        ///     SetAhead(50);
        ///     <br />
        ///     // Set the robot to move 100 pixels Back
        ///     // (overrides the previous order)
        ///     SetAhead(-100);
        ///     <br />
        ///     ...
        ///     // Executes the last SetAhead()
        ///     Execute();
        ///   </code>
        /// </example>
        /// <seealso cref="Robot.Ahead" />
        /// <seealso cref="Robot.Back" />
        /// <seealso cref="SetBack" />
        public void SetAhead(double distance)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setMove(distance);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Sets the robot to move Back by distance measured in pixels when the next
        ///  execution takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  <see cref="Execute()" /> or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input, where
        ///  positive values means that the robot is set to move Back, and negative
        ///  values means that the robot is set to move Ahead. If 0 is given as input,
        ///  the robot will Stop its movement, but will have to decelerate
        ///  till it stands still, and will thus not be able to Stop its movement
        ///  immediately, but eventually.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the robot to move 50 pixels Back
        ///    SetBack(50);
        ///    <p />
        ///    // Set the robot to move 100 pixels Ahead
        ///    // (overrides the previous order)
        ///    SetBack(-100);
        ///    <p />
        ///    ...
        ///    // Executes the last SetBack()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="Robot.Back(double)"/>
        ///  <seealso cref="Robot.Ahead(double)"/>
        ///  <seealso cref="SetAhead(double)"/>
        ///</summary>
        ///  <param name="distance"> the distance to move measured in pixels.
        ///  If distance &gt; 0 the robot is set to move Back.
        ///  If distance &lt; 0 the robot is set to move Ahead.
        ///  If distance = 0 the robot is set to Stop its movement.</param>
        public void SetBack(double distance)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setMove(-distance);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Sets the robot's body to turn left by degrees when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's body is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the robot to turn 180 degrees to the left
        ///    SetTurnLeft(180);
        ///    <p />
        ///    // Set the robot to turn 90 degrees to the right instead of left
        ///    // (overrides the previous order)
        ///    SetTurnLeft(-90);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnLeft()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="SetTurnLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="SetTurnRight(double)"/>
        ///  <seealso cref="SetTurnRightRadians(double)"/>
        ///</summary>
        ///  <param name="degrees"> the amount of degrees to turn the robot's body to the left.
        ///  If degrees &gt; 0 the robot is set to turn left.
        ///  If degrees &lt; 0 the robot is set to turn right.
        ///  If degrees = 0 the robot is set to Stop turning.</param>
        public void SetTurnLeft(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnBody(-Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Sets the robot's body to turn right by degrees when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's body is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the robot to turn 180 degrees to the right
        ///    SetTurnRight(180);
        ///    <p />
        ///    // Set the robot to turn 90 degrees to the left instead of right
        ///    // (overrides the previous order)
        ///    SetTurnRight(-90);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnRight()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="SetTurnRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="SetTurnLeft(double)"/>
        ///  <seealso cref="SetTurnLeftRadians(double)"/>
        ///</summary>
        ///  <param name="degrees"> the amount of degrees to turn the robot's body to the right.
        ///  If degrees &gt; 0 the robot is set to turn right.
        ///  If degrees &lt; 0 the robot is set to turn left.
        ///  If degrees = 0 the robot is set to Stop turning.</param>
        public void SetTurnRight(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnBody(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Sets the gun to Fire a bullet when the next execution takes place.
        ///  The bullet will travel in the direction the gun is pointing.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  The specified bullet power is an amount of energy that will be taken from
        ///  the robot's energy. Hence, the more power you want to spend on the
        ///  bullet, the more energy is taken from your robot.
        ///  <p />
        ///  The bullet will do (4 * power) damage if it hits another robot. If power
        ///  is greater than 1, it will do an additional 2 * (power - 1) damage.
        ///  You will get (3 * power) Back if you hit the other robot. You can call
        ///  Rules.GetBulletDamage(double)} for getting the damage that a
        ///  bullet with a specific bullet power will do.
        ///  <p />
        ///  The specified bullet power should be between
        ///  <see cref="Rules.MIN_BULLET_POWER"/> and <see cref="Rules.MAX_BULLET_POWER"/>.
        ///  <p />
        ///  Note that the gun cannot Fire if the gun is overheated, meaning that
        ///  <see cref="Robot.GunHeat"/> returns a value &gt; 0.
        ///  <p />
        ///  An evnt is generated when the bullet hits a robot, wall, or another
        ///  bullet.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Fire a bullet with maximum power if the gun is ready
        ///    if (GetGunHeat() == 0) {
        ///    SetFire(Rules.MAX_BULLET_POWER);
        ///    }
        ///    ...
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="SetFireBullet(double)"/>
        ///  <seealso cref="Robot.Fire(double)"/>
        ///  <seealso cref="Robot.FireBullet(double)"/>
        ///  <seealso cref="Robot.GunHeat"/>
        ///  <seealso cref="Robot.GunCoolingRate"/>
        ///  <seealso cref="Robot.OnBulletHit(BulletHitEvent)"/>
        ///  <seealso cref="Robot.OnBulletHitBullet(BulletHitBulletEvent)"/>
        ///  <seealso cref="Robot.OnBulletMissed(BulletMissedEvent)"/>
        ///</summary>
        ///  <param name="power">the amount of energy given to the bullet, and subtracted from the robot's energy.</param>
        public void SetFire(double power)
        {
            if (peer != null)
            {
                peer.setFire(power);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Sets the gun to Fire a bullet when the next execution takes place.
        ///  The bullet will travel in the direction the gun is pointing.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  The specified bullet power is an amount of energy that will be taken from
        ///  the robot's energy. Hence, the more power you want to spend on the
        ///  bullet, the more energy is taken from your robot.
        ///  <p />
        ///  The bullet will do (4 * power) damage if it hits another robot. If power
        ///  is greater than 1, it will do an additional 2 * (power - 1) damage.
        ///  You will get (3 * power) Back if you hit the other robot. You can call
        ///  <see cref="Rules.GetBulletDamage(double)"/> for getting the damage that a
        ///  bullet with a specific bullet power will do.
        ///  <p />
        ///  The specified bullet power should be between
        ///  <see cref="Rules.MIN_BULLET_POWER"/> and <see cref="Rules.MAX_BULLET_POWER"/>.
        ///  <p />
        ///  Note that the gun cannot Fire if the gun is overheated, meaning that
        ///  <see cref="Robot.GunHeat"/> returns a value &gt; 0.
        ///  <p />
        ///  A evnt is generated when the bullet hits a robot
        ///  (<see cref="BulletHitEvent"/>), wall (<see cref="BulletMissedEvent"/>), or another
        ///  bullet (<see cref="BulletHitBulletEvent"/>).
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    Bullet bullet = null;
        ///    <p />
        ///    // Fire a bullet with maximum power if the gun is ready
        ///    if (GetGunHeat() == 0) {
        ///    bullet = SetFireBullet(Rules.MAX_BULLET_POWER);
        ///    }
        ///    ...
        ///    Execute();
        ///    ...
        ///    // Get the velocity of the bullet
        ///    if (bullet != null) {
        ///    double bulletVelocity = bullet.getVelocity();
        ///    }
        ///  </pre>
        ///  <seealso cref="SetFire(double)"/>
        ///  <seealso cref="Bullet"/>
        ///  <seealso cref="Robot.Fire(double)"/>
        ///  <seealso cref="Robot.FireBullet(double)"/>
        ///  <seealso cref="Robot.GunHeat()"/>
        ///  <seealso cref="Robot.GunCoolingRate()"/>
        ///  <seealso cref="Robot.OnBulletHit(BulletHitEvent)"/>
        ///  <seealso cref="Robot.OnBulletHitBullet(BulletHitBulletEvent)"/>
        ///  <seealso cref="Robot.OnBulletMissed(BulletMissedEvent)"/>
        ///</summary>
        ///  <param name="power"> the amount of energy given to the bullet, and subtracted
        ///  from the robot's energy.</param>
        ///  Return a <see cref="Bullet"/> that contains information about the bullet if it
        ///  was actually fired, which can be used for tracking the bullet after it
        ///  has been fired. If the bullet was not fired, null is returned.
        public Bullet SetFireBullet(double power)
        {
            if (peer != null)
            {
                return peer.setFire(power);
            }
            UninitializedException();
            return null;
        }

        ///<summary>
        ///  Registers a custom evnt to be called when a condition is met.
        ///  When you are finished with your condition or just want to remove it you
        ///  must call <see cref="RemoveCustomEvent(Condition)"/>.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Create the condition for our custom event
        ///    Condition triggerHitCondition = new Condition("triggerhit") {
        ///    public bool Test() {
        ///    return (getEnergy() &lt;= trigger);
        ///    };
        ///    }
        ///    <p />
        ///    // Add our custom evnt based on our condition
        ///    <b>
        ///      AddCustomEvent(triggerHitCondition);
        ///    </b>
        ///  </pre>
        ///  <seealso cref="Condition"/>
        ///  <seealso cref="RemoveCustomEvent(Condition)"/>
        ///</summary>
        ///  <param name="condition"> the condition that must be met.
        ///  throws ArgumentException if the condition parameter has been set to null.</param>
        public void AddCustomEvent(Condition condition)
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
                UninitializedException();
            }
        }

        /// <summary>
        /// Same as <see cref="AddCustomEvent(Condition)"/>, but alows to define condition as anonymous method.
        /// </summary>
        public void AddCustomEvent(string name, int priority, ConditionTest test)
        {
            AddCustomEvent(new Condition(name, priority, test));
        }


        
        ///<summary>
        ///  Removes a custom evnt that was previously added by calling
        ///  <see cref="AddCustomEvent(Condition)"/>.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Create the condition for our custom event
        ///    Condition triggerHitCondition = new Condition("triggerhit") {
        ///    public bool Test() {
        ///    return (getEnergy() &lt;= trigger);
        ///    };
        ///    }
        ///    <p />
        ///    // Add our custom evnt based on our condition
        ///    AddCustomEvent(triggerHitCondition);
        ///    ...
        ///    <i>
        ///      do something with your robot
        ///    </i>
        ///    ...
        ///    // Remove the custom evnt based on our condition
        ///    <b>
        ///      RemoveCustomEvent(triggerHitCondition);
        ///    </b>
        ///  </pre>
        ///  <seealso cref="Condition"/>
        ///  <seealso cref="AddCustomEvent(Condition)"/>
        ///</summary>
        ///  <param name="condition">the condition that was previous added and that must be removed now.</param>
        public void RemoveCustomEvent(Condition condition)
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
                UninitializedException();
            }
        }

        ///<summary>
        ///  Clears Out any pending events in the robot's evnt queue immediately.
        ///
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public void ClearAllEvents()
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).clearAllEvents();
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Executes any pending actions, or continues executing actions that are
        ///   in process. This call returns after the actions have been started.
        ///   <p />
        ///   Note that advanced robots
        ///   <em>must</em>
        ///   call this function in order to
        ///   Execute pending set* calls like e.g. <see cref="SetAhead(double)"/>,
        ///   <see cref="SetFire(double)"/>, <see cref="SetTurnLeft(double)"/> etc. Otherwise,
        ///   these calls will never get executed.
        ///   <p />
        ///   In this example the robot will move while turning:
        ///   <pre>
        ///     SetTurnRight(90);
        ///     SetAhead(100);
        ///     Execute();
        ///     <p />
        ///     while (getDistanceRemaining() > 0 &amp;&amp; getTurnRemaining() > 0) {
        ///     Execute();
        ///     }
        ///   </pre>
        /// </summary>
        public virtual void Execute()
        {
            if (peer != null)
            {
                peer.execute();
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Returns a vector containing all events currently in the robot's queue.
        ///  You might, for example, call this while processing another evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (Event evnt : GetAllEvents()) {
        ///    if (event instanceof HitRobotEvent) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    } else if (event instanceof HitByBulletEvent) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///    }
        ///  </pre>
        ///  <seealso cref="Event"/>
        ///  <seealso cref="ClearAllEvents()"/>
        ///  <seealso cref="GetStatusEvents()"/>
        ///  <seealso cref="GetScannedRobotEvents()"/>
        ///  <seealso cref="GetBulletHitEvents()"/>
        ///  <seealso cref="GetBulletMissedEvents()"/>
        ///  <seealso cref="GetBulletHitBulletEvents()"/>
        ///  <seealso cref="GetRobotDeathEvents()"/>
        ///</summary>
        public IList<Event> GetAllEvents()
        {
            if (peer != null)
            {
                return new List<Event>(((IAdvancedRobotPeer) peer).getAllEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>
        ///  Returns a vector containing all BulletHitBulletEvents currently in the
        ///  robot's queue. You might, for example, call this while processing another
        ///  evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (BulletHitBulletEvent evnt : GetBulletHitBulletEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  robot's queue
        ///  <seealso cref="Robot.OnBulletHitBullet(BulletHitBulletEvent)"/>
        ///  <seealso cref="BulletHitBulletEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<BulletHitBulletEvent> GetBulletHitBulletEvents()
        {
            if (peer != null)
            {
                return new List<BulletHitBulletEvent>(((IAdvancedRobotPeer) peer).getBulletHitBulletEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>
        ///  Returns a vector containing all BulletHitEvents currently in the robot's
        ///  queue. You might, for example, call this while processing another evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (BulletHitEvent event: GetBulletHitEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  <seealso cref="Robot.OnBulletHit(BulletHitEvent)"/>
        ///  <seealso cref="BulletHitEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<BulletHitEvent> GetBulletHitEvents()
        {
            if (peer != null)
            {
                return new List<BulletHitEvent>(((IAdvancedRobotPeer) peer).getBulletHitEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>
        ///  Returns a vector containing all BulletMissedEvents currently in the
        ///  robot's queue. You might, for example, call this while processing another
        ///  evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (BulletMissedEvent evnt : GetBulletMissedEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  <seealso cref="Robot.OnBulletMissed(BulletMissedEvent)"/>
        ///  <seealso cref="BulletMissedEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<BulletMissedEvent> GetBulletMissedEvents()
        {
            if (peer != null)
            {
                return new List<BulletMissedEvent>(((IAdvancedRobotPeer) peer).getBulletMissedEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>
        ///  Returns a file representing a data directory for the robot.
        ///  <p />
        ///  The system will automatically create the directory for you, so you do not
        ///  need to create it by yourself.
        ///  <seealso cref="GetDataFile(string)"/>
        ///</summary>
        public string GetDataDirectory()
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).getDataDirectory();
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>
        ///  Returns a file in your data directory that you can write to.
        ///  <p />
        ///  The system will automatically create the directory for you, so you do not
        ///  need to create it by yourself.
        ///  <p />
        ///  Please notice that the max. size of your data file is set to 200000 bytes
        ///  (~195 KB).
        ///  <p />
        ///  See the sample.SittingDuck to see an example of how to use this
        ///  method.
        ///  <seealso cref="GetDataDirectory()"/>
        ///</summary>
        ///  <param name="filename"> the file name of the data file for your robot</param>
        public Stream GetDataFile(string filename)
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).getDataFile(filename);
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>
        ///  Returns the data quota available in your data directory, i.e. the amount
        ///  of bytes left in the data directory for the robot.
        ///  <seealso cref="GetDataDirectory()"/>
        ///  <seealso cref="GetDataFile(string)"/>
        ///</summary>
        public long DataQuotaAvailable
        {
            get
            {
                if (peer != null)
                {
                    return ((IAdvancedRobotPeer) peer).getDataQuotaAvailable();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>        
///  Returns the current priority of a class of events.
        ///  An evnt priority is a value from 0 - 99. The higher value, the higher
        ///  priority.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    int myHitRobotPriority = GetEventPriority("HitRobotEvent");
        ///  </pre>
        ///  <p />
        ///  The default priorities are, from highest to lowest:
        ///  <pre>
        ///    <see cref="BattleEndedEvent"/>:     100 (reserved)
        ///    <see cref="WinEvent"/>:             100 (reserved)
        ///    <see cref="SkippedTurnEvent"/>:     100 (reserved)
        ///    <see cref="StatusEvent"/>:           99
        ///    Key and mouse events:  98
        ///    <see cref="CustomEvent"/>:           80 (default value)
        ///    <see cref="MessageEvent"/>:          75
        ///    <see cref="RobotDeathEvent"/>:       70
        ///    <see cref="BulletMissedEvent"/>:     60
        ///    <see cref="BulletHitBulletEvent"/>:  55
        ///    <see cref="BulletHitEvent"/>:        50
        ///    <see cref="HitByBulletEvent"/>:      40
        ///    <see cref="HitWallEvent"/>:          30
        ///    <see cref="HitRobotEvent"/>:         20
        ///    <see cref="ScannedRobotEvent"/>:     10
        ///    <see cref="PaintEvent"/>:             5
        ///    <see cref="DeathEvent"/>:            -1 (reserved)
        ///  </pre>
        ///  <seealso cref="SetEventPriority(string, int)"/>
        ///</summary>
        ///  <param name="eventClass"> the name of the evnt class (string)</param>
        public int GetEventPriority(string eventClass)
        {
            if (peer != null)
            {
                return ((IAdvancedRobotPeer) peer).getEventPriority(eventClass);
            }
            UninitializedException();
            return 0; // never called
        }

        ///<summary>        
        ///  Returns a vector containing all HitByBulletEvents currently in the
        ///  robot's queue. You might, for example, call this while processing
        ///  another evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (HitByBulletEvent evnt : GetHitByBulletEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  <seealso cref="Robot.OnHitByBullet(HitByBulletEvent)"/>
        ///  <seealso cref="HitByBulletEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<HitByBulletEvent> GetHitByBulletEvents()
        {
            if (peer != null)
            {
                return new List<HitByBulletEvent>(((IAdvancedRobotPeer) peer).getHitByBulletEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>        
        ///  Returns a vector containing all HitRobotEvents currently in the robot's
        ///  queue. You might, for example, call this while processing another evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (HitRobotEvent evnt : GetHitRobotEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  <seealso cref="Robot.OnHitRobot(HitRobotEvent)"/>
        ///  <seealso cref="HitRobotEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<HitRobotEvent> GetHitRobotEvents()
        {
            if (peer != null)
            {
                return new List<HitRobotEvent>(((IAdvancedRobotPeer) peer).getHitRobotEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>        
        ///  Returns a vector containing all HitWallEvents currently in the robot's
        ///  queue. You might, for example, call this while processing another evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (HitWallEvent evnt : GetHitWallEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  <seealso cref="Robot.OnHitWall(HitWallEvent)"/>
        ///  <seealso cref="HitWallEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<HitWallEvent> GetHitWallEvents()
        {
            if (peer != null)
            {
                return new List<HitWallEvent>(((IAdvancedRobotPeer) peer).getHitWallEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>        
        ///  Returns a vector containing all RobotDeathEvents currently in the robot's
        ///  queue. You might, for example, call this while processing another evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (RobotDeathEvent evnt : GetRobotDeathEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  <seealso cref="Robot.OnRobotDeath(RobotDeathEvent)"/>
        ///  <seealso cref="RobotDeathEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<RobotDeathEvent> GetRobotDeathEvents()
        {
            if (peer != null)
            {
                return new List<RobotDeathEvent>(((IAdvancedRobotPeer) peer).getRobotDeathEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>        
        ///  Returns a vector containing all ScannedRobotEvents currently in the
        ///  robot's queue. You might, for example, call this while processing another
        ///  evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (ScannedRobotEvent evnt : GetScannedRobotEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  <seealso cref="Robot.OnScannedRobot(ScannedRobotEvent)"/>
        ///  <seealso cref="ScannedRobotEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<ScannedRobotEvent> GetScannedRobotEvents()
        {
            if (peer != null)
            {
                return new List<ScannedRobotEvent>(((IAdvancedRobotPeer) peer).getScannedRobotEvents());
            }
            UninitializedException();
            return null; // never called
        }

        ///<summary>        
        ///  Returns a vector containing all StatusEvents currently in the robot's
        ///  queue. You might, for example, call this while processing another evnt.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    for (StatusEvent evnt : GetStatusEvents()) {
        ///    <i>
        ///      // do something with the event
        ///    </i>
        ///    }
        ///  </pre>
        ///  <seealso cref="Robot.OnStatus(StatusEvent)"/>
        ///  <seealso cref="StatusEvent"/>
        ///  <seealso cref="GetAllEvents()"/>
        ///</summary>
        public IList<StatusEvent> GetStatusEvents()
        {
            if (peer != null)
            {
                return new List<StatusEvent>(((IAdvancedRobotPeer) peer).getStatusEvents());
            }
            UninitializedException();
            return null; // never called
        }

        /// <inheritdoc />
        public virtual void OnCustomEvent(CustomEvent evnt)
        {
        }

        ///<summary>        
        ///  Sets the priority of a class of events.
        ///  <p />
        ///  Events are sent to the onXXX handlers in order of priority.
        ///  Higher priority events can interrupt lower priority events.
        ///  For events with the same priority, newer events are always sent first.
        ///  Valid priorities are 0 - 99, where 100 is reserved and 80 is the default
        ///  priority.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    SetEventPriority("RobotDeathEvent", 15);
        ///  </pre>
        ///  <p />
        ///  The default priorities are, from highest to lowest:
        ///  <pre>
        ///    <see cref="WinEvent"/>:             100 (reserved)
        ///    <see cref="SkippedTurnEvent"/>:     100 (reserved)
        ///    <see cref="StatusEvent"/>:           99
        ///    <see cref="CustomEvent"/>:           80
        ///    <see cref="MessageEvent"/>:          75
        ///    <see cref="RobotDeathEvent"/>:       70
        ///    <see cref="BulletMissedEvent"/>:     60
        ///    <see cref="BulletHitBulletEvent"/>:  55
        ///    <see cref="BulletHitEvent"/>:        50
        ///    <see cref="HitByBulletEvent"/>:      40
        ///    <see cref="HitWallEvent"/>:          30
        ///    <see cref="HitRobotEvent"/>:         20
        ///    <see cref="ScannedRobotEvent"/>:     10
        ///    <see cref="PaintEvent"/>:             5
        ///    <see cref="DeathEvent"/>:            -1 (reserved)
        ///  </pre>
        ///  <p />
        ///  Note that you cannot change the priority for events with the special
        ///  priority value -1 or 100 (reserved) as these evnt are system events.
        ///  Also note that you cannot change the priority of CustomEvent.
        ///  Instead you must change the priority of the condition(s) for your custom
        ///  event(s).
        ///  <seealso cref="GetEventPriority(string)"/>
        ///</summary>
        ///  <param name="eventClass"> the name of the evnt class (string) to set the priority for</param>
        ///  <param name="priority"> the new priority for that evnt class</param>
        public void SetEventPriority(string eventClass, int priority)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setEventPriority(eventClass, priority);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Call this during an evnt handler to allow new events of the same
        ///  priority to restart the evnt handler.
        ///  <p />
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    public override void OnScannedRobot(ScannedRobotEvent e) {
        ///    Fire(1);
        ///    <b>
        ///      IsInterruptible = true;
        ///    </b>
        ///    Ahead(100); // If you see a robot while moving Ahead,
        ///    // this handler will start from the top
        ///    // Without IsInterruptible (true), we wouldn't
        ///    // receive Scan events at all!
        ///    // We'll only get here if we don't see a robot during the move.
        ///    Out.println("Ok, I can't see anyone");
        ///    }
        ///  </pre>
        ///  <seealso cref="SetEventPriority(string, int)"/>
        ///  <seealso cref="Robot.OnScannedRobot(ScannedRobotEvent)"/>
        ///</summary>
        public bool IsInterruptible
        {
            set
            {
                if (peer != null)
                {
                    ((IAdvancedRobotPeer) peer).setInterruptible(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        ///<summary>        
        ///  Sets the maximum turn rate of the robot measured in degrees if the robot
        ///  should turn slower than <see cref="Rules.MAX_TURN_RATE"/> (10 degress/turn).
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="SetTurnRight(double)"/>
        ///  <seealso cref="SetTurnLeft(double)"/>
        ///  <seealso cref="MaxVelocity"/>
        ///</summary>
        public double MaxTurnRate
        {
            set
            {
                if (peer != null)
                {
                    ((IAdvancedRobotPeer) peer).setMaxTurnRate(Utils.ToRadians(value));
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        ///<summary>        
        ///  Sets the maximum velocity of the robot measured in pixels/turn if the
        ///  robot should move slower than <see cref="Rules.MAX_VELOCITY"/> (8 pixels/turn).
        ///  <seealso cref="Robot.Ahead(double)"/>
        ///  <seealso cref="SetAhead(double)"/>
        ///  <seealso cref="Robot.Back(double)"/>
        ///  <seealso cref="SetBack(double)"/>
        ///  <seealso cref="MaxTurnRate"/>
        ///</summary>
        public double MaxVelocity
        {
            set
            {
                if (peer != null)
                {
                    ((IAdvancedRobotPeer) peer).setMaxVelocity(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        ///<summary>        
        ///  Sets the robot to Resume the movement stopped by <see cref="Robot.Stop()"/>
        ///  or <see cref="SetStop()"/>, if any.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  <see cref="Execute()"/> or take an action that executes.
        ///
        ///  <seealso cref="Robot.Resume()"/>
        ///  <seealso cref="Robot.Stop()"/>
        ///  <seealso cref="Robot.Stop(bool)"/>
        ///  <seealso cref="SetStop()"/>
        ///  <seealso cref="SetStop(bool)"/>
        ///  <seealso cref="Execute()"/>
        ///</summary>
        public void SetResume()
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setResume();
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  This call is identical to <see cref="Robot.Stop()"/>, but returns immediately, and
        ///  will not Execute until you call <see cref="Execute()"/> or take an action that
        ///  executes.
        ///  <p />
        ///  If there is already movement saved from a previous Stop, this will have
        ///  no effect.
        ///  <p />
        ///  This call is equivalent to calling SetStop(false);
        ///
        ///  <seealso cref="Robot.Stop()"/>
        ///  <seealso cref="Robot.Stop(bool)"/>
        ///  <seealso cref="Robot.Resume()"/>
        ///  <seealso cref="SetResume()"/>
        ///  <seealso cref="SetStop(bool)"/>
        ///  <seealso cref="Execute()"/>
        ///</summary>
        public void SetStop()
        {
            SetStop(false);
        }

        ///<summary>        
        ///  This call is identical to <see cref="Robot.Stop(bool)"/>, but
        ///  returns immediately, and will not Execute until you call
        ///  <see cref="Execute()"/> or take an action that executes.
        ///  <p />
        ///  If there is already movement saved from a previous Stop, you can
        ///  overwrite it by calling SetStop(true).
        ///
        ///  <seealso cref="Robot.Stop()"/>
        ///  <seealso cref="Robot.Stop(bool)"/>
        ///  <seealso cref="Robot.Resume()"/>
        ///  <seealso cref="SetResume()"/>
        ///  <seealso cref="SetStop()"/>
        ///  <seealso cref="Execute()"/>
        ///</summary>
        ///  <param name="overwrite"> true if the movement saved from a previous Stop
        ///  should be overwritten; false otherwise.</param>
        public void SetStop(bool overwrite)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setStop(overwrite);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Sets the robot's gun to turn left by degrees when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's gun is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the gun to turn 180 degrees to the left
        ///    SetTurnGunLeft(180);
        ///    <p />
        ///    // Set the gun to turn 90 degrees to the right instead of left
        ///    // (overrides the previous order)
        ///    SetTurnGunLeft(-90);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnGunLeft()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="SetTurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="SetTurnGunRight(double)"/>
        ///  <seealso cref="SetTurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustGunForRobotTurn"/>
        ///</summary>
        ///  <param name="degrees"> the amount of degrees to turn the robot's gun to the left.
        ///  If degrees &gt; 0 the robot's gun is set to turn left.
        ///  If degrees &lt; 0 the robot's gun is set to turn right.
        ///  If degrees = 0 the robot's gun is set to Stop turning.</param>
        public void SetTurnGunLeft(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnGun(-Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Sets the robot's gun to turn right by degrees when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's gun is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the gun to turn 180 degrees to the right
        ///    SetTurnGunRight(180);
        ///    <p />
        ///    // Set the gun to turn 90 degrees to the left instead of right
        ///    // (overrides the previous order)
        ///    SetTurnGunRight(-90);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnGunRight()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="SetTurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="SetTurnGunLeft(double)"/>
        ///  <seealso cref="SetTurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustGunForRobotTurn"/>
        ///</summary>
        ///  <param name="degrees"> the amount of degrees to turn the robot's gun to the right.
        ///  If degrees &gt; 0 the robot's gun is set to turn right.
        ///  If degrees &lt; 0 the robot's gun is set to turn left.
        ///  If degrees = 0 the robot's gun is set to Stop turning.</param>
        public void SetTurnGunRight(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnGun(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Sets the robot's radar to turn left by degrees when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's radar is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the radar to turn 180 degrees to the left
        ///    SetTurnRadarLeft(180);
        ///    <p />
        ///    // Set the radar to turn 90 degrees to the right instead of left
        ///    // (overrides the previous order)
        ///    SetTurnRadarLeft(-90);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnRadarLeft()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="SetTurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnRadarRightRadians(double)"/>
        ///  <seealso cref="SetTurnRadarRight(double)"/>
        ///  <seealso cref="SetTurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustRadarForRobotTurn"/>
        ///  <seealso cref="Robot.IsAdjustRadarForGunTurn"/>
        ///</summary>
        ///  <param name="degrees"> the amount of degrees to turn the robot's radar to the left.
        ///  If degrees &gt; 0 the robot's radar is set to turn left.
        ///  If degrees &lt; 0 the robot's radar is set to turn right.
        ///  If degrees = 0 the robot's radar is set to Stop turning.</param>
        public void SetTurnRadarLeft(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnRadar(-Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Sets the robot's radar to turn right by degrees when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's radar is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the radar to turn 180 degrees to the right
        ///    SetTurnRadarRight(180);
        ///    <p />
        ///    // Set the radar to turn 90 degrees to the right instead of right
        ///    // (overrides the previous order)
        ///    SetTurnRadarRight(-90);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnRadarRight()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="SetTurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnRadarLeftRadians(double)"/>
        ///  <seealso cref="SetTurnRadarLeft(double)"/>
        ///  <seealso cref="SetTurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustRadarForRobotTurn"/>
        ///  <seealso cref="Robot.IsAdjustRadarForGunTurn"/>
        ///</summary>
        ///  <param name="degrees"> the amount of degrees to turn the robot's radar to the right.
        ///  If degrees &gt; 0 the robot's radar is set to turn right.
        ///  If degrees &lt; 0 the robot's radar is set to turn left.
        ///  If degrees = 0 the robot's radar is set to Stop turning.</param>
        public void SetTurnRadarRight(double degrees)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnRadar(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Does not return until a condition is met, i.e. when a
        ///  <see cref="Condition.Test()"/> returns true.
        ///  <p />
        ///  This call executes immediately.
        ///  <p />
        ///  See the sample.Crazy robot for how this method can be used.
        ///  <seealso cref="Condition"/>
        ///  <seealso cref="Condition.Test()"/>
        ///</summary>
        ///  <param name="condition">the condition that must be met before this call returns</param>
        public void WaitFor(Condition condition)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).waitFor(condition);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  This method is called if your robot dies.
        ///  <p />
        ///  You should override it in your robot if you want to be informed of this
        ///  evnt. Actions will have no effect if called from this section. The
        ///  intent is to allow you to perform calculations or print something out
        ///  when the robot is killed.
        ///  <seealso cref="DeathEvent"/>
        ///  <seealso cref="Event"/>
        ///</summary>
        ///  <param name="evnt">the death event set by the game</param>
        public override void OnDeath(DeathEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnSkippedTurn(SkippedTurnEvent evnt)
        {
        }

        ///<summary>        
        ///  Returns the direction that the robot's body is facing, in radians.
        ///  The value returned will be between 0 and 2 * PI (is excluded).
        ///  <p />
        ///  Note that the heading in Robocode is like a compass, where 0 means North,
        ///  PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///  <seealso cref="Robot.Heading"/>
        ///  <seealso cref="GunHeadingRadians"/>
        ///  <seealso cref="RadarHeadingRadians"/>
        ///</summary>
        public double HeadingRadians
        {
            get
            {
                if (peer != null)
                {
                    return peer.getBodyHeading();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>        
        ///  Sets the robot's body to turn left by radians when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's body is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the robot to turn 180 degrees to the left
        ///    SetTurnLeftRadians(Math.PI);
        ///    <p />
        ///    // Set the robot to turn 90 degrees to the right instead of left
        ///    // (overrides the previous order)
        ///    SetTurnLeftRadians(-Math.PI / 2);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnLeftRadians()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="AdvancedRobot.SetTurnLeft(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnRight(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnRightRadians(double)"/>
        ///</summary>
        ///  <param name="radians"> the amount of radians to turn the robot's body to the left.
        ///  If radians &gt; 0 the robot is set to turn left.
        ///  If radians &lt; 0 the robot is set to turn right.
        ///  If radians = 0 the robot is set to Stop turning.</param>
        public void SetTurnLeftRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnBody(-radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Sets the robot's body to turn right by radians when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's body is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the robot to turn 180 degrees to the right
        ///    SetTurnRightRadians(Math.PI);
        ///    <p />
        ///    // Set the robot to turn 90 degrees to the left instead of right
        ///    // (overrides the previous order)
        ///    SetTurnRightRadians(-Math.PI / 2);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnRightRadians()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="AdvancedRobot.SetTurnRight(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnLeft(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnLeftRadians(double)"/>
        ///</summary>
        ///  <param name="radians"> the amount of radians to turn the robot's body to the right.
        ///  If radians &gt; 0 the robot is set to turn right.
        ///  If radians &lt; 0 the robot is set to turn left.
        ///  If radians = 0 the robot is set to Stop turning.</param>
        public void SetTurnRightRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnBody(radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Immediately turns the robot's body to the left by radians.
        ///  <p />
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the robot's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's body is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Turn the robot 180 degrees to the left
        ///    TurnLeftRadians(Math.PI);
        ///    <p />
        ///    // Afterwards, turn the robot 90 degrees to the right
        ///    TurnLeftRadians(-Math.PI / 2);
        ///  </pre>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustGunForRobotTurn"/>
        ///</summary>
        ///  <param name="radians">the amount of radians to turn the robot's body to the left.
        ///  If radians &gt; 0 the robot will turn right.
        ///  If radians &lt; 0 the robot will turn left.
        ///  If radians = 0 the robot will not turn, but Execute.</param>
        public void TurnLeftRadians(double radians)
        {
            if (peer != null)
            {
                peer.turnBody(-radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Immediately turns the robot's body to the right by radians.
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the robot's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's body is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Turn the robot 180 degrees to the right
        ///    TurnRightRadians(Math.PI);
        ///    <p />
        ///    // Afterwards, turn the robot 90 degrees to the left
        ///    TurnRightRadians(-Math.PI / 2);
        ///  </pre>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustGunForRobotTurn"/>
        ///</summary>
        ///  <param name="radians"> the amount of radians to turn the robot's body to the right.
        ///  If radians &gt; 0 the robot will turn right.
        ///  If radians &lt; 0 the robot will turn left.
        ///  If radians = 0 the robot will not turn, but Execute.</param>
        public void TurnRightRadians(double radians)
        {
            if (peer != null)
            {
                peer.turnBody(radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Returns the direction that the robot's gun is facing, in radians.
        ///  The value returned will be between 0 and 2 * PI (is excluded).
        ///  <p />
        ///  Note that the heading in Robocode is like a compass, where 0 means North,
        ///  PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///  <seealso cref="Robot.GunHeading"/>
        ///  <seealso cref="HeadingRadians"/>
        ///  <seealso cref="RadarHeadingRadians"/>
        ///</summary>
        public double GunHeadingRadians
        {
            get
            {
                if (peer != null)
                {
                    return peer.getGunHeading();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>        
        ///  Returns the direction that the robot's radar is facing, in radians.
        ///  The value returned will be between 0 and 2 * PI (is excluded).
        ///  <p />
        ///  Note that the heading in Robocode is like a compass, where 0 means North,
        ///  PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///  <seealso cref="Robot.RadarHeading"/>
        ///  <seealso cref="HeadingRadians"/>
        ///  <seealso cref="GunHeadingRadians"/>
        ///</summary>
        public double RadarHeadingRadians
        {
            get
            {
                if (peer != null)
                {
                    return peer.getRadarHeading();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>        
        ///  Sets the robot's gun to turn left by radians when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's gun is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the gun to turn 180 degrees to the left
        ///    SetTurnGunLeftRadians(Math.PI);
        ///    <p />
        ///    // Set the gun to turn 90 degrees to the right instead of left
        ///    // (overrides the previous order)
        ///    SetTurnGunLeftRadians(-Math.PI / 2);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnGunLeftRadians()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="AdvancedRobot.SetTurnGunLeft(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnGunRight(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustGunForRobotTurn"/>
        ///</summary>
        ///  <param name="radians"> the amount of radians to turn the robot's gun to the left.
        ///  If radians &gt; 0 the robot's gun is set to turn left.
        ///  If radians &lt; 0 the robot's gun is set to turn right.
        ///  If radians = 0 the robot's gun is set to Stop turning.</param>
        public void SetTurnGunLeftRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnGun(-radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Sets the robot's gun to turn right by radians when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's gun is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the gun to turn 180 degrees to the right
        ///    SetTurnGunRightRadians(Math.PI);
        ///    <p />
        ///    // Set the gun to turn 90 degrees to the left instead of right
        ///    // (overrides the previous order)
        ///    SetTurnGunRightRadians(-Math.PI / 2);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnGunRightRadians()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="AdvancedRobot.SetTurnGunRight(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnGunLeft(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustGunForRobotTurn"/>
        ///</summary>
        ///  <param name="radians">the amount of radians to turn the robot's gun to the right.
        ///  If radians &gt; 0 the robot's gun is set to turn left.
        ///  If radians &lt; 0 the robot's gun is set to turn right.
        ///  If radians = 0 the robot's gun is set to Stop turning.</param>
        public void SetTurnGunRightRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnGun(radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Sets the robot's radar to turn left by radians when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's radar is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the radar to turn 180 degrees to the left
        ///    SetTurnRadarLeftRadians(Math.PI);
        ///    <p />
        ///    // Set the radar to turn 90 degrees to the right instead of left
        ///    // (overrides the previous order)
        ///    SetTurnRadarLeftRadians(-Math.PI / 2);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnRadarLeftRadians()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="AdvancedRobot.SetTurnRadarLeft(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnRadarRightRadians(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnRadarRight(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustRadarForRobotTurn"/>
        ///  <seealso cref="Robot.IsAdjustRadarForGunTurn"/>
        ///</summary>
        ///  <param name="radians">the amount of radians to turn the robot's radar to the left.
        ///  If radians &gt; 0 the robot's radar is set to turn left.
        ///  If radians &lt; 0 the robot's radar is set to turn right.
        ///  If radians = 0 the robot's radar is set to Stop turning.</param>
        public void SetTurnRadarLeftRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnRadar(-radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Sets the robot's radar to turn right by radians when the next execution
        ///  takes place.
        ///  <p />
        ///  This call returns immediately, and will not Execute until you call
        ///  Execute() or take an action that executes.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's radar is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Set the radar to turn 180 degrees to the right
        ///    SetTurnRadarRightRadians(Math.PI);
        ///    <p />
        ///    // Set the radar to turn 90 degrees to the right instead of right
        ///    // (overrides the previous order)
        ///    SetTurnRadarRightRadians(-Math.PI / 2);
        ///    <p />
        ///    ...
        ///    // Executes the last SetTurnRadarRightRadians()
        ///    Execute();
        ///  </pre>
        ///  <seealso cref="AdvancedRobot.SetTurnRadarRight(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnRadarLeftRadians(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnRadarLeft(double)"/>
        ///  <seealso cref="AdvancedRobot.SetTurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustRadarForRobotTurn"/>
        ///  <seealso cref="Robot.IsAdjustRadarForGunTurn"/>
        ///</summary>
        ///  <param name="radians"> the amount of radians to turn the robot's radar to the right.
        ///  If radians &gt; 0 the robot's radar is set to turn left.
        ///  If radians &lt; 0 the robot's radar is set to turn right.
        ///  If radians = 0 the robot's radar is set to Stop turning.</param>
        public void SetTurnRadarRightRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).setTurnRadar(radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Immediately turns the robot's gun to the left by radians.
        ///  <p />
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the gun's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's gun is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Turn the robot's gun 180 degrees to the left
        ///    TurnGunLeftRadians(Math.PI);
        ///    <p />
        ///    // Afterwards, turn the robot's gun 90 degrees to the right
        ///    TurnGunLeftRadians(-Math.PI / 2);
        ///  </pre>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustGunForRobotTurn"/>
        ///</summary>
        ///  <param name="radians">the amount of radians to turn the robot's gun to the left.
        ///  If radians &gt; 0 the robot's gun will turn left.
        ///  If radians &lt; 0 the robot's gun will turn right.
        ///  If radians = 0 the robot's gun will not turn, but Execute.</param>        
        public void TurnGunLeftRadians(double radians)
        {
            if (peer != null)
            {
                peer.turnGun(-radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Immediately turns the robot's gun to the right by radians.
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the gun's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's gun is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Turn the robot's gun 180 degrees to the right
        ///    TurnGunRightRadians(Math.PI);
        ///    <p />
        ///    // Afterwards, turn the robot's gun 90 degrees to the left
        ///    TurnGunRightRadians(-Math.PI / 2);
        ///  </pre>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnRadarLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnRadarRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustGunForRobotTurn"/>
        ///</summary>
        ///  <param name="radians">the amount of radians to turn the robot's gun to the right.
        ///  If radians &gt; 0 the robot's gun will turn right.
        ///  If radians &lt; 0 the robot's gun will turn left.
        ///  If radians = 0 the robot's gun will not turn, but Execute.</param>
        public void TurnGunRightRadians(double radians)
        {
            if (peer != null)
            {
                peer.turnGun(radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Immediately turns the robot's radar to the left by radians.
        ///  <p />
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the radar's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's radar is set to turn right
        ///  instead of left.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Turn the robot's radar 180 degrees to the left
        ///    TurnRadarLeftRadians(Math.PI);
        ///    <p />
        ///    // Afterwards, turn the robot's radar 90 degrees to the right
        ///    TurnRadarLeftRadians(-Math.PI / 2);
        ///  </pre>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustRadarForRobotTurn"/>
        ///  <seealso cref="Robot.IsAdjustRadarForGunTurn"/>
        ///</summary>
        ///  <param name="radians">the amount of radians to turn the robot's radar to the left.
        ///  If radians &gt; 0 the robot's radar will turn left.
        ///  If radians &lt; 0 the robot's radar will turn right.
        ///  If radians = 0 the robot's radar will not turn, but Execute.</param>
        public void TurnRadarLeftRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).turnRadar(-radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>
        ///  Immediately turns the robot's radar to the right by radians.
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the radar's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's radar is set to turn left
        ///  instead of right.
        ///  <p />
        ///  Example:
        ///  <pre>
        ///    // Turn the robot's radar 180 degrees to the right
        ///    TurnRadarRightRadians(Math.PI);
        ///    <p />
        ///    // Afterwards, turn the robot's radar 90 degrees to the left
        ///    TurnRadarRightRadians(-Math.PI / 2);
        ///  </pre>
        ///  <seealso cref="Robot.TurnRadarRight(double)"/>
        ///  <seealso cref="Robot.TurnRadarLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnLeft(double)"/>
        ///  <seealso cref="TurnLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnRight(double)"/>
        ///  <seealso cref="TurnRightRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunLeft(double)"/>
        ///  <seealso cref="TurnGunLeftRadians(double)"/>
        ///  <seealso cref="Robot.TurnGunRight(double)"/>
        ///  <seealso cref="TurnGunRightRadians(double)"/>
        ///  <seealso cref="Robot.IsAdjustRadarForRobotTurn"/>
        ///  <seealso cref="Robot.IsAdjustRadarForGunTurn"/>
        ///</summary>
        ///  <param name="radians">the amount of radians to turn the robot's radar to the right.
        ///  If radians &gt; 0 the robot's radar will turn right.
        ///  If radians &lt; 0 the robot's radar will turn left.
        ///  If radians = 0 the robot's radar will not turn, but Execute.</param>
        public void TurnRadarRightRadians(double radians)
        {
            if (peer != null)
            {
                ((IAdvancedRobotPeer) peer).turnRadar(radians);
            }
            else
            {
                UninitializedException();
            }
        }

        ///<summary>        
        ///  Returns the angle remaining in the gun's turn, in radians.
        ///  <p />
        ///  This call returns both positive and negative values. Positive values
        ///  means that the gun is currently turning to the right. Negative values
        ///  means that the gun is currently turning to the left.
        ///  <seealso cref="AdvancedRobot.GunTurnRemaining"/>
        ///  <seealso cref="AdvancedRobot.TurnRemaining"/>
        ///  <seealso cref="TurnRemainingRadians"/>
        ///  <seealso cref="AdvancedRobot.RadarTurnRemaining"/>
        ///  <seealso cref="RadarTurnRemainingRadians"/>
        ///</summary>
        public double GunTurnRemainingRadians
        {
            get
            {
                if (peer != null)
                {
                    return peer.getGunTurnRemaining();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>        
        ///  Returns the angle remaining in the radar's turn, in radians.
        ///  <p />
        ///  This call returns both positive and negative values. Positive values
        ///  means that the radar is currently turning to the right. Negative values
        ///  means that the radar is currently turning to the left.
        ///  <seealso cref="RadarTurnRemaining"/>
        ///  <seealso cref="TurnRemaining"/>
        ///  <seealso cref="TurnRemainingRadians"/>
        ///  <seealso cref="GunTurnRemaining"/>
        ///  <seealso cref="GunTurnRemainingRadians"/>
        ///</summary>
        public double RadarTurnRemainingRadians
        {
            get
            {
                if (peer != null)
                {
                    return peer.getRadarTurnRemaining();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>        
        ///  Returns the angle remaining in the robot's turn, in radians.
        ///  <p />
        ///  This call returns both positive and negative values. Positive values
        ///  means that the robot is currently turning to the right. Negative values
        ///  means that the robot is currently turning to the left.
        ///  <seealso cref="AdvancedRobot.TurnRemaining"/>
        ///  <seealso cref="AdvancedRobot.GunTurnRemaining"/>
        ///  <seealso cref="GunTurnRemainingRadians"/>
        ///  <seealso cref="AdvancedRobot.RadarTurnRemaining"/>
        ///  <seealso cref="RadarTurnRemainingRadians"/>
        ///</summary>
        public double TurnRemainingRadians
        {
            get
            {
                if (peer != null)
                {
                    return peer.getBodyTurnRemaining();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <inheritdoc />
        IAdvancedEvents IAdvancedRobot.GetAdvancedEventListener()
        {
            return this; // this robot is listening
        }
    }
}

//doc