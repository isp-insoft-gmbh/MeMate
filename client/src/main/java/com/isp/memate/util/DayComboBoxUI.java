/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * @author nwe
 * @since 04.03.2020
 *
 */
public class DayComboBoxUI extends BasicComboBoxUI
{
  @Override
  protected ComboPopup createPopup()
  {
    return new BasicComboPopup( comboBox )
    {
      @Override
      protected JScrollPane createScroller()
      {
        JScrollPane sp = new JScrollPane( list,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        sp.setHorizontalScrollBar( null );
        sp.getVerticalScrollBar().setUI( new BrightScrollBarUI() );
        return sp;
      }

      @SuppressWarnings( "rawtypes" )
      @Override
      public JList getList()
      {
        list.setBackground( Color.white );
        list.setForeground( MeMateUIManager.getForeground( "comboBox" ).getDayColor() );
        list.setSelectionBackground( UIManager.getColor( "AppColor" ) );
        return list;
      }
    };
  }
}