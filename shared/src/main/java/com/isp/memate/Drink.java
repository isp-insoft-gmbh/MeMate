/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.Serializable;

/**
 * @author nwe
 * @since 27.11.2019
 *
 */
class Drink implements Serializable
{
  String           name;
  Float            price;
  private String   picturePath;
  byte[]           pictureInBytes;
  int              id;
  int              amount;
  boolean          ingredients;
  DrinkIngredients drinkIngredients;


  Drink( String name, Float price, String picturePath, int id, byte[] pictureInBytes, int amount, boolean ingredients,
         DrinkIngredients drinkIngredients )
  {
    this.name = name;
    this.price = price;
    this.picturePath = picturePath;
    this.pictureInBytes = pictureInBytes;
    this.id = id;
    this.amount = amount;
    this.ingredients = ingredients;
    this.drinkIngredients = drinkIngredients;
  }

}
