/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.core;


/**
 * @author Pavel Savara (original)
 */
public abstract class ContainerBase {
	public static ContainerBase instance;

	protected abstract <T> T getBaseComponent(java.lang.Class<T> tClass);

	public static <T> T getComponent(java.lang.Class<T> tClass) {
		return instance == null ? null : instance.getBaseComponent(tClass);
	}
}
