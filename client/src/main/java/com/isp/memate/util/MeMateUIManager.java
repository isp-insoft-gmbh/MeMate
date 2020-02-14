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

  private static boolean darkModeState = false;

  public static void installDefaults()
  {
    installNewKey( defaultKey, new DarkDayColor( new Color( 36, 43, 55 ), Color.WHITE ), new DarkDayColor( Color.WHITE, Color.BLACK ) );

  }

  public static void installNewKey( final String key, final DarkDayColor backgroundColors, final DarkDayColor foregroundColors )
  {
    keySet.add( key );
    backgroundMap.put( key, backgroundColors );
    foregroundMap.put( key, foregroundColors );
  }

  public static JPanel createJPanel()
  {
    return createJPanel( defaultKey );
  }

  public static JPanel createJPanel( final String key )
  {
    final JPanel panel = new JPanel();
    panelList.put( key, panel );
    return panel;
  }

  public static JLabel createJLabel()
  {
    return createJLabel( defaultKey );
  }

  public static JLabel createJLabel( final String key )
  {
    final JLabel label = new JLabel();
    labelList.put( key, label );
    return label;
  }

  public static JTextPane createTextPane()
  {
    final JTextPane textpane = new JTextPane();
    textPaneList.put( "default", textpane );
    return textpane;
  }

  public static JCheckBox createCheckbox()
  {
    final JCheckBox checkbox = new JCheckBox();
    checkBoxList.put( "default", checkbox );
    return checkbox;
  }

  @SuppressWarnings( "javadoc" )
  public static MeMateActionBarButton createButton( final String title, final String tooltip, final Color background,
                                                    final Color foreground,
                                                    final Runnable runnable, final String key )
  {
    MeMateActionBarButton button = new MeMateActionBarButton( title, tooltip, background, foreground, runnable );
    buttonList.put( key, button );
    return button;
  }

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
   * @param key
   * @param imageIcon
   * @param imageIcon2
   * @return
   */
  public static JButton createNormalButton( String key, ImageIcon imageIcon, ImageIcon imageIcon2 )
  {
    JButton button = createNormalButton( key );
    iconList.put( button, new DarkDayIcon( imageIcon, imageIcon2 ) );
    return button;
  }

  public static void showDayMode()
  {
    darkModeState = false;
    UIManager.put( "OptionPane.background", new Color( 240, 240, 240 ) );
    UIManager.put( "Panel.background", new Color( 240, 240, 240 ) );
    UIManager.put( "OptionPane.messageForeground", Color.black );
    UIManager.put( "Label.foreground", Color.black );
    setUISettings();
  }

  public static void showDarkMode()
  {
    darkModeState = true;
    UIManager.put( "OptionPane.background", new Color( 36, 43, 55 ) );
    UIManager.put( "Panel.background", new Color( 36, 43, 55 ) );
    UIManager.put( "OptionPane.messageForeground", Color.white );
    UIManager.put( "Label.foreground", Color.white );
    setUISettings();
  }

  public static void iniDayMode()
  {
    darkModeState = false;
    UIManager.put( "OptionPane.background", new Color( 240, 240, 240 ) );
    UIManager.put( "Panel.background", new Color( 240, 240, 240 ) );
    UIManager.put( "OptionPane.messageForeground", Color.black );
    UIManager.put( "Label.foreground", Color.black );
  }

  public static void iniDarkMode()
  {
    darkModeState = true;
    UIManager.put( "OptionPane.background", new Color( 36, 43, 55 ) );
    UIManager.put( "Panel.background", new Color( 36, 43, 55 ) );
    UIManager.put( "OptionPane.messageForeground", Color.white );
    UIManager.put( "Label.foreground", Color.white );
  }

  public static boolean getDarkModeState()
  {
    return darkModeState;
  }

  public static void registerPanel( final String key, JPanel panel )
  {
    panelList.put( key, panel );
  }


  public static void registerIconLabel( JLabel infoIconLabel, ImageIcon infoIcon, ImageIcon infoIconWhite )
  {
    labelList.put( defaultKey, infoIconLabel );
    panelIconList.put( infoIconLabel, new DarkDayIcon( infoIconWhite, infoIcon ) );
  }

  public static void registerTable( final String key, JTable table )
  {
    tableList.put( key, table );
  }

  public static void registerList( final String key, JList<?> list )
  {
    listList.put( key, list );
  }

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

  public static DarkDayColor getBackground( String key )
  {
    return backgroundMap.get( key );
  }

  public static DarkDayColor getForeground( String key )
  {
    return foregroundMap.get( key );
  }

  /**
   *
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


  public static class DarkDayColor
  {
    private final Color darkColor;
    private final Color dayColor;

    /**
     *
     */
    public DarkDayColor( final Color darkColor, final Color dayColor )
    {
      this.darkColor = darkColor;
      this.dayColor = dayColor;
    }

    public Color getDayColor()
    {
      return dayColor;
    }

    public Color getDarkColor()
    {
      return darkColor;
    }
  }

  public static class DarkDayIcon
  {
    private final ImageIcon darkIcon;
    private final ImageIcon dayIcon;

    /**
     *
     */
    public DarkDayIcon( final ImageIcon darkIcon, final ImageIcon dayIcon )
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