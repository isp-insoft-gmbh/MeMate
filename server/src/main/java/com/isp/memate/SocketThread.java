/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
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
  private int     lastDrinkID;
  private long    lastDate;
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
    ServerLog.newLog( logType.INFO, "Version des MeMateServers: " + version );
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
      sendVersion();

      while ( true )
      {
        try
        {
          final Shared shared = (Shared) objectInputStream.readObject();
          final Operation operation = shared.getOperation();
          ServerLog.newLog( logType.COMMAND, operation.toString() );
          switch ( operation )
          {
            case REGISTER_USER:
              registerUser( (User) shared.getValue() );
              break;

            case CHECK_LOGIN:
              checkLogin( (LoginInformation) shared.getValue() );
              break;

            case CHECK_LOGIN_WITH_SESSION_ID:
              checkLoginWithSessionID( (String) shared.getValue() );
              break;

            case HISTORY_DATA:
              sendHistoryData();
              break;

            case GET_USERS:
              sendUsers();
              break;

            case REGISTER_DRINK:
              registerDrink( (Drink) shared.getValue() );
              SocketPool.notifyAllSocketsToSendDrinks();
              break;

            case REGISTER_INGREDIENTS:
              registerIngredients( (DrinkIngredients) shared.getValue() );
              break;

            case REMOVE_DRINK:
              removeDrink( (int) shared.getValue() );
              SocketPool.notifyAllSocketsToSendDrinks();
              break;

            case UPDATE_DRINKNAME:
              updateDrinkName( (DrinkChangeObject) shared.getValue() );
              SocketPool.notifyAllSocketsToSendDrinks();
              break;

            case UPDATE_DRINKPRICE:
              updateDrinkPrice( (DrinkChangeObject) shared.getValue() );
              SocketPool.notifyAllSocketsToSendDrinks();
              break;

            case UPDATE_DRINKPICTURE:
              updateDrinkPicture( (DrinkChangeObject) shared.getValue() );
              SocketPool.notifyAllSocketsToSendDrinks();
              break;

            case UPDATE_DRINKAMOUNT:
              updateDrinkAmount( (DrinkChangeObject) shared.getValue() );
              SocketPool.notifyAllSocketsToSendDrinks();
              break;

            case UPDATE_BARCODE:
              updateDrinkBarcode( (DrinkChangeObject) shared.getValue() );
              SocketPool.notifyAllSocketsToSendDrinks();
              break;

            case CONNECT_SESSION_ID:
              connectSessionID( (String) shared.getValue() );
              break;

            case ADD_BALANCE:
              addBalance( (int) shared.getValue() );
              sendPiggybankBalance();
              break;

            case CONSUM_DRINK:
              consumeDrink( (Drink) shared.getValue() );
              SocketPool.notifyAllSocketsToSendDrinks();
              break;

            case SET_PIGGYBANK_BALANCE:
              database.setPiggyBankBalance( (Float) shared.getValue() );
              sendPiggybankBalance();
              break;

            case PIGGYBANK_BALANCE:
              sendPiggybankBalance();
              break;

            case UNDO:
              undoLastAction();
              break;

            case LOGOUT:
              resetUser();
              break;

            case CHANGE_PASSWORD:
              final User user = (User) shared.getValue();
              database.changePassword( user.name, user.password, true );
              break;

            case CHANGE_DISPLAYNAME:
              database.changeDisplayName( userIDMap.get( currentUser ), (String) shared.getValue() );
              objectOutputStream.writeObject( new Shared( Operation.USER_DISPLAYNAME, database.getDisplayName( currentUser ) ) );
              sendHistoryData();
              break;

            case CHANGE_PASSWORD_USER:
              database.changePassword( currentUser, (String) shared.getValue(), false );
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
      SocketPool.removeSocket( this );
    }
  }

  private void registerIngredients( final DrinkIngredients drinkIngredients )
  {
    database.addIngredients( drinkIngredients );
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
      database.addLog( HistoryEvents.UNDO, currentUser, Float.valueOf( lastTransaction ) * -1f, newBalance,
          new Date().getTime(), null );
      database.disableLog( lastDate );
      ServerLog.newLog( logType.INFO, "Der Kontostand von " + currentUser + " wurde auf " + newBalance + "€ aktualisiert." );
      sendHistoryData();
      sendBalance();

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
      database.addLog( HistoryEvents.UNDO, currentUser, lastTransaction, newBalance,
          new Date().getTime(), null );
      database.disableLog( lastDate );
      database.increaseAmountOfDrinks( lastDrinkID );
      sendHistoryData();
      sendBalance();
      SocketPool.notifyAllSocketsToSendDrinks();

      //For Undo
      lastActionDeposit = false;
      lastTransaction = 0;
      lastDate = -1;
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
      objectOutputStream.writeObject( new Shared( Operation.HISTORY_DATA, database.getHistory( currentUser, isUserAdmin() ) ) );
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
      objectOutputStream.writeObject( new Shared( Operation.SCOREBOARD, database.getScoreboard( false ) ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Das Scoreboard konnte nicht geladen werden. " + exception );
    }
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.WEEKLY_SCOREBOARD, database.getScoreboard( true ) ) );
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Das WeeklyScoreboard konnte nicht geladen werden. " + exception );
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
   * Checks if the expectedPrice equals the actual price of the drink and if the drink is still available.
   * If so the amount of the drink will lower by 1 and the balance will be removed.
   *
   * @param consumedDrink the consumed drink
   */
  private void consumeDrink( final Drink consumedDrink )
  {
    final Float actualPrice = database.getDrinkPrice( consumedDrink.getId() );
    final Float expectedPrice = consumedDrink.getPrice();
    if ( actualPrice == null )
    {
      return;
    }
    if ( database.getDrinkAmount( consumedDrink.getId() ) < 1 )
    {
      sendDrinks();
      try
      {
        objectOutputStream.writeObject( new Shared( Operation.NO_MORE_DRINKS_AVAIBLE, consumedDrink.getName() ) );
      }
      catch ( final IOException exception )
      {
        ServerLog.newLog( logType.ERROR, exception.getMessage() );
      }
      return;
    }
    ServerLog.newLog( logType.INFO, "EXPECTED: " + expectedPrice );
    ServerLog.newLog( logType.INFO, "REAL: " + actualPrice );
    if ( !expectedPrice.equals( actualPrice ) )
    {
      sendDrinks();
      try
      {
        objectOutputStream
            .writeObject( new Shared( Operation.PRICE_CHANGED, new DrinkChangeObject( consumedDrink.getId(), actualPrice ) ) );
      }
      catch ( final IOException exception )
      {
        ServerLog.newLog( logType.ERROR, exception.getMessage() );
      }
      return;
    }
    final float newBalance = database.getBalance( userIDMap.get( currentUser ) ) - actualPrice;
    database.updateBalance( currentSessionID, newBalance );
    final long date = new Date().getTime();
    database.addLog( HistoryEvents.CONSUMED_DRINK, currentUser, actualPrice * -1, newBalance, date, consumedDrink.getId() );
    database.decreaseAmountOfDrinks( consumedDrink.getId() );
    sendHistoryData();
    sendBalance();
    //For Undo
    lastActionDeposit = false;
    lastTransaction = actualPrice;
    lastDrinkID = consumedDrink.getId();
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
    final long date = new Date().getTime();
    database.addLog( balanceToAdd < 0 ? HistoryEvents.BALANCE_REMOVED : HistoryEvents.BALANCE_ADDED, currentUser,
        Float.valueOf( balanceToAdd ), newBalance, date, null );
    ServerLog.newLog( logType.INFO, "Der Kontostand von " + currentUser + " wurde auf " + newBalance + "€ aktualisiert." );
    sendHistoryData();
    sendBalance();

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
  private void checkLoginWithSessionID( final String sessionID )
  {
    final String username = database.getUsernameForSessionID( sessionID );
    try
    {
      if ( username != null )
      {
        ServerLog.newLog( logType.INFO, "Der Nutzer " + username + " gehört zu der SessionID " + sessionID );
        currentSessionID = sessionID;
        currentUser = username;
        objectOutputStream.writeObject( new Shared( Operation.LOGIN_WITH_SESSION_ID_RESULT, true ) );
        sendNecessaryInformations();
      }
      else
      {
        ServerLog.newLog( logType.ERROR, "Es konnte kein Nutzer für die gegebene Session gefunden werden (" + sessionID + ")" );
        objectOutputStream.writeObject( new Shared( Operation.LOGIN_WITH_SESSION_ID_RESULT, false ) );
      }
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
  private void updateDrinkPicture( final DrinkChangeObject drinkPicture )
  {
    database.updateDrinkInformation( drinkPicture.drinkID, Operation.UPDATE_DRINKPICTURE, drinkPicture.change );
    ServerLog.newLog( logType.INFO, "Das Bild des Getränk mit der ID " + drinkPicture.drinkID + " wurde geändert." );
  }

  /**
   * Updated den Preis des Getränks.
   *
   * @param drinkPrice DrinkPrice-Objekt, welches die ID und den neuen Preis enthält.
   */
  private void updateDrinkPrice( final DrinkChangeObject drinkPrice )
  {
    database.updateDrinkInformation( drinkPrice.drinkID, Operation.UPDATE_DRINKPRICE, drinkPrice.change );
    ServerLog.newLog( logType.INFO,
        "Der Preis des Getränk mit der ID " + drinkPrice.drinkID + " wurde auf " + drinkPrice.change + "€ geändert." );
  }


  /**
   * Updated den Namen des Getränks.
   *
   * @param drinkName DrinkName-Objekt, welches die ID und den neuen Namen enthält.
   */
  private void updateDrinkName( final DrinkChangeObject drinkName )
  {
    database.updateDrinkInformation( drinkName.drinkID, Operation.UPDATE_DRINKNAME, drinkName.change );
    ServerLog.newLog( logType.INFO, "Das Getränk mit der ID " + drinkName.drinkID + " wurde zu " + drinkName.change + " umbenannt." );
  }

  /**
   * Updates the amount of the drink
   *
   * @param amount the amount to set
   */
  private void updateDrinkAmount( final DrinkChangeObject amount )
  {
    database.updateDrinkInformation( amount.drinkID, Operation.UPDATE_DRINKAMOUNT, amount.change );
    ServerLog.newLog( logType.INFO, "Das Anzahl des Getränks mit der ID " + amount.drinkID + " wurde auf " + amount.change + " gesetzt." );
  }

  /**
   * Updates the barcode of the drink
   *
   * @param barcode the barcode to set
   */
  private void updateDrinkBarcode( final DrinkChangeObject barcode )
  {
    database.updateDrinkInformation( barcode.drinkID, Operation.UPDATE_BARCODE, barcode.change );
    ServerLog.newLog( logType.INFO,
        "Der Barcode des Getränks mit der ID " + barcode.drinkID + " wurde auf " + barcode.change + " gesetzt." );
  }


  /**
   * Zuerst wird die zugehörige ID für das Getränk
   * geholt und anschließend der Datensatz für
   * die ID gelöscht.
   *
   * @param drinkID Id of the drink
   */
  private void removeDrink( final int drinkID )
  {
    database.removeDrink( drinkID );
    ServerLog.newLog( logType.INFO, "Das Getränk mit der ID " + drinkID + " wurde entfernt." );
  }

  /**
   * Sendet ein Objekt, welches ein Array aus {@linkplain Drink}-Objekten enthält.
   */
  public void sendDrinks()
  {
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.GET_DRINKS, database.getDrinks() ) );
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
    final int drinkID = database.registerNewDrink( drink );
    if ( drink.getDrinkIngredients() != null )
    {
      database.addIngredients( new DrinkIngredients( drinkID, drink.getDrinkIngredients() ) );
    }
    ServerLog.newLog( logType.INFO, "Ein neues Getränk wurde registriert." );
    ServerLog.newLog( logType.INFO, "Name: " + drink.getName() );
    ServerLog.newLog( logType.INFO, "Preis: " + drink.getPrice() + "€" );
  }

  /**
   * Die UserID wird anhand des Namens beschafft und
   * mit dieser ID wird der Kontostand von der Datenbank
   * erfragt und zurück gegben.
   *
   */
  private void sendBalance()
  {
    //TODO(nwe | 08.04.2021): CHANGE do not save currentUser as String, just save the id!
    final Integer userID = userIDMap.get( currentUser );
    final float balance = database.getBalance( userID );
    ServerLog.newLog( logType.INFO, "Der Kontostand von " + currentUser + " beträgt " + balance + "€" );
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.USER_BALANCE, balance ) );
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
        sendNecessaryInformations();
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
        sendNecessaryInformations();
        break;
    }
  }

  private void sendNecessaryInformations()
  {
    try
    {
      objectOutputStream.writeObject( new Shared( Operation.USER_DISPLAYNAME, database.getDisplayName( currentUser ) ) );
      objectOutputStream.writeObject( new Shared( Operation.IS_ADMIN_USER, isUserAdmin() ) );

      // Is needed because otherwise the client hangs in an infinite loop when there is no data
      Thread.sleep( 100 );
    }
    catch ( final IOException | InterruptedException e )
    {
      // TODO(nwe|04.10.2021): Fehlerbehandlung muss noch implementiert werden!
    }
    sendDrinks();
    sendBalance();
    sendHistoryData();
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
    final String result = database.registerNewUser( username, password, false );
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

  public boolean isUserAdmin()
  {
    return database.isAdmin( currentUser );
  }
}