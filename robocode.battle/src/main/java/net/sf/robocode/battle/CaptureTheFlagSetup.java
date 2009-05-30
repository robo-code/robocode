package net.sf.robocode.battle;

import java.util.ArrayList;
import java.util.List;

import robocode.Obstacle;
import robocode.Robject;
import robocode.Trench;

public class CaptureTheFlagSetup implements IBattlefieldSetup{

	public List<Robject> setupObjects() {
		List<Robject> robjects = new ArrayList<Robject>();

		robjects.add(new Trench("pit", 100, 100, 50, 50));
		robjects.add(new Obstacle("box", 400, 400, 100, 400));
		robjects.add(new Trench("pit", 600, 550, 20, 20));
		robjects.add(new Trench("pit", 167, 264, 200, 256));
		
		return robjects;
	}

}
