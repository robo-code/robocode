/**
 * ****************************************************************************
 * Copyright (c) 2001, 2009 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * <p/>
 * Contributors:
 * Pavel Savara
 * - Initial implementation
 * *****************************************************************************
 */
package net.sf.robocode.repository;


import java.io.Serializable;


public class RobotType implements Serializable {
	private static final long serialVersionUID = 1L;

	transient public static final RobotType INVALID = new RobotType(0);
	transient public static final RobotType JUNIOR = new RobotType(1);
	transient public static final RobotType STANDARD = new RobotType(2);
	transient public static final RobotType ADVANCED = new RobotType(4);
	transient public static final RobotType TEAM = new RobotType(8);
	transient public static final RobotType DROID = new RobotType(16);
	transient public static final RobotType INTERACTIVE = new RobotType(32);
	transient public static final RobotType PAINTING = new RobotType(64);

	private int code;

	public RobotType(int code) {
		this.code = code;
	}

	public RobotType(
			boolean isJuniorRobot,
			boolean isStandardRobot,
			boolean isInteractiveRobot,
			boolean isPaintRobot,
			boolean isAdvancedRobot,
			boolean isTeamRobot,
			boolean isDroid
			) {
		this.code = 0;
		if (isJuniorRobot) {
			code += JUNIOR.getCode();
		}
		if (isStandardRobot) {
			code += STANDARD.getCode();
		}
		if (isInteractiveRobot) {
			code += INTERACTIVE.getCode();
		}
		if (isPaintRobot) {
			code += PAINTING.getCode();
		}
		if (isAdvancedRobot) {
			code += ADVANCED.getCode();
		}
		if (isTeamRobot) {
			code += TEAM.getCode();
		}
		if (isDroid) {
			code += DROID.getCode();
		}
	}

	public int getCode() {
		return code;
	}

	public boolean isValid() {
		return isJuniorRobot() || isStandardRobot() || isAdvancedRobot();
	}

	public boolean isDroid() {
		return (code & DROID.code) != 0;
	}

	public boolean isTeamRobot() {
		return (code & TEAM.code) != 0;
	}

	public boolean isAdvancedRobot() {
		return (code & ADVANCED.code) != 0;
	}

	public boolean isStandardRobot() {
		return (code & STANDARD.code) != 0;
	}

	public boolean isInteractiveRobot() {
		return (code & INTERACTIVE.code) != 0;
	}

	public boolean isPaintRobot() {
		return (code & PAINTING.code) != 0;
	}

	public boolean isJuniorRobot() {
		return (code & JUNIOR.code) != 0;
	}

	public boolean equals(Object obj) {
		return obj instanceof RobotType && ((RobotType) obj).code == code;
	}
}
