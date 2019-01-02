/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.recording;


import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.security.HiddenAccess;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.BattleResults;
import robocode.BattleRules;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
class BattleRecordInfo implements Serializable, IXmlSerializable {
	private static final long serialVersionUID = 1L;

	int robotCount;
	int roundsCount;
	BattleRules battleRules;
	Integer[] turnsInRounds;
	List<BattleResults> results;

	public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		writer.startElement("recordInfo"); {
			writer.writeAttribute("robotCount", robotCount);
			writer.writeAttribute("roundsCount", roundsCount);
			if (!options.skipVersion) {
				writer.writeAttribute("ver", serialVersionUID);
			}
			writer.startElement("rules"); {
				writer.writeAttribute("battlefieldWidth", battleRules.getBattlefieldWidth());
				writer.writeAttribute("battlefieldHeight", battleRules.getBattlefieldHeight());
				writer.writeAttribute("numRounds", battleRules.getNumRounds());
				writer.writeAttribute("gunCoolingRate", battleRules.getGunCoolingRate(), options.trimPrecision);
				writer.writeAttribute("inactivityTime", battleRules.getInactivityTime());
				writer.writeAttribute("ver", serialVersionUID);
			}
			writer.endElement();

			writer.startElement("rounds"); {
				for (int n : turnsInRounds) {
					writer.startElement("turns"); {
						writer.writeAttribute("value", Integer.toString(n));
					}
					writer.endElement();
				}
			}
			writer.endElement();

			if (results != null) {
				writer.startElement("results"); {
					for (BattleResults result : results) {
						new BattleResultsWrapper(result).writeXml(writer, options);
					}
				}
				writer.endElement();
			}
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("recordInfo", new XmlReader.Element() {
			public IXmlSerializable read(XmlReader reader) {
				final BattleRecordInfo recordInfo = new BattleRecordInfo();

				reader.expect("robotCount", new XmlReader.Attribute() {
					public void read(String value) {
						recordInfo.robotCount = Integer.parseInt(value);
					}
				});
				reader.expect("roundsCount", new XmlReader.Attribute() {
					public void read(String value) {
						recordInfo.roundsCount = Integer.parseInt(value);
					}
				});

				new BattleRulesWrapper(recordInfo).readXml(reader);

				reader.expect("rounds", new XmlReader.ListElement() {
					final ArrayList<Integer> ints = new ArrayList<Integer>();

					public IXmlSerializable read(XmlReader reader) {
						// prototype
						return new IntValue("turns");
					}

					public void add(IXmlSerializable child) {
						ints.add(((IntValue) child).intValue);
					}

					public void close() {
						recordInfo.turnsInRounds = new Integer[ints.size()];
						ints.toArray(recordInfo.turnsInRounds);
					}
				});

				reader.expect("results", new XmlReader.ListElement() {

					public IXmlSerializable read(XmlReader reader) {
						recordInfo.results = new ArrayList<BattleResults>();
						// prototype
						return new BattleResultsWrapper();
					}

					public void add(IXmlSerializable child) {
						recordInfo.results.add((BattleResults) child);
					}

					public void close() {}
				});
				return recordInfo;
			}
		});
	}

	private class IntValue implements IXmlSerializable {
		private IntValue(String name) {
			this.name = name;
		}
		private final String name;
		private int intValue;

		public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {}

		public XmlReader.Element readXml(XmlReader reader) {
			return reader.expect(name, new XmlReader.Element() {
				public IXmlSerializable read(XmlReader reader) {
					final IntValue recordInfo = new IntValue(name);

					reader.expect("value", new XmlReader.Attribute() {
						public void read(String value) {
							recordInfo.intValue = Integer.parseInt(value);
						}
					});
					return recordInfo;
				}
			});
		}
	}


	/**
	 * This class is used for wrapping a robocode.BattleResults object and provides
	 * methods for XML serialization that are hidden from the BattleResults class,
	 * which is a part of the public Robot API for Robocode. 
	 *
	 * @author Flemming N. Larsen (original)
	 */
	private class BattleResultsWrapper extends BattleResults implements IXmlSerializable {

		private static final long serialVersionUID = BattleResults.serialVersionUID;

