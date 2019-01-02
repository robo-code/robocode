/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System.Drawing;
using Robocode;
using Robocode.RobotInterfaces;

namespace Robocode.RobotInterfaces.Peer
{
    /// <summary>
    /// The basic robot peer for all robot types.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    /// <seealso cref="IStandardRobotPeer"/>
    /// <seealso cref="IAdvancedRobotPeer"/>
    /// <seealso cref="ITeamRobotPeer"/>
    /// <seealso cref="IJuniorRobotPeer"/>
    /// </summary>
    public interface IBasicRobotPeer
    {
        /// <summary>
        /// Returns the robot's name.
        /// </summary>
        string GetName();

        /// <summary>
        /// Returns the game time of the current round, where the time is equal to
        /// the current turn in the round.
        /// <p/>
        /// A battle consists of multiple rounds.
        /// <p/>
        /// Time is reset to 0 at the beginning of every round.
        /// </summary>
        long GetTime();

        /// <summary>
        /// Returns the robot's current energy.
        /// </summary>
        double GetEnergy();

        /// <summary>
        /// Returns the X position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        /// <seealso cref="GetY"/>
        /// </summary>
        double GetX();

        /// <summary>
        /// Returns the Y position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        /// <seealso cref="GetX"/>
        /// </summary>
        double GetY();

        /// <summary>
        /// Returns the velocity of the robot measured in pixels/turn.
        /// <p/>
        /// The maximum velocity of a robot is defined by
        /// <see cref="Rules.MAX_VELOCITY"/> (8 pixels / turn).
        /// <seealso cref="Rules.MAX_VELOCITY"/>
        /// </summary>
        double GetVelocity();

        /// <summary>
        /// Returns the direction that the robot's body is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 2 means West.
        /// <seealso cref="GetGunHeading"/>
        /// <seealso cref="GetRadarHeading"/>
        /// </summary>
        double GetBodyHeading();

        /// <summary>
        /// Returns the direction that the robot's gun is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 2 means West.
        /// <seealso cref="GetBodyHeading"/>
        /// <seealso cref="GetRadarHeading"/>
        /// </summary>
        double GetGunHeading();

        /// <summary>
        /// Returns the direction that the robot's radar is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 2 means West.
        /// <seealso cref="GetBodyHeading"/>
        /// <seealso cref="GetGunHeading"/>
        /// </summary>
        double GetRadarHeading();

        /// <summary>
        /// Returns the current heat of the gun. The gun cannot Fire unless this is
        /// 0. (Calls to Fire will succeed, but will not actually Fire unless
        /// GetGunHeat() == 0).
        /// <p/>
        /// The amount of gun heat generated when the gun is fired is
        /// 1 + (firePower / 5). Each turn the gun heat drops by the amount returned
        /// by <see cref="GetGunCoolingRate"/>, which is a battle setup.
        /// <p/>
        /// Note that all guns are "hot" at the start of each round, where the gun
        /// heat is 3.
        /// <seealso cref="GetGunCoolingRate"/>
        /// <seealso cref="SetFire"/>
        /// </summary>
        double GetGunHeat();

        /// <summary>
        /// Returns the width of the current battlefield measured in pixels.
        /// </summary>
        double GetBattleFieldWidth();

        /// <summary>
        /// Returns the height of the current battlefield measured in pixels.
        /// </summary>
        double GetBattleFieldHeight();

        /// <summary>
        /// Returns how many opponents that are left in the current round.
        /// </summary>
        int GetOthers();

        /// <summary>
        /// Returns how many sentry robots that are left in the current round.
        /// </summary>
        int GetNumSentries();

        /// <summary>
        /// Returns the number of rounds in the current battle.
        /// <seealso cref="GetRoundNum"/>
        /// </summary>
        int GetNumRounds();

        /// <summary>
        /// Returns the number of the current round (0 to <see cref="GetNumRounds"/> - 1)
        /// in the battle.
        /// <seealso cref="GetNumRounds"/>
        /// </summary>
        int GetRoundNum();

        ///<summary>
        ///  Returns the sentry border size for a <see cref="Robocode.BorderSentry">BorderSentry</see> that defines the how
        ///  far a BorderSentry is allowed to move from the border edges measured in units.<br/>
        ///  Hence, the sentry border size defines the width/range of the border area surrounding the battlefield that
        ///  BorderSentrys cannot leave (sentry robots robots must stay in the border area), but it also define the
        ///  distance from the border edges where BorderSentrys are allowed/able to make damage to robots entering this
        ///  border area.
        ///</summary>
        int GetSentryBorderSize();

