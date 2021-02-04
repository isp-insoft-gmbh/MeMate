/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.isp.memate.DrinkDetailsToolTip;

/**
 * Der {@link MeMateUIManager} enthält Listen von allen Komponenten den App und
 * kann somit global den Darkmode togglen.
 *
 * @author nwe
 * @since 04.02.2020
 *
 */
public class MeMateUIManager
{
  static ClassLoader classLoader = MeMateUIManager.class.getClassLoader();

  private static boolean darkModeState = false;

  public static void setDarkModeState( boolean darkModeState )
  {
    MeMateUIManager.darkModeState = darkModeState;
  }

  public static void putIconsInUIManager()
  {
    UIManager.put( "dashboard.icon.black", getIcon( "dashboard_black.png" ) );
    UIManager.put( "dashboard.icon.white", getIcon( "dashboard_white.png" ) );
    UIManager.put( "adminview.icon.black", getIcon( "adminview_black.png" ) );
    UIManager.put( "adminview.icon.white", getIcon( "adminview_white.png" ) );
    UIManager.put( "logout.icon.black", getIcon( "logout_black_24.png" ) );
    UIManager.put( "logout.icon.white", getIcon( "logout_white_24.png" ) );
    UIManager.put( "history.icon.black", getIcon( "history_black.png" ) );
    UIManager.put( "history.icon.white", getIcon( "history_white.png" ) );
    UIManager.put( "social.icon.black", getIcon( "social_black.png" ) );
    UIManager.put( "social.icon.white", getIcon( "social_white.png" ) );
    UIManager.put( "undo.icon.black", getIcon( "back_black.png" ) );
    UIManager.put( "undo.icon.white", getIcon( "back_white.png" ) );
    UIManager.put( "drinkmanager.icon.black", getIcon( "drinkmanager_black.png" ) );
    UIManager.put( "drinkmanager.icon.white", getIcon( "drinkmanager_white.png" ) );
    UIManager.put( "consumption.icon.black", getIcon( "consumption_black.png" ) );
    UIManager.put( "consumption.icon.white", getIcon( "consumption_white.png" ) );
    UIManager.put( "creditHistory.icon.black", getIcon( "creditHistory_black.png" ) );
    UIManager.put( "creditHistory.icon.white", getIcon( "creditHistory_white.png" ) );
    UIManager.put( "info.icon.black", getIcon( "infoicon.png" ) );
    UIManager.put( "info.icon.white", getIcon( "infoicon_white.png" ) );
  }

  private static Icon getIcon( String string )
  {
    return new ImageIcon( classLoader.getResource( string ) );
  }

  public static void showDarkMode()
  {
    darkModeState = true;
    FlatDarkLaf.install();
    FlatLaf.updateUI();
  }

  public static void showDayMode()
  {
    darkModeState = false;
    FlatLightLaf.install();
    FlatLaf.updateUI();
  }

  public static JPanel createJPanelWithThinBorder()
  {
    final JPanel panel = new JPanel();
    panel.setBorder( BorderFactory.createLineBorder( UIManager.getColor( "Button.borderColor" ) ) );
    return panel;
  }

  public static JPanel createJPanelWithToolTipBackground()
  {
    final JPanel panel = new JPanel();
    panel.setBackground( UIManager.getColor( "ToolTip.background" ) );
    return panel;
  }

  public static JButton createInfoButton( String drinkName )
  {
    JButton button = new JButton( new InfoIcon() )
    {
      JToolTip tooltip;

      @Override
      public JToolTip createToolTip()
      {
        if ( tooltip == null )
        {
          tooltip = new DrinkDetailsToolTip( drinkName );
        }
        return tooltip;
      }

      @Override
      public void updateUI()
      {
        super.updateUI();
        setIcon( new InfoIcon() );
      }
    };
    button.setToolTipText( "" );
    button.setContentAreaFilled( false );
    button.setOpaque( false );
    button.setFocusable( false );
    button.setBorder( BorderFactory.createEmptyBorder() );
    final int defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
    button.addMouseListener( new MouseAdapter()
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
    return button;
  }

  /**
   * Erstellt einen IconButton mit 2 verschiedenen Images.
   *
   * @param key key
   * @param imageIcon Daymode Icon
   * @param imageIcon2 Darkmode Icon
   * @return {@link JButton}
   */
  public static JButton createIconButton( final ImageIcon imageIcon, final ImageIcon imageIcon2 )
  {
    final JButton button = new JButton()
    {
      @Override
      public void updateUI()
      {
        super.updateUI();
        if ( darkModeState )
        {
          super.setIcon( imageIcon );
        }
        else
          super.setIcon( imageIcon2 );
      }
    };
    return button;
  }

  /**
   * @return den Status ob der Darkmode an oder aus ist.
   */
  public static boolean getDarkModeState()
  {
    return darkModeState;
  }

  //    updateGraphs();


  //  public static void updateGraphs()
  //  {
  //    if ( freeChart != null )
  //    {
  //      freeChart.setBackgroundPaint( darkModeState ? MeMateUIManager.getBackground( "default" ).getDarkColor()
  //          : MeMateUIManager.getBackground( "default" ).getDayColor() );
  //      freeChart.getTitle().setPaint( darkModeState ? Color.white : Color.black );
  //      freeChart.getXYPlot()
  //          .setBackgroundPaint( darkModeState ? UIManager.getColor( "Panel.badkground" ).brighter() : new Color( 192, 192, 192 ) );
  //      freeChart.getXYPlot().getDomainAxis().setTickLabelPaint( darkModeState ? Color.white : Color.black );
  //      freeChart.getXYPlot().getRangeAxis().setTickLabelPaint( darkModeState ? Color.white : Color.black );
  //      freeChart.getXYPlot().getDomainAxis().setLabelPaint( darkModeState ? Color.white : Color.black );
  //      freeChart.getXYPlot().getRangeAxis().setLabelPaint( darkModeState ? Color.white : Color.black );
  //      freeChart.getXYPlot().setDomainGridlinesVisible( false );
  //      freeChart.getXYPlot().setRangeGridlinesVisible( false );
  //    }
  //    if ( lineChart != null )
  //    {
  //      lineChart.setBackgroundPaint( darkModeState ? MeMateUIManager.getBackground( "default" ).getDarkColor()
  //          : MeMateUIManager.getBackground( "default" ).getDayColor() );
  //      lineChart.getCategoryPlot()
  //          .setBackgroundPaint( darkModeState ? UIManager.getColor( "Panel.background" ).brighter() : new Color( 192, 192, 192 ) );
  //      lineChart.getTitle().setPaint( darkModeState ? Color.white : Color.black );
  //      lineChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( darkModeState ? Color.white : Color.black );
  //      lineChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( darkModeState ? Color.white : Color.black );
  //      lineChart.getCategoryPlot().getDomainAxis().setLabelPaint( darkModeState ? Color.white : Color.black );
  //      lineChart.getCategoryPlot().getRangeAxis().setLabelPaint( darkModeState ? Color.white : Color.black );
  //      lineChart.getCategoryPlot().setDomainGridlinesVisible( false );
  //      lineChart.getCategoryPlot().setRangeGridlinesVisible( false );
  //    }
  //  }


  public static void applyTheme()
  {
    //TODO(nwe | 09.12.2020): Was für on und off digga ? mach doch einfach true false
    String darkmode = PropertyHelper.getProperty( "Darkmode" );
    if ( darkmode != null && darkmode.equals( "on" ) )
    {
      FlatDarkLaf.install();
      MeMateUIManager.setDarkModeState( true );
    }
    else
    {
      FlatLightLaf.install();
      MeMateUIManager.setDarkModeState( false );
    }
  }

  public static void setUIDefaults()
  {
    Color mainColor = UIManager.getColor( "AppColor" );

    //FIXME sobald FlatLaf komplett implemetiert ist, dann als focusFarbe AppColor setzte. AppColor muss also noch voher gesetzt werden.
    //    UIManager.put( "CheckBox.icon.focusedBorderColor", mainColor );
    //    UIManager.put( "CheckBox.icon.selectedFocusedBorderColor", mainColor );
    //    UIManager.put( "Component.focusedBorderColor", mainColor );
    //    UIManager.put( "Table.selectionBackground", mainColor );
    //    UIManager.put( "Button.default.focusedBorderColor", mainColor.brighter() );
    //    UIManager.put( "Button.default.background", mainColor.darker() );
    //    UIManager.put( "Button.default.borderColor", mainColor );
    UIManager.put( "ScrollBar.thumbArc", 999 );
    UIManager.put( "ScrollBar.thumbInsets", new Insets( 2, 2, 2, 2 ) );
    UIManager.put( "ScrollBar.width", 12 );
    ToolTipManager.sharedInstance().setDismissDelay( 1000000 );
  }

  /**
   * Die Userconfig wird gelesen und das richtige Colortheme geladen.
   */
  public static void installColors()
  {
    String colorScheme = PropertyHelper.getProperty( "colorScheme" );
    if ( colorScheme == null )
    {
      colorScheme = "";
    }
    switch ( colorScheme )
    {
      case "Dark Blue":
        putColorsInUIManager( new Color( 0, 173, 181 ), new Color( 42, 51, 64 ) );
        break;
      case "Red":
        putColorsInUIManager( new Color( 226, 62, 87 ), new Color( 57, 67, 77 ) );
        break;
      case "Green":
        putColorsInUIManager( new Color( 153, 180, 51 ), new Color( 57, 67, 77 ) );
        break;
      case "Blue":
        putColorsInUIManager( new Color( 85, 172, 238 ), new Color( 49, 56, 60 ) );
        break;
      case "Orange":
        putColorsInUIManager( new Color( 227, 162, 26 ), new Color( 49, 56, 60 ) );
        break;
      case "Coral":
        putColorsInUIManager( new Color( 255, 111, 97 ), new Color( 49, 56, 60 ) );
        break;
      case "Tripple Green":
        putColorsInUIManager( new Color( 153, 180, 51 ), new Color( 13, 48, 30 ) );
        break;
      default :
        putColorsInUIManager( new Color( 29, 164, 165 ), new Color( 42, 51, 64 ) );
        break;
    }
  }

  private static void putColorsInUIManager( final Color appColor, final Color actionbar )
  {
    UIManager.put( "AppColor", appColor );
    UIManager.put( "App.Actionbar", actionbar );
  }

  public static void init()
  {
    installColors();
    putIconsInUIManager();
    applyTheme();
    setUIDefaults();
    FlatUIDefaultsInspector.install( "X" );
  }
}