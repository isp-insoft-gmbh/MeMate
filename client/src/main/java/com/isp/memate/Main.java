package com.isp.memate;

import java.awt.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Die Mainklasse setzt das Look and Feel und öffnet den LoginFrame.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class Main
{
  /**
   * @param args unused
   */
  public static void main( String[] args )
  {

    try
    {
      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
    }
    catch ( ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException exception )
    {
      // We don't mind if we aren't able to set a Look and Feel, therefore we just ignore the exceptions.
    }
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
      Login login = Login.getInstance();
      login.setVisible( true );
    }
    else
    {
      ServerCommunication.getInstance().sessionID = sessionID;
      ServerCommunication.getInstance().checkLoginForSessionID( sessionID );
      Mainframe mainframe = Mainframe.getInstance();
      if ( ServerCommunication.getInstance().currentUser == null )
      {
        System.out.println( "Es wurde kein Nutzer für die angegeben Session gefunden." );
        Login login = Login.getInstance();
        login.setVisible( true );
      }
      else
      {
        mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        mainframe.setVisible( true );
        ServerCommunication.getInstance().tellServerToSendHistoryData();
        History.getInstance().updateHistory();
        mainframe.toggleAdminView();
        mainframe.requestFocus();
      }
    }
  }
}