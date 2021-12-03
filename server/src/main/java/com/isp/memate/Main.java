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
 * Starts the server and connects every user with its own socket.
 *
 * @author nwe
 * @since 23.10.2019
 */

public class Main
{
  /**
   * @param args Path for the databse
   */
  public static void main( final String args[] )
  {
    boolean debug = false;
    String dataBasePath = Database.getTargetFolder().toFile().toString() + File.separator + "MeMate.db";
    for ( int i = 0; i < args.length; i++ )
    {
      if ( "-debug".equals( args[ i ] ) && "true".equals( args[ i + 1 ] ) )
      {
        debug = true;
        ServerLog.newLog( logType.INFO, "Debug-Mode activated" );
      }
      if ( "-dataDir".equals( args[ i ] ) )
      {
        dataBasePath = args[ i + 1 ];
      }
    }

    if ( !debug )
    {
      new SendServerInformationsToClients().start();
    }
    ServerSocket serverSocket = null;
    Socket socket = null;
    try
    {
      serverSocket = new ServerSocket( debug ? 3142 : 3141 ); //Default is 3141 - Debug is 3142
      ServerLog.newLog( logType.INFO, "Starte MateServer auf Port: " + serverSocket.getLocalPort() );
    }
    catch ( final IOException e )
    {
      ServerLog.newLog( logType.ERROR, "Der Server konnte nicht gestartet werden" );
      e.printStackTrace();
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
      final SocketThread socketThread = new SocketThread( socket, database );
      SocketPool.addSocket( socketThread );
      socketThread.start();
    }
  }
}