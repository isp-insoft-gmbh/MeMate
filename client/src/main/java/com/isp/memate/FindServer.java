/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import com.isp.memate.util.ClientLog;

/**
 * @author nwe
 * @since 10.12.2019
 *
 */
class FindServer
{
  private static DatagramSocket   socket        = null;
  private static InetAddress      serverAddress = null;
  private static int              serverPort    = -1;
  private static final FindServer instance      = new FindServer();

  /**
   * @return Serveradresse
   */
  static InetAddress getServerAddress()
  {
    return serverAddress;
  }

  /**
   * @return Serverport
   */
  static int getServerPort()
  {
    return serverPort;
  }

  /**
   * Ruft erst listAllBroadcastAddresses auf und startet dann für alle Einträge einen Broadcast.
   * 
   */
  public FindServer()
  {
    List<InetAddress> broadcastList;
    try
    {
      broadcastList = listAllBroadcastAddresses();
      System.out.println( "Es wurden " + broadcastList.size() + " mögliche Netzwerkschnittstellen gefunden." );
      for ( InetAddress inetAddress : broadcastList )
      {
        if ( serverAddress == null )
        {
          System.out.println( "Starte Broadcast für " + inetAddress );
          broadcast( "MateClient", inetAddress );
        }
      }
    }
    catch ( IOException exception )
    {
      ClientLog.newLog( "Es konnte kein Server gefunden werden." );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  /**
   * Startet einen Broadcast mit der gegebene Nachricht an die gegebene Adresse.
   * Der Client sendet ein Packet an den Server und wenn dieser antwortet,
   * kann von dem Serverpacket, die Adresse und der Port weitergegeben werden.
   * 
   * @param broadcastMessage Die Nachricht, welche gebroadcasted werden soll.
   * @param address Adresse an welche der Broadcast gesendet werden soll.
   * @throws IOException wenn etwas mit dem Socket fehlschlägt.
   */
  private static void broadcast( String broadcastMessage, InetAddress address ) throws IOException
  {
    //Sendet das Packet an den Server
    socket = new DatagramSocket();
    socket.setBroadcast( true );
    byte[] buffer = broadcastMessage.getBytes();
    DatagramPacket clientPacket = new DatagramPacket( buffer, buffer.length, address, 3141 );
    socket.send( clientPacket );
    socket.setSoTimeout( 1500 );


    //Empfangen des Serverpackets
    byte[] buf = new byte[256];
    DatagramPacket serverPacket = new DatagramPacket( buf, buf.length );
    try
    {
      socket.receive( serverPacket );
    }
    catch ( IOException exception )
    {
      ClientLog.newLog( "Das Serverpacket konnte nicht richtig empfangen werden." );
      ClientLog.newLog( exception.getMessage() );
    }
    serverAddress = serverPacket.getAddress();
    serverPort = serverPacket.getPort();
    String serverMessage = new String( serverPacket.getData(), 0, serverPacket.getLength() );

    System.out.println( "Es wurde ein Server gefunden." );
    System.out.println( "Adresse: " + serverAddress + "\nPort: " + serverPort + "\nNachricht: " + serverMessage );

    socket.close();
  }

  /**
   * Erstellt eine Liste aller verfügbaren Networkinterfaces.
   * Die Interfaces werden gefiltern, ob diese unter anderem in Verwendung
   * sind und ob sie Loopback sind.
   * 
   * @return Liste der möglichen Adressen der Networkinterfaces.
   * @throws SocketException
   */
  private static List<InetAddress> listAllBroadcastAddresses() throws SocketException
  {
    List<InetAddress> broadcastList = new ArrayList<>();
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    while ( interfaces.hasMoreElements() )
    {
      NetworkInterface networkInterface = interfaces.nextElement();

      if ( networkInterface.isLoopback() || !networkInterface.isUp() )
      {
        continue;
      }

      networkInterface.getInterfaceAddresses().stream()
          .map( a -> a.getBroadcast() )
          .filter( Objects::nonNull )
          .forEach( broadcastList::add );
    }
    return broadcastList;
  }
}
