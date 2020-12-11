package com.isp.memate;

import java.awt.Cursor;
import com.isp.memate.util.Compare;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.PropertyHelper;

/**
 * Die Mainklasse setzt das Look and Feel und öffnet den LoginFrame, wenn es keine SessionID gibt oder sie
 * abgelaufen ist.
 *
 * @author nwe
 * @since 15.10.2019
 */
class Main
{
  private final static ServerCommunication serverCommunication = ServerCommunication.getInstance();

  public static void main( final String[] args )
  {
    PropertyHelper.createPropFile();
    MeMateUIManager.init();

    if ( PropertyHelper.isSessionIDValid() )
    {
      showMainframe();
    }
    else
    {
      showLogin();
    }
  }

  private static void showMainframe()
  {
    final Mainframe mainframe = new Mainframe();
    mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
    mainframe.setVisible( true );
    mainframe.addActionBar();
    serverCommunication.startDrinkInfoTimer();
    serverCommunication.tellServerToSendHistoryData();
    Compare.checkVersion();
    mainframe.requestFocus();
  }

  private static void showLogin()
  {
    final Login login = new Login();
    login.setVisible( true );
    Compare.checkVersion();
  }
}