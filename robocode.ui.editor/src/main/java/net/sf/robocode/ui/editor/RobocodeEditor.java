/*******************************************************************************
 * Copyright (c) 2001, 2008 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/cpl-v10.html
 *
 * Contributors:
 *     Mathew A. Nelson
 *     - Initial API and implementation
 *     Matthew Reeder
 *     - Changes for Find/Replace commands and Window menu
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Bugfixed the removeFromWindowMenu() method which did not remove the
 *       correct item, and did not break out of the loop when it was found.
 *     - Updated to use methods from ImageUtil, FileUtil, Logger, which replaces
 *       methods that have been (re)moved from the robocode.util.Utils class
 *     - Changed to use FileUtil.getRobocodeConfigFile() and
 *       FileUtil.getRobotsDir()
 *     - Added missing close() on FileInputStream, FileOutputStream, and
 *       FileReader
 *******************************************************************************/
package net.sf.robocode.ui.editor;


import net.sf.robocode.core.Container;
import net.sf.robocode.io.FileUtil;
import net.sf.robocode.io.Logger;
import net.sf.robocode.repository.IRepositoryManager;
import net.sf.robocode.ui.BrowserManager;
import net.sf.robocode.ui.IWindowManager;
import net.sf.robocode.ui.IWindowManagerExt;
import net.sf.robocode.ui.gfx.ImageUtil;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


/**
 * @author Mathew A. Nelson (original)
 * @author Matthew Reeder (contributor)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class RobocodeEditor extends JFrame implements Runnable, IRobocodeEditor {
	private JPanel robocodeEditorContentPane;

	private RobocodeEditorMenuBar robocodeEditorMenuBar;
	private JDesktopPane desktopPane;
	public boolean isApplication;

	public final Point origin = new Point();
	public final File robotsDirectory;
	private JToolBar statusBar;
	private JLabel lineLabel;

	private File editorDirectory;
	private final IRepositoryManager repositoryManager;
	private final IWindowManagerExt windowManager;

	private FindReplaceDialog findReplaceDialog;
	private ReplaceAction replaceAction;

	final EventHandler eventHandler = new EventHandler();

	class EventHandler implements ComponentListener {
		public void componentMoved(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {}

		public void componentShown(ComponentEvent e) {
			new Thread(RobocodeEditor.this).start();
		}

		public void componentResized(ComponentEvent e) {}
	}


	/**
	 * Action that launches the Replace dialog.
	 * <p/>
	 * The reason this is needed (and the menubar isn't sufficient) is that
	 * ctrl+H is bound in JTextComponents at a lower level to backspace and in
	 * order to override this, I need to rebind it to an Action when the
	 * JEditorPane is created.
	 */
	class ReplaceAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			replaceDialog();
		}
	}

	public RobocodeEditor(IRepositoryManager repositoryManager, IWindowManager windowManager) {
		super();
		this.windowManager = (IWindowManagerExt) windowManager;
		this.repositoryManager = repositoryManager;
		robotsDirectory = FileUtil.getRobotsDir();
		initialize();
	}

	public void addPlaceShowFocus(JInternalFrame internalFrame) {
		getDesktopPane().add(internalFrame);

		// Center a window
		Dimension screenSize = getDesktopPane().getSize();
		Dimension size = internalFrame.getSize();

		if (size.height > screenSize.height) {
			size.height = screenSize.height;
		}
		if (size.width > screenSize.width) {
			size.width = screenSize.width;
		}

		if (origin.x + size.width > screenSize.width) {
			origin.x = 0;
		}
		if (origin.y + size.height > screenSize.height) {
			origin.y = 0;
		}
		internalFrame.setLocation(origin);
		origin.x += 10;
		origin.y += 10;

		internalFrame.setVisible(true);
		getDesktopPane().moveToFront(internalFrame);
		if (internalFrame instanceof EditWindow) {
			((EditWindow) internalFrame).getEditorPane().requestFocus();
		} else {
			internalFrame.requestFocus();
		}
	}

	public boolean close() {
		JInternalFrame[] frames = getDesktopPane().getAllFrames();

		if (frames != null) {
			for (JInternalFrame frame : frames) {
				if (frame != null) {
					frame.moveToFront();
					if ((frame instanceof EditWindow) && !((EditWindow) frame).fileSave(true)) {
						return false;
					}
				}
			}
		}

		if (isApplication) {
			System.exit(0);
		} else {
			dispose();
		}
		return true;
	}

	public void createNewJavaFile() {
		String packageName = null;

		if (getActiveWindow() != null) {
			packageName = getActiveWindow().getPackage();
		}
		if (packageName == null) {
			packageName = "mypackage";
		}

		EditWindow editWindow = new EditWindow(repositoryManager, this, robotsDirectory);

		editWindow.setModified(false);

		String templateName = "templates" + File.separatorChar + "newjavafile.tpt";

		String template = "";

		File f = new File(FileUtil.getCwd(), templateName);
		int size = (int) (f.length());
		byte buff[] = new byte[size];

		FileInputStream fis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(f);
			dis = new DataInputStream(fis);

			dis.readFully(buff);
			template = new String(buff);
		} catch (IOException e) {
			template = "Unable to read template file: " + FileUtil.getCwd() + File.pathSeparatorChar + templateName;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignored) {}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException ignored) {}
			}
		}

		String name = "MyClass";

		int index = template.indexOf("$");

		while (index >= 0) {
			if (template.substring(index, index + 10).equals("$CLASSNAME")) {
				template = template.substring(0, index) + name + template.substring(index + 10);
				index += name.length();
			} else if (template.substring(index, index + 8).equals("$PACKAGE")) {
				template = template.substring(0, index) + packageName + template.substring(index + 8);
				index += packageName.length();
			} else {
				index++;
			}
			index = template.indexOf("$", index);
		}

		editWindow.getEditorPane().setText(template);
		editWindow.getEditorPane().setCaretPosition(0);
		Document d = editWindow.getEditorPane().getDocument();

		if (d instanceof JavaDocument) {
			((JavaDocument) d).setEditing(true);
		}
		addPlaceShowFocus(editWindow);
	}

	public void createNewRobot() {
		boolean done = false;
		String message = "Type in the name for your robot.\nExample: MyFirstRobot";
		String name = "";

		while (!done) {
			name = (String) JOptionPane.showInputDialog(this, message, "New Robot", JOptionPane.PLAIN_MESSAGE, null,
					null, name);
			if (name == null || name.trim().length() == 0) {
				return;
			}
			if (name.length() > 32) {
				message = "Please choose a shorter name.  (32 characters or less)";
				continue;
			}

			char firstLetter = name.charAt(0);

			if (!Character.isJavaIdentifierStart(firstLetter)) {
				message = "Please start your name with a letter (A-Z)";
				continue;
			}

			done = true;
			for (int t = 1; done && t < name.length(); t++) {
				if (!Character.isJavaIdentifierPart(name.charAt(t))) {
					message = "Your name contains an invalid character.\nPlease use only letters and/or digits.";
					done = false;
					break;
				}
			}
			if (!done) {
				continue;
			}

			if (!Character.isUpperCase(firstLetter)) { // popup
				message = "The first character should be uppercase,\nas should the first letter of all words in the name.\nExample: MyFirstRobot";
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				done = false;
			}
		}

		message = "Please enter your initials.\n" + "To avoid name conflicts with other robots named " + name + ",\n"
				+ "we need a short string to identify this one as one of yours.\n" + "Your initials will work well here,\n"
				+ "but you may choose any short string that you like.\n"
				+ "You should enter the same thing for all your robots.";
		String packageName = "";

		done = false;
		while (!done) {
			packageName = (String) JOptionPane.showInputDialog(this, message, name + " - package name",
					JOptionPane.PLAIN_MESSAGE, null, null, packageName);
			if (packageName == null) {
				return;
			}

			if (packageName.length() > 16) {
				message = "Please choose a shorter name.  (16 characters or less)";
				continue;
			}

			char firstLetter = packageName.charAt(0);

			if (!Character.isJavaIdentifierStart(firstLetter)) {
				message = "Please start with a letter (a-z)";
				continue;
			}

			done = true;
			for (int t = 1; done && t < packageName.length(); t++) {
				if (!Character.isJavaIdentifierPart(packageName.charAt(t))) {
					message = "The string you entered contains an invalid character.\nPlease use only letters and/or digits.";
					done = false;
					break;
				}
			}
			if (!done) {
				continue;
			}

			boolean lowercased = false;

			for (int i = 0; i < packageName.length(); i++) {
				if (!Character.isLowerCase(packageName.charAt(i)) && !Character.isDigit(packageName.charAt(i))) {
					lowercased = true;
					break;
				}
			}
			if (lowercased) {
				packageName = packageName.toLowerCase();
				message = "Please use all lowercase letters here.";
				done = false;
			}

			if (done && repositoryManager != null) {
				done = repositoryManager.verifyRobotName(packageName + "." + name, name);
				if (!done) {
					message = "This package is reserved.  Please select a different package.";
				}
			}
		}

		EditWindow editWindow = new EditWindow(repositoryManager, this, robotsDirectory);

		editWindow.setRobotName(name);
		editWindow.setModified(false);

		String templateName = "templates" + File.separatorChar + "newrobot.tpt";

		String template = "";

		File f = new File(FileUtil.getCwd(), templateName);
		int size = (int) (f.length());
		byte buff[] = new byte[size];
		FileInputStream fis = null;
		DataInputStream dis = null;

		try {
			fis = new FileInputStream(f);
			dis = new DataInputStream(fis);
			dis.readFully(buff);
			dis.close();
			template = new String(buff);
		} catch (IOException e) {
			template = "Unable to read template file: " + FileUtil.getCwd() + File.pathSeparatorChar + templateName;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ignored) {}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException ignored) {}
			}
		}

		int index = template.indexOf("$");

		while (index >= 0) {
			if (template.substring(index, index + 10).equals("$CLASSNAME")) {
				template = template.substring(0, index) + name + template.substring(index + 10);
				index += name.length();
			} else if (template.substring(index, index + 8).equals("$PACKAGE")) {
				template = template.substring(0, index) + packageName + template.substring(index + 8);
				index += packageName.length();
			} else {
				index++;
			}
			index = template.indexOf("$", index);
		}

		editWindow.getEditorPane().setText(template);
		editWindow.getEditorPane().setCaretPosition(0);
		Document d = editWindow.getEditorPane().getDocument();

		if (d instanceof JavaDocument) {
			((JavaDocument) d).setEditing(true);
		}
		addPlaceShowFocus(editWindow);
		if (repositoryManager != null) {
			repositoryManager.refresh();
		}
	}

	public void findDialog() {
		getFindReplaceDialog().showDialog(false);
	}

	public void replaceDialog() {
		getFindReplaceDialog().showDialog(true);
	}

	public EditWindow getActiveWindow() {
		JInternalFrame[] frames = getDesktopPane().getAllFrames();
		EditWindow editWindow = null;

		if (frames != null) {
			for (JInternalFrame frame : frames) {
				if (frame.isSelected()) {
					if (frame instanceof EditWindow) {
						editWindow = (EditWindow) frame;
					}
					break;
				}
			}
		}
		return editWindow;
	}

	public RobocodeCompiler getCompiler() {
		return net.sf.robocode.core.Container.getComponent(RobocodeCompilerFactory.class).createCompiler(this);
	}

	public JDesktopPane getDesktopPane() {
		if (desktopPane == null) {
			desktopPane = new JDesktopPane();
			desktopPane.setBackground(new Color(128, 128, 128));
			desktopPane.setPreferredSize(new Dimension(600, 500));
		}
		return desktopPane;
	}

	private JLabel getLineLabel() {
		if (lineLabel == null) {
			lineLabel = new JLabel();
		}
		return lineLabel;
	}

	private JPanel getRobocodeEditorContentPane() {
		if (robocodeEditorContentPane == null) {
			robocodeEditorContentPane = new JPanel();
			robocodeEditorContentPane.setLayout(new BorderLayout());
			robocodeEditorContentPane.add(getDesktopPane(), "Center");
			robocodeEditorContentPane.add(getStatusBar(), "South");
		}
		return robocodeEditorContentPane;
	}

	private RobocodeEditorMenuBar getRobocodeEditorMenuBar() {
		if (robocodeEditorMenuBar == null) {
			robocodeEditorMenuBar = new RobocodeEditorMenuBar(this);
		}
		return robocodeEditorMenuBar;
	}

	private JToolBar getStatusBar() {
		if (statusBar == null) {
			statusBar = new JToolBar();
			statusBar.setLayout(new BorderLayout());
			statusBar.add(getLineLabel(), BorderLayout.WEST);
		}
		return statusBar;
	}

	public FindReplaceDialog getFindReplaceDialog() {
		if (findReplaceDialog == null) {
			findReplaceDialog = new FindReplaceDialog(this);
		}
		return findReplaceDialog;
	}

	public Action getReplaceAction() {
		if (replaceAction == null) {
			replaceAction = new ReplaceAction();
		}
		return replaceAction;
	}

	public void addToWindowMenu(EditWindow window) {
		WindowMenuItem item = new WindowMenuItem(window, getRobocodeEditorMenuBar().getWindowMenu());

		getRobocodeEditorMenuBar().getMoreWindowsDialog().addWindowItem(item);
	}

	public void removeFromWindowMenu(EditWindow window) {
		for (Component c : getRobocodeEditorMenuBar().getWindowMenu().getMenuComponents()) {
			if (c instanceof WindowMenuItem) {
				WindowMenuItem item = (WindowMenuItem) c;

				if (item.getEditWindow() == window) {
					getRobocodeEditorMenuBar().getWindowMenu().remove(item);
					getRobocodeEditorMenuBar().getMoreWindowsDialog().removeWindowItem(item);
					break;
				}
			}
		}
	}

	private void initialize() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				close();
			}
		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setIconImage(ImageUtil.getImage("/net/sf/robocode/ui/icons/robocode-icon.png"));
		setTitle("Robot Editor");
		setJMenuBar(getRobocodeEditorMenuBar());
		setContentPane(getRobocodeEditorContentPane());
		addComponentListener(eventHandler);
	}

	public static void main(String[] args) {
		try {
			// Set the Look and Feel (LAF)
			final IWindowManager windowManager = net.sf.robocode.core.Container.getComponent(IWindowManager.class);

			windowManager.setLookAndFeel();

			RobocodeEditor robocodeEditor = net.sf.robocode.core.Container.getComponent(RobocodeEditor.class);

			robocodeEditor.isApplication = true; // used for close
			robocodeEditor.pack();
			// Center robocodeEditor
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension size = robocodeEditor.getSize();

			if (size.height > screenSize.height) {
				size.height = screenSize.height;
			}
			if (size.width > screenSize.width) {
				size.width = screenSize.width;
			}
			robocodeEditor.setLocation((screenSize.width - size.width) / 2, (screenSize.height - size.height) / 2);
			robocodeEditor.setVisible(true);
			// 2nd time for bug in some JREs
			robocodeEditor.setVisible(true);
		} catch (Throwable e) {
			Logger.logError("Exception in RoboCodeEditor.main", e);
		}
	}

	public void openRobot() {
		if (editorDirectory == null) {
			editorDirectory = robotsDirectory;
		}
		JFileChooser chooser;

		chooser = new JFileChooser(editorDirectory);
		FileFilter filter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isHidden()) {
					return false;
				}
				if (pathname.isDirectory()) {
					return true;
				}
				String fn = pathname.getName();
				int idx = fn.lastIndexOf('.');
				String extension = "";

				if (idx >= 0) {
					extension = fn.substring(idx);
				}
				return extension.equalsIgnoreCase(".java");
			}

			@Override
			public String getDescription() {
				return "Robots";
			}
		};

		chooser.setFileFilter(filter);
		int rv = chooser.showOpenDialog(this);

		if (rv == JFileChooser.APPROVE_OPTION) {
			String robotFilename = chooser.getSelectedFile().getPath();

			editorDirectory = chooser.getSelectedFile().getParentFile();

			FileReader fileReader = null;

			try {
				fileReader = new FileReader(robotFilename);

				EditWindow editWindow = new EditWindow(repositoryManager, this, robotsDirectory);

				editWindow.getEditorPane().read(fileReader, new File(robotFilename));
				editWindow.getEditorPane().setCaretPosition(0);
				editWindow.setFileName(robotFilename);
				editWindow.setModified(false);
				Document d = editWindow.getEditorPane().getDocument();

				if (d instanceof JavaDocument) {
					((JavaDocument) d).setEditing(true);
				}
				addPlaceShowFocus(editWindow);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.toString());
				Logger.logError(e);
			} finally {
				if (fileReader != null) {
					try {
						fileReader.close();
					} catch (IOException ignored) {}
				}
			}
		}
	}

	public void extractRobot() {
		windowManager.showRobotExtractor(this);
	}

	public void run() {
		getCompiler();
	}

	public void saveAsRobot() {
		EditWindow editWindow = getActiveWindow();

		if (editWindow != null) {
			editWindow.fileSaveAs();
		}
	}

	public void resetCompilerProperties() {
		CompilerProperties props = Container.getComponent(RobocodeCompilerFactory.class).getCompilerProperties();

		props.resetCompiler();
		RobocodeCompilerFactory.saveCompilerProperties();
		getCompiler();
	}

	public void saveRobot() {
		EditWindow editWindow = getActiveWindow();

		if (editWindow != null) {
			editWindow.fileSave(false);
		}
	}

	public void setLineStatus(int line) {
		if (line >= 0) {
			getLineLabel().setText("Line: " + (line + 1));
		} else {
			getLineLabel().setText("");
		}
	}

	public void showHelpApi() {
		String helpurl = "file:" + new File(FileUtil.getCwd(), "").getAbsoluteFile() + File.separator + "javadoc"
				+ File.separator + "index.html";

		try {
			BrowserManager.openURL(helpurl);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Unable to open browser!",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void setSaveFileMenuItemsEnabled(boolean enabled) {
		robocodeEditorMenuBar.getFileSaveAsMenuItem().setEnabled(enabled);
		robocodeEditorMenuBar.getFileSaveMenuItem().setEnabled(enabled);
	}
}
