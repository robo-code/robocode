/**
 * ****************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 * <p/>
 * Contributors:
 * Pavel Savara
 * - Refactoring
 * *****************************************************************************
 */

package robocode.ui;

import robocode.repository.IFileSpecification;
import robocode.manager.RobotRepositoryManager;

import java.io.File;
import java.util.List;

public interface IRepositoryPlugin extends ILoadableManager {
    String[] getExtensions();
    IFileSpecification createSpecification(RobotRepositoryManager repositoryManager, File f, File rootDir, String prefix, boolean developmentVersion);
}
