package com.isp.memate;

import java.awt.Color;
import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.Compare;
import com.isp.memate.util.MeMateUIManager;

/**
 * Die Mainklasse setzt das Look and Feel und öffnet den LoginFrame, wenn es keine SessionID gibt oder sie
 * abgelaufen ist.
 *
 * @author nwe
 * @since 15.10.2019
 */
class Main
{
  private static final String MAIN_FOLDER        = System.getenv( "APPDATA" ) + File.separator + "MeMate";
  static String               sessionIDPropertry = null;
  static String               darkmodeProperty   = null;

  /**
   * @param args unused
   *
   * @throws UnsupportedLookAndFeelException bei fehlerhafter Look&Feel Initialisierung
   * @throws IllegalAccessException bei fehlerhafter Look&Feel Initialisierung
   * @throws InstantiationException bei fehlerhafter Look&Feel Initialisie+rung
   * @throws ClassNotFoundException bei fehlerhafter Look&Feel Initialisierung
   */
  public static void main( final String[] args )
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
  {

    setLFandUIDefaults();
    installColors();
    installColorKeys();
    ServerCommunication serverCommunication = ServerCommunication.getInstance();
    Cache cache = Cache.getInstance();

    createPropFile();
    loadUserProperties();
    applyTheme();

    if ( sessionIDPropertry == null || sessionIDPropertry.equals( "null" ) )
    {
      showLogin();
    }
    else
    {
      serverCommunication.checkLoginForSessionID( sessionIDPropertry );

      //This synchronized Thread will take care of blocking further executions 
      //until the client has recieved the username.
      synchronized ( cache.usernameSync )
      {
        try
        {
          cache.usernameSync.wait();
        }
        catch ( InterruptedException e )
        {
          // Happens if someone interrupts this thread.
        }
      }

      if ( cache.getUsername() == null )
      {
        ClientLog.newLog( "Es wurde kein Nutzer für die angegeben Session gefunden." );
        showLogin();
      }
      else
      {
        final Mainframe mainframe = Mainframe.getInstance();
        mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        mainframe.setVisible( true );
        mainframe.addActionBar();
        serverCommunication.startDrinkInfoTimer();
        serverCommunication.tellServerToSendHistoryData();
        Compare.checkVersion();
        mainframe.requestFocus();
        MeMateUIManager.setUISettings();
      }
    }
  }

  private static void showLogin()
  {
    final Login login = Login.getInstance();
    MeMateUIManager.setUISettings();
    login.setVisible( true );
    Compare.checkVersion();
  }

  private static void applyTheme()
  {
    if ( darkmodeProperty != null && darkmodeProperty.equals( "on" ) )
    {
      MeMateUIManager.iniDarkMode();
    }
    else
    {
      MeMateUIManager.iniDayMode();
    }
  }

  private static void loadUserProperties()
  {
    try ( InputStream input =
        new FileInputStream( MAIN_FOLDER + File.separator + "userconfig.properties" ) )
    {
      final Properties userProperties = new Properties();
      userProperties.load( input );
      sessionIDPropertry = userProperties.getProperty( "SessionID" );
      darkmodeProperty = userProperties.getProperty( "Darkmode" );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Die userconfig-Properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
  }

  private static void setLFandUIDefaults()
      throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
  {
    UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
    UIManager.put( "Label.disabledShadow", new Color( 0, 0, 0 ) );
    UIManager.put( "DefaultBrightColor", Color.white );
    ToolTipManager.sharedInstance().setDismissDelay( 1000000 );
  }

  /**
   * Die Userconfig wird gelesen und das richtige Colortheme geladen.
   */
  private static void installColors()
  {
    String color = "null";
    try ( InputStream input =
        new FileInputStream( MAIN_FOLDER + File.separator + "userconfig.properties" ) )
    {
      final Properties userProperties = new Properties();
      userProperties.load( input );
      color = userProperties.getProperty( "colorScheme" );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Die userconfig-Properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    if ( color == null )
    {
      color = "";
    }
    switch ( color )
    {
      case "Dark Blue":
        putColorsInUIManager( new Color( 0, 173, 181 ), new Color( 34, 40, 49 ), new Color( 57, 62, 70 ), new Color( 42, 51, 64 ) );
        break;
      case "Red":
        putColorsInUIManager( new Color( 226, 62, 87 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
        break;
      case "Green":
        putColorsInUIManager( new Color( 153, 180, 51 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
        break;
      case "Blue":
        putColorsInUIManager( new Color( 85, 172, 238 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Orange":
        putColorsInUIManager( new Color( 227, 162, 26 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Coral":
        putColorsInUIManager( new Color( 255, 111, 97 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Tripple Green":
        putColorsInUIManager( new Color( 153, 180, 51 ), new Color( 11, 40, 25 ), new Color( 30, 113, 69 ), new Color( 13, 48, 30 ) );
        break;
      default :
        putColorsInUIManager( new Color( 29, 164, 165 ), new Color( 36, 43, 55 ), new Color( 52, 73, 94 ), new Color( 42, 51, 64 ) );
        break;
    }
  }

  private static void putColorsInUIManager( final Color appColor, final Color background, final Color background2, final Color actionbar )
  {
    UIManager.put( "AppColor", appColor );
    UIManager.put( "App.Background", background );
    UIManager.put( "App.Secondary.Background", background2 );
    UIManager.put( "App.Actionbar", actionbar );
  }

  /**
   * Erstellt den MeMate Ordner unter AppData und die Userconfig File.
   */
  private static void createPropFile()
  {
    final File meMateFolder = new File( MAIN_FOLDER );
    final File userPropFile = new File( MAIN_FOLDER + File.separator + "userconfig.properties" );
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

  /**
   * setzt alle wichtigen Farben die das Programm benutzt.
   */
  private static void installColorKeys()
  {
    MeMateUIManager.installDefaults();
  }
}