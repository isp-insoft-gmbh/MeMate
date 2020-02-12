/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.isp.memate.util.MeMateUIManager;


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
  private static final Color HOVER_BACKGROUND_COLOR = new Color( 186, 232, 232 );
  private static final Color PRESSED_BACKGROUND     = UIManager.getColor( "AppColor" );
  private final Border       DEFAULT_LINE_BORDER    = BorderFactory.createLineBorder( new Color( 173, 173, 173, 0 ) );
  private final Border       DEFAULT_BORDER         =
      BorderFactory.createCompoundBorder( DEFAULT_LINE_BORDER, BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
  private final Border       FOCUS_BORDER           =
      BorderFactory.createCompoundBorder( DEFAULT_LINE_BORDER, BorderFactory.createCompoundBorder(
          BorderFactory.createEmptyBorder( 1, 1, 1, 1 ), BorderFactory.createDashedBorder( Color.BLACK, 1f, 1f ) ) );

  private final JLabel       nameLabel                              = MeMateUIManager.createJLabel( "drinkButtons" );
  private final JLabel       priceLabel                             = MeMateUIManager.createJLabel( "drinkButtons" );
  private final JPanel       infoPanel                              = MeMateUIManager.createJPanel( "drinkButtons" );
  private final JPanel       nameLabelAndDrinkInfoButtonPanel       = MeMateUIManager.createJPanel( "drinkButtons" );
  private final JButton      acceptButton                           = MeMateUIManager.createNormalButton( "button" );
  private final JButton      abortButton                            = MeMateUIManager.createNormalButton( "button" );
  private final JButton      infoButton                             = MeMateUIManager.createNormalButton( "button" );
  private final JLabel       askWhetherToReallyConsumeLabel         = MeMateUIManager.createJLabel( "drinkButtons" );
  private final JLabel       textLabel                              = MeMateUIManager.createJLabel();
  private final JLayeredPane overlay                                = new JLayeredPane();
  private final JLabel       iconLabel;
  private MouseListener      mouseListener;
  private FocusListener      focusListener;
  private KeyListener        keyListener;
  private ActionListener     acceptAction;
  private ActionListener     abortAction;
  boolean                    askWhetherToReallyConsumeLabelIsActive = false;

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
    acceptButton.setText( "Ja" );
    abortButton.setText( "Nein" );
    infoButton.setText( "Info" );
    askWhetherToReallyConsumeLabel.setText( "Wirklich konsumieren?" );
    nameLabelAndDrinkInfoButtonPanel.setLayout( new GridBagLayout() );
    infoPanel.setLayout( new GridBagLayout() );
    nameLabel.setText( name );
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
            listBuilder.append( "<html>Zutaten:<br>" );
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
            if ( listBuilder.toString().length() >= 190 )
            {
              textLabel.setText( listBuilder.toString().substring( 0, 190 ) + "...<br><br>Durchschnittlicher Gehalt je 100ml<br>" );
            }
            else
            {
              textLabel.setText(
                  listBuilder.toString().substring( 0, listBuilder.length() - 2 ) + "<br><br>Durchschnittlicher Gehalt je 100ml<br>" );
            }
            textLabel.setHorizontalAlignment( JLabel.LEFT );
            GridBagConstraints textLabelConstraints = new GridBagConstraints();
            textLabelConstraints.gridx = 0;
            textLabelConstraints.gridy = 0;
            textLabelConstraints.gridwidth = 3;
            textLabelConstraints.gridheight = 2;
            textLabelConstraints.anchor = GridBagConstraints.LINE_START;
            textLabelConstraints.insets = new Insets( 0, 2, 0, 2 );
            infoPanel.add( textLabel, textLabelConstraints );
            addPanel( "Energie", 3, infoPanel, ingredients );
            addPanel( "Fett", 4, infoPanel, ingredients );
            addPanel( "davon gesättigte Fettsäuren", 5, infoPanel, ingredients );
            addPanel( "Kohlenhydrate", 6, infoPanel, ingredients );
            addPanel( "Zucker", 7, infoPanel, ingredients );
            addPanel( "Eiweiß", 8, infoPanel, ingredients );
            addPanel( "Salz", 9, infoPanel, ingredients );
            add( infoPanel, BorderLayout.CENTER );
          }
          else
          {
            remove( infoPanel );
            infoPanel.removeAll();
            iconLabel.setVisible( true );
          }
          repaint();
          revalidate();
          requestFocus();
        }

        private JPanel addPanel( String ingredient, int y, JPanel mainPanel, DrinkIngredients ingredients )
        {
          JPanel panel = MeMateUIManager.createJPanel( "drinkButtons" );
          panel.setLayout( new GridBagLayout() );

          JLabel label = MeMateUIManager.createJLabel();
          label.setText( ingredient + " " );
          GridBagConstraints labelConstraints = new GridBagConstraints();
          labelConstraints.gridx = 0;
          labelConstraints.gridy = 0;
          labelConstraints.weightx = 0;
          labelConstraints.anchor = GridBagConstraints.LAST_LINE_START;
          panel.add( label, labelConstraints );

          JComponent separator = new JComponent()
          {
            @Override
            public void paintComponent( final Graphics g )
            {
              for ( int x = 0; x < getWidth(); x += 4 )
              {
                g.drawLine( x, 0, x + 1, 0 );
              }
            }
          };
          MeMateUIManager.registerSeparator( separator, "default" );
          GridBagConstraints seperatorConstraints = new GridBagConstraints();
          seperatorConstraints.gridx = 1;
          seperatorConstraints.gridy = 0;
          seperatorConstraints.weightx = 1;
          seperatorConstraints.insets = new Insets( 0, 0, 3, 0 );
          seperatorConstraints.anchor = GridBagConstraints.SOUTH;
          seperatorConstraints.fill = GridBagConstraints.HORIZONTAL;
          panel.add( separator, seperatorConstraints );

          JLabel amountLabel = new JLabel();
          MeMateUIManager.registerlabel( amountLabel );
          switch ( ingredient )
          {
            case "Salz":
              amountLabel.setText( String.format( " %.2fg", ingredients.salt ) );
              break;
            case "Eiweiß":
              amountLabel.setText( String.format( " %.1fg", ingredients.protein ) );
              break;
            case "Energie":
              amountLabel.setText( " " + ingredients.energy_kJ + " kJ (" + ingredients.energy_kcal + " kcal)" );
              break;
            case "Fett":
              amountLabel.setText( String.format( " %.1fg", ingredients.fat ) );
              break;
            case "davon gesättigte Fettsäuren":
              amountLabel.setText( String.format( " %.1fg", ingredients.fatty_acids ) );
              break;
            case "Kohlenhydrate":
              amountLabel.setText( String.format( " %.1fg", ingredients.carbs ) );
              break;
            case "Zucker":
              amountLabel.setText( String.format( " %.1fg", ingredients.sugar ) );
            default :
              break;
          }
          GridBagConstraints amountLabelConstraints = new GridBagConstraints();
          amountLabelConstraints.gridx = 2;
          amountLabelConstraints.gridy = 0;
          amountLabelConstraints.weightx = 0;
          amountLabelConstraints.anchor = GridBagConstraints.LAST_LINE_END;
          panel.add( amountLabel, amountLabelConstraints );

          panel.setPreferredSize( new Dimension( getWidth() - 10, 15 ) );
          GridBagConstraints panelConstraints = new GridBagConstraints();
          panelConstraints.gridx = 0;
          panelConstraints.gridy = y;
          panelConstraints.gridwidth = 3;
          panelConstraints.insets = new Insets( 0, 2, 0, 2 );
          mainPanel.add( panel, panelConstraints );
          return panel;
        }
      } );
    }

    price = price.replace( "€", "" );
    Float priceAsFloat = Float.valueOf( price );
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    String format = formatter.format( priceAsFloat.doubleValue() );

    priceLabel.setText( format + "                                Noch " + ServerCommunication.getInstance().getAmount( name ) + " Stück" );
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


    acceptAction = new ActionListener()
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
    acceptButton.addActionListener( acceptAction );

    abortAction = new ActionListener()
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
    abortButton.addActionListener( abortAction );

    //The following listeners correctly implement the semantics for a button.
    //That is focus behavior, mouse movement / click behavior and confirmation via keyboard.
    mouseListener = new MouseAdapter()
    {
      @Override
      public void mouseClicked( MouseEvent __ )
      {
        askWhetherToReallyConsume( name, abortAction );
      }

      @Override
      public void mouseEntered( MouseEvent __ )
      {
        if ( MeMateUIManager.getDarkModeState() )
        {
          setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          nameLabelAndDrinkInfoButtonPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          Component[] components = infoPanel.getComponents();
          for ( Component component : components )
          {
            component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          }
        }
        else
        {
          setBackground( HOVER_BACKGROUND_COLOR );
          nameLabelAndDrinkInfoButtonPanel.setBackground( HOVER_BACKGROUND_COLOR );
          infoPanel.setBackground( HOVER_BACKGROUND_COLOR );
          Component[] components = infoPanel.getComponents();
          for ( Component component : components )
          {
            component.setBackground( HOVER_BACKGROUND_COLOR );
          }
        }
      }

      public void mouseExited( MouseEvent __ )
      {
        if ( MeMateUIManager.getDarkModeState() )
        {
          setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          nameLabelAndDrinkInfoButtonPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          Component[] components = infoPanel.getComponents();
          for ( Component component : components )
          {
            component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          }
        }
        else
        {
          setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          nameLabelAndDrinkInfoButtonPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          Component[] components = infoPanel.getComponents();
          for ( Component component : components )
          {
            component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          }
        }
      }

      public void mousePressed( MouseEvent __ )
      {
        requestFocus();
        setBackground( PRESSED_BACKGROUND );
        nameLabelAndDrinkInfoButtonPanel.setBackground( PRESSED_BACKGROUND );
        infoPanel.setBackground( PRESSED_BACKGROUND );
        Component[] components = infoPanel.getComponents();
        for ( Component component : components )
        {
          component.setBackground( PRESSED_BACKGROUND );
        }
      }

      public void mouseReleased( MouseEvent event )
      {
        if ( SwingUtilities.getLocalBounds( DrinkConsumptionButton.this ).contains( event.getPoint() ) )
        {
          setBackground( HOVER_BACKGROUND_COLOR );
          nameLabelAndDrinkInfoButtonPanel.setBackground( HOVER_BACKGROUND_COLOR );
          infoPanel.setBackground( HOVER_BACKGROUND_COLOR );
          Component[] components = infoPanel.getComponents();
          for ( Component component : components )
          {
            component.setBackground( HOVER_BACKGROUND_COLOR );
          }
          askWhetherToReallyConsume( name, abortAction );
        }
        else
        {
          if ( MeMateUIManager.getDarkModeState() )
          {
            setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
            nameLabelAndDrinkInfoButtonPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
            infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
            Component[] components = infoPanel.getComponents();
            for ( Component component : components )
            {
              component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
            }
          }
          else
          {
            setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
            nameLabelAndDrinkInfoButtonPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
            infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
            Component[] components = infoPanel.getComponents();
            for ( Component component : components )
            {
              component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
            }
          }
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
          askWhetherToReallyConsume( name, abortAction );
        }
      }
    };
    Action abortAction = new AbstractAction()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        reset();
      }
    };
    acceptButton.getInputMap().put( KeyStroke.getKeyStroke( "ESCAPE" ), "Abbrechen" );
    acceptButton.getActionMap().put( "Abbrechen", abortAction );
    abortButton.getInputMap().put( KeyStroke.getKeyStroke( "ESCAPE" ), "Abbrechen" );
    abortButton.getActionMap().put( "Abbrechen", abortAction );
    addMouseListener( mouseListener );
    addFocusListener( focusListener );
    addKeyListener( keyListener );
    MeMateUIManager.registerPanel( "drinkButtons", this );
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
    Dashboard.getInstance().resetAllDrinkButtons();
    askWhetherToReallyConsumeLabelIsActive = true;
    if ( MeMateUIManager.getDarkModeState() )
    {
      setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
      nameLabelAndDrinkInfoButtonPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
      infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
      Component[] components = infoPanel.getComponents();
      for ( Component component : components )
      {
        component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
      }
    }
    infoButton.setEnabled( false );
    DrinkConsumptionButton.this.removeMouseListener( mouseListener );
    DrinkConsumptionButton.this.removeKeyListener( keyListener );
    iconLabel.setVisible( false );
    try
    {
      remove( infoPanel );
    }
    catch ( Exception exception )
    {
    }
    add( askWhetherToReallyConsumeLabel, BorderLayout.NORTH );
    add( overlay, BorderLayout.CENTER );
    repaint();
    revalidate();
    acceptButton.requestFocus();
    ActionListener actionListener = new ActionListener()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        ServerCommunication.getInstance().lock.lock();
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
        Mainframe.getInstance().setUndoButtonEnabled( true );
        infoButton.setEnabled( true );
        askWhetherToReallyConsumeLabelIsActive = false;
        ServerCommunication.getInstance().lock.unlock();
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
        askWhetherToReallyConsumeLabelIsActive = false;
      }
    };
    abortButton.addActionListener( actionListener2 );
  }

  void reset()
  {
    ActionListener[] abortListenerArray = abortButton.getActionListeners();
    for ( ActionListener actionListener : abortListenerArray )
    {
      abortButton.removeActionListener( actionListener );
    }
    ActionListener[] acceptListenerArray = abortButton.getActionListeners();
    for ( ActionListener actionListener : acceptListenerArray )
    {
      acceptButton.removeActionListener( actionListener );
    }
    abortButton.addActionListener( abortAction );
    acceptButton.addActionListener( acceptAction );

    DrinkConsumptionButton.this.addMouseListener( mouseListener );
    DrinkConsumptionButton.this.addKeyListener( keyListener );
    infoButton.setEnabled( true );
    remove( overlay );
    remove( askWhetherToReallyConsumeLabel );
    iconLabel.setVisible( true );
    if ( MeMateUIManager.getDarkModeState() )
    {
      setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
      nameLabelAndDrinkInfoButtonPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
    }
    else
    {
      setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
      nameLabelAndDrinkInfoButtonPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
    }
    repaint();
    revalidate();
    requestFocus();
    askWhetherToReallyConsumeLabelIsActive = false;
  }
}