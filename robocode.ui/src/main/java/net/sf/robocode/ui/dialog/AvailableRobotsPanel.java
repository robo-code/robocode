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
 *     Flemming N. Larsen
 *     - Code cleanup
 *     - Ported to Java 5
 *     - Replaced FileSpecificationVector with plain Vector
 *     - Replaced synchronizedList on lists for availableRobots, robotList, and
 *       availablePackages with a CopyOnWriteArrayList in order to prevent
 *       ConcurrentModificationExceptions when accessing these list via
 *       Iterators using public methods to this class
 *     - Changed the F5 key press for refreshing the list of available robots
 *       into 'shortcut key' + R to comply with other OSes like e.g. Mac OS
 *     Robert D. Maupin
 *     - Replaced old collection types like Vector and Hashtable with
 *       synchronized List and HashMap
 *******************************************************************************/
package net.sf.robocode.ui.dialog;


import net.sf.robocode.repository.IRepositoryItem;
import net.sf.robocode.ui.util.ShortcutUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 * @author Robert D. Maupin (contributor)
 */
@SuppressWarnings("serial")
public class AvailableRobotsPanel extends JPanel {

	private final List<IRepositoryItem> availableRobots = new CopyOnWriteArrayList<IRepositoryItem>();
	private List<IRepositoryItem> robotList = new CopyOnWriteArrayList<IRepositoryItem>();
	private final List<String> availablePackages = new CopyOnWriteArrayList<String>();

	private JScrollPane availableRobotsScrollPane;
	private JList availableRobotsList;

	private final JButton actionButton;

	private final JList actionList;
	private JList availablePackagesList;

	private JScrollPane availablePackagesScrollPane;

	private final RobotSelectionPanel robotSelectionPanel;

	private final String title;

	private final EventHandler eventHandler = new EventHandler();

	public AvailableRobotsPanel(JButton actionButton, String title, JList actionList,
			RobotSelectionPanel robotSelectionPanel) {
		super();
		this.title = title;
		this.actionButton = actionButton;
		this.actionList = actionList;
		this.robotSelectionPanel = robotSelectionPanel;
		initialize();
	}

	private void initialize() {
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
		setLayout(new BorderLayout());

		JPanel top = new JPanel();

		top.setLayout(new GridLayout(1, 2));

		JPanel a = new JPanel();

		a.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Packages"));
		a.setLayout(new BorderLayout());
		a.add(getAvailablePackagesScrollPane());
		a.setPreferredSize(new Dimension(120, 100));
		top.add(a);

		JPanel b = new JPanel();

		b.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Robots"));
		b.setLayout(new BorderLayout());
		b.add(getAvailableRobotsScrollPane());
		b.setPreferredSize(new Dimension(120, 100));
		top.add(b);

		add(top, BorderLayout.CENTER);

		JLabel refreshLabel = new JLabel("Press " + ShortcutUtil.getModifierKeyText() + "+R to refresh");

		refreshLabel.setHorizontalAlignment(SwingConstants.CENTER);
		add(refreshLabel, BorderLayout.SOUTH);
	}

	public List<IRepositoryItem> getAvailableRobots() {
		return availableRobots;
	}

	public List<IRepositoryItem> getRobotList() {
		return robotList;
	}

	public List<IRepositoryItem> getSelectedRobots() {
		List<IRepositoryItem> selected = new ArrayList<IRepositoryItem>();

		for (int i : getAvailableRobotsList().getSelectedIndices()) {
			selected.add(availableRobots.get(i));
		}
		return selected;
	}

	/**
	 * Return the availableRobotsList.
	 *
	 * @return JList
	 */
	public JList getAvailableRobotsList() {
		if (availableRobotsList == null) {
			availableRobotsList = new JList();
			availableRobotsList.setModel(new AvailableRobotsModel());
			availableRobotsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			MouseListener mouseListener = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					// This does not work in Linux under IBM JRE 1.3.0...
					if (e.getClickCount() >= 2) {
						if (e.getClickCount() % 2 == 0) {
							if (actionButton != null) {
								actionButton.doClick();
							}
						}
					}
				}
			};

