/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;


import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;

import org.jfree.chart.JFreeChart;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.isp.memate.Login;
import com.isp.memate.actionbar.MeMateActionBarButton;
import com.isp.memate.actionbar.MeMateActionBarListener;


/**
 * Der {@link MeMateUIManager} enthält Listen von allen Komponenten den App und kann somit global
 * den Darkmode togglen.
 * 
 * @author nwe
 * @since 04.02.2020
 *
 */
public class MeMateUIManager
{
  private static final Multimap<String, JLabel>                labelList        = ArrayListMultimap.create();
  private static final Multimap<String, JPanel>                panelList        = ArrayListMultimap.create();
  private static final Multimap<String, MeMateActionBarButton> buttonList       = ArrayListMultimap.create();
  private static final Multimap<String, JButton>               normalButtonList = ArrayListMultimap.create();
  private static final Multimap<String, JComponent>            separatorList    = ArrayListMultimap.create();
  private static final Multimap<String, JTable>                tableList        = ArrayListMultimap.create();
  private static final Multimap<String, JScrollPane>           scrollPaneList   = ArrayListMultimap.create();
  private static final Multimap<String, JTextPane>             textPaneList     = ArrayListMultimap.create();
  private static final Multimap<String, JCheckBox>             checkBoxList     = ArrayListMultimap.create();
  private static final Multimap<String, JList<?>>              listList         = ArrayListMultimap.create();
  private static final Map<JButton, DarkDayIcon>               iconList         = new HashMap<>();
  private static final Map<JLabel, DarkDayIcon>                panelIconList    = new HashMap<>();
  private static final Map<String, DarkDayColor>               backgroundMap    = new HashMap<>();
  private static final Map<String, DarkDayColor>               foregroundMap    = new HashMap<>();
  private static final String                                  defaultKey       = "default";
  private static final Set<String>                             keySet           = new HashSet<>();
  private static JFreeChart                                    freeChart        = null;
  private static JFreeChart                                    lineChart        = null;

  private static boolean darkModeState = false;

  /**
   * Install Default Key with default colors.
   */
  public static void installDefaults()
  {
    installNewKey( defaultKey, new DarkDayColor( new Color( 36, 43, 55 ), Color.WHITE ), new DarkDayColor( Color.WHITE, Color.BLACK ) );
  }

  /**
   * Install a new custom Key with custom colors.
   * 
   * @param key keyname
   * @param backgroundColors BG-Colors
   * @param foregroundColors FG-Colors
   */
  public static void installNewKey( final String key, final DarkDayColor backgroundColors, final DarkDayColor foregroundColors )
  {
    keySet.add( key );
    backgroundMap.put( key, backgroundColors );
    foregroundMap.put( key, foregroundColors );
  }

  /**
   * Lädt den Darkmode
   */
  public static void showDarkMode()
  {
    darkModeState = true;
    UIManager.put( "OptionPane.background", new Color( 36, 43, 55 ) );
    UIManager.put( "Panel.background", new Color( 36, 43, 55 ) );
    UIManager.put( "OptionPane.messageForeground", Color.white );
    UIManager.put( "Label.foreground", Color.white );
    UIManager.put( "ToolTip.background", new Color( 72, 87, 111 ) );
    UIManager.put( "ToolTip.foreground", Color.white );
    setUISettings();
  }

  /**
   * Lädt den Daymode
   */
  public static void showDayMode()
  {
    darkModeState = false;
    UIManager.put( "OptionPane.background", new Color( 240, 240, 240 ) );
    UIManager.put( "Panel.background", new Color( 240, 240, 240 ) );
    UIManager.put( "OptionPane.messageForeground", Color.black );
    UIManager.put( "Label.foreground", Color.black );
    UIManager.put( "ToolTip.background", new Color( 255, 255, 225 ) );
    UIManager.put( "ToolTip.foreground", new Color( 0, 0, 0 ) );
    setUISettings();
  }

