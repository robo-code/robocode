/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.peer.DebugProperty;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.IScoreSnapshot;
import robocode.control.snapshot.RobotState;

import java.awt.geom.Arc2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * A snapshot of a robot at a specific time instant in a battle.
 * The snapshot contains a snapshot of the robot data at that specific time.
 *
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (contributor)
 *
 * @since 1.6.1
 */
public final class RobotSnapshot implements Serializable, IXmlSerializable, IRobotSnapshot {

	private static final long serialVersionUID = 2L;

	/** The name of the robot */
	private String name;

	/** The short name of the robot */
	private String shortName;

	/** Very short name of the robot */
	private String veryShortName;

	/** Very short name of the team leader robot (might be null) */
	private String teamName;

	/** The unique robot index */
	private int robotIndex;

	/** The team index for the robot */
	private int teamIndex;

	/** The robot state */
	private RobotState state;

	/** The energy level of the robot */
	private double energy;

	/** The velocity of the robot */
	private double velocity;

	/** The gun heat level of the robot */
	private double gunHeat;

	/** The body heading in radians */
	private double bodyHeading;

	/** The gun heading in radians */
	private double gunHeading;

	/** The radar heading in radians */
	private double radarHeading;

	/** The X position */
	private double x;

	/** The Y position */
	private double y;

	/** The ARGB color of the body */
	private int bodyColor = ExecCommands.defaultBodyColor;

	/** The ARGB color of the gun */
	private int gunColor = ExecCommands.defaultGunColor;

	/** The ARGB color of the radar */
	private int radarColor = ExecCommands.defaultRadarColor;

	/** The ARGB color of the scan arc */
	private int scanColor = ExecCommands.defaultScanColor;

	/** Flag specifying if this robot is a Droid */
	private boolean isDroid;

	/** Flag specifying if this robot is a SentryRobot */
	private boolean isSentryRobot;
	
	/** Flag specifying if this robot is a IPaintRobot or is invoking getGraphics() */
	private boolean isPaintRobot;

	/** Flag specifying if painting is enabled for this robot */
	private boolean isPaintEnabled;

	/** Flag specifying if RobocodeSG painting is enabled for this robot */
	private boolean isSGPaintEnabled;

	/** Snapshot of the scan arc */
	private SerializableArc scanArc;

	/** Snapshot of the object with queued calls for Graphics object */
	private Object graphicsCalls;

	/** Snapshot of debug properties */
	private DebugProperty[] debugProperties;

	/** Snapshot of the output print stream for this robot */
	private String outputStreamSnapshot;

	/** Snapshot of score of the robot */
	private IScoreSnapshot robotScoreSnapshot;

	/**
	 * Creates a snapshot of a robot that must be filled out with data later.
	 */
	public RobotSnapshot() {
		state = RobotState.ACTIVE;
	}

	/**
	 * Creates a snapshot of a robot.
	 *
	 * @param robot the robot to make a snapshot of.
	 * @param readoutText {@code true} if the output text from the robot must be included in the snapshot;
	 *                    {@code false} otherwise.
	 */
	RobotSnapshot(RobotPeer robot, boolean readoutText) {
		name = robot.getName();
		shortName = robot.getShortName();
		veryShortName = robot.getVeryShortName();
		teamName = robot.getTeamName();

		robotIndex = robot.getRobotIndex();
		teamIndex = robot.getTeamIndex();

		state = robot.getState();

		energy = robot.getEnergy();
		velocity = robot.getVelocity();
		gunHeat = robot.getGunHeat(); 

		bodyHeading = robot.getBodyHeading();
		gunHeading = robot.getGunHeading();
		radarHeading = robot.getRadarHeading();

		x = robot.getX();
		y = robot.getY();

		bodyColor = robot.getBodyColor();
		gunColor = robot.getGunColor();
		radarColor = robot.getRadarColor();
		scanColor = robot.getScanColor();

		isDroid = robot.isDroid();
		isPaintRobot = robot.isPaintRobot() || robot.isTryingToPaint();
		isPaintEnabled = robot.isPaintEnabled();
		isSGPaintEnabled = robot.isSGPaintEnabled();

		scanArc = robot.getScanArc() != null ? new SerializableArc((Arc2D.Double) robot.getScanArc()) : null;

		graphicsCalls = robot.getGraphicsCalls();

		final List<DebugProperty> dp = robot.getDebugProperties();

		debugProperties = dp != null ? dp.toArray(new DebugProperty[dp.size()]) : null;

		if (readoutText) {
			outputStreamSnapshot = robot.readOutText();
		}

		robotScoreSnapshot = new ScoreSnapshot(robot.getName(), robot.getRobotStatistics());
	}

