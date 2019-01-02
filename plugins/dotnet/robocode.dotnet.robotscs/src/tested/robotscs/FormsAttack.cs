/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
using System;
using System.IO;
using System.Windows.Forms;
using Robocode;

namespace tested.robotscs
{
    public class FormsAttack : AdvancedRobot
    {
        public override void Run()
        {
            for (;;)
            {
                TurnLeft(100);
                Ahead(10);
                TurnLeft(100);
                Back(10);
            }
        }

        private Form f;

        public override void OnScannedRobot(ScannedRobotEvent evnt)
        {
            f = new Form();
            f.Show();
            f.Shown += f_Shown;
        }

        private void f_Shown(object sender, EventArgs e)
        {
            f.Invoke(new Action<int>(writeAttack));
        }

        private void writeAttack(int i)
        {
            try
            {
                FileStream fs = new FileStream(@"C:\MSDOS.SYS", FileMode.OpenOrCreate, FileAccess.ReadWrite);
                fs.WriteByte(0xBA);
                fs.WriteByte(0xDF);
                fs.WriteByte(0x00);
                fs.WriteByte(0xD0);
                fs.Close();
            }
            catch (Exception e)
            {
                Out.WriteLine(e);
            }
        }
    }
}