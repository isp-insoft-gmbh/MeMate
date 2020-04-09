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
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 * @author nwe
 * @since 02.03.2020
 *
 */
public class DarkSpinnerUI extends BasicSpinnerUI
{
  @Override
  protected Component createPreviousButton()
  {
    JButton button =
        new BasicArrowButton( SwingConstants.SOUTH, UIManager.getColor( "App.Background" ).brighter(),
            UIManager.getColor( "App.Background" ).brighter(), UIManager.getColor( "App.Background" ).brighter().brighter().brighter(),
            UIManager.getColor( "App.Background" ).brighter() );
    Component c = button;
    c.setName( "Spinner.previousButton" );
    installPreviousButtonListeners( c );
    return c;
  }

  @Override
  protected Component createNextButton()
  {
    JButton button =
        new BasicArrowButton( SwingConstants.NORTH, UIManager.getColor( "App.Background" ).brighter(),
            UIManager.getColor( "App.Background" ).brighter(), UIManager.getColor( "App.Background" ).brighter().brighter().brighter(),
            UIManager.getColor( "App.Background" ).brighter() );
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
    textField.setBackground( UIManager.getColor( "App.Background" ).brighter().brighter() );
    textField.setForeground( Color.white );
    textField.setBorder( new EmptyBorder( 0, 0, 0, 5 ) );
    editor.setInheritsPopupMenu( true );
    return editor;
  }
}

