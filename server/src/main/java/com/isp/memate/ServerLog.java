/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Die ServerLog Klasse gibt zum einen alles formatiert und mit Farbe in der Console aus und speichert
 * den Log in einer .txt Datei.
 * 
 * @author nwe
 * @since 02.01.2020
 *
 */
class ServerLog
{
  private static final String            ANSI_RESET  = "\u001B[0m";
  private static final String            ANSI_RED    = "\u001B[31m";
  private static final String            ANSI_YELLOW = "\u001B[33m";
  private static final String            ANSI_CYAN   = "\u001B[36m";
  private static final DateTimeFormatter formatter   = DateTimeFormatter.ofPattern( "d MMM HH:mm:ss.SSS" );

  /**
   * Erstellt ein neuen Log, abhänig von den logType
   * 
   * @param logType Typ des Logs
   * @param message Nachricht
   */
  static void newLog( logType logType, String message )
  {
    LocalDateTime now = LocalDateTime.now();
    String date = now.format( formatter );
    switch ( logType )
    {
      case SQL:
        System.out.println( "[" + ANSI_RED + "SQL" + ANSI_RESET + "]    " + "[" + date + "] " + message );
        break;
      case COMMAND:
        System.out.println( "[" + ANSI_YELLOW + "COMMAND" + ANSI_RESET + "]" + "[" + date + "] " + message );
        break;
      case ERROR:
        System.out.println( "[" + ANSI_RED + "ERROR" + ANSI_RESET + "]  " + "[" + date + "] " + message );
        break;
      case INFO:
        System.out.println( "[" + ANSI_CYAN + "INFO" + ANSI_RESET + "]   " + "[" + date + "] " + message );
        break;
    }
    File logFile = new File( Database.getTargetFolder().toString() + File.separator + "ServerLog.log" );
    Logger logger = Logger.getLogger( "ServerLog" );
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
      newLog( ServerLog.logType.ERROR, exception.getMessage() );
    }
  }

  enum logType
  {
    SQL,
    COMMAND,
    INFO,
    ERROR;
  }
}
