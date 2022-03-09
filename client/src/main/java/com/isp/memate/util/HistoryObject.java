package com.isp.memate.util;

import java.sql.Date;

import javafx.beans.property.SimpleStringProperty;

public class HistoryObject
{
  private final SimpleStringProperty action;
  private final SimpleStringProperty userName;
  private final SimpleStringProperty price;
  private final SimpleStringProperty newBalance;
  private final SimpleStringProperty date;

  public HistoryObject( final String action, final String userName, final String price, final String newBalance, final String date )
  {
    this.action = new SimpleStringProperty( action );
    this.userName = new SimpleStringProperty( userName );
    this.price = new SimpleStringProperty( price );
    this.newBalance = new SimpleStringProperty( newBalance );
    this.date = new SimpleStringProperty( new Date( Long.valueOf( date ) ).toString() );
  }

  public String getAction()
  {
    return action.get();
  }

  public String getUserName()
  {
    return userName.get();
  }

  public String getPrice()
  {
    return price.get();
  }

  public String getNewBalance()
  {
    return newBalance.get();
  }

  public String getDate()
  {
    return date.get();
  }
}
