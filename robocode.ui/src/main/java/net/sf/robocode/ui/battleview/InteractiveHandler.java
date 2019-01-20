/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.battleview;


import net.sf.robocode.battle.BattleProperties;
import net.sf.robocode.battle.IBattleManager;
import net.sf.robocode.security.SafeComponent;
import robocode.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.*;
import static java.lang.Math.min;


/**
 * This handler is used for observing keyboard and mouse events from the battle view,
 * which must be process to the all robots interactive event handlers.
 * The mouse events y coordinates are mirrored to comply to the coordinate system
 * used in Robocode.
 *
 * @author Pavel Savara (original)
 * @author Flemming N. Larsen (contributor)
 */
public final class InteractiveHandler implements KeyEventDispatcher, MouseListener, MouseMotionListener, MouseWheelListener {
	private final IBattleManager battleManager;
	private final BattleView battleView;

	public InteractiveHandler(IBattleManager battleManager, BattleView battleView) {
		this.battleManager = battleManager;
		this.battleView = battleView;
	}

	public boolean dispatchKeyEvent(java.awt.event.KeyEvent e) {
		switch (e.getID()) {
		case KeyEvent.KEY_TYPED:
			handleInteractiveEvent(new KeyTypedEvent(cloneKeyEvent(e)));
			break;

		case KeyEvent.KEY_PRESSED:
			handleInteractiveEvent(new KeyPressedEvent(cloneKeyEvent(e)));
			break;

		case KeyEvent.KEY_RELEASED:
			handleInteractiveEvent(new KeyReleasedEvent(cloneKeyEvent(e)));
			break;
		}

		// Allow KeyboardFocusManager to take further action with regard to the KeyEvent.
		// This way the InteractiveHandler does not steal the event, but is only a keyboard observer.
		return false;
	}

	public void mouseClicked(MouseEvent e) {
		handleInteractiveEvent(new MouseClickedEvent(mirroredMouseEvent(e)));
	}

	public void mouseEntered(MouseEvent e) {
		handleInteractiveEvent(new MouseEnteredEvent(mirroredMouseEvent(e)));
	}

	public void mouseExited(MouseEvent e) {
		handleInteractiveEvent(new MouseExitedEvent(mirroredMouseEvent(e)));
	}

	public void mousePressed(MouseEvent e) {
		handleInteractiveEvent(new MousePressedEvent(mirroredMouseEvent(e)));
	}

	public void mouseReleased(MouseEvent e) {
		handleInteractiveEvent(new MouseReleasedEvent(mirroredMouseEvent(e)));
	}

	public void mouseMoved(MouseEvent e) {
		handleInteractiveEvent(new MouseMovedEvent(mirroredMouseEvent(e)));
	}

	public void mouseDragged(MouseEvent e) {
		handleInteractiveEvent(new MouseDraggedEvent(mirroredMouseEvent(e)));
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		handleInteractiveEvent(new MouseWheelMovedEvent(mirroredMouseWheelEvent(e)));
	}

	public static KeyEvent cloneKeyEvent(final KeyEvent e) {
		return new KeyEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
				e.getKeyCode(), e.getKeyChar(), e.getKeyLocation());
	}

	private void handleInteractiveEvent(robocode.Event event) {
		battleManager.sendInteractiveEvent(event);
	}

	private MouseEvent mirroredMouseEvent(final MouseEvent e) {

		double scale;
		BattleProperties battleProps = battleManager.getBattleProperties();

		int vWidth = battleView.getWidth();
		int vHeight = battleView.getHeight();
		int fWidth = battleProps.getBattlefieldWidth();
		int fHeight = battleProps.getBattlefieldHeight();

		if (vWidth < fWidth || vHeight < fHeight) {
			scale = min((double) vWidth / fWidth, (double) vHeight / fHeight);
		} else {
			scale = 1;
		}

		double dx = (vWidth - scale * fWidth) / 2;
		double dy = (vHeight - scale * fHeight) / 2;

		int x = (int) ((e.getX() - dx) / scale + 0.5);
		int y = (int) (fHeight - (e.getY() - dy) / scale + 0.5);

		return new MouseEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
				e.getClickCount(), e.isPopupTrigger(), e.getButton());
	}

	private MouseWheelEvent mirroredMouseWheelEvent(final MouseWheelEvent e) {

		double scale;
		BattleProperties battleProps = battleManager.getBattleProperties();

		int vWidth = battleView.getWidth();
		int vHeight = battleView.getHeight();
		int fWidth = battleProps.getBattlefieldWidth();
		int fHeight = battleProps.getBattlefieldHeight();

		if (vWidth < fWidth || vHeight < fHeight) {
			scale = min((double) vWidth / fWidth, (double) vHeight / fHeight);
		} else {
			scale = 1;
		}

		double dx = (vWidth - scale * fWidth) / 2;
		double dy = (vHeight - scale * fHeight) / 2;

		int x = (int) ((e.getX() - dx) / scale + 0.5);
		int y = (int) (fHeight - (e.getY() - dy) / scale + 0.5);

		return new MouseWheelEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x,
				y, e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
	}
}
