package com.isp.memate;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.isp.memate.Shared.LoginResult;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.Util;

public class ChangePasswordDialog extends JDialog
{
  private static final long serialVersionUID = -7368383844134207306L;
  private JPanel            mainPanel;
  private JLabel            passwordLabel, repeatPasswordLabel, passwordCompareLabel;
  private JPasswordField    passwordField, repeatPasswordField;
  private JButton           saveButton, abortButton;

  public ChangePasswordDialog()
  {
    initComponents();
    addComponents();
    addActionListener();
    addDocumentListener();
    applyFrameSettings();
  }

  private void initComponents()
  {
    mainPanel = new JPanel( new GridBagLayout() );
    passwordLabel = new JLabel( "Passwort:" );
    repeatPasswordLabel = new JLabel( "Passwort wiederholen:" );
    passwordCompareLabel = new JLabel();
    passwordField = new JPasswordField();
    repeatPasswordField = new JPasswordField();
    saveButton = new JButton( "Speichern" );
    abortButton = new JButton( "Abbrechen" );

    final int prefHeight = passwordField.getPreferredSize().height;
    passwordField.setPreferredSize( new Dimension( 220, prefHeight ) );
    repeatPasswordField.setPreferredSize( new Dimension( 220, prefHeight ) );
    passwordCompareLabel.setPreferredSize( new Dimension( 220, prefHeight ) );
  }

  private void addComponents()
  {
    final GridBagConstraints passwordLabelConstraints = new GridBagConstraints();
    passwordLabelConstraints.gridx = 0;
    passwordLabelConstraints.gridy = 0;
    passwordLabelConstraints.insets = new Insets( 10, 0, 10, 0 );
    passwordLabelConstraints.anchor = GridBagConstraints.LINE_START;
    mainPanel.add( passwordLabel, passwordLabelConstraints );
    final GridBagConstraints passwordFieldConstraints = new GridBagConstraints();
    passwordFieldConstraints.gridx = 1;
    passwordFieldConstraints.gridy = 0;
    passwordFieldConstraints.insets = new Insets( 10, 5, 10, 0 );
    mainPanel.add( passwordField, passwordFieldConstraints );
    final GridBagConstraints repeatPasswordLabelConstraints = new GridBagConstraints();
    repeatPasswordLabelConstraints.gridx = 0;
    repeatPasswordLabelConstraints.gridy = 1;
    repeatPasswordLabelConstraints.insets = new Insets( 0, 0, 10, 0 );
    repeatPasswordLabelConstraints.anchor = GridBagConstraints.LINE_START;
    mainPanel.add( repeatPasswordLabel, repeatPasswordLabelConstraints );
    final GridBagConstraints repeatPasswordFieldConstraints = new GridBagConstraints();
    repeatPasswordFieldConstraints.gridx = 1;
    repeatPasswordFieldConstraints.gridy = 1;
    repeatPasswordFieldConstraints.insets = new Insets( 0, 5, 5, 0 );
    mainPanel.add( repeatPasswordField, repeatPasswordFieldConstraints );
    final GridBagConstraints passwordCompareLabelConstraints = new GridBagConstraints();
    passwordCompareLabelConstraints.gridx = 1;
    passwordCompareLabelConstraints.gridy = 2;
    passwordCompareLabelConstraints.anchor = GridBagConstraints.LINE_START;
    passwordCompareLabelConstraints.insets = new Insets( 0, 5, 5, 0 );
    mainPanel.add( passwordCompareLabel, passwordCompareLabelConstraints );
    final JPanel buttonPanel = new JPanel( new FlowLayout() );
    buttonPanel.add( saveButton );
    buttonPanel.add( abortButton );
    final GridBagConstraints buttonpanelConstraints = new GridBagConstraints();
    buttonpanelConstraints.gridx = 0;
    buttonpanelConstraints.gridy = 3;
    buttonpanelConstraints.gridwidth = 2;
    mainPanel.add( buttonPanel, buttonpanelConstraints );
    add( mainPanel );
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

  private void addActionListener()
  {
    abortButton.addActionListener( e ->
    {
      dispose();
      if ( GUIObjects.loginFrame != null )
      {
        GUIObjects.loginFrame.validateLoginResult( LoginResult.LOGIN_SUCCESSFULL_REQUEST_NEW_PASSWORD );
      }
    } );

    saveButton.addActionListener( e ->
    {
      final boolean isPasswordFieldEmpty = passwordField.getPassword() == null
          || passwordField.getPassword().length == 0 || repeatPasswordField.getPassword() == null
          || repeatPasswordField.getPassword().length == 0;

      if ( isPasswordFieldEmpty )
      {
        JOptionPane.showMessageDialog( this, "Beide Passwort-Felder müssen gefüllt sein.",
            "Passwort ändern", JOptionPane.WARNING_MESSAGE );
      }
      else if ( !String.valueOf( passwordField.getPassword() )
          .equals( String.valueOf( repeatPasswordField.getPassword() ) ) )
      {
        JOptionPane.showMessageDialog( this, "Die Passwörter stimmen nicht überein.",
            "Passwort ändern", JOptionPane.WARNING_MESSAGE );
      }
      else
      {
        final char[] password = passwordField.getPassword();
        final int reply = JOptionPane.showConfirmDialog( this,
            "Wollen Sie wirklich das neue Passwort speichern?", "Passwort ändern",
            JOptionPane.INFORMATION_MESSAGE );
        if ( reply == JOptionPane.YES_OPTION )
        {
          ServerCommunication.getInstance().changePassword( Util.getHash( String.valueOf( password ) ) );
          if ( GUIObjects.loginFrame != null )
          {
            ServerCommunication.getInstance().startDrinkInfoTimer();
            GUIObjects.loginFrame.newPasswordHasBeenSet();
          }
          dispose();
        }
      }
    } );
  }

  private void compare()
  {
    if ( passwordField.getPassword() == null || passwordField.getPassword().length == 0
        || repeatPasswordField.getPassword() == null || repeatPasswordField.getPassword().length == 0 )
    {
      passwordCompareLabel.setText( "" );
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
    setTitle( "Passwort ändern" );
    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    getRootPane().setDefaultButton( saveButton );
    setModal( true );
    pack();
    setResizable( false );
    setSize( getWidth() + 30, getHeight() + 20 );
    setLocationRelativeTo( GUIObjects.loginFrame );
    setIconImage( Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue.png" ) ) );
    setVisible( true );
  }
}