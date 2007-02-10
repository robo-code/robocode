/*******************************************************************************
 * Copyright (c) 2001, 2007 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Matthew Reeder
 *     - Added Edit and Window menus
 *     - Added keyboard mnemonics to all menus and menu items
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Updated to use methods from the WindowUtil, which replaces window methods
 *       that have been (re)moved from the robocode.util.Utils class
 *******************************************************************************/
package robocode.editor;


import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.event.*;

import javax.swing.*;

import robocode.dialog.WindowUtil;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeEditorMenuBar extends JMenuBar {
	private JMenu fileMenu;

	private JMenuItem fileOpenMenuItem;
	private JMenuItem fileExtractMenuItem;
	private JMenuItem fileSaveMenuItem;
	private JMenuItem fileSaveAsMenuItem;
	private JMenuItem fileExitMenuItem;

	private JMenu functionsMenu;

	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
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
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditUndoMenuItem()) {
				editUndoActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditRedoMenuItem()) {
				editRedoActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditCutMenuItem()) {
				editCutActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditCopyMenuItem()) {
				editCopyActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditPasteMenuItem()) {
				editPasteActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditDeleteMenuItem()) {
				editDeleteActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditSelectAllMenuItem()) {
				editSelectAllActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditFindMenuItem()) {
				editFindActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditFindNextMenuItem()) {
				editFindNextActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getEditReplaceMenuItem()) {
				editReplaceActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getWindowCloseMenuItem()) {
				windowCloseActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getWindowCloseAllMenuItem()) {
				windowCloseAllActionPerformed();
			}
			if (e.getSource() == RobocodeEditorMenuBar.this.getWindowWindowsDialogMenuItem()) {
				windowMoreWindowsActionPerformed();
			}
		}
	}

	private JMenuItem compilerCompileMenuItem;
	private JMenu compilerMenu;
	private JMenu compilerOptionsMenu;
	private JMenuItem compilerOptionsPreferencesMenuItem;
	private JMenuItem compilerOptionsResetCompilerMenuItem;
	public RobocodeEditor editor;
	EventHandler eventHandler = new EventHandler();
	private JMenuItem fileNewJavaFileMenuItem;
	private JMenu fileNewMenu;
	private JMenuItem fileNewRobotMenuItem;
	private JMenuItem functionsComingSoonMenuItem;
	private JMenu helpMenu;
	private JMenuItem helpRobocodeApiMenuItem;

	// New Edit menu by Matthew Reeder
	private JMenu editMenu;
	private JMenuItem editUndoMenuItem;
	private JMenuItem editRedoMenuItem;
	private JMenuItem editCutMenuItem;
	private JMenuItem editCopyMenuItem;
	private JMenuItem editPasteMenuItem;
	private JMenuItem editDeleteMenuItem;
	private JMenuItem editFindMenuItem;
	private JMenuItem editFindNextMenuItem;
	private JMenuItem editReplaceMenuItem;
	private JMenuItem editSelectAllMenuItem;

	// New Window menu by Matthew Reeder
	private JMenu windowMenu;
	private JMenuItem windowCloseMenuItem;
	private JMenuItem windowCloseAllMenuItem;
	private JMenuItem windowWindowsDialogMenuItem;
	private MoreWindowsDialog moreWindowsDialog;

	/**
	 * RoboCodeEditorMenuBar constructor.
	 */
	public RobocodeEditorMenuBar(RobocodeEditor editor) {
		super();
		this.editor = editor;
		initialize();
	}

	public void compilerCompileActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();

		if (editWindow != null) {
			editWindow.setFrame(editor);
			editWindow.compile();
		}
	}

	public void compilerOptionsPreferencesActionPerformed() {
		CompilerPreferencesDialog d = new CompilerPreferencesDialog(editor);

		WindowUtil.packCenterShow(editor, d);
	}

	private void compilerOptionsResetCompilerActionPerformed() {
		new Thread(new Runnable() {
			public void run() {
				editor.resetCompilerProperties();
			}
		}).start();
	}

	public void disableMenus() {
		getFileNewMenu().setEnabled(false);
		getFileOpenMenuItem().setEnabled(false);
		getFunctionsMenu().setEnabled(false);
		getCompilerMenu().setEnabled(false);
	}

	public void enableMenus() {
		getFileNewMenu().setEnabled(true);
		getFileOpenMenuItem().setEnabled(true);
		getFunctionsMenu().setEnabled(false); // eventually true
		getCompilerMenu().setEnabled(true);
	}

	public void fileExitActionPerformed() {
		AWTEvent evt;

		evt = new WindowEvent(editor, WindowEvent.WINDOW_CLOSING);
		editor.dispatchEvent(evt);
	}

	public void fileNewJavaFileActionPerformed() {
		editor.createNewJavaFile();
	}

	public void fileNewRobotActionPerformed() {
		editor.createNewRobot();
	}

	public void fileOpenActionPerformed() {
		editor.openRobot();
	}

	public void fileExtractActionPerformed() {
		editor.extractRobot();
	}

	public void fileSaveActionPerformed() {
		editor.saveRobot();
	}

	public void fileSaveAsActionPerformed() {
		editor.saveAsRobot();
	}

	public void editUndoActionPerformed() {
		editor.getActiveWindow().undo();
	}

	public void editRedoActionPerformed() {
		editor.getActiveWindow().redo();
	}

	public void editCutActionPerformed() {
		editor.getActiveWindow().getEditorPane().cut();
	}

	public void editCopyActionPerformed() {
		editor.getActiveWindow().getEditorPane().copy();
	}

	public void editPasteActionPerformed() {
		editor.getActiveWindow().getEditorPane().paste();
	}

	public void editDeleteActionPerformed() {
		editor.getActiveWindow().getEditorPane().replaceSelection(null);
	}

	public void editSelectAllActionPerformed() {
		editor.getActiveWindow().getEditorPane().selectAll();
	}

	public void editFindActionPerformed() {
		editor.findDialog();
	}

	public void editReplaceActionPerformed() {
		editor.replaceDialog();
	}

	public void editFindNextActionPerformed() {
		editor.getFindReplaceDialog().findNext();
	}

	public void windowCloseActionPerformed() {
		if (editor.getActiveWindow() != null) {
			editor.getActiveWindow().doDefaultCloseAction();
		}
	}

	public void windowCloseAllActionPerformed() {
		JInternalFrame[] frames = editor.getDesktopPane().getAllFrames();

		if (frames != null) {
			for (JInternalFrame frame : frames) {
				frame.doDefaultCloseAction();
			}
		}
	}

	public void windowMoreWindowsActionPerformed() {
		getMoreWindowsDialog().setVisible(true);
	}

	/**
	 * Return the Compile property value.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getCompilerCompileMenuItem() {
		if (compilerCompileMenuItem == null) {
			compilerCompileMenuItem = new JMenuItem();
			compilerCompileMenuItem.setText("Compile");
			compilerCompileMenuItem.setMnemonic('m');
			compilerCompileMenuItem.setDisplayedMnemonicIndex(2);
			compilerCompileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0, false));
			compilerCompileMenuItem.addActionListener(eventHandler);
		}
		return compilerCompileMenuItem;
	}

	/**
	 * Return the compileMenu
	 *
	 * @return JMenu
	 */
	private JMenu getCompilerMenu() {
		if (compilerMenu == null) {
			compilerMenu = new JMenu();
			compilerMenu.setText("Compiler");
			compilerMenu.setMnemonic('C');
			compilerMenu.setDisplayedMnemonicIndex(0);
			compilerMenu.add(getCompilerCompileMenuItem());
			compilerMenu.add(getCompilerOptionsMenu());
		}
		return compilerMenu;
	}

	/**
	 * Return the compileMenu
	 *
	 * @return JMenu
	 */
	private JMenu getCompilerOptionsMenu() {
		if (compilerOptionsMenu == null) {
			compilerOptionsMenu = new JMenu();
			compilerOptionsMenu.setText("Options");
			compilerOptionsMenu.setMnemonic('O');
			compilerOptionsMenu.setDisplayedMnemonicIndex(0);
			compilerOptionsMenu.add(getCompilerOptionsPreferencesMenuItem());
			compilerOptionsMenu.add(getCompilerOptionsResetCompilerMenuItem());
		}
		return compilerOptionsMenu;
	}

	/**
	 * Return the compilerOptionsPreferencesMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getCompilerOptionsPreferencesMenuItem() {
		if (compilerOptionsPreferencesMenuItem == null) {
			compilerOptionsPreferencesMenuItem = new JMenuItem();
			compilerOptionsPreferencesMenuItem.setText("Preferences");
			compilerOptionsPreferencesMenuItem.setMnemonic('P');
			compilerOptionsPreferencesMenuItem.setDisplayedMnemonicIndex(0);
			compilerOptionsPreferencesMenuItem.addActionListener(eventHandler);
		}
		return compilerOptionsPreferencesMenuItem;
	}

	/**
	 * Return the compilerOptionsResetCompilerMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getCompilerOptionsResetCompilerMenuItem() {
		if (compilerOptionsResetCompilerMenuItem == null) {
			compilerOptionsResetCompilerMenuItem = new JMenuItem();
			compilerOptionsResetCompilerMenuItem.setText("Reset Compiler");
			compilerOptionsResetCompilerMenuItem.setMnemonic('R');
			compilerOptionsResetCompilerMenuItem.setDisplayedMnemonicIndex(0);
			compilerOptionsResetCompilerMenuItem.addActionListener(eventHandler);
		}
		return compilerOptionsResetCompilerMenuItem;
	}

	/**
	 * Return the fileExitMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getFileExitMenuItem() {
		if (fileExitMenuItem == null) {
			fileExitMenuItem = new JMenuItem();
			fileExitMenuItem.setText("Exit");
			fileExitMenuItem.setMnemonic('x');
			fileExitMenuItem.setDisplayedMnemonicIndex(1);
			fileExitMenuItem.addActionListener(eventHandler);
		}
		return fileExitMenuItem;
	}

	/**
	 * Return the fileMenu
	 *
	 * @return JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setMnemonic('F');
			fileMenu.setDisplayedMnemonicIndex(0);
			fileMenu.add(getFileNewMenu());
			fileMenu.add(getFileOpenMenuItem());
			fileMenu.add(getFileExtractMenuItem());
			fileMenu.add(getFileSaveMenuItem());
			fileMenu.add(getFileSaveAsMenuItem());
			fileMenu.add(new JSeparator());
			fileMenu.add(getFileExitMenuItem());
		}
		return fileMenu;
	}

	/**
	 * Return the fileNewMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getFileNewJavaFileMenuItem() {
		if (fileNewJavaFileMenuItem == null) {
			fileNewJavaFileMenuItem = new JMenuItem();
			fileNewJavaFileMenuItem.setText("Java File");
			fileNewJavaFileMenuItem.setMnemonic('J');
			fileNewJavaFileMenuItem.setDisplayedMnemonicIndex(0);
			fileNewJavaFileMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK | Event.SHIFT_MASK));
			fileNewJavaFileMenuItem.addActionListener(eventHandler);
		}
		return fileNewJavaFileMenuItem;
	}

	/**
	 * Return the fileNewMenu
	 *
	 * @return JMenu
	 */
	private JMenu getFileNewMenu() {
		if (fileNewMenu == null) {
			fileNewMenu = new JMenu();
			fileNewMenu.setText("New");
			fileNewMenu.setMnemonic('N');
			fileNewMenu.setDisplayedMnemonicIndex(0);
			fileNewMenu.add(getFileNewRobotMenuItem());
			fileNewMenu.add(getFileNewJavaFileMenuItem());
		}
		return fileNewMenu;
	}

	/**
	 * Return the fileNewMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getFileNewRobotMenuItem() {
		if (fileNewRobotMenuItem == null) {
			fileNewRobotMenuItem = new JMenuItem();
			fileNewRobotMenuItem.setText("Robot");
			fileNewRobotMenuItem.setMnemonic('R');
			fileNewRobotMenuItem.setDisplayedMnemonicIndex(0);
			fileNewRobotMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
			fileNewRobotMenuItem.addActionListener(eventHandler);
		}
		return fileNewRobotMenuItem;
	}

	/**
	 * Return the fileOpenMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getFileOpenMenuItem() {
		if (fileOpenMenuItem == null) {
			fileOpenMenuItem = new JMenuItem();
			fileOpenMenuItem.setText("Open");
			fileOpenMenuItem.setMnemonic('O');
			fileOpenMenuItem.setDisplayedMnemonicIndex(0);
			fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
			fileOpenMenuItem.addActionListener(eventHandler);
		}
		return fileOpenMenuItem;
	}

	private JMenuItem getFileExtractMenuItem() {
		if (fileExtractMenuItem == null) {
			fileExtractMenuItem = new JMenuItem();
			fileExtractMenuItem.setText("Extract downloaded robot for editing");
			fileExtractMenuItem.setMnemonic('t');
			fileExtractMenuItem.setDisplayedMnemonicIndex(2);
			fileExtractMenuItem.addActionListener(eventHandler);
		}
		return fileExtractMenuItem;
	}

	/**
	 * Return the fileSaveAsMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getFileSaveAsMenuItem() {
		if (fileSaveAsMenuItem == null) {
			fileSaveAsMenuItem = new JMenuItem();
			fileSaveAsMenuItem.setText("Save As");
			fileSaveAsMenuItem.setMnemonic('A');
			fileSaveAsMenuItem.setDisplayedMnemonicIndex(5);
			fileSaveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK));
			fileSaveAsMenuItem.addActionListener(eventHandler);
		}
		return fileSaveAsMenuItem;
	}

	/**
	 * Return the fileSaveMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getFileSaveMenuItem() {
		if (fileSaveMenuItem == null) {
			fileSaveMenuItem = new JMenuItem();
			fileSaveMenuItem.setText("Save");
			fileSaveMenuItem.setMnemonic('S');
			fileSaveMenuItem.setDisplayedMnemonicIndex(0);
			fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
			fileSaveMenuItem.addActionListener(eventHandler);
		}
		return fileSaveMenuItem;
	}

	/**
	 * Return the functionsComingSoonMenuItem
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getFunctionsComingSoonMenuItem() {
		if (functionsComingSoonMenuItem == null) {
			functionsComingSoonMenuItem = new JMenuItem();
			functionsComingSoonMenuItem.setText("Coming Soon");
			functionsComingSoonMenuItem.setEnabled(false);
			functionsComingSoonMenuItem.addActionListener(eventHandler);
		}
		return functionsComingSoonMenuItem;
	}

	/**
	 * Return the functionsMenu
	 *
	 * @return JMenu
	 */
	private JMenu getFunctionsMenu() {
		if (functionsMenu == null) {
			functionsMenu = new JMenu();
			functionsMenu.setText("Functions");
			functionsMenu.add(getFunctionsComingSoonMenuItem());
		}
		return functionsMenu;
	}

	/**
	 * Return the Help Menu.
	 *
	 * @return JMenu
	 */
	@Override
	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.setMnemonic('H');
			helpMenu.setDisplayedMnemonicIndex(0);
			helpMenu.add(getHelpRobocodeApiMenuItem());
		}
		return helpMenu;
	}

	/**
	 * Return the helpRobocodeApiMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getHelpRobocodeApiMenuItem() {
		if (helpRobocodeApiMenuItem == null) {
			helpRobocodeApiMenuItem = new JMenuItem();
			helpRobocodeApiMenuItem.setText("Robocode API");
			helpRobocodeApiMenuItem.setMnemonic('A');
			helpRobocodeApiMenuItem.setDisplayedMnemonicIndex(9);
			helpRobocodeApiMenuItem.addActionListener(eventHandler);
		}
		return helpRobocodeApiMenuItem;
	}

	/**
	 * Return the Edit Menu.
	 *
	 * @return JMenu
	 */
	public JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.setMnemonic('E');
			editMenu.setDisplayedMnemonicIndex(0);
			editMenu.add(getEditUndoMenuItem());
			editMenu.add(getEditRedoMenuItem());
			editMenu.addSeparator();
			editMenu.add(getEditCutMenuItem());
			editMenu.add(getEditCopyMenuItem());
			editMenu.add(getEditPasteMenuItem());
			editMenu.add(getEditDeleteMenuItem());
			editMenu.addSeparator();
			editMenu.add(getEditFindMenuItem());
			editMenu.add(getEditFindNextMenuItem());
			editMenu.add(getEditReplaceMenuItem());
			editMenu.addSeparator();
			editMenu.add(getEditSelectAllMenuItem());
		}
		return editMenu;
	}

	/**
	 * Return the editUndoMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditUndoMenuItem() {
		if (editUndoMenuItem == null) {
			editUndoMenuItem = new JMenuItem();
			editUndoMenuItem.setText("Undo");
			editUndoMenuItem.setMnemonic('U');
			editUndoMenuItem.setDisplayedMnemonicIndex(0);
			editUndoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
			editUndoMenuItem.addActionListener(eventHandler);
		}
		return editUndoMenuItem;
	}

	/**
	 * Return the editRedoMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditRedoMenuItem() {
		if (editRedoMenuItem == null) {
			editRedoMenuItem = new JMenuItem();
			editRedoMenuItem.setText("Redo");
			editRedoMenuItem.setMnemonic('R');
			editRedoMenuItem.setDisplayedMnemonicIndex(0);
			editRedoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
			editRedoMenuItem.addActionListener(eventHandler);
		}
		return editRedoMenuItem;
	}

	/**
	 * Return the editCutMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditCutMenuItem() {
		if (editCutMenuItem == null) {
			editCutMenuItem = new JMenuItem();
			editCutMenuItem.setText("Cut");
			editCutMenuItem.setMnemonic('t');
			editCutMenuItem.setDisplayedMnemonicIndex(2);
			editCutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
			editCutMenuItem.addActionListener(eventHandler);
		}
		return editCutMenuItem;
	}

	/**
	 * Return the editCopyMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditCopyMenuItem() {
		if (editCopyMenuItem == null) {
			editCopyMenuItem = new JMenuItem();
			editCopyMenuItem.setText("Copy");
			editCopyMenuItem.setMnemonic('C');
			editCopyMenuItem.setDisplayedMnemonicIndex(0);
			editCopyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
			editCopyMenuItem.addActionListener(eventHandler);
		}
		return editCopyMenuItem;
	}

	/**
	 * Return the editPasteMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditPasteMenuItem() {
		if (editPasteMenuItem == null) {
			editPasteMenuItem = new JMenuItem();
			editPasteMenuItem.setText("Paste");
			editPasteMenuItem.setMnemonic('P');
			editPasteMenuItem.setDisplayedMnemonicIndex(0);
			editPasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
			editPasteMenuItem.addActionListener(eventHandler);
		}
		return editPasteMenuItem;
	}

	/**
	 * Return the editDeleteMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditDeleteMenuItem() {
		if (editDeleteMenuItem == null) {
			editDeleteMenuItem = new JMenuItem();
			editDeleteMenuItem.setText("Delete");
			editDeleteMenuItem.setMnemonic('l');
			editDeleteMenuItem.setDisplayedMnemonicIndex(2);
			editDeleteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
			editDeleteMenuItem.addActionListener(eventHandler);
		}
		return editDeleteMenuItem;
	}

	/**
	 * Return the editFindMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditFindMenuItem() {
		if (editFindMenuItem == null) {
			editFindMenuItem = new JMenuItem();
			editFindMenuItem.setText("Find...");
			editFindMenuItem.setMnemonic('F');
			editFindMenuItem.setDisplayedMnemonicIndex(0);
			editFindMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
			editFindMenuItem.addActionListener(eventHandler);
		}
		return editFindMenuItem;
	}

	/**
	 * Return the editFindNextMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditFindNextMenuItem() {
		if (editFindNextMenuItem == null) {
			editFindNextMenuItem = new JMenuItem();
			editFindNextMenuItem.setText("Find Next");
			editFindNextMenuItem.setMnemonic('N');
			editFindNextMenuItem.setDisplayedMnemonicIndex(5);
			editFindNextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
			editFindNextMenuItem.addActionListener(eventHandler);
		}
		return editFindNextMenuItem;
	}

	/**
	 * Return the editReplaceMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditReplaceMenuItem() {
		if (editReplaceMenuItem == null) {
			editReplaceMenuItem = new JMenuItem();
			editReplaceMenuItem.setText("Replace...");
			editReplaceMenuItem.setMnemonic('R');
			editReplaceMenuItem.setDisplayedMnemonicIndex(0);
			editReplaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
			editReplaceMenuItem.addActionListener(eventHandler);
		}
		return editReplaceMenuItem;
	}

	/**
	 * Return the editSelectAllMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getEditSelectAllMenuItem() {
		if (editSelectAllMenuItem == null) {
			editSelectAllMenuItem = new JMenuItem();
			editSelectAllMenuItem.setText("Select All");
			editSelectAllMenuItem.setMnemonic('A');
			editSelectAllMenuItem.setDisplayedMnemonicIndex(7);
			editSelectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
			editSelectAllMenuItem.addActionListener(eventHandler);
		}
		return editSelectAllMenuItem;
	}

	/**
	 * Return the Window Menu.
	 *
	 * @return JMenu
	 */
	public JMenu getWindowMenu() {
		if (windowMenu == null) {
			windowMenu = new JMenu();
			windowMenu.setText("Window");
			windowMenu.setMnemonic('W');
			windowMenu.setDisplayedMnemonicIndex(0);
			// If you add more items to this menu, you need to update the
			// constants in WindowMenuItem, too, or the dynamic part of the
			// window menu won't operate correctly.
			windowMenu.add(getWindowCloseMenuItem());
			windowMenu.add(getWindowCloseAllMenuItem());
			windowMenu.addSeparator();
			// stuff will be inserted here...
			windowMenu.add(getWindowWindowsDialogMenuItem());
		}
		return windowMenu;
	}

	/**
	 * Return the windowCloseMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getWindowCloseMenuItem() {
		if (windowCloseMenuItem == null) {
			windowCloseMenuItem = new JMenuItem();
			windowCloseMenuItem.setText("Close");
			windowCloseMenuItem.setMnemonic('C');
			windowCloseMenuItem.setDisplayedMnemonicIndex(0);
			windowCloseMenuItem.addActionListener(eventHandler);
		}
		return windowCloseMenuItem;
	}

	/**
	 * Return the windowCloseAllMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getWindowCloseAllMenuItem() {
		if (windowCloseAllMenuItem == null) {
			windowCloseAllMenuItem = new JMenuItem();
			windowCloseAllMenuItem.setText("Close All");
			windowCloseAllMenuItem.setMnemonic('A');
			windowCloseAllMenuItem.setDisplayedMnemonicIndex(6);
			windowCloseAllMenuItem.addActionListener(eventHandler);
		}
		return windowCloseAllMenuItem;
	}

	/**
	 * Return the windowWindowsMenuItem.
	 *
	 * @return JMenuItem
	 */
	private JMenuItem getWindowWindowsDialogMenuItem() {
		if (windowWindowsDialogMenuItem == null) {
			windowWindowsDialogMenuItem = new WindowMenuItem();
			windowWindowsDialogMenuItem.addActionListener(eventHandler);
		}
		return windowWindowsDialogMenuItem;
	}

	/**
	 * Return the windowWindowsMenuItem.
	 *
	 * @return JMenuItem
	 */
	public MoreWindowsDialog getMoreWindowsDialog() {
		if (moreWindowsDialog == null) {
			moreWindowsDialog = new MoreWindowsDialog(editor);
		}
		return moreWindowsDialog;
	}

	public void helpRobocodeApiActionPerformed() {
		editor.showHelpApi();
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		add(getFileMenu());
		add(getEditMenu());
		add(getCompilerMenu());
		add(getWindowMenu());
		add(getHelpMenu());
	}
}
