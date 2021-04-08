/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * Die Klasse sorgt dafür, dass die Title der Spalten zentriert sind.
 * 
 * @author nwe
 * @since 29.01.2020
 *
 */
public class HorizontalAlignmentHeaderRenderer implements TableCellRenderer
{
  private int horizontalAlignment = SwingConstants.LEFT;

  public HorizontalAlignmentHeaderRenderer( int horizontalAlignment )
  {
    this.horizontalAlignment = horizontalAlignment;
  }

  @Override
  public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
                                                  boolean hasFocus, int row, int column )
  {
    TableCellRenderer r = table.getTableHeader().getDefaultRenderer();
    JLabel l = (JLabel) r.getTableCellRendererComponent(
        table, value, isSelected, hasFocus, row, column );
    l.setHorizontalAlignment( horizontalAlignment );
    return l;
  }
}
