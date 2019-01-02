/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;


/**
 * @author Flemming N. Larsen (original)
 */
@SuppressWarnings("serial")
public class StyledDocument extends DefaultStyledDocument {

	/**
	 * Returns the element index from an offset.
	 * 
	 * @param offset
	 *            is the offset to retrieve the element index from.
	 * @return the element index >= 0.
	 */
	protected int getElementIndex(int offset) {
		return getDefaultRootElement().getElementIndex(offset);
	}

	/**
	 * Returns the end offset of an element.
	 * Notice, that the end offset is off-by-one, and hence this method wraps the Element.getEndOffset()
	 *
	 * @param element is the element to get the end offset from.
	 * @return the end offset of the element.
	 */
	protected int getEndOffset(Element element) {
		return Math.min(getLength(), element.getEndOffset());
	}

	/**
	 * Returns the child element at the given element index.
	 * 
	 * @param index
	 *            is the element index.
	 * @return the child element.
	 */
	protected Element getElement(int index) {
		return getDefaultRootElement().getElement(index);
	}

	/**
	 * Returns the child element at the given document offset.
	 * 
	 * @param offset
	 *            is the document offset.
	 * @return the child element.
	 */
	protected Element getElementFromOffset(int offset) {
		return getElement(getElementIndex(offset));
	}
}
