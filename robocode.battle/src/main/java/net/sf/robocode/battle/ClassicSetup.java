package net.sf.robocode.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.robocode.battle.peer.RobjectPeer;
import net.sf.robocode.battle.peer.RobotPeer;

import robocode.control.RandomFactory;

public class ClassicSetup extends BattlefieldSetup {

	public List<RobjectPeer> setupObjects(int battlefieldWidth,
			int battlefieldHeight) {
		// return an empty list, we don't want any objects for classic mode
		return new ArrayList<RobjectPeer>();
	}

	@Override
	public double[][] computeInitialPositions(String initialPositions, int battlefieldWidth, int battlefieldHeight) {
		double [][] initialRobotPositions = null;

		if (initialPositions == null || initialPositions.trim().length() == 0) {
			return initialRobotPositions;
		}

		List<String> positions = new ArrayList<String>();

		Pattern pattern = Pattern.compile("([^,(]*[(][^)]*[)])?[^,]*,?");
		Matcher matcher = pattern.matcher(initialPositions);

		while (matcher.find()) {
			String pos = matcher.group();

			if (pos.length() > 0) {
				positions.add(pos);
			}
		}

		if (positions.size() == 0) {
			return initialRobotPositions;
		}

		initialRobotPositions = new double[positions.size()][3];

		String[] coords;
		double x = 0, y = 0, heading;

		for (int i = 0; i < positions.size(); i++) {
			coords = positions.get(i).split(",");

			final Random random = RandomFactory.getRandom();

			x = RobotPeer.WIDTH + random.nextDouble() * (battlefieldWidth - 2 * RobotPeer.WIDTH);
			y = RobotPeer.HEIGHT + random.nextDouble() * (battlefieldHeight - 2 * RobotPeer.HEIGHT);
				
			heading = 2 * Math.PI * random.nextDouble();

			int len = coords.length;

			if (len >= 1) {
				// noinspection EmptyCatchBlock
				try {
					x = Double.parseDouble(coords[0].replaceAll("[\\D]", ""));
				} catch (NumberFormatException e) {}

				if (len >= 2) {
					// noinspection EmptyCatchBlock
					try {
						y = Double.parseDouble(coords[1].replaceAll("[\\D]", ""));
					} catch (NumberFormatException e) {}

					if (len >= 3) {
						// noinspection EmptyCatchBlock
						try {
							heading = Math.toRadians(Double.parseDouble(coords[2].replaceAll("[\\D]", "")));
						} catch (NumberFormatException e) {}
					}
				}
			}
			initialRobotPositions[i][0] = x;
			initialRobotPositions[i][1] = y;
			initialRobotPositions[i][2] = heading;
		}
		return initialRobotPositions;
	}

}
