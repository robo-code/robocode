/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara & Flemming N. Larsen
 *     - Initial implementation
 *******************************************************************************/
package robocode.recording;


import robocode.BattleResults;
import robocode.BattleRules;
import robocode.util.XmlReader;
import robocode.util.XmlSerializable;
import robocode.util.XmlWriter;

import java.io.IOException;
import java.io.Serializable;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public class BattleRecordInfo implements Serializable, XmlSerializable {
	private static final long serialVersionUID = 1L;

	public int robotCount;
	public int roundsCount;
	public BattleRules battleRules;
	public int[] turnsInRounds;
	public BattleResults[] results;

	public void writeXml(XmlWriter writer) throws IOException {
		writer.startElement("recordInfo"); {
			writer.writeAttribute("robotCount", robotCount);
			writer.writeAttribute("roundsCount", roundsCount);
			writer.writeAttribute("ver", serialVersionUID);

			writer.startElement("rounds"); {
				for (int n : turnsInRounds) {
					writer.startElement("turns"); {
						writer.writeAttribute("value", Integer.toString(n));
					}
					writer.endElement();
				}
			}
			writer.endElement();

			writer.startElement("results"); {
				for (BattleResults result : results) {
					result.writeXml(writer);
				}
			}
			writer.endElement();
		}
		writer.endElement();
	}

	public XmlReader.Element readXml(XmlReader reader) {
		return reader.expect("recordInfo", new XmlReader.Element() {
			public XmlSerializable read(XmlReader reader) {
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

				reader.expect("results", new XmlReader.ListElement() {
					int pos = 0;

					public XmlSerializable read(XmlReader reader) {
						recordInfo.results = new BattleResults[recordInfo.roundsCount];
						// prototype
						return new BattleResults();
					}

					public void add(XmlSerializable child) {
						recordInfo.results[pos] = ((BattleResults) child);
					}
				});
				return recordInfo;
			}
		});
	}
}
