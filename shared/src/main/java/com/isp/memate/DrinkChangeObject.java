/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.Serializable;

/**
 * An Object which includes the ID of the drink and the changed object. The changed object could be price,
 * name, picture, etc...
 * 
 * @author nwe
 * @since 02.12.2019
 */
class DrinkChangeObject implements Serializable
{
  int    drinkID;
  Object change;

  DrinkChangeObject( int drinkID, Object change )
  {
    this.drinkID = drinkID;
    this.change = change;
  }
}
