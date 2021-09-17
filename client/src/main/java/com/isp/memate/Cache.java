package com.isp.memate;

import java.awt.Cursor;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.panels.Adminview;
import com.isp.memate.panels.Dashboard;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.ObservableValue;
import com.isp.memate.util.ValueListener;

public class Cache
{
  private static final Cache           instance          = new Cache();
  public Object                        sessionIDSync     = new Object();
  private String                       serverVersion     = null;
  private String                       clientVersion     = null;
  private String                       username          = null;
  private String[]                     userArray         = null;
  private String[]                     displayNamesArray = null;
  private User[]                       fullUserArray     = null;
  private String[][]                   history;
  private String[][]                   shortHistory      = null;
  private String                       displayname       = null;
  private Map<String, Integer>         scoreboard;
  private Map<String, Integer>         weeklyScoreboard;
  private final ObservableValue<Float> piggyBankBalance  = new ObservableValue<Float>( 0f );
  private final ObservableValue<Float> userBalance       = new ObservableValue<Float>( 0f );

  private HashMap<Integer, Drink> drinks = new HashMap<>();

  private Cache()
  {
    loadClientVersion();
    initValueListener();
  }

  private void initValueListener()
  {
    piggyBankBalance.addListener( new ValueListener<Float>()
    {
      @Override
      public void valueChanged( Float oldValue, Float newValue )
      {
        if ( GUIObjects.currentPanel != null && GUIObjects.currentPanel instanceof Adminview )
        {
          ((Adminview) GUIObjects.currentPanel).setPiggybankBalance( newValue );
        }
      }
    } );
    userBalance.addListener( new ValueListener<Float>()
    {
      @Override
      public void valueChanged( Float oldValue, Float newValue )
      {
        GUIObjects.mainframe.updateBalanceLabel( newValue );
      }
    } );
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
    synchronized ( sessionIDSync )
    {
      sessionIDSync.notify();
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
    return piggyBankBalance.getValue();
  }

  public void setPiggyBankBalance( Float piggyBankBalance )
  {
    this.piggyBankBalance.setValue( piggyBankBalance );
  }

  /**
   * Gibt die Historydaten zurück.
   *
   * @param dateType gibt an in welchem Format das Datum zurück gegeben werden soll
   *
   * @return Die Historydaten als 2D Array
   */
  public String[][] getHistory( final dateType dateType )
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

  public Map<String, Integer> getScoreboard()
  {
    return scoreboard;
  }

  public void setScoreboard( Map<String, Integer> scoreboard )
  {
    this.scoreboard = scoreboard;
  }

  public Map<String, Integer> getWeeklyScoreboard()
  {
    return weeklyScoreboard;
  }

  public void setWeeklyScoreboard( Map<String, Integer> weeklyScoreboard )
  {
    this.weeklyScoreboard = weeklyScoreboard;
  }

  public String getDisplayname()
  {
    return displayname == null ? username : displayname;
  }

  public void setDisplayname( String displayname )
  {
    this.displayname = displayname;
  }

  public HashMap<Integer, Drink> getDrinks()
  {
    return drinks;
  }

  public void setDrinks( HashMap<Integer, Drink> drinks )
  {
    this.drinks = drinks;
    if ( GUIObjects.currentPanel != null && GUIObjects.currentPanel instanceof Dashboard )
    {
      //TODO(nwe | 08.04.2021): Das muss besser werden. Es soll nicht alles geupdatet werden, nur die geänderte Information
      ((Dashboard) GUIObjects.currentPanel).updateButtonpanel();
      GUIObjects.mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }
  }

  public void setUserBalance( Float userBalance )
  {
    this.userBalance.setValue( userBalance );
  }
}
