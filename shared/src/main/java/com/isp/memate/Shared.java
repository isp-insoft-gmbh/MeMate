package com.isp.memate;

import java.io.Serializable;

/**
 * Erzeugt ein Objekt, welches der Client als auch der Server verwenden kann.
 * Ein Shared-Objekt enthält immer erst den Befehl, welcher ausgeführt werden soll
 * und dahinter ein weiteres spezfisches Objekt.
 * 
 * @author nwe
 * @since 28.11.2019
 */
class Shared implements Serializable
{
  User             user             = null;
  LoginInformation loginInformation = null;
  Drink            drink            = null;
  DrinkName        drinkName        = null;
  DrinkPrice       drinkPrice       = null;
  DrinkPicture     drinkPicture     = null;
  DrinkAmount      drinkAmount      = null;
  DrinkIngredients drinkIngredients = null;
  Drink[]          drinkInfos       = null;
  String[][]       history;
  String[][]       shortHistory;
  String[][]       scoreboard;
  String[]         users;
  String[]         displaynames;
  User[]           fullUserArray;
  Float            userBalance;
  String           displayname;
  String           consumedDrink;
  String           registrationResult;
  String           username;
  String           userSessionID;
  String           version;
  String           sessionID;
  String           pass;
  int              balanceToAdd;
  Operation        operation;
  LoginResult      loginResult;

  /**
   * @param operation der auszuführende Befehl
   * @param object Kann Getränkename, Login-Informationen oder alles andere sein.
   */
  Shared( Operation operation, Object object )
  {
    this.operation = operation;
    switch ( operation )
    {
      case REGISTER_USER:
        user = (User) object;
        break;
      case CHECK_LOGIN:
        loginInformation = (LoginInformation) object;
        break;
      case GET_BALANCE:
        username = (String) object;
        break;
      case GET_BALANCE_RESULT:
        userBalance = (Float) object;
        break;
      case GET_DRINKINFO:
        drinkInfos = (Drink[]) object;
        break;
      case GET_HISTORY:
        history = (String[][]) object;
        break;
      case GET_HISTORY_LAST_5:
        shortHistory = (String[][]) object;
        break;
      case GET_SCOREBOARD:
        scoreboard = (String[][]) object;
        break;
      case REGISTER_DRINK:
        drink = (Drink) object;
        break;
      case REGISTER_INGREDIENTS:
        drinkIngredients = (DrinkIngredients) object;
        break;
      case REMOVE_DRINK:
        drink = (Drink) object;
        break;
      case UPDATE_DRINKNAME:
        drinkName = (DrinkName) object;
        break;
      case UPDATE_DRINKPRICE:
        drinkPrice = (DrinkPrice) object;
        break;
      case UPDATE_DRINKPICTURE:
        drinkPicture = (DrinkPicture) object;
        break;
      case CONNECT_SESSION_ID:
        sessionID = (String) object;
        break;
      case GET_USERNAME_FOR_SESSION_ID:
        userSessionID = (String) object;
        break;
      case GET_USERNAME_FOR_SESSION_ID_RESULT:
        username = (String) object;
        break;
      case ADD_BALANCE:
        balanceToAdd = (int) object;
        break;
      case CONSUM_DRINK:
        drinkPrice = (DrinkPrice) object;
        break;
      case LOGIN_RESULT:
        loginResult = (LoginResult) object;
        break;
      case REGISTRATION_RESULT:
        registrationResult = (String) object;
        break;
      case PRICE_CHANGED:
        drinkPrice = (DrinkPrice) object;
        break;
      case SET_PIGGYBANK_BALANCE:
        userBalance = (Float) object;
        break;
      case PIGGYBANK_BALANCE:
        userBalance = (Float) object;
        break;
      case NO_MORE_DRINKS_AVAIBLE:
        consumedDrink = (String) object;
        break;
      case SET_DRINK_AMOUNT:
        drinkAmount = (DrinkAmount) object;
        break;
      case UNDO:
        break;
      case LOGOUT:
        break;
      case GET_USERS:
        break;
      case GET_USERS_RESULT:
        users = (String[]) object;
        break;
      case GET_USERS_DISPLAYNAMES:
        displaynames = (String[]) object;
        break;
      case GET_DISPLAYNAME:
        displayname = (String) object;
        break;
      case GET_FULLUSERS_RESULT:
        fullUserArray = (User[]) object;
        break;
      case CHANGE_PASSWORD:
        user = (User) object;
        break;
      case CHANGE_PASSWORD_USER:
        pass = (String) object;
        break;
      case CHANGE_DISPLAYNAME:
        displayname = (String) object;
        break;
      case GET_VERSION:
        version = (String) object;
        break;
      default :
        break;
    }
  }

  enum Operation
  {
    REGISTER_USER,
    CHECK_LOGIN,
    GET_BALANCE,
    GET_BALANCE_RESULT,
    GET_HISTORY,
    GET_HISTORY_LAST_5,
    GET_SCOREBOARD,
    GET_DRINKINFO,
    GET_USERS,
    GET_USERS_RESULT,
    GET_FULLUSERS_RESULT,
    GET_VERSION,
    REGISTER_DRINK,
    REGISTER_INGREDIENTS,
    REMOVE_DRINK,
    UPDATE_DRINKNAME,
    UPDATE_DRINKPRICE,
    UPDATE_DRINKPICTURE,
    CONNECT_SESSION_ID,
    GET_USERNAME_FOR_SESSION_ID,
    GET_USERNAME_FOR_SESSION_ID_RESULT,
    ADD_BALANCE,
    LOGIN_RESULT,
    REGISTRATION_RESULT,
    CONSUM_DRINK,
    SET_PIGGYBANK_BALANCE,
    PIGGYBANK_BALANCE,
    PRICE_CHANGED,
    NO_MORE_DRINKS_AVAIBLE,
    SET_DRINK_AMOUNT,
    UNDO,
    CHANGE_PASSWORD,
    LOGOUT,
    CHANGE_PASSWORD_USER,
    GET_USERS_DISPLAYNAMES,
    GET_DISPLAYNAME,
    CHANGE_DISPLAYNAME;
  }

  enum LoginResult
  {
    LOGIN_SUCCESSFULL,
    USER_NOT_FOUND,
    WRONG_PASSWORD,
    LOGIN_SUCCESSFULL_REQUEST_NEW_PASSWORD;
  }
}