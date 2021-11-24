package com.isp.memate.util;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import com.isp.memate.Cache;
import com.isp.memate.Drink;
import com.isp.memate.ServerCommunication;
import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.Shared.Operation;

public class DrinkManagerTableModel extends AbstractTableModel
{
  Drink[]                  drinks;
  private final String[]   columnNames   = { "Bild", "Name", "leer in X Tagen", "Anzahl", "Preis" };
  private final Class<?>[] columnClasses = { ImageIcon.class, String.class, Integer.class, Integer.class, Float.class };

  public DrinkManagerTableModel()
  {
    int counter = 0;
    final Collection<Drink> values = Cache.getInstance().getDrinks().values();
    drinks = new Drink[values.size()];
    for ( final Drink drink : values )
    {
      drinks[ counter ] = drink;
      counter++;
    }
  }

  @Override
  public int getRowCount()
  {
    return drinks.length;
  }

  @Override
  public int getColumnCount()
  {
    return 5;
  }

  @Override
  public String getColumnName( int column )
  {
    return columnNames[ column ];
  }

  @Override
  public Class<?> getColumnClass( int columnIndex )
  {
    return columnClasses[ columnIndex ];
  }

  @Override
  public Object getValueAt( int rowIndex, int columnIndex )
  {
    switch ( columnIndex )
    {
      case 0:
        return drinks[ rowIndex ].getScaledIconForCellRenderer();
      case 1:
        return drinks[ rowIndex ].getName();
      case 2:
        return getDaysLeft( drinks[ rowIndex ] );
      case 3:
        return drinks[ rowIndex ].getAmount();
      case 4:
        return drinks[ rowIndex ].getPrice();

      default :
        return null;
    }
  }

  public Drink getDrinkAt( int rowIndex )
  {
    return drinks[ rowIndex ];
  }

  @Override
  public void setValueAt( Object aValue, int rowIndex, int columnIndex )
  {
    switch ( columnIndex )
    {
      case 0:
        if ( aValue != null )
        {
          if ( !Arrays.equals( drinks[ rowIndex ].getPictureInBytes(), (byte[]) aValue ) )
          {
            drinks[ rowIndex ].setPictureInBytes( (byte[]) aValue );
            ServerCommunication.getInstance().updateDrinkInformations( drinks[ rowIndex ].getId(), Operation.UPDATE_DRINKPICTURE,
                aValue );
          }
        }
        break;
      case 2:
        break;
      case 1:
        if ( !(drinks[ rowIndex ].getName().equals( aValue )) )
        {
          drinks[ rowIndex ].setName( (String) aValue );
          ServerCommunication.getInstance().updateDrinkInformations( drinks[ rowIndex ].getId(), Operation.UPDATE_DRINKNAME,
              aValue );
        }
        break;
      case 3:
        if ( aValue instanceof Number )
        {
          if ( !(drinks[ rowIndex ].getAmount() == (int) aValue) )
          {
            drinks[ rowIndex ].setAmount( (int) aValue );
            ServerCommunication.getInstance().updateDrinkInformations( drinks[ rowIndex ].getId(), Operation.UPDATE_DRINKAMOUNT,
                (int) aValue );
          }
        }
        break;
      case 4:
        if ( aValue instanceof Number )
        {
          final Double valueAsDouble = (Double) aValue;
          final Float value = valueAsDouble.floatValue();
          if ( !(drinks[ rowIndex ].getPrice().equals( value )) )
          {
            drinks[ rowIndex ].setPrice( value );
            ServerCommunication.getInstance().updateDrinkInformations( drinks[ rowIndex ].getId(), Operation.UPDATE_DRINKPRICE,
                value );
          }
        }
        break;
    }
  }

  @Override
  public boolean isCellEditable( int rowIndex, int columnIndex )
  {
    switch ( columnIndex )
    {
      case 0:
        return true;
      case 1:
        return true;
      case 2:
        return false;
      case 3:
        return true;
      case 4:
        return true;
      default :
        return false;
    }
  }

  /**
   * Berechnet zuerst den Durchschnittswert der letzten Monats aus.
   * Nun wird die Anzahl an noch vorhandenen Getränken durch den Wert geteilt.
   */
  private int getDaysLeft( final Drink drink )
  {
    Float amount = 0f;
    final String[][] historyData = Cache.getInstance().getHistory( dateType.LONG );
    if ( historyData != null )
    {
      for ( final String[] data : historyData )
      {
        final String action = data[ 0 ];
        if ( action.contains( "getrunken" ) )
        {
          if ( action.contains( drink.getName() ) )
          {
            final Date date = new Date( Long.valueOf( data[ 4 ] ) );
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime thirtyDaysAgo = now.minusDays( 30 );
            if ( !date.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
            {
              if ( data[ 5 ].equals( "false" ) )
              {
                amount++;
              }
            }
          }
        }
      }
    }
    final Float averageConsumption = amount / 30f;
    return Math.round( drink.getAmount() / averageConsumption );
  }
}
