﻿#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Common Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/cpl-v10.html

#endregion

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