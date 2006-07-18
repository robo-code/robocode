/*******************************************************************************
 * Copyright (c) 2001-2006 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.robocode.net/license/CPLv1.0.html
 * 
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Matthew Reeder
 *     - Changes to facilitate Undo/Redo
 *     - Support for line numbers
 *     - Window menu
 *     - Launching the Replace dialog using ctrl+H
 *     Flemming N. Larsen
 *     - Code cleanup
 *******************************************************************************/
package robocode.editor;


import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.undo.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.*;
import java.beans.*;
import robocode.util.Utils;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder, Flemming N. Larsen (current)
 */
public class EditWindow extends JInternalFrame implements CaretListener, PropertyChangeListener {

	public RobocodeEditorKit editorKit;
	public String fileName;
	public String robotName;
	public boolean modified;
	private RobocodeEditor editor;
	private JEditorPane editorPane;
	private JPanel editWindowContentPane;
	public JFrame frame;
	public File robotsDirectory;
	private JScrollPane scrollPane;

	private LineNumbers lineNumbers;
	private UndoManager undoManager;

	/**
	 * Return the editorPane property value.
	 * 
	 * @return JEditorPane
	 */
	public JEditorPane getEditorPane() {
		if (editorPane == null) {
			editorPane = new JEditorPane();
			editorPane.setFont(new Font("monospaced", 0, 12));
			editorPane.setContentType("text/java");
			editorKit = new RobocodeEditorKit();
			editorPane.setEditorKitForContentType("text/java", editorKit);
			editorPane.setContentType("text/java");
			editorKit.setEditWindow(this);
			editorPane.addCaretListener(this);
			((JavaDocument) editorPane.getDocument()).setEditWindow(this);
			editorPane.getDocument().addUndoableEditListener(getUndoManager());
			editorPane.addPropertyChangeListener(this);

			InputMap im = editorPane.getInputMap();

			// read: hack.
			im.put(KeyStroke.getKeyStroke("ctrl H"), editor.getReplaceAction()); // FIXME: Replace hack with better solution?
		}
		return editorPane;
	}

	public String getFileName() {
		return fileName;
	}

