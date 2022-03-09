package com.isp.memate.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util
{
  /**
   * Das eingebene Passwort wird gehasht.
   *
   * @param input eingebenes Passwort
   * @return gehashtes Passwort
   */
  public static String getHash( final String input )
  {
    try
    {
      final MessageDigest passwordHasher = MessageDigest.getInstance( "SHA-256" );
      final byte[] hashedPasswordArray = passwordHasher.digest( input.getBytes( StandardCharsets.UTF_8 ) );
      final StringBuilder asString = new StringBuilder( hashedPasswordArray.length * 2 );
      for ( final byte b : hashedPasswordArray )
      {
        asString.append( Integer.toHexString( b & 0xff ) );
      }
      return asString.toString().toUpperCase();
    }
    catch ( final NoSuchAlgorithmException exception )
    {
      throw new RuntimeException( exception );
    }
  }
}