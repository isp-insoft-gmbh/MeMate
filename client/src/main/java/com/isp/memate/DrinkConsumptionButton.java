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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.ui.FlatBorder;

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.MeMateUIManager;

/**
 * Creates an JPanel that behaves like an JButton (in relation to focusEvents and highlighting).<br>
 * The DrinkConsumptionButton can display 2 different states:
 * <ul>
 * <li>Default (shows drinkname, drinkprice, amount of drink lefts and a preview picture)</li>
 * <li>Buy will show two Buttons, leaving the choice to the user if he really wants to buy the
 * drink.</li>
 * </ul>
 *
 * @author nwe
 * @since 18.10.2019
 */
class DrinkConsumptionButton extends JPanel
{
  private JPanel  defaultPanel;
  private JPanel  buyPanel;
  private JButton acceptButton;
  private String  name;
  private String  price;

  private STATE CURRENT_STATE = null;

  /**
   * Initializes all the GUI-components, applies the needed listener and sets the state to {@link STATE#DEFAULT}
   * 
   * @param price Price of the drink
   * @param name Name of the drink
   * @param icon Picture of the drink
   */
  DrinkConsumptionButton( String price, final String name, final Icon icon )
  {
    this.name = name;
    this.price = price;
    init( icon );
    applyListener();

    switchState( STATE.DEFAULT );
    setBackground( UIManager.getColor( "Button.background" ) );
  }


  private void init( Icon icon )
  {
    initDefaultPanel( icon );
    initBuyPanel();

    setLayout( new BorderLayout() );
    setFocusable( true );
    setBorder( new FlatBorder() );
    setPreferredSize( new Dimension( 270, 270 ) );
  }

  private void initDefaultPanel( Icon icon )
  {
    JPanel headerPanel = createHeaderPanel();
    JPanel footerPanel = createFooterPanel();
    JLabel iconLabel = new JLabel( icon );

    defaultPanel = new JPanel( new BorderLayout() )
    {
      @Override
      public void setBackground( Color bg )
      {
        super.setBackground( bg );
        headerPanel.setBackground( bg );
        footerPanel.setBackground( bg );
      }
    };

    defaultPanel.add( headerPanel, BorderLayout.NORTH );
    defaultPanel.add( iconLabel, BorderLayout.CENTER );
    defaultPanel.add( footerPanel, BorderLayout.SOUTH );
  }

  private JPanel createHeaderPanel()
  {
    JPanel fillPanel = new JPanel();
    JPanel headerPanel = new JPanel()
    {
      @Override
      public void setBackground( Color bg )
      {
        super.setBackground( bg );
        fillPanel.setBackground( bg );
      }
    };

    JLabel nameLabel = new JLabel();
    headerPanel.setLayout( new GridBagLayout() );
    nameLabel.setText( name );
    nameLabel.setFont( nameLabel.getFont().deriveFont( 14f ) );
    nameLabel.setHorizontalAlignment( SwingConstants.CENTER );
    final GridBagConstraints nameLabelConstraints = new GridBagConstraints();
    nameLabelConstraints.gridx = 1;
    nameLabelConstraints.gridy = 0;
    nameLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
    nameLabelConstraints.weightx = 1;
    headerPanel.add( nameLabel, nameLabelConstraints );

    JButton infoButton = MeMateUIManager.createInfoButton( name );
    infoButton.addComponentListener( new ComponentAdapter()
    {
      @Override
      public void componentResized( final ComponentEvent e )
      {
        fillPanel.setPreferredSize( infoButton.getPreferredSize() );
        fillPanel.setMaximumSize( infoButton.getMaximumSize() );
        fillPanel.setMinimumSize( infoButton.getMinimumSize() );
        fillPanel.setSize( infoButton.getSize() );
        headerPanel.revalidate();
        headerPanel.repaint();
      }
    } );

    if ( Cache.getInstance().hasIngredients( name ) )
    {
      final GridBagConstraints infoButtonConstraints = new GridBagConstraints();
      infoButtonConstraints.gridx = 2;
      infoButtonConstraints.gridy = 0;
      infoButtonConstraints.weightx = 0;
      infoButtonConstraints.anchor = GridBagConstraints.LINE_END;
      infoButtonConstraints.insets = new Insets( 0, 0, 0, 2 );
      headerPanel.add( infoButton, infoButtonConstraints );

      final GridBagConstraints fillPanelConstraints = new GridBagConstraints();
      fillPanelConstraints.gridx = 0;
      fillPanelConstraints.gridy = 0;
      fillPanelConstraints.weightx = 0;
      fillPanelConstraints.anchor = GridBagConstraints.LINE_START;
      fillPanelConstraints.insets = new Insets( 0, 2, 0, 0 );
      headerPanel.add( fillPanel, fillPanelConstraints );
    }
    return headerPanel;
  }


