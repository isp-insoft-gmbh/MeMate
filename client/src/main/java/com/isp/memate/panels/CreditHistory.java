package com.isp.memate.panels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import com.isp.memate.Cache;
import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.GUIObjects;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CreditHistory extends HBox
{
  public CreditHistory()
  {
    GUIObjects.currentPanel = this;

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel( "Datum" );
    yAxis.setLabel( "Guthaben in €" );
    final LineChart<String, Number> lineChart = new LineChart<String, Number>( xAxis, yAxis );
    lineChart.setTitle( "Guthabenverlauf (in den letzten 30 Tagen)" );
    lineChart.getData().add( getSeries() );

    HBox.setHgrow( lineChart, Priority.ALWAYS );
    getChildren().add( lineChart );
  }

  private Series<String, Number> getSeries()
  {
    final DateFormat dateFormat = new SimpleDateFormat( "dd.MM HH:mm:ss" );
    final String[][] historyData = Cache.getInstance().getHistory( dateType.LONG );
    final Series<String, Number> series = new Series<String, Number>();
    if ( historyData != null )
    {
      for ( int i = 0; i < historyData.length / 2; i++ )
      {
        final String[] temp = historyData[ i ];
        historyData[ i ] = historyData[ historyData.length - i - 1 ];
        historyData[ historyData.length - i - 1 ] = temp;
      }

      for ( final String[] data : historyData )
      {
        final String action = data[ 0 ];
        if ( action.contains( "Guthaben" ) || action.contains( "getrunken" ) )
        {
          Date date = null;
          final String dateAsString = data[ 4 ];
          date = new Date( Long.valueOf( data[ 4 ] ) );
          final ZonedDateTime today = ZonedDateTime.now();
          final ZonedDateTime thirtyDaysAgo = today.minusDays( 30 );
          final Date eventDate = new Date( Long.valueOf( dateAsString ) );
          if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
          {
            if ( data[ 5 ].equals( "false" ) && !Cache.getInstance().isUserAdmin() )
            {
              series.getData().add( new Data<String, Number>( dateFormat.format( date ),
                  Float.valueOf( data[ 3 ].replace( ",", "." ).substring( 0, data[ 3 ].length() - 1 ) ) ) );
            }
          }
        }
      }
    }
    series.setName( "Guthaben" );
    return series;
  }
}
