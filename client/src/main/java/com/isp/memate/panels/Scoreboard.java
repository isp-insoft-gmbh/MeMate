package com.isp.memate.panels;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.isp.memate.util.GUIObjects;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Scoreboard extends BorderPane
{
  public Scoreboard()
  {
    GUIObjects.currentPanel = this;
    final FXMLLoader loader = new FXMLLoader();

    final ScoreboardController controller = new ScoreboardController();
    loader.setController( controller );

    final File fxmlFile = new File( "assets/fxml/Scoreboard.fxml" );
    URL fxmlUrl;
    HBox hbox = null;
    try
    {
      fxmlUrl = fxmlFile.toURI().toURL();
      loader.setLocation( fxmlUrl );
      hbox = loader.<HBox>load();
      setCenter( hbox );
      HBox.setHgrow( hbox, Priority.ALWAYS );
    }
    catch ( final IOException e )
    {
      // TODO(nwe|07.03.2022): Fehlerbehandlung muss noch implementiert werden!
    }
  }
}
