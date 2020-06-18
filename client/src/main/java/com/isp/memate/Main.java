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
  static String version = "unknown";

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
    UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
    UIManager.put( "Label.disabledShadow", new Color( 0, 0, 0 ) );
    UIManager.put( "DefaultBrightColor", Color.white );
    ToolTipManager.sharedInstance().setDismissDelay( 1000000 );

    setVersion();
    installColors();
    installColorKeys();

    String sessionID = null;
    String darkmode = null;
    final File meMateFolder = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" );
    final File userPropFile = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
    final File clientLogFile = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "ClientLog.log" );

    createPropFile( meMateFolder, userPropFile, clientLogFile );
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
    {
      final Properties userProperties = new Properties();
      userProperties.load( input );
      sessionID = userProperties.getProperty( "SessionID" );
      darkmode = userProperties.getProperty( "Darkmode" );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Die userconfig-Properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    if ( darkmode != null && darkmode.equals( "on" ) )
    {
      MeMateUIManager.iniDarkMode();
    }
    else
    {
      MeMateUIManager.iniDayMode();
    }
    if ( sessionID == null || sessionID.equals( "null" ) )
    {
      ServerCommunication.getInstance().tellServertoSendVersionNumber();
      final Login login = Login.getInstance();
      MeMateUIManager.setUISettings();
      login.setVisible( true );
      ServerCommunication.getInstance().checkVersion( version );
    }
    else
    {
      ServerCommunication.getInstance().checkLoginForSessionID( sessionID );
      ServerCommunication.getInstance().tellServertoSendVersionNumber();
      final Mainframe mainframe = Mainframe.getInstance();
      if ( ServerCommunication.getInstance().currentUser == null )
      {
        ClientLog.newLog( "Es wurde kein Nutzer für die angegeben Session gefunden." );
        final Login login = Login.getInstance();
        MeMateUIManager.setUISettings();
        login.setVisible( true );
        ServerCommunication.getInstance().checkVersion( version );
      }
      else
      {
        mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        mainframe.setVisible( true );
        mainframe.addActionBar();
        ServerCommunication.getInstance().startDrinkInfoTimer();
        ServerCommunication.getInstance().tellServerToSendHistoryData();
        ServerCommunication.getInstance().checkVersion( version );
        mainframe.requestFocus();
        MeMateUIManager.setUISettings();
      }
    }
  }

  private static void setVersion()
  {
    try ( InputStream input = Main.class.getClassLoader().getResourceAsStream( "version.properties" ) )
    {
      final Properties versionProperties = new Properties();
      versionProperties.load( input );
      version = versionProperties.getProperty( "build_version" );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Die version.properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    System.out.println( version );
  }

  /**
   * Die Userconfig wird gelesen und das richtige Colortheme geladen.
   */
  private static void installColors()
  {
    String color = "null";
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
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
   * Erstellt den MaMate Ordner unter AppData und die Userconfig File.
   *
   * @param meMateFolder
   * @param userPropFile
   */
  private static void createPropFile( final File meMateFolder, final File userPropFile, final File log )
  {
    try
    {
      meMateFolder.mkdir();
      userPropFile.createNewFile();
      log.createNewFile();
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