/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Properties;

import com.isp.memate.ServerLog.logType;
import com.isp.memate.Shared.LoginResult;
import com.isp.memate.Shared.Operation;

/**
 * Damit mehrere Clients sich zum Server verbinden können und
 * der Server nicht abstürzt, wenn ein Client sich
 * beendet, wird jeder clientSocket einen anderen Thread zugewiesen.
 *
 * @author nwe
 * @since 24.10.2019
 *
 */
class SocketThread extends Thread
{
  private String               version = "unkown";
  private ObjectOutputStream   objectOutputStream;
  private ObjectInputStream    objectInputStream;
  private String               currentSessionID;
  private String               currentUser;
  private Map<String, Integer> userIDMap;
  private final Database       database;
  private final Socket         socket;
  //UNDO
  private boolean lastActionDeposit = false;
  private String  lastDrinkName;
  private String  lastDate;
  private float   lastTransaction;

  /**
   * @param clientSocket userSocket
   * @param dataBase Database
   */
  SocketThread( final Socket clientSocket, final Database dataBase )
  {
    setVersion();
    this.socket = clientSocket;
    this.database = dataBase;
    userIDMap = database.getUserIDMap();
  }

  private void setVersion()
  {
    try ( InputStream input = SocketThread.class.getClassLoader().getResourceAsStream( "version.properties" ) )
    {
      final Properties versionProperties = new Properties();
      versionProperties.load( input );
      version = versionProperties.getProperty( "build_version" );
    }
    catch ( final Exception exception )
    {
      ServerLog.newLog( logType.ERROR, "Die version.properties konnten nicht geladen werden" );
      ServerLog.newLog( logType.ERROR, exception.getMessage() );
    }
    System.out.println( version );
  }

