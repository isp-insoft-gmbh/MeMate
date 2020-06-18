/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.isp.memate.ServerLog.logType;
import com.isp.memate.Shared.LoginResult;
import com.isp.memate.Shared.Operation;

/**
 * Stellt die Verbindung zwischen Server und der Datenbank her.
 *
 * @author nwe
 * @since 24.10.2019
 */
class Database
{
  private Connection          conn = null;
  private final ReentrantLock lock = new ReentrantLock( true );

  /**
   * @return path for all configuration / data; changes depending on OS
   */
  static Path getTargetFolder()
  {
    if ( System.getProperty( "os.name" ).toLowerCase().contains( "windows" ) )
    {
      return Paths.get( System.getenv( "APPDATA" ), "MeMate" );
    }
    else
    {
      return Paths.get( System.getProperty( "user.home" ), ".config", "MeMate" );
    }
  }

  /**
   * Erstellt zuerst den Ordner in dem die Datenbank liegen soll, wenn nicht vorhanden.
   * Dannach wird versucht eine Verbindung aufzubauen, einige Tables hinzugefügt
   * und die SessionID werden aufgeräumt.
   *
   * @param dataBasePath Startparameter Pfad der Datenbank
   */
  Database( final String dataBasePath )
  {
    try
    {
      Files.createDirectories( getTargetFolder() );
      final File logFile = new File( getTargetFolder().toString() + File.separator + "ServerLog.log" );
      logFile.createNewFile();
    }
    catch ( final IOException exception )
    {
      ServerLog.newLog( logType.ERROR, "Der Ordner für die Datenbank konnte nicht erstellt werden." + exception.getMessage() );
    }
    try
    {
      conn = DriverManager.getConnection( "jdbc:sqlite:" + dataBasePath );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    addUserTable();
    addSessionIDTable();
    addDrinkTable();
    addHistoryTable();
    addPiggyBankTable();
    addIngredientsTable();
    cleanSessionIDTable();
  }


  /**
   * Erstellt den Drink-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addDrinkTable()
  {
    final String sql = "CREATE TABLE IF NOT EXISTS drink ("
        + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "name string NOT NULL UNIQUE,"
        + "preis double NOT NULL CHECK (Preis != 0),"
        + "picture blob NOT NULL,"
        + "amount integer NOT NULL DEFAULT 0,"
        + "ingredients BOOLEAN DEFAULT (false)"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
  }


  /**
   * Erstellt den History-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addHistoryTable()
  {
    final String sql = "CREATE TABLE IF NOT EXISTS historie_log ("
        + "action string NOT NULL,"
        + "consumer REFERENCES user(username),"
        + "transaction_price double NOT NULL,"
        + "balance double NOT NULL,"
        + "date string NOT NULL,"
        + "undo BOOLEAN DEFAULT (false)"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
  }


  /**
   * Erstellt den User-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addUserTable()
  {
    final String sql = "CREATE TABLE IF NOT EXISTS user ("
        + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "guthaben double NOT NULL,"
        + "username string UNIQUE NOT NULL,"
        + "password string NOT NULL,"
        + "requestNewPassword boolean DEFAULT (false),"
        + "DisplayName string UNIQUE"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    final String sql2 = "SELECT username FROM user WHERE username = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql2 ); )
    {
      pstmt.setString( 1, "admin" );
      final ResultSet rs = pstmt.executeQuery();
      rs.getString( "username" );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
      registerNewUser( "admin", "8C6976E5B541415BDE98BD4DEE15DFB167A9C873FC4BB8A81F6F2AB448A918" );
    }
  }

  /**
   * Erstellt den SessionID-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addSessionIDTable()
  {
    final String sql = "CREATE TABLE IF NOT EXISTS session_id ("
        + "user REFERENCES user(ID),"
        + "sessionID string NOT NULL UNIQUE,"
        + "last_login string NOT NULL"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
  }

  /**
   * Erstellt den Inhaltsstoffe-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addIngredientsTable()
  {
    final String sql = "CREATE TABLE IF NOT EXISTS ingredients ("
        + "drink REFERENCES drink(ID),"
        + "ingredients string NOT NULL,"
        + "energy_kJ integer NOT NULL,"
        + "energy_kcal integer NOT NULL,"
        + "fat double NOT NULL,"
        + "fatty_acids double NOT NULL,"
        + "carbs double NOT NULL,"
        + "sugar double NOT NULL,"
        + "protein double NOT NULL,"
        + "salt double NOT NULL,"
        + "amount double NOT NULL"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
  }

  /**
   * Erstellt den PiggyBank-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addPiggyBankTable()
  {
    final String sql = "CREATE TABLE IF NOT EXISTS piggy_bank ("
        + "guthaben double NOT NULL DEFAULT (0.0)"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.ERROR, e.getMessage() );
    }
    if ( getPiggyBankBalance() == null )
    {
      final String sql2 = "INSERT INTO piggy_bank(guthaben) VALUES(?)";
      try ( PreparedStatement pstmt = conn.prepareStatement( sql2 ) )
      {
        pstmt.setFloat( 1, 0f );
        pstmt.executeUpdate();
      }
      catch ( final SQLException e )
      {
        ServerLog.newLog( logType.SQL, e.getMessage() );
      }
    }
  }

  /**
   * Der Guthaben-wert aus dem user-table,welcher zu der gegebenen User-ID gehört wird zurück gegeben.
   *
   * @param id ID des Nutzers
   * @return Kontostand des Nutzers
   */
  Float getBalance( final Integer id )
  {
    Float balance = 0f;
    final String sql = "SELECT guthaben FROM user WHERE ID= ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ); )
    {
      pstmt.setInt( 1, id );
      final ResultSet rs = pstmt.executeQuery();
      balance = rs.getFloat( "guthaben" );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return balance;
  }

  /**
   * Der DisplayName wird zurückgegeben.
   *
   * @param Nutzername
   * @return Displayname
   */
  String getDisplayName( final String username )
  {
    String displayname = username;
    final String sql = "SELECT DisplayName FROM user WHERE username= ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ); )
    {
      pstmt.setString( 1, username );
      final ResultSet rs = pstmt.executeQuery();
      displayname = rs.getString( "DisplayName" );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return displayname;
  }


  /**
   * Füllt eine Map mit allen Usern und zugehörigen IDs.
   *
   * @return die Map
   */
  public Map<String, Integer> getUserIDMap()
  {
    final Map<String, Integer> userMap = new HashMap<>();
    final String sql = "SELECT ID,username FROM user";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        userMap.put( rs.getString( "username" ), rs.getInt( "ID" ) );
      }
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return userMap;
  }

  /**
   * Legt einen neuen Datenbankeintrag mit den gegebenen Informationen in dem table user an.
   *
   * @param username Benutzername
   * @param password Passwort
   * @return Wenn eine Exception auftritt so wird, die Nachricht "Benutzername bereits vergeben" zurückgegeben,
   *         ansonsten "Registrierung erfolgreich"
   */
  String registerNewUser( final String username, final String password )
  {
    lock.lock();
    final String sql = "INSERT INTO user(guthaben,username,password,DisplayName) VALUES(?,?,?,?)";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setFloat( 1, 0f );
      pstmt.setString( 2, username );
      pstmt.setString( 3, password );
      pstmt.setString( 4, username );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, "Benutzername bereits vergeben." );
      return "Benutzername bereits vergeben.";
    }
    finally
    {
      lock.unlock();
    }
    ServerLog.newLog( logType.INFO, "Registrierung erfolgreich." );
    return "Registrierung erfolgreich.";
  }

