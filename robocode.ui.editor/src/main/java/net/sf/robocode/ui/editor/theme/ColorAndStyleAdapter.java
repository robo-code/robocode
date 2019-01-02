/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor.theme;


import java.awt.Color;

import net.sf.robocode.ui.editor.FontStyle;


/**
 * Adapter class for the {@link IColorAndStyleListener} interface.
 *
 * @author Flemming N. Larsen (original)
 * @since 1.8.3.0
 */
public abstract class ColorAndStyleAdapter implements IColorAndStyleListener {
	@Override
	public void colorChanged(Color newColor) {}

	@Override
	public void styleChanged(FontStyle newStyle) {}
}
