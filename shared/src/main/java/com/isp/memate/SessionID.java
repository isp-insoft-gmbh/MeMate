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
public class SessionID implements Serializable
{
  String sessionID;
  String username;

  @SuppressWarnings( "javadoc" )
  public SessionID( String sessionID, String username )
  {
    this.sessionID = sessionID;
    this.username = username;
  }

}
