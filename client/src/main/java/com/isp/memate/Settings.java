/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.UIManager;

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.ToggleSwitch;

/**
 * @author nwe
 * @since 05.03.2020
 *
 */
class Settings extends JPanel
{
  JComboBox<String> colorThemeComboBox = new JComboBox<>();
  JRadioButton      daymodeButton      = MeMateUIManager.createRadioButton( "Hell" );
  JRadioButton      darkmodeButton     = MeMateUIManager.createRadioButton( "Dunkel" );

  /**
   * 
   */
  public Settings()
  {
    setLayout( new GridBagLayout() );
    MeMateUIManager.registerPanel( "default", this );
    addColorThemePicker();
    addDarkmodeSettings();
    addMeetingNotification();
    addConsumptionNotification();
    MeMateUIManager.setUISettings();
  }

  /**
   * 
   */
  private void addConsumptionNotification()
  {
    JLabel consumptionNotificationLabel = MeMateUIManager.createJLabel();
    consumptionNotificationLabel.setText( "Benachrichtigung wenn jemand etwas trinkt." );
    consumptionNotificationLabel.setFont( consumptionNotificationLabel.getFont().deriveFont( 18f ) );
    GridBagConstraints consumptionNotificationLabelConstraints = new GridBagConstraints();
    consumptionNotificationLabelConstraints.gridx = 0;
    consumptionNotificationLabelConstraints.gridy = 8;
    consumptionNotificationLabelConstraints.gridwidth = 10;
    consumptionNotificationLabelConstraints.anchor = GridBagConstraints.LINE_START;
    consumptionNotificationLabelConstraints.insets = new Insets( 30, 20, 0, 0 );


    add( consumptionNotificationLabel, consumptionNotificationLabelConstraints );
    ToggleSwitch consumptionSwitch = new ToggleSwitch();
    GridBagConstraints consumptionSwitchConstraints = new GridBagConstraints();
    consumptionSwitchConstraints.gridx = 0;
    consumptionSwitchConstraints.gridy = 9;
    consumptionSwitchConstraints.ipadx = 70;
    consumptionSwitchConstraints.ipady = 15;
    consumptionSwitchConstraints.anchor = GridBagConstraints.LINE_START;
    consumptionSwitchConstraints.insets = new Insets( 5, 20, 0, 0 );
    add( consumptionSwitch, consumptionSwitchConstraints );
    GridBagConstraints fillerConstraints = new GridBagConstraints();
    JLabel l = new JLabel();
    fillerConstraints.gridx = 0;
    fillerConstraints.gridy = 10;
    fillerConstraints.weighty = 1;
    fillerConstraints.gridwidth = 10;
    fillerConstraints.anchor = GridBagConstraints.LINE_START;
    fillerConstraints.fill = GridBagConstraints.BOTH;
    add( l, fillerConstraints );
    consumptionSwitch.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseReleased( MouseEvent arg0 )
      {
        consumptionSwitch.activated = !consumptionSwitch.activated;
        consumptionSwitch.repaint();
        try
        {
          File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
          InputStream input = new FileInputStream( file );
          Properties userProperties = new Properties();
          userProperties.load( input );
          String state = "false";
          if ( consumptionSwitch.activated )
          {
            state = "true";
          }
          userProperties.setProperty( "ConsumptionNotification", state );
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
    if ( loadPrefAndSetState( "ConsumptionNotification" ) )
    {
      consumptionSwitch.setActivated( true );
    }
  }

  /**
   * @param string
   * @return
   */
  private boolean loadPrefAndSetState( String propertry )
  {
    String state = null;
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
    {
      Properties userProperties = new Properties();
      userProperties.load( input );
      state = userProperties.getProperty( propertry );
    }
    catch ( Exception exception )
    {
      ClientLog.newLog( "Die userconfig-Properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    if ( state == null || state.equals( "false" ) )
    {
      return false;
    }
    return true;
  }

  /**
   * 
   */
  private void addMeetingNotification()
  {
    JLabel meetingNotificationLabel = MeMateUIManager.createJLabel();
    meetingNotificationLabel.setText( "Benachrichtigung für Standup-Meeting" );
    meetingNotificationLabel.setFont( meetingNotificationLabel.getFont().deriveFont( 18f ) );
    GridBagConstraints meetingNotificationLabelcBagConstraints = new GridBagConstraints();
    meetingNotificationLabelcBagConstraints.gridx = 0;
    meetingNotificationLabelcBagConstraints.gridy = 5;
    meetingNotificationLabelcBagConstraints.gridwidth = 10;
    meetingNotificationLabelcBagConstraints.weightx = 1;
    meetingNotificationLabelcBagConstraints.anchor = GridBagConstraints.LINE_START;
    meetingNotificationLabelcBagConstraints.insets = new Insets( 30, 20, 0, 0 );
    add( meetingNotificationLabel, meetingNotificationLabelcBagConstraints );
    ToggleSwitch meetingSwitch = new ToggleSwitch();
    GridBagConstraints meetingSwitchConstraints = new GridBagConstraints();
    meetingSwitchConstraints.gridx = 0;
    meetingSwitchConstraints.gridy = 6;
    meetingSwitchConstraints.ipadx = 70;
    meetingSwitchConstraints.ipady = 15;
    meetingSwitchConstraints.anchor = GridBagConstraints.LINE_START;
    meetingSwitchConstraints.insets = new Insets( 5, 20, 0, 0 );
    add( meetingSwitch, meetingSwitchConstraints );
    meetingSwitch.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseReleased( MouseEvent arg0 )
      {
        meetingSwitch.activated = !meetingSwitch.activated;
        meetingSwitch.repaint();
        try
        {
          File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
          InputStream input = new FileInputStream( file );
          Properties userProperties = new Properties();
          userProperties.load( input );
          String state = "false";
          if ( meetingSwitch.activated )
          {
            state = "true";
          }
          userProperties.setProperty( "MeetingNotification", state );
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
    if ( loadPrefAndSetState( "MeetingNotification" ) )
    {
      meetingSwitch.setActivated( true );
    }
  }

  /**
   * 
   */
  private void addDarkmodeSettings()
  {
    JLabel pickDarkmodeLabel = MeMateUIManager.createJLabel();
    pickDarkmodeLabel.setText( "Standard-App-Modus wählen" );
    pickDarkmodeLabel.setFont( pickDarkmodeLabel.getFont().deriveFont( 18f ) );
    GridBagConstraints pickDarkmodeLabelConstraints = new GridBagConstraints();
    pickDarkmodeLabelConstraints.gridx = 0;
    pickDarkmodeLabelConstraints.gridy = 2;
    pickDarkmodeLabelConstraints.anchor = GridBagConstraints.LINE_START;
    pickDarkmodeLabelConstraints.insets = new Insets( 30, 20, 0, 0 );

    add( pickDarkmodeLabel, pickDarkmodeLabelConstraints );
    ButtonGroup group = new ButtonGroup();
    group.add( daymodeButton );
    group.add( darkmodeButton );
    ActionListener toggleDarkModeListener = new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        JRadioButton source = (JRadioButton) e.getSource();
        String mode = source.getActionCommand();
        if ( mode.equals( "Dunkel" ) )
        {
          if ( MeMateUIManager.getDarkModeState() )
          {
            return;
          }
          MeMateUIManager.showDarkMode();
          try
          {
            File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
            InputStream input = new FileInputStream( file );
            Properties userProperties = new Properties();
            userProperties.load( input );
            userProperties.setProperty( "Darkmode", "on" );
            OutputStream output = new FileOutputStream( file );
            userProperties.store( output, "" );
          }
          catch ( IOException exception )
          {
            ClientLog.newLog( "Der Darkmodestatus konnte nicht gespeichert werden." );
            ClientLog.newLog( exception.getMessage() );
          }
          Mainframe.getInstance().bar.showDarkmode();
        }
        else
        {
          if ( !MeMateUIManager.getDarkModeState() )
          {
            return;
          }
          MeMateUIManager.showDayMode();
          try
          {
            File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
            InputStream input = new FileInputStream( file );
            Properties userProperties = new Properties();
            userProperties.load( input );
            userProperties.setProperty( "Darkmode", "off" );
            OutputStream output = new FileOutputStream( file );
            userProperties.store( output, "" );
          }
          catch ( IOException exception )
          {
            ClientLog.newLog( "Der Darkmodestatus konnte nicht gespeichert werden." );
            ClientLog.newLog( exception.getMessage() );
          }
          Mainframe.getInstance().bar.showDaymode();
        }
      }
    };
    daymodeButton.addActionListener( toggleDarkModeListener );
    darkmodeButton.addActionListener( toggleDarkModeListener );
    getPrefsAndSelectButton();
    GridBagConstraints dayModeButtonConstraints = new GridBagConstraints();
    dayModeButtonConstraints.gridx = 0;
    dayModeButtonConstraints.gridy = 3;
    dayModeButtonConstraints.gridwidth = 1;
    dayModeButtonConstraints.anchor = GridBagConstraints.LINE_START;
    dayModeButtonConstraints.insets = new Insets( 5, 20, 0, 0 );
    GridBagConstraints darkModeButtonConstraints = new GridBagConstraints();
    darkModeButtonConstraints.gridx = 0;
    darkModeButtonConstraints.gridy = 4;
    darkModeButtonConstraints.gridwidth = 1;
    darkModeButtonConstraints.anchor = GridBagConstraints.LINE_START;
    darkModeButtonConstraints.insets = new Insets( 3, 20, 0, 0 );
    add( daymodeButton, dayModeButtonConstraints );
    add( darkmodeButton, darkModeButtonConstraints );
  }

  /**
   * 
   */
  private void getPrefsAndSelectButton()
  {
    String state = "null";
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
    {
      Properties userProperties = new Properties();
      userProperties.load( input );
      state = userProperties.getProperty( "Darkmode" );
    }
    catch ( Exception exception )
    {
      ClientLog.newLog( "Die userconfig-Properties konnten nicht geladen werden" );
      ClientLog.newLog( exception.getMessage() );
    }
    if ( state == null )
    {
      state = "off";
    }
    if ( state.equals( "on" ) )
    {
      darkmodeButton.setSelected( true );
    }
    else
    {
      daymodeButton.setSelected( true );
    }
  }

  /**
   * 
   */
  private void addColorThemePicker()
  {
    MeMateUIManager.registerComboBox( colorThemeComboBox );
    JLabel pickThemeLabel = MeMateUIManager.createJLabel();
    pickThemeLabel.setText( "Color Scheme auswählen" );
    pickThemeLabel.setFont( pickThemeLabel.getFont().deriveFont( 18f ) );
    GridBagConstraints pickThemeLabelConstraints = new GridBagConstraints();
    pickThemeLabelConstraints.gridx = 0;
    pickThemeLabelConstraints.gridy = 0;
    pickThemeLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
    pickThemeLabelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
    pickThemeLabelConstraints.insets = new Insets( 10, 20, 0, 0 );
    add( pickThemeLabel, pickThemeLabelConstraints );

    colorThemeComboBox.addItem( "Blue" );
    colorThemeComboBox.addItem( "Dark Blue" );
    colorThemeComboBox.addItem( "Red / Gray" );
    colorThemeComboBox.addItem( "Green / Gray" );
    colorThemeComboBox.addItem( "Blue / Black" );
    colorThemeComboBox.addItem( "Orange / Black" );
    colorThemeComboBox.addItem( "Coral / Black" );
    colorThemeComboBox.addItem( "Green" );
    GridBagConstraints colorThemeComboBoxConstraints = new GridBagConstraints();
    colorThemeComboBoxConstraints.gridx = 0;
    colorThemeComboBoxConstraints.gridy = 1;
    colorThemeComboBoxConstraints.gridwidth = 1;
    colorThemeComboBoxConstraints.ipadx = 150;
    colorThemeComboBoxConstraints.ipady = 10;
    colorThemeComboBoxConstraints.insets = new Insets( 5, 20, 0, 0 );
    getPrefsAndSelectItem();
    add( colorThemeComboBox, colorThemeComboBoxConstraints );

    colorThemeComboBox.addItemListener( new ItemListener()
    {

      @Override
      public void itemStateChanged( ItemEvent e )
      {
        String color = String.valueOf( colorThemeComboBox.getSelectedItem() );
        switch ( color )
        {
          case "Dark Blue":
            setColors( color, new Color( 0, 173, 181 ), new Color( 34, 40, 49 ), new Color( 57, 62, 70 ), new Color( 42, 51, 64 ) );
            break;
          case "Red / Gray":
            setColors( color, new Color( 226, 62, 87 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
            break;
          case "Green / Gray":
            setColors( color, new Color( 153, 180, 51 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
            break;
          case "Blue / Black":
            setColors( color, new Color( 85, 172, 238 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
            break;
          case "Orange / Black":
            setColors( color, new Color( 227, 162, 26 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
            break;
          case "Coral / Black":
            setColors( color, new Color( 255, 111, 97 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
            break;
          case "Green":
            setColors( color, new Color( 153, 180, 51 ), new Color( 11, 40, 25 ), new Color( 30, 113, 69 ), new Color( 13, 48, 30 ) );
            break;
          default :
            setColors( color, new Color( 29, 164, 165 ), new Color( 36, 43, 55 ), new Color( 52, 73, 94 ), new Color( 42, 51, 64 ) );
            break;
        }
      }
    } );
  }


  /**
   * 
   */
  private void getPrefsAndSelectItem()
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
        colorThemeComboBox.setSelectedItem( "Dark Blue" );
        break;
      case "Red / Gray":
        colorThemeComboBox.setSelectedItem( "Red / Gray" );
        break;
      case "Green / Gray":
        colorThemeComboBox.setSelectedItem( "Green / Gray" );
        break;
      case "Blue / Black":
        colorThemeComboBox.setSelectedItem( "Blue / Black" );
        break;
      case "Orange / Black":
        colorThemeComboBox.setSelectedItem( "Orange / Black" );
        break;
      case "Coral / Black":
        colorThemeComboBox.setSelectedItem( "Coral / Black" );
        break;
      case "Green":
        colorThemeComboBox.setSelectedItem( "Green" );
        break;
      default :
        colorThemeComboBox.setSelectedItem( "Blue" );
        break;
    }
  }

  private void setColors( String theme, Color appColor, Color background, Color background2, Color actionbar )
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
      userProperties.setProperty( "colorScheme", theme );
      OutputStream output = new FileOutputStream( file );
      userProperties.store( output, "" );
    }
    catch ( IOException exception )
    {
      ClientLog.newLog( "Die SessionID konnte nicht gespeichert werden." );
      ClientLog.newLog( exception.getMessage() );
    }
  }
}
