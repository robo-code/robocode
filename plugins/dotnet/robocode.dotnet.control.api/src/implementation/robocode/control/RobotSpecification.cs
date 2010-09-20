#region Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors

// Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// which accompanies this distribution, and is available at
// http://robocode.sourceforge.net/license/epl-v10.html

#endregion

using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace Robocode.Control
{
    /**
     * Defines the properties of a robot, which is returned from
     * {@link RobocodeEngine#getLocalRepository()} or
     *
     * @author Mathew A. Nelson (original)
     * @author Flemming N. Larsen (contributor)
     */
    [Serializable]
    public class RobotSpecification
    {
        internal readonly robocode.control.RobotSpecification robotSpecification;

        internal RobotSpecification(robocode.control.RobotSpecification robotSpecification)
        {
            this.robotSpecification = robotSpecification;
        }

        /**
         * Returns the name of this robot or team.
         *
         * @return the name of this robot or team.
         * @see #getVersion()
         * @see #getNameAndVersion()
         */
        public string Name
        {
            get { return robotSpecification.getName(); }
        }

        /**
         * Returns the version of this robot or team.
         *
         * @return the version of this robot or team.
         * @see #getName()
         * @see #getNameAndVersion()
         */
        public string Version
        {
            get { return robotSpecification.getVersion(); }
        }

        /**
         * Returns the name and version of this robot or team.
         *
         * @return the name and version of this robot or team.
         * @see #getName()
         * @see #getVersion()
         * @since 1.3
         */
        private string NameAndVersion
        {
            get { return robotSpecification.getNameAndVersion(); }
        }

        /**
         * Returns the full class name of this robot or team.
         *
         * @return the full class name of this robot or team.
         */
        public string ClassName
        {
            get { return robotSpecification.getClassName(); }
        }

        /**
         * Returns the JAR file containing this robot or team, or {@code null} if it
         * does not come from a JAR file (could be class files instead).
         *
         * @return the JAR file containing this robot or team, or {@code null} if it
         *         does not come from a JAR file (could be class files instead).
         */
        public string ArchiveFilePath
        {
            get { return robotSpecification.getJarFile().getAbsolutePath(); }
        }

        /**
         * Returns the description provided by the author of this robot.
         *
         * @return the description provided by the author of this robot.
         */
        public string Description
        {
            get { return robotSpecification.getDescription(); }
        }

        /**
         * Returns the version of Robocode this robot was based on.
         *
         * @return the version of Robocode this robot was based on.
         */
        public string RobocodeVersion
        {
            get { return robotSpecification.getRobocodeVersion(); }
        }

        /**
         * Returns the web page for this robot.
         *
         * @return the web page for this robot.
         */
        public string Webpage
        {
            get { return robotSpecification.getWebpage(); }
        }

        /**
         * Returns the name of this robot's author.
         *
         * @return the name of this robot's author.
         */
        public string AuthorName
        {
            get { return robotSpecification.getAuthorName(); }
        }

        /**
         * @return id of the team in current battle
         */
        public string TeamId
        {
            get { return robotSpecification.getTeamId(); }
        }
    }
}
