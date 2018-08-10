package net.sf.robocode.security;

import robocode.Ball;

/**
 * @author Alfonso Dou Oblanca (original)
 */

public interface IHiddenBallHelper {
    void update(Ball ball, double headingRadians, double x, double y, double velocity);
}
