/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 * @author nwe
 * @since 10.12.2019
 *
 */
public class PreferencesTab extends JPanel
{
  private static final PreferencesTab instance = new PreferencesTab();

  /**
   * @return static instance of {@link PreferencesTab}
   */
  public static PreferencesTab getInstance()
  {
    return instance;
  }

  /**
   * 
   */
  public PreferencesTab()
  {
    setLayout( new FlowLayout() );
    List<String> drinkNames = ServerCommunication.getInstance().getDrinkNames();
    System.out.println( "Nun werden die Panels erzeugt " + drinkNames.size() );
    for ( String drink : drinkNames )
    {
      System.out.println( drink );
      JPanel drinkPanel = new JPanel( new FlowLayout() );
      drinkPanel.add( new JLabel( drink ) );
      drinkPanel.add( new JLabel( ServerCommunication.getInstance().getIcon( drink ) ) );
      drinkPanel.add( new JSlider( 0, 20, 5 ) );
      drinkPanel.setPreferredSize( new Dimension( 700, 200 ) );
      add( drinkPanel );
    }
    JPanel drinkPanel = new JPanel( new FlowLayout() );
    drinkPanel.add( new JLabel( "MioMio Mate" ) );
    drinkPanel.add( new JLabel( ServerCommunication.getInstance().getIcon( "MioMio Mate" ) ) );
    drinkPanel.add( new JSlider( 0, 20, 5 ) );
    drinkPanel.setPreferredSize( new Dimension( 700, 300 ) );
    add( drinkPanel );
  }
}
