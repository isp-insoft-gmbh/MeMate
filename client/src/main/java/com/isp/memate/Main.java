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
  final static String version = "0.9.8.3";

  /**
   * @param args unused
   */
  public static void main( String[] args )
  {
    try
    {
      /*
       * Color Shemes:
       * 
       * *DEFAULT Blue
       *    - AppColor: 29, 164, 165
       *    - App.background: 36, 43, 55
       *    - App.Secondary.Background: 52, 73, 94
       *    - App.Actionbar: 42, 51, 64
       *     
       * *Darker Default
       *    - AppColor: 0, 173, 181
       *    - App.Background: 34, 40, 49
       *    - App.Secondary.background: 57, 62, 70
       *    - App.Actionbar: 42, 51, 64
       *    
       * *Red/Gray
       *    - AppColor: 226, 62, 87
       *    - App.Background: 48, 56, 65
       *    - App.Secondary.background: 58, 71, 80
       *    - App.Actionbar: 57, 67, 77
       *    
       * *Blue/Black
       *    - AppColor: 85, 172, 238
       *    - App.Background: 41, 47, 51
       *    - App.Secondary.background: 102, 117, 127
       *    - App.Actionbar: 49, 56, 60
       *    
       * *Orange/Black
       *    - AppColor: 227, 162, 26
       *    - App.Background: 41, 47, 51
       *    - App.Secondary.background: 102, 117, 127
       *    - App.Actionbar: 49, 56, 60
       *    
       * *Coral/Black
       *    - AppColor: 255, 111, 97
       *    - App.Background: 41, 47, 51
       *    - App.Secondary.background: 102, 117, 127
       *    - App.Actionbar: 49, 56, 60
       *    
       * *Green
       *    - AppColor: 153, 180, 51
       *    - App.Background: 11, 40, 25
       *    - App.Secondary.Background: 30, 113, 69
       *    - App.Actionbar: 13, 48, 30
       *
       * *Green/Gray
       *    - AppColor: 153, 180, 51
       *    - App.Background: 48, 56, 65
       *    - App.Secondary.Background: 58, 71, 80
       *    - App.Actionbar: 57, 67, 77
       */

      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
      UIManager.put( "Label.disabledShadow", new Color( 0, 0, 0, 0 ) );
      UIManager.put( "DefaultBrightColor", Color.white );
      ToolTipManager.sharedInstance().setDismissDelay( 1000000 );
    }
    catch ( ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException exception )
    {
      ClientLog.newLog( exception.getMessage() );
    }
    installColors();
    installColorKeys();

    String sessionID = null;
    String darkmode = null;
    File meMateFolder = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" );
    File userPropFile = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
    File clientLogFile = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "ClientLog.log" );

    createPropFile( meMateFolder, userPropFile, clientLogFile );
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
    {
      Properties userProperties = new Properties();
      userProperties.load( input );
      sessionID = userProperties.getProperty( "SessionID" );
      darkmode = userProperties.getProperty( "Darkmode" );
    }
    catch ( Exception exception )
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
      Login login = Login.getInstance();
      MeMateUIManager.setUISettings();
      login.setVisible( true );
      ServerCommunication.getInstance().checkVersion( version );
    }
    else
    {
      ServerCommunication.getInstance().checkLoginForSessionID( sessionID );
      ServerCommunication.getInstance().tellServertoSendVersionNumber();
      Mainframe mainframe = Mainframe.getInstance();
      if ( ServerCommunication.getInstance().currentUser == null )
      {
        System.out.println( "Es wurde kein Nutzer für die angegeben Session gefunden." );
        Login login = Login.getInstance();
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

  /**
   * 
   */
  private static void installColors()
  {
    String color = "null";
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
    {
      Properties userProperties = new Properties();
      userProperties.load( input );
      color = userProperties.getProperty( "colorScheme" );
    }
    catch ( Exception exception )
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
      case "Red / Gray":
        putColorsInUIManager( new Color( 226, 62, 87 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
        break;
      case "Green / Gray":
        putColorsInUIManager( new Color( 153, 180, 51 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
        break;
      case "Blue / Black":
        putColorsInUIManager( new Color( 85, 172, 238 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Orange / Black":
        putColorsInUIManager( new Color( 227, 162, 26 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Coral / Black":
        putColorsInUIManager( new Color( 255, 111, 97 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Green":
        putColorsInUIManager( new Color( 153, 180, 51 ), new Color( 11, 40, 25 ), new Color( 30, 113, 69 ), new Color( 13, 48, 30 ) );
        break;
      default :
        putColorsInUIManager( new Color( 29, 164, 165 ), new Color( 36, 43, 55 ), new Color( 52, 73, 94 ), new Color( 42, 51, 64 ) );
        break;
    }
  }


  private static void putColorsInUIManager( Color appColor, Color background, Color background2, Color actionbar )
  {
    UIManager.put( "AppColor", appColor );
    UIManager.put( "App.Background", background );
    UIManager.put( "App.Secondary.Background", background2 );
    UIManager.put( "App.Actionbar", actionbar );
  }

  /**
   * @param meMateFolder
   * @param userPropFile
   */
  private static void createPropFile( File meMateFolder, File userPropFile, File log )
  {
    try
    {
      meMateFolder.mkdir();
      userPropFile.createNewFile();
      log.createNewFile();
    }
    catch ( IOException exception1 )
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