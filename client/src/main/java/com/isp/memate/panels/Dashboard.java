/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.isp.memate.Cache;
import com.isp.memate.Drink;
import com.isp.memate.ServerCommunication;
import com.isp.memate.components.DrinkConsumptionButton;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.SwingUtil;
import com.isp.memate.util.WrapLayout;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Auf dem Dashboard soll der Nutzer die Auswahl an Getränken sehen
 * und sich nach Bedarf, welche kaufen können.
 * Tut er dies, so wird der Preis des Getränks von seinem Guthaben abgezogen.
 * Außerdem hat der Benutzer auf dem Dashboard die Möglichkeit sein Kontostand aufzuladen
 * und Getränkeinformationen zu erhalten.
 *
 * @author nwe
 * @since 15.10.2019
 */
public class Dashboard extends JPanel
{
  private JScrollPane                                scrollpane;
  private final Map<Integer, DrinkConsumptionButton> buttonMap = new HashMap<>();
  Cache                                              cache     = Cache.getInstance();

  /**
   * Passt Layout, Hintergrund und Borders an.
   *
   */
  public Dashboard()
  {
    GUIObjects.currentPanel = this;
    initScrollPane();

    setLayout( new BorderLayout() );
    add( scrollpane, BorderLayout.CENTER );
    add( createLowerPanel(), BorderLayout.SOUTH );
  }

  private void initScrollPane()
  {
    scrollpane = new JScrollPane( createDrinkButtonPanel() );
    scrollpane.getVerticalScrollBar().setUnitIncrement( 16 );
    scrollpane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    scrollpane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
  }


  /**
   * Erstellt das lowerPanel und setzt das Layout.
   * Das lowerPanel beinhaltet die Komponenten um das Konto aufzuladen.
   * Außerdem zeigt es noch eine kleine Info zur Einzahlung an.
   */
  private JPanel createLowerPanel()
  {
    final JPanel panel = new JPanel();
    final SpinnerNumberModel spinnerModel = new SpinnerNumberModel( 1, -50, 1000, 1 );
    final JSpinner valueSpinner = new JSpinner( spinnerModel );

    final JButton aufladenButton = new JButton( "button" );
    aufladenButton.setText( "Einzahlen" );
    aufladenButton.addActionListener( e ->
    {
      final Object value = valueSpinner.getValue();
      String description = "<html>Wollen Sie wirklich <b>" + value + "€</b> einzahlen?";
      String title = "Guthaben hinzufügen";
      if ( (int) value < 0 )
      {
        description = "<html>Wollen Sie wirklich <b>" + (int) value * -1 + "€</b> aus der Kasse nehmen?";
        title = "Geld leihen";
      }
      if ( (int) value != 0 )
      {

        final int result =
            JOptionPane.showConfirmDialog( Dashboard.this, description,
                title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );
        if ( result == JOptionPane.YES_OPTION )
        {
          final ServerCommunication sc = ServerCommunication.getInstance();
          sc.addBalance( (int) value );
          GUIObjects.mainframe.setUndoButtonEnabled( true );
        }
      }
    } );

    final JLabel aufladenlabel = new JLabel();
    final String infoText1 =
        "Einzahlung sind nur in Höhe von gültigen Kombination von 1€ und 2€ Münzen, 5€ Scheinen, 10€ Scheinen und 20€ Scheinen möglich.";
    final String infoText2 =
        "Einmal eingezahltes Guthaben kann nicht wieder ausgezahlt werden und muss durch den Konsum von Getränken aufgebraucht werden.";
    final JLabel infoTextLabel1 = new JLabel();
    final JLabel infoTextLabel2 = new JLabel();
    aufladenlabel.setText( "Kontostand aufladen" );
    infoTextLabel1.setText( infoText1 );
    infoTextLabel2.setText( infoText2 );

    JLabel infoIconLabel = new JLabel();
    infoIconLabel
        .setIcon( MeMateUIManager.getDarkModeState() ? UIManager.getIcon( "info.icon.white" ) : UIManager.getIcon( "info.icon.black" ) );
    infoIconLabel.setBorder( new MatteBorder( 0, 20, 0, 0, UIManager.getColor( "separator.background" ) ) );

    final String infoTextToolTip = "<html>" + infoText1 + "<br>" + infoText2 + "</html>";
    infoTextLabel1.setToolTipText( infoTextToolTip );
    infoTextLabel2.setToolTipText( infoTextToolTip );

    infoTextLabel1.setFont( infoTextLabel1.getFont().deriveFont( 13f ) );
    infoTextLabel2.setFont( infoTextLabel2.getFont().deriveFont( 13f ) );
    aufladenlabel.setFont( aufladenlabel.getFont().deriveFont( 15f ) );

    panel.setLayout( new MigLayout( new LC().flowX().fill().insets( "10px", "0", "0", "0" ) ) );
    panel.add( aufladenlabel, new CC().spanX( 2 ) );
    panel.add( infoIconLabel, new CC().spanY() );
    panel.add( infoTextLabel1, new CC().minWidth( "0" ).pushX().wrap() );
    panel.add( valueSpinner, new CC() );
    panel.add( aufladenButton, new CC() );
    panel.add( infoTextLabel2, new CC().skip( 1 ).minWidth( "0" ).pushX() );

    SwingUtil.setPreferredWidth( 50, valueSpinner );

    panel.setBorder( new EmptyBorder( 0, 20, 10, 0 ) );
    return panel;
  }


