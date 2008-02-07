// ****************************************************************************
// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html
// 
// Contributors:
// Pavel Savara
// - Initial implementation
// *****************************************************************************

using System.Collections.Generic;
using System.Drawing;
using nrobocodeui.gfx;
using nrobocodeui.manager;
using robocode.ui;

namespace nrobocodeui.manager
{
    public class ImageManager : LoadableManagerBase, IImageManager
    {
        private Dictionary<java.awt.Color, Bitmap> robotBodyImageCache;
        private Dictionary<java.awt.Color, Bitmap> robotGunImageCache;
        private Dictionary<java.awt.Color, Bitmap> robotRadarImageCache;

        public void initialize()
        {
            robotBodyImageCache = new Dictionary<java.awt.Color, Bitmap>();
            robotGunImageCache = new Dictionary<java.awt.Color, Bitmap>();
            robotRadarImageCache = new Dictionary<java.awt.Color, Bitmap>();
        }

        public Bitmap getColoredBodyRenderImage(java.awt.Color color)
        {
            return GetImage(color, resources.images.body, robotBodyImageCache);
        }

        public Bitmap getColoredGunRenderImage(java.awt.Color color)
        {
            return GetImage(color, resources.images.turret, robotGunImageCache);
        }

        public Bitmap getColoredRadarRenderImage(java.awt.Color color)
        {
            return GetImage(color, resources.images.radar, robotRadarImageCache);
        }

        private static Bitmap GetImage(java.awt.Color color, Bitmap imgSrc, Dictionary<java.awt.Color, Bitmap> cache)
        {
            if (color == null)
            {
                return new Bitmap(imgSrc);
            }

            Bitmap img;
            if (cache.ContainsKey(color))
            {
                img = cache[color];
            }
            else
            {
                img = createColouredRobotImage(imgSrc, color);
                cache.Add(color, img);
            }
            return img;
        }

        private static Bitmap createColouredRobotImage(Bitmap img, java.awt.Color color)
        {
            if (color == null || img == null)
                return img;

            Color argb = Color.FromArgb(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
            ColorFilter cf = new ColorFilter(argb);

            return cf.ChangePrimaryColor(img);
        }
    }
}
