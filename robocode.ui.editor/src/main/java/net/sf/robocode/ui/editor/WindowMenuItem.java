/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;


/**
 * Customized JMenuItem where each item is bound to a specific JInternalFrame,
 * so we can have a dynamic menu of open windows.
 *
 * @author Matthew Reeder (original)
 */
@SuppressWarnings("serial")
public class WindowMenuItem extends JCheckBoxMenuItem implements ActionListener {

	// Maximum number of windows that will be shown on the menu (to get the rest, you'll
	// have to open the dialog).  The number 9 is also the number of most recently used
	// files that normally show up in other applications.  The reason is so that you can
	// give them dynamic hotkeys from 1 to 9.  Otherwise, there's no reason (besides
	// avoiding taking up way too much space) to limit the size of the menu.
	public static final int WINDOW_MENU_MAX_SIZE = 9;
	// Number of static menu items before the dynamic menu (including seperators)
	public static final int PRECEDING_WINDOW_MENU_ITEMS = 3;
	// Number of static menu items after the dynamic menu (including seperators
	// and the More Windows... menu item)
	public static final int SUBSEQUENT_WINDOW_MENU_ITEMS = 1;
	// Normal max length of a window name
	public static final int MAX_WINDOW_NAME_LENGTH = 30;
	// I make one "special" menu item that isn't tied to a window. Since it has
	// similar needs for enabling/visibility and labeling, I made it the same class.
	public static final int REGULAR_WINDOW = 0, SPECIAL_MORE = 2;
	private EditWindow window;
	private JMenu parentMenu;
	private final int type;

	public WindowMenuItem(EditWindow window, JMenu parentMenu) {
		super();
		this.window = window;
		this.parentMenu = parentMenu;
		type = REGULAR_WINDOW;
		parentMenu.add(this, parentMenu.getMenuComponentCount() - SUBSEQUENT_WINDOW_MENU_ITEMS);
		addActionListener(this);
	}

	/**
	 * WindowMenuItem Constructor for "More Windows..." menu.
	 */
	public WindowMenuItem() {
		type = SPECIAL_MORE;
	}

	/**
	 * Event handler for the menu item
	 * <p>
	 * Brings the window to the front. This should be called for the "More
	 * Windows..." Item, because it doesn't make itself its own ActionListener.
	 * <p>
	 * Note that e can be null, and this menu item might not be showing (if this
	 * is called from the "More Windows" dialog).
	 */
	public void actionPerformed(ActionEvent e) {
		if (window.isIcon()) {
			try {
				window.setIcon(false);
			} catch (Throwable ignored) {}
		}
		if (window.getDesktopPane() != null) {
			window.getDesktopPane().setSelectedFrame(window);
		}
		window.toFront();
		window.grabFocus();
		try {
			window.setSelected(true);
		} catch (Throwable ignored) {}
	}

	/**
	 * Returns the label that should be used. If the menu item is supposed to be
	 * hidden, this may not be a real valid label.
	 */
	@Override
	public String getText() {
		if (type == SPECIAL_MORE) {
			Container parent = getParent();

			if (parent == null) {
				return "";
			}

			int numWindows = parent.getComponentCount() - PRECEDING_WINDOW_MENU_ITEMS - SUBSEQUENT_WINDOW_MENU_ITEMS;

			if (numWindows <= 0) {
				return "No Windows Open";
			}

			return "More Windows...";
		}
		if (window == null || parentMenu == null) {
			return "";
		}
		String text = (getIndex() + 1) + " " + getFileName();

		if (window.modified) {
			text += " *";
		}
		return text;
	}

	protected String getFileName() {
		if (window.getFileName() == null) {
			return "Untitled " + (getPrecedingNewFiles() + 1);
		}

		String name = window.getFileName();

		if (name.length() < MAX_WINDOW_NAME_LENGTH) {
			return name;
		}
		if (name.indexOf(File.separatorChar) < 0) {
			return name;
		} // If there are no separators, I can't really intelligently truncate.
		int startLength = name.indexOf(File.separatorChar, 1) + 1;
		int endLength = name.length() - name.lastIndexOf(File.separatorChar);

		if (endLength + startLength + 3 > name.length()) {
			return name;
		} // return name anyways, since we're not getting it any shorter.

		boolean change;

		do {
			change = false;
			int newEndLength = name.length() - name.lastIndexOf(File.separatorChar, name.length() - endLength - 1);

			if (newEndLength + startLength + 3 <= MAX_WINDOW_NAME_LENGTH) {
				endLength = newEndLength;
				change = true;
			}
			int newStartLength = name.indexOf(File.separatorChar, startLength + 1) + 1;

			if (endLength + startLength + 3 <= MAX_WINDOW_NAME_LENGTH) {
				startLength = newStartLength;
				change = true;
			}
		} while (change);

		return name.substring(0, startLength) + "..." + name.substring(name.length() - endLength);
	}

