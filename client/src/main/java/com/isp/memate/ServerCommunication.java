/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.isp.memate.Shared.LoginResult;
import com.isp.memate.Shared.Operation;
import com.isp.memate.panels.Dashboard;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.PropertyHelper;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Die Klasse ServerCommunication kommuniziert mit dem Server und schickt
 * verschiedenen Shared-Objekte an der Server. Beispielsweise bei checkLogin
 * schickt die Klasse ein Objekt, welches den Befehl CHECK_LOGIN und ein
 * Userobjekt, welches Nutzername und gehashtes Passwort enthält an der Server.
 *
 * @author nwe
 * @since 24.10.2019
 */
public class ServerCommunication
{
  private static final ServerCommunication instance                  = new ServerCommunication();
  Cache                                    cache                     = Cache.getInstance();
  public final ReentrantLock               lock                      = new ReentrantLock( true );
  private final ArrayList<String>          alreadyShownNotifications = new ArrayList<>();
  private Socket                           socket;
  private ObjectInputStream                inStream;
  private ObjectOutputStream               outStream;
  private TrayIcon                         trayIcon                  = null;

  /**
   * @return the static instance of {@link ServerCommunication}
   */
  public static ServerCommunication getInstance()
  {
    return instance;
  }

  /**
   * Erzeugt eine Socketverbindung zum Server. Außerdem wird ein Output- und
   * Inputsteam definiert. Dannach werden zwei Tasks in bestimmten Abständen
   * ausgeführt, um die Serveranfragen zu bearbeiten und die Getränke auf dem
   * neusten Stand zu halten.
   */
  public ServerCommunication()
  {
    try
    {
      if ( !cache.isDebugMode() )
      {
        socket = new Socket( FindServer.getServerAddress(), FindServer.getServerPort() );
      }
      else
      {
        socket = new Socket( "192.168.168.82", 3142 );
      }
      outStream = new ObjectOutputStream( socket.getOutputStream() );
      inStream = new ObjectInputStream( socket.getInputStream() );
    }
    catch ( final Exception __ )
    {
      ClientLog.newLog( "Der Server konnte nicht gefunden werden " + __.getMessage() );
      final Alert alert = new Alert( AlertType.ERROR );
      alert.setTitle( "Server nicht gefunden" );
      alert.setHeaderText( null );
      alert.setContentText( "Es konnte kein Server gefunden werden. Bitte stelle sicher, dass der Server an ist" );
      alert.initOwner( GUIObjects.loginFrame );
      alert.showAndWait();
      System.exit( 1 );
    }
    initTrayIcon();

    startReceiverThread();
    startMeetingNotificationThread();
  }

  /**
   * The ReceiverThread constantly checks if the server has sent an object.
   * If so the object gets assigned and the corresponding task gets executed.
   */
  private void startReceiverThread()
  {
    final Thread thread = new Thread( () ->
    {
      while ( true )
      {
        try
        {
          final Shared shared = (Shared) inStream.readObject();
          final Operation operation = shared.getOperation();
          ClientLog.newLog( operation.toString() );
          switch ( operation )
          {
            case LOGIN_RESULT:
              Platform.runLater( () ->
              {
                GUIObjects.loginFrame.validateLoginResult( (LoginResult) shared.getValue() );
              } );
              break;
            case LOGIN_WITH_SESSION_ID_RESULT:
              cache.setSessionIDValid( (boolean) shared.getValue() );
              break;
            case GET_DRINKS:
              Platform.runLater( () ->
              {
                cache.setDrinks( (HashMap<Integer, Drink>) shared.getValue() );
              } );
              break;
            case USER_BALANCE:
              Platform.runLater( () ->
              {
                cache.setBalance( (float) shared.getValue() );
              } );
              break;
            case REGISTRATION_RESULT:
              GUIObjects.registrationFrame.validateRegistartionResult( (String) shared.getValue() );
              break;
            case HISTORY_DATA:
              cache.setHistory( (String[][]) shared.getValue() );
              break;
            case GET_HISTORY_LAST_5:
              cache.setShortHistory( (String[][]) shared.getValue() );
              checkForChanges();
              break;
            case SCOREBOARD:
              lock.lock();
              cache.setScoreboard( (Map<String, Integer>) shared.getValue() );
              lock.unlock();
              break;
            case WEEKLY_SCOREBOARD:
              lock.lock();
              cache.setWeeklyScoreboard( (Map<String, Integer>) shared.getValue() );
              lock.unlock();
              break;
            case IS_ADMIN_USER:
              cache.setAdminUser( (boolean) shared.getValue() );
              break;
            case PRICE_CHANGED:
              //Unsave cast
              final DrinkChangeObject change = (DrinkChangeObject) shared.getValue();
              ((Dashboard) GUIObjects.currentPanel)
                  .showPriceChangedDialog( cache.getDrinks().get( change.drinkID ) );
              break;
            case NO_MORE_DRINKS_AVAIBLE:
              //Unsave cast
              ((Dashboard) GUIObjects.currentPanel).showNoMoreDrinksDialog( (String) shared.getValue() );
              break;
            case PIGGYBANK_BALANCE:
              cache.setPiggyBankBalance( (Float) shared.getValue() );
              break;
            case GET_USERS_RESULT:
              cache.setUserArray( (String[]) shared.getValue() );
              break;
            case USER_DISPLAYNAME:
              Platform.runLater( () ->
              {
                cache.setDisplayname( (String) shared.getValue() );
              } );
              break;
            case GET_FULLUSERS_RESULT:
              cache.setFullUserArray( (User[]) shared.getValue() );
              break;
            case GET_VERSION:
              cache.setServerVersion( (String) shared.getValue() );
              break;
            default :
              break;
          }
        }
        catch ( ClassNotFoundException | IOException exception )
        {
          ClientLog.newLog( exception.getMessage() );
        }
      }
    } );
    thread.setName( "ReceiverThread" );
    thread.start();
  }

