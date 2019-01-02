/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Drawing;
using System.IO;
using System.Security.Permissions;
using net.sf.robocode.security;
using Robocode.Exception;
using Robocode.RobotInterfaces;
using Robocode.RobotInterfaces.Peer;
using Robocode.Util;

namespace Robocode
{
    /// 
    ///<summary>
    ///  The basic robot class that you will extend to create your own robots.
    ///  <p />
    ///  Please note the following standards will be used:
    ///  <p />
    ///  heading - absolute angle in degrees with 0 facing up the screen,
    ///  positive clockwise. 0 &lt;= heading &lt; 360.
    ///  <p />
    ///  bearing - relative angle to some obj from your robot's heading,
    ///  positive clockwise. -180 &lt; bearing &lt;= 180
    ///  <p />
    ///  All coordinates are expressed as (x,y).
    ///  All coordinates are positive.
    ///  <p />
    ///  The origin (0,0) is at the bottom left of the screen.
    ///  <p />
    ///  Positive X is right.
    ///  Positive Y is up.
    ///  <br /><see href="https://robocode.sourceforge.io"/>
    ///  <br /><see href="https://robocode.sourceforge.io/myfirstrobot/MyFirstRobot.html">
    ///    Building your first robot
    ///  </see>
    /// <seealso cref="JuniorRobot"/>
    /// <seealso cref="AdvancedRobot"/>
    /// <seealso cref="TeamRobot"/>
    /// <seealso cref="RateControlRobot"/>
    /// <seealso cref="IDroid"/>
    /// <seealso cref="IBorderSentry"/>
    ///</summary>
    public abstract class Robot : IInteractiveRobot, IPaintRobot, IBasicEvents3, IInteractiveEvents, IPaintEvents, IRunnable
    {
        private const int
            WIDTH = 36,
            HEIGHT = 36;

        internal IBasicRobotPeer peer;
        internal TextWriter _output;

        /// <summary>
        ///   The Out stream your robot should use to print.
        ///   <p />
        ///   You can view the print-outs by clicking the button for your robot in the
        ///   right side of the battle window.
        ///   <p />
        ///   <example>
        ///     <code>
        ///     // Print Out a line each time my robot hits another robot
        ///     public void OnHitRobot(HitRobotEvent e)
        ///     {
        ///         Out.WriteLine("I hit a robot!  My energy: " + Energy + " his energy: " + e.Energy);
        ///     }
        ///     </code>
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
        ///   Throws a RobotException.
        ///   This method should be called when the robot's peer is uninitialized.
        /// </summary>
        internal static void UninitializedException()
        {
            throw new RobotException(
                "You cannot call the methods before your Run() method is called, or you are using a Robot object that the game doesn't know about.");
        }

        /// <inheritdoc cref="IBasicRobot.GetRobotRunnable()"/>
        IRunnable IBasicRobot.GetRobotRunnable()
        {
            return this;
        }

        /// <inheritdoc cref="IBasicRobot.GetBasicEventListener()" />
        IBasicEvents IBasicRobot.GetBasicEventListener()
        {
            return this;
        }

        /// <inheritdoc cref="IInteractiveRobot.GetInteractiveEventListener()"/>
        IInteractiveEvents IInteractiveRobot.GetInteractiveEventListener()
        {
            return this;
        }

        /// <inheritdoc cref="IPaintRobot.GetPaintEventListener()"/>
        IPaintEvents IPaintRobot.GetPaintEventListener()
        {
            return this;
        }

