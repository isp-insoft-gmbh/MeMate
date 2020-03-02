/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;


/**
 * Die Klasse DrinkConsumptionButton erzeugt eine Komponente, welcher auf einem {@link JPanel}
 * basiert, aber einen Button nachahmt. Man kann ihn fokusieren und auch das
 * Highlighting verhält sich ähnlich zu einem echten Button.
 * 
 * @author nwe
 * @since 18.10.2019
 */
class DrinkConsumptionButton extends JPanel
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
  private final JLabel       fillLable                              = new JLabel();
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
  DrinkConsumptionButton( Mainframe mainFrame, String price, String name, Icon icon )
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

      fillLable.setPreferredSize( new Dimension( 33, 21 ) );
      GridBagConstraints fillLablenConstraints = new GridBagConstraints();
      fillLablenConstraints.gridx = 0;
      fillLablenConstraints.gridy = 0;
      fillLablenConstraints.weightx = 0.1;
      fillLablenConstraints.anchor = GridBagConstraints.LINE_START;
      nameLabelAndDrinkInfoButtonPanel.add( fillLable, fillLablenConstraints );


      DrinkIngredients ingredients = ServerCommunication.getInstance().getIngredients( name );
      String[] ingredientsArray = ingredients.ingredients.trim().split( "," );
      int maxLength = 40;
      int currentLength = 0;
      StringBuilder listBuilder = new StringBuilder();
      listBuilder.append( "Zutaten:<br>" );
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
      infoButton
          .setToolTipText(
              "<html><font face=monospace>" + listBuilder.toString().substring( 0, listBuilder.toString().length() - 2 )
                  + "<br><br><br>Durchschnittlicher Gehalt je 100ml<br>"
                  + getIngredient( "Energie", ingredients ) + "<br>"
                  + getIngredient( "Fett", ingredients ) + "<br>"
                  + getIngredient( "davon gesättigte Fettsäuren", ingredients ) + "<br>"
                  + getIngredient( "Kohlenhydrate", ingredients ) + "<br>"
                  + getIngredient( "Zucker", ingredients ) + "<br>"
                  + getIngredient( "Eiweiß", ingredients ) + "<br>"
                  + getIngredient( "Salz", ingredients ) + "<br>"
                  + "</font></html>" );
      infoButton.setFocusable( false );
      infoButton.setEnabled( false );
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
        infoButton.setVisible( true );
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
        infoButton.setVisible( true );
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
   * @param string
   * @param ingredients
   * @return
   */
  private String getIngredient( String ingredient, DrinkIngredients ingredients )
  {
    int maxLength = 40;
    int ingredientLength = ingredient.length() + 1;
    int amountOfPoints = 0;
    StringBuilder builder = new StringBuilder();
    String amount = "";

    switch ( ingredient )
    {
      case "Salz":
        amount = String.format( " %.2fg", ingredients.salt );
        break;
      case "Eiweiß":
        amount = String.format( " %.1fg", ingredients.protein );
        break;
      case "Energie":
        amount = " " + ingredients.energy_kJ + " kJ (" + ingredients.energy_kcal + " kcal)";
        break;
      case "Fett":
        amount = String.format( " %.1fg", ingredients.fat );
        break;
      case "davon gesättigte Fettsäuren":
        amount = String.format( " %.1fg", ingredients.fatty_acids );
        break;
      case "Kohlenhydrate":
        amount = String.format( " %.1fg", ingredients.carbs );
        break;
      case "Zucker":
        amount = String.format( " %.1fg", ingredients.sugar );
      default :
        break;
    }
    ingredientLength += amount.toString().length();
    amountOfPoints = maxLength - ingredientLength;
    builder.append( ingredient + " " );
    for ( int i = 0; i < amountOfPoints; i++ )
    {
      builder.append( "." );
    }
    builder.append( amount );
    return builder.toString();
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
    Mainframe.getInstance().getDashboard().resetAllDrinkButtons();
    infoButton.setVisible( false );
    fillLable.setVisible( false );
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
    DrinkConsumptionButton.this.removeMouseListener( mouseListener );
    DrinkConsumptionButton.this.removeKeyListener( keyListener );
    iconLabel.setVisible( false );
    try
    {
      remove( infoPanel );
    }
    catch ( Exception exception )
    {
      ClientLog.newLog( exception.getMessage() );
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
        try
        {
          ServerCommunication.getInstance().consumeDrink( drinkName );
          ServerCommunication.getInstance().getBalance();
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
          fillLable.setVisible( true );
          askWhetherToReallyConsumeLabelIsActive = false;
        }
        catch ( Exception exception )
        {
          ClientLog.newLog( exception.getMessage() );
        }
        finally
        {
          ServerCommunication.getInstance().lock.unlock();
        }
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
        fillLable.setVisible( true );
        askWhetherToReallyConsumeLabelIsActive = false;
      }
    };
    abortButton.addActionListener( actionListener2 );
  }

  void reset()
  {
    infoButton.setVisible( true );
    fillLable.setVisible( true );
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