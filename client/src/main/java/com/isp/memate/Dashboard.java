/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.isp.memate.util.MeMateUIManager;
import com.isp.memate.util.SwingUtil;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Auf dem {@link Dashboard} soll der Nutzer die Auswahl an Getränken sehen
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
  private static final Dashboard            instance      = new Dashboard( Mainframe.getInstance() );
  private final Mainframe                   mainFrame;
  private final ImageIcon                   infoIcon      = new ImageIcon( getClass().getClassLoader().getResource( "infoicon.png" ) );
  private final ImageIcon                   infoIconWhite =
      new ImageIcon( getClass().getClassLoader().getResource( "infoicon_white.png" ) );
  private final JLabel                      infoIconLabel = new JLabel( infoIcon );
  private final JScrollPane                 scrollpane;
  private ArrayList<DrinkConsumptionButton> buttonList    = new ArrayList<>();

  /**
   * @return static instance of Dashboard
   */
  public static Dashboard getInstance()
  {
    return instance;
  }

  /**
   * Passt Layout, Hintergrund und Borders an.
   * 
   * @param mainFrame Parent-Mainframe für Statuszugriff/updates
   */
  public Dashboard( Mainframe mainFrame )
  {
    this.mainFrame = mainFrame;
    scrollpane = new JScrollPane( createDrinkButtonPanel() );
    scrollpane.getVerticalScrollBar().setUnitIncrement( 16 );
    scrollpane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );

    final JPanel upperPanel = MeMateUIManager.createJPanel();
    upperPanel.setLayout( new GridBagLayout() );
    upperPanel.setBackground( UIManager.getColor( "DefaultBrightColor" ) );


    setLayout( new BorderLayout() );
    add( upperPanel, BorderLayout.NORTH );
    add( scrollpane, BorderLayout.CENTER );
    add( createLowerPanel(), BorderLayout.SOUTH );
    setBackground( UIManager.getColor( "DefaultBrightColor" ) );
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
    final SpinnerNumberModel spinnerModel = new SpinnerNumberModel( 1, 1, 1000, 1 );
    final JSpinner valueSpinner = new JSpinner( spinnerModel );
    //    String pattern = "0€";
    //    JSpinner.NumberEditor editor = new JSpinner.NumberEditor( valueSpinner, pattern );
    //    valueSpinner.setEditor( editor );

    final JButton aufladenButton = MeMateUIManager.createNormalButton( "button" );
    aufladenButton.setText( "Einzahlen" );
    aufladenButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        Object value = valueSpinner.getValue();
        System.out.println( String.valueOf( valueSpinner.getValue() ) );
        int result =
            JOptionPane.showConfirmDialog( Dashboard.this, "<html>Wollen Sie wirklich <b>" + value + "€</b> einzahlen?",
                "Guthaben hinzufügen", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE );
        if ( result == JOptionPane.YES_OPTION )
        {
          ServerCommunication sc = ServerCommunication.getInstance();
          sc.addBalance( (int) value );
          ServerCommunication.getInstance().getBalance( ServerCommunication.getInstance().currentUser );
          Mainframe.getInstance().setUndoButtonEnabled( true );
        }
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

    String infoTextToolTip = "<html>" + infoText1 + "<br>" + infoText2 + "</html>";
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
    panel.add( aufladenButton, new CC() ); // Wenn der Button ein MeMateActionBarButton ist, dann getbarbutton ergänzen
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
  public JPanel createDrinkButtonPanel()
  {
    buttonList.clear();
    JPanel panel = MeMateUIManager.createJPanel();
    panel.setLayout( new WrapLayout( FlowLayout.LEFT ) );

    for ( String drink : ServerCommunication.getInstance().getDrinkNames() )
    {
      if ( ServerCommunication.getInstance().getAmount( drink ) == 0 )
      {
        continue;
      }
      String drinkName = drink;
      Float drinkPriceAsFloat = ServerCommunication.getInstance().getPrice( drink );
      String drinkPrice = String.valueOf( drinkPriceAsFloat );
      ImageIcon drinkIcon = ServerCommunication.getInstance().getIcon( drink );
      Image image = drinkIcon.getImage();
      Image newImage;
      if ( drinkIcon.getIconHeight() > 220 || drinkIcon.getIconWidth() > 250 )
      {
        double scale = 220.0 / drinkIcon.getIconHeight();
        int height = 220;
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
      DrinkConsumptionButton button = new DrinkConsumptionButton( mainFrame, drinkPrice, drinkName, new ImageIcon( newImage ) );
      buttonList.add( button );
      panel.add( button );
    }

    panel.setBackground( UIManager.getColor( "DefaultBrightColor" ) );
    return panel;
  }

  @SuppressWarnings( "javadoc" )
  public void updateButtonpanel()
  {
    scrollpane.setViewportView( createDrinkButtonPanel() );
  }

  /**
   * Zeigt einen neuen Dialog an, falls der Preis des Clients und des Servers nicht übereinstimmen sollten.
   * 
   * @param name Name des Geträmnks
   * @param price Preis des Getränks
   */
  public void showPriceChangedDialog( String name, Float price )
  {

    int result = JOptionPane.showConfirmDialog( mainFrame,
        String.format(
            "<html>Der Preis von <b>" + name + "</b> hat sich auf <b>%.2f€</b> geändert.\nWollen Sie das Getränk trotzdem kaufen?",
            price ),
        "Getränkepreis hat sich geändert", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );
    if ( result == JOptionPane.YES_OPTION )
    {
      ServerCommunication.getInstance().consumeDrink( name );
      ServerCommunication.getInstance().getBalance( ServerCommunication.getInstance().currentUser );
    }
  }

  /**
   * Wenn die Anzahl des Getränks 0 beträgt, so wird ein Dialog angezeigt, dass
   * das gewünschte Getränk leer ist.
   * 
   * @param name Getränkenaame
   */
  public void showNoMoreDrinksDialog( String name )
  {
    JOptionPane.showMessageDialog( mainFrame, "<html><b>" + name + " </b>ist leider nicht mehr verfügbar</html>", "Getränk nicht verfügbar",
        JOptionPane.ERROR_MESSAGE, null );
  }

  /**
   * Wenn der State des Darkmodes gewechselt wird, so wird auch das Icon geändert.
   */
  public void toggleInfoIcon()
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      infoIconLabel.setIcon( infoIconWhite );
    }
    else
    {
      infoIconLabel.setIcon( infoIcon );
    }
  }

  /**
   * Resetet alle drinkButtons, dies tritt nur auf wenn man bereits ein
   * Getränk angeklickt hat und nun ein weiteres anklicken möchte.
   */
  public void resetAllDrinkButtons()
  {
    for ( DrinkConsumptionButton drinkConsumptionButton : buttonList )
    {
      if ( drinkConsumptionButton.askWhetherToReallyConsumeLabelIsActive )
      {
        drinkConsumptionButton.reset();
      }
    }
  }
}
