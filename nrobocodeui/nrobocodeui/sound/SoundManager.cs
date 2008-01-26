using System;
using System.Collections.Generic;
using System.IO;
using System.Media;
using System.Text;
using nrobocodeui.manager;
using robocode.peer;
using robocode.ui;

namespace nrobocodeui.sound
{
    public class SoundManager : LoadableManagerBase, ISoundManager
    {
        public SoundManager()
        {
        }

        private int count;

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

        public void playBulletSound(BulletPeer bp)
        {
            //TODO more logic
            Play(nrobocodeui.resources.sounds.zap);
        }

        public void playRobotSound(RobotPeer rp)
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
