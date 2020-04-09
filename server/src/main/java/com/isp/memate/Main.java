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
  /**
   * @param args Pfad zur Datenbank
   */
  @SuppressWarnings( "resource" )
  public static void main( String args[] )
  {
    new SendServerInformationsToClients().start();
    ServerSocket serverSocket = null;
    Socket socket = null;
    try
    {
      serverSocket = new ServerSocket( 3141 ); //Default is 3141 
      ServerLog.newLog( logType.INFO, "Starte MateServer auf Port: " + serverSocket.getLocalPort() );
    }
    catch ( IOException e )
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
    Database database = new Database( dataBasePath );
    while ( true )
    {
      try
      {
        socket = serverSocket.accept();
      }
      catch ( IOException e )
      {
        ServerLog.newLog( logType.ERROR, "Es konnte keine Verbindung zwischen Client und Server aufgebaut werden" );
        e.printStackTrace();
      }
      new SocketThread( socket, database ).start();
    }
  }
}