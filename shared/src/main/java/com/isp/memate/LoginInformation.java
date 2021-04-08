/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.Serializable;

/**
 * @author nwe
 * @since 29.11.2019
 */
class LoginInformation implements Serializable
{
  String username;
  String password;

  LoginInformation( String username, String password )
  {
    this.username = username;
    this.password = password;
  }
}
