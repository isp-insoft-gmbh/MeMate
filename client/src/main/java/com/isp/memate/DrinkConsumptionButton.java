/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
  private JLabel             ingredientsLabel;
  private final JButton      acceptButton                   = new JButton( "Ja" );
  private final JButton      abortButton                    = new JButton( "Nein" );
  private final JButton      infoButton                     = new JButton( "Info" );
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

    JPanel nameLabelAndDrinkInfoButtonPanel = new JPanel( new GridBagLayout() );
    nameLabel = new JLabel( name );
    nameLabel.setFont( nameLabel.getFont().deriveFont( 14f ) );
    nameLabel.setHorizontalAlignment( JLabel.CENTER );
    GridBagConstraints nameLabelConstraints = new GridBagConstraints();
    nameLabelConstraints.gridx = 1;
    nameLabelConstraints.gridy = 0;
    nameLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
    nameLabelConstraints.weightx = 1;
    nameLabelAndDrinkInfoButtonPanel.add( nameLabel, nameLabelConstraints );
    if ( ServerCommunication.getInstance().hasIngredients( name ) )
    {

      GridBagConstraints infoButtonConstraints = new GridBagConstraints();
      infoButtonConstraints.gridx = 2;
      infoButtonConstraints.gridy = 0;
      infoButtonConstraints.weightx = 0.1;
      infoButtonConstraints.anchor = GridBagConstraints.LINE_END;
      nameLabelAndDrinkInfoButtonPanel.add( infoButton, infoButtonConstraints );

      JLabel fillLable = new JLabel();
      fillLable.setPreferredSize( new Dimension( 51, 1 ) );
      GridBagConstraints fillLablenConstraints = new GridBagConstraints();
      fillLablenConstraints.gridx = 0;
      fillLablenConstraints.gridy = 0;
      fillLablenConstraints.weightx = 0.1;
      fillLablenConstraints.anchor = GridBagConstraints.LINE_START;
      nameLabelAndDrinkInfoButtonPanel.add( fillLable, fillLablenConstraints );

      infoButton.addActionListener( new ActionListener()
      {
        @Override
        public void actionPerformed( ActionEvent e )
        {
          if ( iconLabel.isVisible() )
          {
            iconLabel.setVisible( false );
            DrinkIngredients ingredients = ServerCommunication.getInstance().getIngredients( name );
            String[] ingredientsArray = ingredients.ingredients.trim().split( "," );
            int maxLength = 50;
            int currentLength = 0;
            StringBuilder listBuilder = new StringBuilder();
            for ( int i = 0; i < ingredientsArray.length; i++ )
            {
              currentLength += ingredientsArray[ i ].length() + 2;
              if ( currentLength > maxLength )
              {
                listBuilder.append( "<br>" );
                currentLength = ingredientsArray[ i ].length() + 2;
              }
              listBuilder.append( ingredientsArray[ i ] )
                  .append( ", " );
            }

            StringBuilder ingredientsBuilder = new StringBuilder();
            ingredientsBuilder.append( "<html>Zutaten:<br>" )
                .append( listBuilder.toString().substring( 0, listBuilder.length() - 2 ) )
                .append( "<br><br>Durchschnittlicher Gehalt je 100ml<br>" )
                .append( "Energie .............................................. " )
                .append( ingredients.energy_kJ )
                .append( "kJ(" )
                .append( ingredients.energy_kcal )
                .append( " kcal)<br>" )
                .append( "Fett .................................................................... " )
                .append( ingredients.fat )
                .append( "g<br>davon gesättigte Fettsäuren ............................. " )
                .append( ingredients.fatty_acids )
                .append( "g<br>Kohlenhydrate .................................................. " )
                .append( ingredients.carbs )
                .append( "g<br>davon Zucker .................................................... " )
                .append( ingredients.sugar )
                .append( "g<br>Eiweiß ................................................................ " )
                .append( ingredients.protein )
                .append( "g<br>Salz .................................................................. " )
                .append( ingredients.salt )
                .append( "g" );

            ingredientsLabel = new JLabel( ingredientsBuilder.toString() );
            ingredientsLabel.setHorizontalAlignment( JLabel.LEFT );
            ingredientsLabel.setBorder( new EmptyBorder( 0, 5, 0, 5 ) );
            add( ingredientsLabel, BorderLayout.CENTER );
          }
          else
          {
            remove( ingredientsLabel );
            iconLabel.setVisible( true );
          }
          repaint();
          revalidate();
          requestFocus();
        }
      } );

    }

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
    infoButton.setEnabled( false );
    DrinkConsumptionButton.this.removeMouseListener( mouseListener );
    DrinkConsumptionButton.this.removeKeyListener( keyListener );
    iconLabel.setVisible( false );
    try
    {
      remove( ingredientsLabel );
    }
    catch ( Exception exception )
    {
    }
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
        Dashboard.getInstance().undoButton.setEnabled( true );
        infoButton.setEnabled( true );
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
        infoButton.setEnabled( true );
      }
    };
    abortButton.addActionListener( actionListener2 );
  }
}