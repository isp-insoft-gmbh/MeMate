/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Die Mainklasse startet den Server und verbindet
 * jeden User mit einem eigenen Socket.
 * 
 * @author nwe
 * @since 23.10.2019
 * 
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
    }
    catch ( IOException e )
    {
      System.out.println( "Der Server konnte nicht gestartet werden" );
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
    Database database = new Database( dataBasePath );
    while ( true )
    {
      try
      {
        socket = serverSocket.accept();
      }
      catch ( IOException e )
      {
        System.out.println( "Es konnte keine Verbindung zwischen Client und Server aufgebaut werden\n" + "I/O error: " + e );
      }

      new SocketThread( socket, database ).start();
    }
  }
}