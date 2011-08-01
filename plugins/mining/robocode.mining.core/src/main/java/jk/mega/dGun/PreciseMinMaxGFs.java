package jk.mega.dGun;


import java.awt.geom.Point2D;

import java.util.ArrayList;
import java.util.Collections;

import robocode.util.Utils;


/**
 * @author Julian Kent
 *         http://robowiki.net/wiki/DrussGT
 */
public class PreciseMinMaxGFs {

	static double MARGIN = 18;
	static double WIDTH = 800;
	static double HEIGHT = 600;

	public static double[] getPreciseMEAs(
			Point2D.Double enemyLocation,
			double enemyHeading,
			double enemyVelocity,
			Point2D.Double myLocation,
			double bulletPower,
			double rotationDirection,
			ArrayList<Point2D.Double> pointsList) {

		ArrayList<Point2D.Double> endPoints = getEndPoints(enemyLocation, enemyHeading, enemyVelocity, myLocation,
				bulletPower, pointsList);
		// pointsList.addAll(endPoints);
		double negAngle = Double.POSITIVE_INFINITY;
		double posAngle = Double.NEGATIVE_INFINITY;
		double directBearing = absoluteBearing(myLocation, enemyLocation);

		for (int i = endPoints.size() - 1; i >= 0; i--) {
			double offset = Utils.normalRelativeAngle(absoluteBearing(myLocation, endPoints.get(i)) - directBearing);

			if (offset < negAngle) {
				negAngle = offset;
			}

			if (offset > posAngle) {
				posAngle = offset;
			}
		}
		if (rotationDirection == 1) {
			return new double[] { -negAngle, posAngle};
		}
		return new double[] { posAngle, -negAngle};
	}

	static ArrayList<Point2D.Double> getEndPoints(
			Point2D.Double enemyLocation,
			double enemyHeading,
			double enemyVelocity,
			Point2D.Double myLocation,
			double bulletPower,
			ArrayList<Point2D.Double> pointsList
			) {
		ArrayList<Point2D.Double> endPoints = new ArrayList<Point2D.Double>();

		Collections.addAll(endPoints,
				getSmoothedEndPoints(enemyLocation, enemyHeading, enemyVelocity, myLocation, bulletPower, pointsList));
		Collections.addAll(endPoints,
				getDirectEndPoints(enemyLocation, enemyHeading, enemyVelocity, myLocation, bulletPower, endPoints, pointsList));
		Collections.addAll(endPoints,
				getStraightEndPoints(enemyLocation, enemyHeading, enemyVelocity, myLocation, bulletPower, pointsList));

		return endPoints;
	}

	static Point2D.Double[] getSmoothedEndPoints(
			Point2D.Double enemyLocation,
			double enemyHeading,
			double enemyVelocity,
			Point2D.Double myLocation,
			double bulletPower,
			ArrayList<Point2D.Double> pointsList) {
		Point2D.Double[] locs = new Point2D.Double[2];
		double bulletVelocity = 20 - 3 * bulletPower;

		for (int i = -1; i < 2; i += 2) {
			double angle = enemyHeading + (Math.PI / 2) * (i - 1);
			double vel = i * enemyVelocity;
			Point2D.Double eloc = enemyLocation;
			double bulletDistance = 0;
			double goalAngle = absoluteBearing(myLocation, eloc) + Math.PI / 2;

			if (Math.cos(goalAngle - angle) < 0) {
				goalAngle += Math.PI;
			}
			while (bulletDistance < eloc.distance(myLocation)) {
				Point2D.Double testPoint = project(eloc, goalAngle, 120);
				double testBearing = absoluteBearing(myLocation, testPoint);
				double testDistance = testPoint.distance(myLocation);

				while (MARGIN > testPoint.x || testPoint.x > WIDTH - MARGIN || MARGIN > testPoint.y
						|| testPoint.y > HEIGHT - MARGIN) {
					testDistance *= 0.95;
					testPoint = project(myLocation, testBearing, testDistance);
				}
				double maxTurn = Math.PI / 18 - Math.PI / 240 * Math.abs(vel);
				double smoothAngle = absoluteBearing(eloc, testPoint);
				double wantTurn = Utils.normalRelativeAngle(smoothAngle - angle);

				angle = limit(angle - maxTurn, angle + wantTurn, angle + maxTurn);
				if (vel < 0) {
					vel += 2;
				} else {
					vel = Math.min(8, vel + 1);
				}

				Point2D.Double nextLoc = project(eloc, angle, vel);

				if (nextLoc.x > WIDTH - MARGIN || nextLoc.x < MARGIN || nextLoc.y > HEIGHT - MARGIN
						|| nextLoc.y < MARGIN) {
					break;
				}
				eloc = nextLoc;
				pointsList.add(eloc);
				bulletDistance += bulletVelocity;
			}
			locs[(i + 1) / 2] = eloc;
		}
		return locs;
	}

