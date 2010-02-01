#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

using System.Drawing;

namespace robocode.robotinterfaces.peer
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
        string getName();

        /// <summary>
        /// Returns the game time of the current round, where the time is equal to
        /// the current turn in the round.
        /// <p/>
        /// A battle consists of multiple rounds.
        /// <p/>
        /// Time is reset to 0 at the beginning of every round.
        /// </summary>
        long getTime();

        /// <summary>
        /// Returns the robot's current energy.
        /// </summary>
        double getEnergy();

        /// <summary>
        /// Returns the X position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        /// <seealso cref="getY()"/>
        /// </summary>
        double getX();

        /// <summary>
        /// Returns the Y position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        /// <seealso cref="getX()"/>
        /// </summary>
        double getY();

        /// <summary>
        /// Returns the velocity of the robot measured in pixels/turn.
        /// <p/>
        /// The maximum velocity of a robot is defined by
        /// <see cref="Rules.MAX_VELOCITY"/> (8 pixels / turn).
        /// <seealso cref="Rules.MAX_VELOCITY"/>
        /// </summary>
        double getVelocity();

        /// <summary>
        /// Returns the direction that the robot's body is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        /// <seealso cref="getGunHeading()"/>
        /// <seealso cref="getRadarHeading()"/>
        /// </summary>
        double getBodyHeading();

        /// <summary>
        /// Returns the direction that the robot's gun is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        /// <seealso cref="getBodyHeading()"/>
        /// <seealso cref="getRadarHeading()"/>
        /// </summary>
        double getGunHeading();

        /// <summary>
        /// Returns the direction that the robot's radar is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        /// <seealso cref="getBodyHeading()"/>
        /// <seealso cref="getGunHeading()"/>
        /// </summary>
        double getRadarHeading();

        /// <summary>
        /// Returns the current heat of the gun. The gun cannot Fire unless this is
        /// 0. (Calls to Fire will succeed, but will not actually Fire unless
        /// GetGunHeat() == 0).
        /// <p/>
        /// The amount of gun heat generated when the gun is fired is
        /// 1 + (firePower / 5). Each turn the gun heat drops by the amount returned
        /// by <see cref="getGunCoolingRate()"/>, which is a battle setup.
        /// <p/>
        /// Note that all guns are "hot" at the start of each round, where the gun
        /// heat is 3.
        /// <seealso cref="getGunCoolingRate()"/>
        /// <seealso cref="setFire(double)"/>
        /// </summary>
        double getGunHeat();

        /// <summary>
        /// Returns the width of the current battlefield measured in pixels.
        /// </summary>
        double getBattleFieldWidth();

        /// <summary>
        /// Returns the height of the current battlefield measured in pixels.
        /// </summary>
        double getBattleFieldHeight();

        /// <summary>
        /// Returns how many opponents that are left in the current round.
        /// </summary>
        int getOthers();

        /// <summary>
        /// Returns the number of rounds in the current battle.
        /// <seealso cref="getRoundNum()"/>
        /// </summary>
        int getNumRounds();

        /// <summary>
        /// Returns the number of the current round (0 to <see cref="getNumRounds()"/> - 1)
        /// in the battle.
        /// <seealso cref="getNumRounds()"/>
        /// </summary>
        int getRoundNum();

        /// <summary>
        /// Returns the rate at which the gun will cool down, i.e. the amount of heat
        /// the gun heat will drop per turn.
        /// <p/>
        /// The gun cooling rate is default 0.1 / turn, but can be changed by the
        /// battle setup. So don't count on the cooling rate being 0.1!
        /// <seealso cref="getGunHeat()"/>
        /// <seealso cref="setFire(double)"/>
        /// </summary>
        double getGunCoolingRate();

        /// <summary>
        /// Returns the distance remaining in the robot's current move measured in
        /// pixels.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the robot is currently moving forwards. Negative values means
        /// that the robot is currently moving backwards. If the returned value is 0,
        /// the robot currently stands still.
        /// <seealso cref="getBodyTurnRemaining()"/>
        /// <seealso cref="getGunTurnRemaining()"/>
        /// <seealso cref="getRadarTurnRemaining()"/>
        /// </summary>
        double getDistanceRemaining();

        /// <summary>
        /// Returns the angle remaining in the robot's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the robot is currently turning to the right. Negative values
        /// means that the robot is currently turning to the left.
        /// <seealso cref="getDistanceRemaining()"/>
        /// <seealso cref="getGunTurnRemaining()"/>
        /// <seealso cref="getRadarTurnRemaining()"/>
        /// </summary>
        double getBodyTurnRemaining();

        /// <summary>
        /// Returns the angle remaining in the gun's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the gun is currently turning to the right. Negative values
        /// means that the gun is currently turning to the left.
        /// <seealso cref="getDistanceRemaining()"/>
        /// <seealso cref="getBodyTurnRemaining()"/>
        /// <seealso cref="getRadarTurnRemaining()"/>
        /// </summary>
        double getGunTurnRemaining();

        /// <summary>
        /// Returns the angle remaining in the radar's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the radar is currently turning to the right. Negative values
        /// means that the radar is currently turning to the left.
        /// <seealso cref="getDistanceRemaining()"/>
        /// <seealso cref="getBodyTurnRemaining()"/>
        /// <seealso cref="getGunTurnRemaining()"/>
        /// </summary>
        double getRadarTurnRemaining();

        /// <summary>
        /// Executes any pending actions, or continues executing actions that are
        /// in process. This call returns after the actions have been started.
        /// <p/>
        /// Note that advanced robots <em>must</em> call this function in order to
        /// Execute pending set* calls like e.g. <see cref="IAdvancedRobotPeer.setMove(double)"/>
        /// <see cref="IBasicRobotPeer.setFire(double)"/>, <see cref="IAdvancedRobotPeer.setTurnBody(double)"/> etc.
        /// Otherwise, these calls will never get executed.
        /// <p/>
        /// In this example the robot will move while turning:
        /// <pre>
        ///   setTurnBody(90);
        ///   setMove(100);
        ///   Execute();
        /// <p/>
        ///   while (getDistanceRemaining() > 0 &amp;&amp; getTurnRemaining() > 0) {
        ///       Execute();
        ///   }
        /// </pre>
        /// </summary>
        void execute();

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
        /// <pre>
        ///   // Move the robot 100 pixels forward
        ///   Ahead(100);
        /// <p/>
        ///   // Afterwards, move the robot 50 pixels backward
        ///   Ahead(-50);
        /// </pre>
        /// </example>
        /// <seealso cref="robocode.robotinterfaces.IBasicEvents.OnHitWall(robocode.HitWallEvent)"/>
        /// <seealso cref="robocode.robotinterfaces.IBasicEvents.OnHitRobot(robocode.HitRobotEvent)"/>
        /// </summary>
        /// <param name="distance">the distance to move measured in pixels.
        ///                 If distance &gt; 0 the robot is set to move forward.
        ///                 If distance &lt; 0 the robot is set to move backward.
        ///                 If distance = 0 the robot will not move anywhere, but just
        ///                 finish its turn.</param>
        void move(double distance);

        /// <summary>
        /// Immediately turns the robot's body to the right or left by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the body's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's body is set to turn right, and
        /// negative values means that the robot's body is set to turn left.
        /// If 0 is given as input, the robot's body will Stop turning.
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Turn the robot's body 180 degrees to the right
        ///   turnBody(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's body 90 degrees to the left
        ///   turnBody(-Math.PI / 2);
        /// </pre>
        /// </example>
        /// <seealso cref="turnGun(double)"/>
        /// <seealso cref="IStandardRobotPeer.turnRadar(double)"/>
        /// <seealso cref="move(double)"/>
        /// </summary>
        /// <param name="radians">the amount of radians to turn the robot's body.
        ///                If radians &gt; 0 the robot's body is set to turn right.
        ///                If radians &lt; 0 the robot's body is set to turn left.
        ///                If radians = 0 the robot's body is set to Stop turning.</param>
        void turnBody(double radians);

        /// <summary>
        /// Immediately turns the robot's gun to the right or left by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the gun's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's gun is set to turn right, and
        /// negative values means that the robot's gun is set to turn left.
        /// If 0 is given as input, the robot's gun will Stop turning.
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Turn the robot's gun 180 degrees to the right
        ///   turnGun(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's gun 90 degrees to the left
        ///   turnGun(-Math.PI / 2);
        /// </pre>
        /// </example>
        /// <seealso cref="turnBody(double)"/>
        /// <seealso cref="IStandardRobotPeer.turnRadar(double)"/>
        /// <seealso cref="move(double)"/>
        /// </summary>
        /// <param name="radians">the amount of radians to turn the robot's gun.
        ///                If radians &gt; 0 the robot's gun is set to turn right.
        ///                If radians &lt; 0 the robot's gun is set to turn left.
        ///                If radians = 0 the robot's gun is set to Stop turning.</param>
        void turnGun(double radians);

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
        /// <see cref="Rules.GetBulletDamage(double)"/> for getting the damage that a
        /// bullet with a specific bullet power will do.
        /// <p/>
        /// The specified bullet power should be between
        /// <see cref="Rules.MIN_BULLET_POWER"/> and <see cref="Rules.MAX_BULLET_POWER"/>.
        /// <p/>
        /// Note that the gun cannot Fire if the gun is overheated, meaning that
        /// <see cref="getGunHeat()"/> returns a value &gt; 0.
        /// <p/>
        /// A evnt is generated when the bullet hits a robot
        /// (<see cref="BulletHitEvent"/>"/>, wall (<see cref="BulletMissedEvent"/>), or another
        /// bullet (<see cref="BulletHitBulletEvent"/>).
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Fire a bullet with maximum power if the gun is ready
        ///   if (GetGunHeat() == 0) {
        ///       Bullet bullet = Fire(Rules.MAX_BULLET_POWER);
        /// <p/>
        ///       // Get the velocity of the bullet
        ///       if (bullet != null) {
        ///           double bulletVelocity = bullet.Velocity;
        ///       }
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="setFire(double)"/>
        /// <seealso cref="Bullet"/>
        /// <seealso cref="getGunHeat()"/>
        /// <seealso cref="getGunCoolingRate()"/>
        /// <seealso cref="robocode.robotinterfaces.IBasicEvents.OnBulletHit(BulletHitEvent)"/>
        /// <seealso cref="robocode.robotinterfaces.IBasicEvents.OnBulletHitBullet(BulletHitBulletEvent)"/>
        /// <seealso cref="robocode.robotinterfaces.IBasicEvents.OnBulletMissed(BulletMissedEvent)"/>
        /// </summary>
        /// <param name="power">the amount of energy given to the bullet, and subtracted
        ///              from the robot's energy.</param>
        Bullet fire(double power);

        /// <summary>
        /// Sets the gun to Fire a bullet when the next execution takes place.
        /// The bullet will travel in the direction the gun is pointing.
        /// <p/>
        /// This call returns immediately, and will not Execute until you call
        /// Execute() or take an action that executes.
        /// <p/>
        /// The specified bullet power is an amount of energy that will be taken from
        /// the robot's energy. Hence, the more power you want to spend on the
        /// bullet, the more energy is taken from your robot.
        /// <p/>
        /// The bullet will do (4 * power) damage if it hits another robot. If power
        /// is greater than 1, it will do an additional 2 * (power - 1) damage.
        /// You will get (3 * power) Back if you hit the other robot. You can call
        /// <see cref="Rules.GetBulletDamage(double)"/> for getting the damage that a
        /// bullet with a specific bullet power will do.
        /// <p/>
        /// The specified bullet power should be between
        /// <see cref="Rules.MIN_BULLET_POWER"/> and <see cref="Rules.MAX_BULLET_POWER"/>.
        /// <p/>
        /// Note that the gun cannot Fire if the gun is overheated, meaning that
        /// <see cref="getGunHeat()"/> returns a value &gt; 0.
        /// <p/>
        /// A evnt is generated when the bullet hits a robot
        /// (<see cref="BulletHitEvent"/>), wall (<see cref="BulletMissedEvent"/>), or another
        /// bullet (<see cref="BulletHitBulletEvent"/>).
        /// <p/>
        /// <example>
        /// <pre>
        ///   Bullet bullet = null;
        /// <p/>
        ///   // Fire a bullet with maximum power if the gun is ready
        ///   if (GetGunHeat() == 0) {
        ///       bullet = SetFireBullet(Rules.MAX_BULLET_POWER);
        ///   }
        ///   ...
        ///   Execute();
        ///   ...
        ///   // Get the velocity of the bullet
        ///   if (bullet != null) {
        ///       double bulletVelocity = bullet.Velocity;
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="fire(double)"/>
        /// <seealso cref="Bullet"/>
        /// <seealso cref="getGunHeat()"/>
        /// <seealso cref="getGunCoolingRate()"/>
        /// <seealso cref="robocode.robotinterfaces.IBasicEvents.OnBulletHit(BulletHitEvent)"/>
        /// <seealso cref="robocode.robotinterfaces.IBasicEvents.OnBulletHitBullet(BulletHitBulletEvent)"/>
        /// <seealso cref="robocode.robotinterfaces.IBasicEvents.OnBulletMissed(BulletMissedEvent)"/>
        /// </summary>
        /// <param name="power">the amount of energy given to the bullet, and subtracted
        ///              from the robot's energy.</param> 
        Bullet setFire(double power);

        /// <summary>
        /// Sets the color of the robot's body.
        /// <p/>
        /// A null indicates the default (blue) color.
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setBodyColor(Color.BLACK);
        ///       ...
        ///   }
        /// </pre>
        /// </example>
        ///
        /// <seealso cref="setGunColor(Color)"/>
        /// <seealso cref="setRadarColor(Color)"/>
        /// <seealso cref="setBulletColor(Color)"/>
        /// <seealso cref="setScanColor(Color)"/>
        /// <seealso cref="Color"/>
        /// <param name="color">the new body color</param>
        /// </summary>
        void setBodyColor(Color color);

        /// <summary>
        /// Sets the color of the robot's gun.
        /// <p/>
        /// A null indicates the default (blue) color.
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setGunColor(Color.RED);
        ///       ...
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="setBodyColor(Color)"/>
        /// <seealso cref="setRadarColor(Color)"/>
        /// <seealso cref="setBulletColor(Color)"/>
        /// <seealso cref="setScanColor(Color)"/>
        /// <seealso cref="Color"/>
        /// </summary>
        /// <param name="color">the new gun color</param>
        void setGunColor(Color color);

        /// <summary>
        /// Sets the color of the robot's radar.
        /// <p/>
        /// A null indicates the default (blue) color.
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setRadarColor(Color.YELLOW);
        ///       ...
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="setBodyColor(Color)"/>
        /// <seealso cref="setGunColor(Color)"/>
        /// <seealso cref="setBulletColor(Color)"/>
        /// <seealso cref="setScanColor(Color)"/>
        /// <seealso cref="Color"/>
        /// </summary>
        /// <param name="color">the new radar color</param>
        void setRadarColor(Color color);

        /// <summary>
        /// Sets the color of the robot's bullets.
        /// <p/>
        /// A null indicates the default white color.
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setBulletColor(Color.GREEN);
        ///       ...
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="setBodyColor(Color)"/>
        /// <seealso cref="setGunColor(Color)"/>
        /// <seealso cref="setRadarColor(Color)"/>
        /// <seealso cref="setScanColor(Color)"/>
        /// <seealso cref="Color"/>
        /// </summary>
        /// <param name="color">the new bullet color</param>
        void setBulletColor(Color color);

        /// <summary>
        /// Sets the color of the robot's Scan arc.
        /// <p/>
        /// A null indicates the default (blue) color.
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Don't forget to using java.awt.Color at the top...
        ///   using java.awt.Color;
        ///   ...
        /// <p/>
        ///   public void Run() {
        ///       setScanColor(Color.WHITE);
        ///       ...
        ///   }
        /// </pre>
        /// </example>
        /// <seealso cref="setBodyColor(Color)"/>
        /// <seealso cref="setGunColor(Color)"/>
        /// <seealso cref="setRadarColor(Color)"/>
        /// <seealso cref="setBulletColor(Color)"/>
        /// <seealso cref="Color"/>
        /// </summary>
        /// <param name="color">the new Scan arc color</param>
        void setScanColor(Color color);

        /// <summary>
        /// This call <em>must</em> be made from a robot call to inform the game
        /// that the robot made a get* call like e.g. <see cref="getX()"/> or
        /// <see cref="getVelocity()"/>.
        /// <p/>
        /// This method is used by the game to determine if the robot is inactive or
        /// not. Note: You should only make this call once in a get* method!
        /// <seealso cref="setCall()"/>
        /// </summary>
        void getCall();

        /// <summary>
        /// This call <em>must</em> be made from a robot call to inform the game
        /// that the robot made a set* call like e.g. <see cref="setFire(double)"/>
        /// or <see cref="setBodyColor(Color)"/>.
        /// <p/>
        /// This method is used by the game to determine if the robot is inactive or
        /// not. Note: You should only make this call once in a set* method!
        ///
        /// <seealso cref="getCall()"/>
        /// </summary>
        void setCall();

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
        /// <seealso cref="robocode.robotinterfaces.IPaintEvents.OnPaint(IGraphics)"/>
        /// </summary>
        IGraphics getGraphics();

        /// <summary>
        /// Sets the debug property with the specified key to the specified value.
        /// <p/>
        /// This method is very useful when debugging or reviewing your robot as you
        /// will be able to see this property displayed in the robot console for your
        /// robots under the Debug Properties tab page.
        /// </summary>
        /// <param name="key">the name/key of the debug property.</param>
        /// <param name="value"> the new value of the debug property, where null or
        ///              the empty string is used for removing this debug property. </param>
        void setDebugProperty(string key, string value);

        /// <summary>
        /// Rescan for other robots. This method is called automatically by the game,
        /// as long as the robot is moving, turning its body, turning its gun, or
        /// turning its radar.
        /// <p/>
        /// Rescan will cause <see cref="IBasicEvents.OnScannedRobot(ScannedRobotEvent)"/>
        /// to be called if you see a robot.
        /// <p/>
        /// There are 2 reasons to call rescan() manually:
        /// <ol>
        /// <li>You want to Scan after you Stop moving.</li>
        /// <li>You want to interrupt the OnScannedRobot evnt. This is more
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
        void rescan();
    }
}

//doc