	public String getRobotName() {
		return robotName;
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() {
		try {
			this.addInternalFrameListener(new InternalFrameAdapter() {
				public void internalFrameClosing(InternalFrameEvent e) {
					if (!modified || fileSave(true)) {
						editor.setLineStatus(-1);
						dispose();
					}
					editor.removeFromWindowMenu(EditWindow.this);
					return;
				}

				public void internalFrameDeactivated(InternalFrameEvent e) {
					editor.setLineStatus(-1);
				}

				public void internalFrameIconified(InternalFrameEvent e) {
					editor.setLineStatus(-1);
				}
			});
			setResizable(true);
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			setIconifiable(true);
			setClosable(true);
			setMaximum(false);
			setFrameIcon(new ImageIcon(getClass().getResource("/resources/icons/icon.jpg")));
			setSize(553, 441);
			setMaximizable(true);
			setTitle("Edit Window");
			setContentPane(getEditWindowContentPane());
			editor.addToWindowMenu(this);
		} catch (Throwable e) {
			Utils.log(e);
		}
	}

	public void setFileName(String newFileName) {
		fileName = newFileName;
	}

	public void setModified(boolean modified) {
		if (this.modified == false && modified == true) {
			this.modified = true;
			if (fileName != null) {
				setTitle("Editing - " + fileName + " *");
			} else if (robotName != null) {
				setTitle("Editing - " + robotName + " *");
			} else {
				setTitle("Editing - *");
			}
		} else if (modified == false) {
			this.modified = false;
			if (fileName != null) {
				setTitle("Editing - " + fileName);
			} else if (robotName != null) {
				setTitle("Editing - " + robotName);
			} else {
				setTitle("Editing");
			}
		}
	}

	public void setRobotName(String newRobotName) {
		robotName = newRobotName;
	}

	public EditWindow(RobocodeEditor editor, File robotsDirectory) {
		super();
		this.editor = editor;
		this.robotsDirectory = robotsDirectory;
		initialize();
	}

	public void caretUpdate(CaretEvent e) {
		int lineend = getEditorPane().getDocument().getDefaultRootElement().getElementIndex(e.getDot());

		editor.setLineStatus(lineend);
	}

	/**
	 * Event handler for PropertyChangeListener
	 * 
	 * If the Document changes, adds clears out our UndoManager and attaches it
	 * to the new document.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getOldValue() instanceof JavaDocument) {
			getUndoManager().discardAllEdits();
			((JavaDocument) e.getNewValue()).addUndoableEditListener(getUndoManager());
		}
	}

	public void compile() {
		if (!fileSave(true, true)) {
			error("You must save before compiling.");
			return;
		}
		RobocodeCompiler compiler = editor.getCompiler();

		if (compiler != null) {
			compiler.compile(fileName);
		} else {
			JOptionPane.showMessageDialog(editor, "No compiler installed.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void error(String msg) {
		Object[] options = {
			"OK"
		};

		JOptionPane.showOptionDialog(this, msg, "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null,
				options, options[0]);
	}

	public boolean fileSave(boolean confirm) {
		return fileSave(confirm, false);
	}

	public boolean fileSave(boolean confirm, boolean mustSave) {
		if (confirm) {
			if (!modified) {
				return true;
			}
			String s = fileName;

			if (s == null) {
				s = robotName;
			}
			if (s == null) {
				s = "This file";
			}
			int ok = JOptionPane.showConfirmDialog(this, s + " has been modified.  Do you wish to save it?",
					"Modified file", JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.NO_OPTION) {
				if (mustSave) {
					return false;
				}
				return true;
			}
			if (ok == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		String fileName = getFileName();

		if (fileName == null) {
			return fileSaveAs();
		}

		String reasonableFilename = getReasonableFilename();

		if (reasonableFilename != null) {
			try {
				String a = new File(reasonableFilename).getCanonicalPath();
				String b = new File(fileName).getCanonicalPath();

				if (!a.equals(b)) {
					int ok = JOptionPane.showConfirmDialog(this,
							fileName + " should be saved in: \n" + reasonableFilename
							+ "\n  Would you like to save it there instead?",
							"Name has changed",
							JOptionPane.YES_NO_CANCEL_OPTION);

					if (ok == JOptionPane.CANCEL_OPTION) {
						return false;
					}
					if (ok == JOptionPane.YES_OPTION) {
						if (editor.getManager() != null) {
							editor.getManager().getRobotRepositoryManager().clearRobotList();
						}
						return fileSaveAs();
					}
				}
			} catch (Exception e) {
				Utils.log("Unable to check reasonable filename: " + e);
			}
		}
		File f = new File(fileName);

		try {
			FileWriter writer = new FileWriter(f);

			getEditorPane().write(writer);
			setModified(false);
			writer.close();
		} catch (IOException e) {
			error("Cannot write file: " + e);
			return false;
		}
		return true;
	}

	public boolean fileSaveAs() {
		String fileName; // = editWindow.getFileName();
		String javaFileName = null;
		String saveDir = null;
		String packageTree = null;

		fileName = robotsDirectory.getPath() + File.separatorChar;
		saveDir = fileName;

		try {
			String text = getEditorPane().getText();
			int pIndex = text.indexOf("package ");

			if (pIndex >= 0) {
				int pEIndex = text.indexOf(";", pIndex);

				if (pEIndex > 0) {
					packageTree = text.substring(pIndex + 8, pEIndex) + File.separatorChar;
					packageTree = packageTree.replace('.', File.separatorChar);

					fileName += packageTree;
					saveDir = fileName;
				}
			}

			pIndex = text.indexOf("public class ");
			if (pIndex >= 0) {
				int pEIndex = text.indexOf(" ", pIndex + 13);

				if (pEIndex > 0) {
					int pEIndex2 = text.indexOf("\n", pIndex + 13);

					if (pEIndex2 > 0 && pEIndex2 < pEIndex) {
						pEIndex = pEIndex2;
					}
					javaFileName = text.substring(pIndex + 13, pEIndex).trim() + ".java";
					fileName += javaFileName;
				} else {
					pEIndex = text.indexOf("\n", pIndex + 13);
					if (pEIndex > 0) {
						javaFileName = text.substring(pIndex + 13, pEIndex).trim() + ".java";
						fileName += javaFileName;
					}
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			error("Could not parse package and class names.");
		}

		File f = new File(saveDir);

		if (!f.exists()) {
			int ok = JOptionPane.showConfirmDialog(this,
					"Your robot should be saved in the directory: " + saveDir
					+ "\nThis directory does not exist, would you like to create it?",
					"Create Directory",
					JOptionPane.YES_NO_CANCEL_OPTION);

			if (ok == JOptionPane.YES_OPTION) {
				f.mkdirs();
				f = new File(saveDir);
			}
			if (ok == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}

		JFileChooser chooser;

		chooser = new JFileChooser(f); // .getAbsoluteFile().toString());
		chooser.setCurrentDirectory(f);

		FileFilter filter = new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.isDirectory()) {
					return true;
				}
				String fn = pathname.getName();
				int idx = fn.lastIndexOf('.');
				String extension = "";

				if (idx >= 0) {
					extension = fn.substring(idx);
				}
				if (extension.equalsIgnoreCase(".java")) {
					return true;
				}
				return false;
			}

			public String getDescription() {
				return "Robots";
			}
		};

		chooser.setFileFilter(filter);

		boolean done = false;

		while (!done) {
			done = true;
			if (javaFileName != null) {
				chooser.setSelectedFile(new File(f, javaFileName));
			}
			int rv = chooser.showSaveDialog(this);
			String robotFileName = null;

			if (rv == JFileChooser.APPROVE_OPTION) {
				robotFileName = chooser.getSelectedFile().getPath();
				File outFile = new File(robotFileName);

				if (outFile.exists()) {
					int ok = JOptionPane.showConfirmDialog(this,
							robotFileName + " already exists.  Are you sure you want to replace it?", "Warning",
							JOptionPane.YES_NO_CANCEL_OPTION);

					if (ok == JOptionPane.NO_OPTION) {
						done = false;
						continue;
					}
					if (ok == JOptionPane.CANCEL_OPTION) {
						return false;
					}
				}
				setFileName(robotFileName);
				fileSave(false);
			} else {
				return false;
			}
		}

		return true;
	}

	public RobocodeEditorKit getEditorKit() {
		return editorKit;
	}

	private JPanel getEditWindowContentPane() {
		if (editWindowContentPane == null) {
			editWindowContentPane = new JPanel();
			editWindowContentPane.setLayout(new BorderLayout());
			editWindowContentPane.setDoubleBuffered(true);
			editWindowContentPane.add(getScrollPane(), "Center");
		}
		return editWindowContentPane;
	}

	public JFrame getFrame() {
		return frame;
	}

	public String getPackage() {
		String text = getEditorPane().getText();
		int pIndex = text.indexOf("package ");

		if (pIndex >= 0) {
			int pEIndex = text.indexOf(";", pIndex);

			if (pEIndex > 0) {
				return text.substring(pIndex + 8, pEIndex);
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

	public String getReasonableFilename() {
		String fileName = robotsDirectory.getPath() + File.separatorChar;
		String javaFileName = null;
		String packageTree = null;

		try {
			String text = getEditorPane().getText();
			StringTokenizer tokenizer = new StringTokenizer(text, " \t\r\n;");
			String token = null;
			boolean inComment = false;

			while (tokenizer.hasMoreTokens()) {
				token = tokenizer.nextToken();
				if (!inComment && (token.equals("/*") || token.equals("/**"))) {
					inComment = true;
				}
				if (inComment && (token.equals("*/") || token.equals("**/"))) {
					inComment = false;
				}
				if (inComment) {
					continue;
				}

