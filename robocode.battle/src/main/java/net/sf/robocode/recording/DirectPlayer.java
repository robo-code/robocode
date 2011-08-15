/*******************************************************************************
 * Copyright (c) 2001-2011 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/

package net.sf.robocode.recording;

import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.battle.snapshot.TurnSnapshot;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.serialization.IXmlSerializable;
import net.sf.robocode.serialization.SerializableOptions;
import net.sf.robocode.serialization.XmlReader;
import net.sf.robocode.serialization.XmlWriter;
import robocode.BattleResults;
import robocode.control.events.*;
import robocode.control.snapshot.ITurnSnapshot;

import java.io.*;
import java.util.zip.ZipInputStream;

import static net.sf.robocode.io.Logger.logError;


/**
 * Utility class for replaying records without intermediate temp file and complexity
 */
public class DirectPlayer {

	public void playRecord(String recordFilename, BattleRecordFormat format, BattleEventDispatcher eventDispatcher) {
		BattleRecordInfo recordInfo;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ZipInputStream zis = null;
		ObjectInputStream ois = null;
		InputStream xis = null;

		try {
			fis = new FileInputStream(recordFilename);
			bis = new BufferedInputStream(fis, 1024 * 1024);

			if (format == BattleRecordFormat.BINARY) {
				ois = new ObjectInputStream(bis);
			} else if (format == BattleRecordFormat.BINARY_ZIP) {
				zis = new ZipInputStream(bis);
				zis.getNextEntry();
				ois = new ObjectInputStream(zis);
			} else if (format == BattleRecordFormat.XML_ZIP) {
				zis = new ZipInputStream(bis);
				zis.getNextEntry();
				xis = zis;
			} else if (format == BattleRecordFormat.XML) {
				xis = bis;
			}
			if (format == BattleRecordFormat.BINARY || format == BattleRecordFormat.BINARY_ZIP) {
				recordInfo = (BattleRecordInfo) ois.readObject();
				if (recordInfo.turnsInRounds != null) {
					eventDispatcher.onBattleStarted(new BattleStartedEvent(recordInfo.battleRules, recordInfo.robotCount, true));
					int totalTurns = 0;
					for (int i = 0; i < recordInfo.turnsInRounds.length; i++) {
						for (int j = recordInfo.turnsInRounds[i] - 1; j >= 0; j--) {
							try {
								ITurnSnapshot turn = (ITurnSnapshot) ois.readObject();
								if (turn.getTurn() == 0) {
									eventDispatcher.onRoundStarted(new RoundStartedEvent(turn, i));
								}
								eventDispatcher.onTurnEnded(new TurnEndedEvent(turn));
							} catch (ClassNotFoundException e) {
								logError(e);
							}
						}
						totalTurns += recordInfo.turnsInRounds[i];
						eventDispatcher.onRoundEnded(new RoundEndedEvent(i, recordInfo.turnsInRounds[i], totalTurns));
					}
					eventDispatcher.onBattleFinished(new BattleFinishedEvent(false));
					eventDispatcher.onBattleCompleted(new BattleCompletedEvent(recordInfo.battleRules, recordInfo.results.toArray(new BattleResults[recordInfo.results.size()])));
				}
			} else {
				final RecordRoot root = new RecordRoot(eventDispatcher);

				XmlReader.deserialize(xis, root);
				recordInfo = root.recordInfo;
			}
		} catch (IOException e) {
			logError(e);
			recordInfo = null;
		} catch (ClassNotFoundException e) {
			if (e.getMessage().contains("robocode.recording.BattleRecordInfo")) {
				Logger.logError("Sorry, backward compatibility with record from version 1.6 is not provided.");
			} else {
				logError(e);
			}
			recordInfo = null;
		} finally {
			FileUtil.cleanupStream(ois);
			FileUtil.cleanupStream(zis);
			FileUtil.cleanupStream(bis);
			FileUtil.cleanupStream(fis);
		}
	}

	private static class RecordRoot implements IXmlSerializable {

		public RecordRoot(BattleEventDispatcher eventDispatcher) {
			me = this;
			this.eventDispatcher = eventDispatcher;
		}

		public final RecordRoot me;
		public BattleRecordInfo recordInfo;
		private int totalTurns = 0;
		protected final BattleEventDispatcher eventDispatcher;

		public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {
		}

		public XmlReader.Element readXml(XmlReader reader) {
			return reader.expect("record", new XmlReader.Element() {
				public IXmlSerializable read(final XmlReader reader) {

					final XmlReader.Element element = (new BattleRecordInfo()).readXml(reader);

					reader.expect("recordInfo", new XmlReader.ElementClose() {
						public IXmlSerializable read(XmlReader reader) {
							recordInfo = (BattleRecordInfo) element.read(reader);
							return recordInfo;
						}

						public void close() {
							reader.getContext().put("robots", recordInfo.robotCount);
							eventDispatcher.onBattleStarted(new BattleStartedEvent(recordInfo.battleRules, recordInfo.robotCount, true));
						}
					});

					reader.expect("turns", new XmlReader.ListElement() {
						public IXmlSerializable read(XmlReader reader) {
							// prototype
							return new TurnSnapshot();
						}

						public void add(IXmlSerializable child) {
							ITurnSnapshot turn = (ITurnSnapshot) child;
							if (turn.getTurn() == 0) {
								eventDispatcher.onRoundStarted(new RoundStartedEvent(turn, turn.getRound()));
							}
							eventDispatcher.onTurnEnded(new TurnEndedEvent(turn));
							Integer turnsInRound = recordInfo.turnsInRounds[turn.getRound()];
							if (turn.getTurn() == turnsInRound - 1) {
								totalTurns += turnsInRound;
								eventDispatcher.onRoundEnded(new RoundEndedEvent(turn.getRound(), turnsInRound, totalTurns));
							}
						}

						public void close() {
							eventDispatcher.onBattleFinished(new BattleFinishedEvent(false));
							eventDispatcher.onBattleCompleted(new BattleCompletedEvent(recordInfo.battleRules, recordInfo.results.toArray(new BattleResults[recordInfo.results.size()])));
						}
					});

					return me;
				}
			});
		}
	}
}
