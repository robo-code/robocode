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
package robocode.battleview;

import robocode.battle.events.IBattleListener;
import robocode.battle.events.BattleEventDispatcher;
import robocode.battle.snapshot.BattleSnapshot;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * @author Pavel Savara (original)
 */
public class BattleObserver implements IBattleListener {
    BattleView battleView;
    AtomicReference<BattleSnapshot> snapshot;
    BattleSnapshot lastSnapshot;
    AtomicBoolean isRunning;
    AtomicBoolean isPaused;
    robocode.battle.events.BattleEventDispatcher dispatcher;

    EventsDispatcher eventsDispatcher =new EventsDispatcher();
    TimerDispatcher timerDispatcher =new TimerDispatcher();
    Timer timer;

    public BattleObserver(BattleView battleView, BattleEventDispatcher dispatcher) {
        this.dispatcher=dispatcher;
        this.battleView=battleView;
        snapshot=new AtomicReference<BattleSnapshot>();
        snapshot.set(null);
        timer=new Timer(1000/20, timerDispatcher); //TODO FPS
        isRunning=new AtomicBoolean();
        isRunning.set(false);
        isPaused=new AtomicBoolean();
        isPaused.set(false);
        lastSnapshot=null;

        dispatcher.addListener(this);

    }

    public void dispose(){
        dispatcher.removeListener(this);
    }

    public void onBattleStarted() {
        isRunning.set(true);
        isPaused.set(false);
        EventQueue.invokeLater(eventsDispatcher);
        timer.start();
    }

    public void onBattleEnded(boolean isAborted) {
        timer.stop();
        isRunning.set(false);
        isPaused.set(false);
        EventQueue.invokeLater(eventsDispatcher);
    }

    public void onBattleResumed() {
        isPaused.set(false);
        EventQueue.invokeLater(eventsDispatcher);
        timer.start();
    }

    public void onBattlePaused() {
        timer.stop();
        isPaused.set(true);
        EventQueue.invokeLater(eventsDispatcher);
    }

    public void onRoundStarted() {
        EventQueue.invokeLater(eventsDispatcher);
    }

    public void onRoundEnded() {
        EventQueue.invokeLater(eventsDispatcher);
    }

    public void onTurnEnded(BattleSnapshot battleSnapshot) {
        snapshot.set(battleSnapshot);
    }


    private class EventsDispatcher implements Runnable {
        public void run(){
            if (isRunning.get()){
                battleView.paintBattle(snapshot.get());
            }
            else{
                battleView.paintRobocodeLogo();
            }
        }
    }

    private class TimerDispatcher implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            BattleSnapshot s=snapshot.get();
            if (lastSnapshot!=s){
                lastSnapshot=s;
                battleView.paintBattle(s);
                battleView.invalidate();
            }
        }
    }
}
