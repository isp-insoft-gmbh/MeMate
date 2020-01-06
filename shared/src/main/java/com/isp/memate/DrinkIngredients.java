/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.Serializable;

/**
 * @author nwe
 * @since 06.01.2020
 *
 */
public class DrinkIngredients implements Serializable
{
  int    drinkID;
  String ingredients;
  int    energy_kJ;
  int    energy_kcal;
  double fat;
  double fatty_acids;
  double carbs;
  double sugar;
  double protein;
  double salt;


  @SuppressWarnings( "javadoc" )
  public DrinkIngredients( int drinkID, String ingredients, int energy_kJ, int energy_kcal, double fat, double fatty_acids, double carbs,
                           double sugar, double protein, double salt )
  {
    this.drinkID = drinkID;
    this.ingredients = ingredients;
    this.energy_kJ = energy_kJ;
    this.energy_kcal = energy_kcal;
    this.fat = fat;
    this.fatty_acids = fatty_acids;
    this.carbs = carbs;
    this.sugar = sugar;
    this.protein = protein;
    this.salt = salt;
  }
}
