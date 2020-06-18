/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;

import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.isp.memate.Shared.LoginResult;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.SwingUtil;

/**
 * Im LoginFrame kann der Benutzer sich registrieren oder wenn er bereits ein
 * Konto besitzt, sich einloggen. Außerdem besteht die Möglichkeit angemeldet zu
 * bleiben.
 *
 * @author nwe
 * @since 15.10.2019
 */
public class Login extends JFrame
{
  private static final Login   instance                = new Login();
  private final Font           LABEL_FONT              = UIManager.getFont( "Label.font" ).deriveFont( 15f );
  private final JButton        loginButton             = MeMateUIManager.createButton( "button" );
  private final JLabel         headerLabel             = new JLabel(
      new ImageIcon( getClass().getClassLoader().getResource( "welcome.png" ) ) );
  private final JCheckBox      stayLoggedInCheckBox    = MeMateUIManager.createCheckbox();
  private final JTextPane      registerHyperLink       = MeMateUIManager.createTextPane();
  private final JTextPane      forgotPasswordHyperLink = MeMateUIManager.createTextPane();
  private final JPanel         loginPanel              = MeMateUIManager.createJPanel();
  private final JLabel         usernameLabel           = MeMateUIManager.createJLabel();
  private final JLabel         passwordLabel           = MeMateUIManager.createJLabel();
  private final JLabel         stayLoggedInLabel       = MeMateUIManager.createJLabel();
  private final JPasswordField passwordField           = MeMateUIManager.createJPasswordField();
  private final JTextField     usernameTextField       = MeMateUIManager.createJTextField();
  private static String        currentUsername;

  /**
   * @return static instance of Login
   */
  public static Login getInstance()
  {
    return instance;
  }

