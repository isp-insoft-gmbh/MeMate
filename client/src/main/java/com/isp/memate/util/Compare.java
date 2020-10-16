package com.isp.memate.util;

import javax.swing.FocusManager;
import javax.swing.JOptionPane;

import com.isp.memate.Cache;

public class Compare
{
  /**
   * Überprüft Server- und Clientversion, wenn diese nicht übereinstimmen folgt
   * ein Dialog.
   *
   * @param clientVersion Version des Clients
   */
  public static void checkVersion()
  {
    Cache cache = Cache.getInstance();
    String serverVersion = cache.getServerVersion();
    String clientVersion = cache.getClientVersion();
    ClientLog.newLog( "CHECK VERSION" );
    ClientLog.newLog( "Server: " + serverVersion );
    ClientLog.newLog( "Client: " + clientVersion );
    if ( !serverVersion.equals( clientVersion ) )
    {
      JOptionPane
          .showMessageDialog( FocusManager.getCurrentManager().getActiveWindow(),
              "Es sind Updates verfügbar.\nInstallierte Produkt-Version: " + clientVersion
                  + "\nServer-Version: " + serverVersion,
              "Update", JOptionPane.ERROR_MESSAGE, null );
    }
  }
}
