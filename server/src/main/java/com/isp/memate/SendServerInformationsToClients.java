/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.isp.memate.ServerLog.logType;

/**
 * Die Klasse sorgt dafür, dass wenn ein Client in dem Netzwerk nach einem Server sucht, das Clientpacket
 * empfangen wird und neues Packet an den Client zurück gesendet wird, damit der Client Serveradresse und
 * Sererport weiß um sich zu verbinden.
 * 
 * @author nwe
 * @since 10.12.2019
 *
 */
public class SendServerInformationsToClients extends Thread
{
  private DatagramSocket socket;
  private byte[]         buf = new byte[256];

  /**
   * Erzeugt einen neuen Socket um die Packete zu empfangen.
   */
  public SendServerInformationsToClients()
  {
    try
    {
      socket = new DatagramSocket( 3141 );
    }
    catch ( SocketException exception )
    {
      ServerLog.newLog( logType.ERROR, "Es konnte kein neuer DatagramSocket erstellt werden." );
      exception.printStackTrace();
    }
  }

  public void run()
  {
    while ( true )
    {
      //Empfängt das Clientpaket und wertet es aus.
      DatagramPacket clientPacket = new DatagramPacket( buf, buf.length );
      try
      {
        socket.receive( clientPacket );
      }
      catch ( IOException exception )
      {
        ServerLog.newLog( logType.ERROR, "Das vom Client gesendete Packet konnte nicht richtig empfangen werden." );
        exception.printStackTrace();
      }
      InetAddress clientAddress = clientPacket.getAddress();
      int clientPort = clientPacket.getPort();
      String clientMessage = new String( clientPacket.getData(), 0, clientPacket.getLength() );

      System.out
          .println(
              "############\nReceived a packet from\nADDRESS: " + clientAddress + "\nPort: " + clientPort + "\nMessage: " + clientMessage
                  + "\nSending Packet back to Client.\n############" );


      //Sendet ein neues Paket an den Client zurück.
      String broadcastMessage = "MateServer";
      buf = broadcastMessage.getBytes();
      clientPacket = new DatagramPacket( buf, buf.length, clientAddress, clientPort );
      try
      {
        if ( clientMessage.equals( "MateClient" ) )
        {
          socket.send( clientPacket );
        }
      }
      catch ( IOException exception )
      {
        ServerLog.newLog( logType.ERROR, "Es konnte kein Paket den den Client zurück gesendet werden" );
        exception.printStackTrace();
      }
    }
  }
}
