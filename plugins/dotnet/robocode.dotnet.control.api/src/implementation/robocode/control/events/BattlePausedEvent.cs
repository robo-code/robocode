using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A BattlePausedEvent is sent to {@link IBattleListener#onBattlePaused(BattlePausedEvent)
     * onBattlePaused()} when the battle has been paused. 
     *
     * @see IBattleListener
     * @see BattleResumedEvent
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class BattlePausedEvent : BattleEvent
    {
	    /**
	     * Called by the game to create a new BattlePausedEvent.
	     * Please don't use this constructor as it might change.
	     */
	    public BattlePausedEvent()
            : base()
        {
	    }
    }
}
