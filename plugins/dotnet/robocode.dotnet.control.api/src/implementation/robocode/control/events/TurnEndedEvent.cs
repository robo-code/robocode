using System;
using System.Collections.Generic;
using System.Text;
using Robocode.Control.Snapshot;

namespace Robocode.Control.Events
{
    /**
     * A TurnEndedEvent is sent to {@link IBattleListener#onTurnEnded(TurnEndedEvent)
     * onTurnEnded()} when the current turn in a battle round is ended. 
     *
     * @see IBattleListener
     * @see TurnStartedEvent
     *
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class TurnEndedEvent : BattleEvent
    {
	    private readonly ITurnSnapshot turnSnapshot;

	    /**
	     * Called by the game to create a new TurnEndedEvent.
	     * Please don't use this constructor as it might change.
	     *
	     * @param turnSnapshot a snapshot of the turn that has ended.
	     */
	    public TurnEndedEvent(ITurnSnapshot turnSnapshot) : base()
        {
		    this.turnSnapshot = turnSnapshot;
	    }

	    /**
	     * Returns a snapshot of the turn that has ended.
	     *
	     * @return a snapshot of the turn that has ended.
	     */
	    public ITurnSnapshot TurnSnapshot {
            get { return turnSnapshot; }
	    }
    }
}
