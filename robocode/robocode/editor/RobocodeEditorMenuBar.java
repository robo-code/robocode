/*******************************************************************************
 * Copyright (c) 2001 Mathew Nelson
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew Nelson - initial API and implementation
 *******************************************************************************/
package robocode.editor;


import javax.swing.*;

import robocode.util.*;


/**
 * Insert the type's description here.
 * Creation date: (9/4/2001 5:23:49 PM)
 * @author: Mathew A. Nelson
 */
public class RobocodeEditorMenuBar extends JMenuBar {
	private JMenu fileMenu = null;

	private JMenuItem fileOpenMenuItem = null;
	private JMenuItem fileExtractMenuItem = null;
	private JMenuItem fileSaveMenuItem = null;
	private JMenuItem fileSaveAsMenuItem = null;
	private JMenuItem fileExitMenuItem = null;

	private JMenu functionsMenu = null;

	class EventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == RobocodeEditorMenuBar.this.getFileNewRobotMenuItem()) {
				fileNewRobotActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getFileNewJavaFileMenuItem()) {
				fileNewJavaFileActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getFileOpenMenuItem()) {
				fileOpenActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getFileExtractMenuItem()) {
				fileExtractActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getFileSaveMenuItem()) {
				fileSaveActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getFileSaveAsMenuItem()) {
				fileSaveAsActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getFileExitMenuItem()) {
				fileExitActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getCompilerCompileMenuItem()) {
				compilerCompileActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getCompilerOptionsPreferencesMenuItem()) {
				compilerOptionsPreferencesActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getCompilerOptionsResetCompilerMenuItem()) {
				compilerOptionsResetCompilerActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getHelpRobocodeApiMenuItem()) {
				helpRobocodeApiActionPerformed();
			}
		}
		;
	}


	;

	private JMenuItem compilerCompileMenuItem = null;
	private JMenu compilerMenu = null;
	private JMenu compilerOptionsMenu = null;
	private JMenuItem compilerOptionsPreferencesMenuItem = null;
	private JMenuItem compilerOptionsResetCompilerMenuItem = null;
	public RobocodeEditor editor = null;
	EventHandler eventHandler = new EventHandler();
	private JMenuItem fileNewJavaFileMenuItem = null;
	private JMenu fileNewMenu = null;
	private JMenuItem fileNewRobotMenuItem = null;
	private JMenuItem functionsComingSoonMenuItem = null;
	private JMenu helpMenu = null;
	private JMenuItem helpRobocodeApiMenuItem = null;

	/**
	 * RoboCodeEditorMenuBar constructor.
	 */
	public RobocodeEditorMenuBar(RobocodeEditor editor) {
		super();
		this.editor = editor;
		initialize();
	}

	/**
	 * Comment
	 */
	public void compilerCompileActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();

		if (editWindow != null) {
			editWindow.setFrame(editor);
			editWindow.compile();
		}
		return;
	}

	/**
	 * Comment
	 */
	public void compilerOptionsPreferencesActionPerformed() {
		CompilerPreferencesDialog d = new CompilerPreferencesDialog(editor);

		Utils.packCenterShow(editor, d);
		return;
	}

	/**
	 * Comment
	 */
	private void compilerOptionsResetCompilerActionPerformed() {
		new Thread(new Runnable() {
			public void run() {
				editor.resetCompilerProperties();
			}
		}).start();
		return;
	}

	/**
	 * Initialize the class.
	 */
	public void disableMenus() {
		try {
			getFileNewMenu().setEnabled(false);
			getFileOpenMenuItem().setEnabled(false);
			getFunctionsMenu().setEnabled(false);
			getCompilerMenu().setEnabled(false);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}

	/**
	 * Initialize the class.
	 */
	public void enableMenus() {
		try {
			getFileNewMenu().setEnabled(true);
			getFileOpenMenuItem().setEnabled(true);
			getFunctionsMenu().setEnabled(false); // eventually true
			getCompilerMenu().setEnabled(true);
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}

	/**
	 * Comment
	 */
	public void fileExitActionPerformed() {
		java.awt.AWTEvent evt;

		evt = new java.awt.event.WindowEvent(editor, java.awt.event.WindowEvent.WINDOW_CLOSING);
		editor.dispatchEvent(evt);
		return;
	}

	/**
	 * Comment
	 */
	public void fileNewJavaFileActionPerformed() {

		editor.createNewJavaFile();
		return;
	}

	/**
	 * Comment
	 */
	public void fileNewRobotActionPerformed() {

		editor.createNewRobot();
		return;
	}

	/**
	 * Comment
	 */
	public void fileOpenActionPerformed() {
		editor.openRobot();
		return;
	}

	/**
	 * Comment
	 */
	public void fileExtractActionPerformed() {
		editor.extractRobot();
		return;
	}

	/**
	 * Comment
	 */
	public void fileSaveActionPerformed() {

		editor.saveRobot();
		return;
	}

	/**
	 * Comment
	 */
	public void fileSaveAsActionPerformed() {
		editor.saveAsRobot();
		return;
	}

	/**
	 * Return the Compile property value.
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getCompilerCompileMenuItem() {
		if (compilerCompileMenuItem == null) {
			try {
				compilerCompileMenuItem = new javax.swing.JMenuItem();
				compilerCompileMenuItem.setName("compilerCompileMenuItem");
				compilerCompileMenuItem.setText("Compile");
				compilerCompileMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return compilerCompileMenuItem;
	}

	/**
	 * Return the compileMenu
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getCompilerMenu() {
		if (compilerMenu == null) {
			try {
				compilerMenu = new javax.swing.JMenu();
				compilerMenu.setName("CompilerMenu");
				compilerMenu.setText("Compiler");
				compilerMenu.add(getCompilerCompileMenuItem());
				compilerMenu.add(getCompilerOptionsMenu());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return compilerMenu;
	}

	/**
	 * Return the compileMenu
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getCompilerOptionsMenu() {
		if (compilerOptionsMenu == null) {
			try {
				compilerOptionsMenu = new javax.swing.JMenu();
				compilerOptionsMenu.setName("CompilerOptionsMenu");
				compilerOptionsMenu.setText("Options");
				compilerOptionsMenu.add(getCompilerOptionsPreferencesMenuItem());
				compilerOptionsMenu.add(getCompilerOptionsResetCompilerMenuItem());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return compilerOptionsMenu;
	}

	/**
	 * Return the compilerOptionsPreferencesMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getCompilerOptionsPreferencesMenuItem() {
		if (compilerOptionsPreferencesMenuItem == null) {
			try {
				compilerOptionsPreferencesMenuItem = new javax.swing.JMenuItem();
				compilerOptionsPreferencesMenuItem.setName("compilerOptionsPreferencesMenuItem");
				compilerOptionsPreferencesMenuItem.setText("Preferences");
				compilerOptionsPreferencesMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return compilerOptionsPreferencesMenuItem;
	}

	/**
	 * Return the compilerOptionsResetCompilerMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getCompilerOptionsResetCompilerMenuItem() {
		if (compilerOptionsResetCompilerMenuItem == null) {
			try {
				compilerOptionsResetCompilerMenuItem = new javax.swing.JMenuItem();
				compilerOptionsResetCompilerMenuItem.setName("compilerOptionsResetCompilerMenuItem");
				compilerOptionsResetCompilerMenuItem.setText("Reset Compiler");
				compilerOptionsResetCompilerMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return compilerOptionsResetCompilerMenuItem;
	}

	/**
	 * Return the fileExitMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getFileExitMenuItem() {
		if (fileExitMenuItem == null) {
			try {
				fileExitMenuItem = new javax.swing.JMenuItem();
				fileExitMenuItem.setName("fileExitMenuItem");
				fileExitMenuItem.setText("Exit");
				fileExitMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return fileExitMenuItem;
	}

	/**
	 * Return the fileMenu
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getFileMenu() {
		if (fileMenu == null) {
			try {
				fileMenu = new javax.swing.JMenu();
				fileMenu.setName("fileMenu");
				fileMenu.setText("File");
				fileMenu.add(getFileNewMenu());
				fileMenu.add(getFileOpenMenuItem());
				fileMenu.add(getFileExtractMenuItem());
				fileMenu.add(getFileSaveMenuItem());
				fileMenu.add(getFileSaveAsMenuItem());
				fileMenu.add(new JSeparator());
				fileMenu.add(getFileExitMenuItem());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return fileMenu;
	}

	/**
	 * Return the fileNewMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getFileNewJavaFileMenuItem() {
		if (fileNewJavaFileMenuItem == null) {
			try {
				fileNewJavaFileMenuItem = new javax.swing.JMenuItem();
				fileNewJavaFileMenuItem.setName("fileNewJavaFileMenuItem");
				fileNewJavaFileMenuItem.setText("Java File");
				fileNewJavaFileMenuItem.setAccelerator(
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,
						java.awt.Event.CTRL_MASK | java.awt.Event.SHIFT_MASK));
				fileNewJavaFileMenuItem.addActionListener(eventHandler);
			} catch (Throwable e) {
				log(e);
			}
		}
		return fileNewJavaFileMenuItem;
	}

	/**
	 * Return the fileNewMenu
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getFileNewMenu() {
		if (fileNewMenu == null) {
			try {
				fileNewMenu = new javax.swing.JMenu();
				fileNewMenu.setName("fileNewMenu");
				fileNewMenu.setText("New");
				fileNewMenu.add(getFileNewRobotMenuItem());
				fileNewMenu.add(getFileNewJavaFileMenuItem());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return fileNewMenu;
	}

	/**
	 * Return the fileNewMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getFileNewRobotMenuItem() {
		if (fileNewRobotMenuItem == null) {
			try {
				fileNewRobotMenuItem = new javax.swing.JMenuItem();
				fileNewRobotMenuItem.setName("fileNewRobotMenuItem");
				fileNewRobotMenuItem.setText("Robot");
				fileNewRobotMenuItem.setAccelerator(
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
				fileNewRobotMenuItem.addActionListener(eventHandler);
			} catch (Throwable e) {
				log(e);
			}
		}
		return fileNewRobotMenuItem;
	}

	/**
	 * Return the fileOpenMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getFileOpenMenuItem() {
		if (fileOpenMenuItem == null) {
			try {
				fileOpenMenuItem = new javax.swing.JMenuItem();
				fileOpenMenuItem.setName("fileOpenMenuItem");
				fileOpenMenuItem.setText("Open");
				fileOpenMenuItem.setAccelerator(
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
				fileOpenMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return fileOpenMenuItem;
	}

	private javax.swing.JMenuItem getFileExtractMenuItem() {
		if (fileExtractMenuItem == null) {
			try {
				fileExtractMenuItem = new javax.swing.JMenuItem();
				fileExtractMenuItem.setName("fileExtractMenuItem");
				fileExtractMenuItem.setText("Extract downloaded robot for editing");
				fileExtractMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return fileExtractMenuItem;
	}

	/**
	 * Return the fileSaveAsMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getFileSaveAsMenuItem() {
		if (fileSaveAsMenuItem == null) {
			try {
				fileSaveAsMenuItem = new javax.swing.JMenuItem();
				fileSaveAsMenuItem.setName("fileSaveAsMenuItem");
				fileSaveAsMenuItem.setText("Save As");
				fileSaveAsMenuItem.setAccelerator(
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
						java.awt.Event.CTRL_MASK | java.awt.Event.SHIFT_MASK));
				fileSaveAsMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return fileSaveAsMenuItem;
	}

	/**
	 * Return the fileSaveMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getFileSaveMenuItem() {
		if (fileSaveMenuItem == null) {
			try {
				fileSaveMenuItem = new javax.swing.JMenuItem();
				fileSaveMenuItem.setName("fileSaveMenuItem");
				fileSaveMenuItem.setText("Save");
				fileSaveMenuItem.setAccelerator(
						KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
				fileSaveMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return fileSaveMenuItem;
	}

	/**
	 * Return the functionsComingSoonMenuItem
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getFunctionsComingSoonMenuItem() {
		if (functionsComingSoonMenuItem == null) {
			try {
				functionsComingSoonMenuItem = new javax.swing.JMenuItem();
				functionsComingSoonMenuItem.setName("functionsComingSoonMenuItem");
				functionsComingSoonMenuItem.setText("Coming Soon");
				functionsComingSoonMenuItem.setEnabled(false);
				functionsComingSoonMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return functionsComingSoonMenuItem;
	}

	/**
	 * Return the functionsMenu
	 * @return javax.swing.JMenu
	 */
	private javax.swing.JMenu getFunctionsMenu() {
		if (functionsMenu == null) {
			try {
				functionsMenu = new javax.swing.JMenu();
				functionsMenu.setName("functionsMenu");
				functionsMenu.setText("Functions");
				functionsMenu.add(getFunctionsComingSoonMenuItem());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return functionsMenu;
	}

	/**
	 * Return the Help Menu.
	 * @return javax.swing.JMenu
	 */
	public javax.swing.JMenu getHelpMenu() {
		if (helpMenu == null) {
			try {
				helpMenu = new javax.swing.JMenu();
				helpMenu.setName("helpMenu");
				helpMenu.setText("Help");
				helpMenu.add(getHelpRobocodeApiMenuItem());
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return helpMenu;
	}

	/**
	 * Return the helpRobocodeApiMenuItem.
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getHelpRobocodeApiMenuItem() {
		if (helpRobocodeApiMenuItem == null) {
			try {
				helpRobocodeApiMenuItem = new javax.swing.JMenuItem();
				helpRobocodeApiMenuItem.setName("helpRobocodeApiMenuItem");
				helpRobocodeApiMenuItem.setText("Robocode API");
				helpRobocodeApiMenuItem.addActionListener(eventHandler);
			} catch (java.lang.Throwable e) {
				log(e);
			}
		}
		return helpRobocodeApiMenuItem;
	}

	/**
	 * Comment
	 */
	public void helpRobocodeApiActionPerformed() {
		editor.showHelpApi();
		return;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			setName("RoboCodeEditorMenuBar");
			add(getFileMenu());
			// add(getFunctionsMenu());
			add(getCompilerMenu());
			add(getHelpMenu());
		} catch (java.lang.Throwable e) {
			log(e);
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/22/2001 1:41:21 PM)
	 * @param e java.lang.Exception
	 */
	public void log(Throwable e) {
		Utils.log(e);
	}
}
