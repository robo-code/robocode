#region Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001-2014 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control
{
    /// <summary>
    /// A BattleSpecification defines a battle configuration used by the <see cref="RobocodeEngine"/>.
    /// </summary>
    [Serializable]
    public class BattleSpecification
    {
        private readonly int battlefieldWidth;
        private readonly int battlefieldHeight;
        private readonly int numRounds;
        private readonly double gunCoolingRate;
        private readonly long inactivityTime;
        private readonly bool hideEnemyNames;
        private readonly int sentryBorderSize;
        private readonly RobotSpecification[] robots;

        /// <summary>
        /// Creates a new BattleSpecification with the given number of rounds,
        /// battlefield size, and robots. Inactivity time for the robots defaults to
        /// 450, and the gun cooling rate defaults to 0.1.
        /// </summary>
        /// <param name="numRounds">The number of rounds in this battle.</param>
        /// <param name="battlefieldSize">The battlefield size.</param>
        /// <param name="robots">The robots participating in this battle.</param>
        public BattleSpecification(int numRounds, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots) :
            this(numRounds, 450, .1, battlefieldSize, robots)
        {
        }

        /// <summary>
        /// Creates a new BattleSpecification with the given settings.
        /// </summary>
        /// <param name="numRounds">The number of rounds in this battle.</param>
        /// <param name="inactivityTime">The inactivity time allowed for the robots before
        /// they will loose energy.</param>
        /// <param name="gunCoolingRate">The gun cooling rate for the robots.</param>
        /// <param name="battlefieldSize">The battlefield size.</param>
        /// <param name="robots">The robots participating in this battle.</param>
        public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots) :
            this(numRounds, inactivityTime, gunCoolingRate, false, battlefieldSize, robots)
        {
        }

        /// <summary>
        /// Creates a new BattleSpecification with the given settings.
        /// </summary>
        /// <param name="numRounds">The number of rounds in this battle.</param>
        /// <param name="inactivityTime">The inactivity time allowed for the robots before
        /// they will loose energy.</param>
        /// <param name="gunCoolingRate">The gun cooling rate for the robots.</param>
        /// <param name="hideEnemyNames">Flag specifying if enemy names are hidden from robots.</param>
        /// <param name="battlefieldSize">The battlefield size.</param>
        /// <param name="robots">The robots participating in this battle.</param>
        public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, bool hideEnemyNames, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots)
        {
            this.numRounds = numRounds;
            this.inactivityTime = inactivityTime;
            this.gunCoolingRate = gunCoolingRate;
            this.hideEnemyNames = hideEnemyNames;
            this.battlefieldWidth = battlefieldSize.Width;
            this.battlefieldHeight = battlefieldSize.Height;
            this.robots = robots;
        }

        /// <summary>
        /// Creates a new BattleSpecification with the given settings.
        /// </summary>
        /// <param name="numRounds">The number of rounds in this battle.</param>
        /// <param name="inactivityTime">The inactivity time allowed for the robots before
        /// they will loose energy.</param>
        /// <param name="gunCoolingRate">The gun cooling rate for the robots.</param>
        /// <param name="sentryBorderSize">The sentry border size for a <see cref="Robocode.IBorderSentry">BorderSentry</see>.</param>
        /// <param name="hideEnemyNames">Flag specifying if enemy names are hidden from robots.</param>
        /// <param name="battlefieldSize">The battlefield size.</param>
        /// <param name="robots">The robots participating in this battle.</param>
        public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, int sentryBorderSize, bool hideEnemyNames, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots)
        {
            this.numRounds = numRounds;
            this.inactivityTime = inactivityTime;
            this.gunCoolingRate = gunCoolingRate;
            this.sentryBorderSize = sentryBorderSize;
            this.hideEnemyNames = hideEnemyNames;
            this.battlefieldWidth = battlefieldSize.Width;
            this.battlefieldHeight = battlefieldSize.Height;
            this.robots = robots;
        }

        /// <summary>
        /// Contains the allowed inactivity time for the robots in this battle.
        /// </summary>
        /// <value>
        /// The allowed inactivity time for the robots in this battle.
        /// </value>
        public long InactivityTime
        {
            get { return inactivityTime; }
        }

        /// <summary>
        /// Contains the gun cooling rate of the robots in this battle.
        /// </summary>
        /// <value>
        /// The gun cooling rate of the robots in this battle.
        /// </value>
        public double GunCoolingRate
        {
            get { return gunCoolingRate; }
        }

        /// <summary>
        /// Contains the battlefield size for this battle.
        /// </summary>
        /// <value>
        /// The battlefield size for this battle.
        /// </value>
        public BattlefieldSpecification Battlefield
        {
            get { return new BattlefieldSpecification(battlefieldWidth, battlefieldHeight); }
        }

        /// <summary>
        /// Contains the number of rounds in this battle.
        /// </summary>
        /// <value>
        /// The number of rounds in this battle.
        /// </value>
        public int NumRounds
        {
            get { return numRounds; }
        }

        /// <summary>
        /// Flag specifying if the enemy names must be hidden from events sent to robots.
        /// </summary>
        /// <value>
        /// true if the enemy names must be hidden; false otherwise.
        /// </value>
        public bool HideEnemyNames
        {
            get { return hideEnemyNames; }
        }

        ///<summary>
        ///  Returns the sentry border size for a <see cref="Robocode.IBorderSentry">BorderSentry</see> that defines the how
        ///  far a BorderSentry is allowed to move from the border edges measured in units.
        ///  <p/>
        ///  Hence, the sentry border size defines the width/range of the border area surrounding the battlefield that
	    ///  border sentry robots cannot leave (they must stay in the border area), but it also define the
        ///  distance from the border edges where border sentry robots are allowed/able to make damage to robots entering this
        ///  border area.
        ///</summary>
        public int SentryBorderSize
        {
            get { return sentryBorderSize; }
        }

        /// <summary>
        /// Contains the specifications of the robots participating in this battle.
        /// </summary>
        /// <value>
        /// The specifications of the robots participating in this battle.
        /// </value>
        public RobotSpecification[] Robots
        {
            get
            {
                if (robots == null)
                    return null;

                RobotSpecification[] copy = new RobotSpecification[robots.Length];
                robots.CopyTo(copy, 0);
                return copy;
            }
        }
    }
}
