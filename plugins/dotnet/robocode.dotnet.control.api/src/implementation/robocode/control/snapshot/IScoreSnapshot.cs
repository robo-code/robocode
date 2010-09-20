using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /**
     * Interface of a score snapshot at a specific time instant in a battle.
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public interface IScoreSnapshot : IComparable<IScoreSnapshot>
    {
	    /**
	     * Returns the name of the contestant, i.e. a robot or team.
	     *
	     * @return the name of the contestant, i.e. a robot or team.
	     */
        string Name { get; }

	    /**
	     * Returns the total score.
	     *
	     * @return the total score.
	     */
        double TotalScore { get; }

	    /**
	     * Returns the total survival score.
	     *
	     * @return the total survival score.
	     */
        double TotalSurvivalScore { get; }

	    /**
	     * Returns the total last survivor score.
	     *
	     * @return the total last survivor score.
	     */
        double TotalLastSurvivorBonus { get; }

	    /**
	     * Returns the total bullet damage score.
	     *
	     * @return the total bullet damage score.
	     */
        double TotalBulletDamageScore { get; }

	    /**
	     * Returns the total bullet kill bonus.
	     *
	     * @return the total bullet kill bonus.
	     */
        double TotalBulletKillBonus { get; }

	    /**
	     * Returns the total ramming damage score.
	     *
	     * @return the total ramming damage score.
	     */
        double TotalRammingDamageScore { get; }

	    /**
	     * Returns the total ramming kill bonus.
	     *
	     * @return the total ramming kill bonus.
	     */
        double TotalRammingKillBonus { get; }

	    /**
	     * Returns the total number of first places.
	     *
	     * @return the total number of first places.
	     */
        int TotalFirsts { get; }

	    /**
	     * Returns the total number of second places.
	     *
	     * @return the total number of second places.
	     */
        int TotalSeconds { get; }

	    /**
	     * Returns the total number of third places.
	     *
	     * @return the total number of third places.
	     */
        int TotalThirds { get; }

	    /**
	     * Returns the current score.
	     *
	     * @return the current score.
	     */
        double CurrentScore { get; }

	    /**
	     * Returns the current survival score.
	     *
	     * @return the current survival score.
	     */
        double CurrentSurvivalScore { get; }

	    /**
	     * Returns the current survival bonus.
	     *
	     * @return the current survival bonus.
	     */
        double CurrentSurvivalBonus { get; }

	    /**
	     * Returns the current bullet damage score.
	     *
	     * @return the current bullet damage score.
	     */
        double CurrentBulletDamageScore { get; }

	    /**
	     * Returns the current bullet kill bonus.
	     *
	     * @return the current bullet kill bonus.
	     */
        double CurrentBulletKillBonus { get; }

	    /**
	     * Returns the current ramming damage score.
	     *
	     * @return the current ramming damage score.
	     */
        double CurrentRammingDamageScore { get; }

	    /**
	     * Returns the current ramming kill bonus.
	     *
	     * @return the current ramming kill bonus.
	     */
        double CurrentRammingKillBonus { get; }
    }
}
