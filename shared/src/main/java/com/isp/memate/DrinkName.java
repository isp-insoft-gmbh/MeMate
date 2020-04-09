/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.Serializable;

/**
 * @author nwe
 * @since 02.12.2019
 *
 */
class DrinkName implements Serializable
{
  String name;
  int    id;

  DrinkName( String name, int id )
  {
    this.name = name;
    this.id = id;
  }
}
