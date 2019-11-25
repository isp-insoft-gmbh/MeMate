/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;

/**
 * Damit mehrere Clients sich zum Server verbinden können und der Server nicht abstürzt, wenn ein Client sich
 * beendet, wird Jeder clientSocket einen anderen Thread zugewiesen.
 * 
 * @author nwe
 * @since 24.10.2019
 *
 */
public class SocketThread extends Thread
{
  ConnectDatabase     connectDB = new ConnectDatabase();
  protected Socket    socket;
  Map<String, String> userMap   = connectDB.getUserMap();

  /**
   * @param clientSocket userSocket
   */
  public SocketThread( Socket clientSocket )
  {
    this.socket = clientSocket;
  }


  public void run()
  {
    try
    {
      InputStreamReader inputStreamReader = new InputStreamReader( socket.getInputStream() );
      BufferedReader inputPassword = new BufferedReader( inputStreamReader );
      PrintStream output = new PrintStream( socket.getOutputStream() );

      char[] buffer = new char[1024];
      int lastRead = -1;
      while ( (lastRead = inputPassword.read( buffer )) != -1 )
      {
        String inputString = new String( buffer, 0, lastRead );
        if ( inputString.equals( "GET_DRINK_INFORMATIONS" ) )
        {
          sendDrinkInformations( output );
        }
        else if(inputString.contains("REMOVE_DRINK:"))
        {
            String drinkname = inputString.replace("REMOVE_DRINK:", "")
                .replace("GET_DRINK_INFORMATIONS", ""); //TODO FIXME this should not even appear here
            connectDB.removeDrink(drinkname);
        }
        else if ( inputString.equals( "GET_HISTORY_DATA" ) )
        {
          sendHistoryInformations( output );
        }
        else if ( inputString.contains( "GET_BALANCE_FOR:" ) )
        {
          String username = inputString.replace( "GET_BALANCE_FOR:", "" );
          sendBalance( username, output );
        }
        else if ( inputString.contains( "REGISTER_NEW_USER:" ) )
        {
          String userData = inputString.replace( "REGISTER_NEW_USER:", "" );
          registerNewUser( userData );
        }
        else if ( inputString.contains( "UPDATE_BALANCE_FOR:" ) )
        {
          String input = inputString.replace( "UPDATE_BALANCE_FOR:", "" );
          String[] splitUserAndBalance = input.split( "NEW_BALANCE:" );
          updateBalance( splitUserAndBalance[ 0 ], splitUserAndBalance[ 1 ] );
        }
        else if ( inputString.contains( "REGISTER_NEW_DRINK:" ) )
        {
          String newDrinkData = inputString.replace( "REGISTER_NEW_DRINK:", "" );
          String[] splitedNewDrinkData = newDrinkData.split( "," );
          String name = splitedNewDrinkData[ 0 ];
          Float price = Float.valueOf( splitedNewDrinkData[ 1 ] );
          String picture = splitedNewDrinkData[ 2 ];
          registerNewDrink( name, price, picture );
        }
        else
        {


          String[] usernameAndPasswordSplitted = inputString.split( System.lineSeparator() );

          String username = usernameAndPasswordSplitted[ 0 ];
          String password = usernameAndPasswordSplitted[ 1 ];

          System.out.println( "Benutzername: " + username );
          System.out.println( "Passwort: " + password );

          if ( userMap.containsKey( username ) )
          {
            if ( password.equals( userMap.get( username ) ) )
            {
              output.println( "Login erfolgreich" );
              //            inputStreamReader.close();
              //            inputPassword.close();
              //            output.close();
              //            this.interrupt();
              //            this.socket.close();
            }
            else
            {
              output.println( "falsches Passwort" );
            }
          }
          else
          {
            output.println( "Benutzer konnte nicht gefunden werden" );
          }
        }
      }
    }


    catch ( IOException __ )
    {
      System.out.println( "Verbindung getrennt" );
    }

  }

  /**
   * @param name
   * @param price
   * @param picture
   */
  private void registerNewDrink( String name, Float price, String picture )
  {
    connectDB.registerNewDrink( name, price, picture );
  }


  /**
   */
  private void updateBalance( String username, String newBalance )
  {
    Float updatedBalance = Float.valueOf( newBalance );
    connectDB.updateBalance( username, updatedBalance );
  }


  /**
   * @param userData contains new Username and Password
   * 
   */
  private void registerNewUser( String userData )
  {
    String[] splitUsernameAndPassword = userData.split( System.lineSeparator() );
    String username = splitUsernameAndPassword[ 0 ];
    String password = splitUsernameAndPassword[ 1 ];
    connectDB.registerNewUser( username, password );
    userMap = connectDB.getUserMap();
  }


  /**
   * Sends the Data for creating the Dashboardbuttons
   * 
   * @param output Printstream to user
   */
  private void sendHistoryInformations( PrintStream output )
  {
    StringBuilder historyBuilder = new StringBuilder();
    historyBuilder
        .append( "{" )
        .append( "{Guthaben aufgeladen,Niklas,+1.00€,0.00€,18.10.2019}," )
        .append( "{MioMio Ginger konsumiert,Niklas,-0.60€,-1.00€,18.10.2019}," )
        .append( "{MioMio Pomegranate konsumiert,Niklas,-0.60€,-0.40€,17.10.2019}," )
        .append( "{MioMio Cola konsumiert,Niklas,-0.60€,0.20€,16.10.2019}," )
        .append( "{MioMio Mate konsumiert,Niklas,-0.60€,0.80€,15.10.2019}," )
        .append( "{MioMio Cola konsumiert,Marcel,-0.60€,1.40€,15.10.2019}," )
        .append( "{MioMio Cola konsumiert,Niklas,-0.60€,1.40€,15.10.2019}," )
        .append( "{Guthaben aufgeladen,Niklas,+2.00€,2.00€,15.10.2019}" )
        .append( "}" );
    output.println( historyBuilder.toString() );
  }

  private void sendBalance( String username, PrintStream output )
  {
    output.println( connectDB.getBalance( username ) );
  }

  /**
   * Sendet die Daten der Getränke, wird z.B. verwendet um die Buttons zu erzeugen
   * 
   * @param output Printstream to user
   */
  private void sendDrinkInformations( PrintStream output )
  {
    output.println( connectDB.getDrinkInformations() );
  }
}