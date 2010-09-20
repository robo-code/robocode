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
    /**
     * A BattleSpecification defines battle configuration used by the
     * {@link RobocodeEngine}.
     *
     * @author Mathew A. Nelson (original)
     * @author Flemming N. Larsen (contributor)
     */
    [Serializable]
    public class BattleSpecification {

	    private readonly int battlefieldWidth;
	    private readonly int battlefieldHeight;
	    private readonly int numRounds;
	    private readonly double gunCoolingRate;
	    private readonly long inactivityTime;
        private readonly RobotSpecification[] robots;

	    /**
	     * Creates a new BattleSpecification with the given number of rounds,
	     * battlefield size, and robots. Inactivity time for the robots defaults to
	     * 450, and the gun cooling rate defaults to 0.1.
	     *
	     * @param numRounds	   the number of rounds in this battle
	     * @param battlefieldSize the battlefield size
	     * @param robots		  the robots participating in this battle
	     */
	    public BattleSpecification(int numRounds, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots) :
            this(numRounds, 450, .1, battlefieldSize, robots)
        {
	    }

	    /**
	     * Creates a new BattleSpecification with the given settings.
	     *
	     * @param numRounds	   the number of rounds in this battle
	     * @param inactivityTime  the inactivity time allowed for the robots before
	     *                        they will loose energy
	     * @param gunCoolingRate  the gun cooling rate for the robots
	     * @param battlefieldSize the battlefield size
	     * @param robots		  the robots participating in this battle
	     */
	    public BattleSpecification(int numRounds, long inactivityTime, double gunCoolingRate, BattlefieldSpecification battlefieldSize, RobotSpecification[] robots)
        {
		    this.numRounds = numRounds;
		    this.inactivityTime = inactivityTime;
		    this.gunCoolingRate = gunCoolingRate;
		    this.battlefieldWidth = battlefieldSize.Width;
		    this.battlefieldHeight = battlefieldSize.Height;
		    this.robots = robots;
	    }

	    /**
	     * Returns the allowed inactivity time for the robots in this battle.
	     *
	     * @return the allowed inactivity time for the robots in this battle.
	     */
	    public long InactivityTime {
		    get { return inactivityTime; }
	    }

	    /**
	     * Returns the gun cooling rate of the robots in this battle.
	     *
	     * @return the gun cooling rate of the robots in this battle.
	     */
	    public double GunCoolingRate {
            get { return gunCoolingRate; }
	    }

	    /**
	     * Returns the battlefield size for this battle.
	     *
	     * @return the battlefield size for this battle.
	     */
	    public BattlefieldSpecification Battlefield {
            get { return new BattlefieldSpecification(battlefieldWidth, battlefieldHeight); }
	    }

	    /**
	     * Returns the number of rounds in this battle.
	     *
	     * @return the number of rounds in this battle.
	     */
	    public int NumRounds {
		    get { return numRounds; }
	    }

        /**
         * Returns the specifications of the robots participating in this battle.
         *
         * @return the specifications of the robots participating in this battle.
         */
        public RobotSpecification[] Robots
        {
            get
            {
                if (robots == null)
                {
                    return null;
                }
                RobotSpecification[] copy = new RobotSpecification[robots.Length];
                robots.CopyTo(copy, 0);
                return copy;
            }
	    }
    }
}
