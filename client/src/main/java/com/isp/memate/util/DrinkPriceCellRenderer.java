package com.isp.memate.util;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DrinkPriceCellRenderer extends DefaultTableCellRenderer
{
  public DrinkPriceCellRenderer()
  {
    super();
    setHorizontalAlignment( JLabel.CENTER );
  }

  @Override
  public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
  {
    JLabel label = (JLabel) super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
    label.setText( String.format( "%.2f€", value ) );
    return label;
  }
}