  /**
   * Wird beim Auslesen der Userconfig geladen oder nicht
   */
  public static void iniDayMode()
  {
    darkModeState = false;
    UIManager.put( "OptionPane.background", new Color( 240, 240, 240 ) );
    UIManager.put( "Panel.background", new Color( 240, 240, 240 ) );
    UIManager.put( "OptionPane.messageForeground", Color.black );
    UIManager.put( "Label.foreground", Color.black );
    UIManager.put( "ToolTip.background", new Color( 255, 255, 225 ) );
    UIManager.put( "ToolTip.foreground", new Color( 0, 0, 0 ) );
  }

  /**
   * Wird beim Auslesen der Userconfig geladen oder nicht
   */
  public static void iniDarkMode()
  {
    darkModeState = true;
    UIManager.put( "OptionPane.background", new Color( 36, 43, 55 ) );
    UIManager.put( "Panel.background", new Color( 36, 43, 55 ) );
    UIManager.put( "OptionPane.messageForeground", Color.white );
    UIManager.put( "Label.foreground", Color.white );
    UIManager.put( "ToolTip.background", new Color( 72, 87, 111 ) );
    UIManager.put( "ToolTip.foreground", Color.white );
  }


  /**
   * Erstellt ein {@link JPanel} mit defaultKey
   * 
   * @return {@link JPanel}
   */
  public static JPanel createJPanel()
  {
    return createJPanel( defaultKey );
  }

  /**
   * Erstellt ein {@link JPanel}, welches der panelList hinzugefügt wird.
   * 
   * @param key key
   * @return {@link JPanel}
   */
  public static JPanel createJPanel( final String key )
  {
    final JPanel panel = new JPanel();
    panelList.put( key, panel );
    return panel;
  }

  /**
   * Erstellt ein {@link JLabel} mit defaultKey
   * 
   * @return {@link JLabel}
   */
  public static JLabel createJLabel()
  {
    return createJLabel( defaultKey );
  }

  /**
   * Erstellt ein {@link JLabel}, welches der labelList hinzugefügt wird.
   * 
   * @param key key
   * @return {@link JLabel}
   */
  public static JLabel createJLabel( final String key )
  {
    final JLabel label = new JLabel();
    labelList.put( key, label );
    return label;
  }

  /**
   * Erstellt ein {@link JTextPane}, welches der textPaneList hinzugefügt wird.
   * 
   * @return {@link JTextPane}
   */
  public static JTextPane createTextPane()
  {
    final JTextPane textpane = new JTextPane();
    textPaneList.put( "default", textpane );
    return textpane;
  }

  /**
   * Erstellt eine {@link JCheckBox}, welche der checkBoxList hinzugefügt wird.
   * 
   * @return {@link JTextPane}
   */
  public static JCheckBox createCheckbox()
  {
    final JCheckBox checkbox = new JCheckBox();
    checkBoxList.put( "default", checkbox );
    return checkbox;
  }

