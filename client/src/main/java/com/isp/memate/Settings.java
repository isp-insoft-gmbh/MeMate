/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.ToggleSwitch;

/**
 * Auf dem Settings Panel kann sein gewünschtes Color Scheme auswählen, den Darkmodestate verändern und
 * Benachrichtigungen für Standup-Meeting oder andere Benachrichtigungen aktivieren und deaktivieren.
 *
 * @author nwe
 * @since 05.03.2020
 */
class Settings extends JPanel
{
  private JComboBox<String> colorThemeComboBox;
  private JRadioButton      lightmodeButton;
  private JRadioButton      darkmodeButton;

  public Settings()
  {
    initComponents();
    setLayout( new GridBagLayout() );
    addComponents();
  }

  private void initComponents()
  {
    colorThemeComboBox = new JComboBox<>();
    lightmodeButton = new JRadioButton( "Hell" );
    darkmodeButton = new JRadioButton( "Dunkel" );
  }

  private void addComponents()
  {
    addColorThemePicker();
    addDarkmodeSettings();
    addMeetingNotification();
    addConsumptionNotification();
    addChangePasswordHyperlink();
    addChangeDisplayNameHyperlink();
    addFiller();
  }

  private void addFiller()
  {
    final GridBagConstraints fillerConstraints = new GridBagConstraints();
    final JLabel l = new JLabel();
    fillerConstraints.gridx = 0;
    fillerConstraints.gridy = 12;
    fillerConstraints.weighty = 1;
    fillerConstraints.gridwidth = 10;
    fillerConstraints.anchor = GridBagConstraints.LINE_START;
    fillerConstraints.fill = GridBagConstraints.BOTH;
    add( l, fillerConstraints );
  }

  private void addChangePasswordHyperlink()
  {
    final JLabel hyperlink = new JLabel();
    hyperlink.setText( "Passwort ändern" );
    hyperlink.setFont( hyperlink.getFont().deriveFont( 18f ) );
    hyperlink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    hyperlink.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent e )
      {
        new ChangePasswordDialog();
      }

      @Override
      public void mouseEntered( final MouseEvent e )
      {
        hyperlink.setText( "<html><u>Passwort ändern</u></html>" );
      }

