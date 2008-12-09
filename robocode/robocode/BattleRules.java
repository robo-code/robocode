/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode;


import robocode.battle.BattleProperties;
import robocode.battlefield.BattleField;
import robocode.battlefield.DefaultBattleField;
import robocode.common.IXmlSerializable;
import robocode.common.XmlReader;
import robocode.common.XmlWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Dictionary;


/**
 * @author Pavel Savara (original)
 *         Immutable rules
 */
public final class BattleRules implements Serializable, IXmlSerializable {
	private static final long serialVersionUID = 1L;

	private int battlefieldWidth;
	private int battlefieldHeight;
	private int numRounds;
	private double gunCoolingRate;
	private long inactivityTime;

	public BattleRules(BattleProperties source) {
		this.battlefieldWidth = source.getBattlefieldWidth();
		this.battlefieldHeight = source.getBattlefieldHeight();
		this.numRounds = source.getNumRounds();
		this.gunCoolingRate = source.getGunCoolingRate();
		this.inactivityTime = source.getInactivityTime();
	}

	/**
	 * Gets the battlefieldWidth.
	 *
	 * @return Returns a int
	 */
	public int getBattlefieldWidth() {
		return battlefieldWidth;
	}

	/**
	 * Gets the battlefieldHeight.
	 *
	 * @return Returns a int
	 */
	public int getBattlefieldHeight() {
		return battlefieldHeight;
	}

	/**
	 * Gets the numRounds.
	 *
	 * @return Returns a int
	 */
	public int getNumRounds() {
		return numRounds;
	}

	/**
	 * Gets the gunCoolingRate.
	 *
	 * @return Returns a double
	 */
	public double getGunCoolingRate() {
		return gunCoolingRate;
	}

	/**
	 * Gets the inactivityTime.
	 *
	 * @return Returns a int
	 */
	public long getInactivityTime() {
		return inactivityTime;
	}

	public BattleField getBattleField() {
		return new DefaultBattleField(battlefieldWidth, battlefieldHeight);
	}

	public BattleRules() {}

	public void writeXml(XmlWriter writer, Dictionary<String, Object> options) throws IOException {
		writer.startElement("rules"); {
			writer.writeAttribute("battlefieldWidth", battlefieldWidth);
			writer.writeAttribute("battlefieldHeight", battlefieldHeight);
			writer.writeAttribute("numRounds", numRounds);
			writer.writeAttribute("gunCoolingRate", gunCoolingRate);
			writer.writeAttribute("inactivityTime", inactivityTime);
			writer.writeAttribute("ver", serialVersionUID);
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("rules", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final BattleRules rules = new BattleRules();

				reader.expect("battlefieldWidth", new XmlReader.Attribute() {
					public void read(String value) {
						rules.battlefieldWidth = Integer.parseInt(value);
					}
				});
				reader.expect("battlefieldHeight", new XmlReader.Attribute() {
					public void read(String value) {
						rules.battlefieldHeight = Integer.parseInt(value);
					}
				});

				reader.expect("numRounds", new XmlReader.Attribute() {
					public void read(String value) {
						rules.numRounds = Integer.parseInt(value);
					}
				});
				reader.expect("inactivityTime", new XmlReader.Attribute() {
					public void read(String value) {
						rules.inactivityTime = Integer.parseInt(value);
					}
				});
				reader.expect("gunCoolingRate", new XmlReader.Attribute() {
					public void read(String value) {
						rules.gunCoolingRate = Double.parseDouble(value);
					}
				});

				return rules;
			}
		});
	}
}
