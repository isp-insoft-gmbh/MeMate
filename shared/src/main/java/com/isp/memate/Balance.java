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
public class Balance implements Serializable
{
  String sessionID;
  String username;
  Float  balance;

  @SuppressWarnings( "javadoc" )
  public Balance( String sessionID, String username, Float balance )
  {
    this.sessionID = sessionID;
    this.username = username;
    this.balance = balance;
  }
}
