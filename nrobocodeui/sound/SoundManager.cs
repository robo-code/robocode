// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

using System;
using System.Collections.Generic;
using System.IO;
using System.Media;
using System.Text;
using nrobocodeui.manager;
using robocode.peer;
using robocode.peer.proxies;
using robocode.ui;

namespace nrobocodeui.sound
{
    public class SoundManager : LoadableManagerBase, ISoundManager
    {
        public SoundManager()
        {
        }

        //private int count;

        public void stopBackgroundMusic()
        {
            //StopAll();
        }

        private void StopAll()
        {
           
        }

        private void Play(Stream tune)
        {
            /*
            //if (count > 50) return;

            SoundPlayer player = new SoundPlayer(tune);
            player.Load();
            lock (this)
            {
                count++;
                player.PlaySync();
            }
             */
        }

        public void playBulletSound(IBattleBulletProxy bp, float w)
        {
            //TODO more logic
            Play(nrobocodeui.resources.sounds.zap);
        }

        public void playRobotSound(IBattleRobotProxy rp)
        {
            //TODO more logic
            Play(nrobocodeui.resources.sounds.crash);
        }

        public void playEndOfBattleMusic()
        {
            //TODO
            //StopAll();
        }

        public void playBackgroundMusic()
        {
            //TODO
        }

        public void playThemeMusic()
        {
            //TODO
        }
    }
}
