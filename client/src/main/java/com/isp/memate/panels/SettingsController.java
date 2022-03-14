package com.isp.memate.panels;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.ToggleSwitch;

import com.isp.memate.ServerCommunication;
import com.isp.memate.util.GUIObjects;
import com.isp.memate.util.PropertyHelper;
import com.isp.memate.util.Util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class SettingsController implements Initializable
{
  public ToggleSwitch standupMeetingToggleSwitch = null;
  public ToggleSwitch drinkNotificationSwitch    = null;
  public RadioButton  lightModeRadioButton       = null;
  public RadioButton  darkModeRadioButton        = null;
  public ColorPicker  colorPicker                = null;

  @Override
  public void initialize( URL location, ResourceBundle resources )
  {
    initializeColorPicker();
    initializeDarkModeRadioButtons();
    initializeMeetingToggleSwitch();
    initializeDrinkNotificationSwitch();
  }

  private void initializeColorPicker()
  {
    colorPicker.setValue( PropertyHelper.getAppColorProperty() );
    colorPicker.setOnAction( new EventHandler<ActionEvent>()
    {
      @Override
      public void handle( ActionEvent event )
      {
        PropertyHelper.setProperty( "FXAppColor", colorPicker.getValue().toString() );
        GUIObjects.mainframe.updateHeaderColor();
      }
    } );
  }

  private void initializeDrinkNotificationSwitch()
  {
    drinkNotificationSwitch.setSelected( PropertyHelper.getBooleanProperty( "ConsumptionNotification" ) );
    drinkNotificationSwitch.setOnMouseClicked( new EventHandler<Event>()
    {
      @Override
      public void handle( Event event )
      {
        PropertyHelper.setProperty( "ConsumptionNotification", String.valueOf( drinkNotificationSwitch.isSelected() ) );
      };
    } );
  }

  private void initializeMeetingToggleSwitch()
  {
    standupMeetingToggleSwitch.setSelected( PropertyHelper.getBooleanProperty( "MeetingNotification" ) );
    standupMeetingToggleSwitch.setOnMouseClicked( new EventHandler<Event>()
    {
      @Override
      public void handle( Event event )
      {
        PropertyHelper.setProperty( "MeetingNotification", String.valueOf( standupMeetingToggleSwitch.isSelected() ) );
      };
    } );
  }

  private void initializeDarkModeRadioButtons()
  {
    if ( PropertyHelper.getDarkModeProperty() )
    {
      darkModeRadioButton.setSelected( true );
    }
    else
    {
      lightModeRadioButton.setSelected( true );
    }
    final ToggleGroup group = new ToggleGroup();
    lightModeRadioButton.setToggleGroup( group );
    darkModeRadioButton.setToggleGroup( group );
    group.selectedToggleProperty().addListener( new ChangeListener<Toggle>()
    {
      @Override
      public void changed( ObservableValue<? extends Toggle> ov,
                           Toggle old_toggle, Toggle new_toggle )
      {
        PropertyHelper.setProperty( "Darkmode", String.valueOf( darkModeRadioButton.isSelected() ) );
      }
    } );
  }

  @FXML
  public void onChangePasswordHyperlinkClick( Event e )
  {
    final Dialog<String> dialog = new Dialog<>();
    dialog.setTitle( "Passwort ändern" );
    dialog.setHeaderText( null );
    dialog.initOwner( GUIObjects.mainframe );


    final ButtonType loginButtonType = new ButtonType( "Speichern", ButtonData.OK_DONE );
    dialog.getDialogPane().getButtonTypes().addAll( loginButtonType, ButtonType.CANCEL );

    final GridPane grid = new GridPane();
    grid.setHgap( 10 );
    grid.setVgap( 10 );
    grid.setPadding( new Insets( 20, 150, 10, 10 ) );

    final PasswordField passwordTextField = new PasswordField();
    passwordTextField.setPromptText( "Passwort" );
    final PasswordField passwordRepeatTextField = new PasswordField();
    passwordRepeatTextField.setPromptText( "Passwort" );
    final Label checkPasswortLabel = new Label( "" );
    checkPasswortLabel.setMinWidth( 210 );
    checkPasswortLabel.setPrefWidth( 210 );

    grid.add( new Label( "Passwort:" ), 0, 0 );
    grid.add( passwordTextField, 1, 0 );
    grid.add( new Label( "Passwort wiederholen:" ), 0, 1 );
    grid.add( passwordRepeatTextField, 1, 1 );
    grid.add( checkPasswortLabel, 0, 2 );

    final Node loginButton = dialog.getDialogPane().lookupButton( loginButtonType );
    loginButton.setDisable( true );

    passwordTextField.textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      if ( passwordTextField.getText().trim().isEmpty() || passwordRepeatTextField.getText().trim().isEmpty() )
      {
        loginButton.setDisable( true );
        checkPasswortLabel.setText( "" );
      }
      else
      {
        if ( passwordTextField.getText().equals( passwordRepeatTextField.getText() ) )
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 89, 168, 105 ) );
          loginButton.setDisable( false );
        }
        else
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen nicht überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 219, 88, 96 ) );
          loginButton.setDisable( true );
        }
      }
    } );
    passwordRepeatTextField.textProperty().addListener( ( observable, oldValue, newValue ) ->
    {
      if ( passwordTextField.getText().trim().isEmpty() || passwordRepeatTextField.getText().trim().isEmpty() )
      {
        loginButton.setDisable( true );
        checkPasswortLabel.setText( "" );
      }
      else
      {
        if ( passwordTextField.getText().equals( passwordRepeatTextField.getText() ) )
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 89, 168, 105 ) );
          loginButton.setDisable( false );
        }
        else
        {
          checkPasswortLabel.setText( "Die Passwörter stimmen nicht überein." );
          checkPasswortLabel.setTextFill( Color.rgb( 219, 88, 96 ) );
          loginButton.setDisable( true );
        }
      }
    } );

    dialog.getDialogPane().setContent( grid );

    Platform.runLater( () -> passwordTextField.requestFocus() );

    dialog.setResultConverter( dialogButton ->
    {
      if ( dialogButton == loginButtonType )
      {
        return passwordTextField.getText();
      }
      return null;
    } );

    final Optional<String> result = dialog.showAndWait();

    result.ifPresent( password ->
    {
      ServerCommunication.getInstance().changePassword( Util.getHash( String.valueOf( password ) ) );
    } );
  }

  @FXML
  public void onChangeNameHyperlinkClick( Event e )
  {
    final TextInputDialog dialog = new TextInputDialog( "" );
    dialog.setTitle( "Anzeigenamen ändern" );
    dialog.setHeaderText( null );
    dialog.setContentText( "Bitte neuen Namen eingeben:" );
    dialog.initOwner( GUIObjects.mainframe );

    final Optional<String> result = dialog.showAndWait();

    result.ifPresent( name ->
    {
      if ( name != null && !name.isBlank() )
      {
        ServerCommunication.getInstance().changeDisplayName( name );
      }
    } );
  }
}