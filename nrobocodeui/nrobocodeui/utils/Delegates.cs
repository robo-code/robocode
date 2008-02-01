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

namespace nrobocodeui.utils
{
    internal delegate void Action();
    internal delegate void Action<T>(T param1);
    internal delegate void Action<T, T2>(T param1, T2 param2);
    internal delegate R Delegate<R, T>(T param1);
    internal delegate R Delegate<R>();
}
