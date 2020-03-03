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
import com.isp.memate.util.MeMateUIManager.DarkDayColor;

/**
 * Die Mainklasse setzt das Look and Feel und öffnet den LoginFrame, wenn es keine SessionID gibt oder sie
 * abgelaufen ist.
 * 
 * @author nwe
 * @since 15.10.2019
 */
class Main
{
  final static String version = "0.9.8.2";

  /**
   * @param args unused
   */
  public static void main( String[] args )
  {
    try
    {
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
      UIManager.put( "Label.disabledShadow", new Color( 0, 0, 0, 0 ) );
      UIManager.put( "AppColor", new Color( 29, 164, 165 ) );
      UIManager.put( "DefaultBrightColor", Color.white );
      ToolTipManager.sharedInstance().setDismissDelay( 1000000 );
    }
    catch ( ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException exception )
    {
      ClientLog.newLog( exception.getMessage() );
    }

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
        ServerCommunication.getInstance().startDrinkInfoTimer();
        ServerCommunication.getInstance().tellServerToSendHistoryData();
        ServerCommunication.getInstance().checkVersion( version );
        mainframe.toggleAdminView();
        mainframe.requestFocus();
        MeMateUIManager.setUISettings();
      }
    }
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
    MeMateUIManager.installNewKey( "button",
        new DarkDayColor( new Color( 36, 43, 55 ).brighter(), new Color( 215, 215, 215 ) ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "drinkButtons", new DarkDayColor( new Color( 36, 43, 55 ).brighter(), new Color( 236, 240, 241 ) ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "table", new DarkDayColor( new Color( 36, 43, 55 ), Color.white ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "scroll", new DarkDayColor( new Color( 36, 43, 55 ), Color.white ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "adminButton",
        new DarkDayColor( new Color( 52, 73, 94 ), new Color( 236, 240, 241 ) ),
        new DarkDayColor( Color.WHITE, Color.BLACK ) );
    MeMateUIManager.installNewKey( "spinner",
        new DarkDayColor( new Color( 52, 73, 94 ), new Color( 236, 240, 241 ) ),
        new DarkDayColor( Color.WHITE, Color.BLACK ) );
  }
}