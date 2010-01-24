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
using robocode.exception;
using robocode.net.sf.robocode.security;
using robocode.robotinterfaces;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace robocode
{
    /// <summary>
    /// The basic robot class that you will extend to create your own robots.
    /// <p/>
    /// <p/>Please note the following standards will be used:
    /// <br/> heading - absolute angle in degrees with 0 facing up the screen,
    /// positive clockwise. 0 &lt;= heading &lt; 360.
    /// <br/> bearing - relative angle to some obj from your robot's heading,
    /// positive clockwise. -180 &lt; bearing &lt;= 180
    /// <br/> All coordinates are expressed as (x,y).
    /// <br/> All coordinates are positive.
    /// <br/> The origin (0,0) is at the bottom left of the screen.
    /// <br/> Positive x is right.
    /// <br/> Positive y is up.
    ///
    /// @author Mathew A. Nelson (original)
    /// @author Flemming N. Larsen (contributor)
    /// @author Matthew Reeder (contributor)
    /// @author Stefan Westen (contributor)
    /// @author Pavel Savara (contributor)
    /// @see <a target="_top" href="http://robocode.sourceforge.net">
    ///      robocode.sourceforge.net</a>
    /// @see <a href="http://robocode.sourceforge.net/myfirstrobot/MyFirstRobot.html">
    ///      Building your first robot</a>
    /// @see JuniorRobot
    /// @see AdvancedRobot
    /// @see TeamRobot
    /// @see Droid
    /// </summary>
    public abstract class Robot : IInteractiveRobot, IPaintRobot, IBasicEvents3, IInteractiveEvents,
                                  IPaintEvents, IRunnable
    {
        private const int
            WIDTH = 40,
            HEIGHT = 40;

        internal IBasicRobotPeer peer;
        internal TextWriter _output;

        /// <summary>
        /// The Out stream your robot should use to print.
        /// <p/>
        /// You can view the print-outs by clicking the button for your robot in the
        /// right side of the battle window.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Print Out a line each time my robot hits another robot
        ///   public void OnHitRobot(HitRobotEvent e) {
        ///       Out.println("I hit a robot!  My energy: " + getEnergy() + " his energy: " + e.getEnergy());
        ///   }
        /// </pre>
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
        /// Throws a RobotException. This method should be called when the robot's peer
        /// is uninitialized.
        /// </summary>
        internal static void UninitializedException()
        {
            throw new RobotException(
                "You cannot call the methods before your Run() method is called, or you are using a Robot object that the game doesn't know about.");
        }

        /// <summary>
        /// {@inheritDoc}}
        /// </summary>
        IRunnable IBasicRobot.GetRobotRunnable()
        {
            return this;
        }

        /// <summary>
        /// {@inheritDoc}}
        /// </summary>
        IBasicEvents IBasicRobot.GetBasicEventListener()
        {
            return this;
        }

        /// <summary>
        /// {@inheritDoc}}
        /// </summary>
        IInteractiveEvents IInteractiveRobot.GetInteractiveEventListener()
        {
            return this;
        }

        /// <summary>
        /// {@inheritDoc}}
        /// </summary>
        IPaintEvents IPaintRobot.GetPaintEventListener()
        {
            return this;
        }

        /// <summary>
        /// Immediately moves your robot Ahead (forward) by distance measured in
        /// pixels.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the remaining distance to move is 0.
        /// <p/>
        /// If the robot collides with a wall, the move is complete, meaning that the
        /// robot will not move any further. If the robot collides with another
        /// robot, the move is complete if you are heading toward the other robot.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot is set to move backward
        /// instead of forward.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Move the robot 100 pixels forward
        ///   Ahead(100);
        /// <p/>
        ///   // Afterwards, move the robot 50 pixels backward
        ///   Ahead(-50);
        /// </pre>
        ///
        /// @param distance the distance to move Ahead measured in pixels.
        ///                 If this value is negative, the robot will move Back instead of Ahead.
        /// @see #Back(double)
        /// @see #OnHitWall(HitWallEvent)
        /// @see #OnHitRobot(HitRobotEvent)
        /// </summary>
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
        /// Immediately moves your robot backward by distance measured in pixels.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the remaining distance to move is 0.
        /// <p/>
        /// If the robot collides with a wall, the move is complete, meaning that the
        /// robot will not move any further. If the robot collides with another
        /// robot, the move is complete if you are heading toward the other robot.
        /// <p/>
        /// Note that both positive and negative values can be given as input,
        /// where negative values means that the robot is set to move forward instead
        /// of backward.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Move the robot 100 pixels backward
        ///   Back(100);
        /// <p/>
        ///   // Afterwards, move the robot 50 pixels forward
        ///   Back(-50);
        /// </pre>
        ///
        /// @param distance the distance to move Back measured in pixels.
        ///                 If this value is negative, the robot will move Ahead instead of Back.
        /// @see #Ahead(double)
        /// @see #OnHitWall(HitWallEvent)
        /// @see #OnHitRobot(HitRobotEvent)
        /// </summary>
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
        /// Returns the width of the current battlefield measured in pixels.
        ///
        /// @return the width of the current battlefield measured in pixels.
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
        /// Returns the height of the current battlefield measured in pixels.
        ///
        /// @return the height of the current battlefield measured in pixels.
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
        /// Returns the direction that the robot's body is facing, in degrees.
        /// The value returned will be between 0 and 360 (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// 90 means East, 180 means South, and 270 means West.
        ///
        /// @return the direction that the robot's body is facing, in degrees.
        /// @see #getGunHeading()
        /// @see #getRadarHeading()
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
        /// Returns the height of the robot measured in pixels.
        ///
        /// @return the height of the robot measured in pixels.
        /// @see #getWidth()
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
        /// Returns the width of the robot measured in pixels.
        ///
        /// @return the width of the robot measured in pixels.
        /// @see #getHeight()
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
        /// Returns the robot's name.
        ///
        /// @return the robot's name.
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
        /// Returns the X position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        ///
        /// @return the X position of the robot.
        /// @see #getY()
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
        /// Returns the Y position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        ///
        /// @return the Y position of the robot.
        /// @see #getX()
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
        /// The main method in every robot. You must override this to set up your
        /// robot's basic behavior.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // A basic robot that moves around in a square
        ///   public void Run() {
        ///       while (true) {
        ///           Ahead(100);
        ///           TurnRight(90);
        ///       }
        ///   }
        /// </pre>
        /// </summary>
        public virtual void Run()
        {
        }

        /// <summary>
        /// Immediately turns the robot's body to the left by degrees.
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
        ///   TurnLeft(180);
        /// <p/>
        ///   // Afterwards, turn the robot 90 degrees to the right
        ///   TurnLeft(-90);
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's body to the left.
        ///                If {@code degrees} &gt; 0 the robot will turn left.
        ///                If {@code degrees} &lt; 0 the robot will turn right.
        ///                If {@code degrees} = 0 the robot will not turn, but Execute.
        /// @see #TurnRight(double)
        /// @see #TurnGunLeft(double)
        /// @see #TurnGunRight(double)
        /// @see #TurnRadarLeft(double)
        /// @see #TurnRadarRight(double)
        /// </summary>
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

        /// <summary>
        /// Immediately turns the robot's body to the right by degrees.
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
        ///   TurnRight(180);
        /// <p/>
        ///   // Afterwards, turn the robot 90 degrees to the left
        ///   TurnRight(-90);
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's body to the right.
        ///                If {@code degrees} &gt; 0 the robot will turn right.
        ///                If {@code degrees} &lt; 0 the robot will turn left.
        ///                If {@code degrees} = 0 the robot will not turn, but Execute.
        /// @see #TurnLeft(double)
        /// @see #TurnGunLeft(double)
        /// @see #TurnGunRight(double)
        /// @see #TurnRadarLeft(double)
        /// @see #TurnRadarRight(double)
        /// </summary>
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
        /// Do nothing this turn, meaning that the robot will skip it's turn.
        /// <p/>
        /// This call executes immediately, and does not return until the turn is
        /// over.
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
        /// Immediately fires a bullet. The bullet will travel in the direction the
        /// gun is pointing.
        /// <p/>
        /// The specified bullet power is an amount of energy that will be taken from
        /// the robot's energy. Hence, the more power you want to spend on the
        /// bullet, the more energy is taken from your robot.
        /// <p/>
        /// The bullet will do (4 * power) damage if it hits another robot. If power
        /// is greater than 1, it will do an additional 2 * (power - 1) damage.
        /// You will get (3 * power) Back if you hit the other robot. You can call
        /// {@link Rules#GetBulletDamage(double)} for getting the damage that a
        /// bullet with a specific bullet power will do.
        /// <p/>
        /// The specified bullet power should be between
        /// {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
        /// <p/>
        /// Note that the gun cannot Fire if the gun is overheated, meaning that
        /// {@link #GetGunHeat()} returns a value > 0.
        /// <p/>
        /// A evnt is generated when the bullet hits a robot
        /// ({@link BulletHitEvent}), wall ({@link BulletMissedEvent}), or another
        /// bullet ({@link BulletHitBulletEvent}).
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Fire a bullet with maximum power if the gun is ready
        ///   if (GetGunHeat() == 0) {
        ///       Fire(Rules.MAX_BULLET_POWER);
        ///   }
        /// </pre>
        ///
        /// @param power the amount of energy given to the bullet, and subtracted
        ///              from the robot's energy.
        /// @see #FireBullet(double)
        /// @see #GetGunHeat()
        /// @see #getGunCoolingRate()
        /// @see #OnBulletHit(BulletHitEvent)
        /// @see #OnBulletHitBullet(BulletHitBulletEvent)
        /// @see #OnBulletMissed(BulletMissedEvent)
        /// </summary>
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

        /// <summary>
        /// Immediately fires a bullet. The bullet will travel in the direction the
        /// gun is pointing.
        /// <p/>
        /// The specified bullet power is an amount of energy that will be taken from
        /// the robot's energy. Hence, the more power you want to spend on the
        /// bullet, the more energy is taken from your robot.
        /// <p/>
        /// The bullet will do (4 * power) damage if it hits another robot. If power
        /// is greater than 1, it will do an additional 2 * (power - 1) damage.
        /// You will get (3 * power) Back if you hit the other robot. You can call
        /// {@link Rules#GetBulletDamage(double)} for getting the damage that a
        /// bullet with a specific bullet power will do.
        /// <p/>
        /// The specified bullet power should be between
        /// {@link Rules#MIN_BULLET_POWER} and {@link Rules#MAX_BULLET_POWER}.
        /// <p/>
        /// Note that the gun cannot Fire if the gun is overheated, meaning that
        /// {@link #GetGunHeat()} returns a value > 0.
        /// <p/>
        /// A evnt is generated when the bullet hits a robot
        /// ({@link BulletHitEvent}), wall ({@link BulletMissedEvent}), or another
        /// bullet ({@link BulletHitBulletEvent}).
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Fire a bullet with maximum power if the gun is ready
        ///   if (GetGunHeat() == 0) {
        ///       Bullet bullet = FireBullet(Rules.MAX_BULLET_POWER);
        /// <p/>
        ///       // Get the velocity of the bullet
        ///       if (bullet != null) {
        ///           double bulletVelocity = bullet.getVelocity();
        ///       }
        ///   }
        /// </pre>
        ///
        /// @param power the amount of energy given to the bullet, and subtracted
        ///              from the robot's energy.
        /// @return a {@link Bullet} that contains information about the bullet if it
        ///         was actually fired, which can be used for tracking the bullet after it
        ///         has been fired. If the bullet was not fired, {@code null} is returned.
        /// @see #Fire(double)
        /// @see Bullet
        /// @see #GetGunHeat()
        /// @see #getGunCoolingRate()
        /// @see #OnBulletHit(BulletHitEvent)
        /// @see #OnBulletHitBullet(BulletHitBulletEvent)
        /// @see #OnBulletMissed(BulletMissedEvent)
        /// </summary>
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
        /// Returns the rate at which the gun will cool down, i.e. the amount of heat
        /// the gun heat will drop per turn.
        /// <p/>
        /// The gun cooling rate is default 0.1 / turn, but can be changed by the
        /// battle setup. So don't count on the cooling rate being 0.1!
        ///
        /// @return the gun cooling rate
        /// @see #GetGunHeat()
        /// @see #Fire(double)
        /// @see #FireBullet(double)
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
        /// Returns the direction that the robot's gun is facing, in degrees.
        /// The value returned will be between 0 and 360 (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// 90 means East, 180 means South, and 270 means West.
        ///
        /// @return the direction that the robot's gun is facing, in degrees.
        /// @see #getHeading()
        /// @see #getRadarHeading()
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
        /// Returns the current heat of the gun. The gun cannot Fire unless this is
        /// 0. (Calls to Fire will succeed, but will not actually Fire unless
        /// GetGunHeat() == 0).
        /// <p/>
        /// The amount of gun heat generated when the gun is fired is
        /// 1 + (firePower / 5). Each turn the gun heat drops by the amount returned
        /// by {@link #getGunCoolingRate()}, which is a battle setup.
        /// <p/>
        /// Note that all guns are "hot" at the start of each round, where the gun
        /// heat is 3.
        ///
        /// @return the current gun heat
        /// @see #getGunCoolingRate()
        /// @see #Fire(double)
        /// @see #FireBullet(double)
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
        /// Returns the number of rounds in the current battle.
        ///
        /// @return the number of rounds in the current battle
        /// @see #getRoundNum()
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
        /// Returns how many opponents that are left in the current round.
        ///
        /// @return how many opponents that are left in the current round.
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
        /// Returns the direction that the robot's radar is facing, in degrees.
        /// The value returned will be between 0 and 360 (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// 90 means East, 180 means South, and 270 means West.
        ///
        /// @return the direction that the robot's radar is facing, in degrees.
        /// @see #getHeading()
        /// @see #getGunHeading()
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
        /// Returns the current round number (0 to {@link #getNumRounds()} - 1) of
        /// the battle.
        ///
        /// @return the current round number of the battle
        /// @see #getNumRounds()
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
        /// Returns the game time of the current round, where the time is equal to
        /// the current turn in the round.
        /// <p/>
        /// A battle consists of multiple rounds.
        /// <p/>
        /// Time is reset to 0 at the beginning of every round.
        ///
        /// @return the game time/turn of the current round.
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
        /// Returns the velocity of the robot measured in pixels/turn.
        /// <p/>
        /// The maximum velocity of a robot is defined by {@link Rules#MAX_VELOCITY}
        /// (8 pixels / turn).
        ///
        /// @return the velocity of the robot measured in pixels/turn.
        /// @see Rules#MAX_VELOCITY
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

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnBulletHit(BulletHitEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnBulletHitBullet(BulletHitBulletEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnBulletMissed(BulletMissedEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnDeath(DeathEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnHitByBullet(HitByBulletEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnHitRobot(HitRobotEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnHitWall(HitWallEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnRobotDeath(RobotDeathEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnScannedRobot(ScannedRobotEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnWin(WinEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnRoundEnded(RoundEndedEvent evnt)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnBattleEnded(BattleEndedEvent evnt)
        {
        }

        /// <summary>
        /// Scans for other robots. This method is called automatically by the game,
        /// as long as the robot is moving, turning its body, turning its gun, or
        /// turning its radar.
        /// <p/>
        /// Scan will cause {@link #OnScannedRobot(ScannedRobotEvent)
        /// OnScannedRobot(ScannedRobotEvent)} to be called if you see a robot.
        /// <p/>
        /// There are 2 reasons to call {@code Scan()} manually:
        /// <ol>
        /// <li>You want to Scan after you Stop moving.</li>
        /// <li>You want to interrupt the {@code OnScannedRobot} evnt. This is more
        /// likely. If you are in {@code OnScannedRobot} and call {@code Scan()},
        /// and you still see a robot, then the system will interrupt your
        /// {@code OnScannedRobot} evnt immediately and start it from the top.</li>
        /// </ol>
        /// <p/>
        /// This call executes immediately.
        ///
        /// @see #OnScannedRobot(ScannedRobotEvent)
        /// @see ScannedRobotEvent
        /// </summary>
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
        /// Sets the gun to turn independent from the robot's turn.
        /// <p/>
        /// Ok, so this needs some explanation: The gun is mounted on the robot's
        /// body. So, normally, if the robot turns 90 degrees to the right, then the
        /// gun will turn with it as it is mounted on top of the robot's body. To
        /// compensate for this, you can call {@code setAdjustGunForRobotTurn(true)}.
        /// When this is set, the gun will turn independent from the robot's turn,
        /// i.e. the gun will compensate for the robot's body turn.
        /// <p/>
        /// Note: This method is additive until you reach the maximum the gun can
        /// turn. The "adjust" is added to the amount you set for turning the robot,
        /// then capped by the physics of the game. If you turn infinite, then the
        /// adjust is ignored (and hence overridden).
        /// <p/>
        /// Example, assuming both the robot and gun start Out facing up (0 degrees):
        /// <pre>
        ///   // Set gun to turn with the robot's turn
        ///   setAdjustGunForRobotTurn(false); // This is the default
        ///   TurnRight(90);
        ///   // At this point, both the robot and gun are facing right (90 degrees)
        ///   TurnLeft(90);
        ///   // Both are Back to 0 degrees
        /// <p/>
        ///   -- or --
        /// <p/>
        ///   // Set gun to turn independent from the robot's turn
        ///   setAdjustGunForRobotTurn(true);
        ///   TurnRight(90);
        ///   // At this point, the robot is facing right (90 degrees), but the gun is still facing up.
        ///   TurnLeft(90);
        ///   // Both are Back to 0 degrees.
        /// </pre>
        /// <p/>
        /// Note: The gun compensating this way does count as "turning the gun".
        /// See {@link #setAdjustRadarForGunTurn(bool)} for details.
        ///
        /// @param independent {@code true} if the gun must turn independent from the
        ///                    robot's turn; {@code false} if the gun must turn with the robot's turn.
        /// @see #setAdjustRadarForGunTurn(bool)
        /// </summary>
        public virtual bool IsAdjustGunForRobotTurn
        {
            get
            {
                if (peer != null)
                {
                    return ((IStandardRobotPeer)peer).isAdjustGunForBodyTurn();
                }
                UninitializedException();
                return false;
            }
            set
            {
                if (peer != null)
                {
                    ((IStandardRobotPeer)peer).setAdjustGunForBodyTurn(value);
                }
                else
                {
                    UninitializedException();
                }
            }
        }

        /// <summary>
        /// Sets the radar to turn independent from the robot's turn.
        /// <p/>
        /// Ok, so this needs some explanation: The radar is mounted on the gun, and
        /// the gun is mounted on the robot's body. So, normally, if the robot turns
        /// 90 degrees to the right, the gun turns, as does the radar. Hence, if the
        /// robot turns 90 degrees to the right, then the gun and radar will turn
        /// with it as the radar is mounted on top of the gun. To compensate for
        /// this, you can call {@code setAdjustRadarForRobotTurn(true)}. When this is
        /// set, the radar will turn independent from the robot's turn, i.e. the
        /// radar will compensate for the robot's turn.
        /// <p/>
        /// Note: This method is additive until you reach the maximum the radar can
        /// turn. The "adjust" is added to the amount you set for turning the robot,
        /// then capped by the physics of the game. If you turn infinite, then the
        /// adjust is ignored (and hence overridden).
        /// <p/>
        /// Example, assuming the robot, gun, and radar all start Out facing up (0
        /// degrees):
        /// <pre>
        ///   // Set radar to turn with the robots's turn
        ///   setAdjustRadarForRobotTurn(false); // This is the default
        ///   TurnRight(90);
        ///   // At this point, the body, gun, and radar are all facing right (90 degrees);
        /// <p/>
        ///   -- or --
        /// <p/>
        ///   // Set radar to turn independent from the robot's turn
        ///   setAdjustRadarForRobotTurn(true);
        ///   TurnRight(90);
        ///   // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
        /// </pre>
        ///
        /// @param independent {@code true} if the radar must turn independent from
        ///                    the robots's turn; {@code false} if the radar must turn with the robot's
        ///                    turn.
        /// @see #setAdjustGunForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool)
        /// </summary>
        public bool IsAdjustRadarForRobotTurn
        {
            get
            {
                if (peer != null)
                {
                    return ((IStandardRobotPeer)peer).isAdjustRadarForBodyTurn();
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
        /// Sets the radar to turn independent from the gun's turn.
        /// <p/>
        /// Ok, so this needs some explanation: The radar is mounted on the robot's
        /// gun. So, normally, if the gun turns 90 degrees to the right, then the
        /// radar will turn with it as it is mounted on top of the gun. To compensate
        /// for this, you can call {@code setAdjustRadarForGunTurn(true)}. When this
        /// is set, the radar will turn independent from the robot's turn, i.e. the
        /// radar will compensate for the gun's turn.
        /// <p/>
        /// Note: This method is additive until you reach the maximum the radar can
        /// turn. The "adjust" is added to the amount you set for turning the gun,
        /// then capped by the physics of the game. If you turn infinite, then the
        /// adjust is ignored (and hence overridden).
        /// <p/>
        /// Example, assuming both the gun and radar start Out facing up (0 degrees):
        /// <pre>
        ///   // Set radar to turn with the gun's turn
        ///   setAdjustRadarForGunTurn(false); // This is the default
        ///   TurnGunRight(90);
        ///   // At this point, both the radar and gun are facing right (90 degrees);
        /// <p/>
        ///   -- or --
        /// <p/>
        ///   // Set radar to turn independent from the gun's turn
        ///   setAdjustRadarForGunTurn(true);
        ///   TurnGunRight(90);
        ///   // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
        /// </pre>
        /// Note: Calling {@code setAdjustRadarForGunTurn(bool)} will
        /// automatically call {@link #setAdjustRadarForRobotTurn(bool)} with the
        /// same value, unless you have already called it earlier. This behavior is
        /// primarily for backward compatibility with older Robocode robots.
        ///
        /// @param independent {@code true} if the radar must turn independent from
        ///                    the gun's turn; {@code false} if the radar must turn with the gun's
        ///                    turn.
        /// @see #setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
        public bool IsAdjustRadarForGunTurn
        {
            get
            {
                if (peer != null)
                {
                    return ((IStandardRobotPeer)peer).isAdjustRadarForGunTurn();
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
        /// Sets the color of the robot's body, gun, and radar in the same time.
        /// <p/>
        /// You may only call this method one time per battle. A {@code null}
        /// indicates the default (blue) color.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       SetColors(null, Color.RED, new Color(150, 0, 150));
        ///       ...
        ///   }
        /// </pre>
        ///
        /// @param bodyColor  the new body color
        /// @param gunColor   the new gun color
        /// @param radarColor the new radar color
        /// @see #SetColors(Color, Color, Color, Color, Color)
        /// @see #SetAllColors(Color)
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// </summary>
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
        /// Sets the color of the robot's body, gun, radar, bullet, and Scan arc in
        /// the same time.
        /// <p/>
        /// You may only call this method one time per battle. A {@code null}
        /// indicates the default (blue) color for the body, gun, radar, and Scan
        /// arc, but white for the bullet color.
        /// <p/>
        /// Example:
        /// <pre>
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       SetColors(null, Color.RED, Color.GREEN, null, new Color(150, 0, 150));
        ///       ...
        ///   }
        /// </pre>
        ///
        /// @param bodyColor	the new body color
        /// @param gunColor	 the new gun color
        /// @param radarColor   the new radar color
        /// @param bulletColor  the new bullet color
        /// @param scanArcColor the new Scan arc color
        /// @see #SetColors(Color, Color, Color)
        /// @see #SetAllColors(Color)
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.3
        /// </summary>
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
        /// Sets all the robot's color to the same color in the same time, i.e. the
        /// color of the body, gun, radar, bullet, and Scan arc.
        /// <p/>
        /// You may only call this method one time per battle. A {@code null}
        /// indicates the default (blue) color for the body, gun, radar, and Scan
        /// arc, but white for the bullet color.
        /// <p/>
        /// <pre>
        /// Example:
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       SetAllColors(Color.RED);
        ///       ...
        ///   }
        /// </pre>
        ///
        /// @param color the new color for all the colors of the robot
        /// @see #SetColors(Color, Color, Color)
        /// @see #SetColors(Color, Color, Color, Color, Color)
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.3
        /// </summary>
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
        /// Sets the color of the robot's body.
        /// <p/>
        /// A {@code null} indicates the default (blue) color.
        /// <p/>
        /// <pre>
        /// Example:
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setBodyColor(Color.BLACK);
        ///       ...
        ///   }
        /// </pre>
        ///
        /// @param color the new body color
        /// @see #SetColors(Color, Color, Color)
        /// @see #SetColors(Color, Color, Color, Color, Color)
        /// @see #SetAllColors(Color)
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.2
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

        /// <summary>
        /// Sets the color of the robot's gun.
        /// <p/>
        /// A {@code null} indicates the default (blue) color.
        /// <p/>
        /// <pre>
        /// Example:
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setGunColor(Color.RED);
        ///       ...
        ///   }
        /// </pre>
        ///
        /// @param color the new gun color
        /// @see #SetColors(Color, Color, Color)
        /// @see #SetColors(Color, Color, Color, Color, Color)
        /// @see #SetAllColors(Color)
        /// @see #setBodyColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
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

        /// <summary>
        /// Sets the color of the robot's radar.
        /// <p/>
        /// A {@code null} indicates the default (blue) color.
        /// <p/>
        /// <pre>
        /// Example:
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setRadarColor(Color.YELLOW);
        ///       ...
        ///   }
        /// </pre>
        ///
        /// @param color the new radar color
        /// @see #SetColors(Color, Color, Color)
        /// @see #SetColors(Color, Color, Color, Color, Color)
        /// @see #SetAllColors(Color)
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
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

        /// <summary>
        /// Sets the color of the robot's bullets.
        /// <p/>
        /// A {@code null} indicates the default white color.
        /// <p/>
        /// <pre>
        /// Example:
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setBulletColor(Color.GREEN);
        ///       ...
        ///   }
        /// </pre>
        ///
        /// @param color the new bullet color
        /// @see #SetColors(Color, Color, Color)
        /// @see #SetColors(Color, Color, Color, Color, Color)
        /// @see #SetAllColors(Color)
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
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

        /// <summary>
        /// Sets the color of the robot's Scan arc.
        /// <p/>
        /// A {@code null} indicates the default (blue) color.
        /// <p/>
        /// <pre>
        /// Example:
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setScanColor(Color.WHITE);
        ///       ...
        ///   }
        /// </pre>
        ///
        /// @param color the new Scan arc color
        /// @see #SetColors(Color, Color, Color)
        /// @see #SetColors(Color, Color, Color, Color, Color)
        /// @see #SetAllColors(Color)
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
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

        /// <summary>
        /// Immediately stops all movement, and saves it for a call to
        /// {@link #Resume()}. If there is already movement saved from a previous
        /// Stop, this will have no effect.
        /// <p/>
        /// This method is equivalent to {@code #Stop(false)}.
        ///
        /// @see #Resume()
        /// @see #Stop(bool)
        /// </summary>
        public void Stop()
        {
            Stop(false);
        }

        /// <summary>
        /// Immediately stops all movement, and saves it for a call to
        /// {@link #Resume()}. If there is already movement saved from a previous
        /// Stop, you can overwrite it by calling {@code Stop(true)}.
        ///
        /// @param overwrite If there is already movement saved from a previous Stop,
        ///                  you can overwrite it by calling {@code Stop(true)}.
        /// @see #Resume()
        /// @see #Stop()
        /// </summary>
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
        /// Immediately resumes the movement you stopped by {@link #Stop()}, if any.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete.
        ///
        /// @see #Stop()
        /// @see #Stop(bool)
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

        /// <summary>
        /// Immediately turns the robot's gun to the left by degrees.
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
        ///   TurnGunLeft(180);
        /// <p/>
        ///   // Afterwards, turn the robot's gun 90 degrees to the right
        ///   TurnGunLeft(-90);
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's gun to the left.
        ///                If {@code degrees} &gt; 0 the robot's gun will turn left.
        ///                If {@code degrees} &lt; 0 the robot's gun will turn right.
        ///                If {@code degrees} = 0 the robot's gun will not turn, but Execute.
        /// @see #TurnGunRight(double)
        /// @see #TurnLeft(double)
        /// @see #TurnRight(double)
        /// @see #TurnRadarLeft(double)
        /// @see #TurnRadarRight(double)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
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

        /// <summary>
        /// Immediately turns the robot's gun to the right by degrees.
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
        ///   TurnGunRight(180);
        /// <p/>
        ///   // Afterwards, turn the robot's gun 90 degrees to the left
        ///   TurnGunRight(-90);
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's gun to the right.
        ///                If {@code degrees} &gt; 0 the robot's gun will turn right.
        ///                If {@code degrees} &lt; 0 the robot's gun will turn left.
        ///                If {@code degrees} = 0 the robot's gun will not turn, but Execute.
        /// @see #TurnGunLeft(double)
        /// @see #TurnLeft(double)
        /// @see #TurnRight(double)
        /// @see #TurnRadarLeft(double)
        /// @see #TurnRadarRight(double)
        /// @see #setAdjustGunForRobotTurn(bool)
        /// </summary>
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
        /// Immediately turns the robot's radar to the left by degrees.
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
        ///   TurnRadarLeft(180);
        /// <p/>
        ///   // Afterwards, turn the robot's radar 90 degrees to the right
        ///   TurnRadarLeft(-90);
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's radar to the left.
        ///                If {@code degrees} &gt; 0 the robot's radar will turn left.
        ///                If {@code degrees} &lt; 0 the robot's radar will turn right.
        ///                If {@code degrees} = 0 the robot's radar will not turn, but Execute.
        /// @see #TurnRadarRight(double)
        /// @see #TurnLeft(double)
        /// @see #TurnRight(double)
        /// @see #TurnGunLeft(double)
        /// @see #TurnGunRight(double)
        /// @see #setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool)
        /// </summary>
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

        /// <summary>
        /// Immediately turns the robot's radar to the right by degrees.
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
        ///   TurnRadarRight(180);
        /// <p/>
        ///   // Afterwards, turn the robot's radar 90 degrees to the left
        ///   TurnRadarRight(-90);
        /// </pre>
        ///
        /// @param degrees the amount of degrees to turn the robot's radar to the right.
        ///                If {@code degrees} &gt; 0 the robot's radar will turn right.
        ///                If {@code degrees} &lt; 0 the robot's radar will turn left.
        ///                If {@code degrees} = 0 the robot's radar will not turn, but Execute.
        /// @see #TurnRadarLeft(double)
        /// @see #TurnLeft(double)
        /// @see #TurnRight(double)
        /// @see #TurnGunLeft(double)
        /// @see #TurnGunRight(double)
        /// @see #setAdjustRadarForRobotTurn(bool)
        /// @see #setAdjustRadarForGunTurn(bool)
        /// </summary>
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
        /// Returns the robot's current energy.
        ///
        /// @return the robot's current energy.
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
        /// Returns a graphics context used for painting graphical items for the robot.
        /// <p/>
        /// This method is very useful for debugging your robot.
        /// <p/>
        /// Note that the robot will only be painted if the "Paint" is enabled on the
        /// robot's console window; otherwise the robot will never get painted (the
        /// reason being that all robots might have graphical items that must be
        /// painted, and then you might not be able to tell what graphical items that
        /// have been painted for your robot).
        /// <p/>
        /// Also note that the coordinate system for the graphical context where you
        /// paint items fits for the Robocode coordinate system where (0, 0) is at
        /// the bottom left corner of the battlefield, where X is towards right and Y
        /// is upwards.
        ///
        /// @return a graphics context used for painting graphical items for the robot.
        /// @see #OnPaint(Graphics)
        /// @since 1.6.1
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
        /// Sets the debug property with the specified key to the specified value.
        /// <p/>
        /// This method is very useful when debugging or reviewing your robot as you
        /// will be able to see this property displayed in the robot console for your
        /// robots under the Debug Properties tab page.
        ///
        /// @param key the name/key of the debug property.
        /// @param value the new value of the debug property, where {@code null} or
        ///              the empty string is used for removing this debug property. 
        /// @since 1.6.2
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

        public class DebugPropertyH
        {
            private readonly IBasicRobotPeer peer;

            internal DebugPropertyH(IBasicRobotPeer peer)
            {
                this.peer = peer;
            }

            public string this[string key]
            {
                set
                {
                    peer.setDebugProperty(key, value);
                }
            }
        }


        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnPaint(IGraphics graphics)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnKeyPressed(KeyEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnKeyReleased(KeyEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnKeyTyped(KeyEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMouseClicked(MouseEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMouseEntered(MouseEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMouseExited(MouseEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMousePressed(MouseEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMouseReleased(MouseEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMouseMoved(MouseEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMouseDragged(MouseEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnMouseWheelMoved(MouseWheelMovedEvent e)
        {
        }

        /// <summary>
        /// {@inheritDoc}
        /// </summary>
        public virtual void OnStatus(StatusEvent e)
        {
        }
    }
}

//happy