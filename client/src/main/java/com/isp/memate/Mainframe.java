/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.isp.memate.actionbar.MeMateActionBar;
import com.isp.memate.actionbar.MeMateActionBarButton;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;

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
  Cache                          cache                  = Cache.getInstance();
  private final Dashboard        dashboard              = new Dashboard( instance );
  private final Icon             dashboardIconBlack     = new ImageIcon( getClass().getClassLoader().getResource( "dashboard_black.png" ) );
  private final Icon             dashboardIconWhite     = new ImageIcon( getClass().getClassLoader().getResource( "dashboard_white.png" ) );
  private final Icon             adminViewIconBlack     = new ImageIcon( getClass().getClassLoader().getResource( "adminview_black.png" ) );
  private final Icon             adminViewIconWhite     = new ImageIcon( getClass().getClassLoader().getResource( "adminview_white.png" ) );
  private final Icon             logoutIconBlack        = new ImageIcon( getClass().getClassLoader().getResource( "logout_black_24.png" ) );
  private final Icon             logoutIconWhite        = new ImageIcon( getClass().getClassLoader().getResource( "logout_white_24.png" ) );
  private final Icon             historyIconBlack       = new ImageIcon( getClass().getClassLoader().getResource( "history_black.png" ) );
  private final Icon             historyIconWhite       = new ImageIcon( getClass().getClassLoader().getResource( "history_white.png" ) );
  private final Icon             socialBlackIcon        = new ImageIcon( getClass().getClassLoader().getResource( "social_black.png" ) );
  private final Icon             socialWhiteIcon        = new ImageIcon( getClass().getClassLoader().getResource( "social_white.png" ) );
  private final Icon             undoBlackIcon          = new ImageIcon( getClass().getClassLoader().getResource( "back_black.png" ) );
  private final Icon             undoWhiteIcon          = new ImageIcon( getClass().getClassLoader().getResource( "back_white.png" ) );
  private final Icon             drinkManagerIconBlack  =
      new ImageIcon( getClass().getClassLoader().getResource( "drinkmanager_black.png" ) );
  private final Icon             drinkManagerIconWhite  =
      new ImageIcon( getClass().getClassLoader().getResource( "drinkmanager_white.png" ) );
  private final Icon             consumptionIconBlack   =
      new ImageIcon( getClass().getClassLoader().getResource( "consumption_black.png" ) );
  private final Icon             consumptionIconWhite   =
      new ImageIcon( getClass().getClassLoader().getResource( "consumption_white.png" ) );
  private final Icon             creditHistoryIconBlack =
      new ImageIcon( getClass().getClassLoader().getResource( "creditHistory_black.png" ) );
  private final Icon             creditHistoryIconWhite =
      new ImageIcon( getClass().getClassLoader().getResource( "creditHistory_white.png" ) );
  private final Color            color                  = UIManager.getColor( "AppColor" );
  private final JPanel           contentPanel           = MeMateUIManager.createJPanel();
  private final JLabel           helloUserLabel         = new JLabel( "Hallo User" );
  private final ConsumptionRate  consumptionRate        = new ConsumptionRate();
  private final CreditHistory    creditHistory          = new CreditHistory();
  private final Drinkmanager     drinkManager           = new Drinkmanager();
  private final Adminview        adminView              = new Adminview();
  private final History          history                = new History();
  private final Social           social                 = new Social();
  private final JLabel           balanceLabel           = new JLabel();
  public final JPanel            headerPanel            = new JPanel();
  public JPanel                  burgerButton;
  private MeMateActionBarButton  dashboardButton;
  private MeMateActionBarButton  logoutButton;
  public MeMateActionBarButton   settingsButton;
  private MeMateActionBarButton  undoButton;
  public MeMateActionBar         bar;
  public static Image            frameImage             =
      Toolkit.getDefaultToolkit().getImage( Mainframe.class.getClassLoader().getResource( "frameiconblue.png" ) );

  /**
   * @return the static instance of {@link ServerCommunication}
   */
  static Mainframe getInstance()
  {
    return instance;
  }

  public Drinkmanager getDrinkManager()
  {
    return drinkManager;
  }

  public Dashboard getDashboard()
  {
    return dashboard;
  }

  /**
   * Setzt das Layout und nimmt einige Änderungen an den Komponenten vor.
   * Außerdem wird die Logik für den Logout-Button definiert.
   */
  public Mainframe()
  {
    contentPanel.setLayout( new BorderLayout() );
    contentPanel.add( dashboard );

    deriveFontsAndSetLayout();
    setIconImage(
        Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue.png" ) ) );
    setTitle( "MeMate" );
    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    setMinimumSize( new Dimension( 380, 550 ) );
    setSize( 1185, 770 );
    setLocationRelativeTo( null );
    add( contentPanel, BorderLayout.CENTER );
    add( headerPanel, BorderLayout.NORTH );
    MeMateUIManager.setUISettings();
  }

  /**
   * Fügt die Actionbar hinzu.
   */
  public void addActionBar()
  {
    try
    {
      remove( bar );
      headerPanel.remove( burgerButton );
    }
    catch ( final Exception exception )
    {
      // Muss nicht weiter behandelt werden, da diese Exception nur beim ersten Aufbauen der Actionbar auftritt.
    }
    bar = new MeMateActionBar( new Color( 225, 225, 225 ), Color.black );
    burgerButton = bar.createToggleButtonTitleVisibleState().getBarButton();
    headerPanel.add( burgerButton, BorderLayout.WEST );


    addDefaultButtons();
    toggleAdminButtons();
    addUndoButton();
    addSettingsButton();
    addLogoutButton();

    bar.selectButton( "Dashboard" );
    dashboardButton.getRunnable().run();
    try
    {
      final File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
      final InputStream input = new FileInputStream( file );
      final Properties userProperties = new Properties();
      userProperties.load( input );

      if ( userProperties.getProperty( "Darkmode" ) != null && userProperties.getProperty( "Darkmode" ).equals( "on" ) )
      {
        bar.showDarkmode();
        bar.setBackground( UIManager.getColor( "App.Actionbar" ) );
        burgerButton.setBackground( UIManager.getColor( "App.Actionbar" ) );
        MeMateUIManager.showDarkMode();
      }
      else
      {
        MeMateUIManager.showDayMode();
      }
    }
    catch ( final IOException exception )
    {
      ClientLog.newLog( "Die SessionID konnte nicht gespeichert werden." );
      ClientLog.newLog( exception.getMessage() );
    }
    add( bar, BorderLayout.WEST );
  }

  private void addSettingsButton()
  {
    settingsButton = bar.addActionButton( adminViewIconBlack, adminViewIconWhite, "Settings",
        "Öffnet die Einstellungen", color, () ->
        {
          contentPanel.removeAll();
          contentPanel.add( new Settings() );
          contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
          contentPanel.repaint();
          contentPanel.revalidate();
        } );
  }

  private void addDefaultButtons()
  {
    dashboardButton = bar.addActionButton( dashboardIconBlack, dashboardIconWhite, "Dashboard", "Dashboard öffnen", color, () ->
    {
      contentPanel.removeAll();
      dashboard.updateButtonpanel();
      contentPanel.add( dashboard );
      contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
      contentPanel.repaint();
      contentPanel.revalidate();
    } );
    bar.addActionButton( historyIconBlack, historyIconWhite, "Historie", "Historie öffnen", color, () ->
    {
      contentPanel.removeAll();
      history.updateHistory();
      MeMateUIManager.setUISettings();
      contentPanel.add( history );
      contentPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );
      contentPanel.repaint();
      contentPanel.revalidate();
    } );
    bar.addActionButton( consumptionIconBlack, consumptionIconWhite, "Verbrauchsrate",
        "Hier können sie ihren durchschnittlichen Konsum sehen", color, () ->
        {
          contentPanel.removeAll();
          consumptionRate.addGraph();
          MeMateUIManager.setUISettings();
          contentPanel.add( consumptionRate );
          contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
          contentPanel.repaint();
          contentPanel.revalidate();
        } );
    bar.addActionButton( creditHistoryIconBlack, creditHistoryIconWhite, "Guthabenverlauf",
        "Hier können sie den Verlauf ihres Guthabens betrachten", color, () ->
        {
          contentPanel.removeAll();
          creditHistory.addChart();
          MeMateUIManager.setUISettings();
          contentPanel.add( creditHistory );
          contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
          contentPanel.repaint();
          contentPanel.revalidate();
        } );
    bar.addActionButton( socialBlackIcon, socialWhiteIcon, "Scoreboard", "Scoreboard", color, () ->
    {
      contentPanel.removeAll();
      social.update();
      MeMateUIManager.setUISettings();
      contentPanel.add( social );
      contentPanel.repaint();
      contentPanel.revalidate();
    } );
  }

  private void addLogoutButton()
  {
    logoutButton = bar.addActionButton( logoutIconBlack, logoutIconWhite, "Logout", "Ausloggen", () ->
    {
      cache.setUsername( null );
      ServerCommunication.getInstance().logout();
      setUndoButtonEnabled( false );
      try
      {
        final File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
        final InputStream input = new FileInputStream( file );
        final Properties userProperties = new Properties();
        userProperties.load( input );
        userProperties.setProperty( "SessionID", "null" );
        final OutputStream output = new FileOutputStream( file );
        userProperties.store( output, "" );
      }
      catch ( final IOException exception )
      {
        ClientLog.newLog( "Die SessionID konnte nicht resetet werden." );
        ClientLog.newLog( exception.getMessage() );
      }
      dispose();
      final Login login = Login.getInstance();
      login.setVisible( true );
      logoutButton.setBackground( bar.getBackground() );
    } );
  }


  private void addUndoButton()
  {
    bar.addVariableGlue();
    undoButton = bar.addActionButton( undoBlackIcon, undoWhiteIcon, "Rückgänig", "Letzte Aktion rückgängig machen", () ->
    {
      ServerCommunication.getInstance().undoLastAction();
      undoButton.setBackground( bar.getBackground() );
      updateDashboard();
    } );
    undoButton.setEnabled( false );
  }

  private void toggleAdminButtons()
  {
    if ( cache.getUsername().equals( "admin" ) )
    {
      bar.addActionButton( drinkManagerIconBlack, drinkManagerIconWhite, "Getränkemanager",
          "Getränkemanager öffnen", color, () ->
          {
            contentPanel.removeAll();
            drinkManager.updateList();
            MeMateUIManager.setUISettings();
            contentPanel.add( drinkManager );
            contentPanel.setBorder( new EmptyBorder( 0, 0, 5, 5 ) );
            contentPanel.repaint();
            contentPanel.revalidate();
          } );
      bar.addActionButton( adminViewIconBlack, adminViewIconWhite, "Adminview",
          "Adminansicht öffnen", color, () ->
          {
            contentPanel.removeAll();
            adminView.updateDrinkAmounts();
            MeMateUIManager.setUISettings();
            contentPanel.add( adminView );
            contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
            contentPanel.repaint();
            contentPanel.revalidate();
          } );
    }
  }

  /**
   * Setzt den Text Im Begrüßungslabel.
   *
   * @param username Benutzername
   */
  public void setHelloLabel( final String username )
  {
    helloUserLabel.setText( "Hallo " + username );
  }

  /**
   * Setzt Border und ändert ein paar Schriftgrößen.
   */
  private void deriveFontsAndSetLayout()
  {
    balanceLabel.setFont( balanceLabel.getFont().deriveFont( 20f ) );
    helloUserLabel.setFont( helloUserLabel.getFont().deriveFont( 20f ) );
    balanceLabel.setForeground( Color.white );
    helloUserLabel.setForeground( Color.white );
    headerPanel.setLayout( new BorderLayout() );
    final JPanel titlePanel = new JPanel();
    titlePanel.setLayout( new BoxLayout( titlePanel, BoxLayout.X_AXIS ) );
    titlePanel.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) );

    setLayout( new BorderLayout() );
    headerPanel.setBackground( color );
    contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
    titlePanel.add( helloUserLabel );
    titlePanel.add( Box.createHorizontalGlue() );
    titlePanel.add( balanceLabel );
    titlePanel.setOpaque( false );
    headerPanel.add( titlePanel, BorderLayout.CENTER );
  }

  /**
   * Aktualisiert die UI-Komponente für den Kontostand.
   *
   * @param newBalance der Kontostand
   */
  void updateBalanceLabel( final Float newBalance )
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
   * Bestimmt ob der undoButton aktiviert sein soll oder nicht.
   *
   * @param state true or false
   */
  public void setUndoButtonEnabled( final boolean state )
  {
    undoButton.setEnabled( state );
  }

  void updateDashboard()
  {
    final Component[] comp = contentPanel.getComponents();
    for ( final Component component : comp )
    {
      if ( component instanceof Dashboard )
      {
        contentPanel.removeAll();
        dashboard.updateButtonpanel();
        contentPanel.add( dashboard );
      }
      else
      {
        dashboard.updateButtonpanel();
      }
    }
  }
}
