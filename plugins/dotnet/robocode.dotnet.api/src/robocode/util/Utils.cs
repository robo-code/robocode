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
 *     - Moved all methods to classes like FileUtil, stringUtil, WindowUtil,
 *       Logger etc. exception for the following methods, which have been kept
 *       here as legacy robots make use of these methods:
 *       - NormalAbsoluteAngle()
 *       - NormalNearAbsoluteAngle()
 *       - NormalRelativeAngle()
 *     - The IsNear() was made public
 *     - Optimized and provided javadocs for all methods
 *******************************************************************************/
using System;
using net.sf.robocode.security;

namespace robocode.util
{
    /// <summary>
    /// Utility class that provide methods for normalizing angles.
    ///
    /// @author Mathew A. Nelson (original)
    /// @author Flemming N. Larsen (contributor)
    /// </summary>
    public static class Utils
    {
        private const double TWO_PI = 2*Math.PI;
        private const double THREE_PI_OVER_TWO = 3*Math.PI/2;
        private const double PI_OVER_TWO = Math.PI/2;
        private const double NEAR_DELTA = .00001;

        /// <summary>
        /// Normalizes an angle to an absolute angle.
        /// The normalized angle will be in the range from 0 to 2*PI, where 2*PI
        /// itself is not included.
        ///
        /// @param angle the angle to normalize
        /// @return the normalized angle that will be in the range of [0,2*PI[
        /// </summary>
        public static double NormalAbsoluteAngle(double angle)
        {
            return (angle %= TWO_PI) >= 0 ? angle : (angle + TWO_PI);
        }

        /// <summary>
        /// Normalizes an angle to an absolute angle.
        /// The normalized angle will be in the range from 0 to 360, where 360
        /// itself is not included.
        ///
        /// @param angle the angle to normalize
        /// @return the normalized angle that will be in the range of [0,360[
        /// </summary>
        public static double NormalAbsoluteAngleDegrees(double angle)
        {
            return (angle %= 360) >= 0 ? angle : (angle + 360);
        }

        /// <summary>
        /// Normalizes an angle to a relative angle.
        /// The normalized angle will be in the range from -PI to PI, where PI
        /// itself is not included.
        ///
        /// @param angle the angle to normalize
        /// @return the normalized angle that will be in the range of [-PI,PI[
        /// </summary>
        public static double NormalRelativeAngle(double angle)
        {
            return (angle %= TWO_PI) >= 0
                       ? (angle < Math.PI) ? angle : angle - TWO_PI
                       : (angle >= -Math.PI) ? angle : angle + TWO_PI;
        }

        /// <summary>
        /// Normalizes an angle to a relative angle.
        /// The normalized angle will be in the range from -180 to 180, where 180
        /// itself is not included.
        ///
        /// @param angle the angle to normalize
        /// @return the normalized angle that will be in the range of [-180,180[
        /// </summary>
        public static double NormalRelativeAngleDegrees(double angle)
        {
            return (angle %= 360) >= 0 ? (angle < 180) ? angle : angle - 360 : (angle >= -180) ? angle : angle + 360;
        }

        /// <summary>
        /// Normalizes an angle to be near an absolute angle.
        /// The normalized angle will be in the range from 0 to 360, where 360
        /// itself is not included.
        /// If the normalized angle is near to 0, 90, 180, 270 or 360, that
        /// angle will be returned. The {@link #IsNear(double, double) IsNear}
        /// method is used for defining when the angle is near one of angles listed
        /// above.
        ///
        /// @param angle the angle to normalize
        /// @return the normalized angle that will be in the range of [0,360[
        /// @see #NormalAbsoluteAngle(double)
        /// @see #IsNear(double, double)
        /// </summary>
        public static double NormalNearAbsoluteAngleDegrees(double angle)
        {
            angle = (angle %= 360) >= 0 ? angle : (angle + 360);

            if (IsNear(angle, 180))
            {
                return 180;
            }
            else if (angle < 180)
            {
                if (IsNear(angle, 0))
                {
                    return 0;
                }
                else if (IsNear(angle, 90))
                {
                    return 90;
                }
            }
            else
            {
                if (IsNear(angle, 270))
                {
                    return 270;
                }
                else if (IsNear(angle, 360))
                {
                    return 0;
                }
            }
            return angle;
        }

        /// <summary>
        /// Normalizes an angle to be near an absolute angle.
        /// The normalized angle will be in the range from 0 to 2*PI, where 2*PI
        /// itself is not included.
        /// If the normalized angle is near to 0, PI/2, PI, 3*PI/2 or 2*PI, that
        /// angle will be returned. The {@link #IsNear(double, double) IsNear}
        /// method is used for defining when the angle is near one of angles listed
        /// above.
        ///
        /// @param angle the angle to normalize
        /// @return the normalized angle that will be in the range of [0,2*PI[
        /// @see #NormalAbsoluteAngle(double)
        /// @see #IsNear(double, double)
        /// </summary>
        public static double NormalNearAbsoluteAngle(double angle)
        {
            angle = (angle %= TWO_PI) >= 0 ? angle : (angle + TWO_PI);

            if (IsNear(angle, Math.PI))
            {
                return Math.PI;
            }
            else if (angle < Math.PI)
            {
                if (IsNear(angle, 0))
                {
                    return 0;
                }
                else if (IsNear(angle, PI_OVER_TWO))
                {
                    return PI_OVER_TWO;
                }
            }
            else
            {
                if (IsNear(angle, THREE_PI_OVER_TWO))
                {
                    return THREE_PI_OVER_TWO;
                }
                else if (IsNear(angle, TWO_PI))
                {
                    return 0;
                }
            }
            return angle;
        }

        /// <summary>
        /// Tests if the two {@code double} values are near to each other.
        /// It is recommended to use this method instead of testing if the two
        /// doubles are equal using an this expression: {@code value1 == value2}.
        /// The reason being, that this expression might never become
        /// {@code true} due to the precision of double values.
        /// Whether or not the specified doubles are near to each other is defined by
        /// the following expression:
        /// {@code (Math.abs(value1 - value2) < .00001)}
        ///
        /// @param value1 the first double value
        /// @param value2 the second double value
        /// @return {@code true} if the two doubles are near to each other;
        ///         {@code false} otherwise.
        /// </summary>
        public static bool IsNear(double value1, double value2)
        {
            return (Math.Abs(value1 - value2) < NEAR_DELTA);
        }

        /// <summary>
        /// Returns random number generator. It might be configured for repeatable behavior by setting -DRANDOMSEED option.
        ///
        /// @return random number generator
        /// </summary>
        public static Random GetRandom()
        {
            return HiddenAccessN.randomHelper.GetRandom();
        }

        public static double ToRadians(double angle)
        {
            return Math.PI*angle/180.0;
        }

        public static double ToDegrees(double angle)
        {
            return angle*(180.0/Math.PI);
        }
    }
}

//happy