package net.sf.robocode.battle.snapshot;

import java.io.IOException;

import net.sf.robocode.battle.peer.MinePeer;
import net.sf.robocode.battle.peer.RobotPeer;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.IMineSnapshot;
import robocode.control.snapshot.MineState;

/**
 * A snapshot of a mine at a specific time instant in a battle.
 * The snapshot contains a snapshot of the mine data at that specific time.
 * Basically a copy-paste of BulletSnapshot,
 * which is why I added Flemming and Pavel in the authorlist.
 *
 * @author Thales B.V. / Thomas Hakkers(original)
 * @author Flemming N. Larsen (contributor)
 * @author Pavel Savara (contributor)
 *
 * @since 1.9.2.2
 */
public class MineSnapshot implements java.io.Serializable, IXmlSerializable, IMineSnapshot{

	private static final long serialVersionUID = 2L;

	/** The mine state */
	private MineState state;

	/** The mine power */
	private double power;

	/** The x position */
	private double x;

	/** The y position */
	private double y;

	/** The ARGB color of the bullet */
	private int color = ExecCommands.defaultBulletColor;

	/** The current frame number to display, i.e. when the mine explodes */
	private int frame;

	/** Flag specifying if this mine has turned into an explosion */
	private boolean isExplosion;

	/** Index to which explosion image that must be rendered */
	private int explosionImageIndex;

	private int mineId;

	private int victimIndex = -1;
    
	private int ownerIndex;

	/**
	 * Creates a snapshot of a mine that must be filled out with data later.
	 */
	public MineSnapshot() {
		state = MineState.INACTIVE;
		ownerIndex = -1;
		victimIndex = -1;
		explosionImageIndex = -1;
		power = Double.NaN;
	}

	/**
	 * Creates a snapshot of a mine.
	 *
	 * @param mine the mine to make a snapshot of.
	 */
	MineSnapshot(MinePeer mine) {
		state = mine.getState();

		power = mine.getPower();

		x = mine.getX();
		y = mine.getY();

		color = mine.getColor();

		frame = mine.getFrame();

		explosionImageIndex = mine.getExplosionImageIndex();

		mineId = mine.getMineId();

		final RobotPeer victim = mine.getVictim();

		if (victim != null) {
			victimIndex = victim.getRobotIndex();
		}

		ownerIndex = mine.getOwner().getRobotIndex();
	}

	@Override
	public String toString() {
		return ownerIndex + "-" + mineId + " (" + (int) power + ") X" + (int) x + " Y" + (int) y + " "
				+ state.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public MineState getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getPower() {
		return power;
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
	public int getColor() {
		return color;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getFrame() {
		return frame;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isExplosion() {
		return isExplosion;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getExplosionImageIndex() {
		return explosionImageIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getVictimIndex() {
		return victimIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getOwnerIndex() {
		return ownerIndex;
	}

	@Override
	public int getMineId() {
		return mineId;
	}
	
	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		writer.startElement(options.shortAttributes ? "m" : "mine"); {
			writer.writeAttribute("id", ownerIndex + "-" + mineId);
			if (!options.skipExploded) {
				writer.writeAttribute(options.shortAttributes ? "s" : "state", state.toString());
				writer.writeAttribute(options.shortAttributes ? "p" : "power", power, options.trimPrecision);
			}
			if (state == MineState.HIT_VICTIM) {
				writer.writeAttribute(options.shortAttributes ? "v" : "victim", victimIndex);
			}
			if (state == MineState.PLACED) {
				writer.writeAttribute(options.shortAttributes ? "o" : "owner", ownerIndex);
			}
			writer.writeAttribute("x", x, options.trimPrecision);
			writer.writeAttribute("y", y, options.trimPrecision);
			if (!options.skipNames) {
				if (color != ExecCommands.defaultBulletColor) {
					writer.writeAttribute(options.shortAttributes ? "c" : "color",
							Integer.toHexString(color).toUpperCase());
				}
			}
			if (!options.skipExploded) {
				if (frame != 0) {
					writer.writeAttribute("frame", frame);
				}
				if (isExplosion) {
					writer.writeAttribute("isExplosion", true);
					writer.writeAttribute("explosion", explosionImageIndex);
				}
			}
			if (!options.skipVersion) {
				writer.writeAttribute("ver", serialVersionUID);
			}
		}
		writer.endElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("mine", "m", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final MineSnapshot snapshot = new MineSnapshot();

				reader.expect("id", new XmlReader.Attribute() {
					public void read(String value) {
						String[] parts = value.split("-");

						snapshot.ownerIndex = Integer.parseInt(parts[0]);
						snapshot.mineId = Integer.parseInt(parts[1]);
					}
				});

				reader.expect("state", "s", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.state = MineState.valueOf(value);
					}
				});

				reader.expect("power", "p", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.power = Double.parseDouble(value);
					}
				});

				reader.expect("victim", "v", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.victimIndex = Integer.parseInt(value);
					}
				});

				reader.expect("owner", "o", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.ownerIndex = Integer.parseInt(value);
					}
				});

				reader.expect("x", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.x = Double.parseDouble(value);
						snapshot.x = snapshot.x;
					}
				});

				reader.expect("y", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.y = Double.parseDouble(value);
						snapshot.y = snapshot.y;
					}
				});

				reader.expect("color", "c", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.color = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("isExplosion", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.isExplosion = Boolean.parseBoolean(value);
						if (snapshot.isExplosion && snapshot.state == null) {
							snapshot.state = MineState.EXPLODED;
						}
					}
				});

				reader.expect("explosion", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.explosionImageIndex = Integer.parseInt(value);
					}
				});

				reader.expect("frame", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.frame = Integer.parseInt(value);
					}
				});
				return snapshot;
			}
		});
	}
	
	
}
