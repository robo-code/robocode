/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Drawing;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Robocode;
using Robocode.Util;

namespace SampleCsSentry
{
    /// <summary>
    /// BorderGuard - is a sample robot that demonstrates how the BorderSentry interface can be used for
    /// creating a robot that guards the border area of the battle field, and which is efficient against
    /// robots trying to hide at corners and robots sneaking around near the borders.
    /// <p>
    /// This robot is somewhat advanced due to:<br>
    /// 1) it uses linear targeting for predicting how to hit robots that moves in straight lines.<br>
    /// 2) it will only fire at a robot, if it figures out that its bullets will do damage to that
    /// particular robot, e.g. when the robots predicted future position will be within the sentry border
    /// attack range.<br>
    /// 3) it has an efficient scanner that keeps the scan angle as little as possible to get as new
    /// scanned data for enemy robots as possible.<br>
    /// 4) it picks a target robot to fire at, which is the nearest robot our robot will be able to
    /// damage (by predicting its future position using linear targeting).<br>
    /// 5) it only moves along the borders of the battle field and constantly changes its direction, so
    /// it is not an easy target, and it will try to get as close to its target robot as possible.<br>
    /// <p>
    /// Lots of improvements can be made to this robot by copying it - making it even stronger. The
    /// intention with of this sample robot is to serve as a more advanced example of how a AdvancedRobot
    /// can be made, and how it can be structured as most sample robots are far simpler.
    /// <p>
    ///
    /// Credits goes to the hard working Robocoders at the RoboWiki. :-)
    /// <p>
    ///
    /// This robot makes use of the Oldest Scanned melee scanner from the RoboWiki:<br>
    /// http://robowiki.net/wiki/Melee_Radar
    /// <p>
    ///
    /// In addition, it makes use of the Exact Non-iterative Solution for Linear Targeting from the
    /// RoboWiki:<br>
    /// http://robowiki.net/wiki/Linear_Targeting
    /// <p> 
    ///
    /// Author: Flemming N. Larsen
    /// <p>
    /// Version: 1.0
    /// <p>
    /// Since 1.9.3.0
    /// </summary>
    public class BorderGuard
        : AdvancedRobot, IBorderSentry
    {
        // Constants
        const double FIREPOWER = 3; // Max. power => violent as this robot can afford it!
        const double HALF_ROBOT_SIZE = 18; // Robot size is 36x36 units, so the half size is 18 units

        // Dictionary containing data for all scanned robots.
        // The key to the dictionary is a robot name and the value is an object containing robot data.
        IDictionary<string, RobotData> enemyDictionary;
        // List containing robot names where the order of names reflects the when these names was accessed the last time
        // within the enemy dictionary. The first in the list is the name of the robot that was accessed the last time,
        // and the last in the list is the oldest one.
        IList<string> accessedEnemyList;

        // Scanning direction, where the radar turns to the right with positive values, and turns
        // to the left with negative values.
        double scanDir = 1;

        // Oldest scanned robot. Can be null.
        RobotData oldestScanned;

        // Target robot for the gun. Can be null meaning that there is currently no target robot.
        RobotData target;

        // Last time when the robot shifted its direction
        long lastDirectionShift;

        // Current direction, where 1 means ahead (forward) and -1 means back
        int direction = 1;

        /// <summary>
        /// Constructs this robot.
        /// </summary>
        public BorderGuard()
        {
            // We initialize a Dictionary containing robot data for each scanned robot, and also a
            // List keeping track of the access order for the enemy dictionary. The last accessed robot
            // entry is listed first in the List.
            // This robot always sweep the radar towards the oldest scanned robot.
            enemyDictionary = new Dictionary<string, RobotData>();
            accessedEnemyList = new List<string>();
        }

        /// <summary>
        /// Main method that is called by the game when the robot engage in the next round of a battle.
        /// </summary>
        public override void Run()
        {
            // Do initialization stuff here before the loop
            Initialize();

            // Loop forever. If the robot does not take action, the game will disable our robot!
            while (true)
            {
                // Handle a single turn...

                // Handle the radar that scans enemy robots
                HandleRadar();
                // Handle the gun by turning it and fire at our target
                HandleGun();
                // Move the robot around on the battlefield
                MoveRobot();

                // Scan for other robots. Note that this method will execute all pending commands for
                // the next turn. Hence, scan() ends the turn for our robot.
                Scan();
            }
        }

        /// <summary>
        /// This method is called by the game when your robot sees another robot, i.e. when the robot's
        /// radar scan "hits" another robot.
        /// </summary>
        /// <param name="scannedRobotEvent">The scanned robot event containing data about the scanned robot.</param>
        public override void OnScannedRobot(ScannedRobotEvent scannedRobotEvent)
        {
            // Check that the scanned robot is not a sentry robot
            if (!scannedRobotEvent.IsSentryRobot)
            {
                // The scanned robot is not a sentry robot...

                // Update the enemy dictionary
                UpdateEnemyDictionary(scannedRobotEvent);

                // Update the scan direction
                UpdateScanDirection(scannedRobotEvent);

                // Update enemy target positions
                UpdateEnemyTargetPositions();
            }
        }

        /// <summary>
        /// This method is called by the game when another robot dies.
        /// </summary> 
        /// <param name="robotDeathEvent">The robot death event containing data about the robot that died.</param>
        public override void OnRobotDeath(RobotDeathEvent robotDeathEvent)
        {
            // Gets the name of the robot that died
            var deadRobotName = robotDeathEvent.Name;

            // Remove the robot data for the robot that died from the enemy dictionary and access list
            enemyDictionary.Remove(deadRobotName);
            accessedEnemyList.Remove(deadRobotName);

            // Remove the data entry for the oldest scanned robot, if we have such an entry
            if (oldestScanned != null && oldestScanned.name.Equals(deadRobotName))
            {
                oldestScanned = null;
            }
            if (target != null && target.name.Equals(deadRobotName))
            {
                target = null;
            }
        }

        /// <summary>
        /// This method is called by the every time the robot is painted.
        /// </summary>
        /// <remarks>
        /// In order to see the painting, make sure to enable the Paint button on the robot console for this robot.
        /// </remarks>
        /// <param name="g">
        /// The graphics context used for painting various shapes like rectangles, circles, lines etc.
        /// on top of the battlefield for debugging graphics.
        /// </param>
        public override void OnPaint(IGraphics g)
        {
            // Prepare colors for painting the scanned coordinate and target coordinate
            var color1 = Color.FromArgb(0x40, 0x00, 0xFF, 0x00); // Green with 25% alpha blending
            var color2 = Color.FromArgb(0x40, 0xFF, 0x00, 0x40); // Yellow with 25% alhpa blending

            // Prepare pen with color 1 and a width of 2 pixels
            var pen = new Pen(color1, 2);

            // Paint a two circles for each robot in the enemy dictionary. One circle where the robot was
            // scanned the last time, and another circle where our robot must point the gun in order to
            // hit it (target coordinate). In addition, a line is drawn between these circles.
            foreach (RobotData robot in enemyDictionary.Values)
            {
                // Paint the two circles and a line
                FillCircle(g, robot.scannedX, robot.scannedY, color1); // scanned coordinate
                FillCircle(g, robot.targetX, robot.targetY, color2); // target coordinate
                g.DrawLine(pen, (float)robot.scannedX, (float)robot.scannedY,
                    (float)robot.targetX, (float)robot.targetY);
            }

            // Paint a two circles for the target robot. One circle where the robot was
            // scanned the last time, and another circle where our robot must point the gun in order to
            // hit it (target coordinate). In addition, a line is drawn between these circles.
            if (target != null)
            {
                // Prepare colors for painting the scanned coordinate and target coordinate
                color1 = Color.FromArgb(0x40, 0xFF, 0x7F, 0x00); // Orange with 25% alpha blending
                color2 = Color.FromArgb(0x80, 0xFF, 0x00, 0x00); // Red with 50% alpha blending

                // Prepare pen with color 1 and a width of 2 pixels
                pen = new Pen(color1, 2);

                // Paint the two circles and a line
                FillCircle(g, target.scannedX, target.scannedY, color1); // scanned coordinate
                FillCircle(g, target.targetX, target.targetY, color2); // target coordinate
                g.DrawLine(pen, (float)target.scannedX, (float)target.scannedY,
                    (float)target.targetX, (float)target.targetY);
            }
        }

        /// <summary>
        /// Initializes this robot before a new round in a battle.
        /// </summary> 
        private void Initialize()
        {
            // Let the robot body, gun, and radar turn independently of each other
            IsAdjustRadarForGunTurn = true;
            IsAdjustGunForRobotTurn = true;

            // Set robot colors
            BodyColor = Color.FromArgb(0x5C, 0x33, 0x17); // Chocolate Brown
            GunColor = Color.FromArgb(0x45, 0x8B, 0x74); // Aqua Marine
            RadarColor = Color.FromArgb(0xD2, 0x69, 0x1E); // Orange Chocolate
            BulletColor = Color.FromArgb(0xFF, 0xD3, 0x9B); // Burly wood
            ScanColor = Color.FromArgb(0xCA, 0xFF, 0x70); // Olive Green
        }

        /// <summary>
        /// This method handles the radar that scans for enemy robots.
        /// </summary> 
        private void HandleRadar()
        {
            // Set the radar to turn infinitely to the right if the scan direction is positive;
            // otherwise the radar is moved to the left, if the scan direction is negative.
            // Notice that onScannedRobot(ScannedRobotEvent) is responsible for determining the scan
            // direction.
            SetTurnRadarRightRadians(scanDir * Double.PositiveInfinity);
        }

        /// <summary>
        /// Method that handles the gun be turning it and fire at a target.
        /// </summary> 
        private void HandleGun()
        {
            // Update our target robot to fire at
            UpdateTarget();
            // Update the gun direction
            UpdateGunDirection();
            // Fires the gun, when it is ready
            FireGunWhenReady();
        }

        /// <summary>
        /// Method that moves our robot around the battlefield.
        /// </summary> 
        private void MoveRobot()
        {
            // The movement strategy is to move as close to our target robot as possible.
            // Our robot should move along the borders all the time, vertically or horizontally.
            // When we get close to our target, or have no where to go, our robot should shift its
            // direction from side to side so it does not stand still at any time.
            // If the robot stands still, it will be an easy target for enemy robots.

            int newDirection = direction;

            // Get closer to our target if we have a target robot
            if (target != null)
            {
                // Calculate the range from the walls/borders, our robot should keep within
                int borderRange = SentryBorderSize - 20;

                // The horizontal and vertical flags are used for determining, if our robot should
                // move horizontal or vertical.
                bool horizontal = false;
                bool vertical = false;

                // Initialize the new heading of the robot to the current heading of the robot
                double newHeading = HeadingRadians;

                // Check if our robot is at the upper or lower border and hence should move horizontally
                if (X < borderRange || Y > BattleFieldHeight - borderRange)
                {
                    horizontal = true;
                }
                // Check if our robot is at the left or right border and hence should move vertically
                if (X < borderRange || X > BattleFieldWidth - borderRange)
                {
                    vertical = true;
                }

                // If we are in one of the corners of the battlefield, we could move both horizontally
                // or vertically.
                // In this situation, we need to choose one of the two directions.
                if (horizontal && vertical)
                {
                    // If the horizontal distance to our target is lesser than the vertical distance,
                    // then we choose to move vertically, and hence we clear the horizontal flag.
                    if (Math.Abs(target.targetX - X) <= Math.Abs(target.targetY - Y))
                    {
                        horizontal = false; // Do not move horizontally => move vertically
                    }
                }
                // Adjust the heading of our robot with 90 degrees, if it must move horizontally.
                // Otherwise the calculated heading is towards moving vertically.
                if (horizontal)
                {
                    newHeading -= Math.PI / 2;
                }
                // Set the robot to turn left the amount of radians we have just calculated
                SetTurnLeftRadians(Utils.NormalRelativeAngle(newHeading));

                // Check if our robot has finished turning, i.e. has less than 1 degrees left to turn
                if (Math.Abs(TurnRemaining) < 1 || Math.Abs(Velocity) < 0.01)
                {
                    // If we should move horizontally, the set the robot to move ahead with the
                    // horizontal distance to the target robot. Otherwise, use the vertical distance.
                    double delta; // delta is the delta distance to move
                    if (horizontal)
                    {
                        delta = target.targetX - X;
                    }
                    else
                    {
                        delta = target.targetY - Y;
                    }
                    SetAhead(delta);

                    // Set the new direction of our robot to 1 (meaning move forward) if the delta
                    // distance is positive; otherwise it is set to -1 (meaning move backward).
                    newDirection = delta > 0 ? 1 : -1;

                    // Check if more than 10 turns have past since we changed the direction the last
                    // time
                    if (Time - lastDirectionShift > 10)
                    {
                        // If so, set the new direction to be the reverse direction if the velocity < 1
                        if (Math.Abs(Velocity) < 1)
                        {
                            newDirection = direction * -1;
                        }
                        // Check if the direction really changed
                        if (newDirection != direction)
                        {
                            // If the new direction != current direction, then set the current direction
                            // to be the new direction and save the current time so we know when we
                            // changed the direction the last time.
                            direction = newDirection;
                            lastDirectionShift = Time;
                        }
                    }
                }
            }
            // Set ahead 100 units forward or backward depending on the direction
            SetAhead(100 * direction);
        }

        /// <summary>
        /// Method the updates the enemy dictionary based on new scan data for a scanned robot.
        /// </summary> 
        /// <param name="scannedRobotEvent">The scanned robot event containing data about the scanned robot.</param>
        private void UpdateEnemyDictionary(ScannedRobotEvent scannedRobotEvent)
        {
            var name = scannedRobotEvent.Name;

            // Check if data entry exists for the scanned robot
            if (enemyDictionary.ContainsKey(name))
            {
                // Data entry exists => Update the current entry with new scanned data
                RobotData scannedRobot = enemyDictionary[name];
                scannedRobot.Update(scannedRobotEvent, this);
            }
            else
            {
                // No data entry exists => Create a new data entry for the scanned robot
                RobotData scannedRobot = new RobotData(scannedRobotEvent, this);
                // Put the new data entry into the enemy dictionary
                enemyDictionary.Add(name, scannedRobot);
            }
            // Change the access order for the robot name, so the name is moved to, or added as the last entry
            if (accessedEnemyList.Contains(name))
            {
                accessedEnemyList.Remove(name);
            }
            accessedEnemyList.Add(name);
        }

        /// <summary>
        /// Method that updates the direction of the radar based on new scan data for a scanned robot.
        /// </summary> 
        /// <param name="scannedRobotEvent">The scanned robot event containing data about the scanned robot.</param>
        private void UpdateScanDirection(ScannedRobotEvent scannedRobotEvent)
        {
            // Gets the name of the scanned robot
            var scannedRobotName = scannedRobotEvent.Name;

            // Change the scanning direction if and only if we have no record for the oldest scanned
            // robot or the scanned robot IS the oldest scanned robot (based on the name) AND the enemy
            // dictionary contains scanned data entries for ALL robots (the size of the enemy dictionary is equal to
            // the number of opponent robots found by calling the getOthers() method).
            if ((oldestScanned == null || scannedRobotName.Equals(oldestScanned.name)) && accessedEnemyList.Count == Others)
            {
                // Get the oldest scanned robot data from our LinkedHashMap, where the first value
                // contains the oldest accessed entry, which is the robot we need to get.
                string oldestScannedName = accessedEnemyList[0];
                RobotData oldestScannedRobot = enemyDictionary[oldestScannedName];

                // Get the recent scanned position (x,y) of the oldest scanned robot
                double x = oldestScannedRobot.scannedX;
                double y = oldestScannedRobot.scannedY;

                // Get the heading of our robot
                double ourHeading = RadarHeadingRadians;

                // Calculate the bearing to the oldest scanned robot.
                // The bearing is the delta angle between the heading of our robot and the other robot,
                // which can be a positive or negative angle.
                double bearing = BearingTo(ourHeading, x, y);

                // Update the scan direction based on the bearing.
                // If the bearing is positive, the radar will be moved to the right.
                // If the bearing is negative, the radar will be moved to the left.
                scanDir = bearing;
            }
        }

        /// <summary>
        /// Updates the target positions for all enemies. The target position is the position our robot
        /// must fire at in order to hit the target robot.
        /// </summary> 
        /// <remarks>
        /// This robot uses Linear Targeting (Exact Non-iterative Solution) as described on the RoboWiki here:
        /// http://robowiki.net/wiki/Linear_Targeting
        /// </remarks> 
        private void UpdateEnemyTargetPositions()
        {
            // Go thru all robots in the enemy dictionary
            foreach (RobotData enemy in enemyDictionary.Values)
            {
                // Variables prefixed with e- refer to enemy and b- refer to bullet
                double bV = Rules.GetBulletSpeed(FIREPOWER);
                double eX = enemy.scannedX;
                double eY = enemy.scannedY;
                double eV = enemy.scannedVelocity;
                double eH = enemy.scannedHeading;

                // These constants make calculating the quadratic coefficients below easier
                double A = (eX - X) / bV;
                double B = (eY - Y) / bV;
                double C = eV / bV * Math.Sin(eH);
                double D = eV / bV * Math.Cos(eH);

                // Quadratic coefficients: a*(1/t)^2 + b*(1/t) + c = 0
                double a = A * A + B * B;
                double b = 2 * (A * C + B * D);
                double c = (C * C + D * D - 1);

                // If the discriminant of the quadratic formula is >= 0, we have a solution meaning that
                // at some time, t, the bullet will hit the enemy robot if we fire at it now.
                double discrim = b * b - 4 * a * c;
                if (discrim >= 0)
                {
                    // Reciprocal of quadratic formula. Calculate the two possible solution for the
                    // time, t
                    double t1 = 2 * a / (-b - Math.Sqrt(discrim));
                    double t2 = 2 * a / (-b + Math.Sqrt(discrim));

                    // Choose the minimum positive time or select the one closest to 0, if the time is
                    // negative
                    double t = Math.Min(t1, t2) >= 0 ? Math.Min(t1, t2) : Math.Max(t1, t2);

                    // Calculate the target position (x,y) for the enemy. That is the point that our gun
                    // should point at in order to hit the enemy at the time, t.
                    double targetX = eX + eV * t * Math.Sin(eH);
                    double targetY = eY + eV * t * Math.Cos(eH);

                    // Assume enemy stops at walls. Hence, we limit that target position at the walls.
                    double minX = HALF_ROBOT_SIZE;
                    double minY = HALF_ROBOT_SIZE;
                    double maxX = BattleFieldWidth - HALF_ROBOT_SIZE;
                    double maxY = BattleFieldHeight - HALF_ROBOT_SIZE;

                    enemy.targetX = Limit(targetX, minX, maxX);
                    enemy.targetY = Limit(targetY, minY, maxY);
                }
            }
        }

        /// <summary>
        /// Updates which enemy robot from the enemy dictionary that should be our current target.
        /// </summary> 
        private void UpdateTarget()
        {
            // Set target to null, meaning that we have no target robot yet
            target = null;

            // Create a list over possible target robots that is a copy of robot data from the enemy dictionary
            List<RobotData> targets = new List<RobotData>(enemyDictionary.Values);

            // Run thru all the possible target robots and remove those that are outside the attack
            // range for this border sentry robot as our robot cannot do harm to robots outside its
            // range.
            for (int i = targets.Count - 1; i >= 0; i--) // avoids error "Collection was modified"
            {
                RobotData robot = targets[i];
                if (IsOutsideAttackRange(robot.targetX, robot.targetY))
                {
                    targets.Remove(robot);
                }
            }

            // Set the target robot to be the one among all possible target robots that is closest to
            // our robot.
            double minDist = Double.PositiveInfinity;
            foreach (RobotData robot in targets)
            {
                double dist = DistanceTo(robot.targetX, robot.targetY);
                if (dist < minDist)
                {
                    minDist = dist;
                    target = robot;
                }
            }

            // If we still haven't got a target robot, then take the first one from our list of target
            // robots if the list is not empty.
            if (target == null && targets.Count > 0)
            {
                target = targets[0];
            }
        }

        /// <summary>
        /// Method that updates the gun direction to point at the current target.
        /// </summary> 
        private void UpdateGunDirection()
        {
            // Only update the gun direction, if we have a current target
            if (target != null)
            {
                // Calculate the bearing between the gun and the target, which can be positive or
                // negative
                double targetBearing = BearingTo(GunHeadingRadians, target.targetX, target.targetY);
                // Set the gun to turn right the amount of radians defined by the bearing to the target
                SetTurnGunRightRadians(targetBearing); // positive => turn right, negative => turn left
            }
        }

        /// <summary>
        /// Method that fires a bullet when the gun is ready to fire.
        /// </summary> 
        private void FireGunWhenReady()
        {
            // We only fire the fun, when we have a target robot
            if (target != null)
            {
                // Only fire when the angle of the gun is pointing at our (virtual) target robot

                // Calculate the distance between between our robot and the target robot
                double dist = DistanceTo(target.targetX, target.targetY);
                // Angle that "covers" the the target robot from its center to its edge
                double angle = Math.Atan(HALF_ROBOT_SIZE / dist);

                // Check if the remaining angle (turn) to move the gun is less than our calculated cover
                // angle
                if (Math.Abs(GunTurnRemaining) < angle)
                {
                    // If so, our gun should be pointing at our target so we can hit it => fire!!
                    SetFire(FIREPOWER);
                }
            }
        }

        /// <summary>
        /// Method that checks if a coordinate (x,y) is outside the Border Sentry's attack range.
        /// </summary> 
        /// <param name="x">X coordinate.</param>
        /// <param name="y">Y coordinate.</param>
        /// <return>true if the coordinate is outside the attack range; false otherwise.</return>
        private bool IsOutsideAttackRange(double x, double y)
        {
            double minBorderX = SentryBorderSize;
            double minBorderY = SentryBorderSize;
            double maxBorderX = BattleFieldWidth - SentryBorderSize;
            double maxBorderY = BattleFieldHeight - SentryBorderSize;

            return (x > minBorderX) && (y > minBorderY) && (x < maxBorderX) && (y < maxBorderY);
        }

        /// <summary>
        /// Method that returns a value that is guaranteed to be within a value range defined by a
        /// minimum and maximum value based on an input value.<br>
        /// If the input value is lesser than the minimum value, the returned value will be set to the
        /// minimum value.<br>
        /// If the input value is greater than the maximum value, the returned value will be set to the
        /// maximum value.<br>
        /// Otherwise the returned value will be equal to the input value.
        /// </summary> 
        /// <param name="value">Input value to limit.</param>
        /// <param name="min">Allowed minimum value.</param>
        /// <param name="max">Allowed maximum value.</param>
        /// <return>
        /// The limited input value that is guaranteed to be within the specified minimum and maximum range.
        /// </return>
        private double Limit(double value, double min, double max)
        {
            return Math.Min(max, Math.Max(min, value));
        }

        /// <summary>
        /// Methods that returns the distance to a coordinate (x,y) from our robot.
        /// </summary> 
        /// <param name="x">X coordinate.</param>
        /// <param name="y">Y coordinate.</param>
        /// <return>
        /// The distance to the coordinate (x,y).
        /// </return>
        private double DistanceTo(double x, double y)
        {
            double dx = X - x;
            double dy = Y - y;
            return Math.Sqrt(dx * dx + dy * dy);
        }

        /// <summary>
        /// Method that returns the angle to a coordinate (x,y) from our robot.
        /// </summary> 
        /// <param name="x">X coordinate.</param>
        /// <param name="y">Y coordinate.</param>
        /// <return>
        /// The angle to the coordinate (x,y).
        /// </return>
        private double AngleTo(double x, double y)
        {
            return Math.Atan2(x - X, y - Y);
        }

        /// <summary>
        /// Method that returns the bearing to a coordinate (x,y) from the position and heading of our
        /// robot. The bearing is the delta angle between the heading of our robot and the angle of the
        /// specified coordinate.
        /// </summary> 
        /// <param name="x">X coordinate.</param>
        /// <param name="y">Y coordinate.</param>
        /// <return>
        /// The angle to the coordinate (x,y).
        /// </return>
        private double BearingTo(double heading, double x, double y)
        {
            return Utils.NormalRelativeAngle(AngleTo(x, y) - heading);
        }

        /// <summary>
        /// Method that paints a filled circle at the specified coordinate (x,y) and given color. The
        /// circle will have a radius of 20 pixels (meaning that the diameter will be 40 pixels).
        /// </summary> 
        /// <param name="gfx">X coordinate.</param>
        /// <param name="x">X coordinate for the center of the circle.</param>
        /// <param name="y">Y coordinate for the center of the circle.</param>
        /// <param name="color">Color of the filled circle.</param>
        private void FillCircle(IGraphics gfx, double x, double y, Color color)
        {
            // Prepare brush
            Brush brush = new SolidBrush(color);
            // Paint a filled circle (oval) that has a radius of 20 pixels with a center at the input
            // coordinates.
            gfx.FillEllipse(brush, (float)x - 20, (float)y - 20, 40f, 40f);
        }

        /// <summary>
        /// This class is used for storing data about a robot that has been scanned.<br>
        /// The data is mainly a snapshot of specific scanned data like the scanned position (x,y),
        /// velocity and heading, put also the calculated predicted target position of the robot when our
        /// robot needs to fire at the scanned robot.<br>
        /// Note that this class calculates the position (x,y) of the scanned robot as our robot moves,
        /// and hence data like the angle and distance to the scanned robot will change over time. by
        /// using the position, it is easy to calculate a new angle and distance to the robot.
        /// </summary> 
        internal class RobotData
        {
            internal string name; // name of the scanned robot
            internal double scannedX; // x coordinate of the scanned robot based on the last update
            internal double scannedY; // y coordinate of the scanned robot based on the last update
            internal double scannedVelocity; // velocity of the scanned robot from the last update
            internal double scannedHeading; // heading of the scanned robot from the last update
            internal double targetX; // predicated x coordinate to aim our gun at, when firing at the robot
            internal double targetY; // predicated y coordinate to aim our gun at, when firing at the robot

            /// <summary>
            /// Creates a new robot data entry based on new scan data for a scanned robot.
            /// </summary> 
            /// <param name="scannedRobotEvent">
            /// A ScannedRobotEvent event containing data about a scanned robot.
            /// </param>
            /// <param name="robot">Robot that creates this data entry.</param>
            public RobotData(ScannedRobotEvent scannedRobotEvent, AdvancedRobot robot)
            {
                // Store the name of the scanned robot
                name = scannedRobotEvent.Name;
                // Updates all scanned facts like position, velocity, and heading
                Update(scannedRobotEvent, robot);
                // Initialize the coordinates (x,y) to fire at to the updated scanned position
                targetX = scannedX;
                targetY = scannedY;
            }

            /// <summary>
            /// Updates the scanned data based on new scan data for a scanned robot.
            /// </summary> 
            /// <param name="scannedRobotEvent">
            /// A ScannedRobotEvent event containing data about a scanned robot.
            /// </param>
            /// <param name="robot">Robot that calls this method.</param>
            public void Update(ScannedRobotEvent scannedRobotEvent, AdvancedRobot robot)
            {
                // Get the position of the scanned robot based on the ScannedRobotEvent
                PointF pos = GetPosition(scannedRobotEvent, robot);
                // Store the scanned position (x,y)
                scannedX = pos.X;
                scannedY = pos.Y;
                // Store the scanned velocity and heading
                scannedVelocity = scannedRobotEvent.Velocity;
                scannedHeading = scannedRobotEvent.HeadingRadians;
            }

            /// <summary>
            /// Returns the position of the scanned robot based on new scan data for a scanned robot.
            /// </summary> 
            /// <param name="scannedRobotEvent">
            /// A ScannedRobotEvent event containing data about a scanned robot.
            /// </param>
            /// <param name="robot">Robot that calls this method.</param>
            /// <return>The position (x,y) of the scanned robot.</return>
            private PointF GetPosition(ScannedRobotEvent scannedRobotEvent, AdvancedRobot robot)
            {
                // Gets the distance to the scanned robot
                double distance = scannedRobotEvent.Distance;
                // Calculate the angle to the scanned robot (our robot heading + bearing to scanned robot)
                double angle = robot.HeadingRadians + scannedRobotEvent.BearingRadians;

                // Calculate the coordinates (x,y) of the scanned robot
                double x = robot.X + Math.Sin(angle) * distance;
                double y = robot.Y + Math.Cos(angle) * distance;

                // Return the position as a point (x,y)
                return new PointF((float)x, (float)y);
            }
        }
    }
}
