/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * This is private interface. You should build any external component (or robot)
 * based on it's current methods because it will change in the future.
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *     - Added getGraphics()
 *     Flemming N. Larsen
 *     - Javadocs
 *******************************************************************************/
using System.Drawing;

namespace robocode.robotinterfaces.peer
{
    /// <summary>
    /// The basic robot peer for all robot types.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    ///
    /// @author Pavel Savara (original)
    /// @author Flemming N. Larsen (javadoc)
    /// @see IStandardRobotPeer
    /// @see IAdvancedRobotPeer
    /// @see ITeamRobotPeer
    /// @see IJuniorRobotPeer
    /// @since 1.6
    /// </summary>
    public interface IBasicRobotPeer
    {
        /// <summary>
        /// Returns the robot's name.
        ///
        /// @return the robot's name.
        /// </summary>
        string getName();

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
        long getTime();

        /// <summary>
        /// Returns the robot's current energy.
        ///
        /// @return the robot's current energy.
        /// </summary>
        double getEnergy();

        /// <summary>
        /// Returns the X position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        ///
        /// @return the X position of the robot.
        /// @see #getY()
        /// </summary>
        double getX();

        /// <summary>
        /// Returns the Y position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        ///
        /// @return the Y position of the robot.
        /// @see #getX()
        /// </summary>
        double getY();

        /// <summary>
        /// Returns the velocity of the robot measured in pixels/turn.
        /// <p/>
        /// The maximum velocity of a robot is defined by
        /// {@link Rules#MAX_VELOCITY} (8 pixels / turn).
        ///
        /// @return the velocity of the robot measured in pixels/turn.
        /// @see Rules#MAX_VELOCITY
        /// </summary>
        double getVelocity();

        /// <summary>
        /// Returns the direction that the robot's body is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///
        /// @return the direction that the robot's body is facing, in radians.
        /// @see #getGunHeading()
        /// @see #getRadarHeading()
        /// </summary>
        double getBodyHeading();

        /// <summary>
        /// Returns the direction that the robot's gun is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///
        /// @return the direction that the robot's gun is facing, in radians.
        /// @see #getBodyHeading()
        /// @see #getRadarHeading()
        /// </summary>
        double getGunHeading();

        /// <summary>
        /// Returns the direction that the robot's radar is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 4 means West.
        ///
        /// @return the direction that the robot's radar is facing, in radians.
        /// @see #getBodyHeading()
        /// @see #getGunHeading()
        /// </summary>
        double getRadarHeading();

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
        /// @see #SetFire(double)
        /// </summary>
        double getGunHeat();

        /// <summary>
        /// Returns the width of the current battlefield measured in pixels.
        ///
        /// @return the width of the current battlefield measured in pixels.
        /// </summary>
        double getBattleFieldWidth();

        /// <summary>
        /// Returns the height of the current battlefield measured in pixels.
        ///
        /// @return the height of the current battlefield measured in pixels.
        /// </summary>
        double getBattleFieldHeight();

        /// <summary>
        /// Returns how many opponents that are left in the current round.
        ///
        /// @return how many opponents that are left in the current round.
        /// </summary>
        int getOthers();

        /// <summary>
        /// Returns the number of rounds in the current battle.
        ///
        /// @return the number of rounds in the current battle
        /// @see #getRoundNum()
        /// </summary>
        int getNumRounds();

        /// <summary>
        /// Returns the number of the current round (0 to {@link #getNumRounds()} - 1)
        /// in the battle.
        ///
        /// @return the number of the current round in the battle
        /// @see #getNumRounds()
        /// </summary>
        int getRoundNum();

        /// <summary>
        /// Returns the rate at which the gun will cool down, i.e. the amount of heat
        /// the gun heat will drop per turn.
        /// <p/>
        /// The gun cooling rate is default 0.1 / turn, but can be changed by the
        /// battle setup. So don't count on the cooling rate being 0.1!
        ///
        /// @return the gun cooling rate
        /// @see #GetGunHeat()
        /// @see #SetFire(double)
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
        ///
        /// @return the distance remaining in the robot's current move measured in
        ///         pixels.
        /// @see #getBodyTurnRemaining()
        /// @see #getGunTurnRemaining()
        /// @see #getRadarTurnRemaining()
        /// </summary>
        double getDistanceRemaining();

