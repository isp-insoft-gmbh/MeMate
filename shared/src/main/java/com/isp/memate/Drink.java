/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Image;
import java.io.Serializable;

import javax.swing.ImageIcon;

/**
 * @author nwe
 * @since 27.11.2019
 */
public class Drink implements Serializable
{
  private final int        id;
  private String           barcode;
  private String           name;
  private Float            price;
  private byte[]           pictureInBytes;
  private int              amount;
  private boolean          ingredients;
  private DrinkIngredients drinkIngredients;
  private ImageIcon        icon;
  private ImageIcon        scaledIconForCellRenderer;

  public Drink( final String barcode, final String name, final Float price, final int id, final byte[] pictureInBytes, final int amount,
                final boolean ingredients,
                final DrinkIngredients drinkIngredients )
  {
    this.id = id;
    this.setBarcode( barcode );
    this.setName( name );
    this.setPrice( price );
    this.setAmount( amount );
    this.setIngredients( ingredients );
    this.setDrinkIngredients( drinkIngredients );
    this.setPictureInBytes( pictureInBytes );
  }


  public int getId()
  {
    return id;
  }


  public String getBarcode()
  {
    return barcode;
  }


  public void setBarcode( String barcode )
  {
    this.barcode = barcode;
  }


  public String getName()
  {
    return name;
  }


  public void setName( String name )
  {
    this.name = name;
  }


  public Float getPrice()
  {
    return price;
  }


  public void setPrice( Float price )
  {
    this.price = price;
  }


  public byte[] getPictureInBytes()
  {
    return pictureInBytes;
  }


  public void setPictureInBytes( byte[] pictureInBytes )
  {
    this.pictureInBytes = pictureInBytes;
    this.icon = new ImageIcon( pictureInBytes );
    createScaledIconForCellRenderer();
  }


  public int getAmount()
  {
    return amount;
  }


  public void setAmount( int amount )
  {
    this.amount = amount;
  }


  public boolean isIngredients()
  {
    return ingredients;
  }


  public void setIngredients( boolean ingredients )
  {
    this.ingredients = ingredients;
  }


  public DrinkIngredients getDrinkIngredients()
  {
    return drinkIngredients;
  }


  public void setDrinkIngredients( DrinkIngredients drinkIngredients )
  {
    this.drinkIngredients = drinkIngredients;
  }


  public ImageIcon getIcon()
  {
    return icon;
  }


  public ImageIcon getScaledIconForCellRenderer()
  {
    return scaledIconForCellRenderer;
  }


  public void createScaledIconForCellRenderer()
  {
    if ( icon.getIconHeight() > 140 || icon.getIconHeight() > 150 )
    {
      final double scale = 140.0 / icon.getIconHeight();
      final int height = 140;
      int width = (int) (icon.getIconWidth() * scale);
      if ( width > 150 )
      {
        width = 150;
      }
      final Image scaledImage = icon.getImage().getScaledInstance( width, height, Image.SCALE_SMOOTH );
      this.scaledIconForCellRenderer = new ImageIcon( scaledImage );
    }
    else
    {
      this.scaledIconForCellRenderer = new ImageIcon( icon.getImage().getScaledInstance( 45, 140, Image.SCALE_SMOOTH ) );
    }
  }
}
