/*
 * Copyright (c) 2001-2023 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.recording;


import net.sf.robocode.battle.events.BattleEventDispatcher;
import net.sf.robocode.serialization.SerializableOptions;
import robocode.control.snapshot.ITurnSnapshot;

import java.io.IOException;
import java.io.OutputStream;


/**
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (original)
 */
public interface IRecordManager {

    @FunctionalInterface
    interface CheckedConsumer<T> {
        void accept(T t) throws IOException, ClassNotFoundException;
    }


    void attachRecorder(BattleEventDispatcher battleEventDispatcher);

    void detachRecorder();

    void saveRecord(String fileName, BattleRecordFormat format, SerializableOptions options);

    void loadRecord(String fileName, BattleRecordFormat format);

    void provideTurns(CheckedConsumer<ITurnSnapshot> writeTurn) throws IOException, ClassNotFoundException;

    void generateCsvRecord(OutputStream fosResults, OutputStream fosRounds, OutputStream fosRobots, OutputStream fosBullets, SerializableOptions options, CheckedConsumer<ITurnSnapshot> extension) throws IOException, ClassNotFoundException;

    boolean hasRecord();
}
