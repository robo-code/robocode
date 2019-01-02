/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;

namespace net.sf.robocode.repository
{
    [Flags]
    public enum RobotTypeN
    {
        INVALID = 0,
        JUNIOR = 1,
        STANDARD = 2,
        ADVANCED = 4,
        TEAM = 8,
        DROID = 16,
        INTERACTIVE = 32,
        PAINTING = 64,
        SENTRY = 128
    }

    partial class RobotType
    {
        private static RobotType _Invalid;

        public static RobotType Invalid
        {
            get
            {
                if (_Invalid == null)
                {
                    _Invalid = INVALID;
                }
                return _Invalid;
            }
        }
    }
}