/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;


/**
 * In diesem Panel kann der Nutzer sein Konsum von Flaschen pro Tag im letzten Monat anschauen.
 * Man hat die Möglichkeit zwischen verschiedenen Getränken zu filtern oder sich alle
 * an zu schauen. Der Admin sieht hier die Daten von allen Usern.
 *
 * @author nwe
 * @since 19.12.2019
 */
class ConsumptionRate extends JPanel
{
  private final Map<String, Integer> amountMap      = new HashMap<>();
  private XYDataset                  dataset;
  private JFreeChart                 chart;
  private ChartPanel                 chartPanel;
  private JComboBox<String>          selectDrinkComboBox;
  private final HashSet<String>      consumedDrinks = new LinkedHashSet<>();

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
  private XYDataset createDataset( final String drink )
  {
    amountMap.clear();
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
        final String drinkname = action.substring( 0, action.length() - 10 );
        consumedDrinks.add( drinkname );
        if ( drink.equals( "Alle" ) )
        {
          final String date = data[ 4 ];
          final ZonedDateTime today = ZonedDateTime.now();
          final ZonedDateTime thirtyDaysAgo = today.minusDays( 30 );
          try
          {
            final Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( date );
            if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
            {
              if ( data[ 5 ].equals( "false" ) )
              {
                amountMap.put( date, amountMap.get( date ) + 1 );
              }
            }
          }
          catch ( final ParseException exception )
          {
            ClientLog.newLog( "Das Datum ist out of range." + exception );
          }
        }
        else if ( action.contains( drink ) )
        {
          final String date = data[ 4 ];
          final ZonedDateTime today = ZonedDateTime.now();
          final ZonedDateTime thirtyDaysAgo = today.minusDays( 31 );
          try
          {
            final Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( date );
            if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
            {
              if ( data[ 5 ].equals( "false" ) )
              {
                amountMap.put( date, amountMap.get( date ) + 1 );
              }
            }
          }
          catch ( final ParseException exception )
          {
            ClientLog.newLog( "Das Datum ist out of range." + exception );
          }
        }
      }
    }
    final TimeSeries series = new TimeSeries( "DrinksOverTime" );

    for ( int i = 0; i < 31; i++ )
    {
      final Day day = new Day( now.minusDays( i ).getDayOfMonth(), now.minusDays( i ).getMonthValue(), now.minusDays( i ).getYear() );
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
    final JFreeChart freeChart = ChartFactory.createTimeSeriesChart(
        "Konsum von Flaschen pro Tag (in den letzen 30 Tagen)",
        "Datum",
        "Anzahl Getränke",
        dataset,
        false,
        false,
        false );

    final XYPlot plot = freeChart.getXYPlot();
    final DateAxis dateAxis = new DateAxis();
    dateAxis.setDateFormatOverride( new SimpleDateFormat( "dd.MM" ) );
    dateAxis.setLabel( "Datum" );
    dateAxis.setTickLabelFont( new Font( "Tahoma", Font.PLAIN, 12 ) );
    dateAxis.setLabelFont( new Font( "Tahoma", Font.BOLD, 14 ) );
    plot.setDomainAxis( dateAxis );
    freeChart.getXYPlot().getRenderer().setSeriesPaint( 0, UIManager.getColor( "AppColor" ) );
    freeChart.getXYPlot().getRangeAxis().setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
    //FIXME(nwe | 27.01.2021): 
    //    MeMateUIManager.registerFreeChart( freeChart );
    return freeChart;
  }

  /**
   * Zuerst wird ein neuer Datensatz generiert und anhand davon das neue chartPanel.
   * Außerdem wird das Layout gesetzt, einige Settings geladen und Component- und ItemListener angemeldet.
   */
  void addGraph()
  {
    removeAll();
    dataset = createDataset( "Alle" );
    chart = createChart( dataset );
    chartPanel = new ChartPanel( chart );
    chartPanel.setPreferredSize( new Dimension( 760, 570 ) );
    chartPanel.setMouseZoomable( true, false );
    final GridBagConstraints chartPanelConstraits = getChartPanelConstraits();
    add( chartPanel, chartPanelConstraits );

    addDrinkComboBoxItems();

    final GridBagConstraints selectedDrinkComboboxConstraints = getSelectedDrinkComboboxConstraits();
    add( selectDrinkComboBox, selectedDrinkComboboxConstraints );

    final JLabel averageConsumption = new JLabel();
    averageConsumption.setText( String.format( "Ø %.2f Flaschen/Tag", getAverage() ) );
    final GridBagConstraints averageConsumptionConstraints = getAverageConsumptionConstraints();
    add( averageConsumption, averageConsumptionConstraints );

    appendComponentListener();

    selectDrinkComboBox.addItemListener( e ->
    {
      remove( chartPanel );
      dataset = createDataset( String.valueOf( e.getItem() ) );
      chart = createChart( dataset );
      chartPanel.setChart( chart );
      averageConsumption.setText( String.format( "Ø %.2f Flaschen/Tag", getAverage(), String.valueOf( e.getItem() ) ) );
      add( chartPanel, chartPanelConstraits );
      repaint();
      revalidate();
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

    final ComponentListener rateResizeListener = new ComponentAdapter()
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
      GUIObjects.mainframe.removeComponentListener( rateResizeListener );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Der ComponentListener konnte nicht entfernt werden." );
      ClientLog.newLog( exception.getMessage() );
    }
    GUIObjects.mainframe.addComponentListener( rateResizeListener );
  }

  /**
   * Fügt jedes bisher getrunkene Getränk in die Combobox hinzu.
   */
  private void addDrinkComboBoxItems()
  {
    selectDrinkComboBox = new JComboBox<>();
    //Needed because otherwise very long drinknames would cause a realy wide ComboBox.
    selectDrinkComboBox.setPrototypeDisplayValue( "This is my maximal lenght" );
    selectDrinkComboBox.addItem( "Alle" );
    selectDrinkComboBox.setSelectedItem( "Alle" );
    for ( final String string : consumedDrinks )
    {
      selectDrinkComboBox.addItem( string );
    }
  }


  /**
   * Teilt die Anzahl der kosumierten Getränke des letzen Monats durch 31.
   */
  private Float getAverage()
  {
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    final LocalDateTime now = LocalDateTime.now();
    float counter = 0f;
    for ( int i = 0; i < 31; i++ )
    {
      counter = counter + amountMap.get( formatter.format( now.minusDays( i ) ).toString() );
    }
    return counter / 31f;
  }

  private GridBagConstraints getAverageConsumptionConstraints()
  {
    final GridBagConstraints averageConsumptionConstraints = new GridBagConstraints();
    averageConsumptionConstraints.gridx = 1;
    averageConsumptionConstraints.gridy = 1;
    averageConsumptionConstraints.insets = new Insets( 10, 0, 0, 10 );
    averageConsumptionConstraints.anchor = GridBagConstraints.NORTH;
    return averageConsumptionConstraints;
  }

  private GridBagConstraints getSelectedDrinkComboboxConstraits()
  {
    final GridBagConstraints selectedDrinkComboboxConstraints = new GridBagConstraints();
    selectedDrinkComboboxConstraints.gridx = 1;
    selectedDrinkComboboxConstraints.gridy = 0;
    selectedDrinkComboboxConstraints.insets = new Insets( 35, 0, 0, 10 );
    selectedDrinkComboboxConstraints.anchor = GridBagConstraints.NORTH;
    return selectedDrinkComboboxConstraints;
  }

  private GridBagConstraints getChartPanelConstraits()
  {
    final GridBagConstraints chartPanelConstraits = new GridBagConstraints();
    chartPanelConstraits.gridx = 0;
    chartPanelConstraits.gridy = 0;
    chartPanelConstraits.gridheight = 3;
    chartPanelConstraits.fill = GridBagConstraints.BOTH;
    chartPanelConstraits.weightx = 1;
    chartPanelConstraits.weighty = 1;
    return chartPanelConstraits;
  }
}
