/*******************************************************************************
 * Copyright (c) 2001, 2010 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://robocode.sourceforge.net/license/epl-v10.html
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
	private static final int MAX_PACKAGE_NAME_LENGTH = 16;
	private static final int MAX_ROBOT_NAME_LENGTH = 32;

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
			template = "Unable to read template file: " + FileUtil.getCwd() + File.separatorChar + templateName;
		} finally {
			FileUtil.cleanupStream(fis);
			FileUtil.cleanupStream(dis);
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
		createNewRobot("Robot");
	}

	public void createNewJuniorRobot() {
		createNewRobot("JuniorRobot");
	}

	private void createNewRobot(final String robotType) {
		String message = "Enter the name of your new robot.\nExample: MyFirst" + robotType;
		String name = "";

		boolean done = false;

		while (!done) {
			name = (String) JOptionPane.showInputDialog(this, message, "New " + robotType, JOptionPane.PLAIN_MESSAGE,
					null, null, name);
			if (name == null) {
				return;
			}
			name = name.trim();
			if (name.length() == 0) {
				return;
			}
			if (name.length() > MAX_ROBOT_NAME_LENGTH) {
				name = name.substring(0, MAX_ROBOT_NAME_LENGTH);
				message = "Please choose a shorter name (" + MAX_ROBOT_NAME_LENGTH + " characters or less)";
				continue;
			}

			final char firstLetter = name.charAt(0);

			if (!Character.isJavaIdentifierStart(firstLetter) || Character.isLowerCase(firstLetter)
					|| firstLetter == '$') {
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				message = "Please start your robot name with a big letter (A-Z),\nas should the first letter of every word in the name.\nExample: MyFirstRobot";
				continue;
			}

			done = true;
			for (int i = 1; i < name.length(); i++) {
				char ch = name.charAt(i);

				if (!Character.isJavaIdentifierPart(ch) || ch == '$') {
					done = false;
					break;
				}
			}
			if (!done) {
				message = "Your robot name contains an invalid character.\nPlease use only letters, digits, combination marks and underscores";
				continue;
			}
		}

		message = "Enter a short package name for your new robot. Your initials will work well here.\n"
				+ "Your robot will be put into this package to avoid name conflict with other robots.\n"
				+ "The package name is used to identify your robot(s) in the game, especially if you\n"
				+ "want to let your robot(s) participate in competitions like e.g. RoboRumble@Home.\n"
				+ "Hence, you should enter the same package name for all of your robots."; 

		String packageName = "";

		done = false;
		while (!done) {
			packageName = (String) JOptionPane.showInputDialog(this, message, "Package name for " + name,
					JOptionPane.PLAIN_MESSAGE, null, null, packageName);

			if (packageName == null) {
				return;
			}
			packageName = packageName.trim();
			if (packageName.length() == 0) {
				return;
			}
			if (packageName.length() > MAX_PACKAGE_NAME_LENGTH) {
				packageName = packageName.substring(0, MAX_PACKAGE_NAME_LENGTH);
				message = "Please choose a shorter name (" + MAX_PACKAGE_NAME_LENGTH + " characters or less)";
				continue;
			}

			final char firstLetter = packageName.charAt(0);

			if (!Character.isJavaIdentifierStart(firstLetter) || firstLetter == '$') {
				packageName = packageName.toLowerCase();
				message = "Please start the package name with a small letter (a-z)";
				continue;
			}

			done = true;
			for (int i = 1; i < name.length(); i++) {
				char ch = name.charAt(i);

				if (!(Character.isJavaIdentifierPart(ch) || ch == '.') || ch == '$') {
					done = false;
					break;
				}
			}
			if (!done) {
				message = "Your pakage name contains an invalid character.\nPlease use only small letters, digits, combination marks and underscores";
				continue;
			}
			done = false;

			// Uncommented block, as some robots seems to use upper case names for their package names/initials
			// if (!packageName.equals(packageName.toLowerCase())) {
			// packageName = packageName.toLowerCase();
			// message = "Please use all small letters here.";
			// continue;
			// }

			if (packageName.charAt(packageName.length() - 1) == '.') {
				message = "The package name cannot end with a dot";
				continue;
			}

			boolean wrong_dot_combination = false;
			int lastDotIndex = -1;

			for (int i = 0; i < packageName.length(); i++) {
				if (packageName.charAt(i) == '.') {
					if (i - lastDotIndex == 1) {
						wrong_dot_combination = true;
					}
					lastDotIndex = i;
					break;
				}
			}
			if (wrong_dot_combination) {
				message = "The package name contain two dots next to each other";
				continue;
			}

			if (repositoryManager != null) {
				done = repositoryManager.verifyRobotName(packageName + "." + name, name);
			}
			if (!done) {
				message = "This package is reserved. Please select a different package.";
				continue;
			}
		}

		EditWindow editWindow = new EditWindow(repositoryManager, this, robotsDirectory);

		editWindow.setRobotName(name);
		editWindow.setModified(false);

		String templateName = "templates" + File.separatorChar + "new" + robotType.toLowerCase() + ".tpt";

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
			template = "Unable to read template file: " + FileUtil.getCwd() + File.separatorChar + templateName;
		} finally {
			FileUtil.cleanupStream(fis);
			FileUtil.cleanupStream(dis);
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
		return Container.getComponent(RobocodeCompilerFactory.class).createCompiler(this);
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
			final IWindowManager windowManager = Container.getComponent(IWindowManager.class);

			windowManager.setLookAndFeel();

			RobocodeEditor robocodeEditor = Container.getComponent(RobocodeEditor.class);

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
		Container.getComponent(RobocodeCompilerFactory.class).saveCompilerProperties();
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