  //Checks once every minute if the time is equal to 12:19
  //If so and notification are on the app will show an traymessage
  private void startMeetingNotificationThread()
  {
    final DateTimeFormatter dtf = DateTimeFormatter.ofPattern( "HH:mm" );
    final Thread thread = new Thread( () ->
    {
      while ( true )
      {
        final LocalDateTime now = LocalDateTime.now();
        final String date = dtf.format( now );
        if ( date.equals( "12:19" ) )
        {
          if ( PropertyHelper.getBooleanProperty( "MeetingNotification" ) )
          {
            if ( SystemTray.isSupported() )
            {
              trayIcon.displayMessage( "MeMate", "Standup Meeting", MessageType.NONE );
              break;
            }
          }
        }
        else
        {
          try
          {
            Thread.sleep( 60000 );
          }
          catch ( final InterruptedException __ )
          {
            ClientLog.newLog( "No Sleep for the MeetingNotificationThread" );
          }
        }
      }
    } );
    thread.setName( "MeetingNotificationThread" );
    thread.start();
  }

  private void initTrayIcon()
  {
    if ( SystemTray.isSupported() )
    {
      final SystemTray tray = SystemTray.getSystemTray();
      final Image trayImage = Toolkit.getDefaultToolkit()
          .getImage( ServerCommunication.class.getClassLoader().getResource( "trayicon.png" ) );
      trayIcon = new TrayIcon( trayImage );
      trayIcon.setImageAutoSize( true );
      trayIcon.setToolTip( "MeMate" );
      try
      {
        tray.add( trayIcon );
      }
      catch ( final AWTException exception )
      {
        ClientLog.newLog( "Failed to show trayIcon" + exception );
      }
    }
  }

  /**
   * Sollte es Änderungen der History geben, so wird geprüft, ob jemand etwas
   * getrunken hat. Wenn dies der Fall ist, popt eine Benachrichtigung auf, sollte
   * der User diese Einstellung aktiviert haben.
   */
  private void checkForChanges()
  {
    final String[][] history = cache.getShortHistory();
    if ( history != null && PropertyHelper.getBooleanProperty( "ConsumptionNotification" ) )
    {
      final ZonedDateTime today = ZonedDateTime.now();
      final ZonedDateTime twentyMinutesAgo = today.minusMinutes( 20 );
      for ( final String[] data : history )
      {
        final String action = data[ 0 ];
        final String consumer = data[ 1 ];
        final String date = data[ 2 ];
        final String drinkname = data[ 3 ];
        if ( HistoryEvents.CONSUMED_DRINK == HistoryEvents.valueOf( action ) )
        {
          final Date eventDate = new Date( Long.valueOf( date ) );
          if ( !eventDate.toInstant().isBefore( twentyMinutesAgo.toInstant() ) )
          {
            if ( !alreadyShownNotifications.contains( date ) )
            {
              alreadyShownNotifications.add( date );
              if ( SystemTray.isSupported() )
              {
                trayIcon.displayMessage( "MeMate", consumer + " trinkt gerade " + drinkname,
                    MessageType.NONE );
              }
            }
          }
        }
      }
    }
  }

