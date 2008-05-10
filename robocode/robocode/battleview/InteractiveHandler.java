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
import robocode.battlefield.BattleField;
import robocode.manager.RobocodeManager;

import java.awt.*;
import java.awt.event.*;
import static java.lang.Math.min;

/**
 * @author Pavel Savara (original)
 */
public final class InteractiveHandler implements KeyEventDispatcher, MouseListener, MouseMotionListener, MouseWheelListener {
    private RobocodeManager manager;

    public InteractiveHandler(RobocodeManager manager) {
        this.manager = manager;
    }

    public boolean dispatchKeyEvent(KeyEvent e) {
        Battle battle = manager.getBattleManager().getBattle();
        
        if (battle != null) {
            switch (e.getID()) {
            case KeyEvent.KEY_TYPED:
                battle.keyTyped(e);
                break;

            case KeyEvent.KEY_PRESSED:
                battle.keyPressed(e);
                break;

            case KeyEvent.KEY_RELEASED:
                battle.keyReleased(e);
                break;
            }
        }
        return false;
    }

    public void mouseClicked(MouseEvent e) {
        Battle battle = manager.getBattleManager().getBattle();

        if (battle != null) {
            battle.mouseClicked(mirroredMouseEvent(e));
        }
    }

    public void mouseEntered(MouseEvent e) {
        Battle battle = manager.getBattleManager().getBattle();

        if (battle != null) {
            battle.mouseEntered(mirroredMouseEvent(e));
        }
    }

    public void mouseExited(MouseEvent e) {
        Battle battle = manager.getBattleManager().getBattle();

        if (battle != null) {
            battle.mouseExited(mirroredMouseEvent(e));
        }
    }

    public void mousePressed(MouseEvent e) {
        Battle battle = manager.getBattleManager().getBattle();

        if (battle != null) {
            battle.mousePressed(mirroredMouseEvent(e));
        }
    }

    public void mouseReleased(MouseEvent e) {
        Battle battle = manager.getBattleManager().getBattle();

        if (battle != null) {
            battle.mouseReleased(mirroredMouseEvent(e));
        }
    }

    public void mouseMoved(MouseEvent e) {
        Battle battle = manager.getBattleManager().getBattle();

        if (battle != null) {
            battle.mouseMoved(mirroredMouseEvent(e));
        }
    }

    public void mouseDragged(MouseEvent e) {
        Battle battle = manager.getBattleManager().getBattle();

        if (battle != null) {
            battle.mouseDragged(mirroredMouseEvent(e));
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        Battle battle = manager.getBattleManager().getBattle();

        if (battle != null) {
            battle.mouseWheelMoved(mirroredMouseWheelEvent(e));
        }
    }

    private MouseEvent mirroredMouseEvent(final MouseEvent e) {

        double scale;
        BattleField battleField=manager.getBattleManager().getBattle().getBattleField();
        BattleView battleView =manager.getWindowManager().getRobocodeFrame().getBattleView();  

        int vWidth = battleView.getWidth();
        int fWidth = battleField.getWidth();
        int vHeight = battleView.getHeight();
        int fHeight = battleField.getHeight();

        if (vWidth < fWidth || vHeight < fHeight) {
            scale = min((double) vWidth / fWidth,
                    (double) fHeight / fHeight);
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
        BattleField battleField=manager.getBattleManager().getBattle().getBattleField();
        BattleView battleView =manager.getWindowManager().getRobocodeFrame().getBattleView();

        int vWidth = battleView.getWidth();
        int fWidth = battleField.getWidth();
        int vHeight = battleView.getHeight();
        int fHeight = battleField.getHeight();

        if (vWidth < fWidth || vHeight < fHeight) {
            scale = min((double) vWidth / fWidth,
                    (double) fHeight / fHeight);
        } else {
            scale = 1;
        }

        double dx = (vWidth - scale * fWidth) / 2;
        double dy = (fHeight - scale * fHeight) / 2;

        int x = (int) ((e.getX() - dx) / scale + 0.5);
        int y = (int) (fHeight - (e.getY() - dy) / scale + 0.5);

        return new MouseWheelEvent(SafeComponent.getSafeEventComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
                e.getClickCount(), e.isPopupTrigger(), e.getScrollType(), e.getScrollAmount(), e.getWheelRotation());
    }
}