			availableRobotsList.addMouseListener(mouseListener);
			availableRobotsList.addListSelectionListener(eventHandler);
		}
		return availableRobotsList;
	}

	/**
	 * Return the JScrollPane1 property value.
	 *
	 * @return JScrollPane
	 */
	private JScrollPane getAvailableRobotsScrollPane() {
		if (availableRobotsScrollPane == null) {
			availableRobotsScrollPane = new JScrollPane();
			availableRobotsScrollPane.setViewportView(getAvailableRobotsList());
		}
		return availableRobotsScrollPane;
	}

	public void setRobotList(List<IRepositoryItem> robotListList) {
		robotList = robotListList;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				availablePackages.clear();
				availableRobots.clear();

				if (robotList == null) {
					robotList = new CopyOnWriteArrayList<IRepositoryItem>();
					availablePackages.add("One moment please...");
					((AvailablePackagesModel) getAvailablePackagesList().getModel()).changed();
					getAvailablePackagesList().clearSelection();
					((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
				} else {
					availablePackages.add("(All)");
					String packageName;

					for (IRepositoryItem robotSpec : robotList) {
						packageName = robotSpec.getFullPackage();
						if (packageName == null) {
							continue;
						}
						if (!availablePackages.contains(packageName)) {
							availablePackages.add(packageName);
						}
					}
					availablePackages.add("(No package)");

					for (IRepositoryItem robotSpec : robotList) {
						availableRobots.add(robotSpec);
					}
					((AvailablePackagesModel) getAvailablePackagesList().getModel()).changed();
					getAvailablePackagesList().setSelectedIndex(0);
					((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
					getAvailablePackagesList().requestFocus();
				}
			}
		});
	}

	private void availablePackagesListSelectionChanged() {
		int sel[] = getAvailablePackagesList().getSelectedIndices();

		boolean useShortName = false;

		availableRobots.clear();
		if (sel.length == 1) {
			useShortName = true;
			getAvailablePackagesList().scrollRectToVisible(getAvailablePackagesList().getCellBounds(sel[0], sel[0]));
		}

		for (int element : sel) {
			String selectedPackage = availablePackages.get(element);

			if (selectedPackage.equals("(All)")) {
				useShortName = false;
				availableRobots.clear();
				for (IRepositoryItem robotItem : robotList) {
					robotItem.setUseShortName(false);
					availableRobots.add(robotItem);
				}
				break;
			}
			// Single package.
			for (IRepositoryItem robotItem : robotList) {
				robotItem.setUseShortName(useShortName);

				if ((robotItem.getFullPackage() == null && selectedPackage.equals("(No package)"))
						|| robotItem.getFullPackage().equals(selectedPackage)) {
					availableRobots.add(robotItem);
				}
			}
		}
		((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
		if (availableRobots.size() > 0) {
			availableRobotsList.setSelectedIndex(0);
			availableRobotsListSelectionChanged();
		}
	}

	private void availableRobotsListSelectionChanged() {
		int sel[] = getAvailableRobotsList().getSelectedIndices();

		if (sel.length == 1) {
			if (actionList != null) {
				actionList.clearSelection();
			}
			IRepositoryItem robotSpecification = (IRepositoryItem) getAvailableRobotsList().getModel().getElementAt(
					sel[0]);

			if (robotSelectionPanel != null) {
				robotSelectionPanel.showDescription(robotSpecification);
			}
		} else {
			if (robotSelectionPanel != null) {
				robotSelectionPanel.showDescription(null);
			}
		}
	}

	public void clearSelection() {
		getAvailableRobotsList().clearSelection();
		((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
	}

	/**
	 * Return the availableRobotsList.
	 *
	 * @return JList
	 */
	private JList getAvailablePackagesList() {
		if (availablePackagesList == null) {
			availablePackagesList = new JList();
			availablePackagesList.setModel(new AvailablePackagesModel());
			availablePackagesList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			availablePackagesList.addListSelectionListener(eventHandler);
		}
		return availablePackagesList;
	}

	/**
	 * Return the availablePackagesScrollPane
	 *
	 * @return JScrollPane
	 */
	private JScrollPane getAvailablePackagesScrollPane() {
		if (availablePackagesScrollPane == null) {
			availablePackagesScrollPane = new JScrollPane();
			availablePackagesScrollPane.setViewportView(getAvailablePackagesList());
		}
		return availablePackagesScrollPane;
	}

	private class EventHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			if (e.getSource() == getAvailableRobotsList()) {
				availableRobotsListSelectionChanged();
			} else if (e.getSource() == getAvailablePackagesList()) {
				availablePackagesListSelectionChanged();
			}
		}
	}


	private class AvailablePackagesModel extends AbstractListModel {
		public void changed() {
			fireContentsChanged(this, 0, getSize());
		}

		public int getSize() {
			return availablePackages.size();
		}

		public String getElementAt(int which) {
			return availablePackages.get(which);
		}
	}


	private class AvailableRobotsModel extends AbstractListModel {
		public void changed() {
			fireContentsChanged(this, 0, getSize());
		}

		public int getSize() {
			return availableRobots.size();
		}

		public IRepositoryItem getElementAt(int which) {
			return availableRobots.get(which);
		}
	}
}
