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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.NumberFormat;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.InfoIcon;
import com.isp.memate.util.MeMateUIManager;

/**
 * Die Klasse DrinkConsumptionButton erzeugt eine Komponente, welcher auf einem
 * {@link JPanel} basiert, aber einen Button nachahmt. Man kann ihn fokusieren
 * und auch das Highlighting verhält sich ähnlich zu einem echten Button.
 *
 * @author nwe
 * @since 18.10.2019
 */
class DrinkConsumptionButton extends JPanel
{
  private static final Color HOVER_BACKGROUND_COLOR = new Color( 186, 232, 232 );
  private final Border       DEFAULT_LINE_BORDER    = BorderFactory.createLineBorder( new Color( 173, 173, 173, 0 ) );
  private final Border       DEFAULT_BORDER         = BorderFactory.createCompoundBorder( DEFAULT_LINE_BORDER,
      BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) );
  private final Border       FOCUS_BORDER           =
      BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( UIManager.getColor( "AppColor" ) ),
          BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) );
  Cache                      cache                  = Cache.getInstance();

  private final JLabel       nameLabel                              = MeMateUIManager.createJLabel( "drinkButtons" );
  private final JLabel       priceLabel                             = MeMateUIManager.createJLabel( "drinkButtons" );
  private final JPanel       infoPanel                              = MeMateUIManager.createJPanel( "drinkButtons" );
  private final JPanel       nameLabelAndDrinkInfoButtonPanel       = MeMateUIManager.createJPanel( "drinkButtons" );
  private final JButton      acceptButton                           = MeMateUIManager.createButton( "button" );
  private final JButton      abortButton                            = MeMateUIManager.createButton( "button" );
  private JButton            infoButton                             = MeMateUIManager.createButton( "button" );
  private final JLabel       askWhetherToReallyConsumeLabel         = MeMateUIManager.createJLabel( "drinkButtons" );
  private final JLabel       textLabel                              = MeMateUIManager.createJLabel();
  private final JLayeredPane overlay                                = new JLayeredPane();
  private final JLabel       iconLabel;
  private final JPanel       fillPanel                              = MeMateUIManager.createJPanel( "drinkButtons" );
  private MouseListener      mouseListener;
  private FocusListener      focusListener;
  private KeyListener        keyListener;
  private ActionListener     acceptAction;
  private ActionListener     abortAction;
  boolean                    askWhetherToReallyConsumeLabelIsActive = false;

  /**
   * Erzeugt eine Komponente, welche auf einem {@link JPanel} basiert, aber einen
   * Button nachahmt.
   *
   * @param mainFrame Parent-Mainframe für Zugriff auf state
   * @param price Preis des Getränks, welcher oben im "Button" angezeigt
   *          werden soll
   * @param name Name des Getränks, welcher unten im "Button" angezeigt
   *          werden soll
   * @param icon Bild des Getränks, welches mittig im "Button" angezeigt
   *          werden soll
   */
  DrinkConsumptionButton( final Mainframe mainFrame, String price, final String name, final Icon icon )
  {
    infoButton = new JButton( new InfoIcon() )
    {
      JToolTip tooltip;

      @Override
      public JToolTip createToolTip()
      {
        if ( tooltip == null )
        {
          final JPanel panel = new JPanel( new GridBagLayout() );
          final GridBagConstraints constraints = new GridBagConstraints();
          constraints.gridx = 1;
          constraints.gridy = 1;
          constraints.fill = GridBagConstraints.BOTH;
          loadInfoPanelSettings();
          panel.add( infoPanel, constraints );
          tooltip = super.createToolTip();
          tooltip.setLayout( new BorderLayout() );
          final Insets insets = tooltip.getInsets();
          final Dimension panelSize = panel.getPreferredSize();
          panelSize.width += insets.left + insets.right;
          panelSize.height += insets.top + insets.bottom;
          tooltip.setPreferredSize( panelSize );
          tooltip.add( panel );
        }
        return tooltip;
      }

      private void loadInfoPanelSettings()
      {
        infoPanel.setLayout( new GridBagLayout() );
        final DrinkIngredients ingredients = cache.getIngredients( name );
        final String[] ingredientsArray = ingredients.ingredients.trim().split( "," );
        int maxLength = 40;
        for ( final String string : ingredientsArray )
        {
          if ( string.length() > maxLength )
          {
            maxLength = string.length() + 2;
          }
        }
        int currentLength = 0;
        final StringBuilder listBuilder = new StringBuilder();
        listBuilder.append( "<html>Zutaten:<br>" );
        for ( final String element : ingredientsArray )
        {
          currentLength += element.length() + 2;
          if ( currentLength > maxLength )
          {
            listBuilder.append( "<br>" );
            currentLength = element.length() + 2;
          }
          listBuilder.append( element ).append( ", " );
        }
        String amountString = "";
        if ( ingredients.amount != 0 )
        {
          amountString = "<br><br>Menge: " + String.format( "%.2f", ingredients.amount ) + " Liter";
        }
        textLabel.setText( listBuilder.toString().substring( 0, listBuilder.length() - 2 )
            + amountString + "<br><br>Durchschnittlicher Gehalt je 100ml<br>" );

        textLabel.setHorizontalAlignment( SwingConstants.LEFT );
        final GridBagConstraints textLabelConstraints = new GridBagConstraints();
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
        //Otherwise the generated panels aren't rendered correctly
        MeMateUIManager.updateJPanel( "drinkButtons" );
        MeMateUIManager.updateLabel( "default" );
        MeMateUIManager.updateSeperator( "default" );
      }

      private void addPanel( final String ingredient, final int y, final JPanel mainPanel,
                             final DrinkIngredients ingredients )
      {
        final JPanel panel = MeMateUIManager.createJPanel( "drinkButtons" );
        panel.setLayout( new GridBagLayout() );

        final JLabel label = MeMateUIManager.createJLabel();
        label.setText( ingredient + " " );
        final GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.weightx = 0;
        labelConstraints.anchor = GridBagConstraints.LAST_LINE_START;
        panel.add( label, labelConstraints );

        final JComponent separator = new JComponent()
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
        final GridBagConstraints seperatorConstraints = new GridBagConstraints();
        seperatorConstraints.gridx = 1;
        seperatorConstraints.gridy = 0;
        seperatorConstraints.weightx = 1;
        seperatorConstraints.insets = new Insets( 0, 0, 3, 0 );
        seperatorConstraints.anchor = GridBagConstraints.SOUTH;
        seperatorConstraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add( separator, seperatorConstraints );

        final JLabel amountLabel = MeMateUIManager.createJLabel();
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
        final GridBagConstraints amountLabelConstraints = new GridBagConstraints();
        amountLabelConstraints.gridx = 2;
        amountLabelConstraints.gridy = 0;
        amountLabelConstraints.weightx = 0;
        amountLabelConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        panel.add( amountLabel, amountLabelConstraints );

        final GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 0;
        panelConstraints.gridy = y;
        panelConstraints.gridwidth = 3;
        panelConstraints.fill = GridBagConstraints.HORIZONTAL;
        panelConstraints.insets = new Insets( 0, 2, 0, 2 );
        infoPanel.add( panel, panelConstraints );
      }
    };
    infoButton.setToolTipText( "" );
    infoButton.setContentAreaFilled( false );
    infoButton.setOpaque( false );
    infoButton.setFocusable( false );
    infoButton.setBorder( BorderFactory.createEmptyBorder() );
    final int defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
    infoButton.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mouseEntered( final MouseEvent me )
      {
        ToolTipManager.sharedInstance().setInitialDelay( 1 );
      }

      @Override
      public void mouseExited( final MouseEvent me )
      {
        ToolTipManager.sharedInstance().setInitialDelay( defaultInitialDelay );
      }
    } );

    MeMateUIManager.registerInfoButton( infoButton );

    setLayout( new BorderLayout() );
    acceptButton.setText( "Ja" );
    abortButton.setText( "Nein" );
    askWhetherToReallyConsumeLabel.setText( "Wirklich konsumieren?" );
    nameLabelAndDrinkInfoButtonPanel.setLayout( new GridBagLayout() );
    infoPanel.setLayout( new GridBagLayout() );
    nameLabel.setText( name );
    nameLabel.setFont( nameLabel.getFont().deriveFont( 14f ) );
    nameLabel.setHorizontalAlignment( SwingConstants.CENTER );
    final GridBagConstraints nameLabelConstraints = new GridBagConstraints();
    nameLabelConstraints.gridx = 1;
    nameLabelConstraints.gridy = 0;
    nameLabelConstraints.fill = GridBagConstraints.HORIZONTAL;
    nameLabelConstraints.weightx = 1;
    nameLabelAndDrinkInfoButtonPanel.add( nameLabel, nameLabelConstraints );
    if ( cache.hasIngredients( name ) )
    {
      final GridBagConstraints infoButtonConstraints = new GridBagConstraints();
      infoButtonConstraints.gridx = 2;
      infoButtonConstraints.gridy = 0;
      infoButtonConstraints.weightx = 0;
      infoButtonConstraints.anchor = GridBagConstraints.LINE_END;
      nameLabelAndDrinkInfoButtonPanel.add( infoButton, infoButtonConstraints );

      final GridBagConstraints fillLablenConstraints = new GridBagConstraints();
      fillLablenConstraints.gridx = 0;
      fillLablenConstraints.gridy = 0;
      fillLablenConstraints.weightx = 0;
      fillLablenConstraints.anchor = GridBagConstraints.LINE_START;
      nameLabelAndDrinkInfoButtonPanel.add( fillPanel, fillLablenConstraints );
    }
    infoButton.addComponentListener( new ComponentAdapter()
    {
      @Override
      public void componentResized( final ComponentEvent e )
      {
        fillPanel.setPreferredSize( infoButton.getPreferredSize() );
        fillPanel.setMaximumSize( infoButton.getMaximumSize() );
        fillPanel.setMinimumSize( infoButton.getMinimumSize() );
        fillPanel.setSize( infoButton.getSize() );
        nameLabelAndDrinkInfoButtonPanel.revalidate();
        nameLabelAndDrinkInfoButtonPanel.repaint();
      }
    } );

    price = price.replace( "€", "" );
    final Float priceAsFloat = Float.valueOf( price );
    final NumberFormat formatter = NumberFormat.getCurrencyInstance();
    final String format = formatter.format( priceAsFloat.doubleValue() );
    final int amount = cache.getAmount( name );
    //FIXME(nwe | 09.06.2020): Nicht So !!!!!!!!!
    if ( amount > 99 )
    {
      priceLabel.setText( format + "                             Noch "
          + amount + " Stück" );
    }
    else if ( amount > 9 )
    {
      priceLabel.setText( format + "                               Noch "
          + amount + " Stück" );
    }
    else
    {
      priceLabel.setText( format + "                                Noch "
          + amount + " Stück" );
    }
    priceLabel.setFont( priceLabel.getFont().deriveFont( 14f ) );
    priceLabel.setHorizontalAlignment( SwingConstants.CENTER );

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
    askWhetherToReallyConsumeLabel.setHorizontalAlignment( SwingConstants.CENTER );
    askWhetherToReallyConsumeLabel.setBorder( new EmptyBorder( 40, 0, 30, 0 ) );
    askWhetherToReallyConsumeLabel.setFont( askWhetherToReallyConsumeLabel.getFont().deriveFont( 16f ) );

    acceptAction = e ->
    {
      remove( overlay );
      remove( askWhetherToReallyConsumeLabel );
      iconLabel.setVisible( true );
      infoButton.setVisible( true );
      fillPanel.setVisible( true );
      repaint();
      revalidate();
      requestFocus();
    };
    acceptButton.addActionListener( acceptAction );

    abortAction = e ->
    {
      remove( overlay );
      remove( askWhetherToReallyConsumeLabel );
      iconLabel.setVisible( true );
      infoButton.setVisible( true );
      fillPanel.setVisible( true );
      if ( MeMateUIManager.getDarkModeState() )
      {
        setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
        nameLabelAndDrinkInfoButtonPanel
            .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
        infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
        fillPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
        final Component[] components = infoPanel.getComponents();
        for ( final Component component : components )
        {
          component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
        }
      }
      else
      {
        setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
        nameLabelAndDrinkInfoButtonPanel
            .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
        infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
        fillPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
        final Component[] components = infoPanel.getComponents();
        for ( final Component component : components )
        {
          component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
        }
      }
      repaint();
      revalidate();
      requestFocus();
    };
    abortButton.addActionListener( abortAction );

    // The following listeners correctly implement the semantics for a button.
    // That is focus behavior, mouse movement / click behavior and confirmation via
    // keyboard.
    mouseListener = new MouseAdapter()
    {
      @Override
      public void mouseClicked( final MouseEvent __ )
      {
        askWhetherToReallyConsume( name, abortAction );
      }

      @Override
      public void mouseEntered( final MouseEvent __ )
      {
        if ( MeMateUIManager.getDarkModeState() )
        {
          setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          nameLabelAndDrinkInfoButtonPanel
              .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          fillPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          final Component[] components = infoPanel.getComponents();
          for ( final Component component : components )
          {
            component
                .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
          }
        }
        else
        {
          setBackground( HOVER_BACKGROUND_COLOR );
          nameLabelAndDrinkInfoButtonPanel.setBackground( HOVER_BACKGROUND_COLOR );
          infoPanel.setBackground( HOVER_BACKGROUND_COLOR );
          fillPanel.setBackground( HOVER_BACKGROUND_COLOR );
          final Component[] components = infoPanel.getComponents();
          for ( final Component component : components )
          {
            component.setBackground( HOVER_BACKGROUND_COLOR );
          }
        }
      }

      @Override
      public void mouseExited( final MouseEvent __ )
      {
        if ( MeMateUIManager.getDarkModeState() )
        {
          setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          nameLabelAndDrinkInfoButtonPanel
              .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          fillPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          final Component[] components = infoPanel.getComponents();
          for ( final Component component : components )
          {
            component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
          }
        }
        else
        {
          setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          nameLabelAndDrinkInfoButtonPanel
              .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          fillPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          final Component[] components = infoPanel.getComponents();
          for ( final Component component : components )
          {
            component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
          }
        }
      }

      @Override
      public void mousePressed( final MouseEvent __ )
      {
        requestFocus();
        setBackground( UIManager.getColor( "AppColor" ) );
        nameLabelAndDrinkInfoButtonPanel.setBackground( UIManager.getColor( "AppColor" ) );
        infoPanel.setBackground( UIManager.getColor( "AppColor" ) );
        fillPanel.setBackground( UIManager.getColor( "AppColor" ) );
        final Component[] components = infoPanel.getComponents();
        for ( final Component component : components )
        {
          component.setBackground( UIManager.getColor( "AppColor" ) );
        }
      }

      @Override
      public void mouseReleased( final MouseEvent event )
      {
        if ( SwingUtilities.getLocalBounds( DrinkConsumptionButton.this ).contains( event.getPoint() ) )
        {
          setBackground( HOVER_BACKGROUND_COLOR );
          nameLabelAndDrinkInfoButtonPanel.setBackground( HOVER_BACKGROUND_COLOR );
          infoPanel.setBackground( HOVER_BACKGROUND_COLOR );
          fillPanel.setBackground( HOVER_BACKGROUND_COLOR );
          final Component[] components = infoPanel.getComponents();
          for ( final Component component : components )
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
            nameLabelAndDrinkInfoButtonPanel
                .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
            infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
            fillPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
            final Component[] components = infoPanel.getComponents();
            for ( final Component component : components )
            {
              component.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
            }
          }
          else
          {
            setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
            nameLabelAndDrinkInfoButtonPanel
                .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
            infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
            fillPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDayColor() );
            final Component[] components = infoPanel.getComponents();
            for ( final Component component : components )
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
      public void focusLost( final FocusEvent __ )
      {
        setBorder( UIManager.getBorder( "DefaultBorder" ) );
      }

      @Override
      public void focusGained( final FocusEvent __ )
      {
        setBorder( UIManager.getBorder( "FocusBorder" ) );
      }
    };
    keyListener = new KeyAdapter()
    {
      @Override
      public void keyPressed( final KeyEvent event )
      {
        if ( event.getKeyCode() == KeyEvent.VK_ENTER || event.getKeyCode() == KeyEvent.VK_SPACE )
        {
          askWhetherToReallyConsume( name, abortAction );
        }
      }
    };
    final Action abortAction = new AbstractAction()
    {
      @Override
      public void actionPerformed( final ActionEvent e )
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
    MeMateUIManager.registerPanel( "drinkButton", this );
  }

  /**
   * Sobald man auf ein Getränk klickt, so öffnet sich ein Dialog, welcher fragt,
   * ob man das Getränk wirklich konsumieren möchte. Bestätigt man dies, so wird
   * {@linkplain ServerCommunication} mitgeteilt, welches Getränk man gekauft hat.
   *
   * @param drinkName Name des Getränks
   * @param abortButtonListener
   */
  private void askWhetherToReallyConsume( final String drinkName, final ActionListener abortButtonListener )
  {
    GUIObjects.mainframe.getDashboard().resetAllDrinkButtons();
    infoButton.setVisible( false );
    fillPanel.setVisible( false );
    askWhetherToReallyConsumeLabelIsActive = true;
    if ( MeMateUIManager.getDarkModeState() )
    {
      setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
      nameLabelAndDrinkInfoButtonPanel
          .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
      infoPanel.setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor().brighter() );
      final Component[] components = infoPanel.getComponents();
      for ( final Component component : components )
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
    catch ( final Exception exception )
    {
      ClientLog.newLog( exception.getMessage() );
    }
    add( askWhetherToReallyConsumeLabel, BorderLayout.NORTH );
    add( overlay, BorderLayout.CENTER );
    repaint();
    revalidate();
    acceptButton.requestFocus();
    final ActionListener actionListener = new ActionListener()
    {
      @Override
      public void actionPerformed( final ActionEvent e )
      {
        final ReentrantLock lock = ServerCommunication.getInstance().lock;
        lock.lock();
        try
        {
          ServerCommunication.getInstance().consumeDrink( drinkName );
          ServerCommunication.getInstance().getBalance();
          acceptButton.removeActionListener( this );
          final ActionListener[] array = abortButton.getActionListeners();
          for ( final ActionListener actionListener : array )
          {
            abortButton.removeActionListener( actionListener );
          }
          abortButton.addActionListener( abortButtonListener );
          DrinkConsumptionButton.this.addMouseListener( mouseListener );
          DrinkConsumptionButton.this.addKeyListener( keyListener );
          GUIObjects.mainframe.setUndoButtonEnabled( true );
          fillPanel.setVisible( true );
          infoButton.setVisible( true );
          askWhetherToReallyConsumeLabelIsActive = false;
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
    };
    acceptButton.addActionListener( actionListener );
    final ActionListener actionListener2 = new ActionListener()
    {
      @Override
      public void actionPerformed( final ActionEvent e )
      {
        acceptButton.removeActionListener( actionListener );
        DrinkConsumptionButton.this.addMouseListener( mouseListener );
        DrinkConsumptionButton.this.addKeyListener( keyListener );
        abortButton.removeActionListener( this );
        fillPanel.setVisible( true );
        infoButton.setVisible( true );
        askWhetherToReallyConsumeLabelIsActive = false;
      }
    };
    abortButton.addActionListener( actionListener2 );
  }

  void reset()
  {
    infoButton.setVisible( true );
    fillPanel.setVisible( true );
    final ActionListener[] abortListenerArray = abortButton.getActionListeners();
    for ( final ActionListener actionListener : abortListenerArray )
    {
      abortButton.removeActionListener( actionListener );
    }
    final ActionListener[] acceptListenerArray = abortButton.getActionListeners();
    for ( final ActionListener actionListener : acceptListenerArray )
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
      nameLabelAndDrinkInfoButtonPanel
          .setBackground( MeMateUIManager.getBackground( "drinkButtons" ).getDarkColor() );
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

  JButton getInfoButton()
  {
    return infoButton;
  }
}