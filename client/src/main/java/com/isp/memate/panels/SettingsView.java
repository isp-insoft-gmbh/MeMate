package com.isp.memate.panels;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.isp.memate.util.GUIObjects;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

public class SettingsView extends VBox
{
  public SettingsView()
  {
    GUIObjects.currentPanel = this;
    final FXMLLoader loader = new FXMLLoader();

    final SettingsController controller = new SettingsController();
    loader.setController( controller );

    final File fxmlFile = new File( "assets/fxml/Settings.fxml" );
    URL fxmlUrl;
    VBox vbox = null;
    try
    {
      fxmlUrl = fxmlFile.toURI().toURL();
      loader.setLocation( fxmlUrl );
      vbox = loader.<VBox>load();
      getChildren().add( vbox );
      setPadding( new Insets( 10, 0, 0, 10 ) );
    }
    catch ( final IOException e )
    {
      // TODO(nwe|07.03.2022): Fehlerbehandlung muss noch implementiert werden!
    }
  }
}
