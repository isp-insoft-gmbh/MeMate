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
class DrinkPicture implements Serializable
{
  byte[] pictureAsBytes;
  int    id;

  DrinkPicture( byte[] pictureAsBytes, int id )
  {
    this.pictureAsBytes = pictureAsBytes;
    this.id = id;
  }
}
