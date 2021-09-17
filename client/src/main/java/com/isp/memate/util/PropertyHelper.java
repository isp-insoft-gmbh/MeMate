package com.isp.memate.util;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.isp.memate.Cache;
import com.isp.memate.Config;
import com.isp.memate.ServerCommunication;

public class PropertyHelper
{
  public final static String MAIN_FOLDER  = Config.getConfigDir("MeMate");
  private final static File   userPropFile = new File( MAIN_FOLDER + File.separator + "userconfig.properties" );

  /**
   * Erstellt den MeMate Ordner unter AppData und die Userconfig File.
   */
  public static void createPropFile()
  {
    final File meMateFolder = new File( MAIN_FOLDER );
    final File clientLogFile = new File( MAIN_FOLDER + File.separator + "ClientLog.log" );
    try
    {
      meMateFolder.mkdir();
      userPropFile.createNewFile();
      clientLogFile.createNewFile();
    }
    catch ( final IOException exception1 )
    {
      ClientLog.newLog( "Die userconfig-Properties konnten nicht erstellt werden." );
      ClientLog.newLog( exception1.getMessage() );
    }
  }

  public static String getProperty( String property )
  {
    try ( InputStream input = new FileInputStream( userPropFile ) )
    {
      final Properties userProperties = new Properties();
      userProperties.load( input );
      return userProperties.getProperty( property );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Die userconfig-Properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    return null;
  }

  public static boolean getBooleanProperty( String property )
  {
    final String value = getProperty( property );
    if ( value == null )
    {
      ClientLog.newLog( "Es konnte keine Value für die Property '" + property + "' gefunden werden. Es wird false returned" );
      return false;
    }
    if ( "true".equals( value ) )
    {
      return true;
    }
    if ( "false".equals( value ) )
    {
      return false;
    }
    ClientLog.newLog( "Es wurde eine ungültige Value für die Property '" + property + "' gefunden. Es wird false returned" );
    return false;
  }

  public static void setProperty( final String property, final String value )
  {
    try ( InputStream input = new FileInputStream( userPropFile ) )
    {
      final Properties userProperties = new Properties();
      userProperties.load( input );
      userProperties.setProperty( property, value );
      final OutputStream output = new FileOutputStream( userPropFile );
      userProperties.store( output, "" );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Die userconfig-Properties konnten nicht gespeichert werden" );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  public static Color getAppColorProperty()
  {
    String appColor = PropertyHelper.getProperty( "AppColor" );
    //Default-Value falls die Property nicht gefunden wurde
    if ( appColor == null )
    {
      return Color.CYAN;
    }
    int rgb = Integer.valueOf( appColor );
    return new Color( rgb );
  }

  public static boolean getDarkModeProperty()
  {
    String darkmode = PropertyHelper.getProperty( "Darkmode" );
    if ( darkmode == null )
    {
      return false;
    }
    //Shame on me
    if ( darkmode.equals( "true" ) || darkmode.equals( "on" ) )
    {
      return true;
    }
    return false;
  }

  public static boolean isSessionIDValid()
  {
    Cache cache = Cache.getInstance();

    String sessionID = getProperty( "SessionID" );
    if ( sessionID == null || sessionID.equals( "null" ) )
    {
      return false;
    }
    ServerCommunication.getInstance().checkLoginForSessionID( sessionID );

    //This synchronized Thread will take care of blocking further executions 
    //until the client has received the username.
    synchronized ( cache.sessionIDSync )
    {
      try
      {
        cache.sessionIDSync.wait();
      }
      catch ( InterruptedException e )
      {
        // Happens if someone interrupts this thread.
      }
    }
    if ( cache.getUsername() == null )
    {
      ClientLog.newLog( "Es wurde kein Nutzer für die angegeben Session gefunden." );
      return false;
    }
    return true;
  }
}
