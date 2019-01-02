/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host;


import net.sf.robocode.peer.IRobotStatics;
import net.sf.robocode.repository.IRobotItem;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.serialization.ISerializableHelper;
import net.sf.robocode.serialization.RbSerializer;
import robocode.BattleRules;
import robocode.control.RobotSpecification;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Pavel Savara (original)
 */
public final class RobotStatics implements IRobotStatics, Serializable {
	private static final long serialVersionUID = 1L;

	private final String robocodeVersion;

	private final boolean isJuniorRobot;
	private final boolean isAdvancedRobot;
	private final boolean isTeamRobot;
	private final boolean isDroid;
	private final boolean isSentryRobot;

	private final boolean isInteractiveRobot;
	private final boolean isPaintRobot;

	private final boolean isTeamLeader;

	private final String name;
	private final String shortName;
	private final String veryShortName;

	private final String fullClassName;
	private final String shortClassName;

	private final BattleRules battleRules;

	private final String[] teammates;
	private final String teamName;

	private final int robotIndex;
	private final int teamIndex;

	public RobotStatics(RobotSpecification robotSpecification, int duplicate, boolean isLeader, BattleRules rules, String teamName, List<String> teamMembers, int robotIndex, int teamIndex) {
		IRobotItem robotItem = ((IRobotItem) HiddenAccess.getFileSpecification(robotSpecification));

		this.robotIndex = robotIndex;
		this.teamIndex = teamIndex;

		shortClassName = robotItem.getShortClassName();
		fullClassName = robotItem.getFullClassName();
		if (duplicate >= 0) {
			String countString = " (" + (duplicate + 1) + ')';

			name = robotItem.getUniqueFullClassNameWithVersion() + countString;
			shortName = robotItem.getUniqueShortClassNameWithVersion() + countString;
			veryShortName = robotItem.getUniqueVeryShortClassNameWithVersion() + countString;
		} else {
			name = robotItem.getUniqueFullClassNameWithVersion();
			shortName = robotItem.getUniqueShortClassNameWithVersion();
			veryShortName = robotItem.getUniqueVeryShortClassNameWithVersion();
		}
		this.robocodeVersion = robotItem.getRobocodeVersion();
		this.isJuniorRobot = robotItem.isJuniorRobot();
		this.isAdvancedRobot = robotItem.isAdvancedRobot();
		this.isTeamRobot = robotItem.isTeamRobot();
		this.isDroid = robotItem.isDroid();
		this.isSentryRobot = robotItem.isSentryRobot();
		this.isInteractiveRobot = robotItem.isInteractiveRobot();
		this.isPaintRobot = robotItem.isPaintRobot();
		this.isTeamLeader = isLeader;
		this.battleRules = rules;

		if (teamMembers != null) {
			List<String> list = new ArrayList<String>();

			for (String mate : teamMembers) {
				if (!name.equals(mate)) {
					list.add(mate);
				}
			}
			teammates = list.toArray(new String[] {});
			this.teamName = teamName;
		} else {
			teammates = null;
			this.teamName = name;
		}
	}

	private RobotStatics(String robocodeVersion, boolean isJuniorRobot, boolean isInteractiveRobot, boolean isPaintRobot, boolean isAdvancedRobot,
			boolean isTeamRobot, boolean isTeamLeader, boolean isDroid, boolean isSentryRobot, String name, String shortName,
			String veryShortName, String fullClassName, String shortClassName, BattleRules battleRules,
			String[] teammates, String teamName, int robotIndex, int teamIndex) {

		this.robocodeVersion = robocodeVersion;
		this.isJuniorRobot = isJuniorRobot;
		this.isAdvancedRobot = isAdvancedRobot;
		this.isTeamRobot = isTeamRobot;
		this.isDroid = isDroid;
		this.isSentryRobot = isSentryRobot;
		this.isInteractiveRobot = isInteractiveRobot;
		this.isPaintRobot = isPaintRobot;
		this.isTeamLeader = isTeamLeader;
		this.name = name;
		this.shortName = shortName;
		this.veryShortName = veryShortName;
		this.fullClassName = fullClassName;
		this.shortClassName = shortClassName;
		this.battleRules = battleRules;
		this.teammates = teammates;
		this.teamName = teamName;
		this.robotIndex = robotIndex;
		this.teamIndex = teamIndex;
	}

	public String getRobocodeVersion() {
		return robocodeVersion;
	}

	public String getAnnonymousName() {
		return "#" + robotIndex;
	}

	public boolean isJuniorRobot() {
		return isJuniorRobot;
	}

	public boolean isAdvancedRobot() {
		return isAdvancedRobot;
	}

	public boolean isTeamRobot() {
		return isTeamRobot;
	}

	public boolean isDroid() {
		return isDroid;
	}

	public boolean isSentryRobot() {
		return isSentryRobot;
	}

	public boolean isInteractiveRobot() {
		return isInteractiveRobot;
	}

	public boolean isPaintRobot() {
		return isPaintRobot;
	}

	public boolean isTeamLeader() {
		return isTeamLeader;
	}

	public String getName() {
		return name;
	}

	public String getShortName() {
		return shortName;
	}

	public String getVeryShortName() {
		return veryShortName;
	}

	public String getFullClassName() {
		return fullClassName;
	}

	public String getShortClassName() {
		return shortClassName;
	}

	public BattleRules getBattleRules() {
		return battleRules;
	}

	public String[] getTeammates() {
		return teammates == null ? null : teammates.clone();
	}

	public String getTeamName() {
		return teamName;
	}

