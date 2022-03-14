package com.isp.memate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import com.isp.memate.Shared.LoginResult;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.PropertyHelper;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Login extends Stage
{
  final LoginController controllerRef;

  public Login()
  {
    GUIObjects.loginFrame = this;
    final FXMLLoader loader = new FXMLLoader();

    final LoginController controller = new LoginController( this );
    loader.setController( controller );

    final File fxmlFile = new File( "assets/fxml/Login.fxml" );
    URL fxmlUrl;
    VBox vbox = null;
    try
    {
      fxmlUrl = fxmlFile.toURI().toURL();
      loader.setLocation( fxmlUrl );
      vbox = loader.<VBox>load();
      final Image frameIcon = new Image( new FileInputStream( new File( "assets/icons/frameicon128.png" ) ) );
      getIcons().add( frameIcon );
    }
    catch ( final IOException e )
    {
      // TODO(nwe|07.03.2022): Fehlerbehandlung muss noch implementiert werden!
    }


    controllerRef = loader.getController();

    controllerRef.getLoginButton().setDisable( true );
    controllerRef.getUserNameTextField().textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      controllerRef.updateLoginButtonEnabledState();
    } );
    controllerRef.getPasswordTextField().textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      controllerRef.updateLoginButtonEnabledState();
    } );

    final Scene scene = new Scene( vbox );
    setScene( scene );
    show();
    setResizable( false );
    setTitle( "MeMateFX" );

    setOnCloseRequest( new EventHandler<WindowEvent>()
    {
      @Override
      public void handle( WindowEvent e )
      {
        System.exit( 0 );
      }
    } );
  }

  public void validateLoginResult( LoginResult loginResult )
  {
    if ( LoginResult.LOGIN_SUCCESSFULL == loginResult )
    {
      generateSessionID( controllerRef.getUsername() );
      close();
      final MainFrame mainFrame = new MainFrame();
      mainFrame.show();
      GUIObjects.loginFrame = null;
    }
    else if ( LoginResult.LOGIN_SUCCESSFULL_REQUEST_NEW_PASSWORD == loginResult )
    {
      //TODO(nwe | 07.03.2022): 
    }
    else
    {
      final String message =
          LoginResult.USER_NOT_FOUND == loginResult ? "Benutzer konnte nicht gefunden werden." : "Falsches Passwort eingegeben.";
      final Alert alert = new Alert( AlertType.WARNING );
      alert.setTitle( "Login fehlgeschlagen" );
      alert.setHeaderText( null );
      alert.setContentText( message );
      alert.initOwner( this );
      alert.showAndWait();
    }
  }

  /**
   * Erzeugt eine UUID für die derzeitge Session. Diese SessionID wird zusammen
   * mit dem Benutzername an den Server geschickt, damit diese verbunden werden
   * können. Wenn man eingeloggt bleiben möchte, so wird die SessionID in den
   * userconfig Properties gespeichert und wird beim nächsten Start aufgerufen.
   *
   * @param username Nutzername
   */
  private void generateSessionID( final String username )
  {
    final UUID uuid = UUID.randomUUID();
    ServerCommunication.getInstance().connectSessionIDToUser( uuid.toString() );
    if ( controllerRef.getStayLoggedInState() )
    {
      PropertyHelper.setProperty( "SessionID", uuid.toString() );
    }
  }
}
