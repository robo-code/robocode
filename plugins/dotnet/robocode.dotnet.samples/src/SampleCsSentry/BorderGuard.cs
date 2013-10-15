#region Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2013 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Drawing;
using System.Collections.Generic;
using Robocode;
using Robocode.Util;

namespace SampleCsSentry
{
    /// <summary>
    /// BorderGuard - a sample sentry robot that guards the borders of the battlefield by Flemming N. Larsen
    /// </summary>
    public class BorderGuard
        : AdvancedRobot, IBorderSentry
    {
        // Dictionary used retrieve the scanned data for a robot based on the robot name
        IDictionary<String /* robot name */, ScannedRobotData> scannedRobotData =
            new Dictionary<String, ScannedRobotData>();

        // Current target robot, which we our robot should fire at
        ScannedRobotData target;

        // Last time when the robot shifted its direction
        long lastDirectionShift;

        // Current direction, where 1 means ahead/forward, and -1 means back
        int direction = 1;

        // The main method of the robot, which is called when the robot started up.
        public override void Run()
        {
            // Do initialization stuff..

            // Let the radar turn be independent on the gun turn
            IsAdjustRadarForGunTurn = true;
            // Let the gun turn be independent on the robot (body) turn
            IsAdjustGunForRobotTurn = true;

            // Set the robot body and turret/gun to black, and the radar, bullets and scan arc to red
            SetColors(Color.Black, Color.Black, Color.Red, Color.Red, Color.Red);

            // Set the radar to turn left as fast as possible forever.
            // Note, that it will take 8 turns to turn the radar 360 degrees in total (360 / 45 degrees)
            SetTurnRadarLeft(double.PositiveInfinity);

            // Loop forever (if the robot stops doing anything, the game will disable it)
            while (true)
            {
                // Prepare next turn...

                // Move the robot
                Move();

                // Execute all pending robot commands, meaning that the robot will now take action!
                // This means that commands like, SetFire(), SetTurnLeft(), SetTurnRadarRight() etc. will be executed.
                Execute();
            }
        }

        /// <summary>
        /// This method is called by the game before each turn with the current status of our robot.
        /// </summary>
        /// <param name="statusEvent">The status event containing status data for our robot.</param>
        public override void OnStatus(StatusEvent statusEvent)
        {
            // 'newTarget' is used for selecting our new target to fire at.
            // Set this to null here, which means that we have no target... yet!
            ScannedRobotData newTarget = null;

            // Prepara a list for containing all scanned robot data.
            List<ScannedRobotData> currentScanData = new List<ScannedRobotData>();

            // Add scanned robot data that is not too old. When data is more than 360 / 45 degrees = 8 turns old,
            // our radar should retrieve new information about a particular robot, unless it has died or is
            // out of the scan radius (1200 units). Anyways, the robot will probably have moved far ways since then.

            foreach (ScannedRobotData robotData in scannedRobotData.Values)
            {
                // Add entry if the the delta time (scanned event time - status event time) is less than or equal to 8 turns
                if (statusEvent.Time - robotData.time <= 8)
                {
                    currentScanData.Add(robotData);
                }
            }

            // Now, make a copy of current scan data that should contain target candidates
            List<ScannedRobotData> targetCandidates = new List<ScannedRobotData>(currentScanData);

            // Prepare a new list for containing scanned robot data, but only with robots
            List<ScannedRobotData> newCurrentScanData = new List<ScannedRobotData>();

            // Add all target candidates that are inside our sentry robot's attack range as we
            // will not be able to harm robots outside this range with our gun fire (is a game rule).
            foreach (ScannedRobotData robotData in currentScanData)
            {
                // Add robot data if its location is inside the sentry attack range
                if (IsInsideSentryAttackRange(robotData.x, robotData.y))
                {
                    newCurrentScanData.Add(robotData);
                }
            }
            // Save the new list containing robot scan data as the current robot scan data list
            currentScanData = newCurrentScanData;

            // If we don't have any target candidates, we have no candidates to fire at.
            // However, we will then use the current scan data for moving out robot as close to the nearest robot
            // outside the sentry robot's attack range, so we have a better chance to hit it when it moves too
            // close to the border.
            if (targetCandidates.Count == 0)
            {
                targetCandidates = new List<ScannedRobotData>(currentScanData);
            }

            // Now, pick the new target robot among the target candidates based on how far those are to our robot.
            // Pick the robot closest to our robot.

            double shortestDistance = double.MaxValue; // Initialize shortest distance seen, to maximum value

            foreach (ScannedRobotData robotData in currentScanData)
            {
                // Calculate the distance to the robot based on the hypotenuse (Pythagoras)
                double distance = DistanceTo(robotData.x, robotData.y);
                // Check if the distance is shorter than shortest distance calculated so far
                if (distance < shortestDistance)
                {
                    // If the distance is shorter then save the calculated distance as the new shortest distance
                    shortestDistance = distance;
                    // Save the robot data as our new target robot
                    newTarget = robotData;
                }
            }

            // Check if we got a new target robot
            if (newTarget != null)
            {
                // We got a new target robot..

                // Calculate the target angle of the robot
                double targetAngle = Math.Atan2(newTarget.x - X, newTarget.y - Y);
                // Normalize the target angle so that the angle is kept within the range [0, PI[
                targetAngle = Utils.NormalAbsoluteAngle(targetAngle);

                // Calculate the delta gun angle, i.e. how much of robot should be moved to the right.
                double deltaGunAngle = Utils.NormalRelativeAngle(targetAngle - GunHeadingRadians);

                // Set the gun to turn right the number of radians stored in the calculated delta gun angle.
                // Note: The setTurnGunRightRadians() will first be executed, when the execute() method is called.
                // If the delta gun angle is negative, it will automatically be moved in the opposite direction.
                SetTurnGunRightRadians(deltaGunAngle);

                // Check that the radar turn has almost completed (has less than 1 degrees left to turn) and
                // that the target robot is inside the entry attack range.
                if (RadarTurnRemaining < 1 && IsInsideSentryAttackRange(newTarget.x, newTarget.y))
                {
                    // Calculate the fire power we should use when firing at our target..

                    // We want to use the maximum bullet power 3, when the target is within 100 units and
                    // the bullet power should become lesser the longer the distance is to our target.
                    // Hence, we calculate the fire power as 3 * 100 / distance to target.
                    double firePower = 300 / DistanceTo(newTarget.x, newTarget.y);

                    // Set our gun to fire with the calculated amount of fire power.
                    // Note: If the fire power is less than the minimum fire power 0.1 the gun will not fire.
                    // If the fire power is greater than the maximum fire power 3, then gun will use 3 as fire power.
                    SetFire(firePower);
                }

                // Paint a circle on our current target (debugging graphics)
                PaintCircle(newTarget.x, newTarget.y);
            }

            // Set our new target as our new "global" target, so that other methods can use it
            this.target = newTarget;
        }

        /// <summary>
        /// This method is called by the game whenever our robot has scanned another robot by turning the radar.
        /// Note: Our radar is turning around forever at maximum speed, so this event is called as soon as the
        /// radar "hits" another robot.
        /// </summary>
        /// <param name="scannedRobotEvent">The scanned robot event containing data about the scanned robot.</param>
        public override void OnScannedRobot(ScannedRobotEvent scannedRobotEvent)
        {
            // Exit this method, if the scanned robot is another sentry robot
            if (scannedRobotEvent.IsSentryRobot)
            {
                return;
            }

            // Get the distance from our robot to the scanned robot
            double distance = scannedRobotEvent.Distance;
            // Calculate the angle in radians to the scanned robot.
            // Angle = our robot's heading (angle) + the bearing (delta angle) to the scanned robot.
            double angle = HeadingRadians + scannedRobotEvent.BearingRadians;

            // Prepare an entry with scanned robot data that we can store in our scanned data map
            ScannedRobotData robotData = new ScannedRobotData();
            // Store the name of the scanned robot
            robotData.name = scannedRobotEvent.Name;
            // Store the time when the robot was scanned
            robotData.time = scannedRobotEvent.Time;
            // Calculate and store the x coordinate of the scanned robot (using the Robocode coordinate system)
            robotData.x = X + Math.Sin(angle) * distance;
            // Calculate and store the y coordinate of the scanned robot (using the Robocode coordinate system)
            robotData.y = Y + Math.Cos(angle) * distance;

            // Store the entry of scanned robot entry in our map over scanned robot data.
            // We use the name of the robot as the key for accessing the scanned data for that particular robot later
            scannedRobotData.Remove(robotData.name);
            scannedRobotData.Add(robotData.name, robotData);
        }

        /// <summary>
        /// This method is called by the game whenever another robot dies.
        /// </summary> 
        /// <param name="robotDeathEvent">The robot death event containing data about the robot that died.</param>
        public override void OnRobotDeath(RobotDeathEvent robotDeathEvent)
        {
            // Remove the scanned robot data for the robot that died.
            // This data is useless now, and will only confuse our robot if it stays in our scanned robot data.
            scannedRobotData.Remove(robotDeathEvent.Name);

            // Remove our global target, if our target was the robot that died, but only if we have a current target
            if (target != null && target.name.Equals(robotDeathEvent.Name))
            {
                target = null;
            }
        }

        /// <summary>
        /// Method for moving our robot around on the battlefield.
        /// </summary>
        private void Move()
        {
            // The movement strategy is to move as close to our target robot as possible.
            // Our robot should move along the borders all the time vertically or horizontally.
            // When we get close to our target or have no where to go, our robot should shift its direction
            // from side to side, so it does not stand still at any time.

            // Get close to our target if we have a target robot
            if (target != null)
            {
                // Calculate the range from the walls/borders, our robot should keep within
                int borderRange = SentryBorderSize - 20;

                // The horizontal and vertical flags are used for determining, if our robot should
                // move horizontal or vertical.
                bool horizontal = false;
                bool vertical = false;

                // Set the about of radians we should turn towards the left
                double turnLeft = HeadingRadians;

                // Check if our robot is at the upper or lower border and hence should move horizontally
                if (Y < borderRange || Y > BattleFieldHeight - borderRange)
                {
                    horizontal = true;
                }
                // Check if our robot is at the left or right border and hence should move vertically
                if (X < borderRange || X > BattleFieldWidth - borderRange)
                {
                    vertical = true;
                }

                // If we are in one of the corners of the battlefield, we could move both horizontally or vertically.
                // In this situation, we need to choose one of the two directions.
                if (horizontal && vertical)
                {
                    // If the horizontal distance to our target is lesser than the vertical distance,
                    // then we choose to move vertically, and hence we clear the horizontal flag.
                    if (Math.Abs(target.x - X) <= Math.Abs(target.y - Y))
                    {
                        horizontal = false; // Do not move horizontally => move vertically
                    }
                }
                // Adjust the heading of our robot with 90 degrees, if it must move horizontally.
                // Otherwise the calculated heading is towards moving vertically.
                if (horizontal)
                {
                    turnLeft -= Math.PI / 2;
                }
                // Set the robot to turn left the amount of radians we have just calculated
                SetTurnLeftRadians(Utils.NormalRelativeAngle(turnLeft));

                // Check if our robot has finished turning, i.e. has less than 1 degrees left to turn
                if (TurnRemaining < 1)
                {
                    // If we should move horizontally, the set the robot to move ahead with
                    // the horizontal distance to the target robot. Otherwise, use the vertical distance.
                    if (horizontal)
                    {
                        SetAhead(target.x - X);
                    }
                    else
                    {
                        SetAhead(target.y - Y);
                    }
                }
            }

            // Check if the absolute distance remaining of our current robot move is less than 40 and
            // that at least 25 turns have passed since the last time our robot shifted its direction.
            if (Math.Abs(DistanceRemaining) < 40 && (Time - lastDirectionShift > 25))
            {
                // Record the current time as the new 'last direction shift time'
                lastDirectionShift = Time;
                // Change the direction, which should shift between 1 and -1.
                // Hence we simply multiply the direction variable with -1.
                direction *= -1;
                // Force the robot to move either 100 units forward or back depending on the current direction.
                Ahead(100 * direction);
            }
        }

        /// <summary>
        /// This method checks if a point (x, y) is inside of the attack range for a SentryRobot.
        /// </summary>
        /// <param name="x">The x coordinate.</param>
        /// <param name="y">The y coordinate.</param>
        /// <returns><code>true<code> if the point is inside the attack range of sentry robots; <code>false</code> otherwise.</returns>
        private bool IsInsideSentryAttackRange(double x, double y)
        {
            return !(x > SentryBorderSize && // minimum border x
                     y > SentryBorderSize && // minimum border y 
                     x < ((int)BattleFieldWidth - SentryBorderSize) && // maximum border x
                     y < ((int)BattleFieldHeight - SentryBorderSize)); // maximum border y
        }

        /// <summary>
        /// Returns the distance to a point (x, y) from our robot.
        /// </summary>
        /// <param name="x">The x coordinate.</param>
        /// <param name="y">The y coordinate.</param>
        /// <returns>The distance to the point from our robot.</returns>
        private double DistanceTo(double x, double y)
        {
            // Calculate the distance to the robot based on the hypotenuse (Pythagoras)
            return Math.Sqrt(Math.Pow(x - X, 2) + Math.Pow(y - Y, 2));
        }

        /// <summary>
        /// Paints a yellow circle around a point (x, y).
        /// </summary>
        /// <param name="x">The x coordinate for the center of the circle.</param>
        /// <param name="y">The y coordinate for the center of the circle.</param>
        private void PaintCircle(double x, double y)
        {
            // Set the pen color to the yellow color width of 2 pixels
            Pen pen = new Pen(Color.Yellow, 2);
            // Draw a circle (oval) that has a radius of 20 pixels with the center in the input coordinates.
            Graphics.DrawEllipse(new Pen(Color.Yellow), new Rectangle((int)x - 20, (int)y - 20, 40, 40));
        }

        /// <summary>
        /// This class is used for storing data about a robot that has been scanned.
        /// The data is a snapshot containing the name of the robot that was scanned at a specific time,
        /// and the location (x, y) of the robot at that time.
        /// </summary>
        class ScannedRobotData
        {
            internal string name; // name of the scanned robot
            internal long time; // time when the robot was scanned
            internal double x; // x coordinate of the robot position 
            internal double y; // y coordinate of the robot position
        }
    }
}