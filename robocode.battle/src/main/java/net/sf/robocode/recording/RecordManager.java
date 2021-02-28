/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.recording;


import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.battle.snapshot.BulletSnapshot;
import net.sf.robocode.battle.snapshot.RobotSnapshot;
import net.sf.robocode.battle.snapshot.TurnSnapshot;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import static net.sf.robocode.io.Logger.logError;

import net.sf.robocode.serialization.*;
import net.sf.robocode.settings.ISettingsManager;
import net.sf.robocode.version.IVersionManager;
import net.sf.robocode.version.VersionManager;
import robocode.BattleResults;
import robocode.BattleRules;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * @author Pavel Savara (original)
 */
public class RecordManager implements IRecordManager {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");

	private final ISettingsManager properties;

	private File tempFile;
	private BattleRecorder recorder;
	private final IVersionManager versionManager;

	BattleRecordInfo recordInfo;
	private FileOutputStream fileWriteStream;
	private BufferedOutputStream bufferedWriteStream;
	private ObjectOutputStream objectWriteStream;

	private FileInputStream fileReadStream;
	private BufferedInputStream bufferedReadStream;
	private ObjectInputStream objectReadStream;

	public RecordManager(ISettingsManager properties, IVersionManager versionManager) { // NO_UCD (unused code)
		this.properties = properties;
		recorder = new BattleRecorder(this, properties);
		this.versionManager = versionManager;
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
			if (tempFile.delete() == false) {
				Logger.logError("Could not delete temp file");
			}
			tempFile = null;
		}
		recordInfo = null;
	}

	void cleanupStreams() {
		FileUtil.cleanupStream(objectWriteStream);
		objectWriteStream = null;
		FileUtil.cleanupStream(bufferedWriteStream);
		bufferedWriteStream = null;
		FileUtil.cleanupStream(fileWriteStream);
		fileWriteStream = null;

		FileUtil.cleanupStream(objectReadStream);
		objectReadStream = null;
		FileUtil.cleanupStream(bufferedReadStream);
		bufferedReadStream = null;
		FileUtil.cleanupStream(fileReadStream);
		fileReadStream = null;
	}

	public void attachRecorder(BattleEventDispatcher battleEventDispatcher) {
		recorder.attachRecorder(battleEventDispatcher);
	}

	public void detachRecorder() {
		recorder.detachRecorder();
	}

	private void createTempFile() {
		try {
			if (tempFile == null) {
				tempFile = File.createTempFile("robocode-battle-records", ".tmp");
				tempFile.deleteOnExit();
			} else {
				if (!tempFile.delete()) {
					Logger.logError("Could not delete temp file");
				}
				if (!tempFile.createNewFile()) {
					throw new Error("Temp file creation failed");					
				}
			}
		} catch (IOException e) {
			logError(e);
			throw new Error("Temp file creation failed", e);
		}
	}

	void prepareInputStream() {
		try {
			fileReadStream = new FileInputStream(tempFile);
			bufferedReadStream = new BufferedInputStream(fileReadStream);
			objectReadStream = new ObjectInputStream(bufferedReadStream);
		} catch (FileNotFoundException e) {
			logError(e);
			fileReadStream = null;
			bufferedReadStream = null;
			objectReadStream = null;
		} catch (IOException e) {
			logError(e);
			fileReadStream = null;
			bufferedReadStream = null;
			objectReadStream = null;
		}
	}

	ITurnSnapshot readSnapshot(int currentTime) {
		if (objectReadStream == null) {
			return null;
		}
		try {
			// TODO implement seek to currentTime, warn you. turns don't have same size in bytes
			return (ITurnSnapshot) objectReadStream.readObject();
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
		InputStream xis = null;

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
					fos = new FileOutputStream(tempFile);
					bos = new BufferedOutputStream(fos, 1024 * 1024);
					oos = new ObjectOutputStream(bos);

					for (int i = 0; i < recordInfo.turnsInRounds.length; i++) {
						for (int j = recordInfo.turnsInRounds[i] - 1; j >= 0; j--) {
							try {
								ITurnSnapshot turn = (ITurnSnapshot) ois.readObject();

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
				XmlReader.deserialize(xis, root);
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
			if (e.getMessage().contains("robocode.recording.BattleRecordInfo")) {
				Logger.logError("Sorry, backward compatibility with record from version 1.6 is not provided.");
			} else {
				logError(e);
			}
			createTempFile();
			recordInfo = null;
		} finally {
			FileUtil.cleanupStream(oos);
			FileUtil.cleanupStream(bos);
			FileUtil.cleanupStream(fos);
			FileUtil.cleanupStream(ois);
			FileUtil.cleanupStream(zis);
			FileUtil.cleanupStream(bis);
			FileUtil.cleanupStream(fis);
		}
	}

	private static class RecordRoot implements IXmlSerializable {

		public RecordRoot() {
			me = this;
		}

		public ObjectOutputStream oos;
		public IOException lastException;
		public final RecordRoot me;
		public BattleRecordInfo recordInfo;

		public void writeXml(XmlWriter writer, SerializableOptions options) throws IOException {}

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
						}
					});

					reader.expect("turns", new XmlReader.ListElement() {
						public IXmlSerializable read(XmlReader reader) {
							// prototype
							return new TurnSnapshot();
						}

						public void add(IXmlSerializable child) {
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

	public void saveRecord(String recordFilename, BattleRecordFormat format, SerializableOptions options) {
		if(format == BattleRecordFormat.CSV){
			saveRecordCSV(recordFilename, options);
			return;
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ZipOutputStream zos = null;
		ObjectOutputStream oos = null;
		OutputStreamWriter osw = null;
		XmlWriter xwr = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;

		final boolean isbin = format == BattleRecordFormat.BINARY || format == BattleRecordFormat.BINARY_ZIP;
		final boolean isxml = format == BattleRecordFormat.XML || format == BattleRecordFormat.XML_ZIP;
		Calendar calendar = Calendar.getInstance();

		try {
			fos = new FileOutputStream(recordFilename);
			bos = new BufferedOutputStream(fos, 1024 * 1024);

			if (format == BattleRecordFormat.BINARY) {
				oos = new ObjectOutputStream(bos);
			} else if (format == BattleRecordFormat.BINARY_ZIP) {
				zos = new ZipOutputStream(bos);
				zos.putNextEntry(new ZipEntry(dateFormat.format(calendar.getTime()) + "-robocode.br"));
				oos = new ObjectOutputStream(zos);
			} else if (format == BattleRecordFormat.XML) {
				final Charset utf8 = Charset.forName("UTF-8");

				osw = new OutputStreamWriter(bos, utf8);
				xwr = new XmlWriter(osw, true);
			} else if (format == BattleRecordFormat.XML_ZIP) {
				final Charset utf8 = Charset.forName("UTF-8");

				zos = new ZipOutputStream(bos);
				zos.putNextEntry(new ZipEntry(dateFormat.format(calendar.getTime()) + "-robocode.xml"));

				osw = new OutputStreamWriter(zos, utf8);
				xwr = new XmlWriter(osw, false);
			}

			if (isbin) {
				oos.writeObject(recordInfo);
			} else if (isxml) {
				xwr.startDocument();
				xwr.startElement("record");
				xwr.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				if (options.shortAttributes) {
					xwr.writeAttribute("xsi:noNamespaceSchemaLocation", "battleRecordS.xsd");
				} else {
					xwr.writeAttribute("xsi:noNamespaceSchemaLocation", "battleRecord.xsd");
				}
				recordInfo.writeXml(xwr, options);
				xwr.startElement("turns");
			}

			if (recordInfo.turnsInRounds != null) {
				fis = new FileInputStream(tempFile);
				bis = new BufferedInputStream(fis, 1024 * 1024);
				ois = new ObjectInputStream(bis);

				for (int i = 0; i < recordInfo.turnsInRounds.length; i++) {
					if (recordInfo.turnsInRounds[i] > 0) {
						for (int j = 0; j <= recordInfo.turnsInRounds[i] - 1; j++) {
							try {
								TurnSnapshot turn = (TurnSnapshot) ois.readObject();

								if (j != turn.getTurn()) {
									throw new Error("Something rotten");
								}

								if (isbin) {
									turn.stripDetails(options);
									oos.writeObject(turn);
								} else if (isxml) {
									turn.writeXml(xwr, options);
								}
							} catch (ClassNotFoundException e) {
								logError(e);
							}
						}
						if (isbin) {
							oos.flush();
						} else if (isxml) {
							osw.flush();
						}
						bos.flush();
						fos.flush();
					}
				}
				if (isxml) {
					xwr.endElement(); // turns
					xwr.endElement(); // record
					osw.flush();
				}
			}

		} catch (IOException e) {
			logError(e);
			recorder = new BattleRecorder(this, properties);
			createTempFile();
		} finally {
			FileUtil.cleanupStream(ois);
			FileUtil.cleanupStream(bis);
			FileUtil.cleanupStream(fis);
			FileUtil.cleanupStream(oos);
			FileUtil.cleanupStream(zos);
			FileUtil.cleanupStream(bos);
			FileUtil.cleanupStream(fos);
			FileUtil.cleanupStream(osw);
		}
	}

	private void saveRecordCSV(String recordFilename, SerializableOptions options) {
		FileOutputStream foss = null;
		BufferedOutputStream boss = null;
		OutputStreamWriter osws = null;
		CsvWriter cwrResults = null;

		FileOutputStream fosr = null;
		BufferedOutputStream bosr = null;
		OutputStreamWriter oswr = null;
		CsvWriter cwrRounds = null;

		FileOutputStream fosb = null;
		BufferedOutputStream bosb = null;
		OutputStreamWriter oswb = null;
		CsvWriter cwrBullets = null;

		FileOutputStream fosm = null;
		BufferedOutputStream bosm = null;
		OutputStreamWriter oswm = null;
		CsvWriter cwrRobots = null;

		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;

		final Charset utf8 = Charset.forName("UTF-8");

		try {
			String version = versionManager.getVersion();
			foss = new FileOutputStream(recordFilename+".results.csv");
			boss = new BufferedOutputStream(foss, 1024 * 1024);
			osws = new OutputStreamWriter(boss, utf8);
			cwrResults = new CsvWriter(osws,false);
			cwrResults.startDocument("version,battleId,roundsCount,robotCount,battlefieldWidth,battlefieldHeight,gunCoolingRate,inactivityTime,teamLeaderName,rank,score,survival,lastSurvivorBonus,bulletDamage,bulletDamageBonus,ramDamage,ramDamageBonus,firsts,seconds,thirds");

			fosr = new FileOutputStream(recordFilename+".rounds.csv");
			bosr = new BufferedOutputStream(fosr, 1024 * 1024);
			oswr = new OutputStreamWriter(bosr, utf8);
			cwrRounds = new CsvWriter(oswr,false);
			cwrRounds.startDocument("version,battleId,roundIndex,robotCount,battlefieldWidth,battlefieldHeight,gunCoolingRate,inactivityTime,turnsInRound");

			fosm = new FileOutputStream(recordFilename+".robots.csv");
			bosm = new BufferedOutputStream(fosm, 1024 * 1024);
			oswm = new OutputStreamWriter(bosm, utf8);
			cwrRobots = new CsvWriter(oswm, false);
			cwrRobots.startDocument("version,battleId,roundIndex,turnIndex,robotIndex,robotName,energy,x,y,bodyHeading,gunHeading,radarHeading,gunHeat,velocity,score,survivalScore,bulletDamageScore,bulletKillBonus,rammingDamageScore,rammingKillBonus");

			fosb = new FileOutputStream(recordFilename+".bullets.csv");
			bosb = new BufferedOutputStream(fosb, 1024 * 1024);
			oswb = new OutputStreamWriter(bosb, utf8);
			cwrBullets = new CsvWriter(oswb, false);
			cwrBullets.startDocument("version,battleId,roundIndex,turnIndex,bulletId,ownerIndex,ownerName,state,heading,x,y,victimIndex,victimName");
			int roundsCount = recordInfo.turnsInRounds.length;

			if (recordInfo.turnsInRounds != null) {
				fis = new FileInputStream(tempFile);
				bis = new BufferedInputStream(fis, 1024 * 1024);
				ois = new ObjectInputStream(bis);

				if (recordInfo.results != null) {
					// loop over robots
					for (BattleResults result : recordInfo.results) {
						BattleRecordInfo.BattleResultsWrapper wrapper = new BattleRecordInfo.BattleResultsWrapper(result);
						cwrResults.writeValue(version);
						cwrResults.writeValue(recordInfo.battleId.toString());
						cwrResults.writeValue(roundsCount);
						cwrResults.writeValue(recordInfo.robotCount);
						cwrResults.writeValue(recordInfo.battleRules.getBattlefieldWidth());
						cwrResults.writeValue(recordInfo.battleRules.getBattlefieldHeight());
						cwrResults.writeValue(recordInfo.battleRules.getGunCoolingRate(), options.trimPrecision);
						cwrResults.writeValue(recordInfo.battleRules.getInactivityTime());
						// results
						wrapper.writeCsv(cwrResults,options);
						cwrResults.endLine();
					}
				}

				// loop over rounds
				for (int round = 0; round < roundsCount; round++) {
					int turnsInRound = recordInfo.turnsInRounds[round];
					if (turnsInRound > 0) {
						cwrRounds.writeValue(version);
						cwrRounds.writeValue(recordInfo.battleId.toString());
						cwrRounds.writeValue(round);
						cwrRounds.writeValue(recordInfo.robotCount);
						cwrRounds.writeValue(recordInfo.battleRules.getBattlefieldWidth());
						cwrRounds.writeValue(recordInfo.battleRules.getBattlefieldHeight());
						cwrRounds.writeValue(recordInfo.battleRules.getGunCoolingRate(), options.trimPrecision);
						cwrRounds.writeValue(recordInfo.battleRules.getInactivityTime());
						cwrRounds.writeValue(turnsInRound);
						cwrRounds.endLine();

						// loop over turns
						for (int trn = 0; trn <= turnsInRound - 1; trn++) {
							try {
								TurnSnapshot turn = (TurnSnapshot) ois.readObject();

								if (trn != turn.getTurn()) {
									throw new Error("Something rotten");
								}

								IRobotSnapshot[] robots = turn.getRobots();
								for (IRobotSnapshot robot: robots) {
									RobotSnapshot robotSnapshot = (RobotSnapshot) robot;
									cwrRobots.writeValue(version);
									cwrRobots.writeValue(recordInfo.battleId.toString());
									cwrRobots.writeValue(round);
									cwrRobots.writeValue(trn);
									robotSnapshot.writeCsv(cwrRobots,options);
									cwrRobots.endLine();
								}
								for (IBulletSnapshot bullet:turn.getBullets()) {
									BulletSnapshot bulletSnapshot = (BulletSnapshot) bullet;
									IRobotSnapshot owner =robots[bulletSnapshot.getOwnerIndex()];
									cwrBullets.writeValue(version);
									cwrBullets.writeValue(recordInfo.battleId.toString());
									cwrBullets.writeValue(round);
									cwrBullets.writeValue(trn);
									cwrBullets.writeValue(bulletSnapshot.getBulletId());
									cwrBullets.writeValue(bulletSnapshot.getOwnerIndex());
									cwrBullets.writeValue(owner.getName());
									bulletSnapshot.writeCsv(cwrBullets,options);
									cwrBullets.writeValue(bulletSnapshot.getVictimIndex() != -1 ? robots[bulletSnapshot.getVictimIndex()].getName() : null);
									cwrBullets.endLine();
								}
							} catch (ClassNotFoundException e) {
								logError(e);
							}
						}
					}
				}
				osws.flush();
				boss.flush();
				foss.flush();

				oswr.flush();
				bosr.flush();
				fosr.flush();

				oswb.flush();
				bosb.flush();
				fosb.flush();

				oswm.flush();
				bosm.flush();
				fosm.flush();
			}
		} catch (IOException e) {
			logError(e);
			recorder = new BattleRecorder(this, properties);
			createTempFile();
		} finally {
			FileUtil.cleanupStream(ois);
			FileUtil.cleanupStream(bis);
			FileUtil.cleanupStream(fis);

			FileUtil.cleanupStream(bosr);
			FileUtil.cleanupStream(fosr);
			FileUtil.cleanupStream(oswr);

			FileUtil.cleanupStream(bosb);
			FileUtil.cleanupStream(fosb);
			FileUtil.cleanupStream(oswb);

			FileUtil.cleanupStream(bosm);
			FileUtil.cleanupStream(fosm);
			FileUtil.cleanupStream(oswm);
		}
	}

	public boolean hasRecord() {
		return recordInfo != null;
	}

	void createRecordInfo(BattleRules rules, int numRobots) {
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

	void updateRecordInfoResults(List<BattleResults> results) {
		recordInfo.results = results;
	}

	void writeTurn(ITurnSnapshot turn, int round, int time) {
		try {
			if (time != recordInfo.turnsInRounds[round]) {
				throw new Error("Something rotten");
			}
			if (time == 0) {
				objectWriteStream.reset();
			}
			recordInfo.turnsInRounds[round]++;
			recordInfo.roundsCount = round + 1;
			objectWriteStream.writeObject(turn);
		} catch (IOException e) {
			logError(e);
		}
	}
}
