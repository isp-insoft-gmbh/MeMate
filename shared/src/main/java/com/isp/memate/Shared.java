package com.isp.memate;

import java.io.Serializable;

/**
 * Creates an object that can be shared between client and server.
 * A shared object always contains the command and the corresponding specific object.
 * 
 * @author nwe
 * @since 28.11.2019
 */
public class Shared implements Serializable
{
  private final Operation operation;
  private final Object    value;

  /**
   * @param operation the command to be executed
   * @param value could be drinkName, LoginInformations or something else
   */
  Shared( final Operation operation, final Object value )
  {
    this.operation = operation;
    this.value = value;
  }

  public Operation getOperation()
  {
    return operation;
  }

  public Object getValue()
  {
    return value;
  }

  public enum Operation
  {
    CHECK_LOGIN,
    CHECK_LOGIN_WITH_SESSION_ID,
    REGISTER_USER,
    LOGIN_RESULT,
    LOGIN_WITH_SESSION_ID_RESULT,
    REGISTRATION_RESULT,
    USER_DISPLAYNAME,
    USER_BALANCE,
    IS_ADMIN_USER,
    HISTORY_DATA,
    GET_HISTORY_LAST_5,
    SCOREBOARD,
    WEEKLY_SCOREBOARD,
    GET_DRINKS,
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
    UPDATE_DRINKAMOUNT,
    UPDATE_BARCODE,
    CONNECT_SESSION_ID,
    ADD_BALANCE,
    CONSUM_DRINK,
    SET_PIGGYBANK_BALANCE,
    PIGGYBANK_BALANCE,
    PRICE_CHANGED,
    NO_MORE_DRINKS_AVAIBLE,
    UNDO,
    CHANGE_PASSWORD,
    LOGOUT,
    CHANGE_PASSWORD_USER,
    GET_USERS_DISPLAYNAMES,
    CHANGE_DISPLAYNAME;
  }

  public enum LoginResult
  {
    LOGIN_SUCCESSFULL,
    USER_NOT_FOUND,
    WRONG_PASSWORD,
    LOGIN_SUCCESSFULL_REQUEST_NEW_PASSWORD;
  }
}