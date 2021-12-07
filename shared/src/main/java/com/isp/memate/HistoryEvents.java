package com.isp.memate;

public enum HistoryEvents
{
  CONSUMED_DRINK( " getrunken" ),
  BALANCE_ADDED( "Guthaben aufgeladen" ),
  BALANCE_REMOVED( "Guthaben entfernt" ),
  UNDO( "Letzte Aktion rückgängig" ),
  ERROR( "Fehler" );

  final String guiRepresentation;

  public String getGuiRepresentation()
  {
    return guiRepresentation;
  }

  HistoryEvents( String guiRepresentation )
  {
    this.guiRepresentation = guiRepresentation;
  }
}
