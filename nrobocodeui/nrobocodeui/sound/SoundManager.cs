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

        private List<SoundPlayer> players=new List<SoundPlayer>();

        public void stopBackgroundMusic()
        {
            StopAll();
        }

        private void StopAll()
        {
            lock(players)
            {
                foreach (SoundPlayer player in players)
                {
                    player.Stop();
                }
                players.Clear();
            }
        }

        private void Play(Stream tune)
        {
            return;
            if (players.Count>5)
                return;

            SoundPlayer player = new SoundPlayer(tune);
            player.Load();
            player.Disposed += player_Disposed;
            lock (players)
            {
                players.Add(player);
                player.Play();
            }
        }

        void player_Disposed(object sender, EventArgs e)
        {
            lock (players)
            {
                players.Remove(sender as SoundPlayer);
            }
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
