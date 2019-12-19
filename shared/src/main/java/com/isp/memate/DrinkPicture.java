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
public class DrinkPicture implements Serializable
{
  byte[] pictureAsBytes;
  int    id;

  @SuppressWarnings( "javadoc" )
  public DrinkPicture( byte[] pictureAsBytes, int id )
  {
    this.pictureAsBytes = pictureAsBytes;
    this.id = id;
  }
}
