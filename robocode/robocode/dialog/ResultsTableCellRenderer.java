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
 *     Flemming N. Larsen
 *     - Removed from the robocode.dialog.ResultsDialog into a file of it's own
 *     - Cleanup
 *******************************************************************************/
package robocode.dialog;


import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.SystemColor;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


/**
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
@SuppressWarnings("serial")
public class ResultsTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {

	private boolean isBordered;

	public ResultsTableCellRenderer(boolean isBordered) {
		super();
		this.isBordered = isBordered;
		setHorizontalAlignment(SwingConstants.CENTER);
		setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		if (isBordered) {
			setBorder(new EtchedBorder(EtchedBorder.RAISED));
			setBackground(SystemColor.menu);
			setForeground(SystemColor.menuText);
		} else if (isSelected) {
			setBackground(SystemColor.textHighlight);
			setForeground(SystemColor.textHighlightText);
		} else {
			setBackground(SystemColor.text);
			setForeground(SystemColor.textText);
		}
		setText(value.toString());

		return this;
	}
}
