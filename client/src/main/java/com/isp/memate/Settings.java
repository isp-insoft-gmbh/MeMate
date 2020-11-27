/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.ToggleSwitch;
import com.isp.memate.util.Util;

/**
 * Auf dem Settings Panel kann sein gewünschtes Color Scheme auswählen, den Darkmodestate verändern und
 * Benachrichtigungen für Standup-Meeting oder andere Benachrichtigungen aktivieren und deaktivieren.
 *
 * @author nwe
 * @since 05.03.2020
 *
 */
class Settings extends JPanel
{
  private final JComboBox<String> colorThemeComboBox = new JComboBox<>();
  private final JRadioButton      daymodeButton      = MeMateUIManager.createRadioButton( "Hell" );
  private final JRadioButton      darkmodeButton     = MeMateUIManager.createRadioButton( "Dunkel" );

  public Settings()
  {
    setLayout( new GridBagLayout() );
    MeMateUIManager.registerPanel( "default", this );
    addColorThemePicker();
    addDarkmodeSettings();
    addMeetingNotification();
    addConsumptionNotification();
    addChangePasswordHyperlink();
    addChangeDisplayNameHyperlink();
    addFiller();
    MeMateUIManager.setUISettings();
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
    final JLabel hyperlink = MeMateUIManager.createJLabel();
    hyperlink.setText( "Passwort ändern" );
    hyperlink.setFont( hyperlink.getFont().deriveFont( 18f ) );
    hyperlink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    hyperlink.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent e )
      {
        showPasswordChangeDialog();
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
    final JLabel hyperlink = MeMateUIManager.createJLabel();
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

  private void showPasswordChangeDialog()
  {
    final JDialog changePasswordFrame = new JDialog( Mainframe.getInstance(), "Passwort ändern", true );
    final JPanel changePasswordPanel = new JPanel( new GridBagLayout() );
    final JLabel passwordlabel = new JLabel( "Passwort:" );
    final JLabel password2label = new JLabel( "Passwort wiederholen:" );
    final JLabel passwordCompareLabel = new JLabel();
    final JPasswordField passwordField = MeMateUIManager.createJPasswordField();
    final JPasswordField password2Field = MeMateUIManager.createJPasswordField();
    final JButton savePasswordButton = MeMateUIManager.createButton( "button", "Speichern" );
    final JButton pass_abortButton = MeMateUIManager.createButton( "button", "Abbrechen" );

    changePasswordFrame.getRootPane().setDefaultButton( savePasswordButton );

    final int prefHeight = passwordField.getPreferredSize().height;
    passwordField.setPreferredSize( new Dimension( 200, prefHeight ) );
    password2Field.setPreferredSize( new Dimension( 200, prefHeight ) );
    passwordCompareLabel.setPreferredSize( new Dimension( 200, prefHeight ) );

    final GridBagConstraints reg_passwordlabelConstraints = new GridBagConstraints();
    reg_passwordlabelConstraints.gridx = 0;
    reg_passwordlabelConstraints.gridy = 0;
    reg_passwordlabelConstraints.insets = new Insets( 10, 0, 10, 0 );
    reg_passwordlabelConstraints.anchor = GridBagConstraints.LINE_START;
    changePasswordPanel.add( passwordlabel, reg_passwordlabelConstraints );
    final GridBagConstraints reg_passwordFieldConstraints = new GridBagConstraints();
    reg_passwordFieldConstraints.gridx = 1;
    reg_passwordFieldConstraints.gridy = 0;
    reg_passwordFieldConstraints.insets = new Insets( 10, 5, 10, 0 );
    changePasswordPanel.add( passwordField, reg_passwordFieldConstraints );
    final GridBagConstraints reg_password2labelConstraints = new GridBagConstraints();
    reg_password2labelConstraints.gridx = 0;
    reg_password2labelConstraints.gridy = 1;
    reg_password2labelConstraints.insets = new Insets( 0, 0, 10, 0 );
    reg_password2labelConstraints.anchor = GridBagConstraints.LINE_START;
    changePasswordPanel.add( password2label, reg_password2labelConstraints );
    final GridBagConstraints reg_password2FieldConstraints = new GridBagConstraints();
    reg_password2FieldConstraints.gridx = 1;
    reg_password2FieldConstraints.gridy = 1;
    reg_password2FieldConstraints.insets = new Insets( 0, 5, 5, 0 );
    changePasswordPanel.add( password2Field, reg_password2FieldConstraints );
    final GridBagConstraints passwordCompareLabelConstraints = new GridBagConstraints();
    passwordCompareLabelConstraints.gridx = 1;
    passwordCompareLabelConstraints.gridy = 2;
    passwordCompareLabelConstraints.anchor = GridBagConstraints.LINE_START;
    passwordCompareLabelConstraints.insets = new Insets( 0, 5, 5, 0 );
    changePasswordPanel.add( passwordCompareLabel, passwordCompareLabelConstraints );
    final JPanel buttonPanel = new JPanel( new FlowLayout() );
    buttonPanel.add( savePasswordButton );
    buttonPanel.add( pass_abortButton );
    final GridBagConstraints reg_buttonpanelConstraints = new GridBagConstraints();
    reg_buttonpanelConstraints.gridx = 0;
    reg_buttonpanelConstraints.gridy = 3;
    reg_buttonpanelConstraints.gridwidth = 2;
    changePasswordPanel.add( buttonPanel, reg_buttonpanelConstraints );

    final Color green = new Color( 33, 122, 34 );
    final DocumentListener documentListener = new DocumentListener()
    {

      @Override
      public void removeUpdate( final DocumentEvent e )
      {
        compare();
      }

      @Override
      public void insertUpdate( final DocumentEvent e )
      {
        compare();
      }

      @Override
      public void changedUpdate( final DocumentEvent e )
      {
      }

      private void compare()
      {
        if ( passwordField.getPassword() == null
            || passwordField.getPassword().length == 0
            || password2Field.getPassword() == null
            || password2Field.getPassword().length == 0 )
        {
          passwordCompareLabel.setText( "" );
          passwordCompareLabel.setForeground( Color.black );
        }
        else
        {
          if ( String.valueOf( passwordField.getPassword() ).equals( String.valueOf( password2Field.getPassword() ) ) )
          {
            passwordCompareLabel.setText( "Die Passwörter stimmen überein." );
            passwordCompareLabel.setForeground( green );
          }
          else
          {
            passwordCompareLabel.setText( "Die Passwörter stimmen nicht überein." );
            passwordCompareLabel.setForeground( Color.red );
          }
        }
      }
    };
    pass_abortButton.addActionListener( e -> changePasswordFrame.dispose() );
    savePasswordButton.addActionListener( e ->
    {
      final boolean isPasswordOrUserNameIncorrect = passwordField.getPassword() == null || passwordField.getPassword().length == 0
          || password2Field.getPassword() == null || password2Field.getPassword().length == 0;

      if ( isPasswordOrUserNameIncorrect )
      {
        JOptionPane.showMessageDialog( changePasswordFrame, "Passwort ist nicht zulässig.", "Passwort ändern",
            JOptionPane.WARNING_MESSAGE );
      }
      else if ( !String.valueOf( passwordField.getPassword() ).equals( String.valueOf( password2Field.getPassword() ) ) )
      {
        JOptionPane.showMessageDialog( changePasswordFrame, "Die Passwörter stimmen nicht überein.", "Passwort ändern",
            JOptionPane.WARNING_MESSAGE );
      }
      else
      {
        final char[] password = passwordField.getPassword();
        final int reply =
            JOptionPane.showConfirmDialog( changePasswordFrame, "Wollen Sie wirklich das neue Passwort spechern?", "Passwort ändern",
                JOptionPane.INFORMATION_MESSAGE );
        if ( reply == JOptionPane.YES_OPTION )
        {
          ServerCommunication.getInstance().changePassword( Util.getHash( String.valueOf( password ) ) );
          changePasswordFrame.dispose();
        }
      }
    } );

    password2Field.getDocument().addDocumentListener( documentListener );
    passwordField.getDocument().addDocumentListener( documentListener );

    getRootPane().setDefaultButton( savePasswordButton );
    changePasswordFrame.getContentPane().add( changePasswordPanel );
    changePasswordFrame.pack();
    changePasswordFrame.setResizable( false );
    changePasswordFrame.setSize( changePasswordFrame.getWidth() + 30, changePasswordFrame.getHeight() + 20 );
    changePasswordFrame.setLocationRelativeTo( Settings.this );
    changePasswordFrame
        .setIconImage( Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue.png" ) ) );
    changePasswordFrame.setVisible( true );
  }

  private void showDisplayNameChangeDialog()
  {
    final JDialog changeDisplayNameFrame = new JDialog( Mainframe.getInstance(), "Anzeigenamen ändern", true );
    final JPanel changeDisplayNamePanel = new JPanel( new GridBagLayout() );
    final JLabel displayNamelabel = new JLabel( "Anzeigename:" );
    final JTextField displayNameField = MeMateUIManager.createJTextField();
    final JButton saveDisplayNameButton = MeMateUIManager.createButton( "button", "Speichern" );
    final JButton abortDisplayNameButton = MeMateUIManager.createButton( "button", "Abbrechen" );

    changeDisplayNameFrame.getRootPane().setDefaultButton( saveDisplayNameButton );

    final int prefHeight = displayNameField.getPreferredSize().height;
    displayNameField.setPreferredSize( new Dimension( 100, prefHeight ) );

    final GridBagConstraints displayNamelabelConstraints = new GridBagConstraints();
    displayNamelabelConstraints.gridx = 0;
    displayNamelabelConstraints.gridy = 0;
    displayNamelabelConstraints.insets = new Insets( 10, 0, 10, 0 );
    displayNamelabelConstraints.anchor = GridBagConstraints.LINE_START;
    changeDisplayNamePanel.add( displayNamelabel, displayNamelabelConstraints );
    final GridBagConstraints displayNameFieldConstraints = new GridBagConstraints();
    displayNameFieldConstraints.gridx = 1;
    displayNameFieldConstraints.gridy = 0;
    displayNameFieldConstraints.insets = new Insets( 10, 5, 10, 0 );
    changeDisplayNamePanel.add( displayNameField, displayNameFieldConstraints );
    final JPanel buttonPanel = new JPanel( new FlowLayout() );
    buttonPanel.add( saveDisplayNameButton );
    buttonPanel.add( abortDisplayNameButton );
    final GridBagConstraints displayname_buttonpanelConstraints = new GridBagConstraints();
    displayname_buttonpanelConstraints.gridx = 0;
    displayname_buttonpanelConstraints.gridy = 1;
    displayname_buttonpanelConstraints.gridwidth = 2;
    changeDisplayNamePanel.add( buttonPanel, displayname_buttonpanelConstraints );


    abortDisplayNameButton.addActionListener( e -> changeDisplayNameFrame.dispose() );
    saveDisplayNameButton.addActionListener( e ->
    {
      final boolean isDisplayNameIncorrect = displayNameField.getText() == null || displayNameField.getText().length() == 0;

      if ( isDisplayNameIncorrect )
      {
        JOptionPane.showMessageDialog( changeDisplayNameFrame, "Der Anzeigename darf nicht leer sein", "Anzeigenamen ändern",
            JOptionPane.WARNING_MESSAGE );
      }
      else if ( displayNameField.getText().length() > 10 )
      {
        JOptionPane.showMessageDialog( changeDisplayNameFrame, "Der Anzeigename darf nicht länger als 10 Zeichen sein",
            "Anzeigenamen ändern",
            JOptionPane.WARNING_MESSAGE );
      }
      else
      {
        final String displayName = displayNameField.getText();
        final int reply =
            JOptionPane.showConfirmDialog( changeDisplayNameFrame, "Wollen Sie wirklich den neuen Anzeigenamen spechern?",
                "Anzeigenamen ändern",
                JOptionPane.INFORMATION_MESSAGE );
        if ( reply == JOptionPane.YES_OPTION )
        {
          ServerCommunication.getInstance().changeDisplayName( displayName );
          changeDisplayNameFrame.dispose();
        }
      }
    } );


    getRootPane().setDefaultButton( saveDisplayNameButton );
    changeDisplayNameFrame.getContentPane().add( changeDisplayNamePanel );
    changeDisplayNameFrame.pack();
    changeDisplayNameFrame.setResizable( false );
    changeDisplayNameFrame.setSize( changeDisplayNameFrame.getWidth() + 30, changeDisplayNameFrame.getHeight() + 20 );
    changeDisplayNameFrame.setLocationRelativeTo( Settings.this );
    changeDisplayNameFrame
        .setIconImage( Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue.png" ) ) );
    changeDisplayNameFrame.setVisible( true );
  }


  private void addConsumptionNotification()
  {
    final JLabel consumptionNotificationLabel = MeMateUIManager.createJLabel();
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
    final JLabel meetingNotificationLabel = MeMateUIManager.createJLabel();
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
    final JLabel pickDarkmodeLabel = MeMateUIManager.createJLabel();
    pickDarkmodeLabel.setText( "Standard-App-Modus wählen" );
    pickDarkmodeLabel.setFont( pickDarkmodeLabel.getFont().deriveFont( 18f ) );
    final GridBagConstraints pickDarkmodeLabelConstraints = new GridBagConstraints();
    pickDarkmodeLabelConstraints.gridx = 0;
    pickDarkmodeLabelConstraints.gridy = 2;
    pickDarkmodeLabelConstraints.anchor = GridBagConstraints.LINE_START;
    pickDarkmodeLabelConstraints.insets = new Insets( 30, 20, 0, 0 );

    add( pickDarkmodeLabel, pickDarkmodeLabelConstraints );
    final ButtonGroup group = new ButtonGroup();
    group.add( daymodeButton );
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
        Mainframe.getInstance().bar.showDaymode();
      }
    };
    daymodeButton.addActionListener( toggleDarkModeListener );
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
    add( daymodeButton, dayModeButtonConstraints );
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
      daymodeButton.setSelected( true );
    }
  }

  private void addColorThemePicker()
  {
    MeMateUIManager.registerComboBox( colorThemeComboBox );
    final JLabel pickThemeLabel = MeMateUIManager.createJLabel();
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
    Mainframe.getInstance().headerPanel.setBackground( appColor );
    Mainframe.getInstance().settingsButton.selected();
    if ( MeMateUIManager.getDarkModeState() )
    {
      Mainframe.getInstance().bar.setBackground( actionbar );
      Mainframe.getInstance().burgerButton.setBackground( actionbar );
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