  /**
   * Überschreibt den Guthaben-wert des gegebenen Nutzers in dem user-table.
   *
   * @param sessionID ID des Nutzers.
   * @param updatedBalance neuer Kontostand
   */
  void updateBalance( final String sessionID, final Float updatedBalance )
  {
    final String username = getUsernameForSessionID( sessionID );
    final String sql = "UPDATE user SET guthaben=? WHERE username=?";
    lock.lock();
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setFloat( 1, updatedBalance );
      pstmt.setString( 2, username );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }


  /**
   * Überschreibt eine bestimmte Information des Getränks,
   * abhängig von der Operation.
   *
   * @param id ID des Getränks.
   * @param operation Operation.
   * @param updatedInformation die neue Information, kann Name, Preis oder Bild sein.
   */
  void updateDrinkInformation( final Integer id, final Operation operation, final Object updatedInformation )
  {
    String sql = null;
    switch ( operation )
    {
      case UPDATE_DRINKNAME:
        sql = "UPDATE drink SET name=? WHERE ID=?";
        break;
      case UPDATE_DRINKPICTURE:
        sql = "UPDATE drink SET picture=? WHERE ID=?";
        break;
      case UPDATE_DRINKPRICE:
        sql = "UPDATE drink SET preis=? WHERE ID=?";
        break;
      default :
        break;
    }
    lock.lock();
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setInt( 2, id );
      switch ( operation )
      {
        case UPDATE_DRINKNAME:
          pstmt.setString( 1, (String) updatedInformation );
          break;
        case UPDATE_DRINKPICTURE:
          pstmt.setBytes( 1, (byte[]) updatedInformation );
          break;
        case UPDATE_DRINKPRICE:
          pstmt.setFloat( 1, (float) updatedInformation );
          break;
        default :
          break;
      }
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }


  /**
   * Liest alle Getränke aus der Datenbank und
   * erstellt ein Array aus Drink-Objekten.
   *
   * @return das Drink-Objekt-Array
   */
  public Drink[] getDrinkInformations()
  {
    final ArrayList<Drink> drinkInfos = new ArrayList<>();

    final String sql = "SELECT ID,name,preis,picture,amount,ingredients FROM drink";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        if ( rs.getBoolean( "ingredients" ) )
        {
          drinkInfos
              .add( new Drink( rs.getString( "name" ), rs.getFloat( "preis" ), null, rs.getInt( "ID" ),
                  rs.getBytes( "picture" ), rs.getInt( "amount" ), rs.getBoolean( "ingredients" ),
                  getIngredients( rs.getInt( "ID" ) ) ) );
        }
        else
        {
          drinkInfos
              .add( new Drink( rs.getString( "name" ), rs.getFloat( "preis" ), null, rs.getInt( "ID" ),
                  rs.getBytes( "picture" ), rs.getInt( "amount" ), rs.getBoolean( "ingredients" ), null ) );
        }
      }
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return drinkInfos.toArray( new Drink[drinkInfos.size()] );
  }

  /**
   * Erzeugt einen neuen Datenbankeintrag für ein neues Getränk.
   *
   * @param name Name des Getränks
   * @param price Preis des Getränks
   * @param picture Bild des Getränks
   */
  void registerNewDrink( final String name, final Float price, final byte[] picture )
  {
    lock.lock();
    final String sql = "INSERT INTO drink(name,preis,picture) VALUES(?,?,?)";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, name );
      pstmt.setFloat( 2, price );
      pstmt.setBytes( 3, picture );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Löscht den Datenbankeintrag für das angegebene Getränk.
   *
   * @param id ID des Getränks
   */
  void removeDrink( final Integer id )
  {
    lock.lock();
    final String sql = "DELETE FROM drink WHERE ID= ?";

    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setInt( 1, id );
      pstmt.executeUpdate();

    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }


  /**
   * Überprüft die angegebenen Login-Informationen, sollten diese korrekt sein, so wird Login erfolgreich zurück
   * gegeben.
   *
   * @param username Nutzername
   * @param password gehashtes Passwort
   * @return ob der Login erfolgreich war oder nicht
   */
  LoginResult checkLogin( final String username, final String password )
  {
    final String sql = "SELECT password,requestNewPassword FROM user WHERE username = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, username );
      final ResultSet rs = pstmt.executeQuery();
      if ( rs.getString( "password" ).equals( password ) )
      {
        if ( rs.getBoolean( "requestNewPassword" ) )
        {
          return LoginResult.LOGIN_SUCCESSFULL_REQUEST_NEW_PASSWORD;
        }
        else
        {
          return LoginResult.LOGIN_SUCCESSFULL;
        }
      }
      else
      {
        return LoginResult.WRONG_PASSWORD;
      }
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return LoginResult.USER_NOT_FOUND;
  }

  /**
   * Ordnet dem Nutzer eine SessionID zu.
   *
   * @param sessionID SessionID
   * @param userID NutzerID
   */
  void addSessionIDToUser( final String sessionID, final Integer userID )
  {
    lock.lock();
    final String sql2 = "INSERT INTO session_ID(user,sessionID,last_login) VALUES (?,?,?)";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql2 ) )
    {
      pstmt.setInt( 1, userID );
      pstmt.setString( 2, sessionID );
      pstmt.setString( 3, LocalDateTime.now().toString() );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Gibt den Bneutzername für eine SessioID zurück.
   *
   * @param sessionID SessionID
   * @return Username for the give SessionID
   */
  String getUsernameForSessionID( final String sessionID )
  {
    int userID = -1;
    final String sql = "SELECT user FROM session_id WHERE sessionID = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, sessionID );
      final ResultSet rs = pstmt.executeQuery();
      userID = rs.getInt( "user" );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    updateLastLogin( sessionID );
    final String sql2 = "SELECT username FROM user WHERE ID = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql2 ) )
    {
      pstmt.setInt( 1, userID );
      final ResultSet rs = pstmt.executeQuery();
      return rs.getString( "username" );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return null;
  }

  /**
   * Setzt das LastLogin für die SessionID.
   *
   * @param sessionID
   */
  private void updateLastLogin( final String sessionID )
  {
    lock.lock();
    final String sql = "UPDATE session_id SET last_login=? WHERE sessionID=?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, LocalDateTime.now().toString() );
      pstmt.setString( 2, sessionID );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Alle SessionIDs, die älter als 30 Tage sind werden gelöscht.
   */
  private void cleanSessionIDTable()
  {
    final ArrayList<String> delList = new ArrayList<>();
    Date thirtyDaysAgo = null;
    try
    {
      thirtyDaysAgo = new SimpleDateFormat( "yyyy-MM-dd" ).parse( LocalDateTime.now().minusDays( 30 ).toString() );
    }
    catch ( final ParseException exception1 )
    {
      // TODO(nwe|02.01.2020): Fehlerbehandlung muss noch implementiert werden!
    }
    final String sql = "SELECT last_login FROM session_id";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        final String dateString = rs.getString( "last_login" );
        final Date date = new SimpleDateFormat( "yyyy-MM-dd" ).parse( dateString );
        if ( thirtyDaysAgo.toInstant().isAfter( date.toInstant() ) )
        {
          delList.add( dateString );
        }
      }
    }
    catch ( final Exception exception )
    {
      ServerLog.newLog( logType.SQL, exception.getMessage() );
    }
    for ( final String date : delList )
    {
      final String delStatement = "DELETE FROM session_id WHERE last_login=?";
      try ( PreparedStatement pstmt = conn.prepareStatement( delStatement ) )
      {
        pstmt.setString( 1, date );
        pstmt.executeUpdate();
      }
      catch ( final SQLException e )
      {
        ServerLog.newLog( logType.SQL, e.getMessage() );
      }
    }
  }

  /**
   * Gibt den Getränkepreis zurück.
   *
   * @param consumedDrink Name des gakuften Getränks
   * @return den Preis für das gewählte Getränk
   */
  Float getDrinkPrice( final String consumedDrink )
  {
    final String sql = "SELECT preis FROM drink WHERE name = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, consumedDrink );
      final ResultSet rs = pstmt.executeQuery();
      return rs.getFloat( "preis" );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return null;
  }


  /**
   * @param currentUser derzeitiger Benutzer
   * @return Jede Kontoaufladung oder Getränkekauf.
   */
  String[][] getHistory( final String currentUser )
  {
    final ArrayList<String[]> history = new ArrayList<>();
    final String sql = "SELECT action,consumer,transaction_price,balance,date,undo FROM historie_log";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        final String balance = String.format( "%.2f€", rs.getFloat( "balance" ) );
        final String consumer = rs.getString( "consumer" );
        if ( currentUser != null )
        {
          if ( consumer.equals( currentUser ) || currentUser.equals( "admin" ) )
          {
            final String[] log = { rs.getString( "action" ), consumer,
                NumberFormat.getCurrencyInstance( new Locale( "de", "DE" ) ).format( rs.getFloat( "transaction_price" ) ).toString()
                    .replace( " ", "" ),
                balance, rs.getString( "date" ), String.valueOf( rs.getBoolean( "undo" ) ), getDisplayName( consumer ) };
            history.add( log );
          }
        }
      }
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    final String[][] historyAsArray = history.toArray( new String[history.size()][] );
    return historyAsArray;
  }

  /**
   * @return die letzten fünf Einträge der Historie werden ausgegeben.
   */
  public String[][] getLast5HistoryEntries()
  {
    final ArrayList<String[]> history = new ArrayList<>();
    final String sql = "SELECT action,consumer,date,undo FROM historie_log ORDER BY date DESC LIMIT 5";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        if ( !rs.getBoolean( "undo" ) )
        {
          final String[] log = { rs.getString( "action" ), getDisplayName( rs.getString( "consumer" ) ), rs.getString( "date" ) };
          history.add( log );
        }
      }

    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    final String[][] historyAsArray = history.toArray( new String[history.size()][] );
    return historyAsArray;
  }

  /**
   * @return die Daten für das Scoreboard
   */
  public String[][] getScoreboard()
  {
    final ArrayList<String[]> history = new ArrayList<>();
    final String sql = "SELECT action,consumer,undo,date FROM historie_log";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        if ( rs.getString( "action" ).contains( "getrunken" ) )
        {
          if ( !rs.getBoolean( "undo" ) )
          {
            final String[] log = { rs.getString( "action" ), getDisplayName( rs.getString( "consumer" ) ), rs.getString( "date" ) };
            history.add( log );
          }
        }
      }
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    final String[][] historyAsArray = history.toArray( new String[history.size()][] );
    return historyAsArray;
  }

  /**
   * Erstellt einen neuen History-Log Eintrag.
   *
   * @param action Guthaben aufgeladen / Getränk gekauft
   * @param username Nutzername
   * @param transaction Transaktionsmenge
   * @param newBalance neuer Kontostand
   * @param date Datum
   */
  void addLog( final String action, final String username, final Float transaction, final Float newBalance, final String date )
  {
    lock.lock();
    final String sql = "INSERT INTO historie_log(action,consumer,transaction_price,balance,date) VALUES(?,?,?,?,?)";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, action );
      pstmt.setString( 2, username );
      pstmt.setFloat( 3, transaction );
      pstmt.setFloat( 4, newBalance );
      pstmt.setString( 5, date );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Entfernt einen Log wenn die Aktion rückgängig gemacht wurde
   *
   * @param date Datum des Ereignisses
   */
  void disableLog( final String date )
  {
    lock.lock();
    final String sql = "UPDATE historie_log SET undo=? WHERE date=?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setBoolean( 1, true );
      pstmt.setString( 2, date );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Diese Methode setzt das Guthaben des Spaarschweins
   *
   * @param userBalance Guthaben des Spaarschweins
   */
  public void setPiggyBankBalance( final Float userBalance )
  {
    lock.lock();
    final String sql = "UPDATE piggy_bank SET guthaben=?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setFloat( 1, userBalance );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * @return Guthaben des Spaarschweins
   */
  public Float getPiggyBankBalance()
  {
    final String sql = "SELECT guthaben FROM piggy_bank";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      return rs.getFloat( "guthaben" );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return null;
  }

  /**
   * @param name Name des Getränks.
   * @return Anzahl des Getränks
   */
  int getDrinkAmount( final String name )
  {
    final String sql = "SELECT amount FROM drink WHERE name =?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, name );
      final ResultSet rs = pstmt.executeQuery();
      return rs.getInt( "amount" );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return -1;
  }

  /**
   * Wenn ein Getränk gekauft wird, wird hier die Anzahl um 1 verringert.
   *
   * @param name Name des Getränks
   */
  void decreaseAmountOfDrinks( final String name )
  {
    lock.lock();
    final String sql = "UPDATE drink SET amount = amount -1 WHERE name = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, name );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Wenn ein Getränkekauf rückgängig gmeacht wird, wird hier die Anzahl um 1 erhöht.
   *
   * @param name Name des Getränks
   */
  void increaseAmountOfDrinks( final String name )
  {
    lock.lock();
    final String sql = "UPDATE drink SET amount = amount +1 WHERE name = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, name );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Setz die Anzahl der Getränke.
   *
   * @param name Name des Getränks
   * @param amount Anzahl des Getränks
   */
  void setAmountOfDrinks( final String name, final int amount )
  {
    lock.lock();
    final String sql = "UPDATE drink SET amount = ? WHERE name = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setInt( 1, amount );
      pstmt.setString( 2, name );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }


  /**
   * Die Methode legt einen neuen Eintrag im IngredientsTable an.
   *
   * @param DrinkID ID des Getränks
   * @param ingredients Inhaltsstoffe
   * @param energy_kJ kJ
   * @param energy_kcal kcal
   * @param fat Fett
   * @param fattyAcids gesätigte Fettsäuren
   * @param carbs Kohlenhydrate
   * @param sugar Zucker
   * @param protein Eiweiß
   * @param salt Salz
   */
  void addIngredients( final int DrinkID, final String ingredients, final int energy_kJ, final int energy_kcal, final double fat,
                       final double fattyAcids,
                       final double carbs, final double sugar, final double protein, final double salt, final double amount )
  {
    String sql;
    final boolean hasDrinkIngredients = getIngredients( DrinkID ) == null ? false : true;
    //If the drink already has ingredients, update them.
    if ( hasDrinkIngredients )
    {
      sql = "UPDATE ingredients "
          + "SET ingredients = ?,"
          + "energy_kJ = ?,"
          + "energy_kcal = ?,"
          + "fat = ?,"
          + "fatty_acids = ?,"
          + "carbs = ?,"
          + "sugar = ?,"
          + "protein = ?,"
          + "salt = ?,"
          + "amount = ? "
          + "WHERE drink = ?";
    }
    //If not, create them
    else
    {
      sql =
          "INSERT INTO ingredients(drink,ingredients,energy_kJ,energy_kcal,fat,fatty_acids,carbs,sugar,protein,salt,amount) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    }
    lock.lock();
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      if ( hasDrinkIngredients )
      {
        pstmt.setString( 1, ingredients );
        pstmt.setInt( 2, energy_kJ );
        pstmt.setInt( 3, energy_kcal );
        pstmt.setDouble( 4, fat );
        pstmt.setDouble( 5, fattyAcids );
        pstmt.setDouble( 6, carbs );
        pstmt.setDouble( 7, sugar );
        pstmt.setDouble( 8, protein );
        pstmt.setDouble( 9, salt );
        pstmt.setDouble( 10, amount );
        pstmt.setInt( 11, DrinkID );
      }
      else
      {
        pstmt.setInt( 1, DrinkID );
        pstmt.setString( 2, ingredients );
        pstmt.setInt( 3, energy_kJ );
        pstmt.setInt( 4, energy_kcal );
        pstmt.setDouble( 5, fat );
        pstmt.setDouble( 6, fattyAcids );
        pstmt.setDouble( 7, carbs );
        pstmt.setDouble( 8, sugar );
        pstmt.setDouble( 9, protein );
        pstmt.setDouble( 10, salt );
        pstmt.setDouble( 11, amount );
      }
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }

    final String enableIngredients = "UPDATE drink SET ingredients = ? WHERE ID = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( enableIngredients ) )
    {
      pstmt.setBoolean( 1, true );
      pstmt.setInt( 2, DrinkID );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }

  }

  /**
   * @param drinkID ID des Getränks
   * @return Inhaltsstoffe etc. des Getränks
   */
  private DrinkIngredients getIngredients( final int drinkID )
  {
    final String sql = "SELECT * FROM ingredients WHERE drink = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setInt( 1, drinkID );
      final ResultSet rs = pstmt.executeQuery();
      return new DrinkIngredients( rs.getInt( "drink" ), rs.getString( "ingredients" ), rs.getInt( "energy_kJ" ),
          rs.getInt( "energy_kcal" ),
          rs.getDouble( "fat" ), rs.getDouble( "fatty_acids" ), rs.getDouble( "carbs" ), rs.getDouble( "sugar" ), rs.getDouble( "protein" ),
          rs.getDouble( "salt" ), rs.getDouble( "amount" ) );
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    return null;
  }

  /**
   * @return alle Nutzernamen
   */
  public String[] getUser()
  {
    final ArrayList<String> user = new ArrayList<>();
    final String sql = "SELECT username FROM user";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        user.add( rs.getString( "username" ) );
      }
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    final String[] userAsArray = user.toArray( new String[user.size()] );
    return userAsArray;
  }

  /**
   * @return alle DisplayNames
   */
  public String[] getDisplayNames()
  {
    final ArrayList<String> names = new ArrayList<>();
    final String sql = "SELECT DisplayName FROM user";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        names.add( rs.getString( "DisplayName" ) );
      }
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    final String[] userAsArray = names.toArray( new String[names.size()] );
    return userAsArray;
  }

  /**
   * @return alle user
   */
  public User[] getFullUser()
  {
    final ArrayList<User> user = new ArrayList<>();
    final String sql = "SELECT * FROM user";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        user.add( new User( rs.getString( "username" ), rs.getString( "password" ), rs.getFloat( "guthaben" ), rs.getInt( "ID" ) ) );
      }
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    final User[] userAsArray = user.toArray( new User[user.size()] );
    return userAsArray;
  }

  /**
   * @param name Nutzername
   * @param password Passwort
   */
  void changePassword( final String name, final String password, final boolean requestNewPass )
  {
    lock.lock();
    final String sql = "UPDATE user SET password = ?,requestNewPassword=? WHERE username = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, password );
      if ( requestNewPass )
      {
        pstmt.setBoolean( 2, true );
      }
      else
      {
        pstmt.setBoolean( 2, false );
      }
      pstmt.setString( 3, name );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  void changeDisplayName( final Integer userID, final String newDisplayName )
  {
    lock.lock();
    final String sql = "UPDATE user SET DisplayName = ? WHERE ID = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, newDisplayName );
      pstmt.setInt( 2, userID );
      pstmt.executeUpdate();
    }
    catch ( final SQLException e )
    {
      ServerLog.newLog( logType.SQL, e.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }
}
