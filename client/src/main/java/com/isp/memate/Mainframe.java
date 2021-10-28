/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.isp.memate.actionbar.MeMateActionBar;
import com.isp.memate.actionbar.MeMateActionBarButton;
import com.isp.memate.panels.Adminview;
import com.isp.memate.panels.ConsumptionRate;
import com.isp.memate.panels.CreditHistory;
import com.isp.memate.panels.Dashboard;
import com.isp.memate.panels.DrinkManager;
import com.isp.memate.panels.History;
import com.isp.memate.panels.Settings;
import com.isp.memate.panels.Social;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.PropertyHelper;

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
  Cache                         cache          = Cache.getInstance();
  private final Color           color          = UIManager.getColor( "AppColor" );
  private final JPanel          contentPanel   = new JPanel()
                                               {
                                                 @Override
                                                 public Component add( Component comp )
                                                 {
                                                   super.add( comp );
                                                   repaint();
                                                   revalidate();
                                                   return comp;
                                                 }
                                               };
  private final JLabel          helloUserLabel = new JLabel( "Hallo User" );
  private final JLabel          balanceLabel   = new JLabel();
  public final JPanel           headerPanel    = new JPanel();
  public JPanel                 burgerButton;
  private MeMateActionBarButton dashboardButton;
  private MeMateActionBarButton logoutButton;
  public MeMateActionBarButton  settingsButton;
  private MeMateActionBarButton undoButton;
  public MeMateActionBar        bar;

  /**
   * Setzt das Layout und nimmt einige Änderungen an den Komponenten vor.
   * Außerdem wird die Logik für den Logout-Button definiert.
   */
  public Mainframe()
  {
    showLoadingDialogWhileReceivingData();

    GUIObjects.mainframe = this;
    contentPanel.setLayout( new BorderLayout() );
    contentPanel.add( new Dashboard() );

    deriveFontsAndSetLayout();
    setIconImages( (List<? extends Image>) UIManager.get( "frame.icons" ) );
    setTitle( "MeMate" );
    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    setMinimumSize( new Dimension( 380, 550 ) );
    setSize( 1185, 770 );
    setLocationRelativeTo( null );
    add( contentPanel, BorderLayout.CENTER );
    add( headerPanel, BorderLayout.NORTH );
    setHelloLabelText( cache.getDisplayname() );
    addActionBar();
    requestFocus();
    setVisible( true );
  }

  private void showLoadingDialogWhileReceivingData()
  {
    final JDialog loadingDialog = new JDialog();
    final JPanel loadingPanel = new JPanel( new BorderLayout() );
    loadingPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
    loadingPanel.add( new JLabel( UIManager.getIcon( "loading.icon" ) ), BorderLayout.NORTH );
    final JLabel textLoadingLabel = new JLabel( "Receiving Data..." );
    textLoadingLabel.setFont( textLoadingLabel.getFont().deriveFont( 20f ) );
    textLoadingLabel.setForeground( new Color( 28, 205, 205 ) );
    loadingPanel.add( textLoadingLabel, BorderLayout.CENTER );
    loadingDialog.add( loadingPanel );
    loadingDialog.setUndecorated( true );
    loadingDialog.pack();
    loadingDialog.setLocationRelativeTo( null );
    loadingDialog.setVisible( true );
    synchronized ( cache.getReceivedAllInformationsSync() )
    {
      try
      {
        cache.getReceivedAllInformationsSync().wait();
      }
      catch ( final InterruptedException e )
      {
        // Happens if someone interrupts this thread.
      }
    }
    loadingDialog.dispose();
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
    if ( PropertyHelper.getDarkModeProperty() )
    {
      bar.showDarkmode();
    }
    add( bar, BorderLayout.WEST );
  }

  private void addSettingsButton()
  {
    settingsButton =
        bar.addActionButton( UIManager.getIcon( "settings.icon.black" ), UIManager.getIcon( "settings.icon.white" ), "Settings",
            "Öffnet die Einstellungen", color, () ->
            {
              contentPanel.removeAll();
              contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
              contentPanel.add( new Settings() );
            } );
  }

  private void addDefaultButtons()
  {
    dashboardButton = bar.addActionButton( UIManager.getIcon( "dashboard.icon.black" ), UIManager.getIcon( "dashboard.icon.white" ),
        "Dashboard", "Dashboard öffnen", color, () ->
        {
          contentPanel.removeAll();
          contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
          contentPanel.add( new Dashboard() );
        } );
    bar.addActionButton( UIManager.getIcon( "history.icon.black" ), UIManager.getIcon( "history.icon.white" ), "Historie",
        "Historie öffnen", color, () ->
        {
          contentPanel.removeAll();
          contentPanel.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
          contentPanel.add( new History() );
        } );
    bar.addActionButton( UIManager.getIcon( "consumption.icon.black" ), UIManager.getIcon( "consumption.icon.white" ), "Verbrauchsrate",
        "Hier können sie ihren durchschnittlichen Konsum sehen", color, () ->
        {
          contentPanel.removeAll();
          contentPanel.setBorder( new EmptyBorder( 4, 0, 4, 0 ) );
          contentPanel.add( new ConsumptionRate() );
        } );
    bar.addActionButton( UIManager.getIcon( "creditHistory.icon.black" ), UIManager.getIcon( "creditHistory.icon.white" ),
        "Guthabenverlauf",
        "Hier können sie den Verlauf ihres Guthabens betrachten", color, () ->
        {
          contentPanel.removeAll();
          contentPanel.setBorder( new EmptyBorder( 4, 0, 4, 0 ) );
          contentPanel.add( new CreditHistory() );
        } );
    bar.addActionButton( UIManager.getIcon( "social.icon.black" ), UIManager.getIcon( "social.icon.white" ), "Scoreboard", "Scoreboard",
        color, () ->
        {
          contentPanel.removeAll();
          contentPanel.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
          contentPanel.add( new Social() );
        } );
  }

  private void addLogoutButton()
  {
    logoutButton = bar.addActionButton( UIManager.getIcon( "logout.icon.black" ), UIManager.getIcon( "logout.icon.white" ), "Logout",
        "Ausloggen", () ->
        {
          cache.setSessionIDValid( false );
          cache.setAdminUser( false );
          ServerCommunication.getInstance().logout();
          setUndoButtonEnabled( false );
          //Resetting the sessionID
          PropertyHelper.setProperty( "SessionID", "null" );
          dispose();
          final Login login = new Login();
          login.setVisible( true );
          logoutButton.setBackground( bar.getBackground() );
        } );
  }


  private void addUndoButton()
  {
    bar.addVariableGlue();
    undoButton = bar.addActionButton( UIManager.getIcon( "undo.icon.black" ), UIManager.getIcon( "undo.icon.white" ), "Rückgänig",
        "Letzte Aktion rückgängig machen", () ->
        {
          ServerCommunication.getInstance().undoLastAction();
          undoButton.setBackground( bar.getBackground() );
        } );
    undoButton.setEnabled( false );
  }

  private void toggleAdminButtons()
  {
    if ( cache.isUserAdmin() )
    {
      bar.addActionButton( UIManager.getIcon( "drinkmanager.icon.black" ), UIManager.getIcon( "drinkmanager.icon.white" ),
          "Getränkemanager",
          "Getränkemanager öffnen", color, () ->
          {
            contentPanel.removeAll();
            contentPanel.add( new DrinkManager() );
            contentPanel.setBorder( new EmptyBorder( 0, 0, 5, 5 ) );
            contentPanel.repaint();
            contentPanel.revalidate();
          } );
      bar.addActionButton( UIManager.getIcon( "adminview.icon.black" ), UIManager.getIcon( "adminview.icon.white" ), "Adminview",
          "Adminansicht öffnen", color, () ->
          {
            contentPanel.removeAll();
            contentPanel.add( new Adminview() );
            contentPanel.setBorder( new EmptyBorder( 5, 0, 5, 5 ) );
            contentPanel.repaint();
            contentPanel.revalidate();
          } );
    }
  }

  /**
   * Updates the text for the GreetingsLabel
   *
   * @param displayName the users displayName
   */
  public void setHelloLabelText( final String displayName )
  {
    helloUserLabel.setText( "Hallo " + displayName );
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

    updateBalanceLabel( cache.getBalance() );

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
   * Updates the balancesLabel (red indicates negative balance).
   *
   * @param newBalance the updated balance.
   */
  void updateBalanceLabel( final float newBalance )
  {
    final float rounded = (float) Math.round( newBalance * 100 ) / 100;
    balanceLabel.setText( String.format( "Kontostand: %.2f €", rounded ) );
    if ( rounded >= 0.0 )
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
}
