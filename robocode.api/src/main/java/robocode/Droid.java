/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package robocode;


/**
 * A Droid is an interface used on a {@link TeamRobot} to create a specialized team robot,
 * i.e. a droid robot. A droid has 20 extra life/energy, but comes with the cost that a
 * droid <strong>has no scanner!</strong> This means that a droid is 100% dependent on other
 * team members to perform the scanning on their behalf and communicate the location of target
 * enemies.
 * <p>
 * A team of droids plus at least one non-droid team robot can have a crucial edge over another
 * team without droids (and the same number of robots) due to the additional 20 energy points.
 *
 * @see JuniorRobot
 * @see Robot
 * @see AdvancedRobot
 * @see TeamRobot
 * @see RateControlRobot
 * @see BorderSentry
 *
 * @author Mathew A. Nelson (original)
 */
public interface Droid {}