        /// <summary>
        /// Returns the rate at which the gun will cool down, i.e. the amount of heat
        /// the gun heat will drop per turn.
        /// <p/>
        /// The gun cooling rate is default 0.1 / turn, but can be changed by the
        /// battle setup. So don't count on the cooling rate being 0.1!
        /// <seealso cref="GetGunHeat"/>
        /// <seealso cref="SetFire"/>
        /// </summary>
        double GetGunCoolingRate();

        /// <summary>
        /// Returns the distance remaining in the robot's current move measured in
        /// pixels.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the robot is currently moving forwards. Negative values means
        /// that the robot is currently moving backwards. If the returned value is 0,
        /// the robot currently stands still.
        /// <seealso cref="GetBodyTurnRemaining"/>
        /// <seealso cref="GetGunTurnRemaining"/>
        /// <seealso cref="GetRadarTurnRemaining"/>
        /// </summary>
        double GetDistanceRemaining();

        /// <summary>
        /// Returns the angle remaining in the robot's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the robot is currently turning to the right. Negative values
        /// means that the robot is currently turning to the left.
        /// <seealso cref="GetDistanceRemaining"/>
        /// <seealso cref="GetGunTurnRemaining"/>
        /// <seealso cref="GetRadarTurnRemaining"/>
        /// </summary>
        double GetBodyTurnRemaining();

        /// <summary>
        /// Returns the angle remaining in the gun's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the gun is currently turning to the right. Negative values
        /// means that the gun is currently turning to the left.
        /// <seealso cref="GetDistanceRemaining"/>
        /// <seealso cref="GetBodyTurnRemaining"/>
        /// <seealso cref="GetRadarTurnRemaining"/>
        /// </summary>
        double GetGunTurnRemaining();

        /// <summary>
        /// Returns the angle remaining in the radar's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the radar is currently turning to the right. Negative values
        /// means that the radar is currently turning to the left.
        /// <seealso cref="GetDistanceRemaining"/>
        /// <seealso cref="GetBodyTurnRemaining"/>
        /// <seealso cref="GetGunTurnRemaining"/>
        /// </summary>
        double GetRadarTurnRemaining();

        /// <summary>
        /// Executes any pending actions, or continues executing actions that are
        /// in process. This call returns after the actions have been started.
        /// <p/>
        /// Note that advanced robots <em>must</em> call this function in order to
        /// Execute pending set* calls like e.g. <see cref="IAdvancedRobotPeer.SetMove"/>
        /// <see cref="SetFire"/>, <see cref="IAdvancedRobotPeer.SetTurnBody"/> etc.
        /// Otherwise, these calls will never get executed.
        /// <p/>
        /// In this example the robot will move while turning:
        /// <example>
        ///   <code>
        ///   SetTurnBody(90);
        ///   SetMove(100);
        ///   Execute();
        ///
        ///   while (GetDistanceRemaining() > 0 &amp;&amp; GetTurnRemaining() > 0)
        ///   {
        ///       Execute();
        ///   }
        ///   </code>
        /// </example>
        /// </summary>
        void Execute();

        /// <summary>
        /// Immediately moves your robot forward or backward by distance measured in
        /// pixels.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the remaining distance to move is 0.
        /// <p/>
        /// If the robot collides with a wall, the move is complete, meaning that the
        /// robot will not move any further. If the robot collides with another
        /// robot, the move is complete if you are heading toward the other robot.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot is set to move forward, and negative
        /// values means that the robot is set to move backward.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Move the robot 100 pixels forward
        ///   Ahead(100);
        ///
        ///   // Afterwards, move the robot 50 pixels backward
        ///   Ahead(-50);
        ///   </code>
        /// </example>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnHitWall(Robocode.HitWallEvent)"/>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnHitRobot(Robocode.HitRobotEvent)"/>
        /// </summary>
        /// <param name="distance">
        ///   The distance to move measured in pixels.
        ///   If distance &gt; 0 the robot is set to move forward.
        ///   If distance &lt; 0 the robot is set to move backward.
        ///   If distance = 0 the robot will not move anywhere, but just finish its turn.
        /// </param>
        void Move(double distance);

