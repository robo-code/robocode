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
using nrobocodeui.Properties;

namespace nrobocodeui.resources
{
    public static class images
    {
        #region body
        
        private static Bitmap body_cache;
        public static Bitmap body
        {
            get
            {
                if (body_cache == null)
                {
                    body_cache = Resources.images_body;
                }
                return body_cache;
            }
        }

        #endregion

        #region radar

        private static Bitmap radar_cache;
        public static Bitmap radar
        {
            get
            {
                if (radar_cache == null)
                {
                    radar_cache = Resources.images_radar;
                }
                return radar_cache;
            }
        }

        #endregion

        #region turret

        private static Bitmap turret_cache;
        public static Bitmap turret
        {
            get
            {
                if (turret_cache == null)
                {
                    turret_cache = Resources.images_turret;
                }
                return turret_cache;
            }
        }

        #endregion

        #region robocode_logo

        private static Bitmap robocode_logo_cache;
        public static Bitmap robocode_logo
        {
            get
            {
                if (robocode_logo_cache == null)
                {
                    robocode_logo_cache = Resources.images_robocode_logo;
                }
                return robocode_logo_cache;
            }
        }

        #endregion

        #region robocode_full_logo

        private static Bitmap robocode_full_logo_cache;
        public static Bitmap robocode_full_logo
        {
            get
            {
                if (robocode_full_logo_cache == null)
                {
                    robocode_full_logo_cache = Resources.images_robocode_full_logo;
                }
                return robocode_full_logo_cache;
            }
        }

        #endregion

        public static class explosion
        {
            #region explosions

            private static Image[,] explosions_cache;

            public static Image[,] explosions
            {
                get
                {
                    if (explosions_cache == null)
                    {
                        explosions_cache = new Image[2,71];
                        for (int f = 1; f <= 17; f++)
                        {
                            string name = string.Format("images_explosion_explosion{0}_{1}", 1, f);
                            explosions_cache[0, f - 1] = (Resources.ResourceManager.GetObject(name, Resources.Culture) as Bitmap);
                        }
                        for (int f = 1; f <= 71; f++)
                        {
                            string name = string.Format("images_explosion_explosion{0}_{1}", 2, f);
                            explosions_cache[1, f - 1] = (Resources.ResourceManager.GetObject(name, Resources.Culture) as Bitmap);
                        }
                    }
                    return explosions_cache;
                }
            }

            #endregion
        }

        public static class ground
        {
            #region explode_debris

            private static Bitmap explode_debris_cache;
            public static Bitmap explode_debris
            {
                get
                {
                    if (explode_debris_cache == null)
                    {
                        explode_debris_cache = Resources.images_ground_explode_debris;
                    }
                    return explode_debris_cache;
                }
            }

            #endregion

            #region blue_metal

            private static List<Image> blue_metal_cache;

            public static List<Image> blue_metal
            {
                get
                {
                    if (blue_metal_cache == null)
                    {
                        blue_metal_cache = new List<Image>();
                        for (int f = 0; f <= 4; f++)
                        {
                            string name = string.Format("images_ground_blue_metal_blue_metal_{0}", f);
                            blue_metal_cache.Add(Resources.ResourceManager.GetObject(name, Resources.Culture) as Bitmap);
                        }
                    }
                    return blue_metal_cache;
                }
            }

            #endregion
        }
    }

    public static class sounds
    {
        #region sounds_explode

        public static System.IO.UnmanagedMemoryStream explode
        {
            get
            {
                return Resources.sounds_explode;
            }
        }

        #endregion

        #region shell_hit

        public static System.IO.UnmanagedMemoryStream shell_hit
        {
            get
            {
                return Resources.sounds_shellhit;
            }
        }

        #endregion

        #region zap

        public static System.IO.UnmanagedMemoryStream zap
        {
            get
            {
                return Resources.sounds_shellhit;
            }
        }

        #endregion

        #region crash

        public static System.IO.UnmanagedMemoryStream crash
        {
            get
            {
                return Resources.sounds__13831_adcbicycle_22;
            }
        }

        #endregion
    }
}
