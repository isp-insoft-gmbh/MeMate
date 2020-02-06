package com.isp.memate;

import java.awt.Color;
import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.MeMateUIManager.DarkDayColor;

/**
 * Die Mainklasse setzt das Look and Feel und öffnet den LoginFrame.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class Main
{
  final static String version = "0.9.5";

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
      UIManager.put( "DefaultButtonBackground", new Color( 225, 225, 225 ) );
      UIManager.put( "DefaultButtonForeground", new Color( 0, 0, 0 ) );
      UIManager.put( "DefaultButtonBorderColor", new Color( 173, 173, 173 ) );
      UIManager.put( "DefaultButtonBorderHover", new Color( 0, 120, 215 ) );
      UIManager.put( "DefaultButtonBackgroundHover", new Color( 229, 241, 251 ) );
      UIManager.put( "DefaultButtonPressedBackground", new Color( 204, 228, 247 ) );

    }
    catch ( ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException exception )
    {
      // We don't mind if we aren't able to set a Look and Feel, therefore we just ignore the exceptions.
    }
    MeMateUIManager.installDefaults();
    MeMateUIManager.installNewKey( "button",
        new DarkDayColor( new Color( 36, 43, 55 ).brighter(), new Color( 225, 225, 225 ) ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "drinkButtons", new DarkDayColor( new Color( 36, 43, 55 ).brighter(), new Color( 240, 240, 240 ) ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "table", new DarkDayColor( new Color( 36, 43, 55 ), Color.white ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "scroll", new DarkDayColor( new Color( 36, 43, 55 ), Color.white ),
        new DarkDayColor( Color.white, Color.black ) );


    String sessionID = null;
    File meMateFolder = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" );
    File userPropFile = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
    try
    {
      meMateFolder.mkdir();
      userPropFile.createNewFile();
    }
    catch ( IOException exception1 )
    {
      System.out.println( "Die userconfig-Properties konnten nicht erstellt werden." );
      exception1.printStackTrace();
    }
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
    {
      Properties userProperties = new Properties();
      userProperties.load( input );
      sessionID = userProperties.getProperty( "SessionID" );
      // sessionID = "null"; //TODO DELETE ME IF MULTI-CLIENT TESTING IS DONE
    }
    catch ( Exception exception )
    {
      System.out.println( "Die userconfig-Properties konnten nicht geladen werden" );
      exception.printStackTrace();
    }
    if ( sessionID == null || sessionID.equals( "null" ) )
    {
      ServerCommunication.getInstance().tellServertoSendVersionNumber();
      Login login = Login.getInstance();
      login.setVisible( true );
      ServerCommunication.getInstance().checkVersion( version );
    }
    else
    {
      ServerCommunication.getInstance().sessionID = sessionID;
      ServerCommunication.getInstance().checkLoginForSessionID( sessionID );
      ServerCommunication.getInstance().tellServertoSendVersionNumber();
      Mainframe mainframe = Mainframe.getInstance();
      if ( ServerCommunication.getInstance().currentUser == null )
      {
        System.out.println( "Es wurde kein Nutzer für die angegeben Session gefunden." );
        Login login = Login.getInstance();
        login.setVisible( true );
        ServerCommunication.getInstance().checkVersion( version );
      }
      else
      {
        mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        mainframe.setVisible( true );
        ServerCommunication.getInstance().tellServerToSendHistoryData();
        ServerCommunication.getInstance().checkVersion( version );
        History.getInstance().updateHistory();
        mainframe.toggleAdminView();
        mainframe.requestFocus();
      }
    }
  }
}