		public BattleResultsWrapper() {
			super(null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
		}

		private BattleResultsWrapper(BattleResults results) {
			super(results.getTeamLeaderName(), results.getRank(), results.getScore(), results.getSurvival(),
					results.getLastSurvivorBonus(), results.getBulletDamage(), results.getBulletDamageBonus(),
					results.getRamDamage(), results.getRamDamageBonus(), results.getFirsts(), results.getSeconds(),
					results.getThirds());
		}

		public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
			writer.startElement("result"); {
				writer.writeAttribute("teamLeaderName", teamLeaderName);
				writer.writeAttribute("rank", rank);
				writer.writeAttribute("score", score, options.trimPrecision);
				writer.writeAttribute("survival", survival, options.trimPrecision);
				writer.writeAttribute("lastSurvivorBonus", lastSurvivorBonus, options.trimPrecision);
				writer.writeAttribute("bulletDamage", bulletDamage, options.trimPrecision);
				writer.writeAttribute("bulletDamageBonus", bulletDamageBonus, options.trimPrecision);
				writer.writeAttribute("ramDamage", ramDamage, options.trimPrecision);
				writer.writeAttribute("ramDamageBonus", ramDamageBonus, options.trimPrecision);
				writer.writeAttribute("firsts", firsts);
				writer.writeAttribute("seconds", seconds);
				writer.writeAttribute("thirds", thirds);
				if (!options.skipVersion) {
					writer.writeAttribute("ver", serialVersionUID);
				}
			}
			writer.endElement();
		}

		public XmlReader.Element readXml(XmlReader reader) {
			return reader.expect("result", new XmlReader.Element() {
				public IXmlSerializable read(XmlReader reader) {
					final BattleResultsWrapper rules = new BattleResultsWrapper();

					reader.expect("teamLeaderName", new XmlReader.Attribute() {
						public void read(String value) {
							rules.teamLeaderName = value;
						}
					});

					reader.expect("rank", new XmlReader.Attribute() {
						public void read(String value) {
							rules.rank = Integer.parseInt(value);
						}
					});
					reader.expect("score", new XmlReader.Attribute() {
						public void read(String value) {
							rules.score = Double.parseDouble(value);
						}
					});
					reader.expect("survival", new XmlReader.Attribute() {
						public void read(String value) {
							rules.survival = Double.parseDouble(value);
						}
					});
					reader.expect("lastSurvivorBonus", new XmlReader.Attribute() {
						public void read(String value) {
							rules.lastSurvivorBonus = Double.parseDouble(value);
						}
					});
					reader.expect("bulletDamage", new XmlReader.Attribute() {
						public void read(String value) {
							rules.bulletDamage = Double.parseDouble(value);
						}
					});
					reader.expect("bulletDamageBonus", new XmlReader.Attribute() {
						public void read(String value) {
							rules.bulletDamageBonus = Double.parseDouble(value);
						}
					});
					reader.expect("ramDamage", new XmlReader.Attribute() {
						public void read(String value) {
							rules.ramDamage = Double.parseDouble(value);
						}
					});
					reader.expect("ramDamageBonus", new XmlReader.Attribute() {
						public void read(String value) {
							rules.ramDamageBonus = Double.parseDouble(value);
						}
					});
					reader.expect("firsts", new XmlReader.Attribute() {
						public void read(String value) {
							rules.firsts = Integer.parseInt(value);
						}
					});
					reader.expect("seconds", new XmlReader.Attribute() {
						public void read(String value) {
							rules.seconds = Integer.parseInt(value);
						}
					});
					reader.expect("thirds", new XmlReader.Attribute() {
						public void read(String value) {
							rules.thirds = Integer.parseInt(value);
						}
					});

					return rules;
				}
			});
		}
	}


	private static class BattleRulesWrapper implements IXmlSerializable {
		BattleRulesWrapper(BattleRecordInfo recinfo) {
			this.recinfo = recinfo;
		}

		final BattleProperties props = new BattleProperties();
		final BattleRecordInfo recinfo;

		public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {}

		public XmlReader.Element readXml(XmlReader reader) {
			return reader.expect("rules",
					new XmlReader.ElementClose() {
				public IXmlSerializable read(XmlReader reader) {

					reader.expect("battlefieldWidth", new XmlReader.Attribute() {
						public void read(String value) {
							props.setBattlefieldWidth(Integer.parseInt(value));
						}
					});
					reader.expect("battlefieldHeight", new XmlReader.Attribute() {
						public void read(String value) {
							props.setBattlefieldHeight(Integer.parseInt(value));
						}
					});

					reader.expect("numRounds", new XmlReader.Attribute() {
						public void read(String value) {
							props.setNumRounds(Integer.parseInt(value));
						}
					});
					reader.expect("inactivityTime", new XmlReader.Attribute() {
						public void read(String value) {
							props.setInactivityTime(Integer.parseInt(value));
						}
					});
					reader.expect("gunCoolingRate", new XmlReader.Attribute() {
						public void read(String value) {
							props.setGunCoolingRate(Double.parseDouble(value));
						}
					});

					return BattleRulesWrapper.this;
				}

				public void close() {
					recinfo.battleRules = HiddenAccess.createRules(props.getBattlefieldWidth(),
							props.getBattlefieldHeight(), props.getNumRounds(), props.getGunCoolingRate(),
							props.getInactivityTime(), props.getHideEnemyNames(), props.getSentryBorderSize());
				}
			});
		}
	}

}
