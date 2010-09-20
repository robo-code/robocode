using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Events
{
    /**
     * A BattleErrorEvent is sent to {@link IBattleListener#onBattleError(BattleErrorEvent)
     * onBattleError()} when an error message is sent from the game in the during the battle. 
     * 
     * @see IBattleListener
     * @see BattleMessageEvent
     *
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public class BattleErrorEvent : BattleEvent
    {
	    private readonly string error;

	    /**
	     * Called by the game to create a new BattleErrorEvent.
	     * Please don't use this constructor as it might change.
	     *
	     * @param error the error message.
	     */
	    public BattleErrorEvent(string error) : base()
        {
		    this.error = error;
	    }

	    /**
	     * Returns the error message.
	     *
	     * @return the error message.
	     */
	    public string Error
        {
            get { return error; }
	    }
    }
}
