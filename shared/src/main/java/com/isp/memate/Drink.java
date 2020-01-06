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
public class Drink implements Serializable
{
  String           name;
  Float            price;
  String           picturePath;
  String           pictureInBytes;
  int              id;
  int              amount;
  boolean          ingredients;
  DrinkIngredients drinkIngredients;


  @SuppressWarnings( "javadoc" )
  public Drink( String name, Float price, String picturePath, int id, String pictureInBytes, int amount, boolean ingredients,
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