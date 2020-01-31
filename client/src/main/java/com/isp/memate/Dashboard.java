/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
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
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import com.isp.memate.util.SwingUtil;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * Auf dem {@link Dashboard} soll der Nutzer die Auswahl an Getränken sehen
 * und sich nach Bedarf, welche kaufen können.
 * Tut er dies, so wird der Preis des Getränks von seinem Guthaben abgezogen.
 * Außerdem hat der Benutzer auf dem Dashboard die Möglichkeit sein Kontostand aufzuladen.
 * 
 * @author nwe
 * @since 15.10.2019
 */
public class Dashboard extends JPanel
{
  private static final Dashboard            instance   = new Dashboard( Mainframe.getInstance() );
  private final Mainframe                   mainFrame;
  private final ImageIcon                   undoIcon   = new ImageIcon( getClass().getClassLoader().getResource( "undo.png" ) );
  private final JScrollPane                 scrollpane;
  private ArrayList<DrinkConsumptionButton> buttonList = new ArrayList<>();
  final JButton                             undoButton = new JButton( undoIcon );

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

    final JPanel upperPanel = new JPanel( new GridBagLayout() );
    final JLabel consumeLabel = new JLabel( "Getränk konsumieren" );
    consumeLabel.setHorizontalAlignment( JLabel.CENTER );
    consumeLabel.setFont( consumeLabel.getFont().deriveFont( 20f ) );
    upperPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );
    GridBagConstraints consumeLabelConstraints = new GridBagConstraints();
    consumeLabelConstraints.gridx = 1;
    consumeLabelConstraints.gridy = 0;
    consumeLabelConstraints.weightx = 1;
    upperPanel.add( consumeLabel, consumeLabelConstraints );
    GridBagConstraints undoButtonConstraints = new GridBagConstraints();
    undoButtonConstraints.gridx = 2;
    undoButtonConstraints.gridy = 0;
    upperPanel.add( undoButton, undoButtonConstraints );
    upperPanel.setBackground( UIManager.getColor( "TabbedPane.highlight" ) );
    undoButton.setEnabled( false );
    undoButton.setToolTipText( "Letzte Aktion rückgängig machen" );
    undoButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        ServerCommunication.getInstance().undoLastAction();
      }
    } );


    setLayout( new BorderLayout() );
    add( upperPanel, BorderLayout.NORTH );
    add( scrollpane, BorderLayout.CENTER );
    add( createLowerPanel(), BorderLayout.SOUTH );
    setBackground( Color.white );
  }

  /**
   * Erstellt das lowerPanel und setzt das Layout.
   * Das lowerPanel beinhaltet die Komponenten um das Konto aufzuladen.
   * Außerdem zeigt es noch eine kleine Info zur Einzahlung an.
   */
  private JPanel createLowerPanel()
  {
    final JPanel panel = new JPanel();
    final SpinnerNumberModel spinnerModel = new SpinnerNumberModel( 1, 1, 1000, 1 );
    final JSpinner valueSpinner = new JSpinner( spinnerModel );
    //    String pattern = "0€";
    //    JSpinner.NumberEditor editor = new JSpinner.NumberEditor( valueSpinner, pattern );
    //    valueSpinner.setEditor( editor );

    final JButton aufladenButton = new JButton( "Einzahlen" );
    final JSeparator seperator = new JSeparator( JSeparator.VERTICAL );
    final JLabel aufladenlabel = new JLabel( "Kontostand aufladen" );
    final String infoText1 =
        "Einzahlung sind nur in Höhe von gültigen Kombination von 1€ und 2€ Münzen, 5€ Scheinen, 10€ Scheinen und 20€ Scheinen möglich.";
    final String infoText2 =
        "Einmal eingezahltes Guthaben kann nicht wieder ausgezahlt werden und muss durch den Konsum von Getränken aufgebraucht werden.";
    final JLabel infoTextLabel1 = new JLabel( infoText1 );
    final JLabel infoTextLabel2 = new JLabel( infoText2 );

    final ImageIcon infoIcon = new ImageIcon( getClass().getClassLoader().getResource( "infoicon.png" ) );
    final JLabel infoIconLabel = new JLabel( infoIcon );
    infoIconLabel.setBorder( new MatteBorder( 0, 1, 0, 0, UIManager.getColor( "separator.background" ) ) );

    String infoTextToolTip = "<html>" + infoText1 + "<br>" + infoText2 + "</html>";
    infoTextLabel1.setToolTipText( infoTextToolTip );
    infoTextLabel2.setToolTipText( infoTextToolTip );

    infoTextLabel1.setFont( infoTextLabel1.getFont().deriveFont( 13f ) );
    infoTextLabel2.setFont( infoTextLabel2.getFont().deriveFont( 13f ) );
    aufladenlabel.setFont( aufladenlabel.getFont().deriveFont( 15f ) );

    panel.setLayout( new MigLayout( new LC().flowX().fill().insets( "10px", "0", "0", "0" ) ) );
    panel.add( aufladenlabel, new CC().spanX( 2 ) );
    panel.add( seperator, new CC().spanY().growY() );
    panel.add( infoIconLabel, new CC().spanY() );
    panel.add( infoTextLabel1, new CC().minWidth( "0" ).pushX().wrap() );
    panel.add( valueSpinner, new CC() );
    panel.add( aufladenButton, new CC() );
    panel.add( infoTextLabel2, new CC().skip( 1 ).minWidth( "0" ).pushX() );

    SwingUtil.setPreferredWidth( 50, valueSpinner );

    panel.setBackground( UIManager.getColor( "TabbedPane.highlight" ) );
    panel.setBorder( new EmptyBorder( 0, 20, 10, 0 ) );


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
          undoButton.setEnabled( true );
        }
      }
    } );
    return panel;
  }


  /**
   * Es wird eine Liste von der Klasse {@link ServerCommunication}
   * genommen, welche die Namen aller Getränke enthält.
   * Auf Grundlage dieser Liste, wird für jedes Getränk
   * ein Button, mit zugehörigen Preis und Bild
   * erstellt und dem Panel hinzugefügt.
   * 
   * @return Das Panel in welchem man die angebotenen Getränke anklicken kann.
   */
  public JPanel createDrinkButtonPanel()
  {
    buttonList.clear();
    JPanel panel = new JPanel( new WrapLayout( FlowLayout.LEFT ) );
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
    panel.setBackground( UIManager.getColor( "TabbedPane.highlight" ) );
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
   * Wenn die Anzahl des Getränks 0 beträgt, so wird ein
   * 
   * @param name Getränkenaame
   */
  public void showNoMoreDrinksDialog( String name )
  {
    JOptionPane.showMessageDialog( mainFrame, "<html><b>" + name + " </b>ist leider nicht mehr verfügbar</html>", "Getränk nicht verfügbar",
        JOptionPane.ERROR_MESSAGE, null );
  }

  /**
   * 
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
