/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.isp.memate.Shared.LoginResult;
import com.isp.memate.Shared.Operation;

/**
 * Stellt die Verbindung zwischen Server und der Datenbank her.
 * 
 * @author nwe
 * @since 24.10.2019
 */
public class Database
{
  private Connection conn = null;

  /**
   * @return path for all configuration / data; changes depending on OS
   */
  public static Path getTargetFolder()
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
   * @param dataBasePath Startparameter Pfad der Datenbank
   */
  public Database( String dataBasePath )
  {
    try
    {
      Files.createDirectories( getTargetFolder() );
    }
    catch ( IOException exception )
    {
      System.out.println( "Der Ordern für die Datenbank konnte nicht erstellt werden." + exception.getMessage() );
    }
    try
    {
      conn = DriverManager.getConnection( "jdbc:sqlite:" + dataBasePath );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    addUserTable();
    addDrinkTable();
    addHistoryTable();
    addPiggyBankTable();
  }


  /**
   * Erstellt den Drink-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addDrinkTable()
  {
    String sql = "CREATE TABLE IF NOT EXISTS drink ("
        + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "name string NOT NULL UNIQUE,"
        + "preis double NOT NULL CHECK (Preis != 0),"
        + "picture blob NOT NULL,"
        + "amount integer NOT NULL DEFAULT 0"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }


  /**
   * Erstellt den History-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addHistoryTable()
  {
    String sql = "CREATE TABLE IF NOT EXISTS historie_log ("
        + "action string NOT NULL,"
        + "consumer REFERENCES user(username),"
        + "transaction_price double NOT NULL,"
        + "balance double NOT NULL,"
        + "date string NOT NULL"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }


  /**
   * Erstellt den User-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addUserTable()
  {
    String sql = "CREATE TABLE IF NOT EXISTS user ("
        + "ID INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "guthaben double NOT NULL,"
        + "username string UNIQUE NOT NULL,"
        + "password string NOT NULL,"
        + "sessionID string"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    registerNewUser( "admin", "8C6976E5B541415BDE98BD4DEE15DFB167A9C873FC4BB8A81F6F2AB448A918" );
  }

  /**
   * Erstellt den PiggyBank-Table in der Datenbank, falls dieser noch nicht existiert.
   */
  private void addPiggyBankTable()
  {
    String sql = "CREATE TABLE IF NOT EXISTS piggy_bank ("
        + "guthaben double NOT NULL DEFAULT (0.0)"
        + ");";
    try ( Statement stmt = conn.createStatement() )
    {
      stmt.execute( sql );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    if ( getPiggyBankBalance() == null )
    {
      String sql2 = "INSERT INTO piggy_bank(guthaben) VALUES(?)";
      try ( PreparedStatement pstmt = conn.prepareStatement( sql2 ) )
      {
        pstmt.setFloat( 1, 0f );
        pstmt.executeUpdate();
      }
      catch ( SQLException e )
      {
        System.out.println( e.getMessage() );
      }
    }
  }

  /**
   * Der Guthaben-wert aus dem user-table,welcher zu der gegebenen User-ID gehört wird zurück gegeben.
   * 
   * @param id ID des Nutzers
   * @return Kontostand des Nutzers
   */
  public Float getBalance( Integer id )
  {
    Float balance = 0f;
    String sql = "SELECT guthaben FROM user WHERE ID= ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ); )
    {
      pstmt.setInt( 1, id );
      ResultSet rs = pstmt.executeQuery();
      balance = rs.getFloat( "guthaben" );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    return balance;
  }


  /**
   * Füllt eine Map mit allen Usern und zugehörigen IDs.
   * 
   * @return die Map
   */
  public Map<String, Integer> getUserIDMap()
  {
    Map<String, Integer> userMap = new HashMap<>();
    String sql = "SELECT ID,username FROM user";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        userMap.put( rs.getString( "username" ), rs.getInt( "ID" ) );
      }
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
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
  public String registerNewUser( String username, String password )
  {
    String sql = "INSERT INTO user(guthaben,username,password) VALUES(?,?,?)";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setFloat( 1, 0f );
      pstmt.setString( 2, username );
      pstmt.setString( 3, password );
      pstmt.executeUpdate();
    }
    catch ( SQLException e )
    {
      return "Benutzername bereits vergeben.";
    }
    return "Registrierung erfolgreich.";
  }

  /**
   * Überschreibt den guthaben-wert des gegebenen Nutzers in dem user-table.
   * 
   * @param sessionID ID des Nutzers.
   * @param updatedBalance neuer Kontostand
   */
  public void updateBalance( String sessionID, Float updatedBalance )
  {
    String sql = "UPDATE user SET guthaben=? WHERE sessionID=?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setFloat( 1, updatedBalance );
      pstmt.setString( 2, sessionID );
      pstmt.executeUpdate();
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }


  /**
   * Überschreibt eine bestimmte Information des Getränks,
   * abhängig von der Operaion.
   * 
   * @param id ID des Getränks
   * @param operation Opearion
   * @param updatedInformation die neue Information, kann Name, Preis oder Bild sein.
   */
  public void updateDrinkInformation( Integer id, Operation operation, Object updatedInformation )
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
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }


  /**
   * Liest alle Getränke aus der Datenbank und
   * erstellt ein Array aus {@linkplain Drink}-Objekten.
   * 
   * @return das Drink-Objekt-Array
   */
  public Drink[] getDrinkInformations()
  {
    ArrayList<Drink> drinkInfos = new ArrayList<>();

    String sql = "SELECT ID,name,preis,picture,amount FROM drink";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        drinkInfos
            .add( new Drink( rs.getString( "name" ), rs.getFloat( "preis" ), null, rs.getInt( "ID" ),
                Arrays.toString( rs.getBytes( "picture" ) ), rs.getInt( "amount" ) ) );
      }
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
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
  public void registerNewDrink( String name, Float price, String picture )
  {
    String sql = "INSERT INTO drink(name,preis,picture) VALUES(?,?,?)";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, name );
      pstmt.setFloat( 2, price );
      String[] byteValues = picture.substring( 1, picture.length() - 1 ).split( "," );
      byte[] bytes = new byte[byteValues.length];
      for ( int i = 0, len = bytes.length; i < len; i++ )
      {
        bytes[ i ] = Byte.parseByte( byteValues[ i ].trim() );
      }
      pstmt.setBytes( 3, bytes );
      pstmt.executeUpdate();
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }

