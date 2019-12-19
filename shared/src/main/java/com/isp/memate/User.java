/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.Serializable;

/**
 * @author nwe
 * @since 27.11.2019
 *
 */
public class User implements Serializable
{
  String name;
  String password;
  Float  balance;
  int    id;


  @SuppressWarnings( "javadoc" )
  public User( String name, String password, Float balance, int id )
  {
    this.name = name;
    this.password = password;
    this.balance = balance;
    this.id = id;
  }
}
