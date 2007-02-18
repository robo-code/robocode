/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Luis Crespo
 *     - Initial API and implementation
 *     Flemming N. Larsen
 *     - Integration into Robocode regarding controlling the sound from the Sound
 *       Options and Command Line
 *     - Bugfix: When enabling sounds on-the-fly when it was originally disabled,
 *       the PlaySound caused a NullPointerException because the sounds field had
 *       not been intialized yet. Therefore a getSounds() factory methods has
 *       been added which allocated the SoundCache instance and initializes the
 *       SoundManager if the sounds field is null
 *     - The resources for the sound effects are now loaded from the properties
 *       file
 *     - Added support for playing and stopping music
 *     - The init() method was replaced by a getMixer() factory method
 *     - Extended playSound() to handle loops, and also checking if volume and/or
 *       panning are supported before adjusting these
 *     Titus Chen:
 *     - Slight optimization with pan calculation in playBulletSound()
 *******************************************************************************/
package robocode.sound;


import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;

import robocode.manager.RobocodeManager;
import robocode.manager.RobocodeProperties;
import robocode.peer.BulletPeer;
import robocode.peer.RobotPeer;


/**
 * The sound manager is responsible of keeping a table of sound effects and
 * play the appropriate sound for each bullet or robot event that is supposed
 * to make any noise.
 *
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (contributor)
 * @author Titus Chen (contributor)
 */
public class SoundManager {

	// Cache containing sound clips
	private SoundCache sounds;

	// Access to properties
	private RobocodeProperties properties;

	/**
	 * Constructs a new sound manager.
	 *
	 * @param manager the Robocode manager
	 */
	public SoundManager(RobocodeManager manager) {
		properties = manager.getProperties();
	}

	/**
	 * Returns the current mixer selected from the Robocode properties.
	 *
	 * @return the current Mixer instance
	 */
	public Mixer getMixer() {
		return findMixer(properties.getOptionsSoundMixer());
	}

	/**
	 * Returns the cache containing sound clips.
	 *
	 * @return a SoundCache instance
	 */
	private SoundCache getSounds() {
		if (sounds == null) {
			sounds = new SoundCache(getMixer());

			// Sound effects
			sounds.addSound("gunshot", properties.getFileGunshotSfx(), 5);
			sounds.addSound("robot death", properties.getRobotDeathSfx(), 3);
			sounds.addSound("bullet hits robot", properties.getBulletHitsRobotSfx(), 3);
			sounds.addSound("bullet hits bullet", properties.getBulletHitsBulletSfx(), 2);
			sounds.addSound("robot collision", properties.getRobotCollisionSfx(), 2);
			sounds.addSound("wall collision", properties.getWallCollisionSfx(), 2);

			// Music
			sounds.addSound("theme", properties.getFileThemeMusic(), 1);
			sounds.addSound("background", properties.getFileBackgroundMusic(), 1);
			sounds.addSound("endOfBattle", properties.getFileEndOfBattleMusic(), 1);
		}
		return sounds;
	}

