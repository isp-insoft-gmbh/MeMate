/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;

/**
 * @author nwe
 * @since 05.03.2020
 *
 */
class Settings extends JPanel
{
  JRadioButton defaultButton       = MeMateUIManager.createRadioButton( "Blue" );
  JRadioButton darkerDefaultButton = MeMateUIManager.createRadioButton( "Dark Blue" );
  JRadioButton redGrayButton       = MeMateUIManager.createRadioButton( "Red / Gray" );
  JRadioButton greenGrayButton     = MeMateUIManager.createRadioButton( "Green / Gray" );
  JRadioButton blueBlackButton     = MeMateUIManager.createRadioButton( "Blue / Black" );
  JRadioButton orangeBlackButton   = MeMateUIManager.createRadioButton( "Orange / Black" );
  JRadioButton coralBlackButton    = MeMateUIManager.createRadioButton( "Coral / Black" );
  JRadioButton greenButton         = MeMateUIManager.createRadioButton( "Green" );

  /**
   * 
   */
  public Settings()
  {
    MeMateUIManager.registerPanel( "default", this );
    MeMateUIManager.setUISettings();
    addThemeButtons();
  }

  /**
   * 
   */
  private void addThemeButtons()
  {
    JPanel designPanel = new JPanel();
    designPanel.setLayout( new GridLayout( 0, 1 ) );

    getPrefsAndSelectButton();

    ButtonGroup group = new ButtonGroup();
    group.add( defaultButton );
    group.add( darkerDefaultButton );
    group.add( redGrayButton );
    group.add( greenGrayButton );
    group.add( blueBlackButton );
    group.add( orangeBlackButton );
    group.add( coralBlackButton );
    group.add( greenButton );

    addListener( defaultButton, new Color( 29, 164, 165 ), new Color( 36, 43, 55 ), new Color( 52, 73, 94 ), new Color( 42, 51, 64 ) );
    addListener( darkerDefaultButton, new Color( 0, 173, 181 ), new Color( 34, 40, 49 ), new Color( 57, 62, 70 ), new Color( 42, 51, 64 ) );
    addListener( redGrayButton, new Color( 226, 62, 87 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
    addListener( blueBlackButton, new Color( 85, 172, 238 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
    addListener( orangeBlackButton, new Color( 227, 162, 26 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ),
        new Color( 49, 56, 60 ) );
    addListener( coralBlackButton, new Color( 255, 111, 97 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ),
        new Color( 49, 56, 60 ) );
    addListener( greenButton, new Color( 153, 180, 51 ), new Color( 11, 40, 25 ), new Color( 30, 113, 69 ), new Color( 13, 48, 30 ) );
    addListener( greenGrayButton, new Color( 153, 180, 51 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );

    designPanel.add( defaultButton );
    designPanel.add( darkerDefaultButton );
    designPanel.add( redGrayButton );
    designPanel.add( greenGrayButton );
    designPanel.add( blueBlackButton );
    designPanel.add( orangeBlackButton );
    designPanel.add( coralBlackButton );
    designPanel.add( greenButton );

    add( designPanel );
  }

  /**
   * 
   */
  private void getPrefsAndSelectButton()
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
        darkerDefaultButton.setSelected( true );
        break;
      case "Red / Gray":
        redGrayButton.setSelected( true );
        break;
      case "Green / Gray":
        greenGrayButton.setSelected( true );
        break;
      case "Blue / Black":
        blueBlackButton.setSelected( true );
        break;
      case "Orange / Black":
        orangeBlackButton.setSelected( true );
        break;
      case "Coral / Black":
        coralBlackButton.setSelected( true );
        break;
      case "Green":
        greenButton.setSelected( true );
        break;
      default :
        defaultButton.setSelected( true );
        break;
    }
  }

  private void addListener( JRadioButton button, Color appColor, Color background, Color background2, Color actionbar )
  {
    button.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        UIManager.put( "AppColor", appColor );
        UIManager.put( "App.Background", background );
        UIManager.put( "App.Secondary.Background", background2 );
        UIManager.put( "App.Actionbar", actionbar );
        MeMateUIManager.installDefaults();
        MeMateUIManager.setUISettings();
        Mainframe.getInstance().headerPanel.setBackground( appColor );
        Mainframe.getInstance().settingsButton.selected();
        if ( MeMateUIManager.getDarkModeState() )
        {
          Mainframe.getInstance().bar.setBackground( actionbar );
          Mainframe.getInstance().burgerButton.setBackground( actionbar );
        }
        try
        {
          File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
          InputStream input = new FileInputStream( file );
          Properties userProperties = new Properties();
          userProperties.load( input );
          userProperties.setProperty( "colorScheme", button.getText() );
          OutputStream output = new FileOutputStream( file );
          userProperties.store( output, "" );
        }
        catch ( IOException exception )
        {
          ClientLog.newLog( "Die SessionID konnte nicht gespeichert werden." );
          ClientLog.newLog( exception.getMessage() );
        }
      }
    } );
  }
}
