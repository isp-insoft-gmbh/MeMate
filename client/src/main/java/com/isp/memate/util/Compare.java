package com.isp.memate.util;


import com.isp.memate.Cache;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
      final Alert alert = new Alert( AlertType.WARNING );
      alert.setTitle( "Update notwendig" );
      alert.setHeaderText( "Update notwendig" );
      alert.setContentText( "Die Client-Version entspricht nicht der Version des Servers." );
      alert.initOwner( GUIObjects.loginFrame == null ? GUIObjects.mainframe : GUIObjects.loginFrame );
      alert.showAndWait();
      System.exit( 0 );
    }
  }
}