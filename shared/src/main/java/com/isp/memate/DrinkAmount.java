/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.Serializable;

/**
 * @author nwe
 * @since 17.12.2019
 *
 */
public class DrinkAmount implements Serializable
{
  String name;
  int    amount;


  @SuppressWarnings( "javadoc" )
  public DrinkAmount( String name, int amount )
  {
    this.name = name;
    this.amount = amount;
  }

}