	/**
	 * Iterates over the available mixers, looking for the one that matches a given
	 * class name.
	 *
	 * @param mixerClassName the class name of the mixer to be used.
	 * @return the requested mixer, if found. Otherwise, it returns null.
	 */
	private Mixer findMixer(String mixerClassName) {
		for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
			Mixer m = AudioSystem.getMixer(mi);

			if (m.getClass().getSimpleName().equals(mixerClassName)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * Performs shutdown, by liberating the sound table
	 */
	public void dispose() {
		if (sounds != null) { // Do not call getSounds()!
			sounds.clear();
		}
	}

	/**
	 * Plays a specific sound at a given volume, panning and loop count
	 *
	 * @param key the sound name, as stored in the sound table
	 * @param pan panning to be used (-1=left, 0=middle, +1=right)
	 * @param volume volume to be used, from 0 to 1
	 * @param loop the number of times to loop the sound
	 */
	private void playSound(Object key, float pan, float volume, int loop) {
		Clip c = getSounds().getSound(key);

		if (c == null) {
			return;
		}

		if (properties.getOptionsSoundEnableMixerPan() && c.isControlSupported(FloatControl.Type.PAN)) {
			FloatControl panCtrl = (FloatControl) c.getControl(FloatControl.Type.PAN);

			panCtrl.setValue(pan);
		}
		if (properties.getOptionsSoundEnableMixerVolume() && c.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
			FloatControl volCtrl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);

			float min = volCtrl.getMinimum() / 4;

			if (volume != 1) {
				volCtrl.setValue(min * (1 - volume));
			}
		}
		c.loop(loop);
	}

	/**
	 * Plays a specific sound at a given panning with max. volume and without looping.
	 *
	 * @param key the sound name, as stored in the sound table
	 * @param pan panning to be used (-1=left, 0=middle, +1=right)
	 */
	private void playSound(Object key, float pan) {
		playSound(key, pan, 1, 0);
	}

	/**
	 * Plays a specific piece of music with a given loop count with no panning and
	 * max. volume.
	 *
	 * @param key the sound name, as stored in the sound table
	 * @param loop the number of times to loop the music
	 */
	private void playMusic(Object key, int loop) {
		playSound(key, 0, 1, loop);
	}

	/**
	 * Plays a bullet sound depending on the bullet's state
	 *
	 * @param bp the bullet peer
	 */
	public void playBulletSound(BulletPeer bp) {
		float pan = 0;

		if (properties.getOptionsSoundEnableMixerPan()) {
			pan = calcPan((float) bp.getX(), bp.getBattleField().getWidth());
		}
		switch (bp.getState()) {
		case BulletPeer.STATE_SHOT:
			if (properties.getOptionsSoundEnableGunshot()) {
				playSound("gunshot", pan, calcBulletVolume(bp), 0);
			}
			break;

		case BulletPeer.STATE_HIT_VICTIM:
			if (properties.getOptionsSoundEnableBulletHit()) {
				playSound("bullet hits robot", pan);
			}
			break;

		case BulletPeer.STATE_HIT_BULLET:
			if (properties.getOptionsSoundEnableBulletHit()) {
				playSound("bullet hits bullet", pan);
			}
			break;

		case BulletPeer.STATE_HIT_WALL:
			// Currently, no sound
			break;

		case BulletPeer.STATE_EXPLODED:
			if (properties.getOptionsSoundEnableRobotDeath()) {
				playSound("robot death", pan);
			}
			break;
		}
	}

	/**
	 * Plays a robot sound depending on the robot's state
	 *
	 * @param rp the robot peer
	 */
	public void playRobotSound(RobotPeer rp) {
		float pan = 0;

		if (properties.getOptionsSoundEnableMixerPan()) {
			pan = calcPan((float) rp.getX(), rp.getBattle().getBattleField().getWidth());
		}
		switch (rp.getState()) {
		case RobotPeer.STATE_HIT_ROBOT:
			if (properties.getOptionsSoundEnableRobotCollision()) {
				playSound("robot collision", pan);
			}
			break;

		case RobotPeer.STATE_HIT_WALL:
			if (properties.getOptionsSoundEnableWallCollision()) {
				playSound("wall collision", pan);
			}
			break;
		}
	}

	/**
	 * Plays the theme music once.
	 */
	public void playThemeMusic() {
		playMusic("theme", 0);
	}

	/**
	 * Plays the background music, which is looping forever until stopped.
	 */
	public void playBackgroundMusic() {
		playMusic("background", -1);
	}

	/**
	 * Stops the background music.
	 */
	public void stopBackgroundMusic() {
		Clip c = getSounds().getSound("background");

		if (c != null) {
			c.stop();
		}
	}

	/**
	 * Plays the end of battle music once.
	 */
	public void playEndOfBattleMusic() {
		playMusic("endOfBattle", 0);
	}

	/**
	 * Determines pan based on the relative position to the battlefield's width
	 *
	 * @param x the bullet or robot position
	 * @param width the battlefield's width
	 * @return the panning value, ranging from -1 to +1
	 */
	private float calcPan(float x, float width) {
		float semiWidth = width / 2;

		return (x - semiWidth) / semiWidth;
	}

	/**
	 * Determines volume based on the bullets's energy
	 *
	 * @param bp the bullet peer
	 * @return the volume value, ranging from 0 to 1
	 */
	private float calcBulletVolume(BulletPeer bp) {
		return (float) (bp.getPower() / robocode.Rules.MAX_BULLET_POWER);
	}
}
