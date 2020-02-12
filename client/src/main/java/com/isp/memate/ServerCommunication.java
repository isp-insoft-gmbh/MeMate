/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Cursor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.isp.memate.Shared.Operation;

/**
 * Die Klasse {@link ServerCommunication} kommunizert mit dem Server
 * und schickt verschiedenen Shared-Objekte an der Server.
 * Beispielsweise bei checkLogin schickt die Klasse ein Objekt, welches
 * den Befehl CHECK_LOGIN und ein Userobjekt, welches Nutzername und
 * gehashtes Passwort enthält an der Server.
 * 
 * @author nwe
 * @since 24.10.2019
 */
public class ServerCommunication
{
  private static final ServerCommunication    instance            = new ServerCommunication();
  public final ReentrantLock                  lock                = new ReentrantLock( true );
  private final ArrayList<Byte>               byteImageList       = new ArrayList<>();
  private final List<String>                  drinkNames          = new ArrayList<>();
  private final Map<String, Float>            priceMap            = new HashMap<>();
  private final Map<String, ImageIcon>        imageMap            = new HashMap<>();
  private final Map<String, Integer>          amountMap           = new HashMap<>();
  private final Map<String, Integer>          drinkIDMap          = new HashMap<>();
  private final Map<String, Boolean>          drinkIngredientsMap = new HashMap<>();
  private final Map<String, DrinkIngredients> IngredientsMap      = new HashMap<>();
  private static String                       version             = "x";
  private String[]                            userArray           = null;
  private User[]                              fullUserArray       = null;
  private Drink[]                             drinkArray          = null;
  String                                      currentUser         = null;
  private String[][]                          history;
  private String[][]                          shortHistory;
  private String[][]                          scoreboard;
  private Socket                              socket;
  private ObjectInputStream                   inStream;
  private ObjectOutputStream                  outStream;
  String                                      sessionID;

  /**
   * @return the static instance of {@link ServerCommunication}
   */
  public static ServerCommunication getInstance()
  {
    return instance;
  }

  /**
   * Erzeugt eine Socketverbindung zum Server.
   * Außerdem wird ein Output- und Inputsteam definiert.
   * Dannach werden zwei Tasks in bestimmten Abständen ausgeführt,
   * um die Serveranfragen zu bearbeiten und die Getränke auf dem
   * neusten Stand zu halten.
   */
  public ServerCommunication()
  {
    try
    {
      socket = new Socket( FindServer.getServerAddress(), FindServer.getServerPort() );
      //socket = new Socket( "192.168.168.82", 3142 );// This is for Testing TODO remove later
      outStream = new ObjectOutputStream( socket.getOutputStream() );
      inStream = new ObjectInputStream( socket.getInputStream() );
    }
    catch ( IOException __ )
    {
      showErrorDialog( "Der Server konnte nicht gefunden werden. Bitte stelle sicher, dass der Server an ist", "Server nicht gefunden" );
    }
    /**
     * Dieser Task überprüft alle 100 Milliseconds, ob der Server etwas gesendet hat.
     * Wenn ja wird das Objekt zugeordnet und die entsprechenede Aufgabe ausgeführt.
     */
    TimerTask task = new TimerTask()
    {
      @Override
      public void run()
      {
        try
        {
          Shared shared = (Shared) inStream.readObject();
          Operation operation = shared.operation;
          System.out.println( operation );
          switch ( operation )
          {
            case GET_DRINKINFO:
              drinkArray = shared.drinkInfos;
              updateMaps( shared.drinkInfos );
              break;
            case LOGIN_RESULT:
              Login.getInstance().validateLoginResult( shared.loginResult );
              break;
            case GET_BALANCE_RESULT:
              Mainframe.getInstance().updateBalanceLabel( shared.userBalance );
              break;
            case REGISTRATION_RESULT:
              Login.getInstance().validateRegistartionResult( shared.registrationResult );
              break;
            case GET_HISTORY:
              updateHistory( shared.history );
              break;
            case GET_HISTORY_LAST_5:
              updateShortHistory( shared.shortHistory );
              break;
            case GET_SCOREBOARD:
              updateScoreboard( shared.scoreboard );
              break;
            case GET_USERNAME_FOR_SESSION_ID_RESULT:
              String username = shared.username;
              updateCurrentUser( username );
              if ( username == null )
              {
                break;
              }
              Mainframe.getInstance().setHelloLabel( username );
              tellServerToSendDrinkInformations();
              getBalance( ServerCommunication.getInstance().currentUser );
              break;
            case PRICE_CHANGED:
              Dashboard.getInstance().showPriceChangedDialog( shared.drinkPrice.name, shared.drinkPrice.price );
              break;
            case NO_MORE_DRINKS_AVAIBLE:
              Dashboard.getInstance().showNoMoreDrinksDialog( shared.consumedDrink );
              break;
            case PIGGYBANK_BALANCE:
              Adminview.getInstance().updatePiggybankBalanceLabel( shared.userBalance );
              break;
            case GET_USERS_RESULT:
              userArray = shared.users;
            case GET_FULLUSERS_RESULT:
              fullUserArray = shared.fullUserArray;
              break;
            case GET_VERSION:
              version = shared.version;
              break;
            default :
              break;
          }
        }
        catch ( ClassNotFoundException | IOException exception )
        {
          exception.printStackTrace();
        }
      }
    };

    /**
     * Dieser Task sorgt dafür, dass die Getränke alle 30
     * Sekunden aktualisiert werden.
     */
    TimerTask task2 = new TimerTask()
    {
      @Override
      public void run()
      {
        tellServerToSendDrinkInformations();
      }
    };
    TimerTask task3 = new TimerTask()
    {
      @Override
      public void run()
      {
        tellServerToSendHistoryData();
      }
    };
    tellServertoSendUserArray();
    Timer timer = new Timer();
    timer.schedule( task, 0, 100 );
    Timer timer2 = new Timer();
    timer2.schedule( task2, 10000, 30000 );
    Timer timer3 = new Timer();
    timer3.schedule( task3, 20000, 300000 );
  }


