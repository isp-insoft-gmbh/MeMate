package com.isp.memate;

import java.awt.Cursor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;

public class Cache
{
  private static final Cache                  instance            = new Cache();
  public Object                               usernameSync        = new Object();
  private String                              serverVersion       = null;
  private String                              clientVersion       = null;
  private String                              username            = null;
  private String[]                            userArray           = null;
  private String[]                            displayNamesArray   = null;
  private User[]                              fullUserArray       = null;
  private String[][]                          history;
  private String[][]                          shortHistory        = null;
  private String                              displayname         = null;
  private Drink[]                             drinkArray          = null;
  private String[][]                          scoreboard;
  private Float                               piggyBankBalance;
  private final List<String>                  drinkNames          = new ArrayList<>();
  private final Map<String, Float>            priceMap            = new HashMap<>();
  private final Map<String, ImageIcon>        imageMap            = new HashMap<>();
  private final ArrayList<Byte>               byteImageList       = new ArrayList<>();
  private final Map<String, Integer>          amountMap           = new HashMap<>();
  private final Map<String, Integer>          drinkIDMap          = new HashMap<>();
  private final Map<String, Boolean>          drinkIngredientsMap = new HashMap<>();
  private final Map<String, DrinkIngredients> IngredientsMap      = new HashMap<>();

  private Cache()
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

  /**
   * Die Preismap, Bildermap und eine Liste von allen Namen der Getränke werden
   * gefüllt. Diese Maps werden unter anderem von dem Dashboard oder dem
   * Drinkmanager genutzt. Die Methode wird beim Start des Programms aufgerufen
   * oder wenn Veränderungen an Getränken vorgenommen werden.
   */
  void updateMaps()
  {
    try
    {
      final List<String> oldDrinkNames = new ArrayList<>( drinkNames );
      final Map<String, Float> oldPriceMap = new HashMap<>();
      oldPriceMap.putAll( priceMap );
      final Map<String, Integer> oldAmountMap = new HashMap<>();
      oldAmountMap.putAll( amountMap );
      final ArrayList<Byte> oldByteImageList = new ArrayList<>( byteImageList );

      priceMap.clear();
      amountMap.clear();
      imageMap.clear();
      drinkIDMap.clear();
      drinkNames.clear();
      drinkIngredientsMap.clear();
      IngredientsMap.clear();
      byteImageList.clear();
      for ( final Drink drink : drinkArray )
      {
        final String name = drink.name;
        final Float price = drink.price;
        final int amount = drink.amount;
        final byte[] pictureInBytes = drink.pictureInBytes;
        final Integer id = drink.id;
        final ImageIcon icon = new ImageIcon( pictureInBytes );
        priceMap.put( name, price );
        imageMap.put( name, icon );
        amountMap.put( name, amount );
        // FIXME Das muss besser werden
        // Der 355. byte des Bildes wird in eine Liste hinzugefügt, welche anschließend
        // mit der vorherigen Liste verglichen wird.
        byteImageList.add( pictureInBytes[ 355 ] );
        drinkIDMap.put( name, id );
        drinkNames.add( name );
        drinkIngredientsMap.put( name, drink.ingredients );
        IngredientsMap.put( name, drink.drinkIngredients );
      }
      if ( !drinkNames.equals( oldDrinkNames ) || !priceMap.equals( oldPriceMap )
          || !byteImageList.equals( oldByteImageList ) || !amountMap.equals( oldAmountMap ) )
      {
        GUIObjects.dashboard.updateButtonpanel();
      }
      GUIObjects.mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( exception.getMessage() );
    }
  }

  public void setShortHistory( String[][] shortHistory )
  {
    this.shortHistory = shortHistory;
  }

  public String[][] getScoreboard()
  {
    return scoreboard;
  }

  public void setScoreboard( String[][] scoreboard )
  {
    this.scoreboard = scoreboard;
  }

  public String getDisplayname()
  {
    return displayname == null ? username : displayname;
  }

  public void setDisplayname( String displayname )
  {
    this.displayname = displayname;
  }

  public Drink[] getDrinkArray()
  {
    return drinkArray;
  }

  public void setDrinkArray( Drink[] drinkArray )
  {
    this.drinkArray = drinkArray;
  }

  ImageIcon getIcon( final String name )
  {
    return imageMap.get( name );
  }

  Float getPrice( final String name )
  {
    return priceMap.get( name );
  }

  Integer getAmount( final String name )
  {
    return amountMap.get( name );
  }

  Integer getID( final String name )
  {
    return drinkIDMap.get( name );
  }

  Boolean hasIngredients( final String name )
  {
    return drinkIngredientsMap.get( name );
  }

  DrinkIngredients getIngredients( final String name )
  {
    return IngredientsMap.get( name );
  }

  String getDrinkName( final int id )
  {
    for ( final String string : drinkIDMap.keySet() )
    {
      if ( id == drinkIDMap.get( string ) )
      {
        return string;
      }
    }
    return null;
  }

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
}
