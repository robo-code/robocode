#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
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
        public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots)
        {
            this.numRounds = numRounds;
            this.inactivityTime = inactivityTime;
            this.gunCoolingRate = gunCoolingRate;
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