	public int getRobotIndex() {
		return robotIndex;
	}
	
	public int getTeamIndex() {
		return teamIndex;
	}

	static ISerializableHelper createHiddenSerializer() {
		return new SerializableHelper();
	}

	private static class SerializableHelper implements ISerializableHelper {
		public int sizeOf(RbSerializer serializer, Object object) {
			RobotStatics obj = (RobotStatics) object;
			int size = RbSerializer.SIZEOF_TYPEINFO + serializer.sizeOf(obj.robocodeVersion)
					+ RbSerializer.SIZEOF_BOOL * 9 + serializer.sizeOf(obj.name) + serializer.sizeOf(obj.shortName)
					+ serializer.sizeOf(obj.veryShortName) + serializer.sizeOf(obj.fullClassName)
					+ serializer.sizeOf(obj.shortClassName) + RbSerializer.SIZEOF_INT * 6 + RbSerializer.SIZEOF_DOUBLE
					+ RbSerializer.SIZEOF_LONG;

			if (obj.teammates != null) {
				for (String mate : obj.teammates) {
					size += serializer.sizeOf(mate);
				}
			}
			size += RbSerializer.SIZEOF_INT;
			size += serializer.sizeOf(obj.teamName);

			return size;
		}

		public void serialize(RbSerializer serializer, ByteBuffer buffer, Object object) {
			RobotStatics obj = (RobotStatics) object;

			serializer.serialize(buffer, obj.robocodeVersion);
			serializer.serialize(buffer, obj.isJuniorRobot);
			serializer.serialize(buffer, obj.isInteractiveRobot);
			serializer.serialize(buffer, obj.isPaintRobot);
			serializer.serialize(buffer, obj.isAdvancedRobot);
			serializer.serialize(buffer, obj.isTeamRobot);
			serializer.serialize(buffer, obj.isTeamLeader);
			serializer.serialize(buffer, obj.isDroid);
			serializer.serialize(buffer, obj.isSentryRobot);
			serializer.serialize(buffer, obj.name);
			serializer.serialize(buffer, obj.shortName);
			serializer.serialize(buffer, obj.veryShortName);
			serializer.serialize(buffer, obj.fullClassName);
			serializer.serialize(buffer, obj.shortClassName);
			serializer.serialize(buffer, obj.battleRules.getBattlefieldWidth());
			serializer.serialize(buffer, obj.battleRules.getBattlefieldHeight());
			serializer.serialize(buffer, obj.battleRules.getNumRounds());
			serializer.serialize(buffer, obj.battleRules.getGunCoolingRate());
			serializer.serialize(buffer, obj.battleRules.getInactivityTime());
			serializer.serialize(buffer, obj.battleRules.getHideEnemyNames());
			serializer.serialize(buffer, obj.battleRules.getSentryBorderSize());
			if (obj.teammates != null) {
				for (String mate : obj.teammates) {
					serializer.serialize(buffer, mate);
				}
			}
			buffer.putInt(-1);
			serializer.serialize(buffer, obj.teamName);
			serializer.serialize(buffer, obj.robotIndex);
			serializer.serialize(buffer, obj.teamIndex);
		}

		public Object deserialize(RbSerializer serializer, ByteBuffer buffer) {

			String robocodeVersion = serializer.deserializeString(buffer);
			boolean isJuniorRobot = serializer.deserializeBoolean(buffer);
			boolean isInteractiveRobot = serializer.deserializeBoolean(buffer);
			boolean isPaintRobot = serializer.deserializeBoolean(buffer);
			boolean isAdvancedRobot = serializer.deserializeBoolean(buffer);
			boolean isTeamRobot = serializer.deserializeBoolean(buffer);
			boolean isTeamLeader = serializer.deserializeBoolean(buffer);
			boolean isDroid = serializer.deserializeBoolean(buffer);
			boolean isSentryRobot = serializer.deserializeBoolean(buffer);
			String name = serializer.deserializeString(buffer);
			String shortName = serializer.deserializeString(buffer);
			String veryShortName = serializer.deserializeString(buffer);
			String fullClassName = serializer.deserializeString(buffer);
			String shortClassName = serializer.deserializeString(buffer);

			BattleRules battleRules = HiddenAccess.createRules(serializer.deserializeInt(buffer), // battleFieldWidth
					serializer.deserializeInt(buffer), // battleFieldHeight
					serializer.deserializeInt(buffer), // numOfRounds
					serializer.deserializeDouble(buffer), // gunCoolingRate
					serializer.deserializeLong(buffer), // inactivityTime
					serializer.deserializeBoolean(buffer), // hideEnemyNames
					serializer.deserializeInt(buffer)); // sentryBorderSize

			List<String> teammates = new ArrayList<String>();
			Object item = serializer.deserializeString(buffer);

			if (item == null) {
				teammates = null;
			}
			while (item != null) {
				if (item instanceof String) {
					teammates.add((String) item);
				}
				item = serializer.deserializeString(buffer);
			}

			String teamName = serializer.deserializeString(buffer);
			int index = serializer.deserializeInt(buffer);
			int contestantIndex = serializer.deserializeInt(buffer);

			return new RobotStatics(robocodeVersion, isJuniorRobot, isInteractiveRobot, isPaintRobot, isAdvancedRobot,
					isTeamRobot, isTeamLeader, isDroid, isSentryRobot, name, shortName, veryShortName, fullClassName,
					shortClassName, battleRules, teammates.toArray(new String[teammates.size()]), teamName, index,
					contestantIndex);
		}
	}

}
