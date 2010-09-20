using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /**
     * Interface of a bullet snapshot at a specific time instant in a battle.
     * 
     * @author Pavel Savara (original)
     * @author Flemming N. Larsen (contributor)
     *
     * @since 1.6.2
     */
    public interface IBulletSnapshot
    {
        /**
         * Returns the bullet state.
         *
         * @return the bullet state.
         */
        BulletState State { get; }

        /**
         * Returns the bullet power.
         *
         * @return the bullet power.
         */
        double Power { get; }

        /**
         * Returns the X position of the bullet.
         *
         * @return the X position of the bullet.
         */
        double X { get; }

        /**
         * Returns the Y position of the bullet.
         *
         * @return the Y position of the bullet.
         */
        double Y { get; }

        /**
         * Returns the X painting position of the bullet.
         * Note that this is not necessarily equal to the X position of the bullet, even though
         * it will be in most cases. The painting position of the bullet is needed as the bullet
         * will "stick" to its victim, but only visually. 
         *
         * @return the X painting position of the bullet.
         */
        double PaintX { get; }

        /**
         * Returns the Y painting position of the bullet.
         * Note that this is not necessarily equal to the Y position of the bullet, even though
         * it will be in most cases. The painting position of the bullet is needed as the bullet
         * will "stick" to its victim, but only visually. 
         *
         * @return the Y painting position of the bullet.
         */
        double PaintY { get; }

        /**
         * Returns the color of the bullet.
         *
         * @return a ARGB color value. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
         * 
         * @see java.awt.Color#getRGB()
         */
        int Color { get; }

        /**
         * Returns the frame number to display, i.e. when the bullet explodes.
         *
         * @return the frame number.
         */
        int Frame { get; }

        /**
         * Checks if the bullet has become an explosion.
         *
         * @return {@code true} if the bullet is an explosion; {@code false} otherwise.
         */
        bool IsExplosion { get; }

        /**
         * Returns the explosion image index, which is different depending on the type of explosion.
         * E.g. if it is a small explosion on a robot that has been hit by this bullet,
         * or a big explosion when a robot dies.
         *
         * @return the explosion image index.
         */
        int ExplosionImageIndex { get; }

        /**
         * Returns the ID of the bullet used for identifying the bullet in a collection of bullets.
         *
         * @return the ID of the bullet
         */
        int BulletId { get; }
    }
}
