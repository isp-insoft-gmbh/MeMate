/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;


/**
 * Die Klasse {@link DrinkConsumptionButton} erzeugt eine Komponente, welcher auf einem {@link JPanel}
 * basiert, aber einen Button nachahmt. Man kann ihn fokusieren und auch das
 * Highlighting verhält sich ähnlich zu einem echten Button.
 * 
 * @author nwe
 * @since 18.10.2019
 */
public class DrinkConsumptionButton extends JPanel
{
  private static final Color HOVER_BACKGROUND_COLOR = new Color( 143, 203, 255 );
  private final Border       DEFAULT_LINE_BORDER    = BorderFactory.createLineBorder( UIManager.getColor( "Panel.borderColor" ) );
  private final Border       DEFAULT_BORDER         =
      BorderFactory.createCompoundBorder( DEFAULT_LINE_BORDER, BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
  private final Border       FOCUS_BORDER           =
      BorderFactory.createCompoundBorder( DEFAULT_LINE_BORDER, BorderFactory.createCompoundBorder(
          BorderFactory.createEmptyBorder( 1, 1, 1, 1 ), BorderFactory.createDashedBorder( Color.BLACK, 1f, 1f ) ) );

  private Mainframe mainFrame;

  /**
   * @param mainFrame Parent-Mainframe für Zugriff auf state
   * @param price Preis des Getränks, welcher oben im "Button" angezeigt werden soll
   * @param name Name des Getränks, welcher unten im "Button" angezeigt werden soll
   * @param icon Bild des Getränks, welches mittig im "Button" angezeigt werden soll
   */
  public DrinkConsumptionButton( Mainframe mainFrame, String price, String name, Icon icon )
  {
    this.mainFrame = mainFrame;
    setLayout( new BorderLayout() );

    JLabel nameLabel = new JLabel( name );
    nameLabel.setFont( nameLabel.getFont().deriveFont( 14f ) );
    nameLabel.setHorizontalAlignment( JLabel.CENTER );


    price = price.replace( "€", "" );
    Float priceAsFloat = Float.valueOf( price );
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    String format = formatter.format( priceAsFloat.doubleValue() );

    JLabel priceLabel = new JLabel( format );
    priceLabel.setFont( priceLabel.getFont().deriveFont( 14f ) );
    priceLabel.setHorizontalAlignment( JLabel.CENTER );

    add( nameLabel, BorderLayout.NORTH );
    add( new JLabel( icon ), BorderLayout.CENTER );
    add( priceLabel, BorderLayout.SOUTH );
    setBorder( DEFAULT_BORDER );
    setFocusable( true );
    setPreferredSize( new Dimension( 270, 270 ) );

    //The following listeners correctly implement the semantics for a button.
    //That is focus behavior, mouse movement / click behavior and confirmation via keyboard.
    addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( MouseEvent __ )
      {
        askWhetherToReallyConsume( SwingUtilities.getWindowAncestor( DrinkConsumptionButton.this ), name );
      }

      @Override
      public void mouseEntered( MouseEvent __ )
      {
        setBackground( HOVER_BACKGROUND_COLOR );
      }

      public void mouseExited( MouseEvent __ )
      {
        setBackground( UIManager.getColor( "Panel.background" ) );
      }

      public void mousePressed( MouseEvent __ )
      {
        requestFocus();
        setBackground( UIManager.getColor( "Table.selectionBackground" ) );
      }

      public void mouseReleased( MouseEvent event )
      {
        if ( SwingUtilities.getLocalBounds( DrinkConsumptionButton.this ).contains( event.getPoint() ) )
        {
          setBackground( HOVER_BACKGROUND_COLOR );
        }
        else
        {
          setBackground( UIManager.getColor( "Panel.background" ) );
        }
      }
    } );
    addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( FocusEvent __ )
      {
        setBorder( DEFAULT_BORDER );
      }

      @Override
      public void focusGained( FocusEvent __ )
      {
        setBorder( FOCUS_BORDER );
      }
    } );
    addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyReleased( KeyEvent event )
      {
        if ( event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyCode() == KeyEvent.VK_SPACE )
        {
          askWhetherToReallyConsume( SwingUtilities.getWindowAncestor( DrinkConsumptionButton.this ), name );
        }
      }
    } );
  }

  private void askWhetherToReallyConsume( Window owner, String drinkName )
  {
    int result = JOptionPane.showConfirmDialog( owner, "Wollen Sie wirklich "
        + drinkName + " konsumieren?", "Getränk konsumieren",
        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE );
    if ( result == JOptionPane.YES_OPTION )
    {
      ServerCommunication servercommunication = ServerCommunication.getInstance();
      Float newBalance = servercommunication.balance - servercommunication.getPrice( drinkName );
      ServerCommunication.getInstance()
          .updateBalance( newBalance );
      Mainframe.getInstance().setBalance( newBalance );
    }
  }
}