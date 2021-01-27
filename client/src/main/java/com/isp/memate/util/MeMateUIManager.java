/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.JTableHeader;

import org.jfree.chart.JFreeChart;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatUIDefaultsInspector;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import com.isp.memate.DrinkDetailsToolTip;
import com.isp.memate.actionbar.MeMateActionBarButton;
import com.isp.memate.actionbar.MeMateActionBarListener;

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
  private static final Multimap<String, JLabel>                labelList         = ArrayListMultimap.create();
  private static final Multimap<String, JPanel>                panelList         = ArrayListMultimap.create();
  private static final Multimap<String, MeMateActionBarButton> buttonList        = ArrayListMultimap.create();
  private static final Multimap<String, JButton>               normalButtonList  = ArrayListMultimap.create();
  private static final Multimap<String, JButton>               infoButtonList    = ArrayListMultimap.create();
  private static final Multimap<String, JComponent>            separatorList     = ArrayListMultimap.create();
  private static final Multimap<String, JTable>                tableList         = ArrayListMultimap.create();
  private static final Multimap<String, JScrollPane>           scrollPaneList    = ArrayListMultimap.create();
  private static final Multimap<String, JTextPane>             textPaneList      = ArrayListMultimap.create();
  private static final Multimap<String, JCheckBox>             checkBoxList      = ArrayListMultimap.create();
  private static final Multimap<String, JList<?>>              listList          = ArrayListMultimap.create();
  private static final Multimap<String, JSpinner>              spinnerList       = ArrayListMultimap.create();
  private static final Multimap<String, JRadioButton>          radioButtonList   = ArrayListMultimap.create();
  private static final Multimap<String, JComboBox<String>>     comboBoxList      = ArrayListMultimap.create();
  private static final Multimap<String, JTextField>            textFieldList     = ArrayListMultimap.create();
  private static final Multimap<String, JPasswordField>        passwordFieldList = ArrayListMultimap.create();
  private static final Map<JButton, DarkDayIcon>               iconList          = new HashMap<>();
  private static final Map<JLabel, DarkDayIcon>                panelIconList     = new HashMap<>();
  private static final Map<String, DarkDayColor>               backgroundMap     = new HashMap<>();
  private static final Map<String, DarkDayColor>               foregroundMap     = new HashMap<>();
  private static final String                                  defaultKey        = "default";
  private static final Set<String>                             keySet            = new HashSet<>();
  private static JFreeChart                                    freeChart         = null;
  private static JFreeChart                                    lineChart         = null;
  static ClassLoader                                           classLoader       = MeMateUIManager.class.getClassLoader();

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
  }

  private static Icon getIcon( String string )
  {
    return new ImageIcon( classLoader.getResource( string ) );
  }

  /**
   * Install Default Key with default colors.
   */
  public static void installDefaults()
  {
    installNewKey( defaultKey, new DarkDayColor( UIManager.getColor( "App.Background" ), Color.WHITE ),
        new DarkDayColor( Color.WHITE, Color.BLACK ) );
    MeMateUIManager.installNewKey( "button",
        new DarkDayColor( UIManager.getColor( "App.Background" ).brighter(), new Color( 215, 215, 215 ) ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "drinkButtons",
        new DarkDayColor( UIManager.getColor( "App.Background" ).brighter(), new Color( 236, 240, 241 ) ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "drinkButton",
        new DarkDayColor( UIManager.getColor( "App.Background" ).brighter(), new Color( 236, 240, 241 ) ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "table", new DarkDayColor( UIManager.getColor( "App.Background" ), Color.white ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "scroll", new DarkDayColor( UIManager.getColor( "App.Background" ), Color.white ),
        new DarkDayColor( Color.white, Color.black ) );
    MeMateUIManager.installNewKey( "adminButton",
        new DarkDayColor( UIManager.getColor( "App.Secondary.Background" ), new Color( 236, 240, 241 ) ),
        new DarkDayColor( Color.WHITE, Color.BLACK ) );
    MeMateUIManager.installNewKey( "spinner",
        new DarkDayColor( UIManager.getColor( "App.Background" ).brighter().brighter(), new Color( 236, 240, 241 ) ),
        new DarkDayColor( Color.WHITE, Color.BLACK ) );
    MeMateUIManager.installNewKey( "comboBox",
        new DarkDayColor( UIManager.getColor( "App.Secondary.Background" ).brighter(), new Color( 236, 240, 241 ) ),
        new DarkDayColor( Color.WHITE, Color.BLACK ) );
  }

  // Adds the FocusborderListener
  private static void addFocusBorderListener( final JComponent component )
  {
    component.addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( final FocusEvent __ )
      {
        component.setBorder( UIManager.getBorder( "DefaultBorder" ) );
      }

      @Override
      public void focusGained( final FocusEvent __ )
      {
        component.setBorder( UIManager.getBorder( "FocusBorder" ) );
      }
    } );
  }

  /**
   * Install a new custom Key with custom colors.
   *
   * @param key keyname
   * @param backgroundColors BG-Colors
   * @param foregroundColors FG-Colors
   */
  private static void installNewKey( final String key, final DarkDayColor backgroundColors,
                                     final DarkDayColor foregroundColors )
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

    // Color(0,0,0,0) - >no borders in darkmode
    UIManager.put( "DefaultBorder", BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder( new Color( 0, 0, 0, 0 ) ), BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );
    UIManager.put( "FocusBorder",
        BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( UIManager.getColor( "AppColor" ) ),
            BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );

    //    UIManager.put( "OptionPane.background", UIManager.getColor( "App.Background" ) );
    //    UIManager.put( "Panel.background", UIManager.getColor( "App.Background" ) );
    //    UIManager.put( "OptionPane.messageForeground", Color.white );
    //    UIManager.put( "Label.foreground", Color.white );
    //    UIManager.put( "ToolTip.background", new Color( 72, 87, 111 ) );
    //    UIManager.put( "ToolTip.foreground", Color.white );
    UIManager.put( "ComboBox.buttonBackground", new Color( 51, 61, 78 ) );
    UIManager.put( "ComboBox.buttonShadow", new Color( 51, 61, 78 ) );
    UIManager.put( "ComboBox.buttonDarkShadow", new Color( 91, 109, 139 ) );
    UIManager.put( "ComboBox.buttonHighlight", new Color( 51, 61, 78 ) );
    setUISettings();
  }

  /**
   * Lädt den Daymode
   */
  public static void showDayMode()
  {
    darkModeState = false;
    UIManager.put( "DefaultBorder", BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder( new Color( 173, 173, 173 ) ), BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );
    UIManager.put( "FocusBorder",
        BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( UIManager.getColor( "AppColor" ) ),
            BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );

    UIManager.put( "OptionPane.background", new Color( 240, 240, 240 ) );
    //    UIManager.put( "Panel.background", new Color( 240, 240, 240 ) );
    UIManager.put( "OptionPane.messageForeground", Color.black );
    //    UIManager.put( "Label.foreground", Color.black );
    //    UIManager.put( "ToolTip.background", new Color( 255, 255, 225 ) );
    //    UIManager.put( "ToolTip.foreground", new Color( 0, 0, 0 ) );
    UIManager.put( "ComboBox.buttonBackground", new Color( 215, 215, 215 ) );
    UIManager.put( "ComboBox.buttonShadow", new Color( 215, 215, 215 ) );
    UIManager.put( "ComboBox.buttonDarkShadow", Color.black );
    UIManager.put( "ComboBox.buttonHighlight", new Color( 215, 215, 215 ) );
    setUISettings();
  }

  /**
   * Wird beim Auslesen der Userconfig geladen oder nicht
   */
  public static void iniDayMode()
  {
    darkModeState = false;

    UIManager.put( "DefaultBorder", BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder( new Color( 173, 173, 173 ) ), BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );
    UIManager.put( "FocusBorder",
        BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( UIManager.getColor( "AppColor" ) ),
            BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );

    //    UIManager.put( "OptionPane.background", new Color( 240, 240, 240 ) );
    //    UIManager.put( "Panel.background", new Color( 240, 240, 240 ) );
    //    UIManager.put( "OptionPane.messageForeground", Color.black );
    //    UIManager.put( "Label.foreground", Color.black );
    //    UIManager.put( "ToolTip.background", new Color( 255, 255, 225 ) );
    //    UIManager.put( "ToolTip.foreground", new Color( 0, 0, 0 ) );
    UIManager.put( "ComboBox.buttonBackground", new Color( 215, 215, 215 ) );
    UIManager.put( "ComboBox.buttonShadow", new Color( 215, 215, 215 ) );
    UIManager.put( "ComboBox.buttonDarkShadow", Color.black );
    UIManager.put( "ComboBox.buttonHighlight", new Color( 215, 215, 215 ) );
  }

  /**
   * Wird beim Auslesen der Userconfig geladen oder nicht
   */
  public static void iniDarkMode()
  {
    darkModeState = true;

    UIManager.put( "DefaultBorder", BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder( new Color( 0, 0, 0, 0 ) ), BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );
    UIManager.put( "FocusBorder",
        BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( UIManager.getColor( "AppColor" ) ),
            BorderFactory.createEmptyBorder( 2, 5, 2, 5 ) ) );

    UIManager.put( "OptionPane.background", UIManager.getColor( "App.Background" ) );
    //    UIManager.put( "Panel.background", UIManager.getColor( "App.Background" ) );
    UIManager.put( "OptionPane.messageForeground", Color.white );
    //    UIManager.put( "Label.foreground", Color.white );
    //    UIManager.put( "ToolTip.background", new Color( 72, 87, 111 ) );
    //    UIManager.put( "ToolTip.foreground", Color.white );
    UIManager.put( "ComboBox.buttonBackground", new Color( 51, 61, 78 ) );
    UIManager.put( "ComboBox.buttonShadow", new Color( 51, 61, 78 ) );
    UIManager.put( "ComboBox.buttonDarkShadow", new Color( 91, 109, 139 ) );
    UIManager.put( "ComboBox.buttonHighlight", new Color( 51, 61, 78 ) );
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
   * Erstellt ein {@link JTextField}, welches der textFieldList hinzugefügt wird.
   *
   * @param key key
   * @return {@link JTextField}
   */
  public static JTextField createJTextField()
  {
    final JTextField textField = new JTextField();
    textFieldList.put( "spinner", textField );
    addFocusBorderListener( textField );
    textField.setBackground( darkModeState ? MeMateUIManager.getBackground( "spinner" ).getDarkColor() : Color.white );
    textField.setForeground( darkModeState ? Color.white : Color.black );
    textField.setBorder( UIManager.getBorder( "DefaultBorder" ) );
    return textField;
  }

  /**
   * Erstellt ein {@link JPasswordField}, welches der JPasswordFieldList hinzugefügt wird.
   *
   * @param key key
   * @return {@link JPasswordField}
   */
  public static JPasswordField createJPasswordField()
  {
    final JPasswordField passwordField = new JPasswordField();
    passwordFieldList.put( "spinner", passwordField );
    addFocusBorderListener( passwordField );
    passwordField.setBackground( darkModeState ? MeMateUIManager.getBackground( "spinner" ).getDarkColor() : Color.white );
    passwordField.setForeground( darkModeState ? Color.white : Color.black );
    passwordField.setBorder( UIManager.getBorder( "DefaultBorder" ) );
    return passwordField;
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
    checkbox.setBorder( BorderFactory.createLineBorder( new Color( 0, 0, 0, 0 ) ) );
    checkbox.setBorderPainted( true );
    checkbox.setOpaque( false );
    checkbox.addFocusListener( new FocusListener()
    {

      @Override
      public void focusLost( final FocusEvent e )
      {
        checkbox.setBorder( BorderFactory.createLineBorder( new Color( 0, 0, 0, 0 ) ) );
      }

      @Override
      public void focusGained( final FocusEvent e )
      {
        checkbox.setBorder( BorderFactory.createLineBorder( UIManager.getColor( "AppColor" ) ) );
      }
    } );
    checkBoxList.put( "default", checkbox );
    return checkbox;
  }

  /**
   * Erstellt einen {@link JRadioButton}, welche der radioButtonList hinzugefügt
   * wird.
   *
   * @param text buttontext
   *
   * @return {@link JRadioButton}
   */
  public static JRadioButton createRadioButton( final String text )
  {
    final JRadioButton radioButton = new JRadioButton( text );
    radioButtonList.put( "default", radioButton );
    return radioButton;
  }

  /**
   * Erstellt einen {@link JButton}, welcher der normalButtonList hinzugefügt
   * wird.
   *
   * @param key key
   * @return {@link JButton}
   */
  public static JButton createButton( final String key )
  {
    final JButton button = new JButton();
    button.setBackground(
        darkModeState ? getBackground( "button" ).getDarkColor() : getBackground( "button" ).getDayColor() );
    button.setForeground(
        darkModeState ? getForeground( "button" ).getDarkColor() : getForeground( "button" ).getDayColor() );
    button.setUI( new BasicButtonUI() );
    button.setContentAreaFilled( false );
    button.setOpaque( true );
    addFocusBorderListener( button );
    button.setBorder( UIManager.getBorder( "DefaultBorder" ) );
    normalButtonList.put( key, button );
    return button;
  }

  public static JButton createButton( final String key, final String text )
  {
    final JButton button = createButton( key );
    button.setText( text );
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

  @SuppressWarnings( "javadoc" )
  public static void registerPanel( final String key, final JPanel panel )
  {
    panelList.put( key, panel );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerIconLabel( final JLabel infoIconLabel, final ImageIcon infoIcon,
                                        final ImageIcon infoIconWhite )
  {
    labelList.put( defaultKey, infoIconLabel );
    panelIconList.put( infoIconLabel, new DarkDayIcon( infoIconWhite, infoIcon ) );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerTable( final String key, final JTable table )
  {
    tableList.put( key, table );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerList( final String key, final JList<?> list )
  {
    listList.put( key, list );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerScrollPane( final String key, final JScrollPane scrollPane )
  {
    scrollPaneList.put( key, scrollPane );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerSeparator( final JComponent separator, final String key )
  {
    separatorList.put( key, separator );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerFreeChart( final JFreeChart freeChart )
  {
    MeMateUIManager.freeChart = freeChart;
  }

  @SuppressWarnings( "javadoc" )
  public static void registerLineChart( final JFreeChart lineChart )
  {
    MeMateUIManager.lineChart = lineChart;
  }

  @SuppressWarnings( "javadoc" )
  public static void registerButton( final JButton button )
  {
    normalButtonList.put( "button", button );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerSpinner( final JSpinner spinner )
  {
    ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().addFocusListener( new FocusListener()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        if ( darkModeState )
        {
          spinner.setBorder( BorderFactory.createLineBorder( backgroundMap.get( "spinner" ).getDarkColor(), 1 ) );
        }
        else
        {
          spinner.setBorder( BorderFactory.createLineBorder( backgroundMap.get( "spinner" ).getDayColor().darker(), 1 ) );
        }
      }

      @Override
      public void focusGained( final FocusEvent e )
      {
        spinner.setBorder( BorderFactory.createLineBorder( UIManager.getColor( "AppColor" ), 1 ) );
      }
    } );
    spinnerList.put( "spinner", spinner );
  }

  @SuppressWarnings( "javadoc" )
  public static void registerComboBox( final JComboBox<String> comboBox )
  {
    comboBoxList.put( "comboBox", comboBox );
  }

  /**
   * Gibt die Hintergrundfarben des gegebenen Keys an.
   *
   * @param key Key
   * @return Hintergrundfarben
   */
  public static DarkDayColor getBackground( final String key )
  {
    return backgroundMap.get( key );
  }

  /**
   * Gibt die Vordergrundfarben des gegebenen Keys an.
   *
   * @param key Key
   * @return Vordergrundfarben
   */
  public static DarkDayColor getForeground( final String key )
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
      updateJPanel( key );
      updateList( key );
      updateTextField( key );
      updatePasswordField( key );
      updateLabel( key );
      updateRadioButtons( key );
      updateButton( key );
      updateInfoButton( key );
      updateTextPane( key );
      updateCheckBox( key );
      updateTable( key );
      updateSeperator( key );
      updateActionBarButtons( key );
    }
    updateGraphs();
  }

  public static void updateList( final String key )
  {
    for ( final JList<?> list : listList.get( key ) )
    {
      list.setBackground( darkModeState ? getBackground( key ).getDarkColor() : getBackground( key ).getDayColor() );
    }
  }

  public static void updateTextField( final String key )
  {
    for ( final JTextField textField : textFieldList.get( key ) )
    {
      textField.setBackground( darkModeState ? MeMateUIManager.getBackground( "spinner" ).getDarkColor() : Color.white );
      textField.setForeground( darkModeState ? Color.white : Color.black );
      textField.setBorder( UIManager.getBorder( "DefaultBorder" ) );
    }
  }

  public static void updatePasswordField( final String key )
  {
    for ( final JPasswordField textField : passwordFieldList.get( key ) )
    {
      textField.setBackground( darkModeState ? MeMateUIManager.getBackground( "spinner" ).getDarkColor() : Color.white );
      textField.setForeground( darkModeState ? Color.white : Color.black );
      textField.setBorder( UIManager.getBorder( "DefaultBorder" ) );
    }
  }

  public static void updateLabel( final String key )
  {
    for ( final JLabel label : labelList.get( key ) )
    {
      label.setForeground( darkModeState ? foregroundMap.get( key ).getDarkColor() : foregroundMap.get( key ).getDayColor() );
      if ( panelIconList.get( label ) != null )
      {
        label.setIcon( darkModeState ? panelIconList.get( label ).getDarkIcon() : panelIconList.get( label ).getDayIcon() );
      }
    }
  }

  public static void updateRadioButtons( final String key )
  {
    for ( final JRadioButton radioButton : radioButtonList.get( key ) )
    {
      radioButton.setBackground( darkModeState ? backgroundMap.get( key ).getDarkColor() : backgroundMap.get( key ).getDayColor() );
      radioButton.setForeground( darkModeState ? Color.white : Color.black );
    }
  }

  public static void updateInfoButton( final String key )
  {
    for ( final JButton button : infoButtonList.get( key ) )
    {
      button.setIcon( new InfoIcon() );
    }
  }

  public static void updateButton( final String key )
  {
    for ( final JButton button : normalButtonList.get( key ) )
    {
      button.setBackground( darkModeState ? backgroundMap.get( key ).getDarkColor() : backgroundMap.get( key ).getDayColor() );
      button.setForeground( darkModeState ? foregroundMap.get( key ).getDarkColor() : foregroundMap.get( key ).getDayColor() );
      button.setBorder( UIManager.getBorder( "DefaultBorder" ) );
      if ( iconList.get( button ) != null )
      {
        button.setIcon( darkModeState ? iconList.get( button ).getDarkIcon() : iconList.get( button ).getDayIcon() );
      }
    }
  }

  public static void updateTextPane( final String key )
  {
    for ( final JTextPane textPane : textPaneList.get( key ) )
    {
      textPane.setBackground( darkModeState ? backgroundMap.get( key ).getDarkColor() : backgroundMap.get( key ).getDayColor() );
    }
  }

  public static void updateCheckBox( final String key )
  {
    for ( final JCheckBox checkBox : checkBoxList.get( key ) )
    {
      checkBox.setBackground( darkModeState ? backgroundMap.get( key ).getDarkColor() : backgroundMap.get( key ).getDayColor() );
    }
  }

  public static void updateTable( final String key )
  {
    for ( final JTable table : tableList.get( key ) )
    {
      table.setBackground( darkModeState ? backgroundMap.get( key ).getDarkColor() : backgroundMap.get( key ).getDayColor() );
      final JTableHeader header = table.getTableHeader();
      header.setBackground( darkModeState ? backgroundMap.get( key ).getDarkColor().brighter().brighter()
          : backgroundMap.get( key ).getDayColor().darker() );
      header.setForeground( darkModeState ? foregroundMap.get( key ).getDarkColor() : foregroundMap.get( key ).getDayColor() );
      table.setForeground( darkModeState ? foregroundMap.get( key ).getDarkColor() : foregroundMap.get( key ).getDayColor() );
      table.setSelectionBackground( UIManager.getColor( "AppColor" ) );
    }
  }


  public static void updateActionBarButtons( final String key )
  {
    for ( final MeMateActionBarButton button : buttonList.get( key ) )
    {
      button.toggleDarkMode( darkModeState ? backgroundMap.get( key ).getDarkColor() : backgroundMap.get( key ).getDayColor(),
          darkModeState ? foregroundMap.get( key ).getDarkColor() : foregroundMap.get( key ).getDayColor() );
      button.setDarkModeState( darkModeState ? true : false );
      button.addMouseListener( new MeMateActionBarListener( button,
          () -> button
              .setBackground( darkModeState ? backgroundMap.get( key ).getDarkColor() : backgroundMap.get( key ).getDayColor() ),
          () -> button.setBackground(
              darkModeState ? backgroundMap.get( key ).getDarkColor().darker() : backgroundMap.get( key ).getDayColor().darker() ) ) );
    }
  }

  public static void updateSeperator( final String key )
  {
    for ( final JComponent separator : separatorList.get( key ) )
    {
      separator.setForeground( darkModeState ? foregroundMap.get( key ).getDarkColor() : foregroundMap.get( key ).getDayColor() );
    }
  }

  public static void updateJPanel( final String key )
  {
    for ( final JPanel panel : panelList.get( key ) )
    {
      panel.setBackground( darkModeState ? backgroundMap.get( key ).getDarkColor() : backgroundMap.get( key ).getDayColor() );
      if ( key.equals( "drinkButton" ) )
      {
        panel.setBorder( UIManager.getBorder( "DefaultBorder" ) );
      }
    }
  }

  public static void updateGraphs()
  {
    if ( freeChart != null )
    {
      freeChart.setBackgroundPaint( darkModeState ? MeMateUIManager.getBackground( "default" ).getDarkColor()
          : MeMateUIManager.getBackground( "default" ).getDayColor() );
      freeChart.getTitle().setPaint( darkModeState ? Color.white : Color.black );
      freeChart.getXYPlot()
          .setBackgroundPaint( darkModeState ? UIManager.getColor( "App.Background" ).brighter() : new Color( 192, 192, 192 ) );
      freeChart.getXYPlot().getDomainAxis().setTickLabelPaint( darkModeState ? Color.white : Color.black );
      freeChart.getXYPlot().getRangeAxis().setTickLabelPaint( darkModeState ? Color.white : Color.black );
      freeChart.getXYPlot().getDomainAxis().setLabelPaint( darkModeState ? Color.white : Color.black );
      freeChart.getXYPlot().getRangeAxis().setLabelPaint( darkModeState ? Color.white : Color.black );
      freeChart.getXYPlot().setDomainGridlinesVisible( false );
      freeChart.getXYPlot().setRangeGridlinesVisible( false );
    }
    if ( lineChart != null )
    {
      lineChart.setBackgroundPaint( darkModeState ? MeMateUIManager.getBackground( "default" ).getDarkColor()
          : MeMateUIManager.getBackground( "default" ).getDayColor() );
      lineChart.getCategoryPlot()
          .setBackgroundPaint( darkModeState ? UIManager.getColor( "App.Background" ).brighter() : new Color( 192, 192, 192 ) );
      lineChart.getTitle().setPaint( darkModeState ? Color.white : Color.black );
      lineChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( darkModeState ? Color.white : Color.black );
      lineChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( darkModeState ? Color.white : Color.black );
      lineChart.getCategoryPlot().getDomainAxis().setLabelPaint( darkModeState ? Color.white : Color.black );
      lineChart.getCategoryPlot().getRangeAxis().setLabelPaint( darkModeState ? Color.white : Color.black );
      lineChart.getCategoryPlot().setDomainGridlinesVisible( false );
      lineChart.getCategoryPlot().setRangeGridlinesVisible( false );
    }
  }

  /**
   * Enthält immer eine Farbe für den Darkmode und eine für den Daymode.
   *
   * @author nwe
   * @since 02.03.2020
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
    private DarkDayColor( final Color darkColor, final Color dayColor )
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

  public static void registerInfoButton( final JButton infoButton )
  {
    infoButtonList.put( "button", infoButton );
  }

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
    UIManager.put( "Label.disabledShadow", new Color( 0, 0, 0 ) );
    UIManager.put( "DefaultBrightColor", Color.white );
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
        putColorsInUIManager( new Color( 0, 173, 181 ), new Color( 34, 40, 49 ), new Color( 57, 62, 70 ), new Color( 42, 51, 64 ) );
        break;
      case "Red":
        putColorsInUIManager( new Color( 226, 62, 87 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
        break;
      case "Green":
        putColorsInUIManager( new Color( 153, 180, 51 ), new Color( 48, 56, 65 ), new Color( 58, 71, 80 ), new Color( 57, 67, 77 ) );
        break;
      case "Blue":
        putColorsInUIManager( new Color( 85, 172, 238 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Orange":
        putColorsInUIManager( new Color( 227, 162, 26 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Coral":
        putColorsInUIManager( new Color( 255, 111, 97 ), new Color( 41, 47, 51 ), new Color( 102, 117, 127 ), new Color( 49, 56, 60 ) );
        break;
      case "Tripple Green":
        putColorsInUIManager( new Color( 153, 180, 51 ), new Color( 11, 40, 25 ), new Color( 30, 113, 69 ), new Color( 13, 48, 30 ) );
        break;
      default :
        putColorsInUIManager( new Color( 29, 164, 165 ), new Color( 36, 43, 55 ), new Color( 52, 73, 94 ), new Color( 42, 51, 64 ) );
        break;
    }
  }

  private static void putColorsInUIManager( final Color appColor, final Color background, final Color background2, final Color actionbar )
  {
    UIManager.put( "AppColor", appColor );
    UIManager.put( "App.Background", background );
    UIManager.put( "App.Secondary.Background", background2 );
    UIManager.put( "App.Actionbar", actionbar );
  }

  public static void init()
  {
    installColors();
    installDefaults();
    applyTheme();
    setUIDefaults();
    putIconsInUIManager();
    FlatUIDefaultsInspector.install( "X" );
  }
}