  /**
   * Überprüft ständig, ob es einen neuen Befehl gibt,
   * trifft dies zu, dann wird dieser zugeordnet und
   * die zutreffenden Aktionen ausgeführt.
   */
  @Override
  public void run()
  {
    try
    {
      objectInputStream = new ObjectInputStream( socket.getInputStream() );
      objectOutputStream = new ObjectOutputStream( socket.getOutputStream() );

      while ( true )
      {
        try
        {
          final Shared shared = (Shared) objectInputStream.readObject();
          final Operation operation = shared.operation;
          if ( operation != Operation.GET_DRINKINFO && operation != Operation.PIGGYBANK_BALANCE && operation != Operation.GET_HISTORY )
          {
            ServerLog.newLog( logType.COMMAND, operation.toString() );
          }
          switch ( operation )
          {
            case REGISTER_USER:
              registerUser( shared.user );
              break;

            case CHECK_LOGIN:
              checkLogin( shared.loginInformation );
              break;

            case GET_BALANCE:
              getBalance();
              break;

            case GET_DRINKINFO:
              getDrinkInfo();
              break;

            case GET_HISTORY:
              sendHistoryData();
              break;

            case GET_USERS:
              sendUsers();
              break;

            case GET_VERSION:
              sendVersion();
              break;

            case REGISTER_DRINK:
              registerDrink( shared.drink );
              break;

            case REGISTER_INGREDIENTS:
              registerIngredients( shared.drinkIngredients );
              break;

            case REMOVE_DRINK:
              removeDrink( shared.drink );
              break;

            case UPDATE_DRINKNAME:
              updateDrinkName( shared.drinkName );
              break;

            case UPDATE_DRINKPRICE:
              updateDrinkPrice( shared.drinkPrice );
              break;

            case UPDATE_DRINKPICTURE:
              updateDrinkPicture( shared.drinkPicture );
              break;

            case CONNECT_SESSION_ID:
              connectSessionID( shared.sessionID );
              break;

            case GET_USERNAME_FOR_SESSION_ID:
              sendUsernameForSessionID( shared.userSessionID );
              break;

            case ADD_BALANCE:
              addBalance( shared.balanceToAdd );
              sendPiggybankBalance();
              break;

            case CONSUM_DRINK:
              removeBalance( shared.drinkPrice );
              break;

            case SET_PIGGYBANK_BALANCE:
              database.setPiggyBankBalance( shared.userBalance );
              sendPiggybankBalance();
              break;

            case PIGGYBANK_BALANCE:
              sendPiggybankBalance();
              break;

            case SET_DRINK_AMOUNT:
              database.setAmountOfDrinks( shared.drinkAmount.name, shared.drinkAmount.amount );
              break;

            case UNDO:
              undoLastAction();
              break;

            case LOGOUT:
              resetUser();
              break;

            case CHANGE_PASSWORD:
              database.changePassword( shared.user.name, shared.user.password, true );
              break;

            case CHANGE_DISPLAYNAME:
              database.changeDisplayName( userIDMap.get( currentUser ), shared.displayname );
              objectOutputStream.writeObject( new Shared( Operation.GET_DISPLAYNAME, database.getDisplayName( currentUser ) ) );
              sendHistoryData();
              break;

            case CHANGE_PASSWORD_USER:
              database.changePassword( currentUser, shared.pass, false );
              break;

            default :
              break;
          }
        }
        catch ( final ClassNotFoundException exception )
        {
          ServerLog.newLog( logType.ERROR, exception.getMessage() );
        }
      }
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.INFO, "Die Verbindung zu " + currentUser + " wurde getrennt." );
    }
  }

  private void registerIngredients( final DrinkIngredients drinkIngredients )
  {
    database.addIngredients( drinkIngredients.drinkID, drinkIngredients.ingredients, drinkIngredients.energy_kJ,
        drinkIngredients.energy_kcal, drinkIngredients.fat, drinkIngredients.fatty_acids, drinkIngredients.carbs, drinkIngredients.sugar,
        drinkIngredients.protein, drinkIngredients.salt, drinkIngredients.amount );
  }

  /**
   * Alle Nutzerdaten werden resetet nach dem Logout.
   */
  private void resetUser()
  {
    ServerLog.newLog( logType.INFO, currentUser + " hat sich ausgeloggt." );
    lastActionDeposit = false;
    currentUser = null;
    currentSessionID = null;
  }

  /**
   * Die letzte AKtion wird rückgängig gemacht.
   */
  private void undoLastAction()
  {
    if ( lastActionDeposit )
    {
      final Float newBalance = database.getBalance( userIDMap.get( currentUser ) ) - lastTransaction;
      database.updateBalance( currentSessionID, newBalance );
      database.addLog( "Letzte Aktion rückgängig", currentUser, Float.valueOf( lastTransaction ) * -1f, newBalance,
          LocalDateTime.now().toString() );
      database.disableLog( lastDate );
      ServerLog.newLog( logType.INFO, "Der Kontostand von " + currentUser + " wurde auf " + newBalance + "€ aktualisiert." );
      sendHistoryData();
      getBalance();

      //For Admin-Balance
      final Float adminBalance = database.getPiggyBankBalance() - lastTransaction;
      database.setPiggyBankBalance( adminBalance );

      //For Undo
      lastActionDeposit = false;
      lastTransaction = 0;
    }
    else
    {
      final Float newBalance = database.getBalance( userIDMap.get( currentUser ) ) + lastTransaction;
      database.updateBalance( currentSessionID, newBalance );
      database.addLog( "Letzte Aktion rückgängig", currentUser, lastTransaction, newBalance,
          LocalDateTime.now().toString() );
      database.disableLog( lastDate );
      database.increaseAmountOfDrinks( lastDrinkName );
      sendHistoryData();
      getBalance();

      //For Undo
      lastActionDeposit = false;
      lastTransaction = 0;
      lastDate = "null";
    }
  }

  /**
   * Sendet das Guthaben des Spaarschweins.
   */
  private void sendPiggybankBalance()
  {
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.PIGGYBANK_BALANCE, database.getPiggyBankBalance() ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Das Guthaben des Spaarschweins konnte nicht geladen werden. " + exception );
    }
  }

  /**
   * Sendet die aktuelle Versionsnummer an den Client
   */
  private void sendVersion()
  {
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_VERSION, version ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Die Versionsnummer konnte nicht geladen werden. " + exception );
    }
  }

  /**
   * Sendet die History Daten an den Client.
   */
  private void sendHistoryData()
  {
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_USERS_DISPLAYNAMES, database.getDisplayNames() ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Die User konnten nicht geladen werden. " + exception );
    }
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_HISTORY, database.getHistory( currentUser ) ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Die Historie konnte nicht geladen werden. " + exception );
    }
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_HISTORY_LAST_5, database.getLast5HistoryEntries() ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Die Historie konnte nicht geladen werden. " + exception );
    }
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_SCOREBOARD, database.getScoreboard() ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Das Scoreboard konnte nicht geladen werden. " + exception );
    }
  }

  /**
   * Sendet ein Array mit allen Nutzern an den Client
   */
  private void sendUsers()
  {
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_USERS_RESULT, database.getUser() ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Die User konnten nicht geladen werden. " + exception );
    }
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_USERS_DISPLAYNAMES, database.getDisplayNames() ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Die User konnten nicht geladen werden. " + exception );
    }
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_FULLUSERS_RESULT, database.getFullUser() ) );
    }
    catch ( final Exception exception )
    {
      ServerLog.newLog( logType.ERROR, "Die User konnten nicht geladen werden. " + exception );
    }
  }

  /**
   * Enfernt das angegebene Getränk.
   *
   * @param consumedDrink
   */
  private void removeBalance( final DrinkPrice consumedDrink )
  {
    final Float drinkPrice = database.getDrinkPrice( consumedDrink.name );
    final Float expectedPrice = consumedDrink.price;
    if ( drinkPrice == null )
    {
      return;
    }
    if ( database.getDrinkAmount( consumedDrink.name ) < 1 )
    {
      getDrinkInfo();
      try
      {
        objectOutputStream.writeObject( new Shared( Operation.NO_MORE_DRINKS_AVAIBLE, consumedDrink.name ) );
      }
      catch ( final IOException exception )
      {
        ServerLog.newLog( logType.ERROR, exception.getMessage() );
      }
      return;
    }
    ServerLog.newLog( logType.INFO, "EXPECTED: " + expectedPrice );
    ServerLog.newLog( logType.INFO, "REAL: " + drinkPrice );
    if ( !expectedPrice.equals( drinkPrice ) )
    {
      getDrinkInfo();
      try
      {
        objectOutputStream.writeObject( new Shared( Operation.PRICE_CHANGED, new DrinkPrice( drinkPrice, -1, consumedDrink.name ) ) );
      }
      catch ( final IOException exception )
      {
        ServerLog.newLog( logType.ERROR, exception.getMessage() );
      }
      return;
    }
    final Float newBalance = database.getBalance( userIDMap.get( currentUser ) ) - drinkPrice;
    database.updateBalance( currentSessionID, newBalance );
    final String date = LocalDateTime.now().toString();
    database.addLog( consumedDrink.name + " getrunken", currentUser, drinkPrice * -1, newBalance, date );
    database.decreaseAmountOfDrinks( consumedDrink.name );
    sendHistoryData();

    //For Undo
    lastActionDeposit = false;
    lastTransaction = drinkPrice;
    lastDrinkName = consumedDrink.name;
    lastDate = date;
  }

  /**
   * Fügt dem Guthaben Geld hinzu.
   *
   * @param balanceToAdd
   */
  private void addBalance( final int balanceToAdd )
  {
    final Float newBalance = database.getBalance( userIDMap.get( currentUser ) ) + balanceToAdd;
    database.updateBalance( currentSessionID, newBalance );
    final String date = LocalDateTime.now().toString();
    database.addLog( "Guthaben aufgeladen", currentUser, Float.valueOf( balanceToAdd ), newBalance, date );
    ServerLog.newLog( logType.INFO, "Der Kontostand von " + currentUser + " wurde auf " + newBalance + "€ aktualisiert." );
    sendHistoryData();

    //For Admin-Balance
    final Float adminBalance = database.getPiggyBankBalance() + balanceToAdd;
    database.setPiggyBankBalance( adminBalance );

    //For Undo
    lastActionDeposit = true;
    lastTransaction = balanceToAdd;
    lastDate = date;
  }

  /**
   * sendet den passenden Nutzernamen der SessionID an den Client zurück.
   *
   * @param sessionID
   */
  private void sendUsernameForSessionID( final String sessionID )
  {
    final String username = database.getUsernameForSessionID( sessionID );
    currentSessionID = sessionID;
    currentUser = username;
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_USERNAME_FOR_SESSION_ID_RESULT, username ) );
      objectOutputStream.writeObject( new Shared( Operation.GET_DISPLAYNAME, database.getDisplayName( username ) ) );
      ServerLog.newLog( logType.INFO, "Der Nutzer " + username + " gehört zu der SessionID " + sessionID );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Es konnte kein Nutzer für die gegebene Session gefunden werden\n" + exception );
    }
  }

  /**
   * Verbindet die SessionID mit dem Nutzer
   *
   * @param sessionID
   */
  private void connectSessionID( final String sessionID )
  {
    database.addSessionIDToUser( sessionID, userIDMap.get( currentUser ) );
    currentSessionID = sessionID;
    ServerLog.newLog( logType.INFO, "Die SessionID " + sessionID + " wurde mit " + currentUser + " verbunden." );
  }

  /**
   * Updated das Bild des Getränks.
   *
   * @param drinkPicture DrinkPicture-Objekt, welches die ID und das Bild in Bytes beinhaltet.
   */
  private void updateDrinkPicture( final DrinkPicture drinkPicture )
  {
    database.updateDrinkInformation( drinkPicture.id, Operation.UPDATE_DRINKPICTURE, drinkPicture.pictureAsBytes );
    ServerLog.newLog( logType.INFO, "Das Bild des Getränk mit der ID " + drinkPicture.id + " wurde geändert." );
  }

  /**
   * Updated den Preis des Getränks.
   *
   * @param drinkPrice DrinkPrice-Objekt, welches die ID und den neuen Preis enthält.
   */
  private void updateDrinkPrice( final DrinkPrice drinkPrice )
  {
    database.updateDrinkInformation( drinkPrice.id, Operation.UPDATE_DRINKPRICE, drinkPrice.price );
    ServerLog.newLog( logType.INFO,
        "Der Preis des Getränk mit der ID " + drinkPrice.id + " wurde auf " + drinkPrice.price + "€ geändert." );
  }


  /**
   * Updated den Namen des Getränks.
   *
   * @param drinkName DrinkName-Objekt, welches die ID und den neuen Namen enthält.
   */
  private void updateDrinkName( final DrinkName drinkName )
  {
    database.updateDrinkInformation( drinkName.id, Operation.UPDATE_DRINKNAME, drinkName.name );
    ServerLog.newLog( logType.INFO, "Das Getränk mit der ID " + drinkName.id + " wurde zu " + drinkName.name + " umbenannt." );
  }


  /**
   * Zuerst wird die zugehörige ID für das Getränk
   * geholt und anschließend der Datensatz für
   * die ID gelöscht.
   *
   * @param drink Drink-Objekt, welches den Namen des Getränks enthält.
   */
  private void removeDrink( final Drink drink )
  {
    database.removeDrink( drink.id );
    ServerLog.newLog( logType.INFO, drink.name + " wurde entfernt." );
  }

  /**
   * Sendet ein Objekt, welches ein Array aus {@linkplain Drink}-Objekten enthält.
   */
  private void getDrinkInfo()
  {
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_DRINKINFO, database.getDrinkInformations() ) );
      objectOutputStream.reset();
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Die Getränkeinformationen konnten nicht geladen werden." + exception );
    }
  }

  /**
   * Schickt an die Datenbankverbindung die Informationen
   * (Name, Preis, Bildpfad) für ein neues Getränk.
   *
   * @param drink ein Drink-Objekt, welches Name, Preis und Bildpfad enthält.
   */
  private void registerDrink( final Drink drink )
  {
    final String name = drink.name;
    final Float price = drink.price;
    final byte[] picture = drink.pictureInBytes;
    database.registerNewDrink( name, price, picture );
    ServerLog.newLog( logType.INFO, "Ein neues Getränk wurde registriert." );
    ServerLog.newLog( logType.INFO, "Name: " + name );
    ServerLog.newLog( logType.INFO, "Preis: " + price + "€" );
  }

  /**
   * Die UserID wird anhand des Namens beschafft und
   * mit dieser ID wird der Kontostand von der Datenbank
   * erfragt und zurück gegben.
   *
   *
   */
  private void getBalance()
  {
    final Integer userID = userIDMap.get( currentUser );
    final Float balance = database.getBalance( userID );
    ServerLog.newLog( logType.INFO, "Der Kontostand von " + currentUser + " beträgt " + balance + "€" );
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_BALANCE_RESULT, balance ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Der Kontostand konnte nicht geladen werden." + exception );
    }
  }

  /**
   * Die gegebenen Informationen werden mit den Daten in der Datenbank verglichen
   * und das Ergebnis wird an den Client zurück gesendet.
   *
   * @param login ein Login-Objekt mit Benutzername und Passwort.
   */
  private void checkLogin( final LoginInformation login ) throws IOException
  {
    final String username = login.username;
    final String password = login.password;

    ServerLog.newLog( logType.INFO, "Loginversuch für " + username );

    final LoginResult result = database.checkLogin( username, password );

    switch ( result )
    {
      case LOGIN_SUCCESSFULL:
        currentUser = username;
        ServerLog.newLog( logType.INFO, username + " hat sich erfolgreich eingeloggt." );
        objectOutputStream.writeObject( new Shared( Operation.LOGIN_RESULT, result ) );
        objectOutputStream.writeObject( new Shared( Operation.GET_DISPLAYNAME, database.getDisplayName( username ) ) );
        break;

      case USER_NOT_FOUND:
        ServerLog.newLog( logType.ERROR, "Benutzer " + username + " konnte nicht gefunden werden." );
        objectOutputStream.writeObject( new Shared( Operation.LOGIN_RESULT, result ) );
        break;

      case WRONG_PASSWORD:
        ServerLog.newLog( logType.ERROR, "Falsches Passwort" );
        objectOutputStream.writeObject( new Shared( Operation.LOGIN_RESULT, result ) );
        break;

      case LOGIN_SUCCESSFULL_REQUEST_NEW_PASSWORD:
        currentUser = username;
        ServerLog.newLog( logType.INFO,
            username + " hat sich erfolgreich eingeloggt, wird aber aufgefordert ein neues Passwort zu erstellen." );
        objectOutputStream.writeObject( new Shared( Operation.LOGIN_RESULT, result ) );
        objectOutputStream.writeObject( new Shared( Operation.GET_DISPLAYNAME, database.getDisplayName( username ) ) );
        break;
    }
  }

  /**
   * Es wird ein neuer Datenbankeintrag für den Nutzer angelegt.
   *
   * @param user enthät alle Nutzerinformationen.
   */
  private void registerUser( final User user )
  {
    final String username = user.name;
    final String password = user.password;
    final String result = database.registerNewUser( username, password );
    ServerLog.newLog( logType.INFO, "Ein neuer Benutzer mit dem Namen " + username + " wurde registriert." );
    userIDMap.clear();
    userIDMap = database.getUserIDMap();
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.REGISTRATION_RESULT, result ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Der Benutzer konnte nicht angelegt werden." + exception );
    }
  }
}