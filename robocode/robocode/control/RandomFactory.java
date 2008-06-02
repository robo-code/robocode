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
package robocode.control;

import static robocode.io.Logger.logError;

import java.util.Random;
import java.lang.reflect.Field;

/**
 * @author Pavel Savara (original)
 */
public class RandomFactory {
    private static Random randomNumberGenerator;
    public static Random getRandom() {
        if (randomNumberGenerator==null){
            try {
                Math.random();
                final Field field = Math.class.getDeclaredField("randomNumberGenerator");
                field.setAccessible(true);
                randomNumberGenerator=(Random)field.get(null);
                field.setAccessible(false);
            } catch (NoSuchFieldException e) {
                logError(e);
                randomNumberGenerator=new Random();
            } catch (IllegalAccessException e) {
                logError(e);
                randomNumberGenerator=new Random();
            }
        }
        return randomNumberGenerator;
    }

    public static void setRandom(Random random) {
        randomNumberGenerator = random;
        try {
            Math.random();
            final Field field = Math.class.getDeclaredField("randomNumberGenerator");
            field.setAccessible(true);
            field.set(null, randomNumberGenerator);
            field.setAccessible(false);
        } catch (NoSuchFieldException e) {
            logError(e);
        } catch (IllegalAccessException e) {
            logError(e);
        }
        
        //TODO ZAMO using Robot classloader inject seed also for all instances being created by robots
    }

    public static void resetDeterministic(long seed){
        setRandom(new Random(seed));
    }
}
