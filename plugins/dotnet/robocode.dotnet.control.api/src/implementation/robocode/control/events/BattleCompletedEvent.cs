using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A BattleCompletedEvent is sent to {@link IBattleListener#onBattleCompleted(BattleCompletedEvent)
     * onBattleCompleted()} when the battle is completed successfully and results are available. This event
     * will not occur if the battle is terminated or aborted by the user before the battle is completed.
     *
     * @see IBattleListener
     * @see BattleStartedEvent
     * @see BattleFinishedEvent
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class BattleCompletedEvent : BattleEvent
    {
	    private readonly BattleRules battleRules;
        private readonly BattleResults[] results;

	    /**
	     * Called by the game to create a new BattleCompletedEvent.
	     * Please don't use this constructor as it might change.
	     *
	     * @param battleRules the rules that was used in the battle.
	     * @param results the indexed results of the battle. These are unsorted, but using robot indexes.
	     */
	    public BattleCompletedEvent(BattleRules battleRules, BattleResults[] results) : base()
        {
		    this.battleRules = battleRules;
		    this.results = results;
	    }

	    /**
	     * Returns the rules that was used in the battle.
	     *
	     * @return the rules of the battle.
	     */
	    public BattleRules BattleRules {
            get { return battleRules; }
	    }

	    /**
	     * Returns the battle results sorted on score, meaning that robot indexes cannot be used.
	     *
	     * @return a sorted array of BattleResults, where the results with the biggest score are placed first in the list.
	     */
	    public BattleResults[] SortedResults
        {
            get
            {
                List<BattleResults> copy = new List<BattleResults>(results);
                copy.Sort();
                copy.Reverse();
                return copy.ToArray();
            }
	    }

	    /**
	     * Returns the unsorted battle results so that robot indexes can be used.
	     *
	     * @return an unsorted array of BattleResults, where each index matches an index of a specific robot.
	     */
	    public BattleResults[] IndexedResults
        {
            get
            {
		        BattleResults[] copy = new BattleResults[results.Length];
                results.CopyTo(copy, 0);
		        return copy;
            }
	    }
    }
}