        /// <summary>
        ///   Immediately moves your robot ahead (forward) by distance measured in
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
        ///     <code>
        ///     // Move the robot 100 pixels forward
        ///     Ahead(100);
        ///
        ///     // Afterwards, move the robot 50 pixels backward
        ///     Ahead(-50);
        ///     </code>
        ///   </example>
        /// </summary>
        /// <param name="distance">
        ///   The distance to move ahead measured in pixels.
        ///   If this value is negative, the robot will move back instead of ahead.
        /// </param>
        /// <seealso cref="Back(double)" />
        /// <seealso cref="OnHitWall(HitWallEvent)" />
        /// <seealso cref="OnHitRobot(HitRobotEvent)" />
        public void Ahead(double distance)
        {
            if (peer != null)
            {
                peer.Move(distance);
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
        ///     <code>
        ///     // Move the robot 100 pixels backward
        ///     Back(100);
        ///
        ///     // Afterwards, move the robot 50 pixels forward
        ///     Back(-50);
        ///     </code>
        ///   </example>
        ///   <seealso cref="Ahead(double)" />
        ///   <seealso cref="OnHitWall(HitWallEvent)" />
        ///   <seealso cref="OnHitRobot(HitRobotEvent)" />
        /// </summary>
        /// <param name="distance">
        ///   The distance to move back measured in pixels.
        ///   If this value is negative, the robot will move ahead instead of back.
        /// </param>
        public void Back(double distance)
        {
            if (peer != null)
            {
                peer.Move(-distance);
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
                    return peer.GetBattleFieldWidth();
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
                    return peer.GetBattleFieldHeight();
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
                    double rv = 180.0*peer.GetBodyHeading()/Math.PI;

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
                    return peer.GetName();
                }
                UninitializedException();
                return null; // never called
            }
        }

        /// <summary>
        ///   Returns the X position of the robot. (0,0) is at the bottom left of the battlefield.
        ///   <seealso cref="Y" />
        /// </summary>
        public double X
        {
            get
            {
                if (peer != null)
                {
                    return peer.GetX();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the Y position of the robot. (0,0) is at the bottom left of the battlefield.
        ///   <seealso cref="X" />
        /// </summary>
        public double Y
        {
            get
            {
                if (peer != null)
                {
                    return peer.GetY();
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
        ///     <code>
        ///     // A basic robot that moves around in a square
        ///     public void Run()
        ///     {
        ///         while (true)
        ///         {
        ///             Ahead(100);
        ///             TurnRight(90);
        ///         }
        ///     }
        ///     </code>
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
        ///     <code>
        ///       // Turn the robot 180 degrees to the left
        ///       TurnLeft(180);
        ///
        ///       // Afterwards, turn the robot 90 degrees to the right
        ///       TurnLeft(-90);
        ///     </code>
        ///   </example>
        ///   <seealso cref="TurnRight(double)" />
        ///   <seealso cref="TurnGunLeft(double)" />
        ///   <seealso cref="TurnGunRight(double)" />
        ///   <seealso cref="TurnRadarLeft(double)" />
        ///   <seealso cref="TurnRadarRight(double)" />
        /// </summary>
        /// <param name="degrees">
        ///   The amount of degrees to turn the robot's body to the left.
        ///   If degrees &gt; 0 the robot will turn left.
        ///   If degrees &lt; 0 the robot will turn right.
        ///   If degrees = 0 the robot will not turn, but execute.
        /// </param>
        public void TurnLeft(double degrees)
        {
            if (peer != null)
            {
                peer.TurnBody(-Utils.ToRadians(degrees));
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
        ///    <code>
        ///    // Turn the robot 180 degrees to the right
        ///    TurnRight(180);
        ///
        ///    // Afterwards, turn the robot 90 degrees to the left
        ///    TurnRight(-90);
        ///    </code>
        ///  </example>
        ///
        ///  <seealso cref="TurnLeft(double)" />
        ///  <seealso cref="TurnGunLeft(double)" />
        ///  <seealso cref="TurnGunRight(double)" />
        ///  <seealso cref="TurnRadarLeft(double)" />
        ///  <seealso cref="TurnRadarRight(double)" />
        ///</summary>
        ///<param name="degrees">
        ///  The amount of degrees to turn the robot's body to the right.
        ///  If degrees &gt; 0 the robot will turn right.
        ///  If degrees &lt; 0 the robot will turn left.
        ///  If degrees = 0 the robot will not turn, but execute.
        ///</param>
        public void TurnRight(double degrees)
        {
            if (peer != null)
            {
                peer.TurnBody(Utils.ToRadians(degrees));
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Do nothing this turn, meaning that the robot will skip it's turn.
        ///   <p />
        ///   This call executes immediately, and does not return until the turn is over.
        /// </summary>
        public void DoNothing()
        {
            if (peer != null)
            {
                peer.Execute();
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
        ///   You will get (3 * power) back if you hit the other robot. You can call
        ///   <see cref="Rules.GetBulletDamage(double)" />
        ///   for getting the damage that a
        ///   bullet with a specific bullet power will do.
        ///   <p />
        ///   The specified bullet power should be between <see cref="Rules.MIN_BULLET_POWER" />
        ///   and <see cref="Rules.MAX_BULLET_POWER" />.
        ///   <p />
        ///   Note that the gun cannot Fire if the gun is overheated, meaning that
        ///   <see cref="GunHeat" />
        ///   returns a value &gt; 0.
        ///   <p />
        ///   A event is generated when the bullet hits a robot (<see cref="BulletHitEvent" />),
        ///   wall (<see cref="BulletMissedEvent" />), or another bullet
        ///   (<see cref="BulletHitBulletEvent" />).
        ///   <p />
        ///   <example>
        ///     <code>
        ///     // Fire a bullet with maximum power if the gun is ready
        ///     if (GetGunHeat() == 0)
        ///     {
        ///         Fire(Rules.MAX_BULLET_POWER);
        ///     }
        ///     </code>
        ///   </example>
        ///   <seealso cref="FireBullet(double)" />
        ///   <seealso cref="GunHeat" />
        ///   <seealso cref="GunCoolingRate" />
        ///   <seealso cref="OnBulletHit(BulletHitEvent)" />
        ///   <seealso cref="OnBulletHitBullet(BulletHitBulletEvent)" />
        ///   <seealso cref="OnBulletMissed(BulletMissedEvent)" />
        /// </summary>
        /// <param name="power">
        ///   The amount of energy given to the bullet, and subtracted from the robot's energy.
        /// </param>
        public void Fire(double power)
        {
            if (peer != null)
            {
                peer.SetFire(power);
                peer.Execute();
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
        ///  You will get (3 * power) back if you hit the other robot. You can call
        ///  <see cref="Rules.GetBulletDamage(double)" />
        ///  for getting the damage that a
        ///  bullet with a specific bullet power will do.
        ///  <p />
        ///  The specified bullet power should be between <see cref="Rules.MIN_BULLET_POWER" />
        ///  and <see cref="Rules.MAX_BULLET_POWER" />.
        ///  <p />
        ///  Note that the gun cannot Fire if the gun is overheated, meaning that
        ///  <see cref="GunHeat" /> returns a value &gt; 0.
        ///  <p />
        ///  A event is generated when the bullet hits a robot (<see cref="BulletHitEvent" />),
        ///  wall (<see cref="BulletMissedEvent" />), or another bullet
        ///  (<see cref="BulletHitBulletEvent" />).
        ///  <p />
        ///  <example>
        ///    <code>
        ///    // Fire a bullet with maximum power if the gun is ready
        ///    if (GetGunHeat() == 0)
        ///    {
        ///        Bullet bullet = FireBullet(Rules.MAX_BULLET_POWER);
        ///
        ///        // Get the velocity of the bullet
        ///        if (bullet != null)
        ///        {
        ///            double bulletVelocity = bullet.Velocity;
        ///        }
        ///    }
        ///    </code>
        ///  </example>
        ///  Returns a
        ///  <see cref="Bullet" />
        ///  That contains information about the bullet if it  was actually fired,
        ///  which can be used for tracking the bullet after it has been fired.
        ///  If the bullet was not fired, null is returned.
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
                return peer.Fire(power);
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
                    return peer.GetGunCoolingRate();
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
                    return peer.GetGunHeading()*180.0/Math.PI;
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
        ///   The amount of gun heat generated when the gun is fired is 1 + (firePower / 5).
        ///   Each turn the gun heat drops by the amount returned   by <see cref="GunCoolingRate" />,
        ///   which is a battle setup.
        ///   <p />
        ///   Note that all guns are "hot" at the start of each round, where the gun heat is 3.
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
                    return peer.GetGunHeat();
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
                    return peer.GetNumRounds();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        ///<summary>
        ///  Returns the sentry border size for a <see cref="Robocode.BorderSentry">BorderSentry</see> that defines the how
        ///  far a BorderSentry is allowed to move from the border edges measured in units.<br/>
        ///  Hence, the sentry border size defines the width/range of the border area surrounding the battlefield that
        ///  BorderSentrys cannot leave (sentry robots robots must stay in the border area), but it also define the
        ///  distance from the border edges where BorderSentrys are allowed/able to make damage to robots entering this
        ///  border area.
        ///</summary>
        public int SentryBorderSize
        {
            get {
                if (peer != null)
                {
                    return peer.GetSentryBorderSize();
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
                    return peer.GetOthers();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns how many sentry robots that are left in the current round.
        /// </summary>
        public int NumSentries
        {
            get
            {
                if (peer != null)
                {
                    return peer.GetNumSentries();
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
                    return peer.GetRadarHeading()*180.0/Math.PI;
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the current round number (0 to <see cref="NumRounds" /> - 1) of the battle.
        ///   <seealso cref="NumRounds" />
        /// </summary>
        public int RoundNum
        {
            get
            {
                if (peer != null)
                {
                    return peer.GetRoundNum();
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
                    return peer.GetTime();
                }
                UninitializedException();
                return 0; // never called
            }
        }

        /// <summary>
        ///   Returns the velocity of the robot measured in pixels/turn.
        ///   <p />
        ///   The maximum velocity of a robot is defined by <see cref="Rules.MAX_VELOCITY" />
        ///   (8 pixels / turn).
        ///   <seealso cref="Rules.MAX_VELOCITY" />
        /// </summary>
        public double Velocity
        {
            get
            {
                if (peer != null)
                {
                    return peer.GetVelocity();
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
        ///  Scan will cause <see cref="OnScannedRobot(ScannedRobotEvent)" />
        ///  to be called if you see a robot.
        ///  <p />
        ///  There are 2 reasons to call <see cref="Scan()" /> manually:
        ///  <ol>
        ///    <li>
        ///      You want to scan after you stop moving.
        ///    </li>
        ///    <li>
        ///      You want to interrupt the <see cref="OnScannedRobot(ScannedRobotEvent)" />
        ///      event. This is more likely. If you are in
        ///      <see cref="OnScannedRobot(ScannedRobotEvent)" /> and call
        ///      <see cref="Scan()" />, and you still see a robot, then the system will interrupt your
        ///      <see cref="OnScannedRobot(ScannedRobotEvent)" />  event immediately and start it
        ///      from the top.
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
                peer.Rescan();
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
        ///   <see cref="IsAdjustGunForRobotTurn" />.
        ///   When this is set, the gun will turn independent from the robot's turn,
        ///   i.e. the gun will compensate for the robot's body turn.
        ///   <p />
        ///   Note: This method is additive until you reach the maximum the gun can
        ///   turn. The "adjust" is added to the amount you set for turning the robot,
        ///   then capped by the physics of the game. If you turn infinite, then the
        ///   adjust is ignored (and hence overridden).
        ///   <p />
        /// <example>
        ///   Assuming both the robot and gun start Out facing up (0 degrees):
        ///   <code>
        ///   // Set gun to turn with the robot's turn
        ///   SetAdjustGunForRobotTurn(false); // This is the default
        ///   TurnRight(90);
        ///   // At this point, both the robot and gun are facing right (90 degrees)
        ///   TurnLeft(90);
        ///   // Both are back to 0 degrees
        ///   </code>
        ///   -- or --
        ///   <code>
        ///   // Set gun to turn independent from the robot's turn
        ///   SetAdjustGunForRobotTurn(true);
        ///   TurnRight(90);
        ///   // At this point, the robot is facing right (90 degrees), but the gun is still facing up.
        ///   TurnLeft(90);
        ///   // Both are back to 0 degrees.
        ///   </code>
        /// </example>
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
                    return ((IStandardRobotPeer) peer).IsAdjustGunForBodyTurn();
                }
                UninitializedException();
                return false;
            }
            set
            {
                if (peer != null)
                {
                    ((IStandardRobotPeer) peer).SetAdjustGunForBodyTurn(value);
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
        ///   this, you can call <see cref="IsAdjustRadarForRobotTurn" /> = true.
        ///   When this is set, the radar will turn independent from the robot's turn,
        ///   i.e. the radar will compensate for the robot's turn.
        ///   <p />
        ///   Note: This method is additive until you reach the maximum the radar can
        ///   turn. The "adjust" is added to the amount you set for turning the robot,
        ///   then capped by the physics of the game. If you turn infinite, then the
        ///   adjust is ignored (and hence overridden).
        ///   <p />
        /// <example>
        ///   Assuming the robot, gun, and radar all start Out facing up (0
        ///   degrees):
        ///   <code>
        ///   // Set radar to turn with the robots's turn
        ///   SetAdjustRadarForRobotTurn(false); // This is the default
        ///   TurnRight(90);
        ///   // At this point, the body, gun, and radar are all facing right (90 degrees);
        ///   </code>
        ///   -- or --
        ///   <code>
        ///   // Set radar to turn independent from the robot's turn
        ///   SetAdjustRadarForRobotTurn(true);
        ///   TurnRight(90);
        ///   // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
        ///   </code>
        /// </example>
        ///   <seealso cref="IsAdjustGunForRobotTurn" />
        ///   <seealso cref="IsAdjustRadarForGunTurn" />
        /// </summary>
        public bool IsAdjustRadarForRobotTurn
        {
            get
            {
                if (peer != null)
                {
                    return ((IStandardRobotPeer) peer).IsAdjustRadarForBodyTurn();
                }
                UninitializedException();
                return false;
            }
            set
            {
                if (peer != null)
                {
                    ((IStandardRobotPeer) peer).SetAdjustRadarForBodyTurn(value);
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
        ///   for this, you can call <see cref="IsAdjustRadarForGunTurn" /> = (true).
        ///   When this is set, the radar will turn independent from the robot's turn,
        ///   i.e. the radar will compensate for the gun's turn.
        ///   <p />
        ///   Note: This method is additive until you reach the maximum the radar can
        ///   turn. The "adjust" is added to the amount you set for turning the gun,
        ///   then capped by the physics of the game. If you turn infinite, then the
        ///   adjust is ignored (and hence overridden).
        ///   <p />
        /// <example>
        ///   Assuming both the gun and radar start Out facing up (0 degrees):
        ///   <code>
        ///   // Set radar to turn with the gun's turn
        ///   SetAdjustRadarForGunTurn(false); // This is the default
        ///   TurnGunRight(90);
        ///   // At this point, both the radar and gun are facing right (90 degrees);
        ///   </code>
        ///   -- or --
        ///   <code>
        ///   // Set radar to turn independent from the gun's turn
        ///   SetAdjustRadarForGunTurn(true);
        ///   TurnGunRight(90);
        ///   // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
        ///   </code>
        /// </example>
        ///   Note: Calling <see cref="IsAdjustRadarForGunTurn" /> will automatically call
        ///   <see cref="IsAdjustRadarForRobotTurn" /> with the same value, unless you have
        ///   already called it earlier. This behavior is primarily for backward compatibility
        ///   with older Robocode robots.
        ///   <seealso cref="IsAdjustRadarForRobotTurn" />
        ///   <seealso cref="IsAdjustGunForRobotTurn" />
        /// </summary>
        public bool IsAdjustRadarForGunTurn
        {
            get
            {
                if (peer != null)
                {
                    return ((IStandardRobotPeer) peer).IsAdjustRadarForGunTurn();
                }
                UninitializedException();
                return false;
            }
            set
            {
                if (peer != null)
                {
                    ((IStandardRobotPeer) peer).SetAdjustRadarForGunTurn(value);
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
        ///   You may only call this method one time per battle. A <em>null</em>
        ///   indicates the default (blue) color.
        ///   <p />
        ///   <example>
        ///     <code>
        ///     // Don't forget to using System.Drawing at the top...
        ///     using System.Drawing;
        ///     ...
        ///
        ///     public void Run()
        ///     {
        ///         SetColors(null, Color.Red, Color.fromArgb(150, 0, 150));
        ///         ...
        ///     }
        ///     </code>
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
        /// <param name="bodyColor">The new body color</param>
        /// <param name="gunColor">The new gun color</param>
        /// <param name="radarColor">The new radar color</param>
        public void SetColors(Color bodyColor, Color gunColor, Color radarColor)
        {
            if (peer != null)
            {
                peer.SetBodyColor(bodyColor);
                peer.SetGunColor(gunColor);
                peer.SetRadarColor(radarColor);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Sets the color of the robot's body, gun, radar, bullet, and scan arc in
        ///   the same time.
        ///   <p />
        ///   You may only call this method one time per battle. A <em>null</em>
        ///   indicates the default (blue) color for the body, gun, radar, and scan
        ///   arc, but white for the bullet color.
        ///   <p />
        ///   <example>
        ///     <code>
        ///     // Don't forget to using System.Drawing at the top...
        ///     using System.Drawing;
        ///     ...
        ///
        ///     public void Run()
        ///     {
        ///         SetColors(null, Color.Red, Color.Greeen, null, Color.fromArgb(150, 0, 150));
        ///         ...
        ///     }
        ///     </code>
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
        /// <param name="bodyColor">The new body color</param>
        /// <param name="gunColor">The new gun color</param>
        /// <param name="radarColor">The new radar color</param>
        /// <param name="bulletColor">The new bullet color</param>
        /// <param name="scanArcColor">The new scan arc color</param>
        public void SetColors(Color bodyColor, Color gunColor, Color radarColor, Color bulletColor, Color scanArcColor)
        {
            if (peer != null)
            {
                peer.SetBodyColor(bodyColor);
                peer.SetGunColor(gunColor);
                peer.SetRadarColor(radarColor);
                peer.SetBulletColor(bulletColor);
                peer.SetScanColor(scanArcColor);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Sets all the robot's color to the same color in the same time, i.e. the
        ///   color of the body, gun, radar, bullet, and scan arc.
        ///   <p />
        ///   You may only call this method one time per battle. A <em>null</em>
        ///   indicates the default (blue) color for the body, gun, radar, and scan
        ///   arc, but white for the bullet color.
        ///   <p />
        ///   <example>
        ///     <code>
        ///     // Don't forget to using System.Drawing at the top...
        ///     using System.Drawing;
        ///     ...
        ///
        ///     public void Run()
        ///     {
        ///         SetAllColors(Color.Red);
        ///         ...
        ///     }
        ///     </code>
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
        /// <param name="color">The new color for all the colors of the robot</param>
        public void SetAllColors(Color color)
        {
            if (peer != null)
            {
                peer.SetBodyColor(color);
                peer.SetGunColor(color);
                peer.SetRadarColor(color);
                peer.SetBulletColor(color);
                peer.SetScanColor(color);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Sets the color of the robot's body.
        ///   <p />
        ///   A <em>null</em> indicates the default (blue) color.
        ///   <p />
        ///   <example>
        ///     <code>
        ///     // Don't forget to using System.Drawing at the top...
        ///     using System.Drawing;
        ///     ...
        ///
        ///     public void Run()
        ///     {
        ///         SetBodyColor(Color.Black);
        ///         ...
        ///     }
        ///     </code>
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
                    peer.SetBodyColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
            get
            {
                if (peer != null)
                {
                    return peer.GetBodyColor();
                }
                UninitializedException();
                return default(Color);
            }
        }

        /// 
        ///<summary>
        ///  Sets the color of the robot's gun.
        ///  <p />
        ///  A <em>null</em> indicates the default (blue) color.
        ///  <p />
        ///  <example>
        ///    <code>
        ///    // Don't forget to using System.Drawing at the top...
        ///    using System.Drawing;
        ///    ...
        ///
        ///    public void Run()
        ///    {
        ///        SetGunColor(Color.Red);
        ///        ...
        ///    }
        ///    </code>
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
                    peer.SetGunColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
            get
            {
                if (peer != null)
                {
                    return peer.GetGunColor();
                }
                UninitializedException();
                return default(Color);
            }
        }

        /// 
        ///<summary>
        ///  Sets the color of the robot's radar.
        ///  <p />
        ///  A <em>null</em> indicates the default (blue) color.
        ///  <p />
        ///  <example>
        ///    <code>
        ///    // Don't forget to using System.Drawing at the top...
        ///    using System.Drawing;
        ///    ...
        ///
        ///    public void Run()
        ///    {
        ///        SetRadarColor(Color.Yellow);
        ///        ...
        ///    }
        ///    </code>
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
                    peer.SetRadarColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
            get
            {
                if (peer != null)
                {
                    return peer.GetRadarColor();
                }
                UninitializedException();
                return default(Color);
            }
        }

        /// 
        ///<summary>
        ///  Sets the color of the robot's bullets.
        ///  <p />
        ///  A <em>null</em> indicates the default white color.
        ///  <p />
        ///  <example>
        ///    <code>
        ///    // Don't forget to using System.Drawing at the top...
        ///    using System.Drawing;
        ///    ...
        ///
        ///    public void Run()
        ///    {
        ///        SetBulletColor(Color.Green);
        ///        ...
        ///    }
        ///    </code>
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
                    peer.SetBulletColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
            get
            {
                if (peer != null)
                {
                    return peer.GetBulletColor();
                }
                UninitializedException();
                return default(Color);
            }
        }

        /// 
        ///<summary>
        ///  Sets the color of the robot's scan arc.
        ///  <p />
        ///  A <em>null</em> indicates the default (blue) color.
        ///  <p />
        ///  <example>
        ///    <code>
        ///    // Don't forget to using System.Drawing at the top...
        ///    using System.Drawing;
        ///    ...
        ///
        ///    public void Run()
        ///    {
        ///        SetScanColor(Color.White);
        ///        ...
        ///    }
        ///    </code>
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
                    peer.SetScanColor(value);
                }
                else
                {
                    UninitializedException();
                }
            }
            get
            {
                if (peer != null)
                {
                    return peer.GetScanColor();
                }
                UninitializedException();
                return default(Color);
            }
        }

        /// 
        ///<summary>
        ///  Immediately stops all movement, and saves it for a call to <see cref="Resume()" />.
        ///  If there is already movement saved from a previous stop, this will have no effect.
        ///  <p />
        ///  This method is equivalent to <see cref="Stop(bool)">Stop(false)</see>.
        ///  <seealso cref="Resume()" />
        ///  <seealso cref="Stop(bool)" />
        ///</summary>
        public void Stop()
        {
            Stop(false);
        }

        /// <summary>
        ///   Immediately stops all movement, and saves it for a call to <see cref="Resume()" />.
        ///   If there is already movement saved from a previous stop, you can overwrite it by
        ///   calling <see cref="Stop(bool)">Stop(true)</see>.
        ///   <seealso cref="Resume()" />
        ///   <seealso cref="Stop()" />
        /// </summary>
        /// <param name="overwrite">
        ///   If there is already movement saved from a previous stop, you can overwrite it by
        ///   calling <see cref="Stop(bool)">Stop(true)</see>.
        /// </param>
        public void Stop(bool overwrite)
        {
            if (peer != null)
            {
                ((IStandardRobotPeer) peer).Stop(overwrite);
            }
            else
            {
                UninitializedException();
            }
        }

        /// <summary>
        ///   Immediately resumes the movement you stopped by <see cref="Stop()" />, if any.
        ///   <p />
        ///   This call executes immediately, and does not return until it is complete.
        ///   <seealso cref="Stop()" />
        ///   <seealso cref="Stop(bool)" />
        /// </summary>
        public void Resume()
        {
            if (peer != null)
            {
                ((IStandardRobotPeer) peer).Resume();
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
        ///    <code>
        ///    // Turn the robot's gun 180 degrees to the left
        ///    TurnGunLeft(180);
        ///
        ///    // Afterwards, turn the robot's gun 90 degrees to the right
        ///    TurnGunLeft(-90);
        ///    </code>
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
        ///  The amount of degrees to turn the robot's gun to the left.
        ///  If degrees &gt; 0 the robot's gun will turn left.
        ///  If degrees &lt; 0 the robot's gun will turn right.
        ///  If degrees = 0 the robot's gun will not turn, but execute.
        ///</param>
        public void TurnGunLeft(double degrees)
        {
            if (peer != null)
            {
                peer.TurnGun(-Utils.ToRadians(degrees));
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
        ///    <code>
        ///    // Turn the robot's gun 180 degrees to the right
        ///    TurnGunRight(180);
        ///
        ///    // Afterwards, turn the robot's gun 90 degrees to the left
        ///    TurnGunRight(-90);
        ///    </code>
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
        ///  The amount of degrees to turn the robot's gun to the right.
        ///  If degrees &gt; 0 the robot's gun will turn right.
        ///  If degrees &lt; 0 the robot's gun will turn left.
        ///  If degrees = 0 the robot's gun will not turn, but execute.
        ///</param>
        public void TurnGunRight(double degrees)
        {
            if (peer != null)
            {
                peer.TurnGun(Utils.ToRadians(degrees));
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
        ///     <code>
        ///     // Turn the robot's radar 180 degrees to the left
        ///     TurnRadarLeft(180);
        ///
        ///     // Afterwards, turn the robot's radar 90 degrees to the right
        ///     TurnRadarLeft(-90);
        ///     </code>
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
        ///   The amount of degrees to turn the robot's radar to the left.
        ///   If degrees &gt; 0 the robot's radar will turn left.
        ///   If degrees &lt; 0 the robot's radar will turn right.
        ///   If degrees = 0 the robot's radar will not turn, but execute.
        /// </param>
        public void TurnRadarLeft(double degrees)
        {
            if (peer != null)
            {
                ((IStandardRobotPeer) peer).TurnRadar(-Utils.ToRadians(degrees));
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
        ///    <code>
        ///    // Turn the robot's radar 180 degrees to the right
        ///    TurnRadarRight(180);
        ///
        ///    // Afterwards, turn the robot's radar 90 degrees to the left
        ///    TurnRadarRight(-90);
        ///    </code>
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
        ///  The amount of degrees to turn the robot's radar to the right.
        ///  If degrees &gt; 0 the robot's radar will turn right.
        ///  If degrees &lt; 0 the robot's radar will turn left.
        ///  If degrees = 0 the robot's radar will not turn, but execute.
        ///</param>
        public void TurnRadarRight(double degrees)
        {
            if (peer != null)
            {
                ((IStandardRobotPeer) peer).TurnRadar(Utils.ToRadians(degrees));
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
                    return peer.GetEnergy();
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
                    return peer.GetGraphics();
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
            ///   The name/key of the debug property
            /// </param>
            /// <returns></returns>
            public string this[string key]
            {
                set { peer.SetDebugProperty(key, value); }
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