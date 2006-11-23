/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
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
 *******************************************************************************/
package robocode.sound;


import javax.sound.sampled.*;

import robocode.manager.RobocodeProperties;
import robocode.peer.BulletPeer;
import robocode.peer.RobotPeer;


/**
 * The sound manager is responsible of keeping a table of sound effects and
 * play the appropriate sound for each bullet or robot event that is supposed
 * to make any noise.
 *  
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (integration)
 */
public class SoundManager {

	private static final float MAX_BULLET_POWER = 3;

	private SoundCache sounds;
	private boolean panSupported;
	private boolean volSupported;
	private Mixer theMixer;

	private RobocodeProperties properties;

	/**
	 * Constructs a new SoundManager
	 *
	 * @param properties the Robocode properties
	 */
	public SoundManager(RobocodeProperties properties) {
		this.properties = properties;
	}

	/**
	 * Loads all required samples and performs all necessary setup
	 */
	public void init() {
		theMixer = findMixer(properties.getOptionsSoundMixer());

		panSupported = false;
		volSupported = false;

		Line.Info lineInfo = theMixer.getSourceLineInfo(new Line.Info(Clip.class))[0];

		try {
			Line line = theMixer.getLine(lineInfo);

			volSupported = line.isControlSupported(FloatControl.Type.MASTER_GAIN);
			panSupported = line.isControlSupported(FloatControl.Type.PAN);
		} catch (LineUnavailableException e) {}
	}

	/**
	 * Gets the sounds cache containing the sound clips.
	 * 
	 * @return a SoundCache instance
	 */
	private SoundCache getSounds() {
		if (sounds == null) {
			init();
			
			sounds = new SoundCache(theMixer);
			sounds.addSound("death", "/resources/sounds/explode.wav", 3);
			sounds.addSound("gun", "/resources/sounds/zap.wav", 5);
			sounds.addSound("hit", "/resources/sounds/shellhit.wav", 3);
			sounds.addSound("collision", "/resources/sounds/13831_adcbicycle_22.wav", 2);
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
		getSounds().clear();
	}
	
	/**
	 * Plays a specific sound, at a given volume and panning 
	 * 
	 * @param key the sound name, as stored in the sound table
	 * @param pan panning to be used (-1=left, 0=middle, +1=right)
	 * @param volume volume to be used, from 0 to 1
	 */
	private void playSound(Object key, float pan, float volume) {
		Clip c = getSounds().getSound(key);

		if (c == null) {
			return;
		}
		if (isPanEnabled()) {
			FloatControl panCtrl = (FloatControl) c.getControl(FloatControl.Type.PAN);

			panCtrl.setValue(pan);
		}
		if (isVolumeEnabled()) {
			FloatControl volCtrl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float min = volCtrl.getMinimum() / 4;

			if (volume != 1) {
				volCtrl.setValue(min * (1 - volume));
			}
		}
		c.start();
	}

	/**
	 * Plays a sound depending on the bullet's state
	 * 
	 * @param bp the bullet peer
	 */
	public void playBulletSound(BulletPeer bp) {
		float pan = 0, vol = 1;

		if (isPanEnabled()) {
			pan = calcPan((float) bp.getX(), bp.getBattle().getBattleField().getWidth());
		}
		switch (bp.getBulletState()) {
		case BulletPeer.BULLET_STATE_SHOT:
			if (properties.getOptionsSoundEnableGunShot()) {
				if (isVolumeEnabled()) {
					vol = calcBulletVolume(bp);
				}
				playSound("gun", pan, vol);
			}
			break;

		case BulletPeer.BULLET_STATE_HIT_VICTIM:
			if (properties.getOptionsSoundEnableBulletHit()) {
				playSound("hit", pan, vol);
			}
			break;

		case BulletPeer.BULLET_STATE_HIT_BULLET:
			break;

		case BulletPeer.BULLET_STATE_HIT_WALL:
			break;

		case BulletPeer.BULLET_STATE_EXPLODED:
			if (properties.getOptionsSoundEnableRobotDeath()) {
				playSound("death", pan, vol);
			}
			break;
		}
	}

	/**
	 * Plays a sound depending on the robot's state
	 * 
	 * @param rp the robot peer
	 */
	public void playRobotSound(RobotPeer rp) {
		float pan = 0;

		if (isPanEnabled()) {
			pan = calcPan((float) rp.getX(), rp.getBattle().getBattleField().getWidth());
		}
		switch (rp.getRobotState()) {
		case RobotPeer.ROBOT_STATE_HIT_ROBOT:
			if (properties.getOptionsSoundEnableRobotCollision()) {
				playSound("collision", pan, 1);
			}
			break;

		case RobotPeer.ROBOT_STATE_HIT_WALL:
			if (properties.getOptionsSoundEnableWallCollision()) {
				playSound("collision", pan, 1);
			}
			break;
		}
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
		return (float) bp.getPower() / MAX_BULLET_POWER;
	}

	/**
	 * Returns true if pan is enabled, i.e is supported by the mixer
	 * and enabled from the Sound Options
	 * 
	 * @return true if pan is enabled; false otherwise
	 */
	private boolean isPanEnabled() {
		return panSupported && properties.getOptionsSoundEnableMixerPan();
	}

	/**
	 * Returns true if volume is enabled, i.e is supported by the mixer
	 * and enabled from the Sound Options
	 * 
	 * @return true if volume is enabled; false otherwise
	 */
	private boolean isVolumeEnabled() {
		return volSupported && properties.getOptionsSoundEnableMixerVolume();
	}
}
