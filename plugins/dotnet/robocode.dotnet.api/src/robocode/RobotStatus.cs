/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using net.sf.robocode.nio;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using Robocode.Util;

namespace Robocode
{
    /// <summary>
    /// Contains the status of a robot for a specific time/turn returned by
    /// <see cref="StatusEvent.Status"/>.
    /// </summary>
    [Serializable]
    public sealed class RobotStatus
    {
        private readonly double energy;
        private readonly double x;
        private readonly double y;
        private readonly double bodyHeading;
        private readonly double gunHeading;
        private readonly double radarHeading;
        private readonly double velocity;
        private readonly double bodyTurnRemaining;
        private readonly double radarTurnRemaining;
        private readonly double gunTurnRemaining;
        private readonly double distanceRemaining;
        private readonly double gunHeat;
        private readonly int others;
        private readonly int numSentries;
        private readonly int roundNum;
        private readonly int numRounds;
        private readonly long time;

        /// <summary>
        /// Returns the robot's current energy.
        /// </summary>
        public double Energy
        {
            get { return energy; }
        }

        /// <summary>
        /// Returns the X position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        /// </summary>
        /// <seealso cref="Y"/>
        public double X
        {
            get { return x; }
        }

        /// <summary>
        /// Returns the Y position of the robot. (0,0) is at the bottom left of the
        /// battlefield.
        /// <seealso cref="X"/>
        /// </summary>
        public double Y
        {
            get { return y; }
        }

        /// <summary>
        /// Returns the direction that the robot's body is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 2 means West.
        /// </summary>
        public double HeadingRadians
        {
            get { return bodyHeading; }
        }

        /// <summary>
        /// Returns the direction that the robot's body is facing, in degrees.
        /// The value returned will be between 0 and 360 (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// 90 means East, 180 means South, and 270 means West.
        /// </summary>
        public double Heading
        {
            get { return Utils.ToDegrees(bodyHeading); }
        }

        /// <summary>
        /// Returns the direction that the robot's gun is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 2 means West.
        /// </summary>
        public double GunHeadingRadians
        {
            get { return gunHeading; }
        }

        /// <summary>
        /// Returns the direction that the robot's gun is facing, in degrees.
        /// The value returned will be between 0 and 360 (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// 90 means East, 180 means South, and 270 means West.
        /// </summary>
        public double GunHeading
        {
            get { return Utils.ToDegrees(gunHeading); }
        }

        /// <summary>
        /// Returns the direction that the robot's radar is facing, in radians.
        /// The value returned will be between 0 and 2 * PI (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// PI / 2 means East, PI means South, and 3 * PI / 2 means West.
        /// </summary>
        public double RadarHeadingRadians
        {
            get { return radarHeading; }
        }

        /// <summary>
        /// Returns the direction that the robot's radar is facing, in degrees.
        /// The value returned will be between 0 and 360 (is excluded).
        /// <p/>
        /// Note that the heading in Robocode is like a compass, where 0 means North,
        /// 90 means East, 180 means South, and 270 means West.
        /// </summary>
        public double RadarHeading
        {
            get { return Utils.ToDegrees(radarHeading); }
        }

        /// <summary>
        /// Returns the velocity of the robot measured in pixels/turn.
        /// <p/>
        /// The maximum velocity of a robot is defined by <see cref="Rules.MAX_VELOCITY"/>
        /// (8 pixels / turn).
        /// <seealso cref="Rules.MAX_VELOCITY"/>
        /// </summary>
        public double Velocity
        {
            get { return velocity; }
        }

        /// <summary>
        /// Returns the angle remaining in the robots's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values.
        /// Positive values means that the robot is currently turning to the right.
        /// Negative values means that the robot is currently turning to the left.
        /// </summary>
        public double TurnRemainingRadians
        {
            get { return bodyTurnRemaining; }
        }

        /// <summary>
        /// Returns the angle remaining in the robots's turn, in degrees.
        /// <p/>
        /// This call returns both positive and negative values.
        /// Positive values means that the robot is currently turning to the right.
        /// Negative values means that the robot is currently turning to the left.
        /// </summary>
        public double TurnRemaining
        {
            get { return Utils.ToDegrees(bodyTurnRemaining); }
        }

