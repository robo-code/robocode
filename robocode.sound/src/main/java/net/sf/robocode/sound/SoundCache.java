/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.sound;


import net.sf.robocode.io.Logger;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * SoundCache maintains a table of sound clips. More than one instance of the same
 * sample can be stored, so a given sound effect can be played more than once
 * symultaneously.
 *
 * @author Luis Crespo (original)
 * @author Flemming N. Larsen (contributor)
 */
class SoundCache {

	/**
	 * Table containing all sound clips
	 */
	private final Map<Object, ClipClones> soundTable;

	/**
	 * Mixer used for creating clip instances
	 */
	private final Mixer mixer;

	/**
	 * Holds data, length and format for a given sound effect, which can be used to create
	 * multiple instances of the same clip.
	 */
	private static class SoundData {
		private final AudioFormat format;
		private final int length;
		private final byte[] byteData;

		private SoundData(AudioInputStream ais) throws IOException {
			int bytesRead, pos;

			format = ais.getFormat();
			length = (int) (ais.getFrameLength() * format.getFrameSize());
			byteData = new byte[length];
			pos = 0;

			do {
				bytesRead = ais.read(byteData, pos, length - pos);
				if (bytesRead > 0) {
					pos += bytesRead;
				}
			} while (bytesRead > 0 && pos < length);

			ais.close();
		}
	}


	/**
	 * Holds an array of clips from the same sample stream, and takes care of
	 * returning the next available clip. If all clips are active, the least
	 * recently used clip will be returned.
	 */
	private static class ClipClones {
		private final Clip[] clips;
		private int idx;

		private ClipClones(Mixer mixer, SoundData soundData, int size) throws LineUnavailableException {
			idx = 0;
			clips = new Clip[size];

			DataLine.Info info = new DataLine.Info(Clip.class, soundData.format);

			if (!AudioSystem.isLineSupported(info)) {
				throw new LineUnavailableException("Required data line is not supported by the audio system");
			}

			for (int i = 0; i < size; i++) {
				clips[i] = (Clip) mixer.getLine(info);
				clips[i].open(soundData.format, soundData.byteData, 0, soundData.length);
			}
		}

		private void dispose() {
			for (Clip c : clips) {
				c.close();
			}
		}

		private Clip next() {
			Clip c = clips[idx];

			idx = (idx + 1) % clips.length;
			c.stop();
			c.setFramePosition(0);
			return c;
		}
	}

	/**
	 * Constructs a sound cache to hold sound clips that is created based on the
	 * specified mixer.
	 *
	 * @param mixer the mixer to be used for creating the clip instances
	 */
	public SoundCache(Mixer mixer) {
		this.mixer = mixer;
		soundTable = new HashMap<Object, ClipClones>();
	}

	/**
	 * Adds a number of clip clones for a given resource holding the audio data.
	 * If there is any error, the method returns silently, and clip instances will
	 * not be found later for the provided key.
	 *
	 * @param key		  the key to be used for later retrieval of the sound
	 * @param resourceName the resource holding the audio data
	 * @param numClones	the number of copies of the clip to be created
	 */
	public void addSound(Object key, String resourceName, int numClones) {
		if (mixer == null || resourceName == null || (resourceName.trim().length() == 0)) {
			return;
		}

		SoundData data = createSoundData(resourceName);

		if (data == null) {
			return;
		}

		ClipClones clones;

		try {
			clones = new ClipClones(mixer, data, numClones);
			soundTable.put(key, clones);
		} catch (LineUnavailableException e) {
			Logger.logError(
					"The audio mixer " + mixer.getMixerInfo().getName()
					+ " does not support the audio format of the sound clip: " + resourceName);
		}
	}

	/**
	 * Creates an instance of SoundData, to be used later for creating the clip clones.
	 *
	 * @param resourceName the name of the resource holding the audio data
	 * @return the newly created sound data
	 */
	private SoundData createSoundData(String resourceName) {
		SoundData data;
		URL url = SoundCache.class.getResource(resourceName);

		if (url == null) {
			Logger.logError("Could not load sound because of invalid resource name: " + resourceName);
			return null;
		}
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(url);

			data = new SoundData(ais);
		} catch (Exception e) {
			Logger.logError("Error while reading sound from resource: " + resourceName, e);
			data = null;
		}
		return data;
	}

	/**
	 * Gets the next available clip instance of a given sound. If all clips for that
	 * sound are active (playing), the least recently used will be returned.
	 *
	 * @param key the key that was used when adding the sound
	 * @return a clip instance ready to be played through Clip.start()
	 */
	public Clip getSound(Object key) {
		ClipClones clones = soundTable.get(key);

		if (clones == null) {
			return null;
		}
		return clones.next();
	}

	/**
	 * Removes all clip copies of a given sound, closing all its dependent resources
	 *
	 * @param key the key that was used when adding the sound
	 */
	public void removeSound(Object key) {
		ClipClones clones = soundTable.get(key);

		if (clones == null) {
			return;
		}
		clones.dispose();

		soundTable.remove(key);
	}

	/**
	 * Empties all clips from the sound cache
	 */
	public void clear() {
		for (ClipClones clones : soundTable.values()) {
			clones.dispose();
		}
		soundTable.clear();
	}
}
