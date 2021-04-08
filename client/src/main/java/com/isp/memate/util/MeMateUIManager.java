/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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

import com.isp.memate.components.DrinkDetailsToolTip;
import com.isp.memate.components.InfoIcon;

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
    UIManager.put( "settings.icon.black", getIcon( "settings_black.png" ) );
    UIManager.put( "settings.icon.white", getIcon( "settings_white.png" ) );
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

    List<Image> frameIcons = new ArrayList<>();
    frameIcons.add( Toolkit.getDefaultToolkit().getImage( classLoader.getResource( "frameicon128.png" ) ) );
    frameIcons.add( Toolkit.getDefaultToolkit().getImage( classLoader.getResource( "frameicon64.png" ) ) );
    frameIcons.add( Toolkit.getDefaultToolkit().getImage( classLoader.getResource( "frameicon32.png" ) ) );
    frameIcons.add( Toolkit.getDefaultToolkit().getImage( classLoader.getResource( "frameicon16.png" ) ) );
    UIManager.put( "frame.icons", frameIcons );
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

  public static JButton createInfoButton( int drinkID )
  {
    JButton button = new JButton( new InfoIcon() )
    {
      JToolTip tooltip;

      @Override
      public JToolTip createToolTip()
      {
        if ( tooltip == null )
        {
          tooltip = new DrinkDetailsToolTip( drinkID );
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

  public static void applyTheme()
  {
    if ( PropertyHelper.getDarkModeProperty() )
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

  public static void init()
  {
    UIManager.put( "AppColor", PropertyHelper.getAppColorProperty() );
    putIconsInUIManager();
    applyTheme();
    setUIDefaults();
    FlatUIDefaultsInspector.install( "X" );
  }
}