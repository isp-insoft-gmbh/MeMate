/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.isp.memate.ServerCommunication;
import com.isp.memate.components.ToggleSwitch;
import com.isp.memate.dialogs.ChangePasswordDialog;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.PropertyHelper;

/**
 * Auf dem Settings Panel kann sein gewünschtes Color Scheme auswählen, den Darkmodestate verändern und
 * Benachrichtigungen für Standup-Meeting oder andere Benachrichtigungen aktivieren und deaktivieren.
 *
 * @author nwe
 * @since 05.03.2020
 */
public class Settings extends JPanel
{
  private JRadioButton lightmodeButton;
  private JRadioButton darkmodeButton;

  public Settings()
  {
    GUIObjects.currentPanel = this;
    initComponents();
    setLayout( new GridBagLayout() );
    addComponents();
  }

  private void initComponents()
  {
    lightmodeButton = new JRadioButton( "Hell" );
    darkmodeButton = new JRadioButton( "Dunkel" );
  }

  private void addComponents()
  {
    addColorChooser();
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
    hyperlink.setText( "<html><u>Passwort ändern</u></html>" );
    hyperlink.setFont( hyperlink.getFont().deriveFont( 18f ) );
    hyperlink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    hyperlink.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent e )
      {
        new ChangePasswordDialog( GUIObjects.mainframe );
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
    hyperlink.setText( "<html><u>Anzeigenamen ändern</u></html>" );
    hyperlink.setFont( hyperlink.getFont().deriveFont( 18f ) );
    hyperlink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    hyperlink.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent e )
      {
        showDisplayNameChangeDialog();
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
      if ( username == null || username.trim().isEmpty() )
      {
        JOptionPane.showMessageDialog( this, "Der Anzeigename darf nicht leer sein.", title, JOptionPane.WARNING_MESSAGE );
        showDisplayNameChangeDialog();
      }
      else if ( username.length() > 10 )
      {
        //TODO(nwe | 08.04.2021): why ?
        JOptionPane.showMessageDialog( this, "Der Anzeigename darf nicht länger als 10 Zeichen sein.", title,
            JOptionPane.WARNING_MESSAGE );
        showDisplayNameChangeDialog();
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
      public void mouseReleased( final MouseEvent __ )
      {
        consumptionSwitch.activated = !consumptionSwitch.activated;
        consumptionSwitch.repaint();
        PropertyHelper.setProperty( "ConsumptionNotification", consumptionSwitch.activated ? "true" : "false" );
      }
    } );
    consumptionSwitch.setActivated( PropertyHelper.getBooleanProperty( "ConsumptionNotification" ) );
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
      public void mouseReleased( final MouseEvent __ )
      {
        meetingSwitch.activated = !meetingSwitch.activated;
        meetingSwitch.repaint();
        PropertyHelper.setProperty( "MeetingNotification", meetingSwitch.activated ? "true" : "false" );
      }
    } );
      meetingSwitch.setActivated( PropertyHelper.getBooleanProperty( "MeetingNotification" ) );
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
        PropertyHelper.setProperty( "Darkmode", "true" );
        GUIObjects.mainframe.bar.showDarkmode();
      }
      else
      {
        if ( !MeMateUIManager.getDarkModeState() )
        {
          return;
        }
        MeMateUIManager.showDayMode();
        PropertyHelper.setProperty( "Darkmode", "false" );
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
    if ( PropertyHelper.getDarkModeProperty() )
    {
      darkmodeButton.setSelected( true );
    }
    else
    {
      lightmodeButton.setSelected( true );
    }
  }

  private void addColorChooser()
  {
    final JLabel pickThemeLabel = new JLabel();
    pickThemeLabel.setText( "Akzentfarbe auswählen" );
    pickThemeLabel.setFont( pickThemeLabel.getFont().deriveFont( 18f ) );
    final GridBagConstraints pickThemeLabelConstraints = new GridBagConstraints();
    pickThemeLabelConstraints.gridx = 0;
    pickThemeLabelConstraints.gridy = 0;
    pickThemeLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
    pickThemeLabelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
    pickThemeLabelConstraints.insets = new Insets( 10, 20, 0, 0 );
    add( pickThemeLabel, pickThemeLabelConstraints );

    final GridBagConstraints colorThemeComboBoxConstraints = new GridBagConstraints();
    colorThemeComboBoxConstraints.gridx = 0;
    colorThemeComboBoxConstraints.gridy = 1;
    colorThemeComboBoxConstraints.gridwidth = 1;
    colorThemeComboBoxConstraints.ipadx = 150;
    colorThemeComboBoxConstraints.ipady = 10;
    colorThemeComboBoxConstraints.insets = new Insets( 5, 20, 0, 0 );
    JButton colorButton = new JButton( "Farbe auswählen" );
    add( colorButton, colorThemeComboBoxConstraints );
    colorButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        JColorChooser colorChooser = new JColorChooser();
        colorChooser.setColor( PropertyHelper.getAppColorProperty() );
        int choice = JOptionPane.showConfirmDialog( Settings.this, colorChooser, "Akzentfarbe auswählen", JOptionPane.YES_NO_OPTION,
            JOptionPane.DEFAULT_OPTION, null );
        if ( JOptionPane.YES_OPTION == choice )
        {
          setAppColor( colorChooser.getColor() );
        }
      }
    } );
  }

  private void setAppColor( final Color appColor )
  {
    UIManager.put( "AppColor", appColor );
    GUIObjects.mainframe.headerPanel.setBackground( appColor );
    GUIObjects.mainframe.settingsButton.selected();
    GUIObjects.mainframe.bar.setBackground( UIManager.getColor( "Panel.background" ) );
    GUIObjects.mainframe.burgerButton.setBackground( UIManager.getColor( "Panel.background" ) );
    PropertyHelper.setProperty( "AppColor", String.valueOf( appColor.getRGB() ) );
    Settings.this.repaint();
  }
}
