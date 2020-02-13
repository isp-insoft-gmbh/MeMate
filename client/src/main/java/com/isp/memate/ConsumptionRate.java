/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;


/**
 * In diesem Panel kann der Nutzer sein Konsum von Flaschen pro Tag im letzten Monat anschauen.
 * Man hat die Möglichkeit zwischen verschiedenen Getränken zu filtern oder sich alle
 * an zu schauen. Der Admin sieht hier die Daten von allen Usern.
 * 
 * @author nwe
 * @since 19.12.2019
 *
 */
public class ConsumptionRate extends JPanel
{
  private final Map<String, Integer> amountMap      = new HashMap<>();
  private XYDataset                  dataset;
  private JFreeChart                 chart;
  private ChartPanel                 chartPanel;
  private JComboBox<String>          selectDrinkComboBox;
  private HashSet<String>            consumedDrinks = new LinkedHashSet<>();


  /**
   * Setzt das Layout
   */
  public ConsumptionRate()
  {
    setLayout( new GridBagLayout() );
  }

  /**
   * Erstellt einen neuen Datensatz für das angegebene Getränk.
   * 
   * @param drink Name des Getränks.
   * @return Datensatz für den Graphen.
   */
  private XYDataset createDataset( String drink )
  {
    amountMap.clear();
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    for ( int i = 0; i < 31; i++ )
    {
      amountMap.put( formatter.format( now.minusDays( i ) ).toString(), 0 );
    }

    String[][] historyData = ServerCommunication.getInstance().getHistoryData( dateType.SHORT ).clone();
    for ( String[] data : historyData )
    {
      String action = data[ 0 ];
      if ( action.contains( "getrunken" ) )
      {
        String drinkname = action.substring( 0, action.length() - 10 );
        consumedDrinks.add( drinkname );
        if ( drink.equals( "Alle" ) )
        {
          String date = data[ 4 ];
          ZonedDateTime today = ZonedDateTime.now();
          ZonedDateTime thirtyDaysAgo = today.minusDays( 30 );
          try
          {
            Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( date );
            if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
            {
              amountMap.put( date, amountMap.get( date ) + 1 );
            }
          }
          catch ( ParseException exception )
          {
            ClientLog.newLog( "Das Datum ist out of range." + exception );
          }
        }
        else if ( action.contains( drink ) )
        {
          String date = data[ 4 ];
          ZonedDateTime today = ZonedDateTime.now();
          ZonedDateTime thirtyDaysAgo = today.minusDays( 31 );
          try
          {
            Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( date );
            if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
            {
              amountMap.put( date, amountMap.get( date ) + 1 );
            }
          }
          catch ( ParseException exception )
          {
            ClientLog.newLog( "Das Datum ist out of range." + exception );
          }
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

  /**
   * Erzeugt aus dem Datensatz einen neuen Graphen und lädt
   * anschließend noch Settings, abhängig vom State des Darkmodes.
   * 
   * @param dataset Datensatz
   * @return chart
   */
  private JFreeChart createChart( final XYDataset dataset )
  {
    JFreeChart freeChart = ChartFactory.createTimeSeriesChart(
        "Konsum von Flaschen pro Tag",
        "Datum",
        "Anzahl Getränke",
        dataset,
        false,
        false,
        false );

    freeChart.getXYPlot().getRenderer().setSeriesPaint( 0, UIManager.getColor( "AppColor" ) );
    freeChart.getXYPlot().getRangeAxis().setStandardTickUnits( NumberAxis.createIntegerTickUnits() );

    if ( MeMateUIManager.getDarkModeState() )
    {
      freeChart.setBackgroundPaint( MeMateUIManager.getBackground( "default" ).getDarkColor() );
      freeChart.getTitle().setPaint( Color.white );
      freeChart.getXYPlot().setBackgroundPaint( new Color( 36, 43, 55 ).brighter() );
      freeChart.getXYPlot().setDomainGridlinesVisible( false );
      freeChart.getXYPlot().setRangeGridlinesVisible( false );
      freeChart.getXYPlot().getDomainAxis().setTickLabelPaint( Color.white );
      freeChart.getXYPlot().getRangeAxis().setTickLabelPaint( Color.white );
      freeChart.getXYPlot().getDomainAxis().setLabelPaint( Color.white );
      freeChart.getXYPlot().getRangeAxis().setLabelPaint( Color.white );
    }
    else
    {
      freeChart.setBackgroundPaint( MeMateUIManager.getBackground( "default" ).getDayColor() );
      freeChart.getXYPlot().setBackgroundPaint( new Color( 192, 192, 192 ) );
      freeChart.getXYPlot().setDomainGridlinesVisible( false );
      freeChart.getXYPlot().setRangeGridlinesVisible( false );
      freeChart.getTitle().setPaint( Color.black );
      freeChart.getXYPlot().getDomainAxis().setTickLabelPaint( Color.black );
      freeChart.getXYPlot().getRangeAxis().setTickLabelPaint( Color.black );
      freeChart.getXYPlot().getDomainAxis().setLabelPaint( Color.black );
      freeChart.getXYPlot().getRangeAxis().setLabelPaint( Color.black );
    }
    return freeChart;
  }

  /**
   * Zuerst wird ein neuer Datensatz generiert und anhand davon das neue chartPanel.
   * Außerdem wird das Layout gesetzt, einige Settings geladen und Component- und ItemListener angemeldet.
   */
  public void addGraph()
  {
    removeAll();
    dataset = createDataset( "Alle" );
    chart = createChart( dataset );
    chartPanel = new ChartPanel( chart );
    chartPanel.setPreferredSize( new Dimension( 760, 570 ) );
    chartPanel.setMouseZoomable( true, false );
    GridBagConstraints chartPanelConstraits = getChartPanelConstraits();
    add( chartPanel, chartPanelConstraits );

    addDrinkComboBoxItems();

    GridBagConstraints selectedDrinkComboboxConstraints = getSelectedDrinkComboboxConstraits();
    add( selectDrinkComboBox, selectedDrinkComboboxConstraints );

    JLabel averageConsumption = new JLabel( String.format( "Ø %.2f Flaschen/Tag", getAverage() ) );
    GridBagConstraints averageConsumptionConstraints = getAverageConsumptionConstraints();
    add( averageConsumption, averageConsumptionConstraints );

    toggleDarkMode( averageConsumption );
    appendComponentListener();

    selectDrinkComboBox.addItemListener( new ItemListener()
    {
      @Override
      public void itemStateChanged( ItemEvent e )
      {
        remove( chartPanel );
        dataset = createDataset( String.valueOf( e.getItem() ) );
        chart = createChart( dataset );
        chartPanel.setChart( chart );
        averageConsumption.setText( String.format( "Ø %.2f Flaschen/Tag", getAverage(), String.valueOf( e.getItem() ) ) );
        add( chartPanel, chartPanelConstraits );
        repaint();
        revalidate();
      }
    } );
  }

  /**
   * Fügt einen {@link ComponentListener} hinzu, damit sich die Chart beim Verkleinern/Vergrößern anpasst.
   */
  private void appendComponentListener()
  {
    chartPanel.setMaximumDrawHeight( 1000 );
    chartPanel.setMaximumDrawWidth( 1000 );
    chartPanel.setMinimumDrawWidth( 10 );
    chartPanel.setMinimumDrawHeight( 10 );

    ComponentListener rateResizeListener = new ComponentAdapter()
    {
      @Override
      public void componentResized( final ComponentEvent e )
      {
        chartPanel.setMaximumDrawHeight( e.getComponent().getHeight() );
        chartPanel.setMaximumDrawWidth( e.getComponent().getWidth() );
        chartPanel.setMinimumDrawWidth( e.getComponent().getWidth() );
        chartPanel.setMinimumDrawHeight( e.getComponent().getHeight() );
      }
    };

    try
    {
      Mainframe.getInstance().removeComponentListener( rateResizeListener );
    }
    catch ( Exception exception )
    {
      ClientLog.newLog( "Der ComponentListener konnte nicht entfernt werden." );
      ClientLog.newLog( exception.getMessage() );
    }
    Mainframe.getInstance().addComponentListener( rateResizeListener );
  }

  /**
   * Fügt jedes bisher getrunkene Getränk in die Combobox hinzu.
   */
  private void addDrinkComboBoxItems()
  {
    selectDrinkComboBox = new JComboBox<>();
    selectDrinkComboBox.addItem( "Alle" );
    selectDrinkComboBox.setSelectedItem( "Alle" );
    for ( String string : consumedDrinks )
    {
      selectDrinkComboBox.addItem( string );
    }
  }


  /**
   * Teilt die Anzahl der kosumierten Getränke des letzen Monats durch 31.
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


  private void toggleDarkMode( JLabel averageConsumption )
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      setBackground( MeMateUIManager.getBackground( "default" ).getDarkColor() );
      averageConsumption.setForeground( MeMateUIManager.getForeground( "default" ).getDarkColor() );
    }
    else
    {
      setBackground( MeMateUIManager.getBackground( "default" ).getDayColor() );
      averageConsumption.setForeground( MeMateUIManager.getForeground( "default" ).getDayColor() );
    }
  }

  private GridBagConstraints getAverageConsumptionConstraints()
  {
    GridBagConstraints averageConsumptionConstraints = new GridBagConstraints();
    averageConsumptionConstraints.gridx = 1;
    averageConsumptionConstraints.gridy = 1;
    averageConsumptionConstraints.insets = new Insets( 10, 0, 0, 10 );
    averageConsumptionConstraints.anchor = GridBagConstraints.NORTH;
    return averageConsumptionConstraints;
  }

  private GridBagConstraints getSelectedDrinkComboboxConstraits()
  {
    GridBagConstraints selectedDrinkComboboxConstraints = new GridBagConstraints();
    selectedDrinkComboboxConstraints.gridx = 1;
    selectedDrinkComboboxConstraints.gridy = 0;
    selectedDrinkComboboxConstraints.insets = new Insets( 35, 0, 0, 10 );
    selectedDrinkComboboxConstraints.anchor = GridBagConstraints.NORTH;
    return selectedDrinkComboboxConstraints;
  }

  private GridBagConstraints getChartPanelConstraits()
  {
    GridBagConstraints chartPanelConstraits = new GridBagConstraints();
    chartPanelConstraits.gridx = 0;
    chartPanelConstraits.gridy = 0;
    chartPanelConstraits.gridheight = 3;
    chartPanelConstraits.fill = GridBagConstraints.BOTH;
    chartPanelConstraits.weightx = 1;
    chartPanelConstraits.weighty = 1;
    return chartPanelConstraits;
  }
}
