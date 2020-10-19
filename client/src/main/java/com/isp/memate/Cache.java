package com.isp.memate;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.ClientLog;

public class Cache
{
  private static final Cache instance          = new Cache();
  Object                     usernameSync      = new Object();
  private String             serverVersion     = null;
  private String             clientVersion     = null;
  private String             username          = null;
  private String[]           userArray         = null;
  private String[]           displayNamesArray = null;
  private User[]             fullUserArray     = null;
  private String[][]         history;
  private String[][]         shortHistory      = null;
  private Float              piggyBankBalance;

  public Cache()
  {
    loadClientVersion();
  }

  private void loadClientVersion()
  {
    try ( InputStream input = Cache.class.getClassLoader().getResourceAsStream( "version.properties" ) )
    {
      final Properties versionProperties = new Properties();
      versionProperties.load( input );
      this.clientVersion = versionProperties.getProperty( "build_version" );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Die version.properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    System.out.println( "Version des Clients: " + this.clientVersion );
  }


  public static Cache getInstance()
  {
    return instance;
  }

  public String getServerVersion()
  {
    return serverVersion;
  }

  public void setServerVersion( String serverVersion )
  {
    this.serverVersion = serverVersion;
  }

  public String getClientVersion()
  {
    return clientVersion;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername( String username )
  {
    this.username = username;
    synchronized ( usernameSync )
    {
      usernameSync.notify();
    }
  }

  public String[] getUserArray()
  {
    return userArray;
  }

  public void setUserArray( String[] userArray )
  {
    this.userArray = userArray;
  }

  public String[] getDisplayNamesArray()
  {
    return displayNamesArray;
  }

  public void setDisplayNamesArray( String[] displayNamesArray )
  {
    this.displayNamesArray = displayNamesArray;
  }

  public User[] getFullUserArray()
  {
    return fullUserArray;
  }

  public void setFullUserArray( User[] fullUserArray )
  {
    this.fullUserArray = fullUserArray;
  }

  public Float getPiggyBankBalance()
  {
    return piggyBankBalance;
  }

  public void setPiggyBankBalance( Float piggyBankBalance )
  {
    this.piggyBankBalance = piggyBankBalance;
  }

  /**
   * Gibt die Historydaten zurück.
   *
   * @param dateType gibt an in welchem Format das Datum zurück gegeben werden soll
   *
   * @return Die Historydaten als 2D Array
   */
  String[][] getHistory( final dateType dateType )
  {
    if ( history == null )
    {
      return history;
    }
    final String[][] historyArray = new String[history.length][];
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
        historyArray[ i ][ 1 ] = historyArray[ i ][ 6 ];
      }
    }
    return historyArray;
  }

  public void setHistory( String[][] history )
  {
    final List<String[]> list = Arrays.asList( history );
    Collections.reverse( list );
    this.history = list.toArray( history ).clone();
  }

  public String[][] getShortHistory()
  {
    if ( shortHistory == null )
    {
      return shortHistory;
    }
    else
    {
      final String[][] historyArray = new String[shortHistory.length][];
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

  public void setShortHistory( String[][] shortHistory )
  {
    this.shortHistory = shortHistory;
  }
}
