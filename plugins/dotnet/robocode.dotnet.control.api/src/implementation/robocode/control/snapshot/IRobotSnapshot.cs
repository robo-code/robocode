using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /**
     * Interface of a robot snapshot at a specific time instant in a battle.
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public interface IRobotSnapshot
    {
        /**
         * Returns the name of the robot.
         *
         * @return the name of the robot.
         */
        string Name { get; }

        /**
         * Returns the short name of the robot.
         *
         * @return the short name of the robot.
         */
        string ShortName { get; }

        /**
         * Returns the very short name of the robot.
         *
         * @return the very short name of the robot.
         */
        string VeryShortName { get; }

        /**
         * Returns the name of the team, which can be the name of a robot if the contestant is not a team, but a robot.  
         *
         * @return the name of the team.
         */
        string TeamName { get; }

        /**
         * Returns the contestant index, which will not be changed during a battle.
         *
         * @return the contestant index.
         */
        int ContestantIndex { get; }

        /**
         * Returns the robot state.
         *
         * @return the robot state.
         */
        RobotState State { get; }

        /**
         * Returns the energy level of the robot.
         *
         * @return the energy level of the robot.
         */
        double Energy { get; }

        /**
         * Returns the velocity of the robot.
         *
         * @return the velocity of the robot.
         */
        double Velocity { get; }

        /**
         * Returns the body heading in radians.
         *
         * @return the body heading in radians.
         */
        double BodyHeading { get; }

        /**
         * Returns the gun heading in radians.
         *
         * @return the gun heading in radians.
         */
        double GunHeading { get; }

        /**
         * Returns the radar heading in radians.
         *
         * @return the radar heading in radians.
         */
        double RadarHeading { get; }

        /**
         * Returns the gun heat of the robot.
         *
         * @return the gun heat of the robot.
         */
        double GunHeat { get; }

        /**
         * Returns the X position of the robot.
         *
         * @return the X position of the robot.
         */
        double X { get; }

        /**
         * Returns the Y position of the robot.
         *
         * @return the Y position of the robot.
         */
        double Y { get; }

        /**
         * Returns the color of the body.
         *
         * @return a ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
         * 
         * @see java.awt.Color#getRGB()
         */
        int BodyColor { get; }

        /**
         * Returns the color of the gun.
         *
         * @return a ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
         * 
         * @see java.awt.Color#getRGB()
         */
        int GunColor { get; }

        /**
         * Returns the color of the radar.
         *
         * @return a ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
         * 
         * @see java.awt.Color#getRGB()
         */
        int RadarColor { get; }

        /**
         * Returns the color of the scan arc.
         *
         * @return a ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
         * 
         * @see java.awt.Color#getRGB()
         */
        int ScanColor { get; }

        /**
         * Checks if this robot is a {@link robocode.Droid Droid}.
         *
         * @return {@code true} if this robot is a Droid; {@code false} otherwise.
         */
//        bool IsDroid { get; }

        /**
         * Checks if this robot is a {@link robocode.robotinterfaces.IPaintRobot IPaintRobot} or is invoking getGraphics()
         *
         * @return {@code true} if this robot is a painting; {@code false} otherwise.
         */
//        bool IsPaintRobot { get; }

        /**
         * Checks if painting is enabled for this robot.
         *
         * @return {@code true} if painting is enabled for this robot; {@code false} otherwise.
         */
//        bool IsPaintEnabled { get; }

        /**
         * Checks if RobocodeSG painting is enabled for this robot.
         *
         * @return {@code true} if RobocodeSG painting is enabled for this robot; {@code false} otherwise.
         */
//        bool IsSGPaintEnabled { get; }

        /**
         * Returns snapshot of debug properties.
         * 
         * @return snapshot of debug properties.
         */
        IDebugProperty[] DebugProperties { get; }

        /**
         * Returns snapshot of the output print stream for this robot.
         *
         * @return a string containing the snapshot of the output print stream.
         */
        string OutputStreamSnapshot { get; }

        /**
         * Returns snapshot of the current score for this robot.
         *
         * @return snapshot of the current score for this robot.
         */
        IScoreSnapshot ScoreSnapshot { get; }
    }
}
