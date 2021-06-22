package com.isp.memate.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;


/**
 * Simple JDialog with Escape-Listener.
 * 
 * @author nwe
 * @since 22.06.2021
 */
public class MeMateDialog extends JDialog
{
  @Override
  protected JRootPane createRootPane()
  {
    ActionListener actionListener = new ActionListener()
    {
      public void actionPerformed( ActionEvent actionEvent )
      {
        setVisible( false );
      }
    };
    JRootPane rootPane = new JRootPane();
    KeyStroke stroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 );
    rootPane.registerKeyboardAction( actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW );
    return rootPane;
  }
}
