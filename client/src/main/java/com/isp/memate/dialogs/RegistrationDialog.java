package com.isp.memate.dialogs;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.isp.memate.ServerCommunication;
import com.isp.memate.components.MeMateDialog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.Util;

public class RegistrationDialog extends MeMateDialog
{
  private JPanel         mainPanel;
  private JTextField     usernameTextField;
  private JLabel         usernameLabel, passwordLabel, repeatPasswordLabel, passwordCompareLabel;
  private JPasswordField passwordField, repeatPasswordField;
  private JButton        registrationButton, abortButton;

  public RegistrationDialog()
  {
    GUIObjects.registrationFrame = this;
    initComponents();
    addComponents();
    addActionListener();
    addDocumentListener();
    applyFrameSettings();
  }

  private void initComponents()
  {
    mainPanel = new JPanel( new GridBagLayout() );
    usernameLabel = new JLabel( "Benutzername:" );
    passwordLabel = new JLabel( "Passwort:" );
    repeatPasswordLabel = new JLabel( "Passwort wiederholen:" );
    passwordCompareLabel = new JLabel();
    usernameTextField = new JTextField();
    passwordField = new JPasswordField();
    repeatPasswordField = new JPasswordField();
    registrationButton = new JButton( "Registrieren" );
    abortButton = new JButton( "Abbrechen" );

    final int prefHeight = usernameTextField.getPreferredSize().height;
    usernameTextField.setPreferredSize( new Dimension( 220, prefHeight ) );
    passwordField.setPreferredSize( new Dimension( 220, prefHeight ) );
    repeatPasswordField.setPreferredSize( new Dimension( 220, prefHeight ) );
    passwordCompareLabel.setPreferredSize( new Dimension( 220, prefHeight ) );
  }

  private void addComponents()
  {
    final GridBagConstraints usernameLabelConstraints = new GridBagConstraints();
    usernameLabelConstraints.gridx = 0;
    usernameLabelConstraints.gridy = 0;
    usernameLabelConstraints.anchor = GridBagConstraints.LINE_START;
    mainPanel.add( usernameLabel, usernameLabelConstraints );
    final GridBagConstraints usernameTextFieldConstraints = new GridBagConstraints();
    usernameTextFieldConstraints.gridx = 1;
    usernameTextFieldConstraints.gridy = 0;
    usernameTextFieldConstraints.insets = new Insets( 0, 5, 0, 0 );
    mainPanel.add( usernameTextField, usernameTextFieldConstraints );
    final GridBagConstraints passwordLabelConstraints = new GridBagConstraints();
    passwordLabelConstraints.gridx = 0;
    passwordLabelConstraints.gridy = 1;
    passwordLabelConstraints.insets = new Insets( 10, 0, 10, 0 );
    passwordLabelConstraints.anchor = GridBagConstraints.LINE_START;
    mainPanel.add( passwordLabel, passwordLabelConstraints );
    final GridBagConstraints passwordFieldConstraints = new GridBagConstraints();
    passwordFieldConstraints.gridx = 1;
    passwordFieldConstraints.gridy = 1;
    passwordFieldConstraints.insets = new Insets( 10, 5, 10, 0 );
    mainPanel.add( passwordField, passwordFieldConstraints );
    final GridBagConstraints repeatPasswordLabelConstraints = new GridBagConstraints();
    repeatPasswordLabelConstraints.gridx = 0;
    repeatPasswordLabelConstraints.gridy = 2;
    repeatPasswordLabelConstraints.insets = new Insets( 0, 0, 10, 0 );
    repeatPasswordLabelConstraints.anchor = GridBagConstraints.LINE_START;
    mainPanel.add( repeatPasswordLabel, repeatPasswordLabelConstraints );
    final GridBagConstraints repeatPasswordFieldConstraints = new GridBagConstraints();
    repeatPasswordFieldConstraints.gridx = 1;
    repeatPasswordFieldConstraints.gridy = 2;
    repeatPasswordFieldConstraints.insets = new Insets( 0, 5, 5, 0 );
    mainPanel.add( repeatPasswordField, repeatPasswordFieldConstraints );
    final GridBagConstraints passwordCompareLabelConstraints = new GridBagConstraints();
    passwordCompareLabelConstraints.gridx = 1;
    passwordCompareLabelConstraints.gridy = 3;
    passwordCompareLabelConstraints.anchor = GridBagConstraints.LINE_START;
    passwordCompareLabelConstraints.insets = new Insets( 0, 5, 5, 0 );
    mainPanel.add( passwordCompareLabel, passwordCompareLabelConstraints );
    final JPanel buttonPanel = new JPanel( new FlowLayout() );
    buttonPanel.add( registrationButton );
    buttonPanel.add( abortButton );
    final GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
    buttonPanelConstraints.gridx = 0;
    buttonPanelConstraints.gridy = 4;
    buttonPanelConstraints.gridwidth = 2;
    mainPanel.add( buttonPanel, buttonPanelConstraints );
    add( mainPanel );
  }

  private void addActionListener()
  {
    abortButton.addActionListener( e ->
    {
      GUIObjects.registrationFrame = null;
      dispose();
    } );

    registrationButton.addActionListener( e ->
    {
      final boolean isPasswordOrUserNameIncorrect = passwordField.getPassword() == null
          || passwordField.getPassword().length == 0
          || repeatPasswordField.getPassword() == null
          || repeatPasswordField.getPassword().length == 0
          || usernameTextField.getText() == null || usernameTextField.getText().isEmpty()
          || usernameTextField.getText().trim().length() == 0;

      if ( isPasswordOrUserNameIncorrect )
      {
        JOptionPane.showMessageDialog( this,
            "Passwort oder Benutzername sind nicht zulässig.", "Registrieren",
            JOptionPane.WARNING_MESSAGE );
      }
      else if ( !String.valueOf( passwordField.getPassword() )
          .equals( String.valueOf( repeatPasswordField.getPassword() ) ) )
      {
        JOptionPane.showMessageDialog( this, "Die Passwörter stimmen nicht überein.",
            "Registrieren", JOptionPane.WARNING_MESSAGE );
      }
      else
      {
        final String username = usernameTextField.getText();
        final char[] password = passwordField.getPassword();
        final int reply = JOptionPane.showConfirmDialog( this,
            "Wollen Sie wirklich einen neuen Benutzer anlegen?", "Registrieren",
            JOptionPane.INFORMATION_MESSAGE );
        if ( reply == JOptionPane.YES_OPTION )
        {
          ServerCommunication.getInstance().registerNewUser( username,
              Util.getHash( String.valueOf( password ) ) );
        }
      }
    } );
  }

  private void addDocumentListener()
  {
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
    };
    passwordField.getDocument().addDocumentListener( documentListener );
    repeatPasswordField.getDocument().addDocumentListener( documentListener );
  }

  private void compare()
  {
    if ( passwordField.getPassword() == null || passwordField.getPassword().length == 0
        || repeatPasswordField.getPassword() == null
        || repeatPasswordField.getPassword().length == 0 )
    {
      passwordCompareLabel.setText( "" );
      passwordCompareLabel.setForeground( UIManager.getColor( "Label.foreground" ) );
    }
    else
    {
      if ( String.valueOf( passwordField.getPassword() )
          .equals( String.valueOf( repeatPasswordField.getPassword() ) ) )
      {
        passwordCompareLabel.setText( "Die Passwörter stimmen überein." );
        passwordCompareLabel.setForeground( UIManager.getColor( "Actions.Green" ) );
      }
      else
      {
        passwordCompareLabel.setText( "Die Passwörter stimmen nicht überein." );
        passwordCompareLabel.setForeground( UIManager.getColor( "Actions.Red" ) );
      }
    }
  }

  private void applyFrameSettings()
  {
    setTitle( "Konto erstellen" );
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    getRootPane().setDefaultButton( registrationButton );
    pack();
    setResizable( false );
    setSize( getPreferredSize().width + 40, getPreferredSize().height + 20 );
    setLocationRelativeTo( GUIObjects.loginFrame );
    setIconImage( GUIObjects.loginFrame.getIconImage() );
    setVisible( true );
  }

  /**
   * Wenn bei der Registrierung ein Fehler aufgetreten ist, so wird dieser dem
   * Nutzer angezeigt.
   *
   * @param registrationResult Die Antwort des Servers auf die Registrierung.
   */
  public void validateRegistartionResult( final String registrationResult )
  {
    if ( !registrationResult.equals( "Registrierung erfolgreich." ) )
    {
      JOptionPane.showMessageDialog( this, registrationResult, "Registrierung fehlgeschlagen", JOptionPane.ERROR_MESSAGE, null );
    }
    else
    {
      JOptionPane.showMessageDialog( this, "Registrierung erfolgreich!", "Registrierung erfolgreich", JOptionPane.INFORMATION_MESSAGE,
          null );
      GUIObjects.registrationFrame = null;
      dispose();
    }
  }
}