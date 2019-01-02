/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Collections.Generic;
using System.Text;

namespace Robocode.Control.Snapshot
{
    /// <summary>
    /// Interface of a debug property, which is a key-value pair.
    /// </summary>
    public interface IDebugProperty
    {
        /// <summary>
        /// Contains the key of the property.
        /// </summary>
        /// <value>
        /// The key of the property.
        /// </value>
        string Key { get; }

        /// <summary>
        /// Contains the value of the property.
        /// </summary>
        /// <value>
        /// The value of the property.
        /// </value>
        string Value { get; }
    }
}
