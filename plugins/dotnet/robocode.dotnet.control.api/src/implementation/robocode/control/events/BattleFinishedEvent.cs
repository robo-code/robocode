using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A BattleFinishedEvent is sent to {@link IBattleListener#onBattleFinished(BattleFinishedEvent)
     * onBattleFinished()} when the battle is finished. This event is always sent as the last battle event,
     * both when the battle is completed successfully, terminated due to an error, or aborted by the user.
     * Hence, this events is well-suited for cleanup after the battle. 
     *
     * @see IBattleListener
     * @see BattleStartedEvent
     * @see BattleCompletedEvent
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class BattleFinishedEvent : BattleEvent
    {
	    private readonly bool isAborted;

	    /**
	     * Called by the game to create a new BattleFinishedEvent.
	     * Please don't use this constructor as it might change.
	     *
	     * @param isAborted a flag specifying if the battle was aborted:
	     *                  {@code true} if the battle was aborted; {@code false} otherwise.
	     */
	    public BattleFinishedEvent(bool isAborted) : base()
        {
		    this.isAborted = isAborted;
	    }

	    /**
	     * Checks if the battle was aborted.
	     *
	     * @return {@code true} if the battle was aborted; {@code false} otherwise.
	     */
	    public bool IsAborted
        {
            get { return isAborted; }
	    }
    }
}
