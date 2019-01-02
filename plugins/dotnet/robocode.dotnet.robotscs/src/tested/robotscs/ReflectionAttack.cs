/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.Reflection;
using Robocode;

namespace tested.robotscs
{
    public class ReflectionAttack : AdvancedRobot
    {
        public override void Run()
        {
            FieldInfo fieldInfo = typeof(Console).GetField("_out", BindingFlags.Static | BindingFlags.NonPublic);
            object obj = fieldInfo.GetValue(null);
            fieldInfo.SetValue(null, null);

            MethodInfo method = obj.GetType().GetMethod("Write", new[] { typeof(string) });
            method.Invoke(obj, new Object[] { "Hello World" });
        }
    }
}