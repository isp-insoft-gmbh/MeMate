/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Der Mainframe bildet das Gerüst für Dashboard, Historie und den Getränkemanager. Des weitern zeigt der
 * Mainframe eine
 * kleine Begrüßung des Users und den Kontostand des Benutzers an (sollte dieser negativ sein, so soll der
 * Kontostand in
 * rot dargestellt werden).
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
  private final JTabbedPane      tabbedPane      = new JTabbedPane();
  public Dashboard               dashboard       = Dashboard.getInstance();
  public Drinkmanager            drinkmanager    = new Drinkmanager();

  /**
   * @return the static instance of {@link ServerCommunication}
   */
  public static Mainframe getInstance()
  {
    return instance;
  }

  /**
   * Setzt das Layout und nimmt einige Änderungen an den Komponenten vor.
   */
  public Mainframe()
  {
    setBalance( ServerCommunication.getInstance().balance );
    setBorderAndDeriveFonts();
    tabbedPane.addTab( "Dashboard", dashboard );
    tabbedPane.addTab( "Historie", new History() );
    tabbedPane.addTab( "Getränkemanager", drinkmanager );
    layoutComponents();
    add( mainPanel );
    setIconImage(
        Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue2.png" ) ) );
    setTitle( "MeMate" );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setSize( 1180, 740 );
    setLocationRelativeTo( null );
  }

  /**
   * @param username
   */
  public void setHelloLabel( String username )
  {
    helloUserLabel.setText( "Hallo " + username );
  }

  /**
   * Updates the the balance internally and updates the UI component.
   * 
   * @param newBalance new balance to set and display
   */
  public void setBalance( Float newBalance )
  {
    ServerCommunication.getInstance().balance = newBalance;
    kontostandLabel.setText( String.format( "Kontostand: %.2f €", ServerCommunication.getInstance().balance ) );
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
    halloLabelConstraints.weightx = 0.5;
    headerPanel.add( helloUserLabel, halloLabelConstraints );

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

  public void updateDashboardAndDrinkmanager()
  {
    tabbedPane.removeTabAt( 2 );
    tabbedPane.insertTab( "Getränkemanager", null, new Drinkmanager(), null, 2 );
    tabbedPane.setSelectedIndex( 2 );
    tabbedPane.removeTabAt( 0 );
    tabbedPane.insertTab( "Dashboard", null, new Dashboard( this ), null, 0 );

  }
}
