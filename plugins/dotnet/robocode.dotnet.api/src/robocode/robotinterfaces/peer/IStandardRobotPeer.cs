#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

namespace robocode.robotinterfaces.peer
{
    /// <summary>
    /// The standard robot peer for standard robot types like <see cref="robocode.Robot"/>,
    /// <see cref="robocode.AdvancedRobot"/>, and <see cref="robocode.TeamRobot"/>.
    /// <p/>
    /// A robot peer is the obj that deals with game mechanics and rules, and
    /// makes sure your robot abides by them.
    /// <seealso cref="IBasicRobotPeer"/>
    /// <seealso cref="IAdvancedRobotPeer"/>
    /// <seealso cref="ITeamRobotPeer"/>
    /// <seealso cref="IJuniorRobotPeer"/>
    /// </summary>
    public interface IStandardRobotPeer : IBasicRobotPeer
    {
        /// <summary>
        /// Immediately stops all movement, and saves it for a call to
        /// <see cref="resume()"/>. If there is already movement saved from a previous
        /// Stop, you can overwrite it by calling Stop(true).
        /// <seealso cref="resume()"/>
        /// </summary>
        /// <param name="overwrite">If there is already movement saved from a previous Stop,
        ///                  you can overwrite it by calling Stop(true).</param>
        void stop(bool overwrite);

        /// <summary>
        /// Immediately resumes the movement you stopped by <see cref="stop(bool)"/>,
        /// if any.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete.
        ///
        /// <seealso cref="stop(bool)"/>
        /// </summary>
        void resume();

        /// <summary>
        /// Immediately turns the robot's radar to the right or left by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the radar's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's radar is set to turn right, and
        /// negative values means that the robot's radar is set to turn left.
        /// If 0 is given as input, the robot's radar will Stop turning.
        /// <p/>
        /// <example>
        /// <pre>
        ///   // Turn the robot's radar 180 degrees to the right
        ///   turnRadar(Math.PI);
        /// <p/>
        ///   // Afterwards, turn the robot's radar 90 degrees to the left
        ///   turnRadar(-Math.PI / 2);
        /// </pre>
        /// </example>
        /// <seealso cref="IBasicRobotPeer.turnBody(double)"/>
        /// <seealso cref="IBasicRobotPeer.turnGun(double)"/>
        /// <seealso cref="IBasicRobotPeer.move(double)"/>
        /// </summary>
        /// <param name="radians">the amount of radians to turn the robot's radar.
        ///                If radians &gt; 0 the robot's radar is set to turn right.
        ///                If radians &lt; 0 the robot's radar is set to turn left.
        ///                If radians = 0 the robot's radar is set to Stop turning.</param>
        void turnRadar(double radians);

        /// <summary>
        /// Sets the gun to turn independent from the robot's turn.
        /// <p/>
        /// Ok, so this needs some explanation: The gun is mounted on the robot's
        /// body. So, normally, if the robot turns 90 degrees to the right, then the
        /// gun will turn with it as it is mounted on top of the robot's body. To
        /// compensate for this, you can call IsAdjustGunForBodyTurn(true).
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
        ///   setAdjustGunForBodyTurn(false); // This is the default
        ///   turnBodyRight(Math.PI / 2);
        ///   // At this point, both the robot and gun are facing right (90 degrees)
        ///   turnBodyLeft(Math.PI / 2);
        ///   // Both are Back to 0 degrees
        /// <p/>
        ///   -- or --
        /// <p/>
        ///   // Set gun to turn independent from the robot's turn
        ///   setAdjustGunForBodyTurn(true);
        ///   turnBodyRight(Math.PI / 2);
        ///   // At this point, the robot is facing right (90 degrees), but the gun is still facing up.
        ///   turnBodyLeft(Math.PI / 2);
        ///   // Both are Back to 0 degrees.
        /// </pre>
        /// <p/>
        /// Note: The gun compensating this way does count as "turning the gun".
        /// See <see cref="setAdjustRadarForGunTurn(bool)"/> for details.
        /// <seealso cref="setAdjustRadarForGunTurn(bool)"/>
        /// </summary>
        /// <param name="independent">true if the gun must turn independent from the
        ///                    robot's turn; false if the gun must turn with the robot's turn.</param>
        void setAdjustGunForBodyTurn(bool independent);

