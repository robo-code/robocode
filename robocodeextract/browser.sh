#!/bin/sh

# ******************************************************************************
# * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
# * All rights reserved. This program and the accompanying materials 
# * are made available under the terms of the Common Public License v1.0
# * which accompanies this distribution, and is available at
# * http://robocode.sourceforge.net/license/cpl-v10.html
# * 
# * Contributors:
# *     Mathew Nelson
# *     - Initial API and implementation
# ******************************************************************************
	 
# Choose your own here...
#which="konqueror"
#which="mozilla"
#which="firefox"
#which="galeon"

if [[ $which == "" ]]; then
	which firefox && which="firefox-tab"
fi
if [[ $which == "" ]]; then
	which mozilla && which="mozilla-tab"
fi
if [[ $which == "" ]]; then
	which konqueror && which="konqueror"
fi
if [[ $which == "" ]]; then
	which galeon && which="galeon-tab"
fi

if [[ $which == "firefox" ]]; then
	# New or existing firefox
	firefox $1 &
fi

if [[ $which == "firefox-tab" ]]; then
	# Use new tab in existing firefox, or open new
	firefox -remote "openURL($1,new-tab)" || firefox $1 &
fi

if [[ $which == "mozilla" ]]; then
	# New mozilla.
	mozilla $1 &
fi

if [[ $which == "mozilla-tab" ]]; then
	# Use new tab in existing mozilla, or open new window.
	gnome-moz-remote --remote="openURL($1,new-tab)" || gnome-moz-remote $1 &
fi

if [[ $which == "mozilla-win" ]]; then
	# Open new window in existing mozilla (may not work), or open new process.
	gnome-moz-remote --remote="openURL($1,new-window)" || gnome-moz-remote $1 &
fi

if [[ $which == "konqueror" ]]; then
	konqueror $1&
fi

if [[ $which == "galeon" ]]; then
	# new Galeon
	galeon $1 &
fi

if [[ $which == "galeon-win" ]]; then
	# Galeon in new window:
	galeon -w $1 &
fi

if [[ $which == "galeon-tab" ]]; then
	# Galeon in new tab:
	galeon -n $1 &
fi