/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Added SetColors(Color, Color, Color, Color, Color), SetAllColors(),
 *       setBodyColor(), setGunColor(), setRadarColor(), setBulletColor(), and
 *       setScanColor()
 *     - Updated Javadocs
 *     - The finalize() is now protected instead of public
 *     - Added OnKeyPressed(), OnKeyReleased(), OnKeyTyped() events
 *     - Added OnMouseMoved(), OnMouseClicked(), OnMouseReleased(),
 *       OnMouseEntered(), OnMouseExited(), OnMouseDragged(), OnMouseWheelMoved()
 *       events
 *     - The UninitializedException() method does not need a method name as input
 *       parameter anymore
 *     - The TextWriter 'out' has been moved to the new _RobotBase class
 *     Matthew Reeder
 *     - Fix for HyperThreading hang issue
 *     Stefan Westen (RobocodeGL) & Flemming N. Larsen
 *     - Added OnPaint() method for painting the robot
 *     Pavel Savara
 *     - Re-work of robot interfaces
 *     - Added getGraphics()
 *******************************************************************************/
using System;
using System.Drawing;
using System.IO;
using System.Security.Permissions;
using net.sf.robocode.security;
using robocode.exception;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
    /// 
    ///<summary>
    ///  The basic robot class that you will extend to create your own robots.
    ///  <p />
    ///  <p />
    ///  Please note the following standards will be used:
    ///  <br />
    ///  heading - absolute angle in degrees with 0 facing up the screen,
    ///  positive clockwise. 0 &lt;= heading &lt; 360.
    ///  <br />
    ///  bearing - relative angle to some obj from your robot's heading,
    ///  positive clockwise. -180 &lt; bearing &lt;= 180
    ///  <br />
    ///  All coordinates are expressed as (x,y).
    ///  <br />
    ///  All coordinates are positive.
    ///  <br />
    ///  The origin (0,0) is at the bottom left of the screen.
    ///  <br />
    ///  Positive X is right.
    ///  <br />
    ///  Positive Y is up.
    ///  <see href="http://robocode.sourceforge.net">
    ///    robocode.sourceforge.net
    ///  </see>
    ///  <see href="http://robocode.sourceforge.net/myfirstrobot/MyFirstRobot.html">
    ///    Building your first robot
    ///  </see>
    ///  <seealso cref="JuniorRobot" />
    ///  <seealso cref="AdvancedRobot" />
    ///  <seealso cref="TeamRobot" />
    ///  <seealso cref="IDroid" />
    ///</summary>
    public abstract class Robot : IInteractiveRobot, IPaintRobot, IBasicEvents3, IInteractiveEvents,
                                  IPaintEvents, IRunnable
    {
        private const int
            WIDTH = 40,
            HEIGHT = 40;

        internal IBasicRobotPeer peer;
        internal TextWriter _output;

        /// <summary>
        ///   The Out stream your robot should use to print.
        ///   <p />
        ///   You can view the print-outs by clicking the button for your robot in the
        ///   right side of the battle window.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Print Out a line each time my robot hits another robot
        ///       public void OnHitRobot(HitRobotEvent e) {
        ///       Out.println("I hit a robot!  My energy: " + Energy + " his energy: " + e.Energy);
        ///       }
        ///     </pre>
        ///   </example>
        /// </summary>
        public TextWriter Out
        {
            get { return _output; }
        }

        [RobocodeInternalPermission(SecurityAction.LinkDemand)]
        void IBasicRobot.SetOut(TextWriter outpt)
        {
            _output = outpt;
        }

        [RobocodeInternalPermission(SecurityAction.LinkDemand)]
        void IBasicRobot.SetPeer(IBasicRobotPeer per)
        {
            peer = per;
        }

        /// <summary>
        ///   Throws a RobotException. This method should be called when the robot's peer
        ///   is uninitialized.
        /// </summary>
        internal static void UninitializedException()
        {
            throw new RobotException(
                "You cannot call the methods before your Run() method is called, or you are using a Robot object that the game doesn't know about.");
        }

        /// <inheritdoc />
        IRunnable IBasicRobot.GetRobotRunnable()
        {
            return this;
        }

        /// <inheritdoc />
        IBasicEvents IBasicRobot.GetBasicEventListener()
        {
            return this;
        }

        /// <inheritdoc />
        IInteractiveEvents IInteractiveRobot.GetInteractiveEventListener()
        {
            return this;
        }

        /// <inheritdoc />
        IPaintEvents IPaintRobot.GetPaintEventListener()
        {
            return this;
        }

        /// <summary>
        ///   Immediately moves your robot Ahead (forward) by distance measured in
        ///   pixels.
        ///   <p />
        ///   This call executes immediately, and does not return until it is complete,
        ///   i.e. when the remaining distance to move is 0.
        ///   <p />
        ///   If the robot collides with a wall, the move is complete, meaning that the
        ///   robot will not move any further. If the robot collides with another
        ///   robot, the move is complete if you are heading toward the other robot.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the robot is set to move backward
        ///   instead of forward.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Move the robot 100 pixels forward
        ///       Ahead(100);
        ///       <p />
        ///       // Afterwards, move the robot 50 pixels backward
        ///       Ahead(-50);
        ///     </pre>
        ///   </example>
        /// </summary>
        /// <param name="distance">
        ///   the distance to move Ahead measured in pixels.
        ///   If this value is negative, the robot will move Back instead of Ahead.
        /// </param>
        /// <seealso cref="Back(double)" />
        /// <seealso cref="OnHitWall(HitWallEvent)" />
        /// <seealso cref="OnHitRobot(HitRobotEvent)" />
        public void Ahead(double distance)
        {
            if (peer != null)
            {
                peer.move(distance);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Immediately moves your robot backward by distance measured in pixels.
        ///   <p />
        ///   This call executes immediately, and does not return until it is complete,
        ///   i.e. when the remaining distance to move is 0.
        ///   <p />
        ///   If the robot collides with a wall, the move is complete, meaning that the
        ///   robot will not move any further. If the robot collides with another
        ///   robot, the move is complete if you are heading toward the other robot.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the robot is set to move forward instead
        ///   of backward.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Move the robot 100 pixels backward
        ///       Back(100);
        ///       <p />
        ///       // Afterwards, move the robot 50 pixels forward
        ///       Back(-50);
        ///     </pre>
        ///   </example>
        ///   <seealso cref="Ahead(double)" />
        ///   <seealso cref="OnHitWall(HitWallEvent)" />
        ///   <seealso cref="OnHitRobot(HitRobotEvent)" />
        /// </summary>
        /// <param name="distance">
        ///   the distance to move Back measured in pixels.
        ///   If this value is negative, the robot will move Ahead instead of Back.
        /// </param>
        public void Back(double distance)
        {
            if (peer != null)
            {
                peer.move(-distance);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Returns the width of the current battlefield measured in pixels.
        /// </summary>
        public double BattleFieldWidth
        {
            get
            {
                if (peer != null)
                {
                    return peer.getBattleFieldWidth();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the height of the current battlefield measured in pixels.
        /// </summary>
        public double BattleFieldHeight
        {
            get
            {
                if (peer != null)
                {
                    return peer.getBattleFieldHeight();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the direction that the robot's body is facing, in degrees.
        ///   The value returned will be between 0 and 360 (is excluded).
        ///   <p />
        ///   Note that the heading in Robocode is like a compass, where 0 means North,
        ///   90 means East, 180 means South, and 270 means West.
        ///   <seealso cref="GunHeading" />
        ///   <seealso cref="RadarHeading" />
        /// </summary>
        public double Heading
        {
            get
            {
                if (peer != null)
                {
                    double rv = 180.0*peer.getBodyHeading()/Math.PI;

                    while (rv < 0)
                    {
                        rv += 360;
                    }
                    while (rv >= 360)
                    {
                        rv -= 360;
                    }
                    return rv;
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the height of the robot measured in pixels.
        ///   <seealso cref="Width" />
        /// </summary>
        public double Height
        {
            get
            {
                if (peer == null)
                {
                    UninitializedException();
                }
                return HEIGHT;
            }
        }

        /// <summary>
        ///   Returns the width of the robot measured in pixels.
        ///   <seealso cref="Height" />
        /// </summary>
        public double Width
        {
            get
            {
                if (peer == null)
                {
                    UninitializedException();
                }
                return WIDTH;
            }
        }

        /// <summary>
        ///   Returns the robot's name.
        /// </summary>
        public string Name
        {
            get
            {
                if (peer != null)
                {
                    return peer.getName();
                }
                UninitializedException();
                return null; // never called
            }
        }

        /// <summary>
        ///   Returns the X position of the robot. (0,0) is at the bottom left of the
        ///   battlefield.
        ///   <seealso cref="Y" />
        /// </summary>
        public double X
        {
            get
            {
                if (peer != null)
                {
                    return peer.getX();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the Y position of the robot. (0,0) is at the bottom left of the
        ///   battlefield.
        ///   <seealso cref="X" />
        /// </summary>
        public double Y
        {
            get
            {
                if (peer != null)
                {
                    return peer.getY();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   The main method in every robot. You must override this to set up your
        ///   robot's basic behavior.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // A basic robot that moves around in a square
        ///       public void Run() {
        ///       while (true) {
        ///       Ahead(100);
        ///       TurnRight(90);
        ///       }
        ///       }
        ///     </pre>
        ///   </example>
        /// </summary>
        public virtual void Run()
        {
        }

        /// <summary>
        ///   Immediately turns the robot's body to the left by degrees.
        ///   <p />
        ///   This call executes immediately, and does not return until it is complete,
        ///   i.e. when the angle remaining in the robot's turn is 0.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the robot's body is set to turn right
        ///   instead of left.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Turn the robot 180 degrees to the left
        ///       TurnLeft(180);
        ///       <p />
        ///       // Afterwards, turn the robot 90 degrees to the right
        ///       TurnLeft(-90);
        ///     </pre>
        ///   </example>
        ///   <seealso cref="TurnRight(double)" />
        ///   <seealso cref="TurnGunLeft(double)" />
        ///   <seealso cref="TurnGunRight(double)" />
        ///   <seealso cref="TurnRadarLeft(double)" />
        ///   <seealso cref="TurnRadarRight(double)" />
        /// </summary>
        /// <param name="degrees">
        ///   the amount of degrees to turn the robot's body to the left.
        ///   If
        ///   <pre>degrees</pre>
        ///   &gt; 0 the robot will turn left.
        ///   If
        ///   <pre>degrees</pre>
        ///   &lt; 0 the robot will turn right.
        ///   If
        ///   <pre>degrees</pre>
        ///   = 0 the robot will not turn, but Execute.
        /// </param>
        public void TurnLeft(double degrees)
        {
            if (peer != null)
            {
                peer.turnBody(-Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// 
        ///<summary>
        ///  Immediately turns the robot's body to the right by degrees.
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the robot's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's body is set to turn left
        ///  instead of right.
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Turn the robot 180 degrees to the right
        ///      TurnRight(180);
        ///      <p />
        ///      // Afterwards, turn the robot 90 degrees to the left
        ///      TurnRight(-90);
        ///    </pre>
        ///  </example>
        ///
        ///  <seealso cref="TurnLeft(double)" />
        ///  <seealso cref="TurnGunLeft(double)" />
        ///  <seealso cref="TurnGunRight(double)" />
        ///  <seealso cref="TurnRadarLeft(double)" />
        ///  <seealso cref="TurnRadarRight(double)" />
        ///</summary>
        ///<param name="degrees">
        ///  the amount of degrees to turn the robot's body to the right.
        ///  If
        ///  <pre>degrees</pre>
        ///  &gt; 0 the robot will turn right.
        ///  If
        ///  <pre>degrees</pre>
        ///  &lt; 0 the robot will turn left.
        ///  If
        ///  <pre>degrees</pre>
        ///  = 0 the robot will not turn, but Execute.
        ///</param>
        public void TurnRight(double degrees)
        {
            if (peer != null)
            {
                peer.turnBody(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Do nothing this turn, meaning that the robot will skip it's turn.
        ///   <p />
        ///   This call executes immediately, and does not return until the turn is
        ///   over.
        /// </summary>
        public void DoNothing()
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

        /// <summary>
        ///   Immediately fires a bullet. The bullet will travel in the direction the
        ///   gun is pointing.
        ///   <p />
        ///   The specified bullet power is an amount of energy that will be taken from
        ///   the robot's energy. Hence, the more power you want to spend on the
        ///   bullet, the more energy is taken from your robot.
        ///   <p />
        ///   The bullet will do (4 * power) damage if it hits another robot. If power
        ///   is greater than 1, it will do an additional 2 * (power - 1) damage.
        ///   You will get (3 * power) Back if you hit the other robot. You can call
        ///   <see cref="Rules.GetBulletDamage(double)" />
        ///   for getting the damage that a
        ///   bullet with a specific bullet power will do.
        ///   <p />
        ///   The specified bullet power should be between
        ///   <see cref="Rules.MIN_BULLET_POWER" />
        ///   and
        ///   <see cref="Rules.MAX_BULLET_POWER" />
        ///   .
        ///   <p />
        ///   Note that the gun cannot Fire if the gun is overheated, meaning that
        ///   <see cref="GunHeat" />
        ///   returns a value &gt; 0.
        ///   <p />
        ///   A event is generated when the bullet hits a robot
        ///   (
        ///   <see cref="BulletHitEvent" />
        ///   ), wall (
        ///   <see cref="BulletMissedEvent" />
        ///   ), or another
        ///   bullet (
        ///   <see cref="BulletHitBulletEvent" />
        ///   ).
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Fire a bullet with maximum power if the gun is ready
        ///       if (GetGunHeat() == 0) {
        ///       Fire(Rules.MAX_BULLET_POWER);
        ///       }
        ///     </pre>
        ///   </example>
        ///   <seealso cref="FireBullet(double)" />
        ///   <seealso cref="GunHeat" />
        ///   <seealso cref="GunCoolingRate" />
        ///   <seealso cref="OnBulletHit(BulletHitEvent)" />
        ///   <seealso cref="OnBulletHitBullet(BulletHitBulletEvent)" />
        ///   <seealso cref="OnBulletMissed(BulletMissedEvent)" />
        /// </summary>
        /// <param name="power">
        ///   the amount of energy given to the bullet, and subtracted from the robot's energy.
        /// </param>
        public void Fire(double power)
        {
            if (peer != null)
            {
                peer.setFire(power);
                peer.execute();
            }
            else
            {
                UninitializedException();
            }
        }

        /// 
        ///<summary>
        ///  Immediately fires a bullet. The bullet will travel in the direction the
        ///  gun is pointing.
        ///  <p />
        ///  The specified bullet power is an amount of energy that will be taken from
        ///  the robot's energy. Hence, the more power you want to spend on the
        ///  bullet, the more energy is taken from your robot.
        ///  <p />
        ///  The bullet will do (4 * power) damage if it hits another robot. If power
        ///  is greater than 1, it will do an additional 2 * (power - 1) damage.
        ///  You will get (3 * power) Back if you hit the other robot. You can call
        ///  <see cref="Rules.GetBulletDamage(double)" />
        ///  for getting the damage that a
        ///  bullet with a specific bullet power will do.
        ///  <p />
        ///  The specified bullet power should be between
        ///  <see cref="Rules.MIN_BULLET_POWER" />
        ///  and
        ///  <see cref="Rules.MAX_BULLET_POWER" />
        ///  .
        ///  <p />
        ///  Note that the gun cannot Fire if the gun is overheated, meaning that
        ///  <see cref="GunHeat" />
        ///  returns a value &gt; 0.
        ///  <p />
        ///  A event is generated when the bullet hits a robot
        ///  (
        ///  <see cref="BulletHitEvent" />
        ///  ), wall (
        ///  <see cref="BulletMissedEvent" />
        ///  ), or another
        ///  bullet (
        ///  <see cref="BulletHitBulletEvent" />
        ///  ).
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Fire a bullet with maximum power if the gun is ready
        ///      if (GetGunHeat() == 0) {
        ///      Bullet bullet = FireBullet(Rules.MAX_BULLET_POWER);
        ///      <p />
        ///      // Get the velocity of the bullet
        ///      if (bullet != null) {
        ///      double bulletVelocity = bullet.Velocity;
        ///      }
        ///      }
        ///    </pre>
        ///  </example>
        ///  Returns a
        ///  <see cref="Bullet" />
        ///  that contains information about the bullet if it
        ///  was actually fired, which can be used for tracking the bullet after it
        ///  has been fired. If the bullet was not fired,
        ///  <pre>null</pre>
        ///  is returned.
        ///  <seealso cref="Fire(double)" />
        ///  <seealso cref="Bullet" />
        ///  <seealso cref="GunHeat" />
        ///  <seealso cref="GunCoolingRate" />
        ///  <seealso cref="OnBulletHit(BulletHitEvent)" />
        ///  <seealso cref="OnBulletHitBullet(BulletHitBulletEvent)" />
        ///  <seealso cref="OnBulletMissed(BulletMissedEvent)" />
        ///</summary>
        ///<param name="power">
        ///  power the amount of energy given to the bullet, and subtracted from the robot's energy.
        ///</param>
        public Bullet FireBullet(double power)
        {
            if (peer != null)
            {
                return peer.fire(power);
            }
            UninitializedException();
            return null;
        }

        /// <summary>
        ///   Returns the rate at which the gun will cool down, i.e. the amount of heat
        ///   the gun heat will drop per turn.
        ///   <p />
        ///   The gun cooling rate is default 0.1 / turn, but can be changed by the
        ///   battle setup. So don't count on the cooling rate being 0.1!
        ///   <seealso cref="GunHeat" />
        ///   <seealso cref="Fire(double)" />
        ///   <seealso cref="FireBullet(double)" />
        /// </summary>
        public double GunCoolingRate
        {
            get
            {
                if (peer != null)
                {
                    return peer.getGunCoolingRate();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the direction that the robot's gun is facing, in degrees.
        ///   The value returned will be between 0 and 360 (is excluded).
        ///   <p />
        ///   Note that the heading in Robocode is like a compass, where 0 means North,
        ///   90 means East, 180 means South, and 270 means West.
        ///   <seealso cref="Heading" />
        ///   <seealso cref="RadarHeading" />
        /// </summary>
        public double GunHeading
        {
            get
            {
                if (peer != null)
                {
                    return peer.getGunHeading()*180.0/Math.PI;
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the current heat of the gun. The gun cannot Fire unless this is
        ///   0. (Calls to Fire will succeed, but will not actually Fire unless
        ///   GetGunHeat() == 0).
        ///   <p />
        ///   The amount of gun heat generated when the gun is fired is
        ///   1 + (firePower / 5). Each turn the gun heat drops by the amount returned
        ///   by
        ///   <see cref="GunCoolingRate" />
        ///   , which is a battle setup.
        ///   <p />
        ///   Note that all guns are "hot" at the start of each round, where the gun
        ///   heat is 3.
        ///   <seealso cref="GunCoolingRate" />
        ///   <seealso cref="Fire(double)" />
        ///   <seealso cref="FireBullet(double)" />
        /// </summary>
        public double GunHeat
        {
            get
            {
                if (peer != null)
                {
                    return peer.getGunHeat();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the number of rounds in the current battle.
        ///   <seealso cref="RoundNum" />
        /// </summary>
        public int NumRounds
        {
            get
            {
                if (peer != null)
                {
                    return peer.getNumRounds();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns how many opponents that are left in the current round.
        /// </summary>
        public int Others
        {
            get
            {
                if (peer != null)
                {
                    return peer.getOthers();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the direction that the robot's radar is facing, in degrees.
        ///   The value returned will be between 0 and 360 (is excluded).
        ///   <p />
        ///   Note that the heading in Robocode is like a compass, where 0 means North,
        ///   90 means East, 180 means South, and 270 means West.
        ///   <seealso cref="Heading" />
        ///   <seealso cref="GunHeading" />
        /// </summary>
        public double RadarHeading
        {
            get
            {
                if (peer != null)
                {
                    return peer.getRadarHeading()*180.0/Math.PI;
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the current round number (0 to
        ///   <see cref="NumRounds" />
        ///   - 1) of
        ///   the battle.
        ///   <seealso cref="NumRounds" />
        /// </summary>
        public int RoundNum
        {
            get
            {
                if (peer != null)
                {
                    return peer.getRoundNum();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the game time of the current round, where the time is equal to
        ///   the current turn in the round.
        ///   <p />
        ///   A battle consists of multiple rounds.
        ///   <p />
        ///   Time is reset to 0 at the beginning of every round.
        /// </summary>
        public long Time
        {
            get
            {
                if (peer != null)
                {
                    return peer.getTime();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the velocity of the robot measured in pixels/turn.
        ///   <p />
        ///   The maximum velocity of a robot is defined by
        ///   <see cref="Rules.MAX_VELOCITY" />
        ///   (8 pixels / turn).
        ///   <seealso cref="Rules.MAX_VELOCITY" />
        /// </summary>
        public double Velocity
        {
            get
            {
                if (peer != null)
                {
                    return peer.getVelocity();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <inheritdoc />
        public virtual void OnBulletHit(BulletHitEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnBulletHitBullet(BulletHitBulletEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnBulletMissed(BulletMissedEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnDeath(DeathEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnHitByBullet(HitByBulletEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnHitRobot(HitRobotEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnHitWall(HitWallEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnRobotDeath(RobotDeathEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnScannedRobot(ScannedRobotEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnWin(WinEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnRoundEnded(RoundEndedEvent evnt)
        {
        }

        /// <inheritdoc />
        public virtual void OnBattleEnded(BattleEndedEvent evnt)
        {
        }

        /// 
        ///<summary>
        ///  Scans for other robots. This method is called automatically by the game,
        ///  as long as the robot is moving, turning its body, turning its gun, or
        ///  turning its radar.
        ///  <p />
        ///  Scan will cause
        ///  <see cref="OnScannedRobot(ScannedRobotEvent)" />
        ///  to be called if you see a robot.
        ///  <p />
        ///  There are 2 reasons to call
        ///  <see cref="Scan()" />
        ///  manually:
        ///  <ol>
        ///    <li>
        ///      You want to Scan after you Stop moving.
        ///    </li>
        ///    <li>
        ///      You want to interrupt the
        ///      <see cref="OnScannedRobot(ScannedRobotEvent)" />
        ///      event. This is more
        ///      likely. If you are in
        ///      <see cref="OnScannedRobot(ScannedRobotEvent)" />
        ///      and call
        ///      <see cref="Scan()" />
        ///      ,
        ///      and you still see a robot, then the system will interrupt your
        ///      <see cref="OnScannedRobot(ScannedRobotEvent)" />
        ///      evnt immediately and start it from the top.
        ///    </li>
        ///  </ol>
        ///  <p />
        ///  This call executes immediately.
        ///  <seealso cref="OnScannedRobot(ScannedRobotEvent)" />
        ///  <seealso cref="ScannedRobotEvent" />
        ///</summary>
        public void Scan()
        {
            if (peer != null)
            {
                peer.rescan();
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Sets the gun to turn independent from the robot's turn.
        ///   <p />
        ///   Ok, so this needs some explanation: The gun is mounted on the robot's
        ///   body. So, normally, if the robot turns 90 degrees to the right, then the
        ///   gun will turn with it as it is mounted on top of the robot's body. To
        ///   compensate for this, you can call
        ///   <see cref="IsAdjustGunForRobotTurn" />
        ///   .
        ///   When this is set, the gun will turn independent from the robot's turn,
        ///   i.e. the gun will compensate for the robot's body turn.
        ///   <p />
        ///   Note: This method is additive until you reach the maximum the gun can
        ///   turn. The "adjust" is added to the amount you set for turning the robot,
        ///   then capped by the physics of the game. If you turn infinite, then the
        ///   adjust is ignored (and hence overridden).
        ///   <p />
        ///   Example, assuming both the robot and gun start Out facing up (0 degrees):
        ///   <pre>
        ///     // Set gun to turn with the robot's turn
        ///     setAdjustGunForRobotTurn(false); // This is the default
        ///     TurnRight(90);
        ///     // At this point, both the robot and gun are facing right (90 degrees)
        ///     TurnLeft(90);
        ///     // Both are Back to 0 degrees
        ///     <p />
        ///     -- or --
        ///     <p />
        ///     // Set gun to turn independent from the robot's turn
        ///     setAdjustGunForRobotTurn(true);
        ///     TurnRight(90);
        ///     // At this point, the robot is facing right (90 degrees), but the gun is still facing up.
        ///     TurnLeft(90);
        ///     // Both are Back to 0 degrees.
        ///   </pre>
        ///   <p />
        ///   Note: The gun compensating this way does count as "turning the gun".
        ///   <seealso cref="IsAdjustRadarForGunTurn" />
        /// </summary>
        public virtual bool IsAdjustGunForRobotTurn
        {
            get
            {
                if (peer != null)
                {
                    return ((IStandardRobotPeer) peer).isAdjustGunForBodyTurn();
                }
                UninitializedException();
                return false;
            }
            set
            {
                if (peer != null)
                {
                    ((IStandardRobotPeer) peer).setAdjustGunForBodyTurn(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// <summary>
        ///   Sets the radar to turn independent from the robot's turn.
        ///   <p />
        ///   Ok, so this needs some explanation: The radar is mounted on the gun, and
        ///   the gun is mounted on the robot's body. So, normally, if the robot turns
        ///   90 degrees to the right, the gun turns, as does the radar. Hence, if the
        ///   robot turns 90 degrees to the right, then the gun and radar will turn
        ///   with it as the radar is mounted on top of the gun. To compensate for
        ///   this, you can call
        ///   <see cref="IsAdjustRadarForRobotTurn" />
        ///   = true. When this is
        ///   set, the radar will turn independent from the robot's turn, i.e. the
        ///   radar will compensate for the robot's turn.
        ///   <p />
        ///   Note: This method is additive until you reach the maximum the radar can
        ///   turn. The "adjust" is added to the amount you set for turning the robot,
        ///   then capped by the physics of the game. If you turn infinite, then the
        ///   adjust is ignored (and hence overridden).
        ///   <p />
        ///   Example, assuming the robot, gun, and radar all start Out facing up (0
        ///   degrees):
        ///   <pre>
        ///     // Set radar to turn with the robots's turn
        ///     setAdjustRadarForRobotTurn(false); // This is the default
        ///     TurnRight(90);
        ///     // At this point, the body, gun, and radar are all facing right (90 degrees);
        ///     <p />
        ///     -- or --
        ///     <p />
        ///     // Set radar to turn independent from the robot's turn
        ///     setAdjustRadarForRobotTurn(true);
        ///     TurnRight(90);
        ///     // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
        ///   </pre>
        ///   <seealso cref="IsAdjustGunForRobotTurn" />
        ///   <seealso cref="IsAdjustRadarForGunTurn" />
        /// </summary>
        public bool IsAdjustRadarForRobotTurn
        {
            get
            {
                if (peer != null)
                {
                    return ((IStandardRobotPeer) peer).isAdjustRadarForBodyTurn();
                }
                UninitializedException();
                return false;
            }
            set
            {
                if (peer != null)
                {
                    ((IStandardRobotPeer) peer).setAdjustRadarForBodyTurn(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// <summary>
        ///   Sets the radar to turn independent from the gun's turn.
        ///   <p />
        ///   Ok, so this needs some explanation: The radar is mounted on the robot's
        ///   gun. So, normally, if the gun turns 90 degrees to the right, then the
        ///   radar will turn with it as it is mounted on top of the gun. To compensate
        ///   for this, you can call
        ///   <see cref="IsAdjustRadarForGunTurn" />
        ///   = (true). When this
        ///   is set, the radar will turn independent from the robot's turn, i.e. the
        ///   radar will compensate for the gun's turn.
        ///   <p />
        ///   Note: This method is additive until you reach the maximum the radar can
        ///   turn. The "adjust" is added to the amount you set for turning the gun,
        ///   then capped by the physics of the game. If you turn infinite, then the
        ///   adjust is ignored (and hence overridden).
        ///   <p />
        ///   Example, assuming both the gun and radar start Out facing up (0 degrees):
        ///   <pre>
        ///     // Set radar to turn with the gun's turn
        ///     setAdjustRadarForGunTurn(false); // This is the default
        ///     TurnGunRight(90);
        ///     // At this point, both the radar and gun are facing right (90 degrees);
        ///     <p />
        ///     -- or --
        ///     <p />
        ///     // Set radar to turn independent from the gun's turn
        ///     setAdjustRadarForGunTurn(true);
        ///     TurnGunRight(90);
        ///     // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
        ///   </pre>
        ///   Note: Calling
        ///   <see cref="IsAdjustRadarForGunTurn" />
        ///   will
        ///   automatically call
        ///   <see cref="IsAdjustRadarForRobotTurn" />
        ///   with the
        ///   same value, unless you have already called it earlier. This behavior is
        ///   primarily for backward compatibility with older Robocode robots.
        ///   <seealso cref="IsAdjustRadarForRobotTurn" />
        ///   <seealso cref="IsAdjustGunForRobotTurn" />
        /// </summary>
        public bool IsAdjustRadarForGunTurn
        {
            get
            {
                if (peer != null)
                {
                    return ((IStandardRobotPeer) peer).isAdjustRadarForGunTurn();
                }
                UninitializedException();
                return false;
            }
            set
            {
                if (peer != null)
                {
                    ((IStandardRobotPeer) peer).setAdjustRadarForGunTurn(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// <summary>
        ///   Sets the color of the robot's body, gun, and radar in the same time.
        ///   <p />
        ///   You may only call this method one time per battle. A
        ///   <pre>null</pre>
        ///   indicates the default (blue) color.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Don't forget to using java.awt.Color at the top...
        ///       using java.awt.Color;
        ///       ...
        ///       <p />
        ///       public void Run() {
        ///       SetColors(null, Color.RED, new Color(150, 0, 150));
        ///       ...
        ///       }
        ///     </pre>
        ///   </example>
        ///   <seealso cref="SetColors(Color, Color, Color, Color, Color)" />
        ///   <seealso cref="SetAllColors(Color)" />
        ///   <seealso cref="BodyColor" />
        ///   <seealso cref="GunColor" />
        ///   <seealso cref="RadarColor" />
        ///   <seealso cref="BulletColor" />
        ///   <seealso cref="ScanColor" />
        ///   <seealso cref="Color" />
        /// </summary>
        /// <param name="bodyColor">the new body color</param>
        /// <param name="gunColor">the new gun color</param>
        /// <param name="radarColor">the new radar color</param>
        public void SetColors(Color bodyColor, Color gunColor, Color radarColor)
        {
            if (peer != null)
            {
                peer.setBodyColor(bodyColor);
                peer.setGunColor(gunColor);
                peer.setRadarColor(radarColor);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Sets the color of the robot's body, gun, radar, bullet, and Scan arc in
        ///   the same time.
        ///   <p />
        ///   You may only call this method one time per battle. A
        ///   <pre>null</pre>
        ///   indicates the default (blue) color for the body, gun, radar, and Scan
        ///   arc, but white for the bullet color.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Don't forget to using java.awt.Color at the top...
        ///       using java.awt.Color;
        ///       ...
        ///       <p />
        ///       public void Run() {
        ///       SetColors(null, Color.RED, Color.GREEN, null, new Color(150, 0, 150));
        ///       ...
        ///       }
        ///     </pre>
        ///   </example>
        ///   <seealso cref="SetColors(Color, Color, Color)" />
        ///   <seealso cref="SetAllColors(Color)" />
        ///   <seealso cref="BodyColor" />
        ///   <seealso cref="GunColor" />
        ///   <seealso cref="RadarColor" />
        ///   <seealso cref="BulletColor" />
        ///   <seealso cref="ScanColor" />
        ///   <seealso cref="Color" />
        /// </summary>
        /// <param name="bodyColor">the new body color</param>
        /// <param name="gunColor">the new gun color</param>
        /// <param name="radarColor">the new radar color</param>
        /// <param name="bulletColor">the new bullet color</param>
        /// <param name="scanArcColor">
        ///   the new Scan arc color
        /// </param>
        public void SetColors(Color bodyColor, Color gunColor, Color radarColor, Color bulletColor, Color scanArcColor)
        {
            if (peer != null)
            {
                peer.setBodyColor(bodyColor);
                peer.setGunColor(gunColor);
                peer.setRadarColor(radarColor);
                peer.setBulletColor(bulletColor);
                peer.setScanColor(scanArcColor);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Sets all the robot's color to the same color in the same time, i.e. the
        ///   color of the body, gun, radar, bullet, and Scan arc.
        ///   <p />
        ///   You may only call this method one time per battle. A
        ///   <pre>null</pre>
        ///   indicates the default (blue) color for the body, gun, radar, and Scan
        ///   arc, but white for the bullet color.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Don't forget to using java.awt.Color at the top...
        ///       using java.awt.Color;
        ///       ...
        ///       <p />
        ///       public void Run() {
        ///       SetAllColors(Color.RED);
        ///       ...
        ///       }
        ///     </pre>
        ///   </example>
        ///   <seealso cref="SetColors(Color, Color, Color)" />
        ///   <seealso cref="SetColors(Color, Color, Color, Color, Color)" />
        ///   <seealso cref="BodyColor" />
        ///   <seealso cref="GunColor" />
        ///   <seealso cref="RadarColor" />
        ///   <seealso cref="BulletColor" />
        ///   <seealso cref="ScanColor" />
        ///   <seealso cref="Color" />
        /// </summary>
        /// <param name="color">
        ///   the new color for all the colors of the robot
        /// </param>
        public void SetAllColors(Color color)
        {
            if (peer != null)
            {
                peer.setBodyColor(color);
                peer.setGunColor(color);
                peer.setRadarColor(color);
                peer.setBulletColor(color);
                peer.setScanColor(color);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Sets the color of the robot's body.
        ///   <p />
        ///   A
        ///   <pre>null</pre>
        ///   indicates the default (blue) color.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Don't forget to using java.awt.Color at the top...
        ///       using java.awt.Color;
        ///       ...
        ///       <p />
        ///       public void Run() {
        ///       setBodyColor(Color.BLACK);
        ///       ...
        ///       }
        ///     </pre>
        ///   </example>
        ///   <seealso cref="SetColors(Color, Color, Color)" />
        ///   <seealso cref="SetColors(Color, Color, Color, Color, Color)" />
        ///   <seealso cref="SetAllColors(Color)" />
        ///   <seealso cref="GunColor" />
        ///   <seealso cref="RadarColor" />
        ///   <seealso cref="BulletColor" />
        ///   <seealso cref="ScanColor" />
        ///   <seealso cref="Color" />
        /// </summary>
        public Color BodyColor
        {
            set
            {
                if (peer != null)
                {
                    peer.setBodyColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// 
        ///<summary>
        ///  Sets the color of the robot's gun.
        ///  <p />
        ///  A
        ///  <pre>null</pre>
        ///  indicates the default (blue) color.
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Don't forget to using java.awt.Color at the top...
        ///      using java.awt.Color;
        ///      ...
        ///      <p />
        ///      public void Run() {
        ///      setGunColor(Color.RED);
        ///      ...
        ///      }
        ///    </pre>
        ///  </example>
        ///
        ///  <seealso cref="SetColors(Color, Color, Color)" />
        ///  <seealso cref="SetColors(Color, Color, Color, Color, Color)" />
        ///  <seealso cref="SetAllColors(Color)" />
        ///  <seealso cref="BodyColor" />
        ///  <seealso cref="RadarColor" />
        ///  <seealso cref="BulletColor" />
        ///  <seealso cref="ScanColor" />
        ///  <seealso cref="Color" />
        ///</summary>
        public Color GunColor
        {
            set
            {
                if (peer != null)
                {
                    peer.setGunColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// 
        ///<summary>
        ///  Sets the color of the robot's radar.
        ///  <p />
        ///  A
        ///  <pre>null</pre>
        ///  indicates the default (blue) color.
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Don't forget to using java.awt.Color at the top...
        ///      using java.awt.Color;
        ///      ...
        ///      <p />
        ///      public void Run() {
        ///      setRadarColor(Color.YELLOW);
        ///      ...
        ///      }
        ///    </pre>
        ///  </example>
        ///
        ///  <seealso cref="SetColors(Color, Color, Color)" />
        ///  <seealso cref="SetColors(Color, Color, Color, Color, Color)" />
        ///  <seealso cref="SetAllColors(Color)" />
        ///  <seealso cref="BodyColor" />
        ///  <seealso cref="GunColor" />
        ///  <seealso cref="BulletColor" />
        ///  <seealso cref="ScanColor" />
        ///  <seealso cref="Color" />
        ///</summary>
        public Color RadarColor
        {
            set
            {
                if (peer != null)
                {
                    peer.setRadarColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// 
        ///<summary>
        ///  Sets the color of the robot's bullets.
        ///  <p />
        ///  A
        ///  <pre>null</pre>
        ///  indicates the default white color.
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Don't forget to using java.awt.Color at the top...
        ///      using java.awt.Color;
        ///      ...
        ///      <p />
        ///      public void Run() {
        ///      setBulletColor(Color.GREEN);
        ///      ...
        ///      }
        ///    </pre>
        ///  </example>
        ///
        ///  <seealso cref="SetColors(Color, Color, Color)" />
        ///  <seealso cref="SetColors(Color, Color, Color, Color, Color)" />
        ///  <seealso cref="SetAllColors(Color)" />
        ///  <seealso cref="BodyColor" />
        ///  <seealso cref="GunColor" />
        ///  <seealso cref="RadarColor" />
        ///  <seealso cref="ScanColor" />
        ///  <seealso cref="Color" />
        ///</summary>
        public Color BulletColor
        {
            set
            {
                if (peer != null)
                {
                    peer.setBulletColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// 
        ///<summary>
        ///  Sets the color of the robot's Scan arc.
        ///  <p />
        ///  A
        ///  <pre>null</pre>
        ///  indicates the default (blue) color.
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Don't forget to using java.awt.Color at the top...
        ///      using java.awt.Color;
        ///      ...
        ///      <p />
        ///      public void Run() {
        ///      setScanColor(Color.WHITE);
        ///      ...
        ///      }
        ///    </pre>
        ///  </example>
        ///
        ///  <seealso cref="SetColors(Color, Color, Color)" />
        ///  <seealso cref="SetColors(Color, Color, Color, Color, Color)" />
        ///  <seealso cref="SetAllColors" />
        ///  <seealso cref="BodyColor" />
        ///  <seealso cref="GunColor" />
        ///  <seealso cref="RadarColor" />
        ///  <seealso cref="BulletColor" />
        ///  <seealso cref="Color" />
        ///</summary>
        public Color ScanColor
        {
            set
            {
                if (peer != null)
                {
                    peer.setScanColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// 
        ///<summary>
        ///  Immediately stops all movement, and saves it for a call to
        ///  <see cref="Resume()" />
        ///  . If there is already movement saved from a previous
        ///  Stop, this will have no effect.
        ///  <p />
        ///  This method is equivalent to
        ///  <see cref="Stop(bool)">Stop(false)</see>
        ///  .
        ///  <seealso cref="Resume()" />
        ///  <seealso cref="Stop(bool)" />
        ///</summary>
        public void Stop()
        {
            Stop(false);
        }

        /// <summary>
        ///   Immediately stops all movement, and saves it for a call to
        ///   <see cref="Resume()" />
        ///   . If there is already movement saved from a previous
        ///   Stop, you can overwrite it by calling
        ///   <see cref="Stop(bool)">Stop(true)</see>
        ///   .
        ///   <seealso cref="Resume()" />
        ///   <seealso cref="Stop()" />
        /// </summary>
        /// <param name="overwrite">
        ///   If there is already movement saved from a previous Stop,
        ///   you can overwrite it by calling
        ///   <see cref="Stop(bool)">Stop(true)</see>
        ///   .
        /// </param>
        public void Stop(bool overwrite)
        {
            if (peer != null)
            {
                ((IStandardRobotPeer) peer).stop(overwrite);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Immediately resumes the movement you stopped by
        ///   <see cref="Stop()" />
        ///   , if any.
        ///   <p />
        ///   This call executes immediately, and does not return until it is complete.
        ///   <seealso cref="Stop()" />
        ///   <seealso cref="Stop(bool)" />
        /// </summary>
        public void Resume()
        {
            if (peer != null)
            {
                ((IStandardRobotPeer) peer).resume();
            }
            else
            {
                UninitializedException();
            }
        }

        /// 
        ///<summary>
        ///  Immediately turns the robot's gun to the left by degrees.
        ///  <p />
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the gun's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's gun is set to turn right
        ///  instead of left.
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Turn the robot's gun 180 degrees to the left
        ///      TurnGunLeft(180);
        ///      <p />
        ///      // Afterwards, turn the robot's gun 90 degrees to the right
        ///      TurnGunLeft(-90);
        ///    </pre>
        ///  </example>
        ///
        ///  <seealso cref="TurnGunRight(double)" />
        ///  <seealso cref="TurnLeft(double)" />
        ///  <seealso cref="TurnRight(double)" />
        ///  <seealso cref="TurnRadarLeft(double)" />
        ///  <seealso cref="TurnRadarRight(double)" />
        ///  <seealso cref="IsAdjustGunForRobotTurn" />
        ///</summary>
        ///<param name="degrees">
        ///  the amount of degrees to turn the robot's gun to the left.
        ///  If
        ///  <pre>degrees</pre>
        ///  &gt; 0 the robot's gun will turn left.
        ///  If
        ///  <pre>degrees</pre>
        ///  &lt; 0 the robot's gun will turn right.
        ///  If
        ///  <pre>degrees</pre>
        ///  = 0 the robot's gun will not turn, but Execute.
        ///</param>
        public void TurnGunLeft(double degrees)
        {
            if (peer != null)
            {
                peer.turnGun(-Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// 
        ///<summary>
        ///  Immediately turns the robot's gun to the right by degrees.
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the gun's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's gun is set to turn left
        ///  instead of right.
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Turn the robot's gun 180 degrees to the right
        ///      TurnGunRight(180);
        ///      <p />
        ///      // Afterwards, turn the robot's gun 90 degrees to the left
        ///      TurnGunRight(-90);
        ///    </pre>
        ///  </example>
        ///
        ///  <seealso cref="TurnGunLeft(double)" />
        ///  <seealso cref="TurnLeft(double)" />
        ///  <seealso cref="TurnRight(double)" />
        ///  <seealso cref="TurnRadarLeft(double)" />
        ///  <seealso cref="TurnRadarRight(double)" />
        ///  <seealso cref="IsAdjustGunForRobotTurn" />
        ///</summary>
        ///<param name="degrees">
        ///  the amount of degrees to turn the robot's gun to the right.
        ///  If
        ///  <pre>degrees</pre>
        ///  &gt; 0 the robot's gun will turn right.
        ///  If
        ///  <pre>degrees</pre>
        ///  &lt; 0 the robot's gun will turn left.
        ///  If
        ///  <pre>degrees</pre>
        ///  = 0 the robot's gun will not turn, but Execute.
        ///</param>
        public void TurnGunRight(double degrees)
        {
            if (peer != null)
            {
                peer.turnGun(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Immediately turns the robot's radar to the left by degrees.
        ///   <p />
        ///   This call executes immediately, and does not return until it is complete,
        ///   i.e. when the angle remaining in the radar's turn is 0.
        ///   <p />
        ///   Note that both positive and negative values can be given as input,
        ///   where negative values means that the robot's radar is set to turn right
        ///   instead of left.
        ///   <p />
        ///   <example>
        ///     <pre>
        ///       // Turn the robot's radar 180 degrees to the left
        ///       TurnRadarLeft(180);
        ///       <p />
        ///       // Afterwards, turn the robot's radar 90 degrees to the right
        ///       TurnRadarLeft(-90);
        ///     </pre>
        ///   </example>
        ///   <seealso cref="TurnRadarRight(double)" />
        ///   <seealso cref="TurnLeft(double)" />
        ///   <seealso cref="TurnRight(double)" />
        ///   <seealso cref="TurnGunLeft(double)" />
        ///   <seealso cref="TurnGunRight(double)" />
        ///   <seealso cref="IsAdjustRadarForRobotTurn" />
        ///   <seealso cref="IsAdjustRadarForGunTurn" />
        /// </summary>
        /// <param name="degrees">
        ///   the amount of degrees to turn the robot's radar to the left.
        ///   If
        ///   <pre>degrees</pre>
        ///   &gt; 0 the robot's radar will turn left.
        ///   If
        ///   <pre>degrees</pre>
        ///   &lt; 0 the robot's radar will turn right.
        ///   If
        ///   <pre>degrees</pre>
        ///   = 0 the robot's radar will not turn, but Execute.
        /// </param>
        public void TurnRadarLeft(double degrees)
        {
            if (peer != null)
            {
                ((IStandardRobotPeer) peer).turnRadar(-Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// 
        ///<summary>
        ///  Immediately turns the robot's radar to the right by degrees.
        ///  This call executes immediately, and does not return until it is complete,
        ///  i.e. when the angle remaining in the radar's turn is 0.
        ///  <p />
        ///  Note that both positive and negative values can be given as input,
        ///  where negative values means that the robot's radar is set to turn left
        ///  instead of right.
        ///  <p />
        ///  <example>
        ///    <pre>
        ///      // Turn the robot's radar 180 degrees to the right
        ///      TurnRadarRight(180);
        ///      <p />
        ///      // Afterwards, turn the robot's radar 90 degrees to the left
        ///      TurnRadarRight(-90);
        ///    </pre>
        ///  </example>
        ///
        ///  <seealso cref="TurnRadarLeft(double)" />
        ///  <seealso cref="TurnLeft(double)" />
        ///  <seealso cref="TurnRight(double)" />
        ///  <seealso cref="TurnGunLeft(double)" />
        ///  <seealso cref="TurnGunRight(double)" />
        ///  <seealso cref="IsAdjustRadarForRobotTurn" />
        ///  <seealso cref="IsAdjustRadarForGunTurn" />
        ///</summary>
        ///<param name="degrees">
        ///  the amount of degrees to turn the robot's radar to the right.
        ///  If
        ///  <pre>degrees</pre>
        ///  &gt; 0 the robot's radar will turn right.
        ///  If
        ///  <pre>degrees</pre>
        ///  &lt; 0 the robot's radar will turn left.
        ///  If
        ///  <pre>degrees</pre>
        ///  = 0 the robot's radar will not turn, but Execute.
        ///</param>
        public void TurnRadarRight(double degrees)
        {
            if (peer != null)
            {
                ((IStandardRobotPeer) peer).turnRadar(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Returns the robot's current energy.
        /// </summary>
        public double Energy
        {
            get
            {
                if (peer != null)
                {
                    return peer.getEnergy();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns a graphics context used for painting graphical items for the robot.
        ///   <p />
        ///   This method is very useful for debugging your robot.
        ///   <p />
        ///   Note that the robot will only be painted if the "Paint" is enabled on the
        ///   robot's console window; otherwise the robot will never get painted (the
        ///   reason being that all robots might have graphical items that must be
        ///   painted, and then you might not be able to tell what graphical items that
        ///   have been painted for your robot).
        ///   <p />
        ///   Also note that the coordinate system for the graphical context where you
        ///   paint items fits for the Robocode coordinate system where (0, 0) is at
        ///   the bottom left corner of the battlefield, where X is towards right and Y
        ///   is upwards.
        ///   <seealso cref="OnPaint(IGraphics)" />
        /// </summary>
        public IGraphics Graphics
        {
            get
            {
                if (peer != null)
                {
                    return peer.getGraphics();
                }
                UninitializedException();
                return null; // never called
            }
        }

        /// <summary>
        ///   Sets the debug property with the specified key to the specified value.
        ///   <p />
        ///   This method is very useful when debugging or reviewing your robot as you
        ///   will be able to see this property displayed in the robot console for your
        ///   robots under the Debug Properties tab page.
        /// </summary>
        public DebugPropertyH DebugProperty
        {
            get
            {
                if (peer != null)
                {
                    if (debugProperty == null)
                    {
                        debugProperty = new DebugPropertyH(peer);
                    }
                    return debugProperty;
                }
                UninitializedException();
                return null;
            }
        }

        private DebugPropertyH debugProperty;

        /// <summary>
        /// Container class for debug properties
        /// </summary>
        public class DebugPropertyH
        {
            private readonly IBasicRobotPeer peer;

            internal DebugPropertyH(IBasicRobotPeer peer)
            {
                this.peer = peer;
            }

            /// <summary>
            ///   Set the new value of the debug property, where null or
            ///   the empty string is used for removing this debug property.
            /// </summary>
            /// <param name="key">
            ///   the name/key of the debug property
            /// </param>
            /// <returns></returns>
            public string this[string key]
            {
                set { peer.setDebugProperty(key, value); }
            }
        }


        /// <inheritdoc />
        public virtual void OnPaint(IGraphics graphics)
        {
        }

        /// <inheritdoc />
        public virtual void OnKeyPressed(KeyEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnKeyReleased(KeyEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnKeyTyped(KeyEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnMouseClicked(MouseEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnMouseEntered(MouseEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnMouseExited(MouseEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnMousePressed(MouseEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnMouseReleased(MouseEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnMouseMoved(MouseEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnMouseDragged(MouseEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnMouseWheelMoved(MouseWheelMovedEvent e)
        {
        }

        /// <inheritdoc />
        public virtual void OnStatus(StatusEvent e)
        {
        }
    }
}
//doc