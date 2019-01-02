/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
namespace Robocode.RobotInterfaces.Peer
{
    /// <summary>
    /// The standard robot peer for standard robot types like <see cref="Robocode.Robot"/>,
    /// <see cref="Robocode.AdvancedRobot"/>, and <see cref="Robocode.TeamRobot"/>.
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
        /// <see cref="Resume"/>. If there is already movement saved from a previous
        /// stop, you can overwrite it by calling Stop(true).
        /// <seealso cref="Resume"/>
        /// </summary>
        /// <param name="overwrite">
        ///   If there is already movement saved from a previous stop,
        ///   you can overwrite it by calling Stop(true).
        /// </param>
        void Stop(bool overwrite);

        /// <summary>
        /// Immediately resumes the movement you stopped by <see cref="Stop"/>, if any.
        /// <p/>
        /// This call executes immediately, and does not return until it is complete.
        ///
        /// <seealso cref="Stop"/>
        /// </summary>
        void Resume();

        /// <summary>
        /// Immediately turns the robot's radar to the right or left by radians.
        /// This call executes immediately, and does not return until it is complete,
        /// i.e. when the angle remaining in the radar's turn is 0.
        /// <p/>
        /// Note that both positive and negative values can be given as input, where
        /// positive values means that the robot's radar is set to turn right, and
        /// negative values means that the robot's radar is set to turn left.
        /// If 0 is given as input, the robot's radar will stop turning.
        /// <p/>
        /// <example>
        ///   <code>
        ///   // Turn the robot's radar 180 degrees to the right
        ///   TurnRadar(Math.PI);
        ///
        ///   // Afterwards, turn the robot's radar 90 degrees to the left
        ///   TurnRadar(-Math.PI / 2);
        ///   </code>
        /// </example>
        /// <seealso cref="IBasicRobotPeer.TurnBody"/>
        /// <seealso cref="IBasicRobotPeer.TurnGun"/>
        /// <seealso cref="IBasicRobotPeer.Move"/>
        /// </summary>
        /// <param name="radians">
        ///   The amount of radians to turn the robot's radar.
        ///   If radians &gt; 0 the robot's radar is set to turn right.
        ///   If radians &lt; 0 the robot's radar is set to turn left.
        ///   If radians = 0 the robot's radar is set to stop turning.
        /// </param>
        void TurnRadar(double radians);

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
        /// <example>
        /// Assuming both the robot and gun start Out facing up (0 degrees):
        ///   <code>
        ///   // Set gun to turn with the robot's turn
        ///   SetAdjustGunForBodyTurn(false); // This is the default
        ///   TurnBodyRight(Math.PI / 2);
        ///   // At this point, both the robot and gun are facing right (90 degrees)
        ///   TurnBodyLeft(Math.PI / 2);
        ///   // Both are back to 0 degrees
        ///   </code>
        ///   -- or --
        ///   <code>
        ///   // Set gun to turn independent from the robot's turn
        ///   SetAdjustGunForBodyTurn(true);
        ///   TurnBodyRight(Math.PI / 2);
        ///   // At this point, the robot is facing right (90 degrees), but the gun is still facing up.
        ///   TurnBodyLeft(Math.PI / 2);
        ///   // Both are back to 0 degrees.
        ///   </code>
        /// </example>
        /// <p/>
        /// Note: The gun compensating this way does count as "turning the gun".
        /// See <see cref="SetAdjustRadarForGunTurn"/> for details.
        /// <seealso cref="SetAdjustRadarForGunTurn"/>
        /// </summary>
        /// <param name="independent">
        ///   true if the gun must turn independent from the robot's turn;
        ///   false if the gun must turn with the robot's turn.
        /// </param>
        void SetAdjustGunForBodyTurn(bool independent);

        /// <summary>
        /// Sets the radar to turn independent from the gun's turn.
        /// <p/>
        /// Ok, so this needs some explanation: The radar is mounted on the robot's
        /// gun. So, normally, if the gun turns 90 degrees to the right, then the
        /// radar will turn with it as it is mounted on top of the gun. To compensate
        /// for this, you can call IsAdjustRadarForGunTurn(true). When this
        /// is set, the radar will turn independent from the robot's turn, i.e. the
        /// radar will compensate for the gun's turn.
        /// <p/>
        /// Note: This method is additive until you reach the maximum the radar can
        /// turn. The "adjust" is added to the amount you set for turning the robot,
        /// then capped by the physics of the game. If you turn infinite, then the
        /// adjust is ignored (and hence overridden).
        /// <p/>
        /// <example>
        /// Assuming both the gun and radar start Out facing up (0 degrees):
        ///   <code>
        ///   // Set radar to turn with the gun's turn
        ///   SetAdjustRadarForGunTurn(false); // This is the default
        ///   TurnGunRight(Math.PI / 2);
        ///   // At this point, both the radar and gun are facing right (90 degrees);
        ///   </code>
        ///   -- or --
        ///   <code>
        ///   // Set radar to turn independent from the gun's turn
        ///   SetAdjustRadarForGunTurn(true);
        ///   TurnGunRight(Math.PI / 2);
        ///   // At this point, the gun is facing right (90 degrees), but the radar is still facing up.
        ///   </code>
        /// </example>
        /// Note: Calling IsAdjustRadarForGunTurn(bool) will
        /// automatically call <see cref="SetAdjustRadarForBodyTurn"/> with the
        /// same value, unless you have already called it earlier. This behavior is
        /// primarily for backward compatibility with older Robocode robots.
        /// <seealso cref="SetAdjustRadarForBodyTurn"/>
        /// <seealso cref="SetAdjustGunForBodyTurn"/>
        /// </summary>
        /// <param name="independent">
        ///   true if the radar must turn independent from the gun's turn;
        ///   false if the radar must turn with the gun's turn.
        /// </param>
        void SetAdjustRadarForGunTurn(bool independent);

