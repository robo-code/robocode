﻿using System;
using net.sf.robocode.host;
using net.sf.robocode.peer;
using net.sf.robocode.repository;
using robocode;
using robocode.robotinterfaces.peer;
using robocode.util;

namespace net.sf.robocode.dotnet.host.proxies
{
    internal class JuniorRobotProxy : BasicRobotProxy, IJuniorRobotPeer
    {
        public JuniorRobotProxy(IRobotRepositoryItem specification, IHostManager hostManager, IRobotPeer peer,
                                RobotStatics statics)
            : base(specification, hostManager, peer, statics)
        {
        }

        public void turnAndMove(double distance, double radians)
        {
            if (distance == 0)
            {
                turnBody(radians);
                return;
            }

            // Save current max. velocity and max. turn rate so they can be restored
            double savedMaxVelocity = commands.getMaxVelocity();
            double savedMaxTurnRate = commands.getMaxTurnRate();

            double absDegrees = Math.Abs(Utils.toDegrees(radians));
            double absDistance = Math.Abs(distance);

            // -- Calculate max. velocity for moving perfect in a circle --

            // maxTurnRate = 10 * 0.75 * velocity  (Robocode rule), and
            // maxTurnRate = velocity * degrees / distance  (curve turn rate)
            //
            // Hence, max. velocity = 10 / (degrees / distance + 0.75)

            double maxVelocity = Math.Min(Rules.MAX_VELOCITY, 10/(absDegrees/absDistance + 0.75));

            // -- Calculate number of turns for acceleration + deceleration --

            double accDist = 0; // accumulated distance during acceleration
            double decDist = 0; // accumulated distance during deceleration

            int turns = 0; // number of turns to it will take to move the distance

            // Calculate the amount of turn it will take to accelerate + decelerate
            // up to the max. velocity, but Stop if the distance for used for
            // acceleration + deceleration gets bigger than the total distance to move
            for (int t = 1; t < maxVelocity; t++)
            {
                // Add the current velocity to the acceleration distance
                accDist += t;

                // Every 2nd time we add the deceleration distance needed to
                // get to a velocity of 0
                if (t > 2 && (t%2) > 0)
                {
                    decDist += t - 2;
                }

                // Stop if the acceleration + deceleration > total distance to move
                if ((accDist + decDist) >= absDistance)
                {
                    break;
                }

                // Increment turn for acceleration
                turns++;

                // Every 2nd time we increment time for deceleration
                if (t > 2 && (t%2) > 0)
                {
                    turns++;
                }
            }

            // Add number of turns for the remaining distance at max velocity
            if ((accDist + decDist) < absDistance)
            {
                turns += (int) ((absDistance - accDist - decDist)/maxVelocity + 1);
            }

            // -- Move and turn in a curve --

            // Set the calculated max. velocity
            commands.setMaxVelocity(maxVelocity);

            // Set the robot to move the specified distance
            setMoveImpl(distance);
            // Set the robot to turn its body to the specified amount of radians
            setTurnBodyImpl(radians);

            // Loop thru the number of turns it will take to move the distance and adjust
            // the max. turn rate so it fit the current velocity of the robot
            for (int t = turns; t >= 0; t--)
            {
                commands.setMaxTurnRate(getVelocity()*radians/absDistance);
                execute(); // Perform next turn
            }

            // Restore the saved max. velocity and max. turn rate
            commands.setMaxVelocity(savedMaxVelocity);
            commands.setMaxTurnRate(savedMaxTurnRate);
        }
    }
}