  /**
   * @param history
   */
  protected void updateHistory( String[][] history )
  {
    List<String[]> list = Arrays.asList( history );
    Collections.reverse( list );
    this.history = list.toArray( history ).clone();
    History.getInstance().updateHistory();
    ConsumptionRate.getInstance().addGraph();
    CreditHistory.getInstance().addChart();
    Adminview.getInstance().updateDrinkAmounts();
    Social.update();
  }

  /**
   * @param history
   */
  protected void updateShortHistory( String[][] history )
  {
    this.shortHistory = history;
    Social.update();
  }

  /**
   * @param history
   */
  protected void updateScoreboard( String[][] history )
  {
    this.scoreboard = history;
    Social.update();
  }

  /**
   * @param name Name des Getränks
   * @return Image des Getränks.
   */
  public ImageIcon getIcon( String name )
  {
    return imageMap.get( name );
  }

  /**
   * @param name Name des Getränks
   * @return Preis des Getränks.
   */
  public Float getPrice( String name )
  {
    return priceMap.get( name );
  }

  /**
   * @param name Name des Getränks
   * @return ID des Getränks.
   */
  public Integer getID( String name )
  {
    return drinkIDMap.get( name );
  }

  /**
   * @param name Name des Getränks
   * @return Anzahl des Getränks.
   */
  public Integer getAmount( String name )
  {
    return amountMap.get( name );
  }

  /**
   * @param name Name des Getränks
   * @return Inhaltsstoffe des Getränks
   */
  public DrinkIngredients getIngredients( String name )
  {
    return IngredientsMap.get( name );
  }

  /**
   * @param name Name des Getränks
   * @return Ob es Inhaltsangaben über das Getränk gibt oder nicht.
   */
  public Boolean hasIngredients( String name )
  {
    return drinkIngredientsMap.get( name );
  }


