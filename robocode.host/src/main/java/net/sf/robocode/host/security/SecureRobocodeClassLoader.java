/*
 * Copyright (c) 2001-2025 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.host.security;

import net.sf.robocode.io.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A secure class loader for Robocode that can validate class access
 * without relying on SecurityManager.
 *
 * @author Flemming N. Larsen (original)
 */
public class SecureRobocodeClassLoader extends ClassLoader {

    private static final Set<String> RESTRICTED_PACKAGES = new HashSet<>(Arrays.asList(
            "java.lang.reflect",
            "java.security",
            "javax.swing",
            "java.awt",
            "sun.",
            "com.sun.",
            "net.sf.robocode"
    ));

    private static final Set<String> ALLOWED_CLASSES = new HashSet<>(Arrays.asList(
            "java.awt.Color",
            "java.awt.geom.Rectangle2D",
            "java.awt.Graphics2D"
    ));

    /**
     * Creates a new SecureRobocodeClassLoader with the specified parent.
     *
     * @param parent the parent class loader
     */
    public SecureRobocodeClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * Checks if a class should be allowed to be loaded by robots.
     *
     * @param className the fully qualified name of the class
     * @return true if the class is allowed, false otherwise
     */
    public boolean isClassAllowed(String className) {
        // Allow explicitly allowed classes
        if (ALLOWED_CLASSES.contains(className)) {
            return true;
        }

        // Check against restricted packages
        for (String pkg : RESTRICTED_PACKAGES) {
            if (className.startsWith(pkg)) {
                Logger.logMessage("Blocked access to restricted class: " + className);
                return false;
            }
        }

        return true;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (!isClassAllowed(name)) {
            throw new ClassNotFoundException("Access to class " + name + " is restricted for security reasons");
        }
        return super.loadClass(name, resolve);
    }
}
