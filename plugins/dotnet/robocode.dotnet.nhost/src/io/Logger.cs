/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using java.lang;

namespace net.sf.robocode.io
{
    partial class Logger : ILoggerN
    {
        public Logger(bool fakeInstance)
        {
        }

        #region ILoggerN Members

        public void logMessage(string s, bool newline)
        {
            logMessage((String) s, newline);
        }

        public void logError(string s)
        {
            logError((String) s);
        }

        #endregion
    }
}