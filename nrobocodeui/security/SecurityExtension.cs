using System;
using System.Collections.Generic;
using System.Text;
using nrobocodeui.manager;
using robocode.security;
using robocode.ui;

namespace nrobocodeui.security
{
    public class SecurityExtension : LoadableManagerBase, ISecurityExtension
    {
        private RobocodeSecurityManager securityManager;

        public void initialize()
        {/*
            securityManager = (RobocodeSecurityManager)java.lang.System.getSecurityManager();
            securityManager.addSafeContext();
         */

        }
    }
}