        /// <summary>
        /// Immediately turns the robot's body to the right or left by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the body's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's body is set to turn right, and
        /// negative values means that the robot's body is set to turn left.
        /// If 0 is given as input, the robot's body will stop turning.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Turn the robot's body 180 degrees to the right
        ///   TurnBody(Math.PI);
        ///
        ///   // Afterwards, turn the robot's body 90 degrees to the left
        ///   TurnBody(-Math.PI / 2);
        ///   </code>
        /// </example>
        /// <seealso cref="TurnGun"/>
        /// <seealso cref="IStandardRobotPeer.TurnRadar"/>
        /// <seealso cref="Move"/>
        /// </summary>
        /// <param name="radians">
        ///   The amount of radians to turn the robot's body.
        ///   If radians &gt; 0 the robot's body is set to turn right.
        ///   If radians &lt; 0 the robot's body is set to turn left.
        ///   If radians = 0 the robot's body is set to stop turning.
        /// </param>
        void TurnBody(double radians);

        /// <summary>
        /// Immediately turns the robot's gun to the right or left by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the gun's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's gun is set to turn right, and
        /// negative values means that the robot's gun is set to turn left.
        /// If 0 is given as input, the robot's gun will stop turning.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Turn the robot's gun 180 degrees to the right
        ///   TurnGun(Math.PI);
        ///
        ///   // Afterwards, turn the robot's gun 90 degrees to the left
        ///   TurnGun(-Math.PI / 2);
        ///   </code>
        /// </example>
        /// <seealso cref="TurnBody"/>
        /// <seealso cref="IStandardRobotPeer.TurnRadar"/>
        /// <seealso cref="Move"/>
        /// </summary>
        /// <param name="radians">
        ///   The amount of radians to turn the robot's gun.
        ///   If radians &gt; 0 the robot's gun is set to turn right.
        ///   If radians &lt; 0 the robot's gun is set to turn left.
        ///   If radians = 0 the robot's gun is set to stop turning.
        /// </param>
        void TurnGun(double radians);

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
        /// You will get (3 * power) back if you hit the other robot. You can call
        /// <see cref="Rules.GetBulletDamage(double)"/> for getting the damage that a
        /// bullet with a specific bullet power will do.
        /// <p/>
        /// The specified bullet power should be between
        /// <see cref="Rules.MIN_BULLET_POWER"/> and <see cref="Rules.MAX_BULLET_POWER"/>.
        /// <p/>
        /// Note that the gun cannot Fire if the gun is overheated, meaning that
        /// <see cref="GetGunHeat"/> returns a value &gt; 0.
        /// <p/>
        /// An event is generated when the bullet hits a robot
        /// (<see cref="BulletHitEvent"/>"/>, wall (<see cref="BulletMissedEvent"/>), or another
        /// bullet (<see cref="BulletHitBulletEvent"/>).
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Fire a bullet with maximum power if the gun is ready
        ///   if (GetGunHeat() == 0)
        ///   {
        ///       Bullet bullet = Fire(Rules.MAX_BULLET_POWER);
        ///
        ///       // Get the velocity of the bullet
        ///       if (bullet != null)
        ///       {
        ///           double bulletVelocity = bullet.Velocity;
        ///       }
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="SetFire"/>
        /// <seealso cref="Bullet"/>
        /// <seealso cref="GetGunHeat"/>
        /// <seealso cref="GetGunCoolingRate"/>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletHit(BulletHitEvent)"/>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletHitBullet(BulletHitBulletEvent)"/>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletMissed(BulletMissedEvent)"/>
        /// </summary>
        /// <param name="power">
        ///   The amount of energy given to the bullet, and subtracted from the robot's energy.
        /// </param>
        Bullet Fire(double power);

