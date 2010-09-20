using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A BattleResumedEvent is sent to {@link IBattleListener#onBattleResumed(BattleResumedEvent)
     * onBattleResumed()} when the battle has been resumed (after having been paused). 
     *
     * @see IBattleListener
     * @see BattlePausedEvent
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class BattleResumedEvent : BattleEvent
    {
	    /**
	     * Called by the game to create a new BattleResumedEvent.
	     * Please don't use this constructor as it might change.
	     */
	    public BattleResumedEvent() : base()
        {
	    }
    }
}
