/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.isp.memate.actionbar.MeMateActionBar;
import com.isp.memate.actionbar.MeMateActionBarButton;

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
  private static final Mainframe instance               = new Mainframe();
  private final JPanel           contentPanel           = new JPanel( new BorderLayout() );
  private final JPanel           headerPanel            = new JPanel();
  private final Color            color                  = new Color( 29, 164, 165 );
  private final JLabel           balanceLabel           = new JLabel();
  private final JLabel           helloUserLabel         = new JLabel( "Hallo User" );
  private final Icon             dashboardIconBlack     = new ImageIcon( getClass().getClassLoader().getResource( "dashboard_black.png" ) );
  private final Icon             dashboardIconWhite     = new ImageIcon( getClass().getClassLoader().getResource( "dashboard_white.png" ) );
  private final Icon             historyIconBlack       = new ImageIcon( getClass().getClassLoader().getResource( "history_black.png" ) );
  private final Icon             historyIconWhite       = new ImageIcon( getClass().getClassLoader().getResource( "history_white.png" ) );
  private final Icon             adminViewIconBlack     = new ImageIcon( getClass().getClassLoader().getResource( "adminview_black.png" ) );
  private final Icon             adminViewIconWhite     = new ImageIcon( getClass().getClassLoader().getResource( "adminview_white.png" ) );
  private final Icon             darkModeIconBlack      = new ImageIcon( getClass().getClassLoader().getResource( "darkmode_black.png" ) );
  private final Icon             darkModeIconWhite      = new ImageIcon( getClass().getClassLoader().getResource( "darkmode_white.png" ) );
  private final Icon             dayModeIconBlack       = new ImageIcon( getClass().getClassLoader().getResource( "daymode_black.png" ) );
  private final Icon             dayModeIconWhite       = new ImageIcon( getClass().getClassLoader().getResource( "daymode_white.png" ) );
  private final Icon             drinkManagerIconBlack  =
      new ImageIcon( getClass().getClassLoader().getResource( "drinkmanager_black.png" ) );
  private final Icon             drinkManagerIconWhite  =
      new ImageIcon( getClass().getClassLoader().getResource( "drinkmanager_white.png" ) );
  private final Icon             logoutIconBlack        = new ImageIcon( getClass().getClassLoader().getResource( "logout_black_24.png" ) );
  private final Icon             logoutIconWhite        = new ImageIcon( getClass().getClassLoader().getResource( "logout_white_24.png" ) );
  private final Icon             consumptionIconBlack   =
      new ImageIcon( getClass().getClassLoader().getResource( "consumption_black.png" ) );
  private final Icon             consumptionIconWhite   =
      new ImageIcon( getClass().getClassLoader().getResource( "consumption_white.png" ) );
  private final Icon             creditHistoryIconBlack =
      new ImageIcon( getClass().getClassLoader().getResource( "creditHistory_black.png" ) );
  private final Icon             creditHistoryIconWhite =
      new ImageIcon( getClass().getClassLoader().getResource( "creditHistory_white.png" ) );
  private final MeMateActionBar  bar                    = new MeMateActionBar( new Color( 225, 225, 225 ), Color.black );
  private MeMateActionBarButton  drinkManagerButton;
  private MeMateActionBarButton  adminViewButton;
  private MeMateActionBarButton  logoutButton;
  private MeMateActionBarButton  darkModeButton;

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
    deriveFontsAndSetLayout();
    addActionBar();
    setIconImage(
        Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue2.png" ) ) );
    setTitle( "MeMate" );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setMinimumSize( new Dimension( 380, 475 ) );
    setSize( 1185, 790 );
    setLocationRelativeTo( null );
  }

  /**
   * 
   */
  private void addActionBar()
  {
    headerPanel.add( bar.createToggleButtonTitleVisibleState().getBarButton(), BorderLayout.WEST );

    bar.addActionButton( dashboardIconBlack, dashboardIconWhite, "Dashboard", "Dashboard öffnen", color, new Runnable()
    {
      public void run()
      {
        contentPanel.removeAll();
        contentPanel.add( Dashboard.getInstance(), BorderLayout.CENTER );
        contentPanel.repaint();
        contentPanel.revalidate();
      }
    } );
    bar.addActionButton( historyIconBlack, historyIconWhite, "Historie", "Historie öffnen", color, new Runnable()
    {
      public void run()
      {
        contentPanel.removeAll();
        contentPanel.add( History.getInstance(), BorderLayout.CENTER );
        contentPanel.repaint();
        contentPanel.revalidate();
      }
    } );
    bar.addActionButton( consumptionIconBlack, consumptionIconWhite, "Verbrauchsrate",
        "Hier können sie ihren durchschnittlichen Konsum sehen", color, new Runnable()
        {
          public void run()
          {
            contentPanel.removeAll();
            contentPanel.add( ConsumptionRate.getInstance(), BorderLayout.CENTER );
            contentPanel.repaint();
            contentPanel.revalidate();
          }
        } );
    bar.addActionButton( creditHistoryIconBlack, creditHistoryIconWhite, "Guthabenverlauf",
        "Hier können sie den Verlauf ihres Guthabens betrachten", color, new Runnable()
        {
          public void run()
          {
            contentPanel.removeAll();
            contentPanel.add( CreditHistory.getInstance() );
            contentPanel.repaint();
            contentPanel.revalidate();
          }
        } );
    drinkManagerButton = bar.addActionButton( drinkManagerIconBlack, drinkManagerIconWhite, "Getränkemanager",
        "Getränkemanager öffnen", color, new Runnable()
        {
          public void run()
          {
            contentPanel.removeAll();
            contentPanel.add( Drinkmanager.getInstance() );
            contentPanel.repaint();
            contentPanel.revalidate();
          }
        } );
    drinkManagerButton.setEnabled( false );
    adminViewButton = bar.addActionButton( adminViewIconBlack, adminViewIconWhite, "Adminview",
        "Adminansicht öffnen", color, new Runnable()
        {
          public void run()
          {
            contentPanel.removeAll();
            contentPanel.add( Adminview.getInstance() );
            contentPanel.repaint();
            contentPanel.revalidate();
          }
        } );
    adminViewButton.setEnabled( false );

    bar.addVariableGlue();
    darkModeButton = bar.addActionButton( darkModeIconBlack, darkModeIconWhite, "Darkmode", "Wechselt in den Darkmode", new Runnable()
    {
      @Override
      public void run()
      {
        bar.toggleDarkmode();
        if ( bar.darkModeOn() )
        {
          UIManager.put( "Label.disabledShadow", Color.DARK_GRAY );
          darkModeButton.setIcon( dayModeIconWhite );
          darkModeButton.setPressedIcon( dayModeIconBlack );
          darkModeButton.setTooltip( "Wechselt in den Daymode" );
        }
        else
        {
          UIManager.put( "Label.disabledShadow", Color.white );
          darkModeButton.setIcon( darkModeIconBlack );
          darkModeButton.setPressedIcon( darkModeIconWhite );
          darkModeButton.setTooltip( "Wechselt in den Darkmode" );
        }
      }
    } );
    logoutButton = bar.addActionButton( logoutIconBlack, logoutIconWhite, "Logout", "Ausloggen", new Runnable()
    {
      @Override
      public void run()
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
        logoutButton.setBackground( bar.getBackground() );
      }
    } );
    add( bar, BorderLayout.WEST );
    add( headerPanel, BorderLayout.NORTH );
    add( contentPanel, BorderLayout.CENTER );
    bar.selectButton( "Dashboard" );
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
   * Setzt Border und ändert ein paar Schriftgrößen
   */
  private void deriveFontsAndSetLayout()
  {
    balanceLabel.setFont( balanceLabel.getFont().deriveFont( 20f ) );
    helloUserLabel.setFont( helloUserLabel.getFont().deriveFont( 20f ) );
    balanceLabel.setForeground( Color.white );
    helloUserLabel.setForeground( Color.white );
    headerPanel.setLayout( new BorderLayout() );
    JPanel titlePanel = new JPanel();
    titlePanel.setLayout( new BoxLayout( titlePanel, BoxLayout.X_AXIS ) );
    titlePanel.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) );

    setLayout( new BorderLayout() );
    contentPanel.add( Dashboard.getInstance() );
    headerPanel.setBackground( color );
    contentPanel.setBackground( Color.white );
    contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
    titlePanel.add( helloUserLabel );
    titlePanel.add( Box.createHorizontalGlue() );
    titlePanel.add( balanceLabel );
    titlePanel.setOpaque( false );
    headerPanel.add( titlePanel, BorderLayout.CENTER );
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
    balanceLabel.setText( String.format( "Kontostand: %.2f €", newBalance ) );
    if ( newBalance.floatValue() >= 0 )
    {
      balanceLabel.setForeground( Color.white );
    }
    else
    {
      balanceLabel.setForeground( Color.RED );
    }
  }

  /**
   * Toggles the Adminview
   */
  public void toggleAdminView()
  {
    if ( ServerCommunication.getInstance().currentUser.equals( "admin" ) )
    {
      drinkManagerButton.setEnabled( true );
      adminViewButton.setEnabled( true );
    }
    else
    {
      drinkManagerButton.setEnabled( false );
      adminViewButton.setEnabled( false );
    }

  }
}
