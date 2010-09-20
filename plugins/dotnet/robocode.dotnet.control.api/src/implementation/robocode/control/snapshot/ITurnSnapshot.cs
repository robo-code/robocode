using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /**
     * Interface of a battle turn snapshot at a specific time instant in a battle.
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public interface ITurnSnapshot
    {
        /**
         * Returns a list of snapshots for the robots participating in the battle. 
         *
         * @return a list of snapshots for the robots participating in the battle. 
         */
        IRobotSnapshot[] Robots { get; }

        /**
         * Returns a list of snapshots for the bullets that are currently on the battlefield.
         *
         * @return a list of snapshots for the bullets that are currently on the battlefield.
         */
        IBulletSnapshot[] Bullets { get; }

        /**
         * Returns the current TPS (turns per second).
         *
         * @return the current TPS (turns per second).
         */
        int TPS { get; }

        /**
         * Returns the current round of the battle.
         *
         * @return the current round of the battle.
         */
        int Round { get; }

        /**
         * Returns the current turn in the battle round.
         *
         * @return the current turn in the battle round.
         */
        int Turn { get; }

        /**
         * Returns a list of sorted scores grouped by teams, ordered by position.
         *
         * @return a list of sorted scores grouped by teams, ordered by position.
         */
        IScoreSnapshot[] SortedTeamScores { get; }

        /**
         * Returns a list of indexed scores grouped by teams, i.e. unordered.
         *
         * @return a list of indexed scores grouped by teams, i.e. unordered.
         */
        IScoreSnapshot[] IndexedTeamScores { get; }
    }
}
