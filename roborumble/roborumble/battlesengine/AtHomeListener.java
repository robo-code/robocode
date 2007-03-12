package roborumble.battlesengine;


import robocode.control.*;
import roborumble.battlesengine.Coordinator;
import java.io.*;

import apv.TeamCompetition;


/**
 * TeamsListener by Albert Perez
 */

public class AtHomeListener implements RobocodeListener {
	Coordinator coord = null;

	public AtHomeListener(Coordinator c) {
		coord = c;
	}

	public void battleComplete(BattleSpecification battlespecification, RobotResults[] robotresultses) {
		if (coord != null) {
			coord.put();
		}
	}
    
	public void battleAborted(BattleSpecification battlespecification) {}
    
	public void battleMessage(String string) {}
	
}
