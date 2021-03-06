/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;
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

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.InfoIcon;
import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.SwingUtil;

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
class Dashboard extends JPanel
{
  private final Mainframe                         mainFrame;
  private final ImageIcon                         infoIcon      =
      new ImageIcon( getClass().getClassLoader().getResource( "infoicon.png" ) );
  private final ImageIcon                         infoIconWhite =
      new ImageIcon( getClass().getClassLoader().getResource( "infoicon_white.png" ) );
  private final JLabel                            infoIconLabel = new JLabel( infoIcon );
  private final JScrollPane                       scrollpane;
  private final ArrayList<DrinkConsumptionButton> buttonList    = new ArrayList<>();
  Cache                                           cache         = Cache.getInstance();

  /**
   * Passt Layout, Hintergrund und Borders an.
   *
   * @param mainFrame Parent-Mainframe für Statuszugriff/updates
   */
  Dashboard( final Mainframe mainFrame )
  {
    this.mainFrame = mainFrame;
    scrollpane = new JScrollPane( createDrinkButtonPanel() );
    scrollpane.getVerticalScrollBar().setUnitIncrement( 16 );
    scrollpane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    scrollpane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

    setLayout( new BorderLayout() );
    add( scrollpane, BorderLayout.CENTER );
    add( createLowerPanel(), BorderLayout.SOUTH );
    MeMateUIManager.registerIconLabel( infoIconLabel, infoIcon, infoIconWhite );
    MeMateUIManager.registerPanel( "default", this );
    MeMateUIManager.registerScrollPane( "scroll", scrollpane );
  }

  /**
   * Erstellt das lowerPanel und setzt das Layout.
   * Das lowerPanel beinhaltet die Komponenten um das Konto aufzuladen.
   * Außerdem zeigt es noch eine kleine Info zur Einzahlung an.
   */
  private JPanel createLowerPanel()
  {
    final JPanel panel = MeMateUIManager.createJPanel();
    final SpinnerNumberModel spinnerModel = new SpinnerNumberModel( 1, -50, 1000, 1 );
    final JSpinner valueSpinner = new JSpinner( spinnerModel );
    MeMateUIManager.registerSpinner( valueSpinner );

    final JButton aufladenButton = MeMateUIManager.createButton( "button" );
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
      final int result =
          JOptionPane.showConfirmDialog( Dashboard.this, description,
              title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE );
      if ( result == JOptionPane.YES_OPTION )
      {
        final ServerCommunication sc = ServerCommunication.getInstance();
        sc.addBalance( (int) value );
        ServerCommunication.getInstance().getBalance();
        Mainframe.getInstance().setUndoButtonEnabled( true );
      }
    } );

    final JLabel aufladenlabel = MeMateUIManager.createJLabel();
    final String infoText1 =
        "Einzahlung sind nur in Höhe von gültigen Kombination von 1€ und 2€ Münzen, 5€ Scheinen, 10€ Scheinen und 20€ Scheinen möglich.";
    final String infoText2 =
        "Einmal eingezahltes Guthaben kann nicht wieder ausgezahlt werden und muss durch den Konsum von Getränken aufgebraucht werden.";
    final JLabel infoTextLabel1 = MeMateUIManager.createJLabel();
    final JLabel infoTextLabel2 = MeMateUIManager.createJLabel();
    aufladenlabel.setText( "Kontostand aufladen" );
    infoTextLabel1.setText( infoText1 );
    infoTextLabel2.setText( infoText2 );

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
    buttonList.clear();
    final JPanel panel = MeMateUIManager.createJPanel();
    panel.setLayout( new WrapLayout( FlowLayout.LEFT ) );

    for ( final String drink : cache.getDrinkNames() )
    {
      if ( cache.getAmount( drink ) == 0 )
      {
        continue;
      }
      final String drinkName = drink;
      final Float drinkPriceAsFloat = cache.getPrice( drink );
      final String drinkPrice = String.valueOf( drinkPriceAsFloat );
      final ImageIcon drinkIcon = cache.getIcon( drink );
      final Image image = drinkIcon.getImage();
      Image newImage;
      if ( drinkIcon.getIconHeight() > 220 || drinkIcon.getIconWidth() > 250 )
      {
        final double scale = 220.0 / drinkIcon.getIconHeight();
        final int height = 220;
        int width = (int) (drinkIcon.getIconWidth() * scale);
        if ( width > 250 )
        {
          width = 250;
        }
        newImage = image.getScaledInstance( width, height, Image.SCALE_SMOOTH );
      }
      else
      {
        newImage = image.getScaledInstance( 70, 220, Image.SCALE_SMOOTH );
      }
      final DrinkConsumptionButton button = new DrinkConsumptionButton( mainFrame, drinkPrice, drinkName, new ImageIcon( newImage ) );
      buttonList.add( button );
      panel.add( button );
    }
    return panel;
  }

  void updateButtonpanel()
  {
    final ReentrantLock lock = ServerCommunication.getInstance().lock;
    lock.lock();
    try
    {
      scrollpane.setViewportView( createDrinkButtonPanel() );
      MeMateUIManager.setUISettings();
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
   * Zeigt einen neuen Dialog an, falls der Preis des Clients und des Servers nicht übereinstimmen sollten.
   *
   * @param name Name des Geträmnks
   * @param price Preis des Getränks
   */
  void showPriceChangedDialog( final String name, final Float price )
  {

    final int result = JOptionPane.showConfirmDialog( mainFrame,
        String.format(
            "<html>Der Preis von <b>" + name + "</b> hat sich auf <b>%.2f€</b> geändert.\nWollen Sie das Getränk trotzdem kaufen?",
            price ),
        "Getränkepreis hat sich geändert", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
    if ( result == JOptionPane.YES_OPTION )
    {
      ServerCommunication.getInstance().consumeDrink( name );
      ServerCommunication.getInstance().getBalance();
    }
  }

  /**
   * Wenn die Anzahl des Getränks 0 beträgt, so wird ein Dialog angezeigt, dass
   * das gewünschte Getränk leer ist.
   *
   * @param name Getränkenaame
   */
  void showNoMoreDrinksDialog( final String name )
  {
    JOptionPane.showMessageDialog( mainFrame, "<html><b>" + name + " </b>ist leider nicht mehr verfügbar</html>", "Getränk nicht verfügbar",
        JOptionPane.ERROR_MESSAGE, null );
  }


  /**
   * Resetet alle drinkButtons, dies tritt nur auf wenn man bereits ein
   * Getränk angeklickt hat und nun ein weiteres anklicken möchte.
   */
  void resetAllDrinkButtons()
  {
    for ( final DrinkConsumptionButton drinkConsumptionButton : buttonList )
    {
      if ( drinkConsumptionButton.askWhetherToReallyConsumeLabelIsActive )
      {
        drinkConsumptionButton.reset();
      }
    }
  }

  /**
   * Updated die Infoicons der buttons
   */
  public void updateAllDrinkButtonIcons()
  {
    for ( final DrinkConsumptionButton drinkConsumptionButton : buttonList )
    {
      drinkConsumptionButton.getInfoButton().setIcon( new InfoIcon() );
    }
  }
}
