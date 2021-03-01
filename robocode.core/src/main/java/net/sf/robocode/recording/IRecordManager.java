/**
 * Copyright (c) 2001-2020 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.recording;


import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.serialization.SerializableOptions;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public interface IRecordManager {

    void attachRecorder(BattleEventDispatcher battleEventDispatcher);

    void detachRecorder();

    void saveRecord(String fileName, BattleRecordFormat format, SerializableOptions options);

    void loadRecord(String fileName, BattleRecordFormat format);

    void generateRecordCSV(OutputStream foss, OutputStream fosr, OutputStream fosb, OutputStream fosm, SerializableOptions options) throws IOException;

    boolean hasRecord();
}
