// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Port from Java version
// *****************************************************************************

using System;
using System.Drawing;
using System.Windows.Forms;
using nrobocode.robot;
using nrobocode;

[assembly: Description("A sample robot\n\n This is a robot that is controlled using the arrow keys and mouse only")]
[assembly: WebPage("http://robocode.sourceforge.net/")]
[assembly: Version("1.3.4")]
[assembly: SourceIncluded(true)]
[assembly: AuthorName("Flemming N. Larsen, Pavel Savara")]
[assembly: Name(typeof(sample.Interactive))]

namespace sample
{
    public class Interactive : Robot
    {
        // Move direction: 1 = move forward, 0 = stand still, -1 = move backward
        int moveDirection;

        // Turn direction: 1 = turn right, 0 = no turning, -1 = turn left
        int turnDirection;

        // Amount of pixels/units to move
        double moveAmount;

        // The coordinate of the aim (x,y)
        int aimX, aimY;

        // Fire power, where 0 = don't fire
        int firePower;

        // Called when the robot must run
        public override void Run()
        {

            // Sets the colors of the robot
            // body = black, gun = white, radar = red
            SetColors(Color.Black, Color.White, Color.Red);

            // Loop forever
            for (; ; )
            {
                /*
                // Sets the robot to move forward, backward or stop moving depending
                // on the move direction and amount of pixels to move
                SetAhead(moveAmount * moveDirection);

                // Decrement the amount of pixels to move until we reach 0 pixels
                // This way the robot will automatically stop if the mouse wheel
                // has stopped it's rotation
                moveAmount = Math.max(0, moveAmount - 1);

                // Sets the robot to turn right or turn left (at maximum speed) or
                // stop turning depending on the turn direction 
                setTurnRight(45 * turnDirection); // degrees

                // Turns the gun toward the current aim coordinate (x,y) controlled by
                // the current mouse coordinate
                double angle = normalAbsoluteAngle(Math.atan2(aimX - getX(), aimY - getY()));

                setTurnGunRightRadians(normalRelativeAngle(angle - getGunHeadingRadians()));

                // Fire the gun with the specified fire power, unless the fire power = 0
                if (firePower > 0)
                {
                    setFire(firePower);
                }

                // Execute all pending set-statements
                execute();

                // Next turn is processed in this loop..
                */
                Wait();
            }
        }

        // Called when a key has been pressed
        public override void OnKeyPressed(KeyEventArgs e)
        {
            switch (e.KeyCode)
            {
                case Keys.Up:
                    // Arrow up key: move direction = forward (infinitely)
                    moveDirection = 1;
                    moveAmount = Double.PositiveInfinity;
                    break;

                case Keys.Down:
                    // Arrow down key: move direction = backward (infinitely)
                    moveDirection = -1;
                    moveAmount = Double.PositiveInfinity;
                    break;

                case Keys.Right:
                    // Arrow right key: turn direction = right
                    turnDirection = 1;
                    break;

                case Keys.Left:
                    // Arrow left key: turn direction = left
                    turnDirection = -1;
                    break;
            }
        }

        // Called when a key has been released (after being pressed)
        public override void OnKeyReleased(KeyEventArgs e)
        {
            switch (e.KeyCode)
            {
                case Keys.Up:
                case Keys.Down:
                    // Arrow up and down keys: move direction = stand still 
                    moveDirection = 0;
                    break;

                case Keys.Right:
                case Keys.Left:
                    // Arrow right and left keys: turn direction = stop turning
                    turnDirection = 0;
                    break;
            }
        }

        // Called when the mouse wheel is rotated
        public override void OnMouseWheelMoved(MouseEventArgs e)
        {
            // If the wheel rotation is negative it means that it is moved forward.
            // Set move direction = forward, if wheel is moved forward.
            // Otherwise, set move direction = backward
            moveDirection = (e.Delta < 0) ? 1 : -1;

            // Set the amount to move = absolute wheel rotation amount * 5 (speed)
            // Here 5 means 5 pixels per wheel rotation step. The higher value, the
            // more speed
            moveAmount += Math.Abs(e.Delta) * 5;
        }

        // Called when the mouse has been moved
        public override void OnMouseMoved(MouseEventArgs e)
        {
            // Set the aim coordinate = the mouse pointer coordinate
            aimX = e.X;
            aimY = e.Y;
        }

        // Called when a mouse button has been pressed
        public override void OnMousePressed(MouseEventArgs e)
        {
            switch (e.Button)
            {
                case MouseButtons.Right:
                    firePower = 3;
                    SetBulletColor(Color.Red);
                    break;
                case MouseButtons.Middle:
                    firePower = 2;
                    SetBulletColor(Color.Orange);
                    break;
                default:
                    firePower = 1;
                    SetBulletColor(Color.Yellow);
                    break;
            }
        }

        // Called when a mouse button has been released (after being pressed)
        public override void OnMouseReleased(MouseEventArgs e)
        {
            // Fire power = 0, which means "don't fire"
            firePower = 0;
        }
    }
}
