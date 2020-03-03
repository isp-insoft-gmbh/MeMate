/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 * @author nwe
 * @since 02.03.2020
 *
 */
public class DaySpinnerUI extends BasicSpinnerUI
{
  @Override
  protected Component createPreviousButton()
  {
    JButton button = new BasicArrowButton( SwingConstants.SOUTH, new Color( 215, 215, 215 ), new Color( 215, 215, 215 ), Color.black,
        new Color( 215, 215, 215 ) );
    Component c = button;
    c.setName( "Spinner.previousButton" );
    installPreviousButtonListeners( c );
    return c;
  }

  @Override
  protected Component createNextButton()
  {
    JButton button = new BasicArrowButton( SwingConstants.NORTH, new Color( 215, 215, 215 ), new Color( 215, 215, 215 ), Color.black,
        new Color( 215, 215, 215 ) );
    Component c = button;
    c.setName( "Spinner.nextButton" );
    installNextButtonListeners( c );
    return c;
  }

  @Override
  protected JComponent createEditor()
  {
    JComponent textField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
    JComponent editor = spinner.getEditor();
    textField.setBackground( Color.white );
    textField.setForeground( Color.black );
    editor.setInheritsPopupMenu( true );
    return editor;
  }
}

