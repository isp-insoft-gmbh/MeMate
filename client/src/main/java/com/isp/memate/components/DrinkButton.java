package com.isp.memate.components;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.isp.memate.Drink;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DrinkButton extends VBox
{
  private VBox currentState;

  public DrinkButton( Drink drink )
  {
    final FXMLLoader loader = new FXMLLoader();

    final DrinkButtonController controller = new DrinkButtonController( drink );
    loader.setController( controller );

    final File fxmlFile = new File( "assets/fxml/DrinkButton.fxml" );
    try
    {
      final URL fxmlUrl = fxmlFile.toURI().toURL();
      loader.setLocation( fxmlUrl );
      currentState = loader.<VBox>load();
      getChildren().add( currentState );
      setBorder( new Border( new BorderStroke( Color.LIGHTGRAY,
          BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT ) ) );
      setBackground( new Background( new BackgroundFill( Color.WHITE, CornerRadii.EMPTY, new Insets( 0 ) ) ) );
    }
    catch ( final IOException e )
    {
      // TODO(nwe|07.03.2022): Fehlerbehandlung muss noch implementiert werden!
    }
  }
}
