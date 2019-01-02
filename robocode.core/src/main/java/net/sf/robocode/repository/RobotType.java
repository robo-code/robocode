/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.repository;


import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class RobotType implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final transient int
			NONE_FLAG = 0,
			JUNIOR_FLAG = 1,
			STANDARD_FLAG = 2,
			ADVANCED_FLAG = 4,
			TEAM_FLAG = 8,
			DROID_FLAG = 16,
			INTERACTIVE_FLAG = 32,
			PAINTING_FLAG = 64,
			SENTRY_FLAG = 128;

	public static final transient RobotType INVALID = new RobotType(NONE_FLAG);

	private int typeFlags;

	public RobotType(int typeFlags) { // NO_UCD (use private - used by .NET plug-in)
		this.typeFlags = typeFlags;
	}

	public RobotType(
			boolean isJuniorRobot,
			boolean isStandardRobot,
			boolean isInteractiveRobot,
			boolean isPaintRobot,
			boolean isAdvancedRobot,
			boolean isTeamRobot,
			boolean isDroid,
			boolean isSentryRobot) {

		typeFlags = NONE_FLAG;

		if (isJuniorRobot) {
			typeFlags |= JUNIOR_FLAG;
		}
		if (isStandardRobot) {
			typeFlags |= STANDARD_FLAG;
		}
		if (isInteractiveRobot) {
			typeFlags |= INTERACTIVE_FLAG;
		}
		if (isPaintRobot) {
			typeFlags |= PAINTING_FLAG;
		}
		if (isAdvancedRobot) {
			typeFlags |= ADVANCED_FLAG;
		}
		if (isTeamRobot) {
			typeFlags |= TEAM_FLAG;
		}
		if (isDroid) {
			typeFlags |= DROID_FLAG;
		}
		if (isSentryRobot) {
			typeFlags |= SENTRY_FLAG;
		}
	}

	public int getTypeFlags() {
		return typeFlags;
	}

	public boolean isValid() {
		return isJuniorRobot() || isStandardRobot() || isAdvancedRobot();
	}

	public boolean isJuniorRobot() {
		return (typeFlags & JUNIOR_FLAG) != 0;
	}

	public boolean isStandardRobot() {
		return (typeFlags & STANDARD_FLAG) != 0;
	}

	public boolean isInteractiveRobot() {
		return (typeFlags & INTERACTIVE_FLAG) != 0;
	}

	public boolean isPaintRobot() {
		return (typeFlags & PAINTING_FLAG) != 0;
	}

	public boolean isAdvancedRobot() {
		return (typeFlags & ADVANCED_FLAG) != 0;
	}

	public boolean isTeamRobot() {
		return (typeFlags & TEAM_FLAG) != 0;
	}

	public boolean isDroid() {
		return (typeFlags & DROID_FLAG) != 0;
	}

	public boolean isSentryRobot() {
		return (typeFlags & SENTRY_FLAG) != 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + typeFlags;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		RobotType other = (RobotType) obj;
		return (typeFlags == other.typeFlags);
	}
}
