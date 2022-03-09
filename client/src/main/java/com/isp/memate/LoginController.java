package com.isp.memate;

import com.isp.memate.util.Util;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController
{
  public TextField     usernameTextField    = null;
  public PasswordField passwordField        = null;
  public CheckBox      stayLoggedInCheckBox = null;
  public Button        loginButton          = null;
  public Stage         stage                = null;

  public LoginController( Stage stage )
  {
    this.stage = stage;
  }

  public String getUsername()
  {
    return this.usernameTextField.getText();
  }

  public Button getLoginButton()
  {
    return this.loginButton;
  }

  public TextField getUserNameTextField()
  {
    return this.usernameTextField;
  }

  public TextField getPasswordTextField()
  {
    return this.passwordField;
  }

  public String getPassword()
  {
    return this.passwordField.getText();
  }

  public boolean getStayLoggedInState()
  {
    return this.stayLoggedInCheckBox.isSelected();
  }

  @FXML
  public void loginButtonClicked( Event e )
  {
    final LoginInformation login = new LoginInformation( getUsername(),
        Util.getHash( String.valueOf( getPassword() ) ) );
    ServerCommunication.getInstance().checkLogin( login );
  }

  @FXML
  public void forgotPasswordHyperlinkClicked( Event e )
  {
    final Alert alert = new Alert( AlertType.WARNING );
    alert.setTitle( "Passwort vergessen" );
    alert.setHeaderText( null );
    alert.setContentText( "Bitte kontaktieren Sie ihren Admin." );
    alert.initOwner( stage );
    alert.showAndWait();
  }

  @FXML
  public void createUserHyperlinkClicked( Event e )
  {
    //TODO(nwe | 07.03.2022): Implementieren
  }

  public void updateLoginButtonEnabledState()
  {
    loginButton.setDisable( getUsername().isEmpty() || getPassword().isEmpty() );
  }
}
