/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.io.Serializable;

/**
 * @author nwe
 * @since 06.01.2020
 */
public class DrinkIngredients implements Serializable
{
  private final int    drinkID;
  private final String ingredients;
  private final int    energy_kJ;
  private final int    energy_kcal;
  private final double fat;
  private final double fatty_acids;
  private final double carbs;
  private final double sugar;
  private final double protein;
  private final double salt;
  private final double amount;

  public DrinkIngredients( final int drinkID, final String ingredients, final int energy_kJ, final int energy_kcal, final double fat,
                    final double fatty_acids, final double carbs,
                    final double sugar, final double protein, final double salt, final double amount )
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
    this.amount = amount;
  }

  public int getDrinkID()
  {
    return drinkID;
  }

  public String getIngredients()
  {
    return ingredients;
  }

  public int getEnergy_kJ()
  {
    return energy_kJ;
  }

  public int getEnergy_kcal()
  {
    return energy_kcal;
  }

  public double getFat()
  {
    return fat;
  }

  public double getFatty_acids()
  {
    return fatty_acids;
  }

  public double getCarbs()
  {
    return carbs;
  }

  public double getSugar()
  {
    return sugar;
  }

  public double getProtein()
  {
    return protein;
  }

  public double getSalt()
  {
    return salt;
  }

  public double getAmount()
  {
    return amount;
  }
}