        /// <summary>
        /// Sets the gun to Fire a bullet when the next execution takes place.
        /// The bullet will travel in the direction the gun is pointing.
        /// <p/>
        /// This call returns immediately, and will not execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// The specified bullet power is an amount of energy that will be taken from
        /// the robot's energy. Hence, the more power you want to spend on the
        /// bullet, the more energy is taken from your robot.
        /// <p/>
        /// The bullet will do (4 * power) damage if it hits another robot. If power
        /// is greater than 1, it will do an additional 2 * (power - 1) damage.
        /// You will get (3 * power) back if you hit the other robot. You can call
        /// <see cref="Rules.GetBulletDamage(double)"/> for getting the damage that a
        /// bullet with a specific bullet power will do.
        /// <p/>
        /// The specified bullet power should be between
        /// <see cref="Rules.MIN_BULLET_POWER"/> and <see cref="Rules.MAX_BULLET_POWER"/>.
        /// <p/>
        /// Note that the gun cannot Fire if the gun is overheated, meaning that
        /// <see cref="GetGunHeat"/> returns a value &gt; 0.
        /// <p/>
        /// An event is generated when the bullet hits a robot
        /// (<see cref="BulletHitEvent"/>), wall (<see cref="BulletMissedEvent"/>), or another
        /// bullet (<see cref="BulletHitBulletEvent"/>).
        /// <p/>
        /// <example>
        ///   <code>
        ///   Bullet bullet = null;
        ///
        ///   // Fire a bullet with maximum power if the gun is ready
        ///   if (GetGunHeat() == 0)
        ///   {
        ///       bullet = SetFireBullet(Rules.MAX_BULLET_POWER);
        ///   }
        ///   ...
        ///   Execute();
        ///   ...
        ///   // Get the velocity of the bullet
        ///   if (bullet != null)
        ///   {
        ///       double bulletVelocity = bullet.Velocity;
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="Fire"/>
        /// <seealso cref="Bullet"/>
        /// <seealso cref="GetGunHeat"/>
        /// <seealso cref="GetGunCoolingRate"/>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletHit(BulletHitEvent)"/>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletHitBullet(BulletHitBulletEvent)"/>
        /// <seealso cref="Robocode.RobotInterfaces.IBasicEvents.OnBulletMissed(BulletMissedEvent)"/>
        /// </summary>
        /// <param name="power">
        ///   The amount of energy given to the bullet, and subtracted from the robot's energy.
        /// </param> 
        Bullet SetFire(double power);

        /// <summary>
        /// Sets the color of the robot's body.
        /// <p/>
        /// A null indicates the default (blue) color.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Don't forget to using System.Drawing at the top...
        ///   using System.Drawing;
        ///   ...
        ///
        ///   public void Run()
        ///   {
        ///       SetBodyColor(Color.BLACK);
        ///       ...
        ///   }
        ///   </code>
        /// </example>
        ///
        /// <seealso cref="SetGunColor"/>
        /// <seealso cref="SetRadarColor"/>
        /// <seealso cref="SetBulletColor"/>
        /// <seealso cref="SetScanColor"/>
        /// <seealso cref="Color"/>
        /// <param name="color">The new body color</param>
        /// </summary>
        void SetBodyColor(Color color);

        /// <summary>
        /// Returns current color of body
        /// </summary>
        Color GetBodyColor();

        /// <summary>
        /// Sets the color of the robot's gun.
        /// <p/>
        /// A null indicates the default (blue) color.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Don't forget to using System.Drawing at the top...
        ///   using System.Drawing;
        ///   ...
        ///
        ///   public void Run()
        ///   {
        ///       SetGunColor(Color.RED);
        ///       ...
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="SetBodyColor"/>
        /// <seealso cref="SetRadarColor"/>
        /// <seealso cref="SetBulletColor"/>
        /// <seealso cref="SetScanColor"/>
        /// <seealso cref="Color"/>
        /// </summary>
        /// <param name="color">The new gun color</param>
        void SetGunColor(Color color);

        /// <summary>
        /// Returns current color of gun
        /// </summary>
        Color GetGunColor();

        /// <summary>
        /// Sets the color of the robot's radar.
        /// <p/>
        /// A null indicates the default (blue) color.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Don't forget to using System.Drawing at the top...
        ///   using System.Drawing;
        ///   ...
        ///
        ///   public void Run()
        ///   {
        ///       SetRadarColor(Color.YELLOW);
        ///       ...
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="SetBodyColor"/>
        /// <seealso cref="SetGunColor"/>
        /// <seealso cref="SetBulletColor"/>
        /// <seealso cref="SetScanColor"/>
        /// <seealso cref="Color"/>
        /// </summary>
        /// <param name="color">The new radar color</param>
        void SetRadarColor(Color color);

