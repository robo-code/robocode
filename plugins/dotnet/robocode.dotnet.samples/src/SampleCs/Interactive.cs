/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Drawing;
using Robocode;
using Robocode.Util;

namespace SampleCs
{
    /// <summary>
    /// Interactive - a sample robot by Flemming N. Larsen.
    /// <p/>
    /// This is a robot that is controlled using the arrow keys and mouse only.
    /// <p/>
    /// Keys:
    /// - Arrow up:    Move forward
    /// - Arrow down:  Move backward
    /// - Arrow right: Turn right
    /// - Arrow left:  Turn left
    /// Mouse:
    /// - Moving:      Moves the aim, which the gun will follow
    /// - Wheel up:    Move forward
    /// - Wheel down:  Move backward
    /// - Button 1:    Fire a bullet with power = 1
    /// - Button 2:    Fire a bullet with power = 2
    /// - Button 3:    Fire a bullet with power = 3
    /// <p/>
    /// The bullet color depends on the Fire power:
    /// - Power = 1:   Yellow
    /// - Power = 2:   Orange
    /// - Power = 3:   Red
    /// <p/>
    /// Note that the robot will continue firing as long as the mouse button is
    /// pressed down.
    /// <p/>
    /// By enabling the "Paint" button on the robot console window for this robot,
    /// a cross hair will be painted for the robots current aim (controlled by the
    /// mouse).
    /// </summary>
    public class Interactive : AdvancedRobot
    {
        // Move direction: 1 = move forward, 0 = stand still, -1 = move backward
        private int moveDirection;

        // Turn direction: 1 = turn right, 0 = no turning, -1 = turn left
        private int turnDirection;

        // Amount of pixels/units to move
        private double moveAmount;

        // The coordinate of the aim (x,y)
        private int aimX, aimY;

        // Fire power, where 0 = don't Fire
        private int firePower;

        // Called when the robot must run
        public override void Run()
        {
            // Sets the colors of the robot
            // body = black, gun = white, radar = red
            SetColors(Color.Black, Color.White, Color.Red);

            // Loop forever
            for (;;)
            {
                // Sets the robot to move forward, backward or stop moving depending
                // on the move direction and amount of pixels to move
                SetAhead(moveAmount*moveDirection);

                // Decrement the amount of pixels to move until we reach 0 pixels
                // This way the robot will automatically stop if the mouse wheel
                // has stopped it's rotation
                moveAmount = Math.Max(0, moveAmount - 1);

                // Sets the robot to turn right or turn left (at maximum speed) or
                // stop turning depending on the turn direction
                SetTurnRight(45*turnDirection); // degrees

                // Turns the gun toward the current aim coordinate (x,y) controlled by
                // the current mouse coordinate
                double angle = Utils.NormalAbsoluteAngle(Math.Atan2(aimX - X, aimY - Y));

                SetTurnGunRightRadians(Utils.NormalRelativeAngle(angle - GunHeadingRadians));

                // Fire the gun with the specified Fire power, unless the Fire power = 0
                if (firePower > 0)
                {
                    SetFire(firePower);
                }

                // Execute all pending set-statements
                Execute();

                // Next turn is processed in this loop..
            }
        }

        // Called when a key has been pressed
        public override void OnKeyPressed(KeyEvent e)
        {
            switch (e.KeyCode)
            {
                case Keys.VK_UP:
                    // Arrow up key: move direction = forward (infinitely)
                    moveDirection = 1;
                    moveAmount = Double.PositiveInfinity;
                    break;

                case Keys.VK_DOWN:
                    // Arrow down key: move direction = backward (infinitely)
                    moveDirection = -1;
                    moveAmount = Double.PositiveInfinity;
                    break;

                case Keys.VK_RIGHT:
                    // Arrow right key: turn direction = right
                    turnDirection = 1;
                    break;

                case Keys.VK_LEFT:
                    // Arrow left key: turn direction = left
                    turnDirection = -1;
                    break;
            }
        }

        // Called when a key has been released (after being pressed)
        public override void OnKeyReleased(KeyEvent e)
        {
            switch (e.KeyCode)
            {
                case Keys.VK_UP:
                case Keys.VK_DOWN:
                    // Arrow up and down keys: move direction = stand still
                    moveDirection = 0;
                    moveAmount = 0;
                    break;

                case Keys.VK_RIGHT:
                case Keys.VK_LEFT:
                    // Arrow right and left keys: turn direction = stop turning
                    turnDirection = 0;
                    break;
            }
        }

        // Called when the mouse wheel is rotated
        public override void OnMouseWheelMoved(MouseWheelMovedEvent e)
        {
            // If the wheel rotation is negative it means that it is moved forward.
            // Set move direction = forward, if wheel is moved forward.
            // Otherwise, set move direction = backward
            moveDirection = (e.WheelRotation < 0) ? 1 : -1;

            // Set the amount to move = absolute wheel rotation amount * 5 (speed)
            // Here 5 means 5 pixels per wheel rotation step. The higher value, the
            // more speed
            moveAmount += Math.Abs(e.WheelRotation)*5;
        }

        // Called when the mouse has been moved
        public override void OnMouseMoved(MouseEvent e)
        {
            // Set the aim coordinate = the mouse pointer coordinate
            aimX = e.X;
            aimY = e.Y;
        }

        // Called when a mouse button has been pressed
        public override void OnMousePressed(MouseEvent e)
        {
            if (e.Button == Keys.BUTTON3)
            {
                // Button 3: Fire power = 3 energy points, bullet color = red
                firePower = 3;
                BulletColor = (Color.Red);
            }
            else if (e.Button == Keys.BUTTON2)
            {
                // Button 2: Fire power = 2 energy points, bullet color = orange
                firePower = 2;
                BulletColor = (Color.Orange);
            }
            else
            {
                // Button 1 or unknown button:
                // Fire power = 1 energy points, bullet color = yellow
                firePower = 1;
                BulletColor = (Color.Yellow);
            }
        }

        // Called when a mouse button has been released (after being pressed)
        public override void OnMouseReleased(MouseEvent e)
        {
            // Fire power = 0, which means "don't Fire"
            firePower = 0;
        }

        // Called in order to paint graphics for this robot.
        // "Paint" button on the robot console window for this robot must be
        // enabled in order to see the paintings.
        public override void OnPaint(IGraphics g)
        {
            // Draw a red cross hair with the center at the current aim
            // coordinate (x,y)
            g.DrawEllipse(Pens.Red, aimX - 15, aimY - 15, 30, 30);
            g.DrawLine(Pens.Red, aimX, aimY - 4, aimX, aimY + 4);
            g.DrawLine(Pens.Red, aimX - 4, aimY, aimX + 4, aimY);
        }
    }
}