/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Updated Javadocs
 *******************************************************************************/
using System;
using net.sf.robocode.nio;
using net.sf.robocode.security;
using net.sf.robocode.serialization;
using robocode.util;

namespace robocode
{
    /// <summary>
    /// Represents a bullet. This is returned from {@link Robot#fireBullet(double)}
    /// and {@link AdvancedRobot#setFireBullet(double)}, and all the bullet-related
    /// events.
    ///
    /// @author Mathew A. Nelson (original)
    /// @see Robot#fireBullet(double)
    /// @see AdvancedRobot#setFireBullet(double)
    /// @see BulletHitEvent
    /// @see BulletMissedEvent
    /// @see BulletHitBulletEvent
    /// </summary>
    [Serializable]
    public class Bullet
    {
        private readonly double headingRadians;
        private double x;
        private double y;
        private readonly double power;
        private readonly string ownerName;
        private string victimName;
        private bool _isActive;
        private readonly int bulletId;

        /// <summary>
        /// Called by the game to create a new {@code Bullet} object
        ///
        /// @param heading   the heading of the bullet, in radians.
        /// @param x		 the starting X position of the bullet.
        /// @param y		 the starting Y position of the bullet.
        /// @param power	 the power of the bullet.
        /// @param ownerName the name of the owner robot that owns the bullet.
        /// @param victimName the name of the robot hit by the bullet.
        /// @param isActive {@code true} if the bullet still moves; {@code false} otherwise.
        /// @param bulletId unique id of bullet for owner robot.
        /// </summary>
        public Bullet(double heading, double x, double y, double power, string ownerName, string victimName,
                      bool isActive, int bulletId)
        {
            headingRadians = heading;
            this.bulletId = bulletId;
            this.x = x;
            this.y = y;
            this.power = power;
            this.ownerName = ownerName;
            this.victimName = victimName;
            _isActive = isActive;
        }

        /// <summary>
        /// Returns the direction the bullet is/was heading, in degrees
        /// (0 <= getHeading() < 360). This is not relative to the direction you are
        /// facing.
        ///
        /// @return the direction the bullet is/was heading, in degrees
        /// </summary>
        public double getHeading()
        {
            return Utils.toDegrees(headingRadians);
        }

        /// <summary>
        /// Returns the direction the bullet is/was heading, in radians
        /// (0 <= getHeadingRadians() < 2 * Math.PI). This is not relative to the
        /// direction you are facing.
        ///
        /// @return the direction the bullet is/was heading, in radians
        /// </summary>
        public double getHeadingRadians()
        {
            return headingRadians;
        }

        /// <summary>
        /// Returns the name of the robot that fired this bullet.
        ///
        /// @return the name of the robot that fired this bullet
        /// </summary>
        public string getName()
        {
            return ownerName;
        }

        /// <summary>
        /// Returns the power of this bullet.
        /// <p/>
        /// The bullet will do (4 * power) damage if it hits another robot.
        /// If power is greater than 1, it will do an additional 2 * (power - 1)
        /// damage. You will get (3 * power) back if you hit the other robot.
        ///
        /// @return the power of the bullet
        /// </summary>
        public double getPower()
        {
            return power;
        }

        /// <summary>
        /// Returns the velocity of this bullet. The velocity of the bullet is
        /// constant once it has been fired.
        ///
        /// @return the velocity of the bullet
        /// </summary>
        public double getVelocity()
        {
            return Rules.getBulletSpeed(power);
        }

        /// <summary>
        /// Returns the name of the robot that this bullet hit, or {@code null} if
        /// the bullet has not hit a robot.
        ///
        /// @return the name of the robot that this bullet hit, or {@code null} if
        ///         the bullet has not hit a robot.
        /// </summary>
        public string getVictim()
        {
            return victimName;
        }

        /// <summary>
        /// Returns the X position of the bullet.
        ///
        /// @return the X position of the bullet
        /// </summary>
        public double getX()
        {
            return x;
        }

        /// <summary>
        /// Returns the Y position of the bullet.
        ///
        /// @return the Y position of the bullet
        /// </summary>
        public double getY()
        {
            return y;
        }

        /// <summary>
        /// Checks if this bullet is still active on the battlefield.
        ///
        /// @return {@code true} if the bullet is still active on the battlefield;
        ///         {@code false} otherwise
        /// </summary>
        public bool isActive()
        {
            return _isActive;
        }

        /// <summary>
        /// Updates this bullet based on the specified bullet status.
        ///
        /// @param x the new X position of the bullet .
        /// @param y the new Y position of the bullet.
        /// @param victimName the name if the victim that has been hit by this bullet.
        /// @param isActive {@code true} if the bullet still moves; {@code false} otherwise.
        /// </summary>
        // this method is invisible on RobotAPI
        private void update(double x, double y, string victimName, bool isActive)
        {
            this.x = x;
            this.y = y;
            this.victimName = victimName;
            _isActive = isActive;
        }

        // this method is invisible on RobotAPI
        /// <summary>
        /// @return unique id of bullet for owner robot
        /// </summary>
        internal int getBulletId()
        {
            return bulletId;
        }

        /// <summary>
        /// Creates a hidden bullet helper for accessing hidden methods on this objec.
        /// 
        /// @return a hidden bullet helper.
        /// </summary>
        // this method is invisible on RobotAPI
        private static IHiddenBulletHelper createHiddenHelper()
        {
            return new HiddenBulletHelper();
        }

        /// <summary>
        /// Creates a hidden bullet helper for accessing hidden methods on this objec.
        ///
        /// @return a hidden bullet helper.
        /// </summary>
        // this class is invisible on RobotAPI
        private static ISerializableHelper createHiddenSerializer()
        {
            return new HiddenBulletHelper();
        }

        // this class is invisible on RobotAPI
        private class HiddenBulletHelper : IHiddenBulletHelper, ISerializableHelper
        {
            public void update(Bullet bullet, double x, double y, string victimName, bool isActive)
            {
                bullet.update(x, y, victimName, isActive);
            }

            public int sizeOf(RbnSerializer serializer, object objec)
            {
                var obj = (Bullet) objec;

                return RbnSerializer.SIZEOF_TYPEINFO + 4*RbnSerializer.SIZEOF_DOUBLE + serializer.sizeOf(obj.ownerName)
                       + serializer.sizeOf(obj.victimName) + RbnSerializer.SIZEOF_BOOL;
            }

            public void serialize(RbnSerializer serializer, ByteBuffer buffer, object objec)
            {
                var obj = (Bullet) objec;

                serializer.serialize(buffer, obj.headingRadians);
                serializer.serialize(buffer, obj.x);
                serializer.serialize(buffer, obj.y);
                serializer.serialize(buffer, obj.power);
                serializer.serialize(buffer, obj.ownerName);
                serializer.serialize(buffer, obj.victimName);
                serializer.serialize(buffer, obj._isActive);
            }

            public object deserialize(RbnSerializer serializer, ByteBuffer buffer)
            {
                double headingRadians = buffer.getDouble();
                double x = buffer.getDouble();
                double y = buffer.getDouble();
                double power = buffer.getDouble();
                string ownerName = serializer.deserializeString(buffer);
                string victimName = serializer.deserializeString(buffer);
                bool isActive = serializer.deserializeBoolean(buffer);

                return new Bullet(headingRadians, x, y, power, ownerName, victimName, isActive, -1);
            }
        }
    }
}
//happy