  /**
   * Die Preismap, Bildermap und eine Liste von allen Namen
   * der Getränke werden gefüllt. Diese Maps werden unter anderem
   * von dem Dashboard oder dem Drinkmanager genutzt.
   * Die Methode wird beim Start des Programms aufgerufen oder wenn
   * Veränderungen an Getränken vorgenommen werden.
   */
  private void updateMaps( Drink[] drinkInfos )
  {
    lock.lock();
    List<String> oldDrinkNames = new ArrayList<>( drinkNames );
    Map<String, Float> oldPriceMap = new HashMap<>();
    oldPriceMap.putAll( priceMap );
    Map<String, Integer> oldAmountMap = new HashMap<>();
    oldAmountMap.putAll( amountMap );
    ArrayList<Byte> oldByteImageList = new ArrayList<>( byteImageList );

    priceMap.clear();
    amountMap.clear();
    imageMap.clear();
    drinkIDMap.clear();
    drinkNames.clear();
    drinkIngredientsMap.clear();
    IngredientsMap.clear();
    byteImageList.clear();
    for ( Drink drink : drinkInfos )
    {
      String name = drink.name;
      Float price = drink.price;
      int amount = drink.amount;
      byte[] pictureInBytes = drink.pictureInBytes;
      Integer id = drink.id;
      ImageIcon icon = new ImageIcon( pictureInBytes );
      priceMap.put( name, price );
      imageMap.put( name, icon );
      amountMap.put( name, amount );
      //FIXME Das muss besser werden
      //Der 355. byte des Bildes wird in eine Liste hinzugefügt, welche anschließend mit der vorherigen Liste verglichen wird. 
      byteImageList.add( pictureInBytes[ 355 ] );
      drinkIDMap.put( name, id );
      drinkNames.add( name );
      drinkIngredientsMap.put( name, drink.ingredients );
      IngredientsMap.put( name, drink.drinkIngredients );
    }
    if ( !drinkNames.equals( oldDrinkNames ) || !priceMap.equals( oldPriceMap ) || !byteImageList.equals( oldByteImageList )
        || !amountMap.equals( oldAmountMap ) )
    {
      System.out.println( "GUIUPDATE" );
      Mainframe.getInstance().updateDashboardAndDrinkmanager();
    }
    lock.unlock();
    Mainframe.getInstance().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
  }