				if (packageTree == null && token.equals("package")) {
					packageTree = tokenizer.nextToken();
					if (packageTree == null || packageTree.equals("")) {
						return null;
					}
					packageTree = packageTree.replace('.', File.separatorChar);
					packageTree += File.separator;
					fileName += packageTree;
				}
				if (javaFileName == null && token.equals("class")) {
					javaFileName = tokenizer.nextToken() + ".java";
					fileName += javaFileName;
					return fileName;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * Return the scrollPane
	 * 
	 * @return JScrollPane
	 */
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setViewportView(getEditorPane());
			scrollPane.setRowHeaderView(getLineNumbers());
		}
		return scrollPane;
	}

	/**
	 * Return the lineNumbers
	 * @ robocode.editor.LineNumbers
	 */
	private LineNumbers getLineNumbers() {
		if (lineNumbers == null) {
			lineNumbers = new LineNumbers(getEditorPane());
		}
		return lineNumbers;
	}

	/**
	 * Returns the undoManager
	 * @ UndoManager
	 */
	private UndoManager getUndoManager() {
		if (undoManager == null) {
			undoManager = new UndoManager();
		}
		return undoManager;
	}

	/**
	 * Undo
	 */
	public void undo() {
		if (getUndoManager().canUndo()) {
			getUndoManager().undo();
		}
	}

	/**
	 * Redo
	 */
	public void redo() {
		if (getUndoManager().canRedo()) {
			getUndoManager().redo();
		}
	}

	public void scrollToTop() {
		getEditorPane().scrollRectToVisible(new Rectangle(0, 0, 10, 10));
	}

	public void setFrame(JFrame newFrame) {
		frame = newFrame;
	}

	public void success(String msg) {
		Object[] options = {
			"OK"
		};

		JOptionPane.showOptionDialog(this, msg, "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null, options, options[0]);
	}
}
