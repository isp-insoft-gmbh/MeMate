package com.isp.memate;

import java.util.Optional;

import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.Util;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

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
    final Dialog<Pair<String, String>> dialog = new Dialog<>();
    dialog.setTitle( "Konto erstellen" );
    dialog.setHeaderText( null );
    dialog.initOwner( GUIObjects.loginFrame );


    final ButtonType registerButtonType = new ButtonType( "Registrieren", ButtonData.OK_DONE );
    dialog.getDialogPane().getButtonTypes().addAll( registerButtonType, ButtonType.CANCEL );

    final GridPane grid = new GridPane();
    grid.setHgap( 10 );
    grid.setVgap( 10 );
    grid.setPadding( new Insets( 20, 150, 10, 10 ) );

    final TextField usernameTextField = new TextField();
    usernameTextField.setPromptText( "Benutzername" );
    final PasswordField passwordTextField = new PasswordField();
    passwordTextField.setPromptText( "Passwort" );
    final PasswordField passwordRepeatTextField = new PasswordField();
    passwordRepeatTextField.setPromptText( "Passwort" );
    final Label checkPasswortLabel = new Label( "" );
    checkPasswortLabel.setMinWidth( 210 );
    checkPasswortLabel.setPrefWidth( 210 );

    grid.add( new Label( "Benutzername:" ), 0, 0 );
    grid.add( usernameTextField, 1, 0 );
    grid.add( new Label( "Passwort:" ), 0, 1 );
    grid.add( passwordTextField, 1, 1 );
    grid.add( new Label( "Passwort wiederholen:" ), 0, 2 );
    grid.add( passwordRepeatTextField, 1, 2 );
    grid.add( checkPasswortLabel, 0, 3 );

    final Node registerButton = dialog.getDialogPane().lookupButton( registerButtonType );
    registerButton.setDisable( true );

    passwordTextField.textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      if ( passwordTextField.getText().trim().isEmpty() || passwordRepeatTextField.getText().trim().isEmpty()
          || usernameTextField.getText().trim().isEmpty() )
      {
        registerButton.setDisable( true );
        checkPasswortLabel.setText( "" );
      }
      else
      {
        if ( passwordTextField.getText().equals( passwordRepeatTextField.getText() ) )
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 89, 168, 105 ) );
          registerButton.setDisable( false );
        }
        else
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen nicht überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 219, 88, 96 ) );
          registerButton.setDisable( true );
        }
      }
    } );
    passwordRepeatTextField.textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      if ( passwordTextField.getText().trim().isEmpty() || passwordRepeatTextField.getText().trim().isEmpty()
          || usernameTextField.getText().trim().isEmpty() )
      {
        registerButton.setDisable( true );
        checkPasswortLabel.setText( "" );
      }
      else
      {
        if ( passwordTextField.getText().equals( passwordRepeatTextField.getText() ) )
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 89, 168, 105 ) );
          registerButton.setDisable( false );
        }
        else
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen nicht überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 219, 88, 96 ) );
          registerButton.setDisable( true );
        }
      }
    } );
    usernameTextField.textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      if ( passwordTextField.getText().trim().isEmpty() || passwordRepeatTextField.getText().trim().isEmpty()
          || usernameTextField.getText().trim().isEmpty() )
      {
        registerButton.setDisable( true );
        checkPasswortLabel.setText( "" );
      }
      else
      {
        if ( passwordTextField.getText().equals( passwordRepeatTextField.getText() ) )
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 89, 168, 105 ) );
          registerButton.setDisable( false );
        }
        else
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen nicht überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 219, 88, 96 ) );
          registerButton.setDisable( true );
        }
      }
    } );

    dialog.getDialogPane().setContent( grid );

    Platform.runLater( () -> usernameTextField.requestFocus() );

    dialog.setResultConverter( dialogButton ->
    {
      if ( dialogButton == registerButtonType )
      {
        return new Pair<String, String>( usernameTextField.getText(), passwordTextField.getText() );
      }
      return null;
    } );

    final Optional<Pair<String, String>> result = dialog.showAndWait();

    result.ifPresent( user ->
    {
      ServerCommunication.getInstance().registerNewUser( user.getKey(),
          Util.getHash( String.valueOf( user.getValue() ) ) );
    } );
  }

  public void updateLoginButtonEnabledState()
  {
    loginButton.setDisable( getUsername().isEmpty() || getPassword().isEmpty() );
  }
}