  /**
   * Erstellt einen {@link JButton}, welcher der normalButtonList hinzugefügt wird.
   * 
   * @param key key
   * @return {@link JButton}
   */
  public static JButton createNormalButton( String key )
  {
    JButton button = new JButton();
    button.setContentAreaFilled( false );
    button.setOpaque( true );
    button.setBorder(
        BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color( 41, 48, 60 ).brighter() ),
            BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );
    normalButtonList.put( key, button );
    return button;
  }

  /**
   * Erstellt einen {@link JButton} mit Icon, welcher der iconList hinzugefügt wird.
   * 
   * @param key key
   * @param imageIcon Daymode Icon
   * @param imageIcon2 Darkmode Icon
   * @return {@link JButton}
   */
  public static JButton createNormalButton( String key, ImageIcon imageIcon, ImageIcon imageIcon2 )
  {
    JButton button = createNormalButton( key );
    iconList.put( button, new DarkDayIcon( imageIcon, imageIcon2 ) );
    return button;
  }


  @SuppressWarnings( "javadoc" )
  public static void registerPanel( final String key, JPanel panel )
  {
    panelList.put( key, panel );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerIconLabel( JLabel infoIconLabel, ImageIcon infoIcon, ImageIcon infoIconWhite )
  {
    labelList.put( defaultKey, infoIconLabel );
    panelIconList.put( infoIconLabel, new DarkDayIcon( infoIconWhite, infoIcon ) );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerTable( final String key, JTable table )
  {
    tableList.put( key, table );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerList( final String key, JList<?> list )
  {
    listList.put( key, list );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerScrollPane( final String key, JScrollPane scrollPane )
  {
    scrollPaneList.put( key, scrollPane );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerSeparator( final JComponent separator, final String key )
  {
    separatorList.put( key, separator );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerlabel( final JLabel label )
  {
    labelList.put( defaultKey, label );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerFreeChart( JFreeChart freeChart )
  {
    MeMateUIManager.freeChart = freeChart;
  }

  @SuppressWarnings( "javadoc" )
  public static void registerLineChart( JFreeChart lineChart )
  {
    MeMateUIManager.lineChart = lineChart;
  }


  /**
   * Gibt die Hintergrundfarben des gegebenen Keys an.
   * 
   * @param key Key
   * @return Hintergrundfarben
   */
  public static DarkDayColor getBackground( String key )
  {
    return backgroundMap.get( key );
  }

  /**
   * Gibt die Vordergrundfarben des gegebenen Keys an.
   * 
   * @param key Key
   * @return Vordergrundfarben
   */
  public static DarkDayColor getForeground( String key )
  {
    return foregroundMap.get( key );
  }

  /**
   * @return den Status ob der Darkmode an oder aus ist.
   */
  public static boolean getDarkModeState()
  {
    return darkModeState;
  }


  /**
   * Wendet die derzeitigen UI-Settings an allen registrierten Komponenten an.
   */
  public static void setUISettings()
  {
    ClientLog.newLog( "UI-Update" );
    for ( final String key : keySet )
    {
      for ( final JPanel panel : panelList.get( key ) )
      {
        if ( darkModeState )
        {

          panel.setBackground( backgroundMap.get( key ).getDarkColor() );
        }
        else
        {
          panel.setBackground( backgroundMap.get( key ).getDayColor() );
        }
      }
      for ( final JList<?> list : listList.get( key ) )
      {
        if ( darkModeState )
        {
          list.setBackground( getBackground( key ).getDarkColor() );
        }
        else
        {
          list.setBackground( getBackground( key ).getDayColor() );
        }
      }
      for ( final JLabel label : labelList.get( key ) )
      {
        if ( darkModeState )
        {
          label.setForeground( foregroundMap.get( key ).getDarkColor() );
          if ( panelIconList.get( label ) != null )
          {
            label.setIcon( panelIconList.get( label ).getDarkIcon() );
          }
        }
        else
        {
          label.setForeground( foregroundMap.get( key ).getDayColor() );
          if ( panelIconList.get( label ) != null )
          {
            label.setIcon( panelIconList.get( label ).getDayIcon() );
          }
        }
      }
      for ( final JButton button : normalButtonList.get( key ) )
      {
        if ( darkModeState )
        {
          button.setBackground( backgroundMap.get( key ).getDarkColor() );
          button.setForeground( foregroundMap.get( key ).getDarkColor() );
          button.setBorder(
              BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color( 50, 70, 70 ) ),
                  BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );
          if ( iconList.get( button ) != null )
          {
            button.setIcon( iconList.get( button ).getDarkIcon() );
          }
        }
        else
        {
          button.setBackground( backgroundMap.get( key ).getDayColor() );
          button.setForeground( foregroundMap.get( key ).getDayColor() );
          button.setBorder(
              BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color( 230, 230, 230 ) ),
                  BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );
          if ( iconList.get( button ) != null )
          {
            button.setIcon( iconList.get( button ).getDayIcon() );
          }
        }
      }
      for ( final JTextPane textPane : textPaneList.get( key ) )
      {
        if ( darkModeState )
        {
          textPane.setBackground( backgroundMap.get( key ).getDarkColor() );
        }
        else
        {
          textPane.setBackground( backgroundMap.get( key ).getDayColor() );
        }
      }
      for ( final JCheckBox checkBox : checkBoxList.get( key ) )
      {
        if ( darkModeState )
        {
          checkBox.setBackground( backgroundMap.get( key ).getDarkColor() );
        }
        else
        {
          checkBox.setBackground( backgroundMap.get( key ).getDayColor() );
        }
      }
      for ( final JTable table : tableList.get( key ) )
      {
        if ( darkModeState )
        {
          table.setBackground( backgroundMap.get( key ).getDarkColor() );
          JTableHeader header = table.getTableHeader();
          header.setBackground( backgroundMap.get( key ).getDarkColor().brighter().brighter() );
          header.setForeground( foregroundMap.get( key ).getDarkColor() );
          table.setForeground( foregroundMap.get( key ).getDarkColor() );
        }
        else
        {
          table.setBackground( backgroundMap.get( key ).getDayColor() );
          JTableHeader header = table.getTableHeader();
          header.setBackground( backgroundMap.get( key ).getDayColor().darker() );
          header.setForeground( foregroundMap.get( key ).getDayColor() );
          table.setForeground( foregroundMap.get( key ).getDayColor() );
        }
      }
      for ( JScrollPane scrollPane : scrollPaneList.get( key ) )
      {
        if ( darkModeState )
        {
          scrollPane.setBackground( backgroundMap.get( key ).getDarkColor().brighter() );
          scrollPane.getVerticalScrollBar().setUI( new DarkScrollBarUI() );
          scrollPane.getViewport().setBackground( backgroundMap.get( key ).getDarkColor() );
        }
        else
        {
          scrollPane.setBackground( backgroundMap.get( key ).getDayColor().darker() );
          scrollPane.getVerticalScrollBar().setUI( new BrightScrollBarUI() );
          scrollPane.getViewport().setBackground( backgroundMap.get( key ).getDayColor() );
        }
      }
      for ( final JComponent separator : separatorList.get( key ) )
      {
        if ( darkModeState )
        {
          separator.setForeground( foregroundMap.get( key ).getDarkColor() );
        }
        else
        {
          separator.setForeground( foregroundMap.get( key ).getDayColor() );
        }
      }
      for ( final MeMateActionBarButton button : buttonList.get( key ) )
      {
        if ( darkModeState )
        {
          button.toggleDarkMode( backgroundMap.get( key ).getDarkColor(), foregroundMap.get( key ).getDarkColor() );
          button.setDarkModeState( true );
          button.addMouseListener( new MeMateActionBarListener( button, new Runnable()
          {

            @Override
            public void run()
            {
              button.setBackground( backgroundMap.get( key ).getDarkColor() );
            }
          }, new Runnable()
          {

            @Override
            public void run()
            {
              button.setBackground( backgroundMap.get( key ).getDarkColor().darker() );
            }
          } ) );
        }
        else
        {
          button.toggleDarkMode( backgroundMap.get( key ).getDayColor(), foregroundMap.get( key ).getDayColor() );
          button.setDarkModeState( false );
          button.addMouseListener( new MeMateActionBarListener( button, new Runnable()
          {

            @Override
            public void run()
            {
              button.setBackground( backgroundMap.get( key ).getDayColor() );
            }
          }, new Runnable()
          {

            @Override
            public void run()
            {
              button.setBackground( backgroundMap.get( key ).getDayColor().darker() );
            }
          } ) );
        }
      }
    }
    if ( freeChart != null )
    {
      if ( darkModeState )
      {
        freeChart.setBackgroundPaint( MeMateUIManager.getBackground( "default" ).getDarkColor() );
        freeChart.getTitle().setPaint( Color.white );
        freeChart.getXYPlot().setBackgroundPaint( new Color( 36, 43, 55 ).brighter() );
        freeChart.getXYPlot().setDomainGridlinesVisible( false );
        freeChart.getXYPlot().setRangeGridlinesVisible( false );
        freeChart.getXYPlot().getDomainAxis().setTickLabelPaint( Color.white );
        freeChart.getXYPlot().getRangeAxis().setTickLabelPaint( Color.white );
        freeChart.getXYPlot().getDomainAxis().setLabelPaint( Color.white );
        freeChart.getXYPlot().getRangeAxis().setLabelPaint( Color.white );
      }
      else
      {
        freeChart.setBackgroundPaint( MeMateUIManager.getBackground( "default" ).getDayColor() );
        freeChart.getXYPlot().setBackgroundPaint( new Color( 192, 192, 192 ) );
        freeChart.getXYPlot().setDomainGridlinesVisible( false );
        freeChart.getXYPlot().setRangeGridlinesVisible( false );
        freeChart.getTitle().setPaint( Color.black );
        freeChart.getXYPlot().getDomainAxis().setTickLabelPaint( Color.black );
        freeChart.getXYPlot().getRangeAxis().setTickLabelPaint( Color.black );
        freeChart.getXYPlot().getDomainAxis().setLabelPaint( Color.black );
        freeChart.getXYPlot().getRangeAxis().setLabelPaint( Color.black );
      }
    }
    if ( lineChart != null )
    {
      if ( darkModeState )
      {
        lineChart.setBackgroundPaint( MeMateUIManager.getBackground( "default" ).getDarkColor() );
        lineChart.getCategoryPlot().setBackgroundPaint( new Color( 36, 43, 55 ).brighter() );
        lineChart.getCategoryPlot().setDomainGridlinesVisible( false );
        lineChart.getCategoryPlot().setRangeGridlinesVisible( false );
        lineChart.getTitle().setPaint( Color.white );
        lineChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( Color.white );
        lineChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( Color.white );
        lineChart.getCategoryPlot().getDomainAxis().setLabelPaint( Color.white );
        lineChart.getCategoryPlot().getRangeAxis().setLabelPaint( Color.white );
      }
      else
      {
        lineChart.setBackgroundPaint( MeMateUIManager.getBackground( "default" ).getDayColor() );
        lineChart.getCategoryPlot().setBackgroundPaint( new Color( 192, 192, 192 ) );
        lineChart.getCategoryPlot().setDomainGridlinesVisible( false );
        lineChart.getCategoryPlot().setRangeGridlinesVisible( false );
        lineChart.getTitle().setPaint( Color.black );
        lineChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( Color.black );
        lineChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( Color.black );
        lineChart.getCategoryPlot().getDomainAxis().setLabelPaint( Color.black );
        lineChart.getCategoryPlot().getRangeAxis().setLabelPaint( Color.black );
      }
    }
    if ( Login.getInstance() != null )
    {
      if ( darkModeState )
      {
        Login.getInstance().showDarkHeader();
      }
      else
      {
        Login.getInstance().showDayHeader();
      }
    }

  }


  /**
   * Enthält immer eine Farbe für den Darkmode und eine für den Daymode.
   * 
   * @author nwe
   * @since 02.03.2020
   *
   */
  public static class DarkDayColor
  {
    private final Color darkColor;
    private final Color dayColor;


    /**
     * Setzen der Farben
     * 
     * @param darkColor Darkmode Farbe
     * @param dayColor Daymode Farbe
     */
    public DarkDayColor( final Color darkColor, final Color dayColor )
    {
      this.darkColor = darkColor;
      this.dayColor = dayColor;
    }

    /**
     * @return Daymode Farbe
     */
    public Color getDayColor()
    {
      return dayColor;
    }

    /**
     * @return Darkmode Farbe
     */
    public Color getDarkColor()
    {
      return darkColor;
    }
  }

  private static class DarkDayIcon
  {
    private final ImageIcon darkIcon;
    private final ImageIcon dayIcon;


    private DarkDayIcon( final ImageIcon darkIcon, final ImageIcon dayIcon )
    {
      this.darkIcon = darkIcon;
      this.dayIcon = dayIcon;
    }

    public ImageIcon getDayIcon()
    {
      return dayIcon;
    }

    public ImageIcon getDarkIcon()
    {
      return darkIcon;
    }
  }
}