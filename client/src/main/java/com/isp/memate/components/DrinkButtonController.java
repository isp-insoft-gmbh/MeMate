package com.isp.memate.components;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

import com.isp.memate.Drink;
import com.isp.memate.ServerCommunication;
import com.isp.memate.util.GUIObjects;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DrinkButtonController implements Initializable
{
  public Label        nameLabel         = null;
  public Label        priceLabel        = null;
  public Label        amountLabel       = null;
  public ImageView    infoIconImageView = null;
  public ImageView    pictureImageView  = null;
  private final Drink drink;

  public DrinkButtonController( Drink drink )
  {
    this.drink = drink;
  }


  @FXML
  public void buttonClicked( Event e )
  {
    ServerCommunication.getInstance().consumeDrink( drink );
    GUIObjects.mainframe.setUndoButtonEnabled( true );
  }


  @Override
  public void initialize( URL location, ResourceBundle resources )
  {
    nameLabel.setText( drink.getName() );
    priceLabel.setText( String.format( "%.2f€", drink.getPrice() ) );
    amountLabel.setText( String.format( "Noch %d Stück", drink.getAmount() ) );
    try
    {
      infoIconImageView.setImage( new Image( new FileInputStream( new File( "assets/icons/info.png" ) ) ) );
      final Image image = new Image( new ByteArrayInputStream( drink.getPictureInBytes() ) );
      pictureImageView.setImage( image );
    }
    catch ( final FileNotFoundException e )
    {
      // TODO(nwe|08.03.2022): Fehlerbehandlung muss noch implementiert werden!
    }
    initializePopUpForInfoIcon();
  }


  private void initializePopUpForInfoIcon()
  {
    // TODO(nwe|11.03.2022): Methode muss noch implementiert werden!

  }
}