      @Override
      public void mouseExited( final MouseEvent e )
      {
        hyperlink.setText( "Passwort ändern" );
      }
    } );
    final GridBagConstraints hyperlinkConstraints = new GridBagConstraints();
    hyperlinkConstraints.gridx = 0;
    hyperlinkConstraints.gridy = 10;
    hyperlinkConstraints.anchor = GridBagConstraints.LINE_START;
    hyperlinkConstraints.insets = new Insets( 30, 20, 0, 0 );
    add( hyperlink, hyperlinkConstraints );
  }

  private void addChangeDisplayNameHyperlink()
  {
    final JLabel hyperlink = new JLabel();
    hyperlink.setText( "Anzeigenamen ändern" );
    hyperlink.setFont( hyperlink.getFont().deriveFont( 18f ) );
    hyperlink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    hyperlink.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent e )
      {
        showDisplayNameChangeDialog();
      }

      @Override
      public void mouseEntered( final MouseEvent e )
      {
        hyperlink.setText( "<html><u>Anzeigenamen ändern</u></html>" );
      }

      @Override
      public void mouseExited( final MouseEvent e )
      {
        hyperlink.setText( "Anzeigenamen ändern" );
      }
    } );
    final GridBagConstraints hyperlinkConstraints = new GridBagConstraints();
    hyperlinkConstraints.gridx = 0;
    hyperlinkConstraints.gridy = 11;
    hyperlinkConstraints.anchor = GridBagConstraints.LINE_START;
    hyperlinkConstraints.insets = new Insets( 30, 20, 0, 0 );
    add( hyperlink, hyperlinkConstraints );
  }

  private void showDisplayNameChangeDialog()
  {
    final JTextField nameField = new JTextField();
    final String title = "Anzeigenamen ändern";
    final Object[] buttonNames = { "Speichern", "Abbrechen" };
    final Object[] params = { title + ":", nameField };
    int answer = JOptionPane.showOptionDialog( this, params, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
        buttonNames, buttonNames[ 0 ] );
    if ( answer == JOptionPane.YES_OPTION )
    {
      final String username = nameField.getText();
      if ( username == null || username.length() == 0 )
      {
        JOptionPane.showMessageDialog( this, "Der Anzeigename darf nicht leer sein.", title, JOptionPane.WARNING_MESSAGE );
      }
      else if ( username.length() > 10 )
      {
        JOptionPane.showMessageDialog( this, "Der Anzeigename darf nicht länger als 10 Zeichen sein.", title,
            JOptionPane.WARNING_MESSAGE );
      }
      else
      {
        ServerCommunication.getInstance().changeDisplayName( username );
      }
    }
  }


  private void addConsumptionNotification()
  {
    final JLabel consumptionNotificationLabel = new JLabel();
    consumptionNotificationLabel.setText( "Benachrichtigung wenn jemand etwas trinkt." );
    consumptionNotificationLabel.setFont( consumptionNotificationLabel.getFont().deriveFont( 18f ) );
    final GridBagConstraints consumptionNotificationLabelConstraints = new GridBagConstraints();
    consumptionNotificationLabelConstraints.gridx = 0;
    consumptionNotificationLabelConstraints.gridy = 8;
    consumptionNotificationLabelConstraints.gridwidth = 10;
    consumptionNotificationLabelConstraints.anchor = GridBagConstraints.LINE_START;
    consumptionNotificationLabelConstraints.insets = new Insets( 30, 20, 0, 0 );

    add( consumptionNotificationLabel, consumptionNotificationLabelConstraints );
    final ToggleSwitch consumptionSwitch = new ToggleSwitch();
    final GridBagConstraints consumptionSwitchConstraints = new GridBagConstraints();
    consumptionSwitchConstraints.gridx = 0;
    consumptionSwitchConstraints.gridy = 9;
    consumptionSwitchConstraints.ipadx = 70;
    consumptionSwitchConstraints.ipady = 15;
    consumptionSwitchConstraints.anchor = GridBagConstraints.LINE_START;
    consumptionSwitchConstraints.insets = new Insets( 5, 20, 0, 0 );
    add( consumptionSwitch, consumptionSwitchConstraints );
    consumptionSwitch.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseReleased( final MouseEvent arg0 )
      {
        consumptionSwitch.activated = !consumptionSwitch.activated;
        consumptionSwitch.repaint();
        try
        {
          final File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
          final InputStream input = new FileInputStream( file );
          final Properties userProperties = new Properties();
          userProperties.load( input );
          String state = "false";
          if ( consumptionSwitch.activated )
          {
            state = "true";
          }
          userProperties.setProperty( "ConsumptionNotification", state );
          final OutputStream output = new FileOutputStream( file );
          userProperties.store( output, "" );
        }
        catch ( final IOException exception )
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

  private boolean loadPrefAndSetState( final String propertry )
  {
    String state = null;
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
    {
      final Properties userProperties = new Properties();
      userProperties.load( input );
      state = userProperties.getProperty( propertry );
    }
    catch ( final Exception exception )
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


  private void addMeetingNotification()
  {
    final JLabel meetingNotificationLabel = new JLabel();
    meetingNotificationLabel.setText( "Benachrichtigung für Standup-Meeting" );
    meetingNotificationLabel.setFont( meetingNotificationLabel.getFont().deriveFont( 18f ) );
    final GridBagConstraints meetingNotificationLabelcBagConstraints = new GridBagConstraints();
    meetingNotificationLabelcBagConstraints.gridx = 0;
    meetingNotificationLabelcBagConstraints.gridy = 5;
    meetingNotificationLabelcBagConstraints.gridwidth = 10;
    meetingNotificationLabelcBagConstraints.weightx = 1;
    meetingNotificationLabelcBagConstraints.anchor = GridBagConstraints.LINE_START;
    meetingNotificationLabelcBagConstraints.insets = new Insets( 30, 20, 0, 0 );
    add( meetingNotificationLabel, meetingNotificationLabelcBagConstraints );
    final ToggleSwitch meetingSwitch = new ToggleSwitch();
    final GridBagConstraints meetingSwitchConstraints = new GridBagConstraints();
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
      public void mouseReleased( final MouseEvent arg0 )
      {
        meetingSwitch.activated = !meetingSwitch.activated;
        meetingSwitch.repaint();
        try
        {
          final File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
          final InputStream input = new FileInputStream( file );
          final Properties userProperties = new Properties();
          userProperties.load( input );
          String state = "false";
          if ( meetingSwitch.activated )
          {
            state = "true";
          }
          userProperties.setProperty( "MeetingNotification", state );
          final OutputStream output = new FileOutputStream( file );
          userProperties.store( output, "" );
        }
        catch ( final IOException exception )
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

  private void addDarkmodeSettings()
  {
    final JLabel pickDarkmodeLabel = new JLabel();
    pickDarkmodeLabel.setText( "Standard-App-Modus wählen" );
    pickDarkmodeLabel.setFont( pickDarkmodeLabel.getFont().deriveFont( 18f ) );
    final GridBagConstraints pickDarkmodeLabelConstraints = new GridBagConstraints();
    pickDarkmodeLabelConstraints.gridx = 0;
    pickDarkmodeLabelConstraints.gridy = 2;
    pickDarkmodeLabelConstraints.anchor = GridBagConstraints.LINE_START;
    pickDarkmodeLabelConstraints.insets = new Insets( 30, 20, 0, 0 );

    add( pickDarkmodeLabel, pickDarkmodeLabelConstraints );
    final ButtonGroup group = new ButtonGroup();
    group.add( lightmodeButton );
    group.add( darkmodeButton );
    final ActionListener toggleDarkModeListener = e ->
    {
      final JRadioButton source = (JRadioButton) e.getSource();
      final String mode = source.getActionCommand();
      if ( mode.equals( "Dunkel" ) )
      {
        if ( MeMateUIManager.getDarkModeState() )
        {
          return;
        }
        MeMateUIManager.showDarkMode();
        try
        {
          final File file1 = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
          final InputStream input1 = new FileInputStream( file1 );
          final Properties userProperties1 = new Properties();
          userProperties1.load( input1 );
          userProperties1.setProperty( "Darkmode", "on" );
          final OutputStream output1 = new FileOutputStream( file1 );
          userProperties1.store( output1, "" );
        }
        catch ( final IOException exception1 )
        {
          ClientLog.newLog( "Der Darkmodestatus konnte nicht gespeichert werden." );
          ClientLog.newLog( exception1.getMessage() );
        }
        GUIObjects.mainframe.bar.showDarkmode();
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
          final File file2 = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
          final InputStream input2 = new FileInputStream( file2 );
          final Properties userProperties2 = new Properties();
          userProperties2.load( input2 );
          userProperties2.setProperty( "Darkmode", "off" );
          final OutputStream output2 = new FileOutputStream( file2 );
          userProperties2.store( output2, "" );
        }
        catch ( final IOException exception2 )
        {
          ClientLog.newLog( "Der Darkmodestatus konnte nicht gespeichert werden." );
          ClientLog.newLog( exception2.getMessage() );
        }
        GUIObjects.mainframe.bar.showDaymode();
      }
    };
    lightmodeButton.addActionListener( toggleDarkModeListener );
    darkmodeButton.addActionListener( toggleDarkModeListener );
    getPrefsAndSelectButton();
    final GridBagConstraints dayModeButtonConstraints = new GridBagConstraints();
    dayModeButtonConstraints.gridx = 0;
    dayModeButtonConstraints.gridy = 3;
    dayModeButtonConstraints.gridwidth = 1;
    dayModeButtonConstraints.anchor = GridBagConstraints.LINE_START;
    dayModeButtonConstraints.insets = new Insets( 5, 20, 0, 0 );
    final GridBagConstraints darkModeButtonConstraints = new GridBagConstraints();
    darkModeButtonConstraints.gridx = 0;
    darkModeButtonConstraints.gridy = 4;
    darkModeButtonConstraints.gridwidth = 1;
    darkModeButtonConstraints.anchor = GridBagConstraints.LINE_START;
    darkModeButtonConstraints.insets = new Insets( 3, 20, 0, 0 );
    add( lightmodeButton, dayModeButtonConstraints );
    add( darkmodeButton, darkModeButtonConstraints );
  }

  private void getPrefsAndSelectButton()
  {
    String state = "null";
    try ( InputStream input =
        new FileInputStream( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ) )
    {
      final Properties userProperties = new Properties();
      userProperties.load( input );
      state = userProperties.getProperty( "Darkmode" );
    }
    catch ( final Exception exception )
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
      lightmodeButton.setSelected( true );
    }
  }

  private void addColorThemePicker()
  {
    MeMateUIManager.registerComboBox( colorThemeComboBox );
    final JLabel pickThemeLabel = new JLabel();
    pickThemeLabel.setText( "Color Scheme auswählen" );
    pickThemeLabel.setFont( pickThemeLabel.getFont().deriveFont( 18f ) );
    final GridBagConstraints pickThemeLabelConstraints = new GridBagConstraints();
    pickThemeLabelConstraints.gridx = 0;
    pickThemeLabelConstraints.gridy = 0;
    pickThemeLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
    pickThemeLabelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
    pickThemeLabelConstraints.insets = new Insets( 10, 20, 0, 0 );
    add( pickThemeLabel, pickThemeLabelConstraints );

    colorThemeComboBox.addItem( "Cyan" );
    colorThemeComboBox.addItem( "Dark Blue" );
    colorThemeComboBox.addItem( "Red" );
    colorThemeComboBox.addItem( "Green" );
    colorThemeComboBox.addItem( "Blue" );
    colorThemeComboBox.addItem( "Orange" );
    colorThemeComboBox.addItem( "Coral" );
    colorThemeComboBox.addItem( "Tripple Green" );
    final GridBagConstraints colorThemeComboBoxConstraints = new GridBagConstraints();
    colorThemeComboBoxConstraints.gridx = 0;
    colorThemeComboBoxConstraints.gridy = 1;
    colorThemeComboBoxConstraints.gridwidth = 1;
    colorThemeComboBoxConstraints.ipadx = 150;
    colorThemeComboBoxConstraints.ipady = 10;
    colorThemeComboBoxConstraints.insets = new Insets( 5, 20, 0, 0 );
    getPrefsAndSelectItem();
    add( colorThemeComboBox, colorThemeComboBoxConstraints );

    colorThemeComboBox.addItemListener( e ->
    {
      //Otherwise the event will trigger twice every time
      if ( e.getStateChange() == ItemEvent.SELECTED )
      {
        final String color = String.valueOf( colorThemeComboBox.getSelectedItem() );
        switch ( color )
        {
          case "Dark Blue":
            setColors( color, new Color( 0, 173, 181 ), new Color( 34, 40, 49 ), new Color( 57, 62, 70 ), new Color( 42, 51, 64 ) );
            break;
          case "Red":
            setColors( color, new Color( 226, 62, 87 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
            break;
          case "Green":
            setColors( color, new Color( 153, 180, 51 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
            break;
          case "Blue":
            setColors( color, new Color( 85, 172, 238 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
            break;
          case "Orange":
            setColors( color, new Color( 227, 162, 26 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
            break;
          case "Coral":
            setColors( color, new Color( 255, 111, 97 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
            break;
          case "Tripple Green":
            setColors( color, new Color( 153, 180, 51 ), new Color( 11, 40, 25 ), new Color( 30, 113, 69 ), new Color( 13, 48, 30 ) );
            break;
          default :
            setColors( color, new Color( 29, 164, 165 ), new Color( 36, 43, 55 ), new Color( 52, 73, 94 ), new Color( 42, 51, 64 ) );
            break;
        }
      }
    } );
  }


  private void getPrefsAndSelectItem()
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
        colorThemeComboBox.setSelectedItem( "Dark Blue" );
        break;
      case "Red":
        colorThemeComboBox.setSelectedItem( "Red" );
        break;
      case "Green":
        colorThemeComboBox.setSelectedItem( "Green" );
        break;
      case "Blue":
        colorThemeComboBox.setSelectedItem( "Blue" );
        break;
      case "Orange":
        colorThemeComboBox.setSelectedItem( "Orange" );
        break;
      case "Coral":
        colorThemeComboBox.setSelectedItem( "Coral" );
        break;
      case "Tripple Green":
        colorThemeComboBox.setSelectedItem( "Green" );
        break;
      default :
        colorThemeComboBox.setSelectedItem( "Cyan" );
        break;
    }
  }

  private void setColors( final String theme, final Color appColor, final Color background, final Color background2, final Color actionbar )
  {
    UIManager.put( "AppColor", appColor );
    UIManager.put( "App.Background", background );
    UIManager.put( "App.Secondary.Background", background2 );
    UIManager.put( "App.Actionbar", actionbar );
    UIManager.put( "FocusBorder",
        BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( appColor ),
            BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );
    MeMateUIManager.installDefaults();
    MeMateUIManager.setUISettings();
    GUIObjects.mainframe.headerPanel.setBackground( appColor );
    GUIObjects.mainframe.settingsButton.selected();
    if ( MeMateUIManager.getDarkModeState() )
    {
      GUIObjects.mainframe.bar.setBackground( actionbar );
      GUIObjects.mainframe.burgerButton.setBackground( actionbar );
    }
    try
    {
      final File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
      final InputStream input = new FileInputStream( file );
      final Properties userProperties = new Properties();
      userProperties.load( input );
      userProperties.setProperty( "colorScheme", theme );
      final OutputStream output = new FileOutputStream( file );
      userProperties.store( output, "" );
    }
    catch ( final IOException exception )
    {
      ClientLog.newLog( "Die SessionID konnte nicht gespeichert werden." );
      ClientLog.newLog( exception.getMessage() );
    }
  }
}
