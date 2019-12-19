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
public class DrinkPrice implements Serializable
{
  Float  price;
  int    id;
  String name;

  @SuppressWarnings( "javadoc" )
  public DrinkPrice( Float price, int id, String name )
  {
    this.price = price;
    this.id = id;
    this.name = name;
  }
}