        /// <summary>
        /// Sets the radar to turn independent from the robot's turn.
        /// <p/>
        /// Ok, so this needs some explanation: The radar is mounted on the gun, and
        /// the gun is mounted on the robot's body. So, normally, if the robot turns
        /// 90 degrees to the right, the gun turns, as does the radar. Hence, if the
        /// robot turns 90 degrees to the right, then the gun and radar will turn
        /// with it as the radar is mounted on top of the gun. To compensate for
        /// this, you can call IsAdjustRadarForBodyTurn(true). When this is
        /// set, the radar will turn independent from the robot's turn, i.e. the
        /// radar will compensate for the robot's turn.
        /// <p/>
        /// Note: This method is additive until you reach the maximum the radar can
        /// turn. The "adjust" is added to the amount you set for turning the gun,
        /// then capped by the physics of the game. If you turn infinite, then the
        /// adjust is ignored (and hence overridden).
        /// <p/>
        /// <example>
        /// Assuming the robot, gun, and radar all start Out facing up (0 degrees):
        ///   <code>
        ///   // Set radar to turn with the robots's turn
        ///   SetAdjustRadarForBodyTurn(false); // This is the default
        ///   TurnRight(Math.PI / 2);
        ///   // At this point, the body, gun, and radar are all facing right (90 degrees);
        ///   </code>
        ///   -- or --
        ///   <code>
        ///   // Set radar to turn independent from the robot's turn
        ///   SetAdjustRadarForBodyTurn(true);
        ///   TurnRight(Math.PI / 2);
        ///   // At this point, the robot and gun are facing right (90 degrees), but the radar is still facing up.
        ///   </code>
        /// </example>
        /// <seealso cref="SetAdjustGunForBodyTurn"/>
        /// <seealso cref="SetAdjustRadarForGunTurn"/>
        /// </summary>
        /// <param name="independent">
        ///   true if the radar must turn independent from the robots's turn;
        ///   false if the radar must turn with the robot's turn.
        /// </param>
        void SetAdjustRadarForBodyTurn(bool independent);

        /// <summary>
        /// Checks if the gun is set to adjust for the robot turning, i.e. to turn
        /// independent from the robot's body turn.
        /// <p/>
        /// This call returns true if the gun is set to turn independent of
        /// the turn of the robot's body. Otherwise, false is returned,
        /// meaning that the gun is set to turn with the robot's body turn.
        /// <seealso cref="IsAdjustGunForBodyTurn"/>
        /// <seealso cref="IsAdjustRadarForBodyTurn"/>
        /// <seealso cref="IsAdjustRadarForGunTurn"/>
        /// </summary>
        bool IsAdjustGunForBodyTurn();

        /// <summary>
        /// Checks if the radar is set to adjust for the robot turning, i.e. to turn
        /// independent from the robot's body turn.
        /// <p/>
        /// This call returns true if the radar is set to turn independent of
        /// the turn of the robot. Otherwise, false is returned, meaning that
        /// the radar is set to turn with the robot's turn.
        /// <seealso cref="IsAdjustRadarForBodyTurn"/>
        /// <seealso cref="IsAdjustGunForBodyTurn"/>
        /// <seealso cref="IsAdjustRadarForGunTurn"/>
        /// </summary>
        bool IsAdjustRadarForGunTurn();

        /// <summary>
        /// Checks if the radar is set to adjust for the gun turning, i.e. to turn
        /// independent from the gun's turn.
        /// <p/>
        /// This call returns true if the radar is set to turn independent of
        /// the turn of the gun. Otherwise, false is returned, meaning that
        /// the radar is set to turn with the gun's turn.
        /// <seealso cref="IsAdjustRadarForGunTurn"/>
        /// <seealso cref="IsAdjustGunForBodyTurn"/>
        /// <seealso cref="IsAdjustRadarForBodyTurn"/>
        /// </summary>
        bool IsAdjustRadarForBodyTurn();
    }
}

//doc