/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Pavel Savara
 *     - Initial implementation
 *******************************************************************************/
package robocode.ui;


import robocode.manager.RobotRepositoryManager;
import robocode.repository.IFileSpecification;

import java.io.File;


/**
 * @author Pavel Savara (original)
 */
public interface IRepositoryPlugin extends ILoadableManager {

    /**
     * @return setof file extensions returned by this plugin
     */
    String[] getExtensions();

    /**
     * Creates new instance of file specification for given file. Returns null when there is no supported robot in the file.
     *
     * @param repositoryManager
     * @param file               to load robot from
     * @param rootDir            parent directory
     * @param prefix             robot prefix
     * @param developmentVersion
     * @return specification or null
     */
    IFileSpecification createSpecification(RobotRepositoryManager repositoryManager, File file, File rootDir, String prefix, boolean developmentVersion);
}
