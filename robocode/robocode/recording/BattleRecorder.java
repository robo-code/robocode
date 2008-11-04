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

import java.io.*;
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

		try {
			fos = new FileOutputStream(fileName);
			bos = new BufferedOutputStream(fos, 1024 * 1024);

			if (format == BattleRecordFormat.BINARY) {
				oos = new ObjectOutputStream(bos);
			} else if (format == BattleRecordFormat.BINARY_ZIP) {
				zos = new ZipOutputStream(bos);
				zos.putNextEntry(new ZipEntry("robocode.battleRecord"));
				oos = new ObjectOutputStream(zos);
			}
			// TODO: Add support for XML and XML_ZIP

			oos.writeObject(recordInfo);

			if (recordInfo.numberOfTurns != null) {
				fis = new FileInputStream(tempFile);
				bis = new BufferedInputStream(fis, 1024 * 1024);
				ois = new ObjectInputStream(bis);

				for (int i = 0; i < recordInfo.numberOfTurns.length; i++) {
					for (int j = recordInfo.numberOfTurns[i] - 1; j >= 0; j--) {
						try {
							oos.writeObject(ois.readObject());
							oos.flush();
							bos.flush();
							fos.flush();
						} catch (ClassNotFoundException e) {
							logError(e);
						}
					}
				}
			}

		} catch (IOException e) {
			logError(e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException e) {
					logError(e);
				}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					logError(e);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logError(e);
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					logError(e);
				}
			}
			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					logError(e);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					logError(e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					logError(e);
				}
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
			if (objectStream != null) {
				try {
					objectStream.close();
				} catch (IOException e) {
					logError(e);
				}
				objectStream = null;
			}

			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException e) {
					logError(e);
				}
				fileStream = null;
			}			
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
					tempFile.delete();
					tempFile.createNewFile();
				}

				fileStream = new FileOutputStream(tempFile);
				objectStream = new ObjectOutputStream(fileStream);
			} catch (IOException e) {
				logError(e);
			}

			recordInfo = new BattleRecordInfo();
			recordInfo.robotCount = event.getTurnSnapshot().getRobots().size();
			recordInfo.numberOfTurns = new int[event.getBattleRules().getNumRounds()];
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
				recordInfo.numberOfTurns[currentRound] = currentTurn;
			}
			recordInfo.rounds = currentRound;

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
			recordInfo.numberOfTurns[currentRound] = currentTurn;
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
