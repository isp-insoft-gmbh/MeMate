/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @author nwe
 * @since 23.10.2019
 * 
 */
public class Main
{
  /**
   * @param args unused
   */
  public static void main( String args[] )
  {
    ServerSocket serverSocket = null;
    Socket socket = null;

    try
    {
      serverSocket = new ServerSocket( 3141 );
    }
    catch ( IOException e )
    {
      e.printStackTrace();

    }
    while ( true )
    {
      try
      {
        socket = serverSocket.accept();
      }
      catch ( IOException e )
      {
        System.out.println( "I/O error: " + e );
      }
      // neuer Thread für jeden Client
      new SocketThread( socket ).start();
    }
  }
}