        /// <summary>
        /// Sets the radar to turn independent from the gun's turn.
        /// <p/>
        /// Ok, so this needs some explanation: The radar is mounted on the robot's
        /// gun. So, normally, if the gun turns 90 degrees to the right, then the
        /// radar will turn with it as it is mounted on top of the gun. To compensate
        /// for this, you can call isAdjustRadarForGunTurn(true). When this
        /// is set, the radar will turn independent from the robot's turn, i.e. the
        /// radar will compensate for the gun's turn.
        /// <p/>
        /// Note: This method is additive until you reach the maximum the radar can
        /// turn. The "adjust" is added to the amount you set for turning the robot,
        /// then capped by the physics of the game. If you turn infinite, then the
        /// adjust is ignored (and hence overridden).
        /// <p/>
        /// Example, assuming both the gun and radar start Out facing up (0 degrees):
        /// <pre>
        ///   // Set radar to turn with the gun's turn
        ///   setAdjustRadarForGunTurn(false); // This is the default
        ///   TurnGunRight(Math.PI / 2);
        ///   // At this point, both the radar and gun are facing right (90 degrees);
        /// <p/>
        ///   -- or --
        /// <p/>
        ///   // Set radar to turn independent from the gun's turn
        ///   setAdjustRadarForGunTurn(true);
        ///   TurnGunRight(Math.PI / 2);
        ///   // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
        /// </pre>
        /// Note: Calling isAdjustRadarForGunTurn(bool) will
        /// automatically call <see cref="setAdjustRadarForBodyTurn(bool)"/> with the
        /// same value, unless you have already called it earlier. This behavior is
        /// primarily for backward compatibility with older Robocode robots.
        /// <seealso cref="setAdjustRadarForBodyTurn(bool)"/>
        /// <seealso cref="setAdjustGunForBodyTurn(bool)"/>
        /// </summary>
        /// <param name="independent">true if the radar must turn independent from
        ///                    the gun's turn; false if the radar must turn with the gun's
        ///                    turn.</param>
        void setAdjustRadarForGunTurn(bool independent);

        /// <summary>
        /// Sets the radar to turn independent from the robot's turn.
        /// <p/>
        /// Ok, so this needs some explanation: The radar is mounted on the gun, and
        /// the gun is mounted on the robot's body. So, normally, if the robot turns
        /// 90 degrees to the right, the gun turns, as does the radar. Hence, if the
        /// robot turns 90 degrees to the right, then the gun and radar will turn
        /// with it as the radar is mounted on top of the gun. To compensate for
        /// this, you can call isAdjustRadarForBodyTurn(true). When this is
        /// set, the radar will turn independent from the robot's turn, i.e. the
        /// radar will compensate for the robot's turn.
        /// <p/>
        /// Note: This method is additive until you reach the maximum the radar can
        /// turn. The "adjust" is added to the amount you set for turning the gun,
        /// then capped by the physics of the game. If you turn infinite, then the
        /// adjust is ignored (and hence overridden).
        /// <p/>
        /// Example, assuming the robot, gun, and radar all start Out facing up (0
        /// degrees):
        /// <pre>
        ///   // Set radar to turn with the robots's turn
        ///   setAdjustRadarForBodyTurn(false); // This is the default
        ///   TurnRight(Math.PI / 2);
        ///   // At this point, the body, gun, and radar are all facing right (90 degrees);
        /// <p/>
        ///   -- or --
        /// <p/>
        ///   // Set radar to turn independent from the robot's turn
        ///   setAdjustRadarForBodyTurn(true);
        ///   TurnRight(Math.PI / 2);
        ///   // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
        /// </pre>
        /// <seealso cref="setAdjustGunForBodyTurn(bool)"/>
        /// <seealso cref="setAdjustRadarForGunTurn(bool)"/>
        /// </summary>
        /// <param name="independent">true if the radar must turn independent from
        ///                    the robots's turn; false if the radar must turn with the robot's
        ///                    turn.</param>
        void setAdjustRadarForBodyTurn(bool independent);

        /// <summary>
        /// Checks if the gun is set to adjust for the robot turning, i.e. to turn
        /// independent from the robot's body turn.
        /// <p/>
        /// This call returns true if the gun is set to turn independent of
        /// the turn of the robot's body. Otherwise, false is returned,
        /// meaning that the gun is set to turn with the robot's body turn.
        /// <seealso cref="isAdjustGunForBodyTurn()"/>
        /// <seealso cref="isAdjustRadarForBodyTurn()"/>
        /// <seealso cref="isAdjustRadarForGunTurn()"/>
        /// </summary>
        bool isAdjustGunForBodyTurn();

        /// <summary>
        /// Checks if the radar is set to adjust for the robot turning, i.e. to turn
        /// independent from the robot's body turn.
        /// <p/>
        /// This call returns true if the radar is set to turn independent of
        /// the turn of the robot. Otherwise, false is returned, meaning that
        /// the radar is set to turn with the robot's turn.
        /// <seealso cref="isAdjustRadarForBodyTurn()"/>
        /// <seealso cref="isAdjustGunForBodyTurn()"/>
        /// <seealso cref="isAdjustRadarForGunTurn()"/>
        /// </summary>
        bool isAdjustRadarForGunTurn();

        /// <summary>
        /// Checks if the radar is set to adjust for the gun turning, i.e. to turn
        /// independent from the gun's turn.
        /// <p/>
        /// This call returns true if the radar is set to turn independent of
        /// the turn of the gun. Otherwise, false is returned, meaning that
        /// the radar is set to turn with the gun's turn.
        /// <seealso cref="isAdjustRadarForGunTurn()"/>
        /// <seealso cref="isAdjustGunForBodyTurn()"/>
        /// <seealso cref="isAdjustRadarForBodyTurn()"/>
        /// </summary>
        bool isAdjustRadarForBodyTurn();
    }
}

//happy