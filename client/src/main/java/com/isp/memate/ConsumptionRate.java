/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;


/**
 * @author nwe
 * @since 19.12.2019
 *
 */
public class ConsumptionRate extends JPanel
{
  private static final ConsumptionRate instance  = new ConsumptionRate();
  XYDataset                  dataset;
  JFreeChart                 chart;
  ChartPanel                 chartPanel;
  final Map<String, Integer> amountMap = new HashMap<>();

  /**
   * 
   */
  public ConsumptionRate()
  {
    setLayout( new GridBagLayout() );
    setBackground( UIManager.getColor( "TabbedPane.highlight" ) );
  }

  private XYDataset createDataset( String drink )
  {
    amountMap.clear();
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    for ( int i = 0; i < 31; i++ )
    {
      amountMap.put( formatter.format( now.minusDays( i ) ).toString(), 0 );
    }

    String[][] historyData = ServerCommunication.getInstance().getHistoryData( true ).clone();
    for ( String[] data : historyData )
    {
      String action = data[ 0 ];
      if ( action.contains( "getrunken" ) )
      {
        if ( drink.equals( "Alle" ) )
        {
          String date = data[ 4 ];
          amountMap.put( date, amountMap.get( date ) + 1 );
        }
        else if ( action.contains( drink ) )
        {
          String date = data[ 4 ];
          amountMap.put( date, amountMap.get( date ) + 1 );
        }
      }
    }


    final TimeSeries series = new TimeSeries( "DrinksOverTime" );

    for ( int i = 0; i < 31; i++ )
    {
      Day day = new Day( now.minusDays( i ).getDayOfMonth(), now.minusDays( i ).getMonthValue(), now.minusDays( i ).getYear() );
      series.add( day, amountMap.get( formatter.format( now.minusDays( i ) ).toString() ) );
    }
    return new TimeSeriesCollection( series );
  }


  private JFreeChart createChart( final XYDataset dataset )
  {
    return ChartFactory.createTimeSeriesChart(
        "Konsum von Flaschen pro Tag",
        "Datum",
        "Anzahl Getränke",
        dataset,
        false,
        false,
        false );
  }

  /**
   * @return static Instance of Stats
   */
  public static ConsumptionRate getInstance()
  {
    return instance;

  }

  /**
   * 
   */
  public void addGraph()
  {
    removeAll();
    dataset = createDataset( "Alle" );
    chart = createChart( dataset );
    chartPanel = new ChartPanel( chart );
    chartPanel.setPreferredSize( new Dimension( 760, 570 ) );
    chartPanel.setMouseZoomable( true, false );
    GridBagConstraints chartPanelConstraits = new GridBagConstraints();
    chartPanelConstraits.gridx = 0;
    chartPanelConstraits.gridy = 0;
    chartPanelConstraits.gridheight = 3;
    chartPanelConstraits.fill = GridBagConstraints.BOTH;
    chartPanelConstraits.weightx = 1;
    chartPanelConstraits.weighty = 1;
    add( chartPanel, chartPanelConstraits );
    String[] values = new String[ServerCommunication.getInstance().getDrinkNames().size()];
    values = ServerCommunication.getInstance().getDrinkNames().toArray( values );
    JComboBox<String> selectDrinkComboBox = new JComboBox<>( values );
    selectDrinkComboBox.addItem( "Alle" );
    selectDrinkComboBox.setSelectedItem( "Alle" );
    GridBagConstraints selectedDrinkComboboxConstraints = new GridBagConstraints();
    selectedDrinkComboboxConstraints.gridx = 1;
    selectedDrinkComboboxConstraints.gridy = 0;
    selectedDrinkComboboxConstraints.insets = new Insets( 35, 0, 0, 10 );
    selectedDrinkComboboxConstraints.anchor = GridBagConstraints.NORTH;
    add( selectDrinkComboBox, selectedDrinkComboboxConstraints );
    JLabel averageConsumption =
        new JLabel(
            String.format( "Ø %.2f Flaschen/Tag", getAverage() ) );
    GridBagConstraints averageConsumptionConstraints = new GridBagConstraints();
    averageConsumptionConstraints.gridx = 1;
    averageConsumptionConstraints.gridy = 1;
    averageConsumptionConstraints.insets = new Insets( 10, 0, 0, 10 );
    averageConsumptionConstraints.anchor = GridBagConstraints.NORTH;
    add( averageConsumption, averageConsumptionConstraints );


    selectDrinkComboBox.addItemListener( new ItemListener()
    {
      @Override
      public void itemStateChanged( ItemEvent e )
      {
        remove( chartPanel );
        dataset = createDataset( String.valueOf( e.getItem() ) );
        chart = createChart( dataset );
        chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new Dimension( 760, 570 ) );
        chartPanel.setMouseZoomable( true, false );
        averageConsumption
            .setText(
                String.format( "Ø %.2f Flaschen/Tag", getAverage(),
                    String.valueOf( e.getItem() ) ) );
        add( chartPanel, chartPanelConstraits );
        repaint();
        revalidate();
      }
    } );
  }

  /**
   * @return
   */
  private Float getAverage()
  {
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    LocalDateTime now = LocalDateTime.now();
    float counter = 0f;
    for ( int i = 0; i < 31; i++ )
    {
      counter = counter + amountMap.get( formatter.format( now.minusDays( i ) ).toString() );
    }
    return counter / 31f;
  }
}
