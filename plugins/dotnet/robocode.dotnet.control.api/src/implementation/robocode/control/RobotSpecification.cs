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
using System.IO;
using System.Text;

namespace Robocode.Control
{
    /// <summary>
    /// Defines the properties of a robot, which is returned from
    /// <see cref="RobocodeEngine.GetLocalRepository()"/>.
    /// </summary>
    [Serializable]
    public class RobotSpecification
    {
        internal readonly robocode.control.RobotSpecification robotSpecification;

        internal RobotSpecification(robocode.control.RobotSpecification robotSpecification)
        {
            this.robotSpecification = robotSpecification;
        }

        /// <summary>
        /// Contains the name of this robot or team.
        /// </summary>
        /// <value>
        /// The name of this robot or team.
        /// </value>
        /// <seealso cref="P:Version"/>
        /// <seealso cref="P:NameAndVersion"/>
        public string Name
        {
            get { return robotSpecification.getName(); }
        }

        /// <summary>
        /// Contains the version of this robot or team.
        /// </summary>
        /// <value>
        /// The version of this robot or team.
        /// </value>
        /// <seealso cref="P:Name"/>
        /// <seealso cref="P:NameAndVersion"/>
        public string Version
        {
            get { return robotSpecification.getVersion(); }
        }

        /// <summary>
        /// Contains the name and version of this robot or team.
        /// </summary>
        /// <value>
        /// The name and version of this robot or team.
        /// </value>
        /// <seealso cref="P:Name"/>
        /// <seealso cref="P:Version"/>
        private string NameAndVersion
        {
            get { return robotSpecification.getNameAndVersion(); }
        }

        /// <summary>
        /// Contains the full class name of this robot or team.
        /// </summary>
        /// <value>
        /// The full class name of this robot or team.
        /// </value>
        public string ClassName
        {
            get { return robotSpecification.getClassName(); }
        }

        /// <summary>
        /// Contains the path of the archive file containing this robot or team.
        /// </summary>
        /// <value>
        /// The path of the archive file containing this robot or team or <em>null</em>
        /// if it does not come from an archive file (could be class files instead).
        /// </value>
        public string ArchiveFilePath
        {
            get { return robotSpecification.getJarFile().getAbsolutePath(); }
        }

        /// <summary>
        /// Contains the description provided by the author of this robot or team.
        /// </summary>
        /// <value>
        /// The description provided by the author of this robot or team.
        /// </value>
        public string Description
        {
            get { return robotSpecification.getDescription(); }
        }

        /// <summary>
        /// Contains the version of Robocode this robot or team was build with.
        /// </summary>
        /// <value>
        /// The version of Robocode this robot or team was build with.
        /// </value>
        public string RobocodeVersion
        {
            get { return robotSpecification.getRobocodeVersion(); }
        }

        /// <summary>
        /// Contains the link to the web page for this robot or team.
        /// </summary>
        /// <value>
        /// The link to the web page for this robot or team.
        /// </value>
        public string Webpage
        {
            get { return robotSpecification.getWebpage(); }
        }

        /// <summary>
        /// Contains the name of the author of this robot or team.
        /// </summary>
        /// <value>
        /// The name of the author of this robot or team.
        /// </value>
        public string AuthorName
        {
            get { return robotSpecification.getAuthorName(); }
        }

        /// <summary>
        /// Contains the id of the robot team.
        /// </summary>
        /// <value>
        /// The id of the robot team.
        /// </value>
        public string TeamId
        {
            get { return robotSpecification.getTeamId(); }
        }
    }
}
