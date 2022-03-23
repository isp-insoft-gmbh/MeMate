package com.isp.memate.util;

import java.io.ByteArrayInputStream;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;

public class DrinkTableObject
{
  private final Image                 image;
  private final SimpleStringProperty  name;
  private final SimpleIntegerProperty amount;
  private final SimpleStringProperty  price;

  public DrinkTableObject( final byte[] imageInBytes, final String name, final int amount, final Float price )
  {
    this.image = new Image( new ByteArrayInputStream( imageInBytes ) );
    this.name = new SimpleStringProperty( name );
    this.amount = new SimpleIntegerProperty( amount );
    this.price = new SimpleStringProperty( String.format( "%.2f €", price ) );
  }

  public Image getImage()
  {
    return image;
  }

  public String getName()
  {
    return name.get();
  }

  public int getAmount()
  {
    return amount.get();
  }

  public String getPrice()
  {
    return price.get();
  }

}
