package extensions;

import robocode.Robject;

public class Flag extends Robject {

	public Flag(double x, double y, int teamNumber) {
		super("flag", x, y, 10, 10, false, false, false,
				true, true, true);
		if (teamNumber != 0)
		{
			this.teamNumber = teamNumber;
		}
	}

}
