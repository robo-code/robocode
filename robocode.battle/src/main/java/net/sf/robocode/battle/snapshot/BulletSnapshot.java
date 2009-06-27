/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial implementation
 *     Pavel Savara
 *     - Xml Serialization, refactoring
 *******************************************************************************/
package net.sf.robocode.battle.snapshot;


import net.sf.robocode.battle.peer.BulletPeer;
import net.sf.robocode.battle.peer.ExplosionPeer;
import net.sf.robocode.peer.ExecCommands;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.control.snapshot.BulletState;
import robocode.control.snapshot.IBulletSnapshot;

import java.io.IOException;
import java.util.Dictionary;


/**
 * A snapshot of a bullet at a specific time instant in a battle.
 * The snapshot contains a snapshot of the bullet data at that specific time.
 *
 * @author Flemming N. Larsen (original)
 * @author Pavel Savara (contributor)
 * @since 1.6.1
 */
public final class BulletSnapshot implements java.io.Serializable, IXmlSerializable, IBulletSnapshot {

	private static final long serialVersionUID = 1L;

	/** The bullet state */
	private BulletState state;

	/** The bullet power */
	private double power;

	/** The x position */
	private double x;

	/** The y position */
	private double y;

	/** The x painting position (due to offset on robot when bullet hits a robot) */
	private double paintX;

	/** The y painting position (due to offset on robot when bullet hits a robot) */
	private double paintY;

	/** The ARGB color of the bullet */
	private int color = ExecCommands.defaultBulletColor;

	/** The current frame number to display, i.e. when the bullet explodes */
	private int frame;

	/** Flag specifying if this bullet has turned into an explosion */
	private boolean isExplosion;

	/** Index to which explosion image that must be rendered */
	private int explosionImageIndex;

	private int bulletId;

	/**
	 * Creates a snapshot of a bullet that must be filled out with data later.
	 */
	public BulletSnapshot() {}

	/**
	 * Creates a snapshot of a bullet.
	 *
	 * @param bullet the bullet to make a snapshot of.
	 */
	public BulletSnapshot(BulletPeer bullet) {
		state = bullet.getState();

		power = bullet.getPower();

		x = bullet.getX();
		y = bullet.getY();

		paintX = bullet.getPaintX();
		paintY = bullet.getPaintY();

		color = bullet.getColor();

		frame = bullet.getFrame();

		isExplosion = (bullet instanceof ExplosionPeer);
		explosionImageIndex = bullet.getExplosionImageIndex();

		bulletId = bullet.getBulletId();
	}

	/**
	 * {@inheritDoc}
	 */
	public int getBulletId() {
		return bulletId;
	}

	/**
	 * {@inheritDoc}
	 */
	public BulletState getState() {
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
	public double getPaintX() {
		return paintX;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getPaintY() {
		return paintY;
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
	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("bullet"); {
			writer.writeAttribute("state", state.toString());
			writer.writeAttribute("power", power);
			writer.writeAttribute("x", paintX);
			writer.writeAttribute("y", paintY);
			if (color != ExecCommands.defaultBulletColor) {
				writer.writeAttribute("color", Integer.toHexString(color).toUpperCase());
			}
			if (frame != 0) {
				writer.writeAttribute("frame", frame);
			}
			if (isExplosion) {
				writer.writeAttribute("isExplosion", true);
				writer.writeAttribute("explosion", explosionImageIndex);
			}
			writer.writeAttribute("ver", serialVersionUID);
		}
		writer.endElement();
	}

	/**
	 * {@inheritDoc}
	 */
	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("bullet", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final BulletSnapshot snapshot = new BulletSnapshot();

				reader.expect("state", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.state = BulletState.valueOf(value);
					}
				});

				reader.expect("power", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.power = Double.parseDouble(value);
					}
				});

				reader.expect("x", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.x = Double.parseDouble(value);
						snapshot.paintX = snapshot.x;
					}
				});

				reader.expect("y", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.y = Double.parseDouble(value);
						snapshot.paintY = snapshot.y;
					}
				});

				reader.expect("color", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.color = Long.valueOf(value.toUpperCase(), 16).intValue();
					}
				});

				reader.expect("isExplosion", new XmlReader.Attribute() {
					public void read(String value) {
						snapshot.isExplosion = Boolean.parseBoolean(value);
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
