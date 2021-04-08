package com.isp.memate;

import java.util.ArrayList;
import java.util.List;

public class SocketPool
{
  static List<SocketThread> sockets = new ArrayList<>();

  public static void addSocket( SocketThread socketThread )
  {
    sockets.add( socketThread );
  }

  public static void notifyAllSocketsToSendDrinks()
  {
    for ( SocketThread socketThread : sockets )
    {
      if ( !socketThread.isUserAdmin() )
      {
        socketThread.sendDrinks();
      }
    }
  }
}
