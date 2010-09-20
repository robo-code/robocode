using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A TurnStartedEvent is sent to {@link IBattleListener#onTurnStarted(TurnStartedEvent)
     * onTurnStarted()} when a new turn in a battle round is started. 
     *
     * @see IBattleListener
     * @see TurnEndedEvent
     *
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class TurnStartedEvent : BattleEvent
    {
	    /**
	     * Called by the game to create a new TurnStartedEvent.
	     * Please don't use this constructor as it might change.
	     */
	    public TurnStartedEvent() : base()
        {
	    }	
    }
}
