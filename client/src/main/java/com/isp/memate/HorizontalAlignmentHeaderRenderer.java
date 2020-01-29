/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 * @author nwe
 * @since 29.01.2020
 *
 */
public class HorizontalAlignmentHeaderRenderer implements TableCellRenderer
{
  private int horizontalAlignment = SwingConstants.LEFT;

  @SuppressWarnings( "javadoc" )
  public HorizontalAlignmentHeaderRenderer( int horizontalAlignment )
  {
    this.horizontalAlignment = horizontalAlignment;
  }

  @SuppressWarnings( "javadoc" )
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
