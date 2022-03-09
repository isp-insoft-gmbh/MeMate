package com.isp.memate.panels;


import com.isp.memate.Cache;
import com.isp.memate.Drink;
import com.isp.memate.ServerCommunication;
import com.isp.memate.components.DrinkButton;
import com.isp.memate.util.GUIObjects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Dashboard extends VBox
{
  final ScrollPane scrollPane = new ScrollPane();
  final FlowPane   flowPane   = new FlowPane();

  public Dashboard()
  {
    setPadding( new Insets( 5 ) );
    GUIObjects.currentPanel = this;
    flowPane.setHgap( 5 );
    flowPane.setVgap( 5 );
    for ( final Drink drink : Cache.getInstance().getDrinks().values() )
    {
      if ( drink.getAmount() > 0 )
      {
        flowPane.getChildren().add( new DrinkButton( drink ) );
      }
    }
    scrollPane.setFitToWidth( true );
    scrollPane.setFitToHeight( true );
    scrollPane.setContent( flowPane );
    scrollPane.setStyle( "-fx-background-color:transparent;" );

    final BorderPane footer = new BorderPane();
    final VBox leftVBox = new VBox();
    final HBox hBox = new HBox();
    final Spinner<Integer> spinner = new Spinner<>();
    spinner.setValueFactory( new IntegerSpinnerValueFactory( -1000, 1000, 1, 1 ) );
    hBox.getChildren().add( spinner );
    final Button payButton = new Button( "Einzahlen" );
    payButton.setOnAction( new EventHandler<ActionEvent>()
    {
      @Override
      public void handle( ActionEvent e )
      {
        ServerCommunication.getInstance().addBalance( spinner.getValue() );
      }
    } );
    hBox.getChildren().add( payButton );
    final Label addBalanceLabel = new Label( "Kontostand aufladen" );
    addBalanceLabel.setFont( new Font( 14 ) );
    leftVBox.getChildren().add( addBalanceLabel );
    leftVBox.getChildren().add( hBox );
    leftVBox.setPadding( new Insets( 0, 5, 0, 0 ) );


    final VBox rightVBox = new VBox();
    final Label infoLabel1 =
        new Label( "Einzahlungen sind nur in Höhe von gültigen Kombination von 1€ und 2€ Münzen, 5€ Scheinen, 10€ Scheinen und "
            + "20€ Scheinen möglich." );
    final Label infoLabel2 = new Label(
        "Einmal eingezahltes Guthaben kann nicht wieder ausgezahlt werden und "
            + "muss durch den Kauf von Getränken aufgebraucht werden." );
    infoLabel1.setFont( new Font( 13 ) );
    infoLabel2.setFont( new Font( 13 ) );
    rightVBox.getChildren().add( infoLabel1 );
    rightVBox.getChildren().add( infoLabel2 );
    final BorderPane borderPane = new BorderPane();
    borderPane.setCenter( rightVBox );
    borderPane.prefHeightProperty().bind( leftVBox.heightProperty() );

    getChildren().add( scrollPane );
    footer.setLeft( leftVBox );
    footer.setRight( borderPane );
    getChildren().add( footer );
    footer.setPadding( new Insets( 5, 10, 10, 15 ) );
  }

  public void updateButtonpanel()
  {
    flowPane.getChildren().clear();
    for ( final Drink drink : Cache.getInstance().getDrinks().values() )
    {
      if ( drink.getAmount() > 0 )
      {
        flowPane.getChildren().add( new DrinkButton( drink ) );
      }
    }
  }

  public void showPriceChangedDialog( Drink drink )
  {
    // TODO(nwe|07.03.2022): Methode muss noch implementiert werden!

  }

  public void showNoMoreDrinksDialog( String value )
  {
    // TODO(nwe|07.03.2022): Methode muss noch implementiert werden!

  }
}