  /**
   * Teilt dem Server mit, dass die letzte Aktion rückgängig gemacht werden soll.
   */
  void undoLastAction()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.UNDO, null ) );
      GUIObjects.mainframe.setUndoButtonEnabled( false );
    }
    catch ( final IOException exception )
    {
      ClientLog.newLog( "Die letzte Aktion konnte nicht rückgängig gemacht werden. " + exception );
    }
  }

  private void showErrorDialog( final String message, final String title )
  {
    ClientLog.newLog( message );
    final Alert alert = new Alert( AlertType.ERROR );
    alert.setTitle( title );
    alert.setHeaderText( null );
    alert.setContentText( message );
    alert.initOwner( GUIObjects.mainframe );
    alert.showAndWait();
  }

  /**
   * Fügt einem Getränk optional die Inhaltsstoffe hinzu.
   *
   * @param drinkIngredients Inhaltsstoffe
   */
  public void registerIngredients( final DrinkIngredients drinkIngredients )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.REGISTER_INGREDIENTS, drinkIngredients ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog(
          "Die Getränke konnten nicht geladen werden.\nBitte stellen Sie sicher, dass der Server an ist\nund Sie mit dem Internet verbunden sind",
          "Getränke laden fehlgeschlagen" );
    }
  }

  /**
   * Die eingebenen Logindaten werden an den Server gesendet und dieser überprüft
   * die Korrektheit der Daten.
   *
   * @param login ein {@link LoginInformation} Objekt, welches den Nutzernamen und
   *          das gehashte Passwort enthält.
   */
  void checkLogin( final LoginInformation login )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CHECK_LOGIN, login ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog(
          "Die Logininformationen konnten nicht an den Server geschickt werden.\nBitte stellen Sie sicher, dass der Server an ist\nund Sie mit dem Internet verbunden sind",
          "Login fehlgeschlagen" );
    }
  }

  public enum dateType
  {
    SHORT,
    LONG,
    MIDDLE;
  };


  /**
   * Es wird ein Shared-Objekt mit dem Befehl REGISTER_USER und einem User-Objekt
   * mit Name und Passwort an den Server geschickt.
   *
   * @param username Benutzername
   * @param password gehashtes Passwort
   */
  public void registerNewUser( final String username, final String password )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.REGISTER_USER, new User( username, password, null, -1 ) ) );
    }
    catch ( final IOException __ )
    {
      showErrorDialog( "Es konnte kein neuer Benutzer angelegt werden.", "Registrierung fehlgeschlagen" );
    }
  }

  /**
   * Wenn ein Benutzer ein Getränk bearbeitet, so wird ein Shared-Objekt, welches
   * den Befehl für die Änderung und entweder ein {@linkplain DrinkName},
   * {@linkplain DrinkChangeObject} oder {@linkplain DrinkPicture} Objekt enthält. Die
   * genannten Objekte enthalten die ID des des Getränks und die spezifische
   * Änderung.
   *
   * @param id ID des Getränks
   * @param operation kann entweder UPDATE_DRINKNAME, UPDATE_DRINKPRICE
   *          oder UPDATE_DRINKPICTURE sein, alle anderen
   *          Operationen werden ignoriert.
   * @param updatedInformation enthät die neue Informationen. Abhängig von der
   *          Operation kann dies ein Objekt mit dem neuen Namen,
   *          dem neuen Preis oder dem neuen Bild sein.
   */
  public void updateDrinkInformations( final Integer id, final Operation operation, final Object updatedInformation )
  {
    try
    {
      switch ( operation )
      {
        case UPDATE_DRINKNAME:
          outStream.writeObject(
              new Shared( Operation.UPDATE_DRINKNAME, new DrinkChangeObject( id, updatedInformation ) ) );
          break;
        case UPDATE_DRINKPRICE:
          outStream.writeObject(
              new Shared( Operation.UPDATE_DRINKPRICE, new DrinkChangeObject( id, updatedInformation ) ) );
          break;
        case UPDATE_DRINKPICTURE:
          outStream.writeObject(
              new Shared( Operation.UPDATE_DRINKPICTURE, new DrinkChangeObject( id, updatedInformation ) ) );
          break;
        case UPDATE_DRINKAMOUNT:
          outStream.writeObject(
              new Shared( Operation.UPDATE_DRINKAMOUNT, new DrinkChangeObject( id, updatedInformation ) ) );
          break;
        case UPDATE_BARCODE:
          outStream.writeObject(
              new Shared( Operation.UPDATE_BARCODE, new DrinkChangeObject( id, updatedInformation ) ) );
          break;
        default :
          break;
      }
    }
    catch ( final IOException __ )
    {
      showErrorDialog( "Das Getränk konnte nicht aktualisiert werden.", "Getränk bearbeiten fehlgeschlagen" );
    }
  }

  /**
   * Wenn ein neues Getränk registriert wird, werden zunächst die zugehörigen
   * Daten an den Server geschickt und dieser legt für das Getränk einen neuen
   * Datenbankeintrag an. Außerdem werden anschließend Bildermap. Preismap,
   * Dashboard und Drinkmanager aktualisiert.
   *
   * @param drink ein Drink-Objekt, welches alle angegeben Informationen des
   *          Getränks enthält.
   */
  public void registerNewDrink( final Drink drink )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.REGISTER_DRINK, drink ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Das Getränk konnte nicht hinzugefügt werden.", "Getränk hinzufügen fehlgeschlagen" );
    }
  }

  /**
   * Wenn ein Getränk entfernt werden soll, so wird die ID des Getränks an der
   * Server weitergegeben und dieser löscht den Eintrag in der Datenbank. Als
   * nächstes werden dann Bildermap. Preismap, Dashboard und Drinkmanager
   * aktualisiert.
   *
   * @param id Id of the drink
   */
  public void removeDrink( final Integer drinkID )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.REMOVE_DRINK, drinkID ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Das Getränk konnte nicht gelöscht werden.", "Getränk löschen fehlgeschlagen" );
    }
  }

  /**
   * Schickt den Nutzername und die zugehörige SessionID an den Server.
   *
   * @param username Nutzername
   * @param uuid SessionID
   */
  void connectSessionIDToUser( final String uuid )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CONNECT_SESSION_ID, uuid ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Die SessionID konnte nicht mit dem Nutzernamen verbunden werden.",
          "Session fehlgeschlagen" );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  /**
   * Wenn man eingeloggt bleibt, bekommt man keine neue SessionID, sondern diese
   * findes diese in den Properties. Dieses SessionID wird dem Server übergeben.
   *
   * @param sessionID SessionID
   */
  public void checkSessionID( final String sessionID )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CHECK_LOGIN_WITH_SESSION_ID, sessionID ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Die SessionID is ungültig oder bereits abgelaufen", "Session ungültig" );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  /**
   * Wenn man Guthaben hinzufügt, wird die Höhe der Einzahlung an den Server
   * geschickt.
   *
   * @param valueToAdd Amount to add to balance
   */
  public void addBalance( final int valueToAdd )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.ADD_BALANCE, valueToAdd ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Das Guthaben konnte nicht hinzugefügt werden", "Guthaben hinzufügen fehlgeschlagen" );
      ClientLog.newLog( exception.getMessage() );
    }

  }

  /**
   * Wenn ein Getränk gekauft wird, so wird der Name des Getränks an den Server
   * weiter geleitet, damit dieser den korrekten Betrag vom Nutzerkonto abziehen
   * kann.
   *
   * @param drink Name des Getränks
   */
  public void consumeDrink( final Drink drink )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CONSUM_DRINK, drink ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Das Getränk konnte nicht gekauft werden.", "Getränkekauf fehlgeschlagen" );
    }
  }

  /**
   * @param balance neuer Kontostand des Spaarschweins
   */
  public void setAdminBalance( final Float balance )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.SET_PIGGYBANK_BALANCE, balance ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Das Guthaben des Spaarschweins konnte nicht gesetzt werden.", "Admin-Error" );
    }
  }

  /**
   * Teilt dem Server mit, dass er den Kontostand des Spaarschweins schicken soll.
   */
  public void tellServerToSendPiggybankBalance()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.PIGGYBANK_BALANCE, 0f ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Das Guthaben des Spaarschweins konnte nicht geladen werden.", "Admin-Error" );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  /**
   * Teilt dem Server mit, dass der Nutzer sich ausgeloggt hat.
   */
  void logout()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.LOGOUT, null ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Ausloggen fehlgeschlagen", "Ausloggen" );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  /**
   * Teilt dem Server eine Passwortänderung mit.
   *
   * @param username Nutzername
   * @param password neues Passwort
   */
  public void changePassword( final String username, final String password )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CHANGE_PASSWORD, new User( username, password, 0f, 1 ) ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Passwort ändern fehlgeschlagen.", "Passwort" );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  public void changePassword( final String newPass )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CHANGE_PASSWORD_USER, newPass ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Passwort ändern fehlgeschlagen.", "Passwort" );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  public void changeDisplayName( final String newDisplayName )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CHANGE_DISPLAYNAME, newDisplayName ) );
    }
    catch ( final IOException exception )
    {
      showErrorDialog( "Anzeigenamen ändern fehlgeschlagen.", "Anzeigenamen" );
      ClientLog.newLog( exception.getMessage() );
    }
  }
}