  /**
   * Löscht den Datenbankeintrag für das angegebene Getränk.
   * 
   * @param id ID des Getränks
   */
  public void removeDrink( Integer id )
  {
    String sql = "DELETE FROM drink WHERE ID= ?";

    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setInt( 1, id );
      pstmt.executeUpdate();

    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }


  /**
   * Überprüft die angegebenen Login-Informationen, sollten diese korrekt sein, sow ird Login erfolgreich zurück
   * gegeben.
   * 
   * @param username Nutzername
   * @param password gehashtes Passwort
   * @return ob der Login erfolgreich war oder nicht
   */
  public LoginResult checkLogin( String username, String password )
  {
    String sql = "SELECT password FROM user WHERE username = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, username );
      ResultSet rs = pstmt.executeQuery();
      if ( rs.getString( "password" ).equals( password ) )
      {
        return LoginResult.LOGIN_SUCCESSFULL;
      }
      else
      {
        return LoginResult.WRONG_PASSWORD;
      }
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    return LoginResult.USER_NOT_FOUND;
  }

  /**
   * Ordnet dem Nutzer eine SessionID zu.
   * 
   * @param sessionID SessionID
   * @param userID NutzerID
   */
  public void addSessionIDToUser( String sessionID, Integer userID )
  {
    String sql = "UPDATE user SET sessionid=? WHERE ID=?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, sessionID );
      pstmt.setInt( 2, userID );
      pstmt.executeUpdate();
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }

  /**
   * Gibt den Bneutzername für eine SessioID zurück.
   * 
   * @param sessionID SessionID
   * @return Username for the give SessionID
   */
  public String getUsernameForSessionID( String sessionID )
  {
    String sql = "SELECT username FROM user WHERE sessionID = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, sessionID );
      ResultSet rs = pstmt.executeQuery();
      return rs.getString( "username" );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    return null;
  }

  /**
   * Gibt den Getränkepreis zurück.
   * 
   * @param consumedDrink Name des gakuften Getränks
   * @return den Preis für das gewählte Getränk
   */
  public Float getDrinkPrice( String consumedDrink )
  {
    String sql = "SELECT preis FROM drink WHERE name = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, consumedDrink );
      ResultSet rs = pstmt.executeQuery();
      return rs.getFloat( "preis" );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    return null;
  }


  /**
   * @param currentUser derzeitiger Benutzer
   * @return Jede Kontoaufladung oder Getränkekauf.
   */
  public String[][] getHistory( String currentUser )
  {
    ArrayList<String[]> history = new ArrayList<>();
    String sql = "SELECT action,consumer,transaction_price,balance,date FROM historie_log";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      while ( rs.next() )
      {
        String balance = String.format( "%.2f€", rs.getFloat( "balance" ) );
        String consumer = rs.getString( "consumer" );
        if ( consumer.equals( currentUser ) || currentUser.equals( "admin" ) )
        {
          String[] log = { rs.getString( "action" ), consumer, String.valueOf( rs.getFloat( "transaction_price" ) + "0€" ),
              balance, rs.getString( "date" ) };
          history.add( log );
        }
      }
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    String[][] historyAsArray = history.toArray( new String[history.size()][] );
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
  public void addLog( String action, String username, Float transaction, Float newBalance, String date )
  {
    String sql = "INSERT INTO historie_log(action,consumer,transaction_price,balance,date) VALUES(?,?,?,?,?)";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, action );
      pstmt.setString( 2, username );
      pstmt.setFloat( 3, transaction );
      pstmt.setFloat( 4, newBalance );
      pstmt.setString( 5, date );
      pstmt.executeUpdate();
    }
    catch ( SQLException e )
    {
      e.printStackTrace();
      System.out.println( e.getMessage() );
    }
  }

  /**
   * Diese Methode setzt das Guthaben des Spaarschweins
   * 
   * @param userBalance Guthaben des Spaarschweins
   */
  public void setPiggyBankBalance( Float userBalance )
  {
    String sql = "UPDATE piggy_bank SET guthaben=?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setFloat( 1, userBalance );
      pstmt.executeUpdate();
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }

  /**
   * @return Guthaben des Spaarschweins
   */
  public Float getPiggyBankBalance()
  {
    String sql = "SELECT guthaben FROM piggy_bank";
    try ( Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql ) )
    {
      return rs.getFloat( "guthaben" );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    return null;
  }

  /**
   * @param name Name des Getränks.
   * @return Anzahl des Getränks
   */
  public int getDrinkAmount( String name )
  {
    String sql = "SELECT amount FROM drink WHERE name =?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, name );
      ResultSet rs = pstmt.executeQuery();
      return rs.getInt( "amount" );
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
    return -1;
  }

  /**
   * Wenn ein Getränk gekauft wird, wird hier die Anzahl um 1 verringert.
   * 
   * @param name Name des Getränks
   */
  public void decreaseAmountOfDrinks( String name )
  {
    String sql = "UPDATE drink SET amount = amount -1 WHERE name = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setString( 1, name );
      pstmt.executeUpdate();
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }

  /**
   * Setz die Anzahl der Getränke.
   * 
   * @param name Name des Getränks
   * @param amount Anzahl des Getränks
   */
  public void setAmountOfDrinks( String name, int amount )
  {
    String sql = "UPDATE drink SET amount = ? WHERE name = ?";
    try ( PreparedStatement pstmt = conn.prepareStatement( sql ) )
    {
      pstmt.setInt( 1, amount );
      pstmt.setString( 2, name );
      pstmt.executeUpdate();
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }
}
