package com.isp.memate.actionbar;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;


/**
 * @author dtr
 * @since 28.01.2020
 *
 */
public class MeMateActionBar extends JPanel
{
  /**
   * Darstellungsmodi der ActionBar.
   *
   * @author dtr
   * @since 21.09.2018
   */
  public enum BarType
  {
    /** Einfache Darstellung ohne Anzeigewechsel */
    SIMPLE,
    /** Durch zusätzlichen Button können Button in verschiedenen Größen angezeigt werden */
    SHOW_LABLES;
  }

  private final JPanel     contentPanel;
  private JPanel           viewButton;
  private final JPanel     contextMenuButton;
  private final JPopupMenu invisibleButtonPopupMenu;

  //Die Liste beinhaltet den Button für ButtonBar und den dazugehörigen Button im Kontextmenü
  private final List<MeMateActionBarButton> allButtons = new ArrayList<>();


  private boolean labelsVisible = false;

  private boolean darkModeOn = false;

  private Optional<Component> lastSeparator = Optional.empty();

  private final int MIN_GROUP_INSET = 25;

  private Icon viewBlack = new ImageIcon( getClass().getClassLoader().getResource( "view_black_24.png" ) );
  private Icon viewWhite = new ImageIcon( getClass().getClassLoader().getResource( "view_white_24.png" ) );

  private final Icon arrowBlack = new ImageIcon( "C:/Users/nwe/Desktop/IconsMeMate/Burger/arrow_down_black.png" );
  private final Icon arrowWhite = new ImageIcon( "C:/Users/nwe/Desktop/IconsMeMate/Burger/arrow_down_white.png" );

  private Color backgroundColor;
  private Color foregoundColor;
  private Color darkModeBackground = new Color( 29, 164, 165 );//Color.gray;//168.168.168

  private MeMateActionBarButton burgerButton;


  /**
   * Erzeugt anhand des übergebenen {@link BarType BarTypes} eine neuen ButtonBar.
   *
   * @param type Darstellungsmodi der ButtonBar.
   */
  public MeMateActionBar(
                          final Color backgroundColor,
                          final Color foregoundColor )
  {
    super( new BorderLayout() );
    this.backgroundColor = backgroundColor;
    this.foregoundColor = foregoundColor;

    //In diesem Panel befinden sich später die hinzugefügten Buttons.
    contentPanel = new JPanel();
    contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );

    add( contentPanel, BorderLayout.CENTER );


    /*
     * Hier wird der Button initalisiert, der bei Klick ein Kontextmenü öffnet, welches die Buttons beinhalten, die aktuell nicht im sichtbaren Bereich der ButtonBar sich befinden.
     * Dieser Button ist Standardmäßig ausgeblendet und wird nur angezeigt,
     * wenn nicht alle Buttons in der ToolBar angezeigt werden können.
     *
     * Der Button wird ganz unter in der ButtonBar angezeigt.
     */
    invisibleButtonPopupMenu = new JPopupMenu();


    final MeMateActionBarButton context = new MeMateActionBarButton( arrowBlack, arrowWhite, backgroundColor, BorderLayout.CENTER );
    context.addMouseListener( new MeMateActionBarListener( context,
        () ->
        {//Standard-Darstellung.
          context.setBackground( backgroundColor );
        },
        () ->
        {//Maus-Hover
          context.setBackground( backgroundColor.darker() );
        } ) );
    contextMenuButton = context.getBarButton();

    contextMenuButton.addMouseListener( new MouseAdapter()
    {
      @Override
      public void mousePressed( final MouseEvent e )
      {//Der Button zeigt das Menu an, in dem sich die nicht sichtbaren Buttons befinden.
        invisibleButtonPopupMenu.show( e.getComponent(), e.getX(), e.getY() );
      }
    } );

    contextMenuButton.setVisible( false );
    add( contextMenuButton, BorderLayout.SOUTH );

