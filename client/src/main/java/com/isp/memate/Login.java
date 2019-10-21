/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.isp.memate.util.SwingUtil;

/**
 * 
 * Im LoginFrame kann der Benutzer sich registrieren oder wenn er bereits ein Konto besitzt, sich
 * einloggen. Außerdem besteht die Möglichkeit angemeldet zu bleiben.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class Login extends JFrame
{
  private final JPanel         loginPanel           = new JPanel( new GridBagLayout() );
  private final JLabel         headerLabel          = new JLabel( "Willkommen bei MeMate" );
  private final JLabel         usernameLabel        = new JLabel( "Benutzername" );
  private final JLabel         passwordLabel        = new JLabel( "Passwort" );
  private final JLabel         orLabel              = new JLabel( "oder" );
  private final JLabel         stayLoggedInLabel    = new JLabel( "Eingeloggt bleiben" );
  private final JTextField     usernameTextField    = new JTextField();
  private final JPasswordField passwordField        = new JPasswordField();
  private final JButton        loginButton          = new JButton( "Anmelden" );
  private final JButton        registerButton       = new JButton( "Registrieren" );
  private final JCheckBox      stayLoggedInCheckBox = new JCheckBox();

  private final Font LABEL_FONT = UIManager.getFont( "Label.font" ).deriveFont( 15f );

  /**
   * Passt Schriftgrößen, Borders und Größen an. Außerdem werden die
   * Komponenten gelayoutet und ActionListener werden hinzugefügt
   */
  public Login()
  {
    setIconImage( Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "memateicon.png" ) ) );
    setTitle( "MeMate" );
    deriveFonts();
    setBordersAndPreferredSize();
    layoutComponents();
    addActionListener();

    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    pack();
    setResizable( false );
    setLocationRelativeTo( null );
  }

  private void addActionListener()
  {
    loginButton.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent e )
      {
        dispose();
        Mainframe mainframe = new Mainframe();
        mainframe.setVisible( true );
        // TODO implement Login
      }
    } );
    registerButton.addActionListener( new ActionListener()
    {
      public void actionPerformed( ActionEvent __ )
      {
        if ( isPasswordOrUserNameIncorrect() )
        {
          JOptionPane.showMessageDialog( Login.this, "Passwort oder Benutzername sind nicht zulässig", "Registrieren",
              JOptionPane.WARNING_MESSAGE );
        }
        else
        {
          //String username = usernameTextField.getText();
          //char[] password = passwordField.getPassword();
          int reply =
              JOptionPane.showConfirmDialog( Login.this, "Wollen Sie wirklich einen neuen Benutzer anlegen?", "Registrieren",
                  JOptionPane.INFORMATION_MESSAGE );
          if ( reply == JOptionPane.YES_OPTION )
          {
            //TODO implement new User 
          }
        }
      }
    } );
  }


  private boolean isPasswordOrUserNameIncorrect()
  {
    return passwordField.getPassword() == null || passwordField.getPassword().length == 0
        || usernameTextField.getText() == null || usernameTextField.getText().isEmpty();
  }

  private void layoutComponents()
  {
    GridBagConstraints titleConstraints = new GridBagConstraints();
    titleConstraints.gridx = 0;
    titleConstraints.gridy = 0;
    titleConstraints.gridwidth = 6;
    loginPanel.add( headerLabel, titleConstraints );

    GridBagConstraints usernameConstraints = new GridBagConstraints();
    usernameConstraints.gridx = 0;
    usernameConstraints.gridy = 1;
    usernameConstraints.anchor = GridBagConstraints.LINE_START;
    loginPanel.add( usernameLabel, usernameConstraints );
    GridBagConstraints usernameConstraints2 = new GridBagConstraints();
    usernameConstraints2.gridx = 2;
    usernameConstraints2.gridy = 1;
    usernameConstraints2.gridwidth = 4;
    usernameConstraints2.insets = new Insets( 0, 10, 5, 0 );
    loginPanel.add( usernameTextField, usernameConstraints2 );

    GridBagConstraints passwordConstraints = new GridBagConstraints();
    passwordConstraints.gridx = 0;
    passwordConstraints.gridy = 2;
    passwordConstraints.anchor = GridBagConstraints.LINE_START;
    loginPanel.add( passwordLabel, passwordConstraints );
    GridBagConstraints passwordConstraints2 = new GridBagConstraints();
    passwordConstraints2.gridx = 2;
    passwordConstraints2.gridy = 2;
    passwordConstraints2.gridwidth = 4;
    passwordConstraints2.insets = new Insets( 0, 10, 5, 0 );
    loginPanel.add( passwordField, passwordConstraints2 );

    GridBagConstraints stayLoggedInConstraints = new GridBagConstraints();
    stayLoggedInConstraints.gridx = 0;
    stayLoggedInConstraints.gridy = 3;
    stayLoggedInConstraints.anchor = GridBagConstraints.LINE_START;
    loginPanel.add( stayLoggedInLabel, stayLoggedInConstraints );

    GridBagConstraints checkboxConstraints = new GridBagConstraints();
    checkboxConstraints.gridx = 2;
    checkboxConstraints.gridy = 3;
    checkboxConstraints.insets = new Insets( 0, 6, 0, 0 );
    loginPanel.add( stayLoggedInCheckBox, checkboxConstraints );

    GridBagConstraints loginButtonConstraints = new GridBagConstraints();
    loginButtonConstraints.gridx = 0;
    loginButtonConstraints.gridy = 4;
    loginButtonConstraints.gridwidth = 6;
    loginButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
    loginButtonConstraints.insets = new Insets( 5, 0, 3, 0 );
    loginPanel.add( loginButton, loginButtonConstraints );

    GridBagConstraints orLabelConstraints = new GridBagConstraints();
    orLabelConstraints.gridx = 0;
    orLabelConstraints.gridy = 5;
    orLabelConstraints.gridwidth = 6;
    loginPanel.add( orLabel, orLabelConstraints );

    GridBagConstraints registrierenButtonConstraints = new GridBagConstraints();
    registrierenButtonConstraints.gridx = 0;
    registrierenButtonConstraints.gridy = 6;
    registrierenButtonConstraints.gridwidth = 6;
    registrierenButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
    registrierenButtonConstraints.insets = new Insets( 3, 0, 5, 0 );
    loginPanel.add( registerButton, registrierenButtonConstraints );

    add( loginPanel );
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
    Font headerLabelFont = headerLabel.getFont();
    headerLabel.setFont( new Font( "Comic Sans MS", headerLabelFont.getStyle(), (int) (headerLabelFont.getSize() * 3f) ) );
    usernameLabel.setFont( LABEL_FONT );
    passwordLabel.setFont( LABEL_FONT );
    stayLoggedInLabel.setFont( LABEL_FONT );
  }
}
