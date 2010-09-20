using System;
using System.Collections.Generic;
using System.Text;
using Robocode.Control.Snapshot;

namespace Robocode.Control.Events
{
    /**
     * A RoundStartedEvent is sent to {@link IBattleListener#onRoundStarted(RoundStartedEvent)
     * onRoundStarted()} when a new round in a battle is started. 
     *
     * @see IBattleListener
     * @see RoundEndedEvent
     *
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class RoundStartedEvent : BattleEvent
    {
	    private readonly ITurnSnapshot startSnapshot;
        private readonly int round;

	    /**
	     * Called by the game to create a new RoundStartedEvent.
	     * Please don't use this constructor as it might change.
	     *
	     * @param startSnapshot the start snapshot of the participating robots, initial starting positions etc.
	     * @param round the round number (zero indexed).
	     */
	    public RoundStartedEvent(ITurnSnapshot startSnapshot, int round) : base()
        {
		    this.startSnapshot = startSnapshot;
		    this.round = round;
	    }

	    /**
	     * Returns the start snapshot of the participating robots, initial starting positions etc.
	     *
	     * @return a ITurnSnapshot instance that serves as the start snapshot of the round.
	     */
	    public ITurnSnapshot StartSnapshot {
            get { return startSnapshot; }
	    }

	    /**
	     * Returns the round number (zero indexed).
	     *
	     * @return the round number (zero indexed).
	     */
	    public int Round {
            get { return round; }
	    }
    }
}