	/**
	 * @return how many nameless windows occur before this one in the parent.
	 */
	protected int getPrecedingNewFiles() {
		int count = 0;

		for (int i = 0; i < WINDOW_MENU_MAX_SIZE
				&& i < parentMenu.getMenuComponentCount() - PRECEDING_WINDOW_MENU_ITEMS - SUBSEQUENT_WINDOW_MENU_ITEMS
				&& parentMenu.getMenuComponent(i + PRECEDING_WINDOW_MENU_ITEMS) != this; i++) {
			if (parentMenu.getMenuComponent(i + PRECEDING_WINDOW_MENU_ITEMS) instanceof WindowMenuItem
					&& ((WindowMenuItem) parentMenu.getMenuComponent(i + PRECEDING_WINDOW_MENU_ITEMS)).window.getFileName()
							== null) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Figures out what index (from 0 to WINDOW_MENU_MAX_SIZE-1) this item is in
	 * the window menu.
	 * <p>
	 * @return -1 if this item isn't showing.
	 */
	protected int getIndex() {
		for (int i = 0; i < WINDOW_MENU_MAX_SIZE
				&& i < parentMenu.getMenuComponentCount() - PRECEDING_WINDOW_MENU_ITEMS - SUBSEQUENT_WINDOW_MENU_ITEMS; i++) {
			if (this == parentMenu.getMenuComponent(i + PRECEDING_WINDOW_MENU_ITEMS)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the character in the label that should be underlined
	 */
	@Override
	public int getDisplayedMnemonicIndex() {
		return (type == SPECIAL_MORE) ? 11 : 0;
	}

	/**
	 * Returns the keyboard mnemonic for this item, which is the virtual key
	 * code for its 1-based index.
	 */
	@Override
	public int getMnemonic() {
		return (type == SPECIAL_MORE) ? KeyEvent.VK_S : KeyEvent.VK_1 + getIndex();
	}

	/**
	 * Returns true if this item should be showing.
	 * <p>
	 * Returns false if there are more than WINDOW_MENU_MAX_SIZE items before it
	 * in the menu.
	 */
	@Override
	public boolean isVisible() {
		if (type == SPECIAL_MORE) {
			Container parent = getParent();

			if (parent == null) {
				return true;
			}
			int numWindows = parent.getComponentCount() - PRECEDING_WINDOW_MENU_ITEMS - SUBSEQUENT_WINDOW_MENU_ITEMS;

			updateSelection();
			return (numWindows <= 0) || (numWindows > WINDOW_MENU_MAX_SIZE);
		}
		updateSelection();
		return getIndex() >= 0;
	}

	/**
	 * Returns true if this item should be enabled (selectable).
	 * <p>
	 * Returns false if it is a More Windows... item and there are no windows.
	 */
	@Override
	public boolean isEnabled() {
		if (type == SPECIAL_MORE) {
			Container parent = getParent();

			if (parent == null) {
				return true;
			}
			int numWindows = parent.getComponentCount() - PRECEDING_WINDOW_MENU_ITEMS - SUBSEQUENT_WINDOW_MENU_ITEMS;

			return (numWindows > 0);
		}
		return true;
	}

	/**
	 * Determines if this menu item should currently show as "selected".
	 * <p>
	 * The item should be seleced if the window it's tied to has focus.
	 */
	@Override
	public boolean isSelected() {
		return (type != SPECIAL_MORE) && (window != null && window.getDesktopPane() != null)
				&& window.getDesktopPane().getSelectedFrame() == window;
	}

	/**
	 * Makes sure the underlying menu item knows if we're selected.
	 */
	public void updateSelection() {
		setSelected(isSelected()); // Sort of a silly thing to do...
		setEnabled(isEnabled());
	}

	/**
	 * @return the EditWindow that this menu item is tied to.
	 */
	public EditWindow getEditWindow() {
		return window;
	}

	/**
	 * Creates a string representation of this object.
	 * <p>
	 * Handy for repurposing the menu items as list items :-)
	 */
	@Override
	public String toString() {
		return (type == SPECIAL_MORE) ? "" : getFileName();
	}
}
