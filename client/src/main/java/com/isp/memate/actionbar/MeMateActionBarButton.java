/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.actionbar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.isp.memate.util.MeMateUIManager;


/**
 * @author dtr
 * @since 29.01.2020
 *
 */
public class MeMateActionBarButton
{
  private Icon icon;
  private Icon pressedIcon;

  private String title;
  private String tooltip;

  private Color background;
  private Color foreground;
  private Color marker;

  private Runnable runnable;

  private boolean enabled    = true;
  private boolean darkModeOn = false;

  private JPanel barPanel;
  private JPanel menuPanel;

  private boolean isMarked = false;

  private final List<JLabel> iconLabels   = new ArrayList<>();
  private final List<JPanel> markerPanels = new ArrayList<>();
  private final List<JLabel> titleLabels  = new ArrayList<>();
  private String             iconOrientation;


  @SuppressWarnings( "javadoc" )
  public MeMateActionBarButton( final Icon icon, final Color background, final Runnable runnable )
  {
    this( icon, icon, background, runnable );
  }


  @SuppressWarnings( "javadoc" )
  public MeMateActionBarButton( final String title, final String tooltip, final Color background, final Color foreground,
                                final Runnable runnable )
  {
    this( title, tooltip, null, null, background, foreground, null, runnable, null );
  }


  @SuppressWarnings( "javadoc" )
  public MeMateActionBarButton( final Icon icon, final Icon pressedIcon, final Color background, final String iconOrientation )
  {
    this( null, null, icon, pressedIcon, background, null, null, null, iconOrientation );
  }


  @SuppressWarnings( "javadoc" )
  public MeMateActionBarButton( final Icon icon, final Icon pressedIcon, final Color background, final Runnable runnable )
  {
    this( null, null, icon, pressedIcon, background, null, null, runnable, BorderLayout.WEST );
  }


  @SuppressWarnings( "javadoc" )
  public MeMateActionBarButton( final String title, final String tooltip, final Icon icon, final Icon pressedIcon, final Color background,
                                final Color foreground, final Color marker, final Runnable runnable )
  {
    this( title, tooltip, icon, pressedIcon, background, foreground, marker, runnable, BorderLayout.WEST );
  }


  @SuppressWarnings( "javadoc" )
  public MeMateActionBarButton( final String title, final String tooltip, final Icon icon, final Icon pressedIcon, final Color background,
                                final Color foreground, final Color marker, final Runnable runnable, final String iconOrientation )
  {
    this.title = title;
    this.tooltip = tooltip;
    this.icon = icon;
    this.pressedIcon = pressedIcon;
    this.background = background;
    this.foreground = foreground;
    this.marker = marker;
    this.runnable = runnable;
    this.iconOrientation = iconOrientation;

    initButton();
  }


  private void initButton()
  {
    barPanel = createButtonPanel();
    menuPanel = createButtonPanel();
  }


  private JPanel createButtonPanel()
  {
    final JPanel actionPanel = new JPanel( new BorderLayout() );
    actionPanel.setBackground( background );
    if ( !isNullOrEmpty( tooltip ) )
    {
      actionPanel.setToolTipText( tooltip );
    }
    if ( icon != null )
    {
      if ( !isNullOrEmpty( title ) )
      {//nur wenn der Titel nicht leer ist muss ein Label gesetzt werden

        actionPanel.add( createIconLable(), iconOrientation );


        final JLabel titleLabel = createTitleLable( foreground, 0, title );
        titleLabel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 15 ) );
        titleLabels.add( titleLabel );

        actionPanel.add( titleLabel, BorderLayout.CENTER );
      }
      else
      {//wenn es kein Titel gibt soll das Icon zentriert werden.
        actionPanel.add( createIconLable(), iconOrientation );
      }
    }
    else
    {
      actionPanel.setBorder( BorderFactory.createLineBorder( new Color( 173, 173, 173 ) ) );
      final JLabel titleLabel = createTitleLable( foreground, 0, title );
      titleLabel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
      titleLabels.add( titleLabel );

      actionPanel.add( titleLabel, BorderLayout.CENTER );
    }
    //Die Buttons sollen eine feste größe Bekommen.
    actionPanel.setMaximumSize( new Dimension( actionPanel.getMaximumSize().width, actionPanel.getPreferredSize().height ) );
    actionPanel.setEnabled( false );
    return actionPanel;
  }

  /**
   * @param tooltip2
   * @return
   */
  private boolean isNullOrEmpty( String tooltip2 )
  {
    if ( tooltip2 == null || tooltip2.isEmpty() )
    {
      return true;
    }
    return false;
  }

  private JPanel createIconLable()
  {
    final JPanel iconPanel = new JPanel( new GridBagLayout() );
    iconPanel.setOpaque( false );

    if ( marker != null )
    {
      final JPanel markerPanel = new JPanel();
      markerPanel.setPreferredSize( new Dimension( 5, icon.getIconHeight() ) );
      markerPanel.setBackground( background );
      markerPanels.add( markerPanel );

      final GridBagConstraints iconConstraints = new GridBagConstraints();
      iconConstraints.anchor = GridBagConstraints.WEST;
      //Indikator, ob der Button selektiert wurde
      iconPanel.add( markerPanel, iconConstraints );
    }

    final GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.gridx = 0;
    labelConstraints.gridy = 0;
    labelConstraints.anchor = GridBagConstraints.CENTER;

    //Das Icon
    final JLabel label = new JLabel( icon );
    label.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
    label.setEnabled( enabled );
    iconPanel.add( label, labelConstraints );
    iconLabels.add( label );

    return iconPanel;
  }

  private JLabel createTitleLable( final Color foreGround, final int deriveFontSizeBy, final String text )
  {
    final JLabel firstLabel = new JLabel( text );

    firstLabel.setEnabled( enabled );
    firstLabel.setVisible( true );
    firstLabel.setOpaque( false );
    firstLabel.setForeground( foreGround );
    firstLabel.setFont( firstLabel.getFont().deriveFont( (float) firstLabel.getFont().getSize() + deriveFontSizeBy ) );

    return firstLabel;
  }

  /**
   * @param mouseListener Melder einen MausListener an alle Ausprägungen des Buttons an.
   */
  public void addMouseListener( final MouseListener mouseListener )
  {
    barPanel.addMouseListener( mouseListener );
    menuPanel.addMouseListener( mouseListener );
  }

  /**
   * Setzt die Sichtbarkeit des Buttons.
   *
   * @param visible true, wenn Button sichtabr sein soll.
   */
  public void setVisible( final boolean visible )
  {
    barPanel.setVisible( visible );
    menuPanel.setVisible( visible );
  }


  /**
   * @return ob der Button enabled ist oder nicht.
   */
  public boolean isEnabled()
  {
    return enabled;
  }

  /**
   * Setzt den Enable-Status des Buttons.
   *
   * @param enabled true, wenn der Button aktiv sein soll.
   */
  public void setEnabled( final boolean enabled )
  {
    setComponentsEnabled( barPanel, enabled );
    setComponentsEnabled( menuPanel, enabled );
  }

  /**
   * Setzt den Title des Buttons
   * 
   * @param title Title
   */
  public void setTitle( String title )
  {
    this.title = title;
    titleLabels.forEach( label -> label.setText( title ) );
  }

  private void setComponentsEnabled( final Component comp, final boolean enabled )
  {
    this.enabled = enabled;
    comp.setEnabled( enabled );
    if ( comp instanceof Container )
    {
      for ( final Component child : ((Container) comp).getComponents() )
      {
        setComponentsEnabled( child, enabled );
      }

    }
  }

  /**
   * Ändert die Darstellung des Buttons, wenn der Button gedrückt wird.
   */
  public void showPressedStyle()
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      changeButtonStyle( pressedIcon, background.brighter().brighter().brighter(), Color.black );
    }
    else
    {
      changeButtonStyle( pressedIcon, background.darker(), Color.WHITE );
    }
  }

  /**
   * Ändert die Darstellung des Buttons, wenn der Button los gelassen wird.
   */
  public void hidePressedStyle()
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      changeButtonStyle( icon, background, Color.white );
    }
    else
    {
      changeButtonStyle( icon, background, foreground );
    }
  }

  private void changeButtonStyle( final Icon buttonIcon, final Color background, final Color foreground )
  {
    if ( buttonIcon != null )
    {
      iconLabels.forEach( lable -> lable.setIcon( buttonIcon ) );
    }
    barPanel.setBackground( background );
    menuPanel.setBackground( background );

    if ( !isMarked )
    {
      markerPanels.forEach( markerPanel -> markerPanel.setBackground( background ) );
    }

    titleLabels.forEach( label -> label.setForeground( foreground ) );
  }

  /**
   * Ändert den Visible-State des Textes des Buttons.
   *
   * @param visible true, wenn Texte angezeigt werden sollen.
   */
  public void setTitleVisible( final boolean visible )
  {
    titleLabels.forEach( label -> label.setVisible( visible ) );
  }

  /**
   * Setzt das Flag, das der Button selektiert ist. Beim Selektieren wird der Marker in einer anderen Farbe
   * angezeigt.
   */
  public void selected()
  {
    isMarked = true;
    markerPanels.forEach( markerPanel -> markerPanel.setBackground( UIManager.getColor( "AppColor" ) ) );
  }

  /**
   * Deselktiert den Button und der Marker bekommt die gleiche Farbe des Panels und ist somit nicht sichtbar.
   */
  public void unselected()
  {
    isMarked = false;
    markerPanels.forEach( markerPanel -> markerPanel.setBackground( background ) );
  }

  /**
   * Liefert den Button im sichtbaren Bereich der {@link MeMateActionBar Aktionsleiste}.
   *
   * @return Button im sichtbaren Bereich.
   */
  public JPanel getBarButton()
  {
    return barPanel;
  }

  /**
   * Liefert den Button aus dem Kontextmenü der {@link MeMateActionBar Aktionsleiste}.
   *
   * @return Button im Kontextmenü.
   */
  public JPanel getMenuButton()
  {
    return menuPanel;
  }

  /**
   * @return das Icon des Buttons.
   */
  public Icon getIcon()
  {
    return icon;
  }

  /**
   * Setzt das Icon für den Button.
   * 
   * @param icon Icon
   */
  public void setIcon( final Icon icon )
  {
    this.icon = icon;

    iconLabels.forEach( lable -> lable.setIcon( icon ) );
  }

  /**
   * @return das Icon, welches beim Drücken auf den Button angezeigt wird.
   */
  public Icon getPressedIcon()
  {
    return pressedIcon;
  }

  /**
   * Setzt das pressedIcon des Buttons.
   * 
   * @param pressedIcon pressedIcon
   */
  public void setPressedIcon( final Icon pressedIcon )
  {
    this.pressedIcon = pressedIcon;
  }

  /**
   * @return Title des Buttons.
   */
  public String getTitle()
  {
    return title;
  }

  /**
   * @return Tooltip des Buttons.
   */
  public String getTooltip()
  {
    return tooltip;
  }

  /**
   * Setzt den Tooltip für den Button.
   * 
   * @param tooltip Tooltip-text
   */
  public void setTooltip( final String tooltip )
  {
    this.tooltip = tooltip;

    barPanel.setToolTipText( tooltip );
    menuPanel.setToolTipText( tooltip );
  }

  /**
   * @return Hintergrundfarbe des Buttons.
   */
  public Color getBackground()
  {
    return background;
  }

  /**
   * Setzt die Hintergrundfarbe des Buttons.
   * 
   * @param background Hintergrundfarbe.
   */
  public void setBackground( final Color background )
  {
    this.background = background;

    barPanel.setBackground( background );
    menuPanel.setBackground( background );

    if ( !isMarked )
    {
      markerPanels.forEach( markerPanel -> markerPanel.setBackground( background ) );
    }
  }

  /**
   * @return Vordergrundfarbe des Buttons
   */
  public Color getForeground()
  {
    return foreground;
  }

  /**
   * Setzt die Vordergrundfarbe des Buttons.
   * 
   * @param foreground Vordergrundfarbe.
   */
  public void setForeground( final Color foreground )
  {
    this.foreground = foreground;

    titleLabels.forEach( title -> title.setForeground( foreground ) );
  }

  /**
   * @return die MarkerColor des Buttons.
   */
  public Color getMarker()
  {
    return marker;
  }

  /**
   * Setzt die MarkerColor des Buttons.
   * 
   * @param marker Farbe des Markers.
   */
  public void setMarker( Color marker )
  {
    this.marker = marker;
    if ( isMarked )
    {
      markerPanels.forEach( markerPanel -> markerPanel.setBackground( marker ) );
    }
  }


  /**
   * @return Runnable des Buttons.
   */
  public Runnable getRunnable()
  {
    return runnable;
  }

  /**
   * Setzt das Runnable des Buttons.
   * 
   * @param runnable Runnable.
   */
  public void setRunnable( final Runnable runnable )
  {
    this.runnable = runnable;
  }

  /**
   * Ändert die Schriftfarbe, abhängig von dem Dark- oder Daymode.
   */
  public void toggleFontColor()
  {
    if ( darkModeOn )
    {
      titleLabels.forEach( label -> label.setForeground( foreground ) );

      darkModeOn = false;
    }
    else
    {
      titleLabels.forEach( label -> label.setForeground( Color.white ) );
      darkModeOn = true;
    }
  }

  /**
   * Ändert die Farben des Buttonsm sollte sich der Dark/Daymode ändern.
   * 
   * @param background Hintergrundfarbe
   * @param foreground Vordergrundfarbe
   */
  public void toggleDarkMode( Color background, Color foreground )
  {
    changeButtonStyle( null, background, foreground );
  }

  /**
   * Setzt den Darkmodestate.
   * 
   * @param state State
   */
  public void setDarkModeState( boolean state )
  {
    darkModeOn = state;
  }
}