    /*
     * Das ContentPanel bekommt ein Listener der auf verändern der Größe (Höhe) des Panels hört.
     * Es muss überprüft werden, ob die Buttons noch komplett im sichtbaren Bereich sind, oder diese in das Kontextmenü angezeigt werden
     * müssen und der ContextButton sichtbar geschaltet werden muss
     */
    contentPanel.addComponentListener( new ComponentAdapter()
    {
      // Hilfsvariable, um herrauszufinden, ob die AKtionsleiste größer oder kleiner wird.
      int oldHeight = -1;

      @Override
      public void componentResized( final ComponentEvent e )
      {
        boolean isBigger = false;

        //Überprüfung, ob sich die Aktionsleiste vergrößert oder verkleinert.
        if ( oldHeight != -1 )
        {
          isBigger = oldHeight < contentPanel.getHeight();
        }

        oldHeight = contentPanel.getHeight();

        //Initial setzen wir die Sichtbarkeit des ContextButtons auf false
        boolean showContextButton = false;

        //Über alle hinzugefügten Buttons iterieren
        for ( final MeMateActionBarButton button : allButtons )
        {
          //Button-Y-Position ermitteln.
          double buttonYPos = button.getBarButton().getLocation().getY();

          /*
           * Spezial-Fall: Wenn ein Glue hinzugefügt wurden sind
           * Wenn die unterste Button-Gruppe komplett ausgeblendet ist, muss der Glue (Separator bei dem ermitteln der Y-Pos mit verrechnet werden.)
           */
          if ( buttonYPos + 1 >= contentPanel.getSize().getHeight() && lastSeparator.isPresent() )
          {
            buttonYPos = buttonYPos - lastSeparator.get().getHeight();
          }

          /*
           * Überprüfen ob der Button noch im sichtbaren Bereich ist.
           */
          double yEndPos = button.getBarButton().getPreferredSize().getHeight() + buttonYPos;

          /**
           * Wenn die Aktionsleiste größer wird, muss ein sonder Behandlung durchgeführt werden, wenn nur noch ein
           * Button im nicht sichtbaren Bereich ist.
           *
           * Grund: Der letzte Button in der ActionBar wird erst dann wieder eingeblendet wenn genug Platz für dieses
           * Element und den "ContextMenuButton" vorhanden ist.
           *
           * Der ContextMenuButton muss mit verrechnet werden, wenn es sich um den letzten Button handelt und die
           * Aktionsleiste vergrößert wird.
           */
          if ( contextMenuButton.isVisible() && isBigger )
          {
            int itemsInContextMenu = 0;

            // Überprüfen, wie viele Buttons aktuell im nicht sichtbaren Bereich sind.
            for ( final Component popupItem : invisibleButtonPopupMenu.getComponents() )
            {
              if ( popupItem.isVisible() )
              {
                itemsInContextMenu++;
                if ( itemsInContextMenu > 1 )
                {
                  break;
                }
              }
            }

            //Wenn nur noch ein Button nicht angezeigt wird, muss die Höhe des ContextMenuButton mit verrechnet werden.
            if ( itemsInContextMenu == 1 )
            {
              yEndPos = yEndPos - contextMenuButton.getHeight();
            }
          }

          final boolean buttonInVisibleArea = contentPanel.getSize().getHeight() >= yEndPos;

          /*
           * Je nachdem ob der Button im sichtbaren Bereich ist oder nicht,
           * wird der Buttons (in der Bar und im KontextMenü) ein/ausgblendet
           */
          button.getBarButton().setVisible( buttonInVisibleArea );
          button.getMenuButton().setVisible( !buttonInVisibleArea );

          if ( !(button.getBarButton().isVisible()) )
          {//Wenn mindesten ein Button nicht mehr sichtbar ist muss der Contextbutton sicht bar werden.
            showContextButton = true;
          }
        }

        //Sichtbarkeit des ContextButtons wird gesetzt
        contextMenuButton.setVisible( showContextButton );
      }
    } );