  /**
   * Es wird eine Liste von der Klasse {@link ServerCommunication}
   * genommen, welche die Namen aller Getränke enthält.
   * Auf Grundlage dieser Liste, wird für jedes Getränk (wenn Anzahl>0)
   * ein Button, mit zugehörigen Preis und Bild
   * erstellt und dem Panel hinzugefügt.
   *
   * @return Das Panel in welchem man die angebotenen Getränke anklicken kann.
   */
  private JPanel createDrinkButtonPanel()
  {
    buttonMap.clear();
    final JPanel panel = new JPanel();
    panel.setLayout( new WrapLayout( FlowLayout.LEFT ) );

    for ( final Drink drink : cache.getDrinks().values() )
    {
      if ( drink.getAmount() == 0 )
      {
        continue;
      }
      final DrinkConsumptionButton button = new DrinkConsumptionButton( drink );
      buttonMap.put( drink.getId(), button );
      panel.add( button );
    }
    return panel;
  }

  public void updateButtonpanel()
  {
    final ReentrantLock lock = ServerCommunication.getInstance().lock;
    lock.lock();
    try
    {
      scrollpane.setViewportView( createDrinkButtonPanel() );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( exception.getMessage() );
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * Shows a dialog, if the drinkprice of the client differs from the one on the sever.
   *
   * @param drink the matching drinkObject
   */
  public void showPriceChangedDialog( final Drink drink )
  {

    final int result = JOptionPane.showConfirmDialog( Dashboard.this,
        String.format(
            "<html>Der Preis von <b>" + drink.getName()
                + "</b> hat sich auf <b>%.2f€</b> geändert.\nWollen Sie das Getränk trotzdem kaufen?",
            drink.getPrice() ),
        "Getränkepreis hat sich geändert", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
    if ( result == JOptionPane.YES_OPTION )
    {
      ServerCommunication.getInstance().consumeDrink( drink );
    }
  }

  /**
   * Wenn die Anzahl des Getränks 0 beträgt, so wird ein Dialog angezeigt, dass
   * das gewünschte Getränk leer ist.
   *
   * @param name Getränkenaame
   */
  public void showNoMoreDrinksDialog( final String name )
  {
    JOptionPane.showMessageDialog( Dashboard.this, "<html><b>" + name + " </b>ist leider nicht mehr verfügbar</html>",
        "Getränk nicht verfügbar",
        JOptionPane.ERROR_MESSAGE, null );
  }


  /**
   * All DrinkConsumptionButtons are being set to default State, in order to only show the buy state in one
   * button at the time.
   * 
   * @param source the {@link DrinkConsumptionButton} that should not get reseted.
   */
  public void resetAllDrinkButtons( DrinkConsumptionButton source )
  {
    for ( final DrinkConsumptionButton drinkConsumptionButton : buttonMap.values() )
    {
      if ( DrinkConsumptionButton.STATE.BUY.equals( drinkConsumptionButton.getCURRENT_STATE() ) )
      {
        if ( !drinkConsumptionButton.equals( source ) )
        {
          drinkConsumptionButton.switchState( DrinkConsumptionButton.STATE.DEFAULT );
          drinkConsumptionButton.setBackground( UIManager.getColor( "Button.background" ) );
        }
      }
    }
  }
}
