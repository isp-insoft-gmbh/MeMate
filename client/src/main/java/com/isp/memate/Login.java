/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Color;
import java.awt.Cursor;
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
import java.util.Properties;
import java.util.UUID;

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
import com.isp.memate.Shared.LoginResult;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.SwingUtil;
import com.isp.memate.util.Util;

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
  private static final long   serialVersionUID   = 2601644901607012389L;
  private static final String LABEL_FOREGROUND   = "Label.foreground";
  private static final String PASSWORT_VERGESSEN = "Passwort vergessen?";
  private static final String KONTO_ERSTELLEN    = "Konto erstellen";
  Cache                       cache              = Cache.getInstance();
  private JButton             loginButton;
  private JLabel              usernameLabel, passwordLabel, stayLoggedInLabel, headerLabel;
  private JTextPane           registerHyperLink, forgotPasswordHyperLink;
  private JCheckBox           stayLoggedInCheckBox;
  private JPanel              loginPanel;
  private JPasswordField      passwordField;
  private JTextField          usernameTextField;
  private static String       currentUsername;

  public Login()
  {
    GUIObjects.loginFrame = this;
    initComponents();
    layoutComponents();
    addActionListener();
    applyFrameSettings();
  }

  private void initComponents()
  {
    stayLoggedInCheckBox = new JCheckBox();
    registerHyperLink = new JTextPane();
    forgotPasswordHyperLink = new JTextPane();
    loginButton = new JButton();
    loginPanel = new JPanel();
    usernameLabel = new JLabel();
    passwordLabel = new JLabel();
    stayLoggedInLabel = new JLabel();
    passwordField = new JPasswordField();
    usernameTextField = new JTextField();
    ImageIcon icon;

    if ( MeMateUIManager.getDarkModeState() )
    {
      icon = new ImageIcon( getClass().getClassLoader().getResource( "welcome_white.png" ) );
    }
    else
    {
      icon = new ImageIcon( getClass().getClassLoader().getResource( "welcome.png" ) );
    }

    headerLabel = new JLabel( icon );
    loginPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
    headerLabel.setBorder( new EmptyBorder( 0, 0, 15, 0 ) );
    SwingUtil.setPreferredWidth( 420, usernameTextField );
    SwingUtil.setPreferredWidth( 420, passwordField );

    loginButton.setText( "Anmelden" );
    passwordLabel.setText( "Passwort" );
    usernameLabel.setText( "Benutzername" );
    stayLoggedInLabel.setText( "Eingeloggt bleiben" );

    registerHyperLink.setEditable( false );
    registerHyperLink.setContentType( "text/html" );
    registerHyperLink.setText( generateHTMLText( UIManager.getColor( LABEL_FOREGROUND ), KONTO_ERSTELLEN ) );
    forgotPasswordHyperLink.setEditable( false );
    forgotPasswordHyperLink.setContentType( "text/html" );
    forgotPasswordHyperLink.setText( generateHTMLText( UIManager.getColor( LABEL_FOREGROUND ), PASSWORT_VERGESSEN ) );

    addHyperlinkFocusListener( registerHyperLink, KONTO_ERSTELLEN );
    addHyperlinkFocusListener( forgotPasswordHyperLink, PASSWORT_VERGESSEN );
  }

  private String generateHTMLText( Color color, String string )
  {
    final String fontName = UIManager.getFont( "Label.font" ).getFontName();
    if ( color != null )
    {
      final String htmlColor = "rgb(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
      return "<html><font color='" + htmlColor + "'><font face='" + fontName + "'><a href>" + string + "</a></font></font></html>";
    }
    return "<html><font face='" + fontName + "'><a href>" + string + "</a></font></html>";
  }

  private void addHyperlinkFocusListener( JTextPane hyperlink, String text )
  {
    hyperlink.addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        hyperlink.setText(
            generateHTMLText( UIManager.getColor( LABEL_FOREGROUND ), text ) );
      }

      @Override
      public void focusGained( final FocusEvent e )
      {
        hyperlink.setText(
            generateHTMLText( UIManager.getColor( "AppColor" ), text ) );
      }
    } );
  }

  private void addActionListener()
  {
    loginButton.addActionListener( __ ->
    {
      if ( usernameTextField.getText().isEmpty() )
      {
        showMessageDialog( "Benutzername" );
        return;
      }
      else if ( passwordField.getPassword().length == 0 )
      {
        showMessageDialog( "Passwort" );
        return;
      }
      else
      {
        currentUsername = usernameTextField.getText();
        final LoginInformation login = new LoginInformation( usernameTextField.getText(),
            Util.getHash( String.valueOf( passwordField.getPassword() ) ) );
        ServerCommunication.getInstance().checkLogin( login );
      }
    } );

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
        new RegistrationDialog();
      }
    } );
  }

  private void showMessageDialog( String string )
  {
    JOptionPane.showMessageDialog( loginPanel, string + " darf nicht leer sein.", "Login fehlgeschlagen",
        JOptionPane.WARNING_MESSAGE, null );
  }

  private void layoutComponents()
  {
    loginPanel.setLayout( new GridBagLayout() );
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
  }

  private void applyFrameSettings()
  {
    setIconImage(
        Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue.png" ) ) );
    setTitle( "MeMate" );
    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    pack();
    setResizable( false );
    setLocationRelativeTo( null );
    getRootPane().setDefaultButton( loginButton );
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
      cache.setUsername( currentUsername );
      generateSessionID( currentUsername );
      GUIObjects.loginFrame = null;
      dispose();
      final Mainframe mainframe = new Mainframe();
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
      new ChangePasswordDialog();
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

  public void newPasswordHasBeenSet()
  {
    cache.setUsername( currentUsername );
    generateSessionID( currentUsername );
    GUIObjects.loginFrame = null;
    dispose();
    final Mainframe mainframe = new Mainframe();
    mainframe.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
    mainframe.setVisible( true );
    ServerCommunication.getInstance().tellServerToSendDrinkInformations();
    ServerCommunication.getInstance().getBalance();
    ServerCommunication.getInstance().tellServerToSendHistoryData();
    mainframe.requestFocus();
  }
}
