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
package robocode.recording;


import robocode.manager.RobocodeManager;
import robocode.util.XmlWriter;
import robocode.util.XmlReader;
import robocode.util.XmlSerializable;
import robocode.battle.snapshot.TurnSnapshot;
import robocode.battle.events.BattleEventDispatcher;
import robocode.battle.IBattle;
import static robocode.io.Logger.logError;
import robocode.BattleResults;
import robocode.BattleRules;

import java.io.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.List;
import java.nio.charset.Charset;


/**
 * @author Pavel Savara (original)
 */
public class RecordManager implements IRecordManager {
	private File tempFile;
	private final RobocodeManager manager;
	private BattleRecorder recorder;

	public BattleRecordInfo recordInfo;
	private FileOutputStream fileWriteStream;
	private BufferedOutputStream bufferedWriteStream;
	private ObjectOutputStream objectWriteStream;

	private FileInputStream fileReadStream;
	private BufferedInputStream bufferedReadStream;
	private ObjectInputStream objectReadStream;

	public RecordManager(RobocodeManager manager) {
		this.manager = manager;
		recorder = new BattleRecorder(this);
	}

	protected void finalize() throws Throwable {
		try {
			cleanup();
		} finally {
			super.finalize();
		}
	}

	private void cleanup() {
		cleanupStreams();
		if (tempFile != null && tempFile.exists()) {
			// noinspection ResultOfMethodCallIgnored
			tempFile.delete();
			tempFile = null;
		}
		recordInfo = null;
	}

	public void cleanupStreams() {
		cleanupStream(objectWriteStream);
		objectWriteStream = null;
		cleanupStream(bufferedWriteStream);
		bufferedWriteStream = null;
		cleanupStream(fileWriteStream);
		fileWriteStream = null;

		cleanupStream(objectReadStream);
		objectReadStream = null;
		cleanupStream(bufferedReadStream);
		bufferedReadStream = null;
		cleanupStream(fileReadStream);
		fileReadStream = null;
	}

	private void cleanupStream(Object stream) {
		if (stream instanceof Flushable) {
			try {
				((Flushable) stream).flush();
			} catch (IOException e) {
				logError(e);
			}
		}
		if (stream instanceof Closeable) {
			try {
				((Closeable) stream).close();
			} catch (IOException e) {
				logError(e);
			}
		}
	}

	public void attachRecorder(BattleEventDispatcher battleEventDispatcher) {
		recorder.attachRecorder(battleEventDispatcher);
	}

	public void detachRecorder() {
		recorder.detachRecorder();
	}

	public IBattle createPlayer(BattleEventDispatcher battleEventDispatcher) {
		prepareInputStream();
		return new BattlePlayer(manager, this, battleEventDispatcher);
	}

	private void createTempFile() {
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
		} catch (IOException e) {
			logError(e);
			throw new Error("Temp file creation failed", e);
		}
	}

	public void prepareInputStream() {
		try {
			fileReadStream = new FileInputStream(tempFile);
			bufferedReadStream = new BufferedInputStream(fileReadStream);
			objectReadStream = new ObjectInputStream(bufferedReadStream);
		} catch (FileNotFoundException e) {
			logError(e);
		} catch (IOException e) {
			logError(e);
		}
	}

	public TurnSnapshot readSnapshot(int currentTime) {
		if (objectReadStream == null) {
			return null;
		}
		try {
			// TODO implement seek to currentTime, warn you. turns don't have same size in bytes
			return (TurnSnapshot) objectReadStream.readObject();
		} catch (EOFException e) {
			logError(e);
			return null;
		} catch (Exception e) {
			logError(e);
			return null;
		}
	}

	public void loadRecord(String recordFilename, BattleRecordFormat format) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ZipInputStream zis = null;
		ObjectInputStream ois = null;

		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;

		try {
			createTempFile();
			fis = new FileInputStream(recordFilename);
			bis = new BufferedInputStream(fis, 1024 * 1024);

			if (format == BattleRecordFormat.BINARY) {
				ois = new ObjectInputStream(bis);
			} else if (format == BattleRecordFormat.BINARY_ZIP) {
				zis = new ZipInputStream(bis);
				zis.getNextEntry();
				ois = new ObjectInputStream(zis);
			}
			if (format == BattleRecordFormat.BINARY || format == BattleRecordFormat.BINARY_ZIP) {
				recordInfo = (BattleRecordInfo) ois.readObject();
				if (recordInfo.turnsInRounds != null) {
					fos = new FileOutputStream(tempFile);
					bos = new BufferedOutputStream(fos, 1024 * 1024);
					oos = new ObjectOutputStream(bos);

					for (int i = 0; i < recordInfo.turnsInRounds.length; i++) {
						for (int j = recordInfo.turnsInRounds[i] - 1; j >= 0; j--) {
							try {
								TurnSnapshot turn = (TurnSnapshot) ois.readObject();

								oos.writeObject(turn);
							} catch (ClassNotFoundException e) {
								logError(e);
							}
						}
					}
				}
			} else {
				final RecordRoot root = new RecordRoot();

				fos = new FileOutputStream(tempFile);
				bos = new BufferedOutputStream(fos, 1024 * 1024);
				root.oos = new ObjectOutputStream(bos);
				XmlReader.deserialize(bis, root);
				if (root.lastException != null) {
					logError(root.lastException);
				}
				recordInfo = root.recordInfo;
			}
		} catch (IOException e) {
			logError(e);
			createTempFile();
			recordInfo = null;
		} catch (ClassNotFoundException e) {
			logError(e);
			createTempFile();
			recordInfo = null;
		} finally {
			cleanupStream(oos);
			cleanupStream(bos);
			cleanupStream(fos);
			cleanupStream(ois);
			cleanupStream(zis);
			cleanupStream(bis);
			cleanupStream(fis);
		}
	}

	private class RecordRoot implements XmlSerializable {

		public RecordRoot() {
			me = this;
		}

		public ObjectOutputStream oos;
		public IOException lastException;
		public RecordRoot me;
		public BattleRecordInfo recordInfo;

		public void writeXml(XmlWriter writer) throws IOException {}

		public XmlReader.Element readXml(XmlReader reader) {
			return reader.expect("record", new XmlReader.Element() {
				public XmlSerializable read(XmlReader reader) {

					final XmlReader.Element element = (new BattleRecordInfo()).readXml(reader);

					reader.expect("recordInfo", new XmlReader.Element() {
						public XmlSerializable read(XmlReader reader) {
							recordInfo = (BattleRecordInfo) element.read(reader);
							return recordInfo;
						}
					});

					reader.expect("turns", new XmlReader.ListElement() {
						public XmlSerializable read(XmlReader reader) {
							// prototype
							return new TurnSnapshot();
						}

						public void add(XmlSerializable child) {
							try {
								me.oos.writeObject(child);
							} catch (IOException e) {
								me.lastException = e;
							}
						}

						public void close() {}
					});

					return me;
				}
			});
		}
	}

	public void saveRecord(String recordFilename, BattleRecordFormat format) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;
		ObjectOutputStream oos = null;
		OutputStreamWriter osw = null;
		XmlWriter xwr = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;

		try {
			fos = new FileOutputStream(recordFilename);
			bos = new BufferedOutputStream(fos, 1024 * 1024);

			if (format == BattleRecordFormat.BINARY) {
				oos = new ObjectOutputStream(bos);
			} else if (format == BattleRecordFormat.BINARY_ZIP) {
				zos = new ZipOutputStream(bos);
				zos.putNextEntry(new ZipEntry("robocode.br"));
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
				xwr.startElement("turns");
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
					xwr.endElement(); // turns
					xwr.endElement(); // record
					osw.flush();
				}
			}

		} catch (IOException e) {
			logError(e);
			recorder = null;
			createTempFile();
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

	public boolean hasRecord() {
		return recordInfo != null;
	}

	public void createRecordInfo(BattleRules rules, int numRobots) {
		try {
			createTempFile();

			fileWriteStream = new FileOutputStream(tempFile);
			bufferedWriteStream = new BufferedOutputStream(fileWriteStream, 1024 * 1024);
			objectWriteStream = new ObjectOutputStream(bufferedWriteStream);
		} catch (IOException e) {
			logError(e);
		}

		recordInfo = new BattleRecordInfo();
		recordInfo.robotCount = numRobots;
		recordInfo.battleRules = rules;
		recordInfo.turnsInRounds = new Integer[rules.getNumRounds()];
		for (int i = 0; i < rules.getNumRounds(); i++) {
			recordInfo.turnsInRounds[i] = 0; 
		}
	}

	public void updateRecordInfoResults(List<BattleResults> results) {
		recordInfo.results = results;
	}

	public void writeTurn(TurnSnapshot turn, int round, int time) {
		try {
			recordInfo.turnsInRounds[round]++;
			if (time != recordInfo.turnsInRounds[round]) {
				throw new Error("Something rotten");
			}
			recordInfo.roundsCount = round + 1;
			objectWriteStream.writeObject(turn);
		} catch (IOException e) {
			logError(e);
		}
	}
}