        /// <summary>
        /// Returns the angle remaining in the radar's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values.
        /// Positive values means that the radar is currently turning to the right.
        /// Negative values means that the radar is currently turning to the left.
        /// </summary>
        public double RadarTurnRemainingRadians
        {
            get { return radarTurnRemaining; }
        }

        /// <summary>
        /// Returns the angle remaining in the radar's turn, in degrees.
        /// <p/>
        /// This call returns both positive and negative values.
        /// Positive values means that the radar is currently turning to the right.
        /// Negative values means that the radar is currently turning to the left.
        /// </summary>
        public double RadarTurnRemaining
        {
            get { return Utils.ToDegrees(radarTurnRemaining); }
        }

        /// <summary>
        /// Returns the angle remaining in the gun's turn, in radians.
        /// <p/>
        /// This call returns both positive and negative values.
        /// Positive values means that the gun is currently turning to the right.
        /// Negative values means that the gun is currently turning to the left.
        /// </summary>
        public double GunTurnRemainingRadians
        {
            get { return gunTurnRemaining; }
        }

        /// <summary>
        /// Returns the angle remaining in the gun's turn, in degrees.
        /// <p/>
        /// This call returns both positive and negative values.
        /// Positive values means that the gun is currently turning to the right.
        /// Negative values means that the gun is currently turning to the left.
        /// </summary>
        public double GunTurnRemaining
        {
            get { return Utils.ToDegrees(gunTurnRemaining); }
        }

        /// <summary>
        /// Returns the distance remaining in the robot's current move measured in
        /// pixels.
        /// <p/>
        /// This call returns both positive and negative values.
        /// Positive values means that the robot is currently moving forwards.
        /// Negative values means that the robot is currently moving backwards.
        /// </summary>
        public double DistanceRemaining
        {
            get { return distanceRemaining; }
        }

        /// <summary>
        /// Returns the current heat of the gun. The gun cannot Fire unless this is
        /// 0. (Calls to Fire will succeed, but will not actually Fire unless
        /// GetGunHeat() == 0).
        /// <p/>
        /// The amount of gun heat generated when the gun is fired is
        /// 1 + (firePower / 5). Each turn the gun heat drops by the amount returned
        /// by <see cref="Robot.GunCoolingRate"/>, which is a battle setup.
        /// <p/>
        /// Note that all guns are "hot" at the start of each round, where the gun
        /// heat is 3.
        /// </summary>
        /// <seealso cref="Robot.GunCoolingRate"/>
        /// <seealso cref="Robot.Fire(double)"/>
        /// <seealso cref="Robot.FireBullet(double)"/>
        public double GunHeat
        {
            get { return gunHeat; }
        }

        /// <summary>
        /// Returns how many opponents that are left in the current round.
        /// </summary>
        public int Others
        {
            get { return others; }
        }

        /// <summary>
        /// Returns how many sentry robots that are left in the current round.
        /// </summary>
        public int NumSentries
        {
            get { return numSentries; }
        }

        /// <summary>
        /// Returns the number of rounds in the current battle.
        /// </summary>
        /// <seealso cref="RoundNum"/>
        public int NumRounds
        {
            get { return numRounds; }
        }

        /// <summary>
        /// Returns the current round number (0 to <see cref="NumRounds"/> - 1) of
        /// the battle.
        /// </summary>
        /// <seealso cref="NumRounds"/>
        public int RoundNum
        {
            get { return roundNum; }
        }

        /// <summary>
        /// Returns the game time of the round, where the time is equal to the current turn in the round.
        /// </summary>
        public long Time
        {
            get { return time; }
        }

