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
public class DrinkName implements Serializable
{
  String name;
  int    id;

  @SuppressWarnings( "javadoc" )
  public DrinkName( String name, int id )
  {
    this.name = name;
    this.id = id;
  }
}
