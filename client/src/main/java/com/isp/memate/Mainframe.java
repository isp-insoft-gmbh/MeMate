/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Der Mainframe bildet das Gerüst für Dashboard, Historie und den Getränkemanager.
 * Des weitern zeigt der Mainframe eine kleine Begrüßung des Users und den
 * Kontostand des Benutzers an (sollte dieser negativ sein, so wird der
 * Kontostand in rot dargestellt ).
 *
 * @author nwe
 * @since 15.10.2019
 */
public class Mainframe extends JFrame
{
  private static final Mainframe instance        = new Mainframe();
  private final JPanel           mainPanel       = new JPanel( new BorderLayout() );
  private final JPanel           headerPanel     = new JPanel( new GridBagLayout() );
  private final JLabel           kontostandLabel = new JLabel();
  private final JLabel           helloUserLabel  = new JLabel( "Hallo User" );
  private final JButton          logOutButton    =
      new JButton( new ImageIcon( getClass().getClassLoader().getResource( "logout.png" ) ) );
  private final JTabbedPane      tabbedPane      = new JTabbedPane();

  /**
   * @return the static instance of {@link ServerCommunication}
   */
  public static Mainframe getInstance()
  {
    return instance;
  }

  /**
   * Setzt das Layout und nimmt einige Änderungen an den Komponenten vor.
   * Außerdem wird die Logik für den Logout-Button definiert.
   */
  public Mainframe()
  {
    setBorderAndDeriveFonts();
    tabbedPane.addTab( "Dashboard", Dashboard.getInstance() );
    tabbedPane.addTab( "Historie", History.getInstance() );
    tabbedPane.addTab( "Vebrauchsrate", ConsumptionRate.getInstance() );
    tabbedPane.addTab( "Guthabenverlauf", CreditHistory.getInstance() );
    // tabbedPane.addTab( "Präferenzen", PreferencesTab.getInstance() );

    layoutComponents();
    add( mainPanel );
    setIconImage(
        Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue2.png" ) ) );
    setTitle( "MeMate" );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setSize( 1170, 790 );
    setLocationRelativeTo( null );
    logOutButton.setToolTipText( "Ausloggen" );

    logOutButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        ServerCommunication.getInstance().sessionID = null;
        ServerCommunication.getInstance().currentUser = null;
        ServerCommunication.getInstance().logout();
        Dashboard.getInstance().undoButton.setEnabled( false );
        try ( OutputStream output = new FileOutputStream(
            new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" ), false ); )
        {
          Properties userProperties = new Properties();
          userProperties.setProperty( "SessionID", "null" );
          userProperties.store( output, "SessionID" );
        }
        catch ( IOException exception )
        {
          System.out.println( "Die SessionID konnte nicht resetet werden." );
          exception.printStackTrace();
        }
        dispose();
        Login login = Login.getInstance();
        login.setVisible( true );
      }
    } );
  }


  /**
   * Setzt den Text Im Begrüßungslabel.
   * 
   * @param username Benutzername
   */
  public void setHelloLabel( String username )
  {
    helloUserLabel.setText( "Hallo " + username );
  }

  /**
   * Setzt das Layout für das Kontostandlabel und das Begrüßungslabel und fügt diese dem Headerpanel hinzu.
   */
  private void layoutComponents()
  {
    final GridBagConstraints kontoStandLabelConstraints = new GridBagConstraints();
    kontoStandLabelConstraints.anchor = GridBagConstraints.LINE_START;
    kontoStandLabelConstraints.gridx = 0;
    kontoStandLabelConstraints.weightx = 0.5;
    headerPanel.add( kontostandLabel, kontoStandLabelConstraints );

    final GridBagConstraints halloLabelConstraints = new GridBagConstraints();
    halloLabelConstraints.anchor = GridBagConstraints.LINE_END;
    halloLabelConstraints.gridx = 1;
    halloLabelConstraints.weightx = 1;
    headerPanel.add( helloUserLabel, halloLabelConstraints );

    final GridBagConstraints logOutButtonConstraints = new GridBagConstraints();
    logOutButtonConstraints.anchor = GridBagConstraints.LINE_END;
    logOutButtonConstraints.gridx = 2;
    logOutButtonConstraints.weightx = 0.01;
    headerPanel.add( logOutButton, logOutButtonConstraints );

    mainPanel.add( headerPanel, BorderLayout.NORTH );
    mainPanel.add( tabbedPane, BorderLayout.CENTER );
  }

  /**
   * Setzt Border und ändert ein paar Schriftgrößen
   */
  private void setBorderAndDeriveFonts()
  {
    mainPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
    kontostandLabel.setFont( kontostandLabel.getFont().deriveFont( 20f ) );
    helloUserLabel.setFont( helloUserLabel.getFont().deriveFont( 20f ) );
  }

  /**
   * Wenn Getränke hinzugefügt, bearbeitet oder entfernt werden,
   * dann werden Dashboard und Drinkmanager aktualisiert.
   */
  public void updateDashboardAndDrinkmanager()
  {
    Drinkmanager.getInstance().updateList();
    Dashboard.getInstance().updateButtonpanel();
    Adminview.getInstance().updateDrinkAmounts();
  }

  /**
   * Aktualisiert die UI-Komponente für den Kontostand.
   * 
   * @param newBalance der Kontostand
   */
  public void updateBalanceLabel( Float newBalance )
  {
    kontostandLabel.setText( String.format( "Kontostand: %.2f €", newBalance ) );
    if ( newBalance.floatValue() >= 0 )
    {
      kontostandLabel.setForeground( UIManager.getColor( "Label.foreground" ) );
    }
    else
    {
      kontostandLabel.setForeground( Color.RED );
    }
  }

  /**
   * Toggles the Adminview
   */
  public void toggleAdminView()
  {
    if ( ServerCommunication.getInstance().currentUser.equals( "admin" ) )
    {
      tabbedPane.addTab( "Getränkemanager", Drinkmanager.getInstance() );
      tabbedPane.addTab( "Adminview", Adminview.getInstance() );
    }
    else
    {
      if ( tabbedPane.getTabCount() > 4 )
      {
        tabbedPane.removeTabAt( 5 );
        tabbedPane.removeTabAt( 4 );
      }
    }
  }
}
