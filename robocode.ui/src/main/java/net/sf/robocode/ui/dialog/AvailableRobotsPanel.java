/**
 * Copyright (c) 2001-2019 Mathew A. Nelson and Robocode contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * https://robocode.sourceforge.io/license/epl-v10.html
 */
package net.sf.robocode.ui.dialog;


import net.sf.robocode.repository.IRobotSpecItem;
import net.sf.robocode.ui.util.ShortcutUtil;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

	private final List<ItemWrapper> availableRobots = new CopyOnWriteArrayList<ItemWrapper>();
	private List<IRobotSpecItem> robotList = new CopyOnWriteArrayList<IRobotSpecItem>();
	private final List<String> availablePackages = new CopyOnWriteArrayList<String>();

	private JScrollPane availableRobotsScrollPane;
	private JList availableRobotsList;

	private final JButton actionButton;

	private final JList actionList;
	private JList availablePackagesList;

	private JScrollPane availablePackagesScrollPane;

	private RobotNameCellRenderer robotNameCellRenderer;

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

	public List<ItemWrapper> getAvailableRobots() {
		return availableRobots;
	}

	public List<IRobotSpecItem> getRobotList() {
		return robotList;
	}

	public List<ItemWrapper> getSelectedRobots() {
		List<ItemWrapper> selected = new ArrayList<ItemWrapper>();

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

			availableRobotsList.setCellRenderer(getRobotNameCellRenderer());

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

			// Bug fix [2975871] - Minor visual bug - Currently selected robot gets covered
			availableRobotsScrollPane.addComponentListener(new ComponentAdapter() {
				public void componentResized(ComponentEvent e) {
					getAvailableRobotsList().ensureIndexIsVisible(getAvailableRobotsList().getSelectedIndex());
				}
			});
		}
		return availableRobotsScrollPane;
	}

	public void setRobotList(List<IRobotSpecItem> robotListList) {
		robotList = robotListList;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				availablePackages.clear();
				availableRobots.clear();

				if (robotList == null) {
					robotList = new CopyOnWriteArrayList<IRobotSpecItem>();
					availablePackages.add("One moment please...");
					((AvailablePackagesModel) getAvailablePackagesList().getModel()).changed();
					getAvailablePackagesList().clearSelection();
					((AvailableRobotsModel) getAvailableRobotsList().getModel()).changed();
				} else {
					availablePackages.add("(All)");
					String packageName;

					for (IRobotSpecItem robotSpec : robotList) {
						packageName = robotSpec.getFullPackage();
						if (packageName == null) {
							continue;
						}
						if (!availablePackages.contains(packageName)) {
							availablePackages.add(packageName);
						}
					}
					availablePackages.add("(No package)");

					for (IRobotSpecItem robotSpec : robotList) {
						availableRobots.add(new ItemWrapper(robotSpec));
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
				for (IRobotSpecItem robotItem : robotList) {
					getRobotNameCellRenderer().setUseShortName(useShortName);
					availableRobots.add(new ItemWrapper(robotItem));
				}
				break;
			}
			// Single package.
			for (IRobotSpecItem robotItem : robotList) {
				getRobotNameCellRenderer().setUseShortName(useShortName);

				if ((robotItem.getFullPackage() == null && selectedPackage.equals("(No package)"))
						|| selectedPackage.equals(robotItem.getFullPackage())) {
					availableRobots.add(new ItemWrapper(robotItem));
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
			IRobotSpecItem robotSpecification = ((ItemWrapper) getAvailableRobotsList().getModel().getElementAt(sel[0])).getItem();

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

	private RobotNameCellRenderer getRobotNameCellRenderer() {
		if (robotNameCellRenderer == null) {
			robotNameCellRenderer = new RobotNameCellRenderer();
		}
		return robotNameCellRenderer;
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

		public ItemWrapper getElementAt(int which) {
			return availableRobots.get(which);
		}
	}


	/**
	 * Is there because of keyboard navigation is tied to toString() method
	 */
	public static class ItemWrapper {
		private final IRobotSpecItem item;

		public ItemWrapper(IRobotSpecItem item) {
			this.item = item;
		}

		public IRobotSpecItem getItem() {
			return item;
		}

		// Used writing the robot name in JList. Is used for keyboard typing in JList to find robot. Bugfix for [2658090]
		public String toString() {
			return item.getUniqueShortClassNameWithVersion();
		}
	}


	/**
	 * RobotNameCellRender, which renders the list cells with "Team: " prefix, and with or without package names.
	 * This is cheating, as the ItemWrapper.toString() delivers the string used for keyboard navigation etc.
	 */
	private static class RobotNameCellRenderer extends JLabel implements ListCellRenderer {
		private boolean useShortName = false;

		public RobotNameCellRenderer() {
			setOpaque(true);
		}

		public void setUseShortName(boolean useShortNames) {
			this.useShortName = useShortNames;
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

			setComponentOrientation(list.getComponentOrientation());

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			String text;

			if (value instanceof ItemWrapper) {
				IRobotSpecItem item = ((ItemWrapper) value).getItem();

				text = (item.isTeam() ? "Team: " : "");
				text += useShortName
						? item.getUniqueShortClassNameWithVersion()
						: item.getUniqueFullClassNameWithVersion();
			} else {
				text = value.toString();
			}
			setText(text);

			setEnabled(list.isEnabled());
			setFont(list.getFont());

			return this;
		}
	}
}
