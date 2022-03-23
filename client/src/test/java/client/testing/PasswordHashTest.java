package client.testing;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.isp.memate.util.Util;

/* Diese Testklasse prüft, ob das Passwort Hashing fehlerfrei funktioniert.
 * Dies ist nötig, damit das im Login eingegeben Passwort korrekt gehashed wird,
 * damit dieses an den Server weiter gegeben werden. Dieser darf Passwörter nämlich
 * nicht als Klartext abspeichern. */
public class PasswordHashTest
{
  @Test
  public void testPasswordHashing()
  {
    assertEquals( "8C6976E5B541415BDE98BD4DEE15DFB167A9C873FC4BB8A81F6F2AB448A918", Util.getHash( "admin" ) );
    assertEquals( "A36C101570CC4410993DE5385AD734ADB2DAE6A5139AC7672577803084634D", Util.getHash( "Passwort" ) );
    assertEquals( "33F1E3C46DDA1B66B5D2B9BDEB5ECF0B9AE85C8348F16DB5116C9BDE6DC9677", Util.getHash( "P4ss!wort_%" ) );
    assertEquals( "1AF564DDC6039457B2FB26B3D6A316C15EBA2A886449847C3210C35821A693", Util.getHash( "    " ) );
    assertEquals( "E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", Util.getHash( "" ) );
    assertEquals( "164D9F5955481CF4CF27166763C22AC8051B4D7683CB63EF43C533D9AC2657B", Util.getHash( "5324123" ) );
    assertThrows( NullPointerException.class, () -> Util.getHash( null ) );
  }
}