  /**
   * Passt Schriftgrößen, Borders und Größen an. Außerdem werden die Komponenten
   * gelayoutet und ActionListener für Login und Registrieren werden hinzugefügt.
   */
  public Login()
  {
    loginPanel.setLayout( new GridBagLayout() );
    loginButton.setText( "Anmelden" );
    passwordLabel.setText( "Passwort" );
    usernameLabel.setText( "Benutzername" );
    stayLoggedInLabel.setText( "Eingeloggt bleiben" );
    setIconImage(
        Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue.png" ) ) );
    setTitle( "MeMate" );
    deriveFonts();
    setBordersAndPreferredSize();
    layoutComponents();
    addActionListener();

    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    pack();
    setResizable( false );
    setLocationRelativeTo( null );
  }

  /**
   * Enthält die Logik, für das Einloggen und Registrieren von Nutzern.
   */
  private void addActionListener()
  {
    loginButton.addActionListener( __ ->
    {
      if ( usernameTextField.getText().isEmpty() )
      {
        JOptionPane.showMessageDialog( loginPanel, "Benutzername darf nicht leer sein.", "Login fehlgeschlagen",
            JOptionPane.WARNING_MESSAGE, null );
        return;
      }
      else if ( passwordField.getPassword().length == 0 )
      {
        JOptionPane.showMessageDialog( loginPanel, "Passwort darf nicht leer sein.", "Login fehlgeschlagen",
            JOptionPane.WARNING_MESSAGE, null );
        return;
      }
      else
      {
        currentUsername = usernameTextField.getText();
        final LoginInformation login = new LoginInformation( usernameTextField.getText(),
            getHash( String.valueOf( passwordField.getPassword() ) ) );
        ServerCommunication.getInstance().checkLogin( login );
      }
    } );
    getRootPane().setDefaultButton( loginButton );

    forgotPasswordHyperLink.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent e )
      {
        JOptionPane.showMessageDialog( Login.this, "Bitte kontaktieren Sie ihren Admin.", "Passwort vergessen",
            JOptionPane.WARNING_MESSAGE, null );
      }
    } );

    registerHyperLink.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent e )
      {
        final JFrame registrationFrame = new JFrame( "Konto erstellen" );
        final JPanel registrationPanel = new JPanel( new GridBagLayout() );
        final JLabel reg_usernamelabel = new JLabel( "Benutzername:" );
        final JLabel reg_passwordlabel = new JLabel( "Passwort:" );
        final JLabel reg_password2label = new JLabel( "Passwort wiederholen:" );
        final JLabel passwordCompareLabel = new JLabel();
        final JTextField reg_usernameTextField = MeMateUIManager.createJTextField();
        final JPasswordField reg_passwordField = MeMateUIManager.createJPasswordField();
        final JPasswordField reg_password2Field = MeMateUIManager.createJPasswordField();
        final JButton reg_registrationButton = MeMateUIManager.createButton( "button", "Registrieren" );
        final JButton reg_abortButton = MeMateUIManager.createButton( "button", "Abbrechen" );

        registrationFrame.getRootPane().setDefaultButton( reg_registrationButton );

        final int prefHeight = reg_usernameTextField.getPreferredSize().height;
        reg_usernameTextField.setPreferredSize( new Dimension( 200, prefHeight ) );
        reg_passwordField.setPreferredSize( new Dimension( 200, prefHeight ) );
        reg_password2Field.setPreferredSize( new Dimension( 200, prefHeight ) );
        passwordCompareLabel.setPreferredSize( new Dimension( 200, prefHeight ) );

        final GridBagConstraints reg_usernameLabelConstraints = new GridBagConstraints();
        reg_usernameLabelConstraints.gridx = 0;
        reg_usernameLabelConstraints.gridy = 0;
        reg_usernameLabelConstraints.anchor = GridBagConstraints.LINE_START;
        registrationPanel.add( reg_usernamelabel, reg_usernameLabelConstraints );
        final GridBagConstraints reg_usernameTextFieldConstraints = new GridBagConstraints();
        reg_usernameTextFieldConstraints.gridx = 1;
        reg_usernameTextFieldConstraints.gridy = 0;
        reg_usernameTextFieldConstraints.insets = new Insets( 0, 5, 0, 0 );
        registrationPanel.add( reg_usernameTextField, reg_usernameTextFieldConstraints );
        final GridBagConstraints reg_passwordlabelConstraints = new GridBagConstraints();
        reg_passwordlabelConstraints.gridx = 0;
        reg_passwordlabelConstraints.gridy = 1;
        reg_passwordlabelConstraints.insets = new Insets( 10, 0, 10, 0 );
        reg_passwordlabelConstraints.anchor = GridBagConstraints.LINE_START;
        registrationPanel.add( reg_passwordlabel, reg_passwordlabelConstraints );
        final GridBagConstraints reg_passwordFieldConstraints = new GridBagConstraints();
        reg_passwordFieldConstraints.gridx = 1;
        reg_passwordFieldConstraints.gridy = 1;
        reg_passwordFieldConstraints.insets = new Insets( 10, 5, 10, 0 );
        registrationPanel.add( reg_passwordField, reg_passwordFieldConstraints );
        final GridBagConstraints reg_password2labelConstraints = new GridBagConstraints();
        reg_password2labelConstraints.gridx = 0;
        reg_password2labelConstraints.gridy = 2;
        reg_password2labelConstraints.insets = new Insets( 0, 0, 10, 0 );
        reg_password2labelConstraints.anchor = GridBagConstraints.LINE_START;
        registrationPanel.add( reg_password2label, reg_password2labelConstraints );
        final GridBagConstraints reg_password2FieldConstraints = new GridBagConstraints();
        reg_password2FieldConstraints.gridx = 1;
        reg_password2FieldConstraints.gridy = 2;
        reg_password2FieldConstraints.insets = new Insets( 0, 5, 5, 0 );
        registrationPanel.add( reg_password2Field, reg_password2FieldConstraints );
        final GridBagConstraints passwordCompareLabelConstraints = new GridBagConstraints();
        passwordCompareLabelConstraints.gridx = 1;
        passwordCompareLabelConstraints.gridy = 3;
        passwordCompareLabelConstraints.anchor = GridBagConstraints.LINE_START;
        passwordCompareLabelConstraints.insets = new Insets( 0, 5, 5, 0 );
        registrationPanel.add( passwordCompareLabel, passwordCompareLabelConstraints );
        final JPanel buttonPanel = new JPanel( new FlowLayout() );
        buttonPanel.add( reg_registrationButton );
        buttonPanel.add( reg_abortButton );
        final GridBagConstraints reg_buttonpanelConstraints = new GridBagConstraints();
        reg_buttonpanelConstraints.gridx = 0;
        reg_buttonpanelConstraints.gridy = 4;
        reg_buttonpanelConstraints.gridwidth = 2;
        registrationPanel.add( buttonPanel, reg_buttonpanelConstraints );

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
            if ( reg_passwordField.getPassword() == null || reg_passwordField.getPassword().length == 0
                || reg_password2Field.getPassword() == null
                || reg_password2Field.getPassword().length == 0 )
            {
              passwordCompareLabel.setText( "" );
              passwordCompareLabel.setForeground( Color.black );
            }
            else
            {
              if ( String.valueOf( reg_passwordField.getPassword() )
                  .equals( String.valueOf( reg_password2Field.getPassword() ) ) )
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
        reg_password2Field.getDocument().addDocumentListener( documentListener );
        reg_passwordField.getDocument().addDocumentListener( documentListener );

        getRootPane().setDefaultButton( reg_registrationButton );
        registrationFrame.add( registrationPanel );
        registrationFrame.pack();
        registrationFrame.setResizable( false );
        registrationFrame.setSize( registrationFrame.getWidth() + 30, registrationFrame.getHeight() + 20 );
        registrationFrame.setLocationRelativeTo( Login.this );
        registrationFrame.setIconImage( Toolkit.getDefaultToolkit()
            .getImage( getClass().getClassLoader().getResource( "frameiconblue.png" ) ) );
        registrationFrame.setVisible( true );

        reg_abortButton.addActionListener( e1 ->
        {
          registrationFrame.dispose();
          getRootPane().setDefaultButton( loginButton );
        } );

        reg_registrationButton.addActionListener( e1 ->
        {
          final boolean isPasswordOrUserNameIncorrect = reg_passwordField.getPassword() == null
              || reg_passwordField.getPassword().length == 0
              || reg_password2Field.getPassword() == null
              || reg_password2Field.getPassword().length == 0
              || reg_usernameTextField.getText() == null || reg_usernameTextField.getText().isEmpty()
              || reg_usernameTextField.getText().trim().length() == 0;

          if ( isPasswordOrUserNameIncorrect )
          {
            JOptionPane.showMessageDialog( registrationFrame,
                "Passwort oder Benutzername sind nicht zulässig.", "Registrieren",
                JOptionPane.WARNING_MESSAGE );
          }
          else if ( !String.valueOf( reg_passwordField.getPassword() )
              .equals( String.valueOf( reg_password2Field.getPassword() ) ) )
          {
            JOptionPane.showMessageDialog( registrationFrame, "Die Passwörter stimmen nicht überein.",
                "Registrieren", JOptionPane.WARNING_MESSAGE );
          }
          else
          {
            final String username = reg_usernameTextField.getText();
            final char[] password = reg_passwordField.getPassword();
            final int reply = JOptionPane.showConfirmDialog( registrationFrame,
                "Wollen Sie wirklich einen neuen Benutzer anlegen?", "Registrieren",
                JOptionPane.INFORMATION_MESSAGE );
            if ( reply == JOptionPane.YES_OPTION )
            {
              ServerCommunication.getInstance().registerNewUser( username,
                  getHash( String.valueOf( password ) ) );
              registrationFrame.dispose();
              getRootPane().setDefaultButton( loginButton );
              currentUsername = username;
              final LoginInformation login = new LoginInformation( reg_usernameTextField.getText(),
                  getHash( String.valueOf( reg_passwordField.getPassword() ) ) );
              ServerCommunication.getInstance().checkLogin( login );
            }
          }
        } );
      }
    } );
  }

  /**
   * Das eingebene Passwort wird gehasht.
   *
   * @param input eingebenes Passwort
   * @return gehashtes Passwort
   */
  String getHash( final String input )
  {
    try
    {
      final MessageDigest passwordHasher = MessageDigest.getInstance( "SHA-256" );
      final byte[] hashedPasswordArray = passwordHasher.digest( input.getBytes( StandardCharsets.UTF_8 ) );
      final StringBuilder asString = new StringBuilder( hashedPasswordArray.length * 2 );
      for ( final byte b : hashedPasswordArray )
      {
        asString.append( Integer.toHexString( b & 0xff ) );
      }
      return asString.toString().toUpperCase();
    }
    catch ( final NoSuchAlgorithmException exception )
    {
      throw new RuntimeException( exception );
    }
  }

  private void layoutComponents()
  {
    final GridBagConstraints titleConstraints = new GridBagConstraints();
    titleConstraints.gridx = 0;
    titleConstraints.gridy = 0;
    titleConstraints.gridwidth = 6;
    loginPanel.add( headerLabel, titleConstraints );

    final GridBagConstraints usernameConstraints = new GridBagConstraints();
    usernameConstraints.gridx = 0;
    usernameConstraints.gridy = 1;
    usernameConstraints.anchor = GridBagConstraints.LINE_START;
    loginPanel.add( usernameLabel, usernameConstraints );
    final GridBagConstraints usernameConstraints2 = new GridBagConstraints();
    usernameConstraints2.gridx = 2;
    usernameConstraints2.gridy = 1;
    usernameConstraints2.gridwidth = 4;
    usernameConstraints2.insets = new Insets( 0, 10, 5, 0 );
    loginPanel.add( usernameTextField, usernameConstraints2 );

    final GridBagConstraints passwordConstraints = new GridBagConstraints();
    passwordConstraints.gridx = 0;
    passwordConstraints.gridy = 2;
    passwordConstraints.anchor = GridBagConstraints.LINE_START;
    loginPanel.add( passwordLabel, passwordConstraints );
    final GridBagConstraints passwordConstraints2 = new GridBagConstraints();
    passwordConstraints2.gridx = 2;
    passwordConstraints2.gridy = 2;
    passwordConstraints2.gridwidth = 4;
    passwordConstraints2.insets = new Insets( 0, 10, 5, 0 );
    loginPanel.add( passwordField, passwordConstraints2 );

    final GridBagConstraints stayLoggedInConstraints = new GridBagConstraints();
    stayLoggedInConstraints.gridx = 0;
    stayLoggedInConstraints.gridy = 3;
    stayLoggedInConstraints.anchor = GridBagConstraints.LINE_START;
    loginPanel.add( stayLoggedInLabel, stayLoggedInConstraints );

    final GridBagConstraints checkboxConstraints = new GridBagConstraints();
    checkboxConstraints.gridx = 2;
    checkboxConstraints.gridy = 3;
    checkboxConstraints.insets = new Insets( 0, 8, 0, 0 );
    loginPanel.add( stayLoggedInCheckBox, checkboxConstraints );

    final GridBagConstraints loginButtonConstraints = new GridBagConstraints();
    loginButtonConstraints.gridx = 0;
    loginButtonConstraints.gridy = 4;
    loginButtonConstraints.gridwidth = 6;
    loginButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
    loginButtonConstraints.insets = new Insets( 5, 0, 10, 0 );
    loginPanel.add( loginButton, loginButtonConstraints );

    final GridBagConstraints registrierenHyperlinkConstraints = new GridBagConstraints();
    registrierenHyperlinkConstraints.gridx = 5;
    registrierenHyperlinkConstraints.gridy = 6;
    registrierenHyperlinkConstraints.fill = GridBagConstraints.HORIZONTAL;
    registrierenHyperlinkConstraints.insets = new Insets( 0, 85, 0, 0 );
    loginPanel.add( registerHyperLink, registrierenHyperlinkConstraints );

    final GridBagConstraints forgotPasswordHyperlinkConstraints = new GridBagConstraints();
    forgotPasswordHyperlinkConstraints.gridx = 5;
    forgotPasswordHyperlinkConstraints.gridy = 3;
    forgotPasswordHyperlinkConstraints.anchor = GridBagConstraints.LINE_END;
    loginPanel.add( forgotPasswordHyperLink, forgotPasswordHyperlinkConstraints );

    final GridBagConstraints seperatorConstraints = new GridBagConstraints();
    seperatorConstraints.gridx = 0;
    seperatorConstraints.gridy = 5;
    seperatorConstraints.gridwidth = 6;
    seperatorConstraints.fill = GridBagConstraints.HORIZONTAL;
    loginPanel.add( new JSeparator( SwingConstants.HORIZONTAL ), seperatorConstraints );

    add( loginPanel );
    MeMateUIManager.registerPanel( "default", loginPanel );
  }

  private void setBordersAndPreferredSize()
  {
    loginPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
    headerLabel.setBorder( new EmptyBorder( 0, 0, 15, 0 ) );
    SwingUtil.setPreferredWidth( 420, usernameTextField );
    SwingUtil.setPreferredWidth( 420, passwordField );
  }

  private void deriveFonts()
  {
    final String fontName = LABEL_FONT.getFontName();
    usernameLabel.setFont( LABEL_FONT );
    passwordLabel.setFont( LABEL_FONT );
    stayLoggedInLabel.setFont( LABEL_FONT );
    registerHyperLink.setEditable( false );
    registerHyperLink.setContentType( "text/html" );
    registerHyperLink.setText( "<html><font color=blue><font face='" + fontName
        + "'><a href>Konto erstellen</a></font></font></html>" );
    registerHyperLink.setBackground( loginPanel.getBackground() );
    forgotPasswordHyperLink.setEditable( false );
    forgotPasswordHyperLink.setContentType( "text/html" );
    forgotPasswordHyperLink.setText( "<html><font color=blue><font face='" + fontName
        + "'><a href>Passwort vergessen ?</a></font></font></html>" );
    forgotPasswordHyperLink.setBackground( loginPanel.getBackground() );
  }

  /**
   * Der Antwort des Servers auf die Loginabfrage wird validiert.
   *
   * @param loginResult Antwort des Servers
   */
  void validateLoginResult( final LoginResult loginResult )
  {
    if ( loginResult == LoginResult.LOGIN_SUCCESSFULL )
    {
      ServerCommunication.getInstance().startDrinkInfoTimer();
      ServerCommunication.getInstance().updateCurrentUser( currentUsername );
      generateSessionID( currentUsername );
      dispose();
      final Mainframe mainframe = Mainframe.getInstance();
      mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
      mainframe.addActionBar();
      mainframe.setVisible( true );
      ServerCommunication.getInstance().tellServerToSendDrinkInformations();
      ServerCommunication.getInstance().getBalance();
      ServerCommunication.getInstance().tellServerToSendHistoryData();
      mainframe.requestFocus();
    }
    else if ( loginResult == LoginResult.LOGIN_SUCCESSFULL_REQUEST_NEW_PASSWORD )
    {
      final JFrame changePasswordFrame = new JFrame( "Passwort ändern" );
      final JPanel changePasswordPanel = new JPanel( new GridBagLayout() );
      final JLabel passwordlabel = new JLabel( "Passwort:" );
      final JLabel password2label = new JLabel( "Passwort wiederholen:" );
      final JLabel passwordCompareLabel = new JLabel();
      final JPasswordField passwordField = new JPasswordField();
      final JPasswordField password2Field = new JPasswordField();
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
          if ( passwordField.getPassword() == null || passwordField.getPassword().length == 0
              || password2Field.getPassword() == null || password2Field.getPassword().length == 0 )
          {
            passwordCompareLabel.setText( "" );
            passwordCompareLabel.setForeground( Color.black );
          }
          else
          {
            if ( String.valueOf( passwordField.getPassword() )
                .equals( String.valueOf( password2Field.getPassword() ) ) )
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
      pass_abortButton.addActionListener( e ->
      {
        changePasswordFrame.dispose();
        getRootPane().setDefaultButton( loginButton );
        validateLoginResult( LoginResult.LOGIN_SUCCESSFULL_REQUEST_NEW_PASSWORD );
      } );
      savePasswordButton.addActionListener( e ->
      {
        final boolean isPasswordOrUserNameIncorrect = passwordField.getPassword() == null
            || passwordField.getPassword().length == 0 || password2Field.getPassword() == null
            || password2Field.getPassword().length == 0;

        if ( isPasswordOrUserNameIncorrect )
        {
          JOptionPane.showMessageDialog( changePasswordFrame, "Passwort ist nicht zulässig.",
              "Passwort ändern", JOptionPane.WARNING_MESSAGE );
        }
        else if ( !String.valueOf( passwordField.getPassword() )
            .equals( String.valueOf( password2Field.getPassword() ) ) )
        {
          JOptionPane.showMessageDialog( changePasswordFrame, "Die Passwörter stimmen nicht überein.",
              "Passwort ändern", JOptionPane.WARNING_MESSAGE );
        }
        else
        {
          final char[] password = passwordField.getPassword();
          final int reply = JOptionPane.showConfirmDialog( changePasswordFrame,
              "Wollen Sie wirklich das neue Passwort spechern?", "Passwort ändern",
              JOptionPane.INFORMATION_MESSAGE );
          if ( reply == JOptionPane.YES_OPTION )
          {
            ServerCommunication.getInstance().changePassword( getHash( String.valueOf( password ) ) );
            ServerCommunication.getInstance().startDrinkInfoTimer();
            ServerCommunication.getInstance().updateCurrentUser( currentUsername );
            generateSessionID( currentUsername );
            changePasswordFrame.dispose();
            dispose();
            final Mainframe mainframe = Mainframe.getInstance();
            mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
            mainframe.setVisible( true );
            ServerCommunication.getInstance().tellServerToSendDrinkInformations();
            ServerCommunication.getInstance().getBalance();
            ServerCommunication.getInstance().tellServerToSendHistoryData();
            mainframe.requestFocus();
          }
        }
      } );

      password2Field.getDocument().addDocumentListener( documentListener );
      passwordField.getDocument().addDocumentListener( documentListener );

      getRootPane().setDefaultButton( savePasswordButton );
      changePasswordFrame.add( changePasswordPanel );
      changePasswordFrame.pack();
      changePasswordFrame.setResizable( false );
      changePasswordFrame.setSize( changePasswordFrame.getWidth() + 30, changePasswordFrame.getHeight() + 20 );
      changePasswordFrame.setLocationRelativeTo( Login.this );
      changePasswordFrame.setIconImage(
          Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue.png" ) ) );
      changePasswordFrame.setVisible( true );
    }
    else
    {
      String message;
      if ( loginResult == LoginResult.USER_NOT_FOUND )
      {
        message = "Benutzer konnte nicht gefunden werden.";
      }
      else
      {
        message = "Falsches Passwort eingegeben.";
      }
      JOptionPane.showMessageDialog( loginButton, message, "Login fehlgeschlagen", JOptionPane.ERROR_MESSAGE,
          null );
    }
  }

  /**
   * Erzeugt eine UUID für die derzeitge Session. Diese SessionID wird zusammen
   * mit dem Benutzername an den Server geschickt, damit diese verbunden werden
   * können. Wenn man eingeloggt bleiben möchte, so wird die SessionID in den
   * userconfig Properties gespeichert und wird beim nächsten Start aufgerufen.
   *
   * @param username Nutzername
   */
  private void generateSessionID( final String username )
  {
    final UUID uuid = UUID.randomUUID();
    ServerCommunication.getInstance().connectSessionIDToUser( uuid.toString() );
    if ( stayLoggedInCheckBox.isSelected() )
    {
      try
      {
        final File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator
            + "userconfig.properties" );
        final InputStream input = new FileInputStream( file );
        final Properties userProperties = new Properties();
        userProperties.load( input );
        userProperties.setProperty( "SessionID", uuid.toString() );
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

  /**
   * Wenn bei der Registrierung ein Fehler aufgetreten ist, so wird dieser dem
   * Nutzer angezeigt.
   *
   * @param registrationResult Die Antwort des Servers auf die Registrierung.
   */
  void validateRegistartionResult( final String registrationResult )
  {
    if ( !registrationResult.equals( "Registrierung erfolgreich." ) )
    {
      JOptionPane.showMessageDialog( FocusManager.getCurrentManager().getActiveWindow(), registrationResult,
          "Registrierung fehlgeschlagen", JOptionPane.ERROR_MESSAGE, null );
    }
  }

  /**
   * zeigt den Darkmode Header
   */
  public void showDarkHeader()
  {
    final String fontName = LABEL_FONT.getFontName();

    final Color appColor = UIManager.getColor( "AppColor" );
    final String htmlAppColor = "rgb(" + appColor.getRed() + ", " + appColor.getGreen() + ", " + appColor.getBlue() + ")";
    headerLabel.setIcon( new ImageIcon( getClass().getClassLoader().getResource( "welcome_white.png" ) ) );
    registerHyperLink.setText( "<html><font color=white><font face='" + fontName
        + "'><a href>Konto erstellen</a></font></font></html>" );
    forgotPasswordHyperLink.setText( "<html><font color=white><font face='" + fontName
        + "'><a href>Passwort vergessen ?</a></font></font></html>" );
    registerHyperLink.addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        registerHyperLink.setText( "<html><font color=white><font face='" + fontName
            + "'><a href>Konto erstellen</a></font></font></html>" );
      }

      @Override
      public void focusGained( final FocusEvent e )
      {
        registerHyperLink.setText( "<html><font color='" + htmlAppColor + "'><font face='" + fontName
            + "'><a href>Konto erstellen</a></font></font></html>" );
      }
    } );
    forgotPasswordHyperLink.addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        forgotPasswordHyperLink.setText( "<html><font color=white><font face='" + fontName
            + "'><a href>Passwort vergessen ?</a></font></font></html>" );
      }

      @Override
      public void focusGained( final FocusEvent e )
      {
        forgotPasswordHyperLink.setText( "<html><font color='" + htmlAppColor + "'><font face='" + fontName
            + "'><a href>Passwort vergessen ?</a></font></font></html>" );
      }
    } );
  }

  /**
   * zeigt den Daymode Header
   */
  public void showDayHeader()
  {
    final String fontName = LABEL_FONT.getFontName();
    final Color appColor = UIManager.getColor( "AppColor" );
    final String htmlAppColor = "rgb(" + appColor.getRed() + ", " + appColor.getGreen() + ", " + appColor.getBlue() + ")";
    headerLabel.setIcon( new ImageIcon( getClass().getClassLoader().getResource( "welcome.png" ) ) );
    registerHyperLink.setText( "<html><font color=blue><font face='" + fontName
        + "'><a href>Konto erstellen</a></font></font></html>" );
    forgotPasswordHyperLink.setText( "<html><font color=blue><font face='" + fontName
        + "'><a href>Passwort vergessen ?</a></font></font></html>" );
    registerHyperLink.addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        registerHyperLink.setText( "<html><font color=blue><font face='" + fontName
            + "'><a href>Konto erstellen</a></font></font></html>" );
      }

      @Override
      public void focusGained( final FocusEvent e )
      {
        registerHyperLink.setText( "<html><font color='" + htmlAppColor + "'><font face='" + fontName
            + "'><a href>Konto erstellen</a></font></font></html>" );
      }
    } );
    forgotPasswordHyperLink.addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        forgotPasswordHyperLink.setText( "<html><font color=blue><font face='" + fontName
            + "'><a href>Passwort vergessen ?</a></font></font></html>" );
      }

      @Override
      public void focusGained( final FocusEvent e )
      {
        forgotPasswordHyperLink.setText( "<html><font color='" + htmlAppColor + "'><font face='" + fontName
            + "'><a href>Passwort vergessen ?</a></font></font></html>" );
      }
    } );
  }
}
