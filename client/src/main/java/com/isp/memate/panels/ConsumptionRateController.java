package com.isp.memate.panels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.isp.memate.Cache;
import com.isp.memate.ServerCommunication.dateType;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class ConsumptionRateController
{
  private final ConsumptionRateView                 view;
  private final Map<String, Series<String, Number>> activeSeries = new HashMap<>();

  public ConsumptionRateController( ConsumptionRateView consumptionRateView )
  {
    this.view = consumptionRateView;
    setAvailableDrinks();
    // Add default series
    addSeriesFor( "Alle" );
  }

  private void setAvailableDrinks()
  {
    final Set<String> consumedDrinks = new LinkedHashSet<>();
    consumedDrinks.add( "Alle" );
    final String[][] historyData = Cache.getInstance().getHistory( dateType.SHORT ).clone();
    for ( final String[] data : historyData )
    {
      final String action = data[ 0 ];
      final String date = data[ 4 ];
      final ZonedDateTime today = ZonedDateTime.now();
      final ZonedDateTime thirtyDaysAgo = today.minusDays( 30 );
      final Date eventDate = new Date( Long.valueOf( date ) );
      if ( action.contains( "getrunken" ) && !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
      {
        final String drinkname = action.substring( 0, action.length() - 10 );
        consumedDrinks.add( drinkname );
      }
    }
    view.setAvailableDrinks( consumedDrinks );
  }

  private void addSeriesFor( String drink )
  {
    final Map<String, Integer> amountMap = new HashMap<>();
    final DateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
    final Series<String, Number> series = new Series<String, Number>();
    series.setName( drink );

    final LocalDateTime now = LocalDateTime.now();
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    for ( int i = 0; i < 31; i++ )
    {
      amountMap.put( formatter.format( now.minusDays( i ) ).toString(), 0 );
    }

    final String[][] historyData = Cache.getInstance().getHistory( dateType.SHORT ).clone();
    for ( final String[] data : historyData )
    {
      final String action = data[ 0 ];
      if ( action.contains( "getrunken" ) )
      {
        if ( drink.equals( "Alle" ) )
        {
          final String date = data[ 4 ];
          final ZonedDateTime today = ZonedDateTime.now();
          final ZonedDateTime thirtyDaysAgo = today.minusDays( 30 );
          final Date eventDate = new Date( Long.valueOf( date ) );
          if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
          {
            if ( data[ 5 ].equals( "false" ) )
            {
              amountMap.put( simpleDateFormat.format( eventDate ), amountMap.get( simpleDateFormat.format( eventDate ) ) + 1 );
            }
          }
        }
        else if ( action.contains( drink ) )
        {
          final String date = data[ 4 ];
          final ZonedDateTime today = ZonedDateTime.now();
          final ZonedDateTime thirtyDaysAgo = today.minusDays( 31 );
          final Date eventDate = new Date( Long.valueOf( date ) );
          if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
          {
            if ( data[ 5 ].equals( "false" ) )
            {
              amountMap.put( simpleDateFormat.format( eventDate ), amountMap.get( simpleDateFormat.format( eventDate ) ) + 1 );
            }
          }
        }
      }
    }
    for ( final Entry<String, Integer> entry : amountMap.entrySet() )
    {
      series.getData().add( new Data<String, Number>( entry.getKey(), entry.getValue() ) );
    }
    activeSeries.put( drink, series );
    view.addSeries( series );
  }

  public void onDrinkSelected( String drink )
  {
    addSeriesFor( drink );
  }

  public void onDrinkUnselected( String drink )
  {
    final Series<String, Number> series = activeSeries.get( drink );
    view.removeSeries( series );
    activeSeries.remove( drink );

  }
}