        private RobotStatus(double energy, double x, double y, double bodyHeading, double gunHeading,
                            double radarHeading,
                            double velocity, double bodyTurnRemaining, double radarTurnRemaining,
                            double gunTurnRemaining,
                            double distanceRemaining, double gunHeat,
                            int others, int numSentries,
                            int roundNum, int numRounds, long time)
        {
            this.energy = energy;
            this.x = x;
            this.y = y;
            this.bodyHeading = bodyHeading;
            this.gunHeading = gunHeading;
            this.radarHeading = radarHeading;
            this.bodyTurnRemaining = bodyTurnRemaining;
            this.velocity = velocity;
            this.radarTurnRemaining = radarTurnRemaining;
            this.gunTurnRemaining = gunTurnRemaining;
            this.distanceRemaining = distanceRemaining;
            this.gunHeat = gunHeat;
            this.others = others;
            this.numSentries = numSentries;
            this.roundNum = roundNum;
            this.numRounds = numRounds;
            this.time = time;
        }

        private static ISerializableHelperN createHiddenSerializer()
        {
            return new SerializableHelper();
        }

        private class SerializableHelper : ISerializableHelperN, IHiddenStatusHelper
        {
            public int sizeOf(RbSerializerN serializer, object objec)
            {
                return RbSerializerN.SIZEOF_TYPEINFO + 12*RbSerializerN.SIZEOF_DOUBLE + 4*RbSerializerN.SIZEOF_INT
                       + RbSerializerN.SIZEOF_LONG;
            }

            public void serialize(RbSerializerN serializer, ByteBuffer buffer, object objec)
            {
                var obj = (RobotStatus) objec;

                serializer.serialize(buffer, obj.energy);
                serializer.serialize(buffer, obj.x);
                serializer.serialize(buffer, obj.y);
                serializer.serialize(buffer, obj.bodyHeading);
                serializer.serialize(buffer, obj.gunHeading);
                serializer.serialize(buffer, obj.radarHeading);
                serializer.serialize(buffer, obj.velocity);
                serializer.serialize(buffer, obj.bodyTurnRemaining);
                serializer.serialize(buffer, obj.radarTurnRemaining);
                serializer.serialize(buffer, obj.gunTurnRemaining);
                serializer.serialize(buffer, obj.distanceRemaining);
                serializer.serialize(buffer, obj.gunHeat);
                serializer.serialize(buffer, obj.others);
                serializer.serialize(buffer, obj.numSentries);
                serializer.serialize(buffer, obj.roundNum);
                serializer.serialize(buffer, obj.numRounds);
                serializer.serialize(buffer, obj.time);
            }

            public object deserialize(RbSerializerN serializer, ByteBuffer buffer)
            {
                double energy = buffer.getDouble();
                double x = buffer.getDouble();
                double y = buffer.getDouble();
                double bodyHeading = buffer.getDouble();
                double gunHeading = buffer.getDouble();
                double radarHeading = buffer.getDouble();
                double velocity = buffer.getDouble();
                double bodyTurnRemaining = buffer.getDouble();
                double radarTurnRemaining = buffer.getDouble();
                double gunTurnRemaining = buffer.getDouble();
                double distanceRemaining = buffer.getDouble();
                double gunHeat = buffer.getDouble();
                int others = buffer.getInt();
                int numSentries = buffer.getInt();
                int roundNum = buffer.getInt();
                int numRounds = buffer.getInt();
                long time = buffer.getLong();

                return new RobotStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity, bodyTurnRemaining,
                                       radarTurnRemaining, gunTurnRemaining, distanceRemaining, gunHeat, others, numSentries,
                                       roundNum, numRounds, time);
            }

            public RobotStatus createStatus(double energy, double x, double y, double bodyHeading, double gunHeading,
                                            double radarHeading, double velocity, double bodyTurnRemaining,
                                            double radarTurnRemaining, double gunTurnRemaining, double distanceRemaining,
                                            double gunHeat, int others, int numSentries, int roundNum, int numRounds, long time)
            {
                return new RobotStatus(energy, x, y, bodyHeading, gunHeading, radarHeading, velocity, bodyTurnRemaining,
                                       radarTurnRemaining, gunTurnRemaining, distanceRemaining, gunHeat, others, numSentries,
                                       roundNum, numRounds, time);
            }
        }
    }
}
//doc