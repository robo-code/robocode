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
 *     Flemming N. Larsen
 *     - Removed from the robocode.dialog.ResultsDialog into a file of it's own
 *******************************************************************************/
package robocode.dialog;


import java.awt.*;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


/**
 * @author Mathew A. Nelson (original)
 */
public class ResultsTableCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
	boolean isBordered;

	public ResultsTableCellRenderer(boolean isBordered) {
		super();
		this.setHorizontalAlignment(JLabel.CENTER);
		this.isBordered = isBordered;
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));	    
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) {
		if (isBordered) {
			this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			this.setBackground(SystemColor.menu);
			this.setForeground(SystemColor.menuText);
		} else if (isSelected) {
			this.setBackground(SystemColor.textHighlight);
			this.setForeground(SystemColor.textHighlightText);
		} else {
			this.setBackground(SystemColor.text);
			this.setForeground(SystemColor.textText);
		}
		this.setText(value.toString());
		return this;
	}
}