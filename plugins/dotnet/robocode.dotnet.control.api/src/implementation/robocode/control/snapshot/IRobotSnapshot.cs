﻿#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /// <summary>
    /// Interface of a robot snapshot at a specific time in a battle.
    /// </summary>
    public interface IRobotSnapshot
    {
        /// <summary>
        /// Contains the name of the robot.
        /// </summary>
        /// <value>
        /// The name of the robot.
        /// </value>
        string Name { get; }

        /// <summary>
        /// Contains the short name of the robot.
        /// </summary>
        /// <value>
        /// The short name of the robot.
        /// </value>
        string ShortName { get; }

        /// <summary>
        /// Contains the very short name of the robot.
        /// </summary>
        /// <value>
        /// The very short name of the robot.
        /// </value>
        string VeryShortName { get; }

        /// <summary>
        /// Contains the name of the team, which can be the name of a robot if the contestant is not a team, but a robot.
        /// </summary>
        /// <value>
        /// The name of the team.
        /// </value>
        string TeamName { get; }

        /// <summary>
        /// Contains the contestant index of the robot, which is constant during a battle.
        /// </summary>
        /// <value>
        /// The contestant index of the robot.
        /// </value>
        int ContestantIndex { get; }

        /// <summary>
        /// Contains the robot state.
        /// </summary>
        /// <value>
        /// The robot state.
        /// </value>
        RobotState State { get; }

        /// <summary>
        /// Contains the energy level of the robot.
        /// </summary>
        /// <value>
        /// The energy level of the robot.
        /// </value>
        double Energy { get; }

        /// <summary>
        /// Contains the velocity of the robot.
        /// </summary>
        /// <value>
        /// The velocity of the robot.
        /// </value>
        double Velocity { get; }

        /// <summary>
        /// Contains the body heading of the robot in radians.
        /// </summary>
        /// <value>
        /// The body heading of the robot in radians.
        /// </value>
        double BodyHeading { get; }

        /// <summary>
        /// Contains the gun heading of the robot in radians.
        /// </summary>
        /// <value>
        /// The gun heading of the robot in radians.
        /// </value>
        double GunHeading { get; }

        /// <summary>
        /// Contains the radar heading of the robot in radians.
        /// </summary>
        /// <value>
        /// The radar heading of the robot in radians.
        /// </value>
        double RadarHeading { get; }

        /// <summary>
        /// Contains the gun heat of the robot.
        /// </summary>
        /// <value>
        /// The gun heat of the robot.
        /// </value>
        double GunHeat { get; }

        /// <summary>
        /// Contains the X position of the robot.
        /// </summary>
        /// <value>
        /// The X position of the robot.
        /// </value>
        double X { get; }

        /// <summary>
        /// Contains the Y position of the robot.
        /// </summary>
        /// <value>
        /// The Y position of the robot.
        /// </value>
        double Y { get; }

        /// <summary>
        /// Contains the color of the body.
        /// </summary>
        /// <value>
        /// An ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
        /// </value>
        int BodyColor { get; }

        /// <summary>
        /// Contains the color of the gun.
        /// </summary>
        /// <value>
        /// An ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
        /// </value>
        int GunColor { get; }

        /// <summary>
        /// Contains the color of the radar.
        /// </summary>
        /// <value>
        /// An ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
        /// </value>
        int RadarColor { get; }

        /// <summary>
        /// Contains the color of the scan arc.
        /// </summary>
        /// <value>
        /// An ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
        /// </value>
        int ScanColor { get; }

        /// <summary>
        /// Flag specifying if this robot is an <see cref="Robocode.IDroid"/>.
        /// </summary>
        /// <value>
        /// <em>true</em> if this robot is an <see cref="Robocode.IDroid"/>; <em>false</em> otherwise.
        /// </value>
        bool IsDroid { get; }

        /// <summary>
        /// Flag specifying if this robot is an <see cref="Robocode.RobotInterfaces.IPaintRobot"/>.
        /// </summary>
        /// <value>
        /// <em>true</em> if this robot is an <see cref="Robocode.RobotInterfaces.IPaintRobot"/>; <em>false</em> otherwise.
        /// </value>
        bool IsPaintRobot { get; }

        /// <summary>
        /// Flag specifying if painting is enabled for this robot.
        /// </summary>
        /// <value>
        /// <em>true</em> if painting is enabled for this robot; <em>false</em> otherwise.
        /// </value>
        bool IsPaintEnabled { get; }

        /// <summary>
        /// Flag specifying if RobocodeSG painting (the point (0,0) is in the upper left corner) is enabled for this robot.
        /// </summary>
        /// <value>
        /// <em>true</em> if RobocodeSG painting is enabled for this robot; <em>false</em> otherwise.
        /// </value>
        bool IsSGPaintEnabled { get; }

        /// <summary>
        /// Contains a snapshot of debug properties.
        /// </summary>
        /// <value>
        /// A snapshot of debug properties.
        /// </value>
        IDebugProperty[] DebugProperties { get; }

        /// <summary>
        /// Contains a snapshot of the output print stream for this robot.
        /// </summary>
        /// <value>
        /// A string containing the snapshot of the output print stream.
        /// </value>
        string OutputStreamSnapshot { get; }

        /// <summary>
        /// Contains a snapshot of the current score for this robot.
        /// </summary>
        /// <value>
        /// A snapshot of the current score for this robot.
        /// </value>
        IScoreSnapshot ScoreSnapshot { get; }
    }
}
