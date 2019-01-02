/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using net.sf.robocode.security;

namespace robocode.control
{
    partial class RandomFactory : IHiddenRandomHelper
    {
        private RandomRedirect rr;

        public RandomFactory(bool fakeInstance)
        {
        }

        #region IHiddenRandomHelper Members

        public Random GetRandom()
        {
            if (isDeterministic())
            {
                if (rr == null)
                {
                    rr = new RandomRedirect(getRandom());
                }
                return rr;
            }
            return new Random();
        }

        #endregion
    }

    public class RandomRedirect : Random
    {
        private java.util.Random r;

        public RandomRedirect(java.util.Random r)
        {
            this.r = r;
        }

        public override int Next()
        {
            return r.nextInt();
        }

        public override int Next(int maxValue)
        {
            return r.nextInt(maxValue);
        }

        public override int Next(int minValue, int maxValue)
        {
            return minValue + r.nextInt(maxValue - minValue);
        }

        public override void NextBytes(byte[] buffer)
        {
            throw new NotImplementedException();
        }

        public override double NextDouble()
        {
            return r.nextDouble();
        }
    }

}
