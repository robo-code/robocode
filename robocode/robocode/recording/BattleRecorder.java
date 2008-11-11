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


import robocode.battle.events.*;
import robocode.battle.snapshot.TurnSnapshot;
import static robocode.io.Logger.logError;
import robocode.manager.RobocodeManager;
import robocode.util.XmlWriter;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public class BattleRecorder implements IBattleRecorder {

	private final RobocodeManager manager;
	private BattleObserver battleObserver;
	private boolean recordingEnabled;
	private BattleRecordInfo recordInfo;
	private File tempFile;

	public BattleRecorder(RobocodeManager manager) {
		this.manager = manager;
	}

	protected void finalize() throws Throwable {
		try {
			cleanup();
		} finally {
			super.finalize();
		}
	}

	private void cleanup() {
		if (tempFile != null && tempFile.exists()) {
			// noinspection ResultOfMethodCallIgnored
			tempFile.delete();
			tempFile = null;
		}
	}

	public void setBattleEventDispatcher(BattleEventDispatcher battleEventDispatcher) {
		if (battleObserver != null) {
			battleObserver.dispose();
		}
		battleObserver = new BattleObserver(battleEventDispatcher);
	}

	public boolean hasRecord() {
		return recordInfo != null;
	}

	public void saveRecord(String fileName, BattleRecordFormat format) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;
		ObjectOutputStream oos = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;
		OutputStreamWriter osw = null;
		XmlWriter xwr = null;

		try {
			fos = new FileOutputStream(fileName);
			bos = new BufferedOutputStream(fos, 1024 * 1024);

			if (format == BattleRecordFormat.BINARY) {
				oos = new ObjectOutputStream(bos);
			} else if (format == BattleRecordFormat.BINARY_ZIP) {
				zos = new ZipOutputStream(bos);
				zos.putNextEntry(new ZipEntry("robocode.battleRecord"));
				oos = new ObjectOutputStream(zos);
			} else if (format == BattleRecordFormat.XML) {
				final Charset utf8 = Charset.forName("UTF-8");

				osw = new OutputStreamWriter(bos, utf8);
				xwr = new XmlWriter(osw, true);
			}

			if (format == BattleRecordFormat.BINARY || format == BattleRecordFormat.BINARY_ZIP) {
				oos.writeObject(recordInfo);
			} else if (format == BattleRecordFormat.XML) {
				xwr.startDocument();
				xwr.startElement("record");
				xwr.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				xwr.writeAttribute("xsi:noNamespaceSchemaLocation", "battleRecord.xsd");
				recordInfo.writeXml(xwr);
			}

			if (recordInfo.turnsInRounds != null) {
				fis = new FileInputStream(tempFile);
				bis = new BufferedInputStream(fis, 1024 * 1024);
				ois = new ObjectInputStream(bis);

				for (int i = 0; i < recordInfo.turnsInRounds.length; i++) {
					for (int j = recordInfo.turnsInRounds[i] - 1; j >= 0; j--) {
						try {
							TurnSnapshot turn = (TurnSnapshot) ois.readObject();

							if (format == BattleRecordFormat.BINARY || format == BattleRecordFormat.BINARY_ZIP) {
								oos.writeObject(turn);
							} else if (format == BattleRecordFormat.XML) {
								turn.writeXml(xwr);
							}
						} catch (ClassNotFoundException e) {
							logError(e);
						}
					}
					if (format == BattleRecordFormat.BINARY || format == BattleRecordFormat.BINARY_ZIP) {
						oos.flush();
					} else if (format == BattleRecordFormat.XML) {
						osw.flush();
					}
					bos.flush();
					fos.flush();
				}
				if (format == BattleRecordFormat.XML) {
					xwr.endElement(); // record
					osw.flush();
				}
			}

		} catch (IOException e) {
			logError(e);
		} finally {
			cleanupStream(ois);
			cleanupStream(bis);
			cleanupStream(fis);
			cleanupStream(oos);
			cleanupStream(zos);
			cleanupStream(bos);
			cleanupStream(fos);
			cleanupStream(osw);
		}
	}

	private void cleanupStream(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				logError(e);
			}
		}
	}

	private class BattleObserver extends BattleAdaptor {
		private robocode.battle.events.BattleEventDispatcher dispatcher;

		private FileOutputStream fileStream;
		private ObjectOutputStream objectStream;

		private int currentTurn;
		private int currentRound;

		public BattleObserver(BattleEventDispatcher dispatcher) {
			this.dispatcher = dispatcher;
			dispatcher.addListener(this);
		}

		public void dispose() {
			dispatcher.removeListener(this);

			cleanupStreams();
		}

		private void cleanupStreams() {
			cleanupStream(objectStream);
			objectStream = null;
			cleanupStream(fileStream);
			fileStream = null;
		}

		@Override
		public void onBattleStarted(BattleStartedEvent event) {
			recordingEnabled = !event.isReplay() && manager.getProperties().getOptionsCommonEnableReplayRecording();
			if (!recordingEnabled) {
				return;
			}
			recordInfo = null;

			cleanupStreams();

			try {
				if (tempFile == null) {
					tempFile = File.createTempFile("robocode-battle-records", ".tmp");
					tempFile.deleteOnExit();
				} else {
					// noinspection ResultOfMethodCallIgnored
					tempFile.delete();
					// noinspection ResultOfMethodCallIgnored
					tempFile.createNewFile();
				}

				fileStream = new FileOutputStream(tempFile);
				objectStream = new ObjectOutputStream(fileStream);
			} catch (IOException e) {
				logError(e);
			}

			recordInfo = new BattleRecordInfo();
			recordInfo.robotCount = event.getTurnSnapshot().getRobots().size();
			recordInfo.turnsInRounds = new int[event.getBattleRules().getNumRounds()];
			recordInfo.battleRules = event.getBattleRules();

			currentRound = 0;
			currentTurn = 1;

			writeTurn(event.getTurnSnapshot());
		}

		@Override
		public void onBattleEnded(BattleEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			if (event.isAborted()) {
				recordInfo.turnsInRounds[currentRound] = currentTurn;
			}
			recordInfo.roundsCount = currentRound;

			try {
				if (objectStream != null) {
					objectStream.flush();
				}
				if (fileStream != null) {
					fileStream.flush();
				}
			} catch (IOException e) {
				logError(e);
			}

			cleanupStreams();
		}

		@Override
		public void onBattleCompleted(BattleCompletedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			recordInfo.results = event.getResults();
		}

		@Override
		public void onRoundEnded(RoundEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			recordInfo.turnsInRounds[currentRound] = currentTurn;
			currentTurn = 0;
			currentRound++;
		}

		@Override
		public void onTurnEnded(TurnEndedEvent event) {
			if (!recordingEnabled) {
				return;
			}
			currentTurn++;

			writeTurn(event.getTurnSnapshot());
		}

		private void writeTurn(TurnSnapshot turn) {
			try {
				objectStream.writeObject(turn);
			} catch (IOException e) {
				logError(e);
			}
		}
	}
}
