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

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.isp.memate.Shared.Operation;

/**
 * Die Klasse {@link ServerCommunication} kommunizert mit dem Server
 * und schickt verschiedenen Shared-Objekte an der Server.
 * Beispielsweise bei checkLogin schickt die Klasse ein Objekt, welches
 * den Befehl CHECK_LOGIN und ein Userobjekt, welches Nutzername und
 * gehastes Passwort enthält an der Server.
 * 
 * @author nwe
 * @since 24.10.2019
 */
public class ServerCommunication
{
  private static final ServerCommunication    instance            = new ServerCommunication();
  private final Map<String, Float>            priceMap            = new HashMap<>();
  private final Map<String, ImageIcon>        imageMap            = new HashMap<>();
  private final Map<String, Integer>          amountMap           = new HashMap<>();
  private final Map<String, Integer>          drinkIDMap          = new HashMap<>();
  private final Map<String, Boolean>          drinkIngredientsMap = new HashMap<>();
  private final Map<String, DrinkIngredients> IngredientsMap      = new HashMap<>();
  private final ArrayList<Byte>               byteImageList       = new ArrayList<>();

  final List<String> drinkNames  = new ArrayList<>();
  String[][]         history;
  String             sessionID;
  Socket             socket;
  ObjectInputStream  inStream;
  ObjectOutputStream outStream;
  String             currentUser = null;

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
    Timer timer = new Timer();
    timer.schedule( task, 0, 100 );
    Timer timer2 = new Timer();
    timer2.schedule( task2, 10000, 30000 );
  }

  /**
   * @param history
   */
  protected void updateHistory( String[][] history )
  {
    List<String[]> list = Arrays.asList( history );
    Collections.reverse( list );
    this.history = list.toArray( history );
    History.getInstance().updateHistory();
    Stats.getInstance().addGraph();
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
      String pictureInBytes = drink.pictureInBytes;
      Integer id = drink.id;
      String[] byteValues = pictureInBytes.substring( 1, pictureInBytes.length() - 1 ).split( "," );
      byte[] bytes = new byte[byteValues.length];
      for ( int i = 0, len = bytes.length; i < len; i++ )
      {
        bytes[ i ] = Byte.parseByte( byteValues[ i ].trim() );
      }
      ImageIcon icon = new ImageIcon( bytes );
      priceMap.put( name, price );
      imageMap.put( name, icon );
      amountMap.put( name, amount );
      byteImageList.add( bytes[ 355 ] );
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
    Mainframe.getInstance().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
  }

  /**
   * Sagt dem Server, dass er die Histore schicken soll.
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

  public void undoLastAction()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.UNDO, null ) );
      Dashboard.getInstance().undoButton.setEnabled( false );
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
   * @return Die Historydaten als 2D Array
   */
  public String[][] getHistoryData()
  {
    String[][] historyArray = history;
    if ( history == null )
    {
      return history;
    }
    for ( int i = 0; i < historyArray.length; i++ )
    {
      historyArray[ i ][ 4 ] = historyArray[ i ][ 4 ].substring( 0, 10 );
    }

    return historyArray;
  }


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
    return new ArrayList<>( drinkNames );
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


  @SuppressWarnings( "javadoc" )
  public void tellServerToSendPiggybankBalance()
  {
    try
    {
      outStream.writeObject( new Shared( Operation.PIGGYBANK_BALANCE, 0f ) );
    }
    catch ( IOException exception )
    {
      showErrorDialog( "Das Guthaben des Spaarschweins konnte nicht geladen werden.", "Admin-Error" );
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
      // TODO(nwe|17.12.2019): Fehlerbehandlung muss noch implementiert werden!
    }
  }

  /**
   * 
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
    }
  }
}