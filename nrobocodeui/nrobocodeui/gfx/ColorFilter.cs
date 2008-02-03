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

using System;
using System.Drawing;

namespace nrobocodeui.gfx
{
    public class ColorFilter
    {
        private HSLColor hsl2;

        public ColorFilter(Color source)
        {
            hsl2 = source;
        }

        public Color filterRGB(Color pixel)
        {
            HSLColor HSL = pixel;

            if (HSL.Saturation > 0.01f)
            {
                float L = Math.Min(HSL.Luminosity, (hsl2.Luminosity + HSL.Luminosity) / 2 + hsl2.Luminosity / 7);
                return Color.FromArgb(pixel.A, new HSLColor(hsl2.Hue, hsl2.Saturation, L));
            }
            return pixel;
        }

        public Bitmap ChangePrimaryColor(Bitmap image)
        {
            Bitmap cpy = new Bitmap(image);
            for (int y = 0; y < image.Height; y++)
            {
                for (int x = 0; x < image.Width; x++)
                {
                    Color oldC = cpy.GetPixel(x, y);
                    Color newC = filterRGB(oldC);
                    cpy.SetPixel(x, y, newC);
                }
            }
            return cpy;
        }
    }
}