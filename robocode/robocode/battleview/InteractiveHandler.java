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
package robocode.battleview;


import robocode.battle.Battle;
import robocode.battle.BattleProperties;
import robocode.manager.RobocodeManager;
import robocode.peer.RobotPeer;
import robocode.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import static java.lang.Math.min;


/**
 * @author Pavel Savara (original)
 */
public final class InteractiveHandler implements KeyEventDispatcher, MouseListener, MouseMotionListener, MouseWheelListener {
	private RobocodeManager manager;

	public InteractiveHandler(RobocodeManager manager) {
		this.manager = manager;
	}

	public boolean dispatchKeyEvent(java.awt.event.KeyEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {

			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					switch (e.getID()) {
					case KeyEvent.KEY_TYPED:
						robotPeer.onInteractiveEvent(new KeyTypedEvent(cloneKeyEvent(e)));
						break;

					case KeyEvent.KEY_PRESSED:
						robotPeer.onInteractiveEvent(new KeyPressedEvent(cloneKeyEvent(e)));
						break;

					case KeyEvent.KEY_RELEASED:
						robotPeer.onInteractiveEvent(new KeyReleasedEvent(cloneKeyEvent(e)));
						break;
					}
				}
			}
		}
		return false;
	}

	public void mouseClicked(MouseEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseClickedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseEnteredEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseExited(MouseEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseExitedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MousePressedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseReleasedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseMovedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseDraggedEvent(mirroredMouseEvent(e)));
				}
			}
		}
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		Battle battle = manager.getBattleManager().getBattle();

		if (battle != null && battle.isRunning()) {
			for (RobotPeer robotPeer : battle.getRobots()) {
				if (robotPeer.isAlive() && robotPeer.isInteractiveRobot()) {
					robotPeer.onInteractiveEvent(new MouseWheelMovedEvent(mirroredMouseWheelEvent(e)));
				}
			}
		}
	}

	public static KeyEvent cloneKeyEvent(final KeyEvent e) {
		return new KeyEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
				e.getKeyCode(), e.getKeyChar(), e.getKeyLocation());
	}

	private MouseEvent mirroredMouseEvent(final MouseEvent e) {

		double scale;
		BattleProperties battleProps = manager.getBattleManager().getBattleProperties();
		Battle3DView battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();

		int vWidth = battleView.getWidth();
		int vHeight = battleView.getHeight();
		int fWidth = battleProps.getBattlefieldWidth();
		int fHeight = battleProps.getBattlefieldHeight();

		if (vWidth < fWidth || vHeight < fHeight) {
			scale = min((double) vWidth / fWidth, (double) fHeight / fHeight);
		} else {
			scale = 1;
		}

		double dx = (vWidth - scale * fWidth) / 2;
		double dy = (fHeight - scale * fHeight) / 2;

		int x = (int) ((e.getX() - dx) / scale + 0.5);
		int y = (int) (fHeight - (e.getY() - dy) / scale + 0.5);

		return new MouseEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
				e.getClickCount(), e.isPopupTrigger(), e.getButton());
	}

	private MouseWheelEvent mirroredMouseWheelEvent(final MouseWheelEvent e) {

		double scale;
		BattleProperties battleProps = manager.getBattleManager().getBattleProperties();
		Battle3DView battleView = manager.getWindowManager().getRobocodeFrame().getBattleView();

		int vWidth = battleView.getWidth();
		int vHeight = battleView.getHeight();
		int fWidth = battleProps.getBattlefieldWidth();
		int fHeight = battleProps.getBattlefieldHeight();

		if (vWidth < fWidth || vHeight < fHeight) {
			scale = min((double) vWidth / fWidth, (double) fHeight / fHeight);
		} else {
			scale = 1;
		}

		double dx = (vWidth - scale * fWidth) / 2;
		double dy = (fHeight - scale * fHeight) / 2;

		int x = (int) ((e.getX() - dx) / scale + 0.5);
		int y = (int) (fHeight - (e.getY() - dy) / scale + 0.5);

		return new MouseWheelEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x,
				y, e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
	}
}
