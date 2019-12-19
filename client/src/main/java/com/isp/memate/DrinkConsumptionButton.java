/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


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

  private JLabel             nameLabel;
  private final JLabel       priceLabel;
  private final JLabel       iconLabel;
  private final JButton      acceptButton                   = new JButton( "Ja" );
  private final JButton      abortButton                    = new JButton( "Nein" );
  private final JLayeredPane overlay                        = new JLayeredPane();
  private final JLabel       askWhetherToReallyConsumeLabel = new JLabel( "Wirklich konsumieren?" );
  private MouseListener      mouseListener;
  private FocusListener      focusListener;
  private KeyListener        keyListener;

  /**
   * Erzeugt eine Komponente, welche auf einem {@link JPanel} basiert, aber einen Button nachahmt.
   * 
   * @param mainFrame Parent-Mainframe für Zugriff auf state
   * @param price Preis des Getränks, welcher oben im "Button" angezeigt werden soll
   * @param name Name des Getränks, welcher unten im "Button" angezeigt werden soll
   * @param icon Bild des Getränks, welches mittig im "Button" angezeigt werden soll
   */
  public DrinkConsumptionButton( Mainframe mainFrame, String price, String name, Icon icon )
  {
    setLayout( new BorderLayout() );

    JPanel nameLabelAndDrinkInfoButtonPanel = new JPanel( new BorderLayout() );
    nameLabel = new JLabel( name );
    nameLabel.setFont( nameLabel.getFont().deriveFont( 14f ) );
    nameLabel.setHorizontalAlignment( JLabel.CENTER );
    nameLabelAndDrinkInfoButtonPanel.add( nameLabel, BorderLayout.CENTER );
    // nameLabelAndDrinkInfoButtonPanel.add( new JButton( "Info" ), BorderLayout.EAST );

    price = price.replace( "€", "" );
    Float priceAsFloat = Float.valueOf( price );
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    String format = formatter.format( priceAsFloat.doubleValue() );

    priceLabel =
        new JLabel( format + "                                Noch " + ServerCommunication.getInstance().getAmount( name ) + " Stück" );
    priceLabel.setFont( priceLabel.getFont().deriveFont( 14f ) );
    priceLabel.setHorizontalAlignment( JLabel.CENTER );

    iconLabel = new JLabel( icon );
    acceptButton.setPreferredSize( new Dimension( 200, 50 ) );
    abortButton.setPreferredSize( new Dimension( 200, 50 ) );

    add( nameLabelAndDrinkInfoButtonPanel, BorderLayout.NORTH );
    add( iconLabel, BorderLayout.CENTER );
    add( priceLabel, BorderLayout.SOUTH );
    setBorder( DEFAULT_BORDER );
    setFocusable( true );
    setPreferredSize( new Dimension( 270, 270 ) );

    overlay.setLayout( new FlowLayout() );
    overlay.add( acceptButton );
    overlay.add( abortButton );
    askWhetherToReallyConsumeLabel.setHorizontalAlignment( JLabel.CENTER );
    askWhetherToReallyConsumeLabel.setBorder( new EmptyBorder( 40, 0, 30, 0 ) );
    askWhetherToReallyConsumeLabel.setFont( askWhetherToReallyConsumeLabel.getFont().deriveFont( 16f ) );

    acceptButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        remove( overlay );
        remove( askWhetherToReallyConsumeLabel );
        iconLabel.setVisible( true );
        repaint();
        revalidate();
        requestFocus();
      }
    } );
    ActionListener abortButtonListener = new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        remove( overlay );
        remove( askWhetherToReallyConsumeLabel );
        iconLabel.setVisible( true );
        repaint();
        revalidate();
        requestFocus();
      }
    };
    abortButton.addActionListener( abortButtonListener );

    //The following listeners correctly implement the semantics for a button.
    //That is focus behavior, mouse movement / click behavior and confirmation via keyboard.
    mouseListener = new MouseAdapter()
    {
      @Override
      public void mouseClicked( MouseEvent __ )
      {
        askWhetherToReallyConsume( name, abortButtonListener );
      }

      @Override
      public void mouseEntered( MouseEvent __ )
      {
        setBackground( HOVER_BACKGROUND_COLOR );
        nameLabelAndDrinkInfoButtonPanel.setBackground( HOVER_BACKGROUND_COLOR );
      }

      public void mouseExited( MouseEvent __ )
      {
        setBackground( UIManager.getColor( "Panel.background" ) );
        nameLabelAndDrinkInfoButtonPanel.setBackground( UIManager.getColor( "Panel.background" ) );
      }

      public void mousePressed( MouseEvent __ )
      {
        requestFocus();
        setBackground( UIManager.getColor( "Table.selectionBackground" ) );
        nameLabelAndDrinkInfoButtonPanel.setBackground( UIManager.getColor( "Table.selectionBackground" ) );
      }

      public void mouseReleased( MouseEvent event )
      {
        if ( SwingUtilities.getLocalBounds( DrinkConsumptionButton.this ).contains( event.getPoint() ) )
        {
          setBackground( HOVER_BACKGROUND_COLOR );
          nameLabelAndDrinkInfoButtonPanel.setBackground( HOVER_BACKGROUND_COLOR );
        }
        else
        {
          setBackground( UIManager.getColor( "Panel.background" ) );
          nameLabelAndDrinkInfoButtonPanel.setBackground( UIManager.getColor( "Panel.background" ) );
        }
      }
    };
    focusListener = new FocusListener()
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
    };
    keyListener = new KeyAdapter()
    {
      @Override
      public void keyPressed( KeyEvent event )
      {
        if ( event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyCode() == KeyEvent.VK_SPACE )
        {
          askWhetherToReallyConsume( name, abortButtonListener );
        }
      }
    };
    addMouseListener( mouseListener );
    addFocusListener( focusListener );
    addKeyListener( keyListener );
  }

  /**
   * Sobald man auf ein Getränk klickt, so öffnet sich ein Dialog, welcher fragt, ob man das
   * Getränk wirklich konsumieren möchte. Bestätigt man dies, so wird {@linkplain ServerCommunication}
   * mitgeteilt, welches Getränk man gekauft hat.
   * 
   * @param drinkName Name des Getränks
   * @param abortButtonListener
   */
  private void askWhetherToReallyConsume( String drinkName, ActionListener abortButtonListener )
  {
    DrinkConsumptionButton.this.removeMouseListener( mouseListener );
    DrinkConsumptionButton.this.removeKeyListener( keyListener );
    iconLabel.setVisible( false );
    add( askWhetherToReallyConsumeLabel, BorderLayout.NORTH );
    add( overlay, BorderLayout.CENTER );
    repaint();
    revalidate();
    requestFocus();
    ActionListener actionListener = new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        ServerCommunication.getInstance().consumeDrink( drinkName );
        ServerCommunication.getInstance().getBalance( ServerCommunication.getInstance().currentUser );
        acceptButton.removeActionListener( this );
        ActionListener[] array = abortButton.getActionListeners();
        for ( ActionListener actionListener : array )
        {
          abortButton.removeActionListener( actionListener );
        }
        abortButton.addActionListener( abortButtonListener );
        DrinkConsumptionButton.this.addMouseListener( mouseListener );
        DrinkConsumptionButton.this.addKeyListener( keyListener );
      }
    };
    acceptButton.addActionListener( actionListener );
    ActionListener actionListener2 = new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        acceptButton.removeActionListener( actionListener );
        DrinkConsumptionButton.this.addMouseListener( mouseListener );
        DrinkConsumptionButton.this.addKeyListener( keyListener );
        abortButton.removeActionListener( this );
      }
    };
    abortButton.addActionListener( actionListener2 );
  }
}