	static Point2D.Double[] getDirectEndPoints(
			Point2D.Double enemyLocation,
			double enemyHeading,
			double enemyVelocity,
			Point2D.Double myLocation,
			double bulletPower,
			ArrayList<Point2D.Double> prevLocs,
			ArrayList<Point2D.Double> pointsList) {
		Point2D.Double[] locs = new Point2D.Double[2];
		double bulletVelocity = 20 - 3 * bulletPower;

		for (int i = -1; i < 2; i += 2) {
			double angle = enemyHeading + (Math.PI / 2) * (i - 1);
			double vel = i * enemyVelocity;
			Point2D.Double eloc = enemyLocation;
			double bulletDistance = 0;
			double goalAngle = absoluteBearing(eloc, prevLocs.get((i + 1) / 2));

			// if(FastTrig.cos(goalAngle - angle) < 0)
			// goalAngle += Math.PI;
			while (bulletDistance < eloc.distance(myLocation)) {
				Point2D.Double testPoint = project(eloc, goalAngle, 90);
				double testBearing = absoluteBearing(myLocation, testPoint);
				double testDistance = testPoint.distance(myLocation);

				while (MARGIN > testPoint.x || testPoint.x > WIDTH - MARGIN || MARGIN > testPoint.y
						|| testPoint.y > HEIGHT - MARGIN) {
					testDistance *= 0.95;
					testPoint = project(myLocation, testBearing, testDistance);
				}
				double maxTurn = Math.PI / 18 - Math.PI / 240 * Math.abs(vel);
				double smoothAngle = absoluteBearing(eloc, testPoint);
				double wantTurn = Utils.normalRelativeAngle(smoothAngle - angle);

				angle = limit(angle - maxTurn, angle + wantTurn, angle + maxTurn);
				if (vel < 0) {
					vel += 2;
				} else {
					vel = Math.min(8, vel + 1);
				}

				Point2D.Double nextLoc = project(eloc, angle, vel);

				if (nextLoc.x > WIDTH - MARGIN || nextLoc.x < MARGIN || nextLoc.y > HEIGHT - MARGIN
						|| nextLoc.y < MARGIN) {
					break;
				}
				eloc = nextLoc;
				pointsList.add(eloc);
				bulletDistance += bulletVelocity;
			}
			locs[(i + 1) / 2] = eloc;
		}
		return locs;
	}

	static Point2D.Double[] getStraightEndPoints(
			Point2D.Double enemyLocation,
			double enemyHeading,
			double enemyVelocity,
			Point2D.Double myLocation,
			double bulletPower,
			ArrayList<Point2D.Double> pointsList) {
		Point2D.Double[] locs = new Point2D.Double[2];
		double bulletVelocity = 20 - 3 * bulletPower;

		for (int i = -1; i < 2; i += 2) {
			double angle = enemyHeading + (Math.PI / 2) * (i - 1);
			double vel = i * enemyVelocity;
			Point2D.Double eloc = enemyLocation;
			double bulletDistance = 0;
			double goalAngle = absoluteBearing(myLocation, eloc) + Math.PI / 2;

			if (Math.cos(goalAngle - angle) < 0) {
				goalAngle += Math.PI;
			}
			while (bulletDistance < eloc.distance(myLocation)) {
				double maxTurn = Math.PI / 18 - Math.PI / 240 * Math.abs(vel);
				double wantTurn = Utils.normalRelativeAngle(goalAngle - angle);

				angle = limit(angle - maxTurn, angle + wantTurn, angle + maxTurn);
				if (vel < 0) {
					vel += 2;
				} else {
					vel = Math.min(8, vel + 1);
				}

				Point2D.Double nextLoc = project(eloc, angle, vel);

				if (nextLoc.x > WIDTH - MARGIN || nextLoc.x < MARGIN || nextLoc.y > HEIGHT - MARGIN
						|| nextLoc.y < MARGIN) {
					break;
				}
				eloc = nextLoc;
				pointsList.add(eloc);
				bulletDistance += bulletVelocity;
			}
			locs[(i + 1) / 2] = eloc;
		}
		return locs;
	}

	static Point2D.Double project(Point2D.Double p, double angle, double distance) {
		return new Point2D.Double(p.x + distance * Math.sin(angle), p.y + distance * Math.cos(angle));
	}

	public static double absoluteBearing(Point2D.Double source, Point2D.Double target) {
		return Math.atan2(target.x - source.x, target.y - source.y);
	}

	public static double limit(double min, double value, double max) {
		if (value > max) {
			return max;
		}
		if (value < min) {
			return min;
		}

		return value;
	}
}
