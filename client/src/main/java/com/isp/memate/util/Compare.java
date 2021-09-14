package com.isp.memate.util;

import javax.swing.JOptionPane;

import com.isp.memate.Cache;

public class Compare
{
  /**
   * Compares sever and client version. If these are different the user will see an update-Dialog and the
   * application gets close.
   */
  public static void checkVersion()
  {
    final Cache cache = Cache.getInstance();
    final String serverVersion = cache.getServerVersion();
    final String clientVersion = cache.getClientVersion();
    ClientLog.newLog( "CHECK VERSION" );
    ClientLog.newLog( "Server: " + serverVersion );
    ClientLog.newLog( "Client: " + clientVersion );
    if ( serverVersion == null || !serverVersion.equals( clientVersion ) )
    {
      JOptionPane
          .showMessageDialog( GUIObjects.loginFrame,
              "Die Client-Version entspricht nicht der Version des Servers.\nBitte auf den neusten Client updaten.",
              "Update", JOptionPane.WARNING_MESSAGE, null );
      System.exit( 0 );
    }
  }
}