    //ButtonBar bekommt Initial-Farbe.
    setBackground( backgroundColor );
  }

  public void installToggleButtonTitleVisibleState()
  {
    createToggleButtonTitleVisibleState();
    add( createToggleButtonTitleVisibleState().getBarButton(), BorderLayout.NORTH );
  }

  public MeMateActionBarButton createToggleButtonTitleVisibleState()
  {
    burgerButton =
        new MeMateActionBarButton( viewBlack, viewWhite, backgroundColor, () -> changeLabelVisibleState() );

    burgerButton.addMouseListener( new MeMateActionBarListener( burgerButton,
        () ->
        {//Standard-Darstellung.
          burgerButton.setBackground( backgroundColor );
        },
        () ->
        {//Maus-Hover
          burgerButton.setBackground( backgroundColor.darker() );
        } ) );

    return burgerButton;
  }

  @SuppressWarnings( "javadoc" )
  public MeMateActionBarButton addActionButton( final Icon icon, final Icon pressedIcon, final String title, final String tooltip,
                                                final Runnable runnable )
  {
    return addActionButton( icon, pressedIcon, title, tooltip, null, runnable );
  }

  public MeMateActionBarButton addActionButton( final Icon icon, final Icon pressedIcon, final String title, final String tooltip,
                                                final Color markerColor, final Runnable runnable )
  {
    final MeMateActionBarButton button =
        new MeMateActionBarButton( title, tooltip, icon, pressedIcon, backgroundColor, foregoundColor, markerColor, runnable );

    button.addMouseListener( new MeMateActionBarListener( button,
        () -> button.setBackground( backgroundColor ),
        () -> button.setBackground( backgroundColor.darker() ) ) );

    if ( markerColor != null )
    {
      button.addMouseListener( new MouseAdapter()
      {
        @Override
        public void mouseReleased( final MouseEvent e )
        {
          if ( button.isEnabled() )
          {
            selectButton( button.getTitle() );
          }
        };
      } );
    }

    allButtons.add( button );
    contentPanel.add( button.getBarButton() );
    invisibleButtonPopupMenu.add( button.getMenuButton() );

    button.setTitleVisible( labelsVisible );

    return button;
  }

  /**
   * Ändert die Sichtbarkeit der Labels der hinzugefügten Buttons.
   */
  public void changeLabelVisibleState()
  {
    setLabelsVisible( !labelsVisible );
  }

  /**
   * @return the labelsVisible Liefert den Status ob die Actionbar eingeklappt oder ausgeklappt ist.
   */
  public boolean isLabelsVisible()
  {
    return labelsVisible;
  }

  /**
   * Status ob die Labels in der ActionBar angezeigt werden sollen oder nicht.
   *
   * @param state Wenn {@code true} werden die LAbels angezeigt.
   */
  public void setLabelsVisible( final boolean state )
  {
    labelsVisible = state;

    allButtons.forEach( button -> button.setTitleVisible( labelsVisible ) );

    repaint();
    validate();
  }

  /**
   * Fügt einen freien Bereich in die ButtonBar hinzu.
   */
  public void addFixGlue()
  {
    addGlue( false );
  }

  /**
   * Fügt einen freien Bereich in die ButtonBar hinzu.
   */
  public void addVariableGlue()
  {
    addGlue( true );
  }

  private void addGlue( final boolean resizeable )
  {
    //Da Gruppierung immer eindeutig sichtbar sein sollen müssen wird zunäch ein festen Inset hinzufügen.
    contentPanel.add( Box.createRigidArea( new Dimension( 0, MIN_GROUP_INSET ) ) );

    if ( resizeable )
    {
      //Als nächstes fügen wir ein Glue hinzu, damit sich die Buttons vernünftig ausrichten bei Größenänderung der Bar.
      final Component glue = Box.createVerticalGlue();
      contentPanel.add( glue );

      //Der letzte Separator ist zum ermitteln der nicht sichtbaren Buttons wichtig.
      lastSeparator = Optional.of( glue );
    }
  }

  /**
   * Selektiert den übergeben Button, Anhand des Titles. Alle anderen Buttons werden bei dieser Aktion
   * deslektiert.
   *
   * @param byTitle Title des Buttons.
   */
  public void selectButton( final String byTitle )
  {

    allButtons.forEach( btn ->
    {
      if ( !isNullOrEmpty( byTitle ) && byTitle.equals( btn.getTitle() ) )
      {
        btn.selected();
      }
      else
      {
        btn.unselected();
      }
    } );

  }

  /**
   * @param byTitle
   * @return
   */
  private boolean isNullOrEmpty( String test )
  {
    if ( test == null || test.isEmpty() )
    {
      return true;
    }
    return false;
  }

  /**
   * Fügt einen Separator zur ActionBar hinzu.
   */
  public void addSeparator()
  {
    final JSeparator separator = new JSeparator();
    separator.setMaximumSize( new Dimension( getMaximumSize().width, 2 ) );
    contentPanel.add( separator );
  }


  @Override
  public void setBackground( final Color bg )
  {
    backgroundColor = bg;

    super.setBackground( bg );

    if ( viewButton != null )
    {
      viewButton.setBackground( bg );
    }

    if ( contextMenuButton != null )
    {
      contextMenuButton.setBackground( bg );
    }

    if ( contentPanel != null )
    {
      contentPanel.setBackground( bg );
    }
    if ( allButtons != null )
    {
      allButtons.forEach( buttons -> buttons.setBackground( bg ) );
    }
  }

  /**
   * 
   */
  public void toggleDarkmode()
  {
    if ( darkModeOn )
    {
      setBackground( Color.white );
      burgerButton.setBackground( Color.white );
      Icon temp = viewBlack;
      viewBlack = viewWhite;
      viewWhite = temp;
      allButtons.forEach( btn ->
      {
        Icon icon = btn.getIcon();
        Icon pressedIcon = btn.getPressedIcon();
        btn.setIcon( pressedIcon );
        btn.setPressedIcon( icon );
        btn.toggleFontColor();
        btn.setMarker( new Color( 29, 164, 165 ) );
      } );
      Icon icon = burgerButton.getIcon();
      Icon pressedIcon = burgerButton.getPressedIcon();
      burgerButton.setIcon( pressedIcon );
      burgerButton.setPressedIcon( icon );
      burgerButton.toggleFontColor();

      darkModeOn = false;
    }
    else
    {
      setBackground( darkModeBackground );
      burgerButton.setBackground( darkModeBackground );
      Icon temp = viewBlack;
      viewBlack = viewWhite;
      viewWhite = temp;
      allButtons.forEach( btn ->
      {
        Icon icon = btn.getPressedIcon();
        Icon pressedIcon = btn.getIcon();
        btn.setIcon( icon );
        btn.setPressedIcon( pressedIcon );
        btn.toggleFontColor();
        btn.setMarker( Color.white );
      } );
      Icon icon = burgerButton.getPressedIcon();
      Icon pressedIcon = burgerButton.getIcon();
      burgerButton.setIcon( icon );
      burgerButton.setPressedIcon( pressedIcon );
      burgerButton.toggleFontColor();
      darkModeOn = true;
    }
  }

  /**
   * @return ob der Darkmode an oder aus ist
   */
  public boolean darkModeOn()
  {
    return darkModeOn;
  }
}
