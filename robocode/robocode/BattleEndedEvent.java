/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode;

/**
 * @author Pavel Savara (original)
 */
public class BattleEndedEvent extends Event {
    private boolean aborted;
    private BattleResults results;
    public BattleEndedEvent(boolean aborted, BattleResults results){
        this.aborted=aborted;
        this.results=results;
    }

    public boolean getAborted(){
        return aborted;
    }

    public BattleResults getResults(){
        return results;
    }

    

}
