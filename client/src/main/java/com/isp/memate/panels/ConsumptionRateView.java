package com.isp.memate.panels;

import java.util.Set;

import com.isp.memate.util.GUIObjects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ConsumptionRateView extends HBox
{
  private final ConsumptionRateController controller;
  private final LineChart<String, Number> lineChart;
  private final VBox                      checkBoxList;

  public ConsumptionRateView()
  {
    GUIObjects.currentPanel = this;

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel( "Datum" );
    yAxis.setLabel( "Anzahl Getränke" );
    lineChart = new LineChart<String, Number>( xAxis, yAxis );
    lineChart.setTitle( "Konsum von Flaschen pro Tag (in den letzten 30 Tagen)" );

    checkBoxList = new VBox();
    checkBoxList.setPadding( new Insets( 40, 20, 0, 10 ) );

    HBox.setHgrow( lineChart, Priority.ALWAYS );
    HBox.setHgrow( checkBoxList, Priority.SOMETIMES );
    getChildren().add( lineChart );
    getChildren().add( checkBoxList );

    this.controller = new ConsumptionRateController( this );
  }

  public void addSeries( final Series<String, Number> series )
  {
    lineChart.getData().add( series );
  }

  public void removeSeries( Series<String, Number> series )
  {
    lineChart.getData().remove( series );
  }

  public void setAvailableDrinks( Set<String> consumedDrinks )
  {
    for ( final String drink : consumedDrinks )
    {
      final CheckBox checkBox = new CheckBox( drink );
      if ( drink.equals( "Alle" ) )
      {
        checkBox.setSelected( true );
      }
      checkBox.setOnAction( new EventHandler<ActionEvent>()
      {
        @Override
        public void handle( ActionEvent e )
        {
          if ( checkBox.isSelected() )
          {
            controller.onDrinkSelected( drink );
          }
          else
          {
            controller.onDrinkUnselected( drink );
          }
        }
      } );
      checkBoxList.getChildren().add( checkBox );
    }
  }
}