  /**
   * Sagt dem Server, dass er die Historie schicken soll.
   */
  public void tellServerToSendHistoryData()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.GET_HISTORY, new String[0][0] ) );
    }
    catch ( IOException exception )
    {
      System.out.println( "Die Historie konnte nicht geladen werden. " + exception );
    }
  }

  /**
   * Sagt dem Server, dass er ein Array mit allen Nutzern schicken soll.
   */
  private void tellServertoSendUserArray()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.GET_USERS, null ) );
    }
    catch ( Exception exception )
    {
      System.out.println( "Die Nutzer konnten nicht geladen werden. " + exception );
    }
  }

  /**
   * Sagt dem Server, dass er die aktuelle Versionsnummer schicken soll.
   */
  public void tellServertoSendVersionNumber()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.GET_VERSION, "x" ) );
    }
    catch ( Exception exception )
    {
      System.out.println( "Die Versionsnummer konnte nicht geladen werden. " + exception );
    }
  }

  /**
   * Sagt dem Server, dass die letzte Aktion rückgängig gemacht werden soll.
   */
  public void undoLastAction()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.UNDO, null ) );
      Mainframe.getInstance().setUndoButtonEnabled( false );
    }
    catch ( IOException exception )
    {
      System.out.println( "Die letzte Aktion konnte nicht rückgängig gemacht werden. " + exception );
    }
  }

  /**
   * @param message Error-Nachricht
   * @param title Titel des Errordialogs
   */
  private void showErrorDialog( String message, String title )
  {
    JOptionPane.showMessageDialog( Mainframe.getInstance(), message, title, JOptionPane.ERROR_MESSAGE, null );
  }

  /**
   * Wird beim Login aufgerufen, damit der
   * Socket weiß, um welchen User es sich handelt.
   * 
   * @param username Benutzername
   */
  public void updateCurrentUser( String username )
  {
    this.currentUser = username;
  }


  /**
   * Schickt einen Befehl an den Server, damit dieser
   * die Getränksinformationen sendet.
   */
  public void tellServerToSendDrinkInformations()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.GET_DRINKINFO, new Drink[0] ) );
      outStream.reset();
    }
    catch ( IOException exception )
    {
      showErrorDialog(
          "Die Getränke konnten nicht geladen werden.\nBitte stellen Sie sicher, dass der Server an ist\nund Sie mit dem Internet verbunden sind",
          "Getränke laden fehlgeschlagen" );
    }
  }

  /**
   * Fügt einem Getränk optional die Inhaltsstoffe hinzu
   * 
   * @param drinkIngredients Inhaltsstoffe
   */
  public void registerIngredients( DrinkIngredients drinkIngredients )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.REGISTER_INGREDIENTS, drinkIngredients ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog(
          "Die Getränke konnten nicht geladen werden.\nBitte stellen Sie sicher, dass der Server an ist\nund Sie mit dem Internet verbunden sind",
          "Getränke laden fehlgeschlagen" );
    }
  }


  /**
   * Die eingebenen Logindaten werden an den Server
   * gesendet und dieser überprüft die Korrektheit der Daten.
   * 
   * @param login ein {@link LoginInformation} Objekt, welches den Nutzernamen und das gehashte Passwort
   *          enthält.
   */
  public void checkLogin( LoginInformation login )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CHECK_LOGIN, login ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog(
          "Die Logininformationen konnten nicht an den Server geschickt werden.\nBitte stellen Sie sicher, dass der Server an ist\nund Sie mit dem Internet verbunden sind",
          "Login fehlgeschlagen" );
    }
  }

  /**
   * Es wird ein Shared-Objekt mit dem Befehl GET_BALANCE
   * und dem Nutzernamen an den Server geschickt.
   * 
   * @param username Benutzername
   */
  public void getBalance( String username )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.GET_BALANCE, username ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog(
          "Das Guthaben konnte nicht geladen werden.\nBitte stellen Sie sicher, dass der Server gestartet ist\nund Sie mit dem Internet verbunden sind.",
          "Guthaben laden fehlgeschlagen" );
    }
  }


  /**
   * Gibt die Historydaten zurück.
   * 
   * @param dateType gibt an in welchem Format das Datum zurück gegeben werden soll
   * 
   * @return Die Historydaten als 2D Array
   */
  public String[][] getHistoryData( dateType dateType )
  {
    if ( history == null )
    {
      return history;
    }
    String[][] historyArray = new String[history.length][];
    for ( int i = 0; i < history.length; i++ )
    {
      historyArray[ i ] = Arrays.copyOf( history[ i ], history[ i ].length );
    }
    if ( dateType != null && dateType == com.isp.memate.ServerCommunication.dateType.SHORT )
    {
      for ( int i = 0; i < historyArray.length; i++ )
      {
        historyArray[ i ][ 4 ] = historyArray[ i ][ 4 ].substring( 0, 10 );
      }
    }
    else if ( dateType == com.isp.memate.ServerCommunication.dateType.MIDDLE )
    {
      for ( int i = 0; i < historyArray.length; i++ )
      {
        historyArray[ i ][ 4 ] = historyArray[ i ][ 4 ].substring( 0, 16 ).replace( "T", " " );
      }
    }
    return historyArray;
  }

  /**
   * @return die letzten fünf Einträge der History.
   */
  public String[][] getShortHistory()
  {
    if ( shortHistory == null )
    {
      return shortHistory;
    }
    else
    {
      String[][] historyArray = new String[shortHistory.length][];
      for ( int i = 0; i < shortHistory.length; i++ )
      {
        historyArray[ i ] = Arrays.copyOf( shortHistory[ i ], shortHistory[ i ].length );
      }
      for ( int i = 0; i < historyArray.length; i++ )
      {
        historyArray[ i ][ 2 ] = historyArray[ i ][ 2 ].substring( 0, 16 ).replace( "T", " " );
      }
      return historyArray;
    }
  }

  public String[][] getScoreboard()
  {
    return scoreboard;
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
  public void registerNewUser( String username, String password )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.REGISTER_USER, new User( username, password, null, -1 ) ) );
    }
    catch ( IOException __ )
    {
      showErrorDialog( "Es konnte kein neuer Benutzer angelegt werden.", "Registrierung fehlgeschlagen" );
    }
  }

  /**
   * Wenn ein Benutzer ein Getränk bearbeitet, so wird ein Shared-Objekt, welches den Befehl für die
   * Änderung und entweder ein {@linkplain DrinkName}, {@linkplain DrinkPrice} oder {@linkplain DrinkPicture}
   * Objekt enthält. Die genannten Objekte enthalten die ID des des Getränks und die spezifische Änderung.
   * 
   * @param id ID des Getränks
   * @param operation kann entweder UPDATE_DRINKNAME, UPDATE_DRINKPRICE oder UPDATE_DRINKPICTURE sein, alle
   *          anderen Operationen werden ignoriert.
   * @param updatedInformation enthät die neue Informationen. Abhängig von der Operation kann dies ein Objekt
   *          mit dem neuen
   *          Namen, dem neuen Preis oder dem neuen Bild sein.
   */
  public void updateDrinkInformations( Integer id, Operation operation, Object updatedInformation )
  {
    try
    {
      switch ( operation )
      {
        case UPDATE_DRINKNAME:
          outStream.writeObject( new Shared( Operation.UPDATE_DRINKNAME, new DrinkName( (String) updatedInformation, id ) ) );
          break;
        case UPDATE_DRINKPRICE:
          outStream.writeObject( new Shared( Operation.UPDATE_DRINKPRICE, new DrinkPrice( (Float) updatedInformation, id, null ) ) );
          break;
        case UPDATE_DRINKPICTURE:
          outStream
              .writeObject( new Shared( Operation.UPDATE_DRINKPICTURE, new DrinkPicture( (byte[]) updatedInformation, id ) ) );
          break;
        default :
          break;
      }
    }
    catch ( IOException __ )
    {
      showErrorDialog( "Das Getränk konnte nicht aktualisiert werden.", "Getränk bearbeiten fehlgeschlagen" );
    }
    tellServerToSendDrinkInformations();
  }

  /**
   * Wenn ein neues Getränk registriert wird, werden zunächst die zugehörigen Daten an
   * den Server geschickt und dieser legt für das Getränk einen neuen Datenbankeintrag an.
   * Außerdem werden anschließend Bildermap. Preismap, Dashboard und Drinkmanager aktualisiert.
   * 
   * @param drink ein Drink-Objekt, welches alle angegeben Informationen des Getränks enthält.
   */
  public void registerNewDrink( Drink drink )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.REGISTER_DRINK, drink ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Das Getränk konnte nicht hinzugefügt werden.", "Getränk hinzufügen fehlgeschlagen" );
    }
    tellServerToSendDrinkInformations();
    Mainframe.getInstance().updateDashboardAndDrinkmanager();
  }

  /**
   * Wenn ein Getränk entfernt werden soll, so wird die ID des Getränks an der
   * Server weitergegeben und dieser löscht den Eintrag in der Datenbank.
   * Als nächstes werden dann Bildermap. Preismap, Dashboard
   * und Drinkmanager aktualisiert.
   * 
   * @param id ID des Getränks
   * @param name Name des Getränks
   */
  public void removeDrink( Integer id, String name )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.REMOVE_DRINK, new Drink( name, null, null, id, null, -1, false, null ) ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Das Getränk konnte nicht gelöscht werden.", "Getränk löschen fehlgeschlagen" );
    }
    tellServerToSendDrinkInformations();
    Mainframe.getInstance().updateDashboardAndDrinkmanager();
  }

  @SuppressWarnings( "javadoc" )
  public List<String> getDrinkNames()
  {
    if ( drinkNames != null )
    {
      return new ArrayList<>( drinkNames );
    }
    else
    {
      return new ArrayList<>();
    }
  }

  /**
   * Schickt den Nutzername und die zugehörige SessionID an den Server.
   * 
   * @param username Nutzername
   * @param uuid SessionID
   */
  public void connectSessionIDToUser( String username, String uuid )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CONNECT_SESSION_ID, new SessionID( uuid, username ) ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Die SessionID konnte nicht mit dem Nutzernamen verbunden werden.", "Session fehlgeschlagen" );
      exception.printStackTrace();
    }
  }


  /**
   * Wenn man eingeloggt bleibt, bekommt man keine neue SessionID, sondern diese findes diese
   * in den Properties. Dieses SessionID wird dem Server übergeben.
   * 
   * @param sessionID SessionID
   */
  public void checkLoginForSessionID( String sessionID )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.GET_USERNAME_FOR_SESSION_ID, sessionID ) );

    }
    catch ( IOException exception )
    {
      showErrorDialog( "Die SessionID is ungültig oder bereits abgelaufen", "Session ungültig" );
      exception.printStackTrace();
    }
  }

  /**
   * Wenn man Guthaben hinzufügt, wird die Höhe der Einzahlung an den Server geschickt.
   * 
   * @param valueToAdd Amount to add to balance
   */
  public void addBalance( int valueToAdd )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.ADD_BALANCE, valueToAdd ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Das Guthaben konnte nicht hinzugefügt werden", "Guthaben hinzufügen fehlgeschlagen" );
      exception.printStackTrace();
    }

  }

  /**
   * Wenn ein Getränk gekauft wird, so wird der Name des Getränks an den Server weiter geleitet, damit dieser
   * den korrekten Betrag vom Nutzerkonto abziehen kann.
   * 
   * @param drinkName Name des Getränks
   */
  public void consumeDrink( String drinkName )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CONSUM_DRINK, new DrinkPrice( priceMap.get( drinkName ), -1, drinkName ) ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Das Getränk konnte nicht gekauft werden.", "Getränkekauf fehlgeschlagen" );
    }
    tellServerToSendDrinkInformations();
  }

  /**
   * @param balance neuer Kontostand des Spaarschweins
   */
  public void setAdminBalance( Float balance )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.SET_PIGGYBANK_BALANCE, balance ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Das Guthaben des Spaarschweins konnte nicht gesetzt werden.", "Admin-Error" );
    }
  }


  /**
   * Sagt dem Server, dass er Den Kontostand des Spaarschweins schicken soll.
   */
  public void tellServerToSendPiggybankBalance()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.PIGGYBANK_BALANCE, 0f ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Das Guthaben des Spaarschweins konnte nicht geladen werden.", "Admin-Error" );
      exception.printStackTrace();
    }
  }

  @SuppressWarnings( "javadoc" )
  public void setDrinkAmount( String name, int amount )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.SET_DRINK_AMOUNT, new DrinkAmount( name, amount ) ) );
    }
    catch ( IOException exception )
    {
      exception.printStackTrace();
    }
  }

  /**
   * Teilt dem Server mit, dass der Nutzer sich ausgeloggt hat.
   */
  public void logout()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.LOGOUT, null ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Ausloggen fehlgeschlagen", "Ausloggen" );
      exception.printStackTrace();
    }
  }


  @SuppressWarnings( "javadoc" )
  public String[] getAllUsers()
  {
    return userArray;
  }

  /**
   * Teilt dem Server eine Passwortänderung mit.
   * 
   * @param username Nutzername
   * @param password neues Passwort
   */
  public void changePassword( String username, String password )
  {
    try
    {
      outStream.writeObject( new Shared( Operation.CHANGE_PASSWORD, new User( username, password, 0f, 1 ) ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Passwort ändern fehlgeschlagen.", "Passwort" );
      exception.printStackTrace();
    }
  }

  /**
   * @return ein user Array mit allen Informationen um diese zu exportieren.
   */
  public User[] getUserArray()
  {
    return fullUserArray;
  }

  /**
   * @return ein Array mit allen Getränken.
   */
  public Drink[] getDrinkArray()
  {
    return drinkArray;
  }

  /**
   * Überprüft Server- und Clientversion, wenn diese nicht übereinstimmen folgt ein Dialog.
   * 
   * @param clientVersion Version des Clients
   */
  public void checkVersion( String clientVersion )
  {
    System.out.println( "CHECK" );
    System.out.println( version );
    System.out.println( clientVersion );
    if ( !version.equals( clientVersion ) )
    {
      JOptionPane.showMessageDialog( FocusManager.getCurrentManager().getActiveWindow(),
          "Es sind Updates verfügbar.\nInstallierte Produkt-Version: " + Main.version + "\nServer-Version: "
              + ServerCommunication.version,
          "Update",
          JOptionPane.ERROR_MESSAGE, null );
    }
  }
}
