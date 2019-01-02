/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
﻿

using System;
using System.Reflection;
using System.Security;
using System.Threading;
using Mono.Reflection;

namespace net.sf.robocode.dotnet.utils
{
    partial class Reflection
    {
        private const BindingFlags nextedFlags = BindingFlags.NonPublic | BindingFlags.Public;
        private const BindingFlags memberFlags =
            BindingFlags.Static | BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public |
            BindingFlags.CreateInstance | BindingFlags.GetProperty;

        public static void CheckAssembly(Assembly assembly)
        {
            foreach (Type type in assembly.GetTypes())
            {
                CheckType(type);
            }
        }

        private static void CheckType(Type type)
        {
            foreach (MemberInfo member in type.GetMembers(memberFlags))
            {
                MethodBase mb = member as MethodBase;
                if (mb != null && member.DeclaringType == type)
                {
                    CheckMethodBase(mb);
                }
            }   
            CheckUsedType(type.BaseType);
            foreach (Type ifc in type.GetInterfaces())
            {
                if (ifc.DeclaringType == type)
                {
                    CheckUsedType(ifc);
                }
            }
            foreach (Type nestedType in type.GetNestedTypes(nextedFlags))
            {
                CheckType(nestedType);
            }
        }

        private static void CheckMethodBase(MethodBase methodBase)
        {
            foreach (Instruction i in MethodBodyReader.GetInstructions(methodBase))
            {
                MethodBase mb = i.Operand as MethodBase;
                MethodInfo mi = i.Operand as MethodInfo;
                FieldInfo fi = i.Operand as FieldInfo;

                if (mb != null)
                {
                    foreach (var parameter in mb.GetParameters())
                    {
                        CheckUsedType(parameter.ParameterType);
                    }
                    CheckUsedType(mb.DeclaringType);
                }

                if (mi != null)
                {
                    CheckUsedType(mi.ReturnType);
                }

                if (fi != null)
                {
                    CheckUsedType(fi.FieldType);
                }
                
            }
        }

        private static void CheckUsedType(Type usedType)
        {
            if (typeof(Thread).IsAssignableFrom(usedType) ||
                typeof(ThreadPool).IsAssignableFrom(usedType) ||
                typeof(ThreadPriority).IsAssignableFrom(usedType)
                )
            {
                throw new SecurityException("System.Threading.Thread usage is banned here! Please use robocode.Thread instead");
            }
        }

    }
}