        /// <summary>
        /// Returns current color of radar
        /// </summary>
        Color GetRadarColor();

        /// <summary>
        /// Sets the color of the robot's bullets.
        /// <p/>
        /// A null indicates the default white color.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Don't forget to using System.Drawing at the top...
        ///   using System.Drawing;
        ///   ...
        ///
        ///   public void Run()
        ///   {
        ///       SetBulletColor(Color.GREEN);
        ///       ...
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="SetBodyColor"/>
        /// <seealso cref="SetGunColor"/>
        /// <seealso cref="SetRadarColor"/>
        /// <seealso cref="SetScanColor"/>
        /// <seealso cref="Color"/>
        /// </summary>
        /// <param name="color">The new bullet color</param>
        void SetBulletColor(Color color);

        /// <summary>
        /// Returns current color of bullet
        /// </summary>
        Color GetBulletColor();

        /// <summary>
        /// Sets the color of the robot's scan arc.
        /// <p/>
        /// A null indicates the default (blue) color.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Don't forget to using System.Drawing at the top...
        ///   using System.Drawing;
        ///   ...
        ///
        ///   public void Run()
        ///   {
        ///       SetScanColor(Color.WHITE);
        ///       ...
        ///   }
        ///   </code>
        /// </example>
        /// <seealso cref="SetBodyColor"/>
        /// <seealso cref="SetGunColor"/>
        /// <seealso cref="SetRadarColor"/>
        /// <seealso cref="SetBulletColor"/>
        /// <seealso cref="Color"/>
        /// </summary>
        /// <param name="color">the new scan arc color</param>
        void SetScanColor(Color color);

        /// <summary>
        /// Returns current color of scan beam
        /// </summary>
        Color GetScanColor();

        /// <summary>
        /// This call <em>must</em> be made from a robot call to inform the game
        /// that the robot made a Get* call like e.g. <see cref="GetX"/> or
        /// <see cref="GetVelocity"/>.
        /// <p/>
        /// This method is used by the game to determine if the robot is inactive or
        /// not. Note: You should only make this call once in a Get* method!
        /// <seealso cref="SetCall"/>
        /// </summary>
        void GetCall();

        /// <summary>
        /// This call <em>must</em> be made from a robot call to inform the game
        /// that the robot made a Set* call like e.g. <see cref="SetFire"/>
        /// or <see cref="SetBodyColor"/>.
        /// <p/>
        /// This method is used by the game to determine if the robot is inactive or
        /// not. Note: You should only make this call once in a Set* method!
        ///
        /// <seealso cref="GetCall"/>
        /// </summary>
        void SetCall();

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
        /// <seealso cref="Robocode.RobotInterfaces.IPaintEvents.OnPaint(IGraphics)"/>
        /// </summary>
        IGraphics GetGraphics();

        /// <summary>
        /// Sets the debug property with the specified key to the specified value.
        /// <p/>
        /// This method is very useful when debugging or reviewing your robot as you
        /// will be able to see this property displayed in the robot console for your
        /// robots under the Debug Properties tab page.
        /// </summary>
        /// <param name="key">The name/key of the debug property.</param>
        /// <param name="value">
        ///   The new value of the debug property, where null or the empty string is used
        ///   for removing this debug property.
        /// </param>
        void SetDebugProperty(string key, string value);

        /// <summary>
        /// Rescan for other robots. This method is called automatically by the game,
        /// as long as the robot is moving, turning its body, turning its gun, or
        /// turning its radar.
        /// <p/>
        /// Rescan will cause <see cref="IBasicEvents.OnScannedRobot(ScannedRobotEvent)"/>
        /// to be called if you see a robot.
        /// <p/>
        /// There are 2 reasons to call Rescan() manually:
        /// <ol>
        /// <li>You want to scan after you stop moving.</li>
        /// <li>You want to interrupt the OnScannedRobot event. This is more
        /// likely. If you are in OnScannedRobot and call Scan(),
        /// and you still see a robot, then the system will interrupt your
        /// OnScannedRobot event immediately and start it from the top.</li>
        /// </ol>
        /// <p/>
        /// This call executes immediately.
        ///
        /// <seealso cref="IBasicEvents.OnScannedRobot(ScannedRobotEvent)"/>
        /// <seealso cref="ScannedRobotEvent"/>
        /// </summary>
        void Rescan();
    }
}

//doc