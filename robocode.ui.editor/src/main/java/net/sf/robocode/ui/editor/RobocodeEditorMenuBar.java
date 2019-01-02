/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.editor;


import net.sf.robocode.ui.dialog.WindowUtil;
import static net.sf.robocode.ui.util.ShortcutUtil.MENU_SHORTCUT_KEY_MASK;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;


/**
 * Menu bar for the Robocode source code editor.
 * 
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Matthew Reeder (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeEditorMenuBar extends JMenuBar {

	private final RobocodeEditor editor;
	private final EventHandler eventHandler = new EventHandler();

	private class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final Object source = e.getSource();
			
			if (source == RobocodeEditorMenuBar.this.getFileNewRobotMenuItem()) {
				fileNewRobotActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getFileNewJuniorRobotMenuItem()) {
				fileNewJuniorRobotActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getFileNewJavaFileMenuItem()) {
				fileNewJavaFileActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getFileOpenMenuItem()) {
				fileOpenActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getFileExtractMenuItem()) {
				fileExtractActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getFileSaveMenuItem()) {
				fileSaveActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getFileSaveAsMenuItem()) {
				fileSaveAsActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getFileExitMenuItem()) {
				fileExitActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getCompilerCompileMenuItem()) {
				compilerCompileActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getCompilerOptionsPreferencesMenuItem()) {
				compilerOptionsPreferencesActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getCompilerOptionsResetCompilerMenuItem()) {
				compilerOptionsResetCompilerActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getHelpRobocodeApiMenuItem()) {
				helpRobocodeApiActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditUndoMenuItem()) {
				editUndoActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditRedoMenuItem()) {
				editRedoActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditCutMenuItem()) {
				editCutActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditCopyMenuItem()) {
				editCopyActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditPasteMenuItem()) {
				editPasteActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditDeleteMenuItem()) {
				editDeleteActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditSelectAllMenuItem()) {
				editSelectAllActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditFindMenuItem()) {
				editFindActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditFindNextMenuItem()) {
				editFindNextActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getEditReplaceMenuItem()) {
				editReplaceActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getWindowCloseMenuItem()) {
				windowCloseActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getWindowCloseAllMenuItem()) {
				windowCloseAllActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getWindowWindowsDialogMenuItem()) {
				windowMoreWindowsActionPerformed();
			} else if (source == RobocodeEditorMenuBar.this.getViewEditorThemeConfigDialogMenuItem()) {
				viewEditorThemeConfigDialogActionPerformed();
			}
		}
	}

	// File menu
	private JMenu fileMenu;
	private JMenuItem fileOpenMenuItem;
	private JMenuItem fileExtractMenuItem;
	private JMenuItem fileSaveMenuItem;
	private JMenuItem fileSaveAsMenuItem;
	private JMenuItem fileExitMenuItem;
	private JMenuItem fileNewJavaFileMenuItem;
	private JMenu fileNewMenu;
	private JMenuItem fileNewRobotMenuItem;
	private JMenuItem fileNewJuniorRobotMenuItem;

	// Compiler menu
	private JMenuItem compilerCompileMenuItem;
	private JMenu compilerMenu;
	private JMenu compilerOptionsMenu;
	private JMenuItem compilerOptionsPreferencesMenuItem;
	private JMenuItem compilerOptionsResetCompilerMenuItem;

	// Edit menu
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

	// View menu
	private JMenu viewMenu;
	private JMenuItem viewEditorThemeConfigDialogMenuItem;
	
	// Window menu
	private JMenu windowMenu;
	private JMenuItem windowCloseMenuItem;
	private JMenuItem windowCloseAllMenuItem;
	private JMenuItem windowWindowsDialogMenuItem;
	private MoreWindowsDialog moreWindowsDialog;

	// Help menu
	private JMenu helpMenu;
	private JMenuItem helpRobocodeApiMenuItem;

	public RobocodeEditorMenuBar(RobocodeEditor editor) {
		super();
		this.editor = editor;
		initialize();
	}

	private void initialize() {
		add(getFileMenu());
		add(getEditMenu());
		add(getViewMenu());
		add(getCompilerMenu());
		add(getWindowMenu());
		add(getHelpMenu());
	}

	private void compilerCompileActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();
		if (editWindow != null) {
			editWindow.compile();
		}
	}

	private void compilerOptionsPreferencesActionPerformed() {
		CompilerPreferencesDialog dialog = new CompilerPreferencesDialog(editor);
		WindowUtil.packCenterShow(editor, dialog);
	}

	private void compilerOptionsResetCompilerActionPerformed() {
		if (JOptionPane.showConfirmDialog(editor,
				"You are about to reset the compiler preferences.  Do you wish to proceed?", "Reset Compiler Preferences",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
				== JOptionPane.NO_OPTION) {
			return;
		}

		new Thread(new Runnable() {
			public void run() {
				editor.resetCompilerProperties();
			}
		}).start();
	}

	private void fileExitActionPerformed() {
		editor.dispose();
	}

	private void fileNewJavaFileActionPerformed() {
		editor.createNewJavaFile();

		getFileSaveMenuItem().setEnabled(true);
		getFileSaveAsMenuItem().setEnabled(true);
	}

	private void fileNewRobotActionPerformed() {
		editor.createNewRobot();

		getFileSaveMenuItem().setEnabled(true);
		getFileSaveAsMenuItem().setEnabled(true);
	}

	private void fileNewJuniorRobotActionPerformed() {
		editor.createNewJuniorRobot();

		getFileSaveMenuItem().setEnabled(true);
		getFileSaveAsMenuItem().setEnabled(true);
	}

	private void fileOpenActionPerformed() {
		editor.openRobot();
	}

	private void fileExtractActionPerformed() {
		editor.extractRobot();
	}

	private void fileSaveActionPerformed() {
		editor.saveRobot();
	}

	private void fileSaveAsActionPerformed() {
		editor.saveAsRobot();
	}

	private void editUndoActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();
		if (editWindow != null) {
			editWindow.getEditorPane().undo();
		}
	}

	private void editRedoActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();
		if (editWindow != null) {
			editWindow.getEditorPane().redo();
		}
	}

	private void editCutActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();

		if (editWindow != null) {
			editWindow.getEditorPane().cut();
		}
	}

	private void editCopyActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();
		if (editWindow != null) {
			editWindow.getEditorPane().copy();
		}
	}

	private void editPasteActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();
		if (editWindow != null) {
			editWindow.getEditorPane().paste();
		}
	}

	private void editDeleteActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();
		if (editWindow != null) {
			editWindow.getEditorPane().replaceSelection(null);
		}
	}

	private void editSelectAllActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();
		if (editWindow != null) {
			editWindow.getEditorPane().selectAll();
		}
	}

	private void editFindActionPerformed() {
		editor.findDialog();
	}

	private void editReplaceActionPerformed() {
		editor.replaceDialog();
	}

	private void editFindNextActionPerformed() {
		editor.getFindReplaceDialog().findNext();
	}

	private void windowCloseActionPerformed() {
		EditWindow editWindow = editor.getActiveWindow();
		if (editWindow != null) {
			editWindow.doDefaultCloseAction();
		}
	}

	private void windowCloseAllActionPerformed() {
		JInternalFrame[] frames = editor.getDesktopPane().getAllFrames();
		if (frames != null) {
			for (JInternalFrame frame : frames) {
				frame.doDefaultCloseAction();
			}
		}
	}

	private void windowMoreWindowsActionPerformed() {
		getMoreWindowsDialog().setVisible(true);
	}

	private void viewEditorThemeConfigDialogActionPerformed() {
		EditorThemeConfigDialog dialog = new EditorThemeConfigDialog(editor);
		WindowUtil.packCenterShow(dialog);
	}

	private JMenuItem getCompilerCompileMenuItem() {
		if (compilerCompileMenuItem == null) {
			compilerCompileMenuItem = new JMenuItem();
			compilerCompileMenuItem.setText("Compile");
			compilerCompileMenuItem.setMnemonic('m');
			compilerCompileMenuItem.setDisplayedMnemonicIndex(2);
			compilerCompileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, MENU_SHORTCUT_KEY_MASK));
			compilerCompileMenuItem.addActionListener(eventHandler);
		}
		return compilerCompileMenuItem;
	}

	private JMenu getCompilerMenu() {
		if (compilerMenu == null) {
			compilerMenu = new JMenu();
			compilerMenu.setText("Compiler");
			compilerMenu.setMnemonic('C');
			compilerMenu.add(getCompilerCompileMenuItem());
			compilerMenu.add(getCompilerOptionsMenu());
		}
		return compilerMenu;
	}

	private JMenu getCompilerOptionsMenu() {
		if (compilerOptionsMenu == null) {
			compilerOptionsMenu = new JMenu();
			compilerOptionsMenu.setText("Options");
			compilerOptionsMenu.setMnemonic('O');
			compilerOptionsMenu.add(getCompilerOptionsPreferencesMenuItem());
			compilerOptionsMenu.add(getCompilerOptionsResetCompilerMenuItem());
		}
		return compilerOptionsMenu;
	}

	private JMenuItem getCompilerOptionsPreferencesMenuItem() {
		if (compilerOptionsPreferencesMenuItem == null) {
			compilerOptionsPreferencesMenuItem = new JMenuItem();
			compilerOptionsPreferencesMenuItem.setText("Preferences");
			compilerOptionsPreferencesMenuItem.setMnemonic('P');
			compilerOptionsPreferencesMenuItem.addActionListener(eventHandler);
		}
		return compilerOptionsPreferencesMenuItem;
	}

	private JMenuItem getCompilerOptionsResetCompilerMenuItem() {
		if (compilerOptionsResetCompilerMenuItem == null) {
			compilerOptionsResetCompilerMenuItem = new JMenuItem();
			compilerOptionsResetCompilerMenuItem.setText("Reset Compiler");
			compilerOptionsResetCompilerMenuItem.setMnemonic('R');
			compilerOptionsResetCompilerMenuItem.addActionListener(eventHandler);
		}
		return compilerOptionsResetCompilerMenuItem;
	}

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

	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setMnemonic('F');
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

	private JMenuItem getFileNewJavaFileMenuItem() {
		if (fileNewJavaFileMenuItem == null) {
			fileNewJavaFileMenuItem = new JMenuItem();
			fileNewJavaFileMenuItem.setText("Java File");
			fileNewJavaFileMenuItem.setMnemonic('J');
			fileNewJavaFileMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_N, MENU_SHORTCUT_KEY_MASK | Event.SHIFT_MASK));
			fileNewJavaFileMenuItem.addActionListener(eventHandler);
		}
		return fileNewJavaFileMenuItem;
	}

	private JMenu getFileNewMenu() {
		if (fileNewMenu == null) {
			fileNewMenu = new JMenu();
			fileNewMenu.setText("New");
			fileNewMenu.setMnemonic('N');
			fileNewMenu.add(getFileNewRobotMenuItem());
			fileNewMenu.add(getFileNewJuniorRobotMenuItem());
			fileNewMenu.add(getFileNewJavaFileMenuItem());
		}
		return fileNewMenu;
	}

	private JMenuItem getFileNewRobotMenuItem() {
		if (fileNewRobotMenuItem == null) {
			fileNewRobotMenuItem = new JMenuItem();
			fileNewRobotMenuItem.setText("Robot");
			fileNewRobotMenuItem.setMnemonic('R');
			fileNewRobotMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, MENU_SHORTCUT_KEY_MASK));
			fileNewRobotMenuItem.addActionListener(eventHandler);
		}
		return fileNewRobotMenuItem;
	}

	private JMenuItem getFileNewJuniorRobotMenuItem() {
		if (fileNewJuniorRobotMenuItem == null) {
			fileNewJuniorRobotMenuItem = new JMenuItem();
			fileNewJuniorRobotMenuItem.setText("JuniorRobot");
			fileNewJuniorRobotMenuItem.setMnemonic('J');
			fileNewJuniorRobotMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_J, MENU_SHORTCUT_KEY_MASK));
			fileNewJuniorRobotMenuItem.addActionListener(eventHandler);
		}
		return fileNewJuniorRobotMenuItem;
	}

	private JMenuItem getFileOpenMenuItem() {
		if (fileOpenMenuItem == null) {
			fileOpenMenuItem = new JMenuItem();
			fileOpenMenuItem.setText("Open");
			fileOpenMenuItem.setMnemonic('O');
			fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, MENU_SHORTCUT_KEY_MASK));
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

	public JMenuItem getFileSaveAsMenuItem() {
		if (fileSaveAsMenuItem == null) {
			fileSaveAsMenuItem = new JMenuItem();
			fileSaveAsMenuItem.setText("Save As");
			fileSaveAsMenuItem.setMnemonic('A');
			fileSaveAsMenuItem.setDisplayedMnemonicIndex(5);
			fileSaveAsMenuItem.setAccelerator(
					KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SHORTCUT_KEY_MASK | Event.SHIFT_MASK));
			fileSaveAsMenuItem.addActionListener(eventHandler);
			fileSaveAsMenuItem.setEnabled(false);
		}
		return fileSaveAsMenuItem;
	}

	public JMenuItem getFileSaveMenuItem() {
		if (fileSaveMenuItem == null) {
			fileSaveMenuItem = new JMenuItem();
			fileSaveMenuItem.setText("Save");
			fileSaveMenuItem.setMnemonic('S');
			fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, MENU_SHORTCUT_KEY_MASK));
			fileSaveMenuItem.addActionListener(eventHandler);
			fileSaveMenuItem.setEnabled(false);
		}
		return fileSaveMenuItem;
	}

	@Override
	public JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.setMnemonic('H');
			helpMenu.add(getHelpRobocodeApiMenuItem());
		}
		return helpMenu;
	}

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

	private JMenu getEditMenu() {
		if (editMenu == null) {
			editMenu = new JMenu();
			editMenu.setText("Edit");
			editMenu.setMnemonic('E');
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

	private JMenuItem getEditUndoMenuItem() {
		if (editUndoMenuItem == null) {
			editUndoMenuItem = new JMenuItem();
			editUndoMenuItem.setText("Undo");
			editUndoMenuItem.setMnemonic('U');
			editUndoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, MENU_SHORTCUT_KEY_MASK));
			editUndoMenuItem.addActionListener(eventHandler);
		}
		return editUndoMenuItem;
	}

	private JMenuItem getEditRedoMenuItem() {
		if (editRedoMenuItem == null) {
			editRedoMenuItem = new JMenuItem();
			editRedoMenuItem.setText("Redo");
			editRedoMenuItem.setMnemonic('R');
			editRedoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, MENU_SHORTCUT_KEY_MASK));
			editRedoMenuItem.addActionListener(eventHandler);
		}
		return editRedoMenuItem;
	}

	private JMenuItem getEditCutMenuItem() {
		if (editCutMenuItem == null) {
			editCutMenuItem = new JMenuItem();
			editCutMenuItem.setText("Cut");
			editCutMenuItem.setMnemonic('t');
			editCutMenuItem.setDisplayedMnemonicIndex(2);
			editCutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, MENU_SHORTCUT_KEY_MASK));
			editCutMenuItem.addActionListener(eventHandler);
		}
		return editCutMenuItem;
	}

	private JMenuItem getEditCopyMenuItem() {
		if (editCopyMenuItem == null) {
			editCopyMenuItem = new JMenuItem();
			editCopyMenuItem.setText("Copy");
			editCopyMenuItem.setMnemonic('C');
			editCopyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, MENU_SHORTCUT_KEY_MASK));
			editCopyMenuItem.addActionListener(eventHandler);
		}
		return editCopyMenuItem;
	}

	private JMenuItem getEditPasteMenuItem() {
		if (editPasteMenuItem == null) {
			editPasteMenuItem = new JMenuItem();
			editPasteMenuItem.setText("Paste");
			editPasteMenuItem.setMnemonic('P');
			editPasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, MENU_SHORTCUT_KEY_MASK));
			editPasteMenuItem.addActionListener(eventHandler);
		}
		return editPasteMenuItem;
	}

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

	private JMenuItem getEditFindMenuItem() {
		if (editFindMenuItem == null) {
			editFindMenuItem = new JMenuItem();
			editFindMenuItem.setText("Find...");
			editFindMenuItem.setMnemonic('F');
			editFindMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, MENU_SHORTCUT_KEY_MASK));
			editFindMenuItem.addActionListener(eventHandler);
		}
		return editFindMenuItem;
	}

	private JMenuItem getEditFindNextMenuItem() {
		if (editFindNextMenuItem == null) {
			editFindNextMenuItem = new JMenuItem();
			editFindNextMenuItem.setText("Find Next");
			editFindNextMenuItem.setMnemonic('N');
			editFindNextMenuItem.setDisplayedMnemonicIndex(5);
			editFindNextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, MENU_SHORTCUT_KEY_MASK));
			editFindNextMenuItem.addActionListener(eventHandler);
		}
		return editFindNextMenuItem;
	}

	private JMenuItem getEditReplaceMenuItem() {
		if (editReplaceMenuItem == null) {
			editReplaceMenuItem = new JMenuItem();
			editReplaceMenuItem.setText("Replace...");
			editReplaceMenuItem.setMnemonic('R');
			editReplaceMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, MENU_SHORTCUT_KEY_MASK));
			editReplaceMenuItem.addActionListener(eventHandler);
		}
		return editReplaceMenuItem;
	}

	private JMenuItem getEditSelectAllMenuItem() {
		if (editSelectAllMenuItem == null) {
			editSelectAllMenuItem = new JMenuItem();
			editSelectAllMenuItem.setText("Select All");
			editSelectAllMenuItem.setMnemonic('A');
			editSelectAllMenuItem.setDisplayedMnemonicIndex(7);
			editSelectAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, MENU_SHORTCUT_KEY_MASK));
			editSelectAllMenuItem.addActionListener(eventHandler);
		}
		return editSelectAllMenuItem;
	}

	public JMenu getWindowMenu() {
		if (windowMenu == null) {
			windowMenu = new JMenu();
			windowMenu.setText("Window");
			windowMenu.setMnemonic('W');
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

	private JMenuItem getWindowCloseMenuItem() {
		if (windowCloseMenuItem == null) {
			windowCloseMenuItem = new JMenuItem();
			windowCloseMenuItem.setText("Close");
			windowCloseMenuItem.setMnemonic('C');
			windowCloseMenuItem.addActionListener(eventHandler);
		}
		return windowCloseMenuItem;
	}

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

	private JMenuItem getWindowWindowsDialogMenuItem() {
		if (windowWindowsDialogMenuItem == null) {
			windowWindowsDialogMenuItem = new WindowMenuItem();
			windowWindowsDialogMenuItem.addActionListener(eventHandler);
		}
		return windowWindowsDialogMenuItem;
	}

	public MoreWindowsDialog getMoreWindowsDialog() {
		if (moreWindowsDialog == null) {
			moreWindowsDialog = new MoreWindowsDialog(editor);
		}
		return moreWindowsDialog;
	}

	private void helpRobocodeApiActionPerformed() {
		editor.showHelpApi();
	}

	private JMenu getViewMenu() {
		if (viewMenu == null) {
			viewMenu = new JMenu();
			viewMenu.setText("View");
			viewMenu.setMnemonic('V');

			viewMenu.add(getViewEditorThemeConfigDialogMenuItem());
		}
		return viewMenu;
	}

	private JMenuItem getViewEditorThemeConfigDialogMenuItem() {
		if (viewEditorThemeConfigDialogMenuItem == null) {
			viewEditorThemeConfigDialogMenuItem = new JMenuItem();
			viewEditorThemeConfigDialogMenuItem.setText("Change Editor Theme");
			viewEditorThemeConfigDialogMenuItem.setMnemonic('T');
			viewEditorThemeConfigDialogMenuItem.addActionListener(eventHandler);
		}
		return viewEditorThemeConfigDialogMenuItem;
	}
}
