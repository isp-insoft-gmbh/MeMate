/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.isp.memate.ServerLog.logType;


/**
 * Die Mainklasse startet den Server und verbindet jeden User mit einem eigenen Socket.
 *
 * @author nwe
 * @since 23.10.2019
 */

public class Main
{
  private final static boolean debug = false;

  /**
   * @param args Pfad zur Datenbank
   */
  @SuppressWarnings( { "resource", "unused" } )
  public static void main( final String args[] )
  {
    //For Debug commented out
    if ( !debug )
    {
      new SendServerInformationsToClients().start();
    }
    ServerSocket serverSocket = null;
    Socket socket = null;
    try
    {
      serverSocket = new ServerSocket( debug  ? 3142 : 3141 ); //Default is 3141 - Debug is 3142
      ServerLog.newLog( logType.INFO, "Starte MateServer auf Port: " + serverSocket.getLocalPort() );
    }
    catch ( final IOException e )
    {
      ServerLog.newLog( logType.ERROR, "Der Server konnte nicht gestartet werden" );
      e.printStackTrace();
    }
    final String dataBasePath;
    if ( args.length > 0 )
    {
      dataBasePath = args[ 0 ];
    }
    else
    {
      dataBasePath = Database.getTargetFolder().toFile().toString() + File.separator + "MeMate.db";
    }
    ServerLog.newLog( logType.INFO, "Datenbankverbindung wird hergestellt...." );
    final Database database = new Database( dataBasePath );
    while ( true )
    {
      try
      {
        socket = serverSocket.accept();
      }
      catch ( final IOException e )
      {
        ServerLog.newLog( logType.ERROR, "Es konnte keine Verbindung zwischen Client und Server aufgebaut werden" );
        e.printStackTrace();
      }
      new SocketThread( socket, database ).start();
    }
  }
}