/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * In dieser Klasse liegen Informationen zu den Getränken
 * 
 * @author nwe
 * @since 21.10.2019
 */
public class DrinkInfos
{
  /**
   * TODO Kontostand aus Datenbank bekommen
   * Der Kontostand des Nutzers
   */
  public static Float              balance     = 0f;
  private final Map<String, Float> priceMap    = new HashMap<>();
  private final Map<String, Icon>  imageMap    = new HashMap<>( 4 );
  private final String[][]         historyData = {
      { "Guthaben aufgeladen", "Niklas", "+1,00€", "0,00€", "18.10.2019" },
      { "MioMio Ginger konsumiert", "Niklas", "-0,60€", "-1,00€", "18.10.2019" },
      { "MioMio Pomegranate konsumiert", "Niklas", "-0,60€", "-0,40€", "17.10.2019" },
      { "MioMio Cola konsumiert", "Niklas", "-0,60€", "0,20€", "16.10.2019" },
      { "MioMio Mate konsumiert", "Niklas", "-0,60€", "0,80€", "15.10.2019" },
      { "MioMio Cola konsumiert", "Marcel", "-0,60€", "1,40€", "15.10.2019" },
      { "MioMio Cola konsumiert", "Niklas", "-0,60€", "1,40€", "15.10.2019" },
      { "Guthaben aufgeladen", "Niklas", "+2,00€", "2,00€", "15.10.2019" } };

  private static final DrinkInfos instance = new DrinkInfos();

  /**
   * @return the static instance of {@link DrinkInfos}
   */
  public static DrinkInfos getInstance()
  {
    return instance;
  }

  /**
   * Fügt der Preisliste und der Bilderliste Daten hinzu
   */
  private DrinkInfos()
  {
    priceMap.put( "MioMio Mate", 0.6f );
    priceMap.put( "MioMio Cola", 0.6f );
    priceMap.put( "Coca Cola", 0.7f );
    priceMap.put( "MioMio Pomegranate", 0.6f );

    imageMap.put( "MioMio Mate",
        new ImageIcon( Toolkit.getDefaultToolkit()
            .getImage( getClass().getClassLoader().getResource( "placeholder1.png" ) )
            .getScaledInstance( 45, 140, Image.SCALE_SMOOTH ) ) );
    imageMap.put( "MioMio Cola",
        new ImageIcon( Toolkit.getDefaultToolkit()
            .getImage( getClass().getClassLoader().getResource( "placeholder2.png" ) )
            .getScaledInstance( 45, 140, Image.SCALE_SMOOTH ) ) );
    imageMap.put( "Coca Cola",
        new ImageIcon( Toolkit.getDefaultToolkit()
            .getImage( getClass().getClassLoader().getResource( "placeholder3.png" ) )
            .getScaledInstance( 45, 140, Image.SCALE_SMOOTH ) ) );
    imageMap.put( "MioMio Pomegranate",
        new ImageIcon(
            Toolkit.getDefaultToolkit()
                .getImage( getClass().getClassLoader().getResource( "placeholder4.png" ) )
                .getScaledInstance( 45, 140, Image.SCALE_SMOOTH ) ) );
  }

  /**
   * Retrieves the price for a drink.
   * 
   * @param drink Drink to get the price for
   * 
   * @return price or {@code null} if not available
   */
  public Float getPrice( Object drink )
  {
    return priceMap.get( drink );
  }

  /**
   * Retrieves the image for a drink.
   * 
   * @param drink Drink to get the icon for
   * 
   * @return icon or {@code null} if not available
   */
  public Icon getIcon( Object drink )
  {
    return imageMap.get( drink );
  }

  /**
   * @return die Daten der Historie
   */
  public String[][] getHistoryData()
  {
    return historyData;
  }
}