  private JPanel createFooterPanel()
  {
    JPanel footerPanel = new JPanel();
    footerPanel.setLayout( new GridBagLayout() );

    price = price.replace( "€", "" );
    final Float priceAsFloat = Float.valueOf( price );
    final NumberFormat formatter = NumberFormat.getCurrencyInstance();
    final String format = formatter.format( priceAsFloat.doubleValue() );

    JLabel priceLabel = new JLabel();
    JLabel amountLabel = new JLabel();
    priceLabel.setText( format );
    amountLabel.setText( String.format( "Noch %d Stück", Cache.getInstance().getAmount( name ) ) );
    priceLabel.setFont( priceLabel.getFont().deriveFont( 14f ) );
    priceLabel.setHorizontalAlignment( SwingConstants.CENTER );
    amountLabel.setFont( priceLabel.getFont().deriveFont( 14f ) );
    amountLabel.setHorizontalAlignment( SwingConstants.CENTER );

    final GridBagConstraints priceLabelConstraints = new GridBagConstraints();
    priceLabelConstraints.gridx = 0;
    priceLabelConstraints.gridy = 0;
    priceLabelConstraints.weightx = 1;
    priceLabelConstraints.anchor = GridBagConstraints.LINE_START;
    priceLabelConstraints.insets = new Insets( 0, 5, 0, 0 );
    footerPanel.add( priceLabel, priceLabelConstraints );

    final GridBagConstraints amountLabelConstraints = new GridBagConstraints();
    amountLabelConstraints.gridx = 1;
    amountLabelConstraints.gridy = 0;
    amountLabelConstraints.weightx = 1;
    amountLabelConstraints.anchor = GridBagConstraints.LINE_END;
    amountLabelConstraints.insets = new Insets( 0, 0, 0, 5 );
    footerPanel.add( amountLabel, amountLabelConstraints );

    return footerPanel;
  }


  private void initBuyPanel()
  {
    buyPanel = new JPanel();
    acceptButton = new JButton();
    JButton abortButton = new JButton();
    JLabel askWhetherToReallyConsumeLabel = new JLabel();

    acceptButton.setText( "Ja" );
    abortButton.setText( "Nein" );
    askWhetherToReallyConsumeLabel.setText( "Wirklich konsumieren?" );
    askWhetherToReallyConsumeLabel.setHorizontalAlignment( SwingConstants.CENTER );
    askWhetherToReallyConsumeLabel.setBorder( new EmptyBorder( 40, 0, 30, 0 ) );
    askWhetherToReallyConsumeLabel.setFont( askWhetherToReallyConsumeLabel.getFont().deriveFont( 16f ) );

    abortButton.setPreferredSize( new Dimension( 200, 50 ) );
    acceptButton.setPreferredSize( new Dimension( 200, 50 ) );

    buyPanel.setLayout( new FlowLayout() );
    buyPanel.add( askWhetherToReallyConsumeLabel );
    buyPanel.add( acceptButton );
    buyPanel.add( abortButton );

    acceptButton.addActionListener( new ActionListener()
    {
      @Override
      public void actionPerformed( final ActionEvent e )
      {
        final ReentrantLock lock = ServerCommunication.getInstance().lock;
        lock.lock();
        try
        {
          ServerCommunication.getInstance().consumeDrink( name );
          ServerCommunication.getInstance().getBalance();
          GUIObjects.mainframe.setUndoButtonEnabled( true );
          switchState( STATE.DEFAULT );
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
    } );
    abortButton.addActionListener( e -> switchState( STATE.DEFAULT ) );
  }

  /**
   * The following listeners correctly implement the semantics for a button.
   * That is focus behavior, mouse movement / click behavior and confirmation via
   * keyboard.
   */
  private void applyListener()
  {
    addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent __ )
      {
        switchState( STATE.BUY );
      }

      @Override
      public void mouseEntered( final MouseEvent __ )
      {
        if ( STATE.DEFAULT.equals( CURRENT_STATE ) )
        {
          setBackground( UIManager.getColor( "Button.hoverBackground" ) );
        }
      }

      @Override
      public void mouseExited( final MouseEvent __ )
      {
        if ( STATE.DEFAULT.equals( CURRENT_STATE ) )
        {
          setBackground( UIManager.getColor( "Button.background" ) );
        }
      }

      @Override
      public void mousePressed( final MouseEvent __ )
      {
        if ( STATE.DEFAULT.equals( CURRENT_STATE ) )
        {
          requestFocus();
          setBackground( UIManager.getColor( "Button.default.pressedBackground" ) );
        }
      }

      @Override
      public void mouseReleased( final MouseEvent event )
      {
        if ( STATE.DEFAULT.equals( CURRENT_STATE ) )
        {
          if ( SwingUtilities.getLocalBounds( DrinkConsumptionButton.this ).contains( event.getPoint() ) )
          {
            setBackground( UIManager.getColor( "Button.default.hoverBackground" ) );
            switchState( STATE.BUY );
          }
          else
          {
            setBackground( UIManager.getColor( "Button.background" ) );
          }
        }
      }
    } );


    addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyPressed( final KeyEvent event )
      {
        if ( event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyCode() == KeyEvent.VK_SPACE )
        {
          switchState( STATE.BUY );
        }
      }
    } );

    //Needed to update the border
    addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( FocusEvent e )
      {
        repaint();
      }

      @Override
      public void focusGained( FocusEvent e )
      {
        repaint();
      }
    } );
  }

  public void switchState( STATE state )
  {
    if ( CURRENT_STATE != state )
    {
      CURRENT_STATE = state;
      removeAll();
      switch ( state )
      {
        case DEFAULT:
          add( defaultPanel );
          requestFocus();
          setBackground( UIManager.getColor( "Button.background" ) );
          break;
        case BUY:
          add( buyPanel );
          GUIObjects.mainframe.getDashboard().resetAllDrinkButtons( this );
          acceptButton.requestFocus();
          setBackground( UIManager.getColor( "Button.hoverBackground" ) );
          break;
      }
      repaint();
      revalidate();
    }
  }

  @Override
  public void setBackground( Color bg )
  {
    super.setBackground( bg );
    if ( defaultPanel != null && buyPanel != null )
    {
      defaultPanel.setBackground( bg );
      buyPanel.setBackground( bg );
    }
    repaint();
  }

  public STATE getCURRENT_STATE()
  {
    return CURRENT_STATE;
  }
}

enum STATE
{
  DEFAULT,
  BUY;
}