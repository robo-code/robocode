using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A RoundEndedEvent is sent to {@link IBattleListener#onRoundEnded(RoundEndedEvent)
     * onRoundEnded()} when the current round of a battle has ended. 
     *
     * @see IBattleListener
     * @see RoundStartedEvent
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class RoundEndedEvent : BattleEvent
    {
	    private readonly int round;
	    private readonly int turns;
        private readonly int totalTurns;

	    /**
	     * Called by the game to create a new RoundEndedEvent.
	     * Please don't use this constructor as it might change.
	     *
	     * @param round the round number that was ended (zero indexed).
	     * @param turns the number of turns that this round reached.
	     * @param totalTurns the total number of turns reached in the battle when this round ended.
	     */
	    public RoundEndedEvent(int round, int turns, int totalTurns) : base()
        {
		    this.round = round;
		    this.turns = turns;
		    this.totalTurns = totalTurns;
	    }

	    /**
	     * Returns the round number that was ended (zero indexed).
	     *
	     * @return the round number that was ended (zero indexed).
	     */
	    public int Round {
            get { return round; }
	    }

	    /**
	     * Returns the number of turns that this round reached.
	     *
	     * @return the number of turns that this round reached.
	     */
	    public int Turns {
		    get { return turns; }
	    }

	    /**
	     * Returns the total number of turns reached in the battle when this round ended. 
	     *
	     * @return the total number of turns reached in the battle when this round ended.
	     *
	     * @since 1.7.2
	     */
	    public int TotalTurns {
		    get { return totalTurns; }
	    }
    }
}
