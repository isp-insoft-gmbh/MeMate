/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Die {@link ClientLog} Klasse gibt zum einen alles formatiert in der Console aus und speichert
 * den Log in einer .txt Datei.
 * 
 * @author nwe
 * @since 02.01.2020
 *
 */
public class ClientLog
{
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "d MMM HH:mm:ss.SSS" );

  /**
   * Erstellt ein neuen Log, abhänig von den logType
   * 
   * @param message Nachricht
   */
  public static void newLog( String message )
  {
    LocalDateTime now = LocalDateTime.now();
    String date = now.format( formatter );

    System.out.println( "[" + date + "] " + message );


    File logFile = new File( PropertyHelper.MAIN_FOLDER + File.separator + "ClientLog.log" );
    Logger logger = Logger.getLogger( "ClientLog" );
    try
    {
      logger.setUseParentHandlers( false );
      FileHandler logFileHandler = new FileHandler( logFile.toString(), true );
      logger.addHandler( logFileHandler );
      SimpleFormatter formatter = new SimpleFormatter();
      logFileHandler.setFormatter( formatter );
      logger.info( message );
      logFileHandler.close();
    }
    catch ( SecurityException | IOException exception )
    {
      newLog( exception.getMessage() );
    }
  }
}

