package com.isp.memate;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Die Mainklasse setzt das Look and Feel und öffnet den LoginFrame.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class Main
{
  /**
   * @param args unused
   */
  public static void main( String[] args )
  {
    try
    {
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
    }
    catch ( ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException exception )
    {
      //We don't mind if we aren't able to set a Look and Feel, therefore we just ignore the exceptions.
    }

    Login login = new Login();
    login.setVisible( true );
  }
}