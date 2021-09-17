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
  private final float  fat;
  private final float  fatty_acids;
  private final float  carbs;
  private final float  sugar;
  private final float  protein;
  private final float  salt;
  private final float  amount;

  public DrinkIngredients( final int drinkID, final String ingredients, final int energy_kJ,
                           final int energy_kcal, final float fat, final float fatty_acids,
                           final float carbs, final float sugar, final float protein,
                           final float salt, final float amount )
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

  public DrinkIngredients( final int drinkID, final DrinkIngredients ingredients )
  {
    this.drinkID = drinkID;
    this.ingredients = ingredients.getIngredients();
    this.energy_kJ = ingredients.getEnergy_kJ();
    this.energy_kcal = ingredients.getEnergy_kcal();
    this.fat = ingredients.getFat();
    this.fatty_acids = ingredients.getFatty_acids();
    this.carbs = ingredients.getCarbs();
    this.sugar = ingredients.getSugar();
    this.protein = ingredients.getProtein();
    this.salt = ingredients.getSalt();
    this.amount = ingredients.getAmount();
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

  public float getFat()
  {
    return fat;
  }

  public float getFatty_acids()
  {
    return fatty_acids;
  }

  public float getCarbs()
  {
    return carbs;
  }

  public float getSugar()
  {
    return sugar;
  }

  public float getProtein()
  {
    return protein;
  }

  public float getSalt()
  {
    return salt;
  }

  public float getAmount()
  {
    return amount;
  }
}