        /// <summary>
        /// Returns the angle remaining in the robot's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the robot is currently turning to the right. Negative values
        /// means that the robot is currently turning to the left.
        ///
        /// @return the angle remaining in the robot's turn, in radians
        /// @see #getDistanceRemaining()
        /// @see #getGunTurnRemaining()
        /// @see #getRadarTurnRemaining()
        /// </summary>
        double getBodyTurnRemaining();

        /// <summary>
        /// Returns the angle remaining in the gun's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the gun is currently turning to the right. Negative values
        /// means that the gun is currently turning to the left.
        ///
        /// @return the angle remaining in the gun's turn, in radians
        /// @see #getDistanceRemaining()
        /// @see #getBodyTurnRemaining()
        /// @see #getRadarTurnRemaining()
        /// </summary>
        double getGunTurnRemaining();

        /// <summary>
        /// Returns the angle remaining in the radar's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values. Positive values
        /// means that the radar is currently turning to the right. Negative values
        /// means that the radar is currently turning to the left.
        ///
        /// @return the angle remaining in the radar's turn, in radians
        /// @see #getDistanceRemaining()
        /// @see #getBodyTurnRemaining()
        /// @see #getGunTurnRemaining()
        /// </summary>
        double getRadarTurnRemaining();

        /// <summary>
        /// Executes any pending actions, or continues executing actions that are
        /// in process. This call returns after the actions have been started.
        /// <p/>
        /// Note that advanced robots <em>must</em> call this function in order to
        /// Execute pending set* calls like e.g. {@link
        /// IAdvancedRobotPeer#setMove(double) setMove(double)},
        /// {@link IAdvancedRobotPeer#SetFire(double) SetFire(double)}, {@link
        /// IAdvancedRobotPeer#setTurnBody(double) setTurnBody(double)} etc.
        /// Otherwise, these calls will never get executed.
        /// <p/>
        /// In this example the robot will move while turning:
        /// <pre>
        ///   setTurnBody(90);
        ///   setMove(100);
        ///   Execute();
        /// <p/>
        ///   while (getDistanceRemaining() > 0 && getTurnRemaining() > 0) {
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
        /// Example:
        /// <pre>
        ///   // Move the robot 100 pixels forward
        ///   Ahead(100);
        /// <p/>
        ///   // Afterwards, move the robot 50 pixels backward
        ///   Ahead(-50);
        /// </pre>
        ///
        /// @param distance the distance to move measured in pixels.
        ///                 If {@code distance} > 0 the robot is set to move forward.
        ///                 If {@code distance} < 0 the robot is set to move backward.
        ///                 If {@code distance} = 0 the robot will not move anywhere, but just
        ///                 finish its turn.
        /// @see robocode.robotinterfaces.IBasicEvents#OnHitWall(robocode.HitWallEvent)
        /// @see robocode.robotinterfaces.IBasicEvents#OnHitRobot(robocode.HitRobotEvent)
        /// </summary>
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
        /// Example:
        /// <pre>
        ///   // Turn the robot's body 180 degrees to the right
        ///   turnBody(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's body 90 degrees to the left
        ///   turnBody(-Math.PI / 2);
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's body.
        ///                If {@code radians} > 0 the robot's body is set to turn right.
        ///                If {@code radians} < 0 the robot's body is set to turn left.
        ///                If {@code radians} = 0 the robot's body is set to Stop turning.
        /// @see #turnGun(double)
        /// @see IStandardRobotPeer#turnRadar(double) turnRadar(double)
        /// @see #move(double)
        /// </summary>
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
        /// Example:
        /// <pre>
        ///   // Turn the robot's gun 180 degrees to the right
        ///   turnGun(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's gun 90 degrees to the left
        ///   turnGun(-Math.PI / 2);
        /// </pre>
        ///
        /// @param radians the amount of radians to turn the robot's gun.
        ///                If {@code radians} > 0 the robot's gun is set to turn right.
        ///                If {@code radians} < 0 the robot's gun is set to turn left.
        ///                If {@code radians} = 0 the robot's gun is set to Stop turning.
        /// @see #turnBody(double)
        /// @see IStandardRobotPeer#turnRadar(double) turnRadar(double)
        /// @see #move(double)
        /// </summary>
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
        ///       Bullet bullet = Fire(Rules.MAX_BULLET_POWER);
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
        /// @see #SetFire(double)
        /// @see Bullet
        /// @see #GetGunHeat()
        /// @see #getGunCoolingRate()
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletHit(BulletHitEvent)
        ///      OnBulletHit(BulletHitEvent)
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletHitBullet(BulletHitBulletEvent)
        ///      OnBulletHitBullet(BulletHitBulletEvent)
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletMissed(BulletMissedEvent)
        ///      OnBulletMissed(BulletMissedEvent)
        /// </summary>
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
        ///       double bulletVelocity = bullet.getVelocity();
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
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletHit(BulletHitEvent)
        ///      OnBulletHit(BulletHitEvent)
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletHitBullet(BulletHitBulletEvent)
        ///      OnBulletHitBullet(BulletHitBulletEvent)
        /// @see robocode.robotinterfaces.IBasicEvents#OnBulletMissed(BulletMissedEvent)
        ///      OnBulletMissed(BulletMissedEvent)
        /// </summary>
        Bullet setFire(double power);

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
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
        void setBodyColor(Color color);

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
        /// @see #setBodyColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
        void setGunColor(Color color);

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
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setBulletColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
        void setRadarColor(Color color);

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
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setScanColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
        void setBulletColor(Color color);

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
        /// @see #setBodyColor(Color)
        /// @see #setGunColor(Color)
        /// @see #setRadarColor(Color)
        /// @see #setBulletColor(Color)
        /// @see Color
        /// @since 1.1.2
        /// </summary>
        void setScanColor(Color color);

        /// <summary>
        /// This call <em>must</em> be made from a robot call to inform the game
        /// that the robot made a {@code get*} call like e.g. {@link #getX()} or
        /// {@link #getVelocity()}.
        /// <p/>
        /// This method is used by the game to determine if the robot is inactive or
        /// not. Note: You should only make this call once in a {@code get*} method!
        ///
        /// @see #setCall()
        /// </summary>
        void getCall();

        /// <summary>
        /// This call <em>must</em> be made from a robot call to inform the game
        /// that the robot made a {@code set*} call like e.g. {@link
        /// #SetFire(double)} or {@link #setBodyColor(Color)}.
        /// <p/>
        /// This method is used by the game to determine if the robot is inactive or
        /// not. Note: You should only make this call once in a {@code set*} method!
        ///
        /// @see #getCall()
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
        ///
        /// @return a graphics context used for painting graphical items for the robot.
        /// @see robocode.robotinterfaces.IPaintEvents#OnPaint(Graphics)
        /// @since 1.6.1
        /// </summary>
        IGraphics getGraphics();

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
        void setDebugProperty(string key, string value);

        /// <summary>
        /// Rescan for other robots. This method is called automatically by the game,
        /// as long as the robot is moving, turning its body, turning its gun, or
        /// turning its radar.
        /// <p/>
        /// Rescan will cause {@link IBasicEvents#OnScannedRobot(ScannedRobotEvent)
        /// OnScannedRobot(ScannedRobotEvent)} to be called if you see a robot.
        /// <p/>
        /// There are 2 reasons to call {@code rescan()} manually:
        /// <ol>
        /// <li>You want to Scan after you Stop moving.
        /// <li>You want to interrupt the {@code OnScannedRobot} evnt. This is more
        /// likely. If you are in {@code OnScannedRobot} and call {@code Scan()},
        /// and you still see a robot, then the system will interrupt your
        /// {@code OnScannedRobot} evnt immediately and start it from the top.
        /// </ol>
        /// <p/>
        /// This call executes immediately.
        ///
        /// @see IBasicEvents#OnScannedRobot(ScannedRobotEvent)
        ///      OnScannedRobot(ScannedRobotEvent)
        /// @see ScannedRobotEvent
        ///
        /// @since 1.7.2
        /// </summary>
        void rescan();
    }
}
//happy