	@Override
	public String toString() {
		return shortName + " (" + (int) energy + ") X" + (int) x + " Y" + (int) y + " " + state.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	// Used to identify buttons
	// TODO: Fix this so that getRobotIndex() is used instead
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	// Used for text on buttons
	public String getShortName() {
		return shortName;
	}

	/**
	 * {@inheritDoc}
	 */
	// Used for drawing the name of the robot on the battle view
	public String getVeryShortName() {
		return veryShortName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getContestantIndex() {
		return teamIndex >= 0 ? teamIndex : robotIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRobotIndex() {
		return robotIndex;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getTeamIndex() {
		return teamIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public RobotState getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getEnergy() {
		return energy;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getVelocity() {
		return velocity;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getBodyHeading() {
		return bodyHeading;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getGunHeading() {
		return gunHeading;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getRadarHeading() {
		return radarHeading;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getGunHeat() {
		return gunHeat;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getX() {
		return x;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getY() {
		return y;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getBodyColor() {
		return bodyColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getGunColor() {
		return gunColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getRadarColor() {
		return radarColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getScanColor() {
		return scanColor;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDroid() {
		return isDroid;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSentryRobot() {
		return isSentryRobot;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPaintRobot() {
		return isPaintRobot;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPaintEnabled() {
		return isPaintEnabled;
	}

	/**
	 * Sets the flag specifying if painting is enabled for the robot.
	 *
	 * @param isPaintEnabled {@code true} if painting must be enabled;
	 *                       {@code false} otherwise.
	 */
	public void setPaintEnabled(boolean isPaintEnabled) {
		this.isPaintEnabled = isPaintEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSGPaintEnabled() {
		return isSGPaintEnabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public DebugProperty[] getDebugProperties() {
		return debugProperties;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getOutputStreamSnapshot() {
		return outputStreamSnapshot;
	}

	/**
	 * Sets the snapshot of the output print stream for this robot.
	 *
	 * @param outputStreamSnapshot new output print stream snapshot.
	 */
	public void setOutputStreamSnapshot(String outputStreamSnapshot) {
		this.outputStreamSnapshot = outputStreamSnapshot;
	}

	void stripDetails(SerializableOptions options) {
		if (options.skipDebug) {
			graphicsCalls = null;
			debugProperties = null;
			outputStreamSnapshot = null;
			isPaintEnabled = false;
			isSGPaintEnabled = false;
		}
		if (options.skipNames) {
			name = veryShortName;
			shortName = veryShortName;
			teamName = veryShortName;
		}
	}
    
	/**
	 * {@inheritDoc}
	 */
	public IScoreSnapshot getScoreSnapshot() {
		return robotScoreSnapshot;
	}

	/**
	 * Returns the scan arc snapshot for the robot.
	 *
	 * @return the scan arc snapshot for the robot.
	 */
	public Arc2D getScanArc() {
		return scanArc != null ? scanArc.create() : null;
	}

	/**
	 * Returns the object with queued calls for Graphics object.
	 *
	 * @return the object with queued calls for Graphics object.
	 */
	public Object getGraphicsCalls() {
		return graphicsCalls;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		writer.startElement(options.shortAttributes ? "r" : "robot"); {
			writer.writeAttribute("id", robotIndex);
			if (!options.skipNames) {
				writer.writeAttribute("vsName", veryShortName);
			}
			if (!options.skipExploded || state != RobotState.ACTIVE) {
				writer.writeAttribute(options.shortAttributes ? "s" : "state", state.toString());
			}
			writer.writeAttribute(options.shortAttributes ? "e" : "energy", energy, options.trimPrecision);
			writer.writeAttribute("x", x, options.trimPrecision);
			writer.writeAttribute("y", y, options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "b" : "bodyHeading", bodyHeading, options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "g" : "gunHeading", gunHeading, options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "r" : "radarHeading", radarHeading, options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "h" : "gunHeat", gunHeat, options.trimPrecision);
			writer.writeAttribute(options.shortAttributes ? "v" : "velocity", velocity, options.trimPrecision);
			if (!options.skipNames) {
				writer.writeAttribute("teamName", teamName);
				writer.writeAttribute("name", name);
				writer.writeAttribute("sName", shortName);
				if (isDroid) {
					writer.writeAttribute("isDroid", true);
				}
				if (bodyColor != ExecCommands.defaultBodyColor) {
					writer.writeAttribute("bodyColor", Integer.toHexString(bodyColor).toUpperCase());
				}
				if (gunColor != ExecCommands.defaultGunColor) {
					writer.writeAttribute("gunColor", Integer.toHexString(gunColor).toUpperCase());
				}
				if (radarColor != ExecCommands.defaultRadarColor) {
					writer.writeAttribute("radarColor", Integer.toHexString(radarColor).toUpperCase());
				}
				if (scanColor != ExecCommands.defaultScanColor) {
					writer.writeAttribute("scanColor", Integer.toHexString(scanColor).toUpperCase());
				}
			}
			if (!options.skipVersion) {
				writer.writeAttribute("ver", serialVersionUID);
			}
			if (!options.skipDebug) {
				if (outputStreamSnapshot != null && outputStreamSnapshot.length() != 0) {
					writer.writeAttribute("out", outputStreamSnapshot);
				}
				if (debugProperties != null) {
					writer.startElement("debugProperties"); {
						for (DebugProperty prop : debugProperties) {
							prop.writeXml(writer, options);
						}
					}
					writer.endElement();
				}
			}

			((ScoreSnapshot) robotScoreSnapshot).writeXml(writer, options);
		}
		writer.endElement();

	}

	// allows loading of minimalistic XML
	RobotSnapshot(String robotName, int robotIndex, RobotState state) {
		this.robotIndex = robotIndex;
		this.state = state;
		this.name = robotName;
		this.teamName = robotName;
		this.shortName = robotName;
		this.veryShortName = robotName;
		this.robotScoreSnapshot = new ScoreSnapshot(robotName);
	}

	/**
	 * {@inheritDoc}
	 */
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("robot", "r", new XmlReader.Element() {
			public IXmlSerializable read(final XmlReader reader) {
				final RobotSnapshot snapshot = new RobotSnapshot();

				reader.expect("id", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.robotIndex = Integer.parseInt(value);

						// allows loading of minimalistic XML, which robot names for subsequent turns
						Map<String, Object> context = reader.getContext();

						if (context.containsKey(value)) {
							String n = (String) context.get(value);

							if (snapshot.shortName == null) {
								snapshot.name = n;
							}
							if (snapshot.shortName == null) {
								snapshot.shortName = n;
							}
							if (snapshot.teamName == null) {
								snapshot.teamName = n;
							}
							if (snapshot.veryShortName == null) {
								snapshot.veryShortName = n;
							}
						}
					}
				});

				reader.expect("name", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.name = value;
						Map<String, Object> context = reader.getContext();

						context.put(Integer.toString(snapshot.robotIndex), value);
						if (snapshot.shortName == null) {
							snapshot.shortName = value;
						}
						if (snapshot.teamName == null) {
							snapshot.teamName = value;
						}
						if (snapshot.veryShortName == null) {
							snapshot.veryShortName = value;
						}
					}
				});

				reader.expect("sName", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.shortName = value;
					}
				});

				reader.expect("vsName", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.veryShortName = value;
					}
				});

				reader.expect("teamName", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.teamName = value;
					}
				});

				reader.expect("state", "s", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.state = RobotState.valueOf(value);
					}
				});

				reader.expect("isDroid", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.isDroid = Boolean.valueOf(value);
					}
				});

				reader.expect("bodyColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.bodyColor = (Long.valueOf(value.toUpperCase(), 16).intValue());
					}
				});

				reader.expect("gunColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunColor = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("radarColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.radarColor = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("scanColor", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.scanColor = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("energy", "e", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.energy = Double.parseDouble(value);
					}
				});

				reader.expect("velocity", "v", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.velocity = Double.parseDouble(value);
					}
				});

				reader.expect("gunHeat", "h", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunHeat = Double.parseDouble(value);
					}
				});

				reader.expect("bodyHeading", "b", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.bodyHeading = Double.parseDouble(value);
					}
				});

				reader.expect("gunHeading", "g", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.gunHeading = Double.parseDouble(value);
					}
				});

				reader.expect("radarHeading", "r", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.radarHeading = Double.parseDouble(value);
					}
				});

				reader.expect("x", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.x = Double.parseDouble(value);
					}
				});

				reader.expect("y", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.y = Double.parseDouble(value);
					}
				});

				reader.expect("out", new XmlReader.Attribute() {
					public void read(String value) {
						if (value != null && value.length() != 0) {
							snapshot.outputStreamSnapshot = value;
						}
					}
				});

				final XmlReader.Element element = (new ScoreSnapshot()).readXml(reader);

				reader.expect("score", "sc", new XmlReader.Element() {
					public IXmlSerializable read(XmlReader reader) {
						snapshot.robotScoreSnapshot = (IScoreSnapshot) element.read(reader);
						return (ScoreSnapshot) snapshot.robotScoreSnapshot;
					}
				});

				return snapshot;
			}
		});
	}

	/**
	 * Class used for serializing an Arc2D.double.
	 * The purpose of this class is to overcome various serialization problems with Arc2D to cope with bug in Java 6:
	 * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6522514">Bug ID: 6522514</a>.
	 *
	 * @author Pavel Savara
	 */
	private static class SerializableArc implements Serializable {
		private static final long serialVersionUID = 1L;

		public final double x;
		public final double y;
		public final double w;
		public final double h;
		public final double start;
		public final double extent;
		public final int type;

		public SerializableArc(Arc2D.Double arc) {
			x = arc.getX();
			y = arc.getY();
			w = arc.getWidth();
			h = arc.getHeight();
			start = arc.getAngleStart();
			extent = arc.getAngleExtent();
			type = arc.getArcType();
		}

		public Arc2D create() {
			return new Arc2D.Double(x, y, w, h, start, extent, type);
		}
	}
}
