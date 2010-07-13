/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
 *
 * Contributors:
 *     Flemming N. Larsen
 *     - Initial API and implementation
 *******************************************************************************/
package robocode.annotation;


import java.lang.annotation.*;


/**
 * Annotation used for marking a static field as being safe so that Robocode should
 * not print out warnings at runtime when this annotation is being used.
 * For example, Robocode will print out warnings if a static field to a robot is found.
 * But not when the @SafeStatic is declared for the static field.  
 *
 * @author Flemming N. Larsen (original)
 *
 * @since 1.7.2.1
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SafeStatic {}
