package com.isp.memate.util;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class MeMateUIManager
{
  public static void applyTheme( Scene scene )
  {
    if ( PropertyHelper.getDarkModeProperty() )
    {
      final File cssFile = new File( "assets/css/darkmode.css" );
      scene.getStylesheets().clear();
      scene.getStylesheets().add( "file:///" + cssFile.getAbsolutePath().replace( "\\", "/" ) );
    }
    else
    {
      scene.getStylesheets().clear();
    }
  }

  public static Background getDefaultBackground()
  {
    if ( PropertyHelper.getDarkModeProperty() )
    {
      return new Background( new BackgroundFill( Color.rgb( 88, 98, 106 ), CornerRadii.EMPTY, Insets.EMPTY ) );
    }
    return new Background( new BackgroundFill( Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY ) );
  }

  public static Background getHoverBackground()
  {
    if ( PropertyHelper.getDarkModeProperty() )
    {
      return new Background( new BackgroundFill( Color.rgb( 88, 98, 106 ).brighter(), CornerRadii.EMPTY, Insets.EMPTY ) );
    }
    return new Background( new BackgroundFill( Color.rgb( 169, 169, 169 ), CornerRadii.EMPTY, Insets.EMPTY ) );
  }

  public static Background getPressedBackground()
  {
    if ( PropertyHelper.getDarkModeProperty() )
    {
      return new Background( new BackgroundFill( Color.rgb( 88, 98, 106 ).brighter().brighter(), CornerRadii.EMPTY, Insets.EMPTY ) );
    }
    return new Background( new BackgroundFill( Color.rgb( 118, 118, 118 ), CornerRadii.EMPTY, Insets.EMPTY ) );
  }

}
