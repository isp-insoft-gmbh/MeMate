/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.border.EmptyBorder;

import com.isp.memate.actionbar.MeMateActionBar;
import com.isp.memate.actionbar.MeMateActionBarButton;
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
  private final Icon             dashboardIconBlack     = new ImageIcon( getClass().getClassLoader().getResource( "dashboard_black.png" ) );
  private final Icon             dashboardIconWhite     = new ImageIcon( getClass().getClassLoader().getResource( "dashboard_white.png" ) );
  private final Icon             adminViewIconBlack     = new ImageIcon( getClass().getClassLoader().getResource( "adminview_black.png" ) );
  private final Icon             adminViewIconWhite     = new ImageIcon( getClass().getClassLoader().getResource( "adminview_white.png" ) );
  private final Icon             logoutIconBlack        = new ImageIcon( getClass().getClassLoader().getResource( "logout_black_24.png" ) );
  private final Icon             logoutIconWhite        = new ImageIcon( getClass().getClassLoader().getResource( "logout_white_24.png" ) );
  private final Icon             darkModeIconBlack      = new ImageIcon( getClass().getClassLoader().getResource( "darkmode_black.png" ) );
  private final Icon             darkModeIconWhite      = new ImageIcon( getClass().getClassLoader().getResource( "darkmode_white.png" ) );
  private final Icon             historyIconBlack       = new ImageIcon( getClass().getClassLoader().getResource( "history_black.png" ) );
  private final Icon             historyIconWhite       = new ImageIcon( getClass().getClassLoader().getResource( "history_white.png" ) );
  private final Icon             dayModeIconBlack       = new ImageIcon( getClass().getClassLoader().getResource( "daymode_black.png" ) );
  private final Icon             dayModeIconWhite       = new ImageIcon( getClass().getClassLoader().getResource( "daymode_white.png" ) );
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
  private final JLabel           balanceLabel           = new JLabel();
  private final JPanel           headerPanel            = new JPanel();
  private MeMateActionBarButton  drinkManagerButton;
  private MeMateActionBarButton  adminViewButton;
  private MeMateActionBarButton  logoutButton;
  private MeMateActionBarButton  darkModeButton;
  private MeMateActionBarButton  undoButton;
  private final MeMateActionBar  bar;

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
    contentPanel.setLayout( new BorderLayout() );
    bar = new MeMateActionBar( new Color( 225, 225, 225 ), Color.black );

    deriveFontsAndSetLayout();
    addActionBar();
    setIconImage(
        Toolkit.getDefaultToolkit().getImage( getClass().getClassLoader().getResource( "frameiconblue2.png" ) ) );
    setTitle( "MeMate" );
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    setMinimumSize( new Dimension( 380, 510 ) );
    setSize( 1170, 770 );
    setLocationRelativeTo( null );
  }

  /**
   * Fügt die Actionbar hinzu.
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
        contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
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
        contentPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );
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
            ConsumptionRate.getInstance().addGraph();
            contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
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
            CreditHistory.getInstance().addChart();
            contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
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
            contentPanel.setBorder( new EmptyBorder( 0, 0, 5, 5 ) );
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
            contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
            contentPanel.repaint();
            contentPanel.revalidate();
          }
        } );
    adminViewButton.setEnabled( false );

    bar.addVariableGlue();
    undoButton = bar.addActionButton( undoBlackIcon, undoWhiteIcon, "Rückgänig", "Letzte Aktion rückgängig machen", new Runnable()
    {

      @Override
      public void run()
      {
        ServerCommunication.getInstance().undoLastAction();
      }
    } );
    undoButton.setEnabled( false );
    darkModeButton = bar.addActionButton( darkModeIconBlack, darkModeIconWhite, "Darkmode", "Wechselt in den Darkmode", new Runnable()
    {
      @Override
      public void run()
      {
        bar.toggleDarkmode();
        if ( bar.darkModeOn() )
        {
          MeMateUIManager.showDarkMode();
          darkModeButton.setIcon( dayModeIconWhite );
          darkModeButton.setPressedIcon( dayModeIconBlack );
          darkModeButton.setTooltip( "Wechselt in den Daymode" );
          darkModeButton.setTitle( "Daymode" );


          try
          {
            File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
            InputStream input = new FileInputStream( file );
            Properties userProperties = new Properties();
            userProperties.load( input );
            userProperties.setProperty( "Darkmode", "on" );
            OutputStream output = new FileOutputStream( file );
            userProperties.store( output, "" );
          }
          catch ( IOException exception )
          {
            System.out.println( "Der Darkmodestatus konnte nicht gespeichert werden." );
            exception.printStackTrace();
          }
        }
        else
        {
          MeMateUIManager.showDayMode();
          darkModeButton.setIcon( darkModeIconBlack );
          darkModeButton.setPressedIcon( darkModeIconWhite );
          darkModeButton.setTooltip( "Wechselt in den Darkmode" );
          darkModeButton.setTitle( "Darkmode" );
          try
          {
            File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
            InputStream input = new FileInputStream( file );
            Properties userProperties = new Properties();
            userProperties.load( input );
            userProperties.setProperty( "Darkmode", "off" );
            OutputStream output = new FileOutputStream( file );
            userProperties.store( output, "" );
          }
          catch ( IOException exception )
          {
            System.out.println( "Der Darkmodestatus konnte nicht gespeichert werden." );
            exception.printStackTrace();
          }
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
        setUndoButtonEnabled( false );
        try
        {
          File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
          InputStream input = new FileInputStream( file );
          Properties userProperties = new Properties();
          userProperties.load( input );
          userProperties.setProperty( "SessionID", "null" );
          OutputStream output = new FileOutputStream( file );
          userProperties.store( output, "" );
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
    try
    {
      File file = new File( System.getenv( "APPDATA" ) + File.separator + "MeMate" + File.separator + "userconfig.properties" );
      InputStream input = new FileInputStream( file );
      Properties userProperties = new Properties();
      userProperties.load( input );

      if ( userProperties.getProperty( "Darkmode" ) != null && userProperties.getProperty( "Darkmode" ).equals( "on" ) )
      {
        darkModeButton.setIcon( dayModeIconBlack );
        darkModeButton.setPressedIcon( dayModeIconWhite );
        darkModeButton.setTooltip( "Wechselt in den Daymode" );
        darkModeButton.setTitle( "Daymode" );
        bar.toggleDarkmode();
        MeMateUIManager.showDarkMode();
      }
      else
      {
        MeMateUIManager.showDayMode();
      }
    }
    catch ( IOException exception )
    {
      System.out.println( "Die SessionID konnte nicht gespeichert werden." );
      exception.printStackTrace();
    }
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
    contentPanel.setBackground( UIManager.getColor( "DefaultBrightColor" ) );
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

  /**
   * Bestimmt ob der undoButton aktiviert sein soll oder nicht
   * 
   * @param state true or false
   */
  public void setUndoButtonEnabled( boolean state )
  {
    undoButton.setEnabled( state );
  }
}
