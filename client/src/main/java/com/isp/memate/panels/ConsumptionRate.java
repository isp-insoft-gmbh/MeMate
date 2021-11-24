/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DateFormat;
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

import com.isp.memate.Cache;
import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.GUIObjects;

/**
 * This panel allows the user to see his consumption of bottles per day in the last month.
 * It has the possibility to filter between different drinks or to look at all of them.
 * The admin can see the data of all users in order to see the overall consumption.
 *
 * @author nwe
 * @since 19.12.2019
 */
public class ConsumptionRate extends JPanel
{
  private final Map<String, Integer> amountMap        = new HashMap<>();
  private ChartPanel                 chartPanel;
  private final HashSet<String>      consumedDrinks   = new LinkedHashSet<>();
  private final DateFormat           simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

  public ConsumptionRate()
  {
    GUIObjects.currentPanel = this;
    setLayout( new GridBagLayout() );
    addChartPanel();
    setDrawRange();
    addResizeListener();
  }

  /**
   * Adds a new created chart that displays all drinks to the chartpanel.
   * And the selectedDrinkCombobox and averageConsumptionLabel get initialized and added.
   */
  void addChartPanel()
  {
    final JFreeChart chart = createChart( createDataset( "Alle" ) );
    chartPanel = new ChartPanel( chart );
    chartPanel.setPreferredSize( new Dimension( 760, 570 ) );
    chartPanel.setMouseZoomable( true, false );

    final JComboBox<String> selectDrinkComboBox = getSelectedDrinkComboBox();
    final JLabel averageConsumption = new JLabel();
    averageConsumption.setText( String.format( "Ø %.2f Flaschen/Tag", getAverage() ) );

    add( chartPanel, getChartPanelConstraits() );
    add( selectDrinkComboBox, getSelectedDrinkComboboxConstraits() );
    add( averageConsumption, getAverageConsumptionConstraints() );

    selectDrinkComboBox.addItemListener( e ->
    {
      remove( chartPanel );
      chartPanel.setChart( createChart( createDataset( String.valueOf( e.getItem() ) ) ) );
      averageConsumption.setText( String.format( "Ø %.2f Flaschen/Tag", getAverage(), String.valueOf( e.getItem() ) ) );
      add( chartPanel, getChartPanelConstraits() );
      repaint();
      revalidate();
    } );
  }

  private JComboBox<String> getSelectedDrinkComboBox()
  {
    final JComboBox<String> selectDrinkComboBox = new JComboBox<>();
    //Needed because otherwise very long drinknames would cause a really wide ComboBox.
    selectDrinkComboBox.setPrototypeDisplayValue( "This is my maximal lenght" );
    selectDrinkComboBox.addItem( "Alle" );
    selectDrinkComboBox.setSelectedItem( "Alle" );
    for ( final String string : consumedDrinks )
    {
      selectDrinkComboBox.addItem( string );
    }
    return selectDrinkComboBox;
  }


  /**
   * Creates a new dataset for the given drink.
   *
   * @param drink Drink name for which the dataset is to be created.
   * @return the created dataset.
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
    final TimeSeries series = new TimeSeries( "DrinksOverTime" );

    for ( int i = 0; i < 31; i++ )
    {
      final Day day = new Day( now.minusDays( i ).getDayOfMonth(), now.minusDays( i ).getMonthValue(), now.minusDays( i ).getYear() );
      series.add( day, amountMap.get( formatter.format( now.minusDays( i ) ).toString() ) );
    }
    return new TimeSeriesCollection( series );
  }

  /**
   * Creates a new chart with the given dataset.
   *
   * @param dataset the needed dataset for the chart.
   * @return created chart.
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

    customizeChart( freeChart );
    return freeChart;
  }

  private void customizeChart( JFreeChart chart )
  {
    final XYPlot plot = chart.getXYPlot();
    final DateAxis dateAxis = new DateAxis();
    dateAxis.setDateFormatOverride( new SimpleDateFormat( "dd.MM" ) );
    dateAxis.setLabel( "Datum" );
    dateAxis.setTickLabelFont( UIManager.getFont( "Label.font" ) );
    dateAxis.setLabelFont( UIManager.getFont( "Label.font" ).deriveFont( 14f ) );
    plot.setDomainAxis( dateAxis );
    chart.getXYPlot().getRenderer().setSeriesPaint( 0, UIManager.getColor( "AppColor" ) );
    chart.getXYPlot().getRangeAxis().setStandardTickUnits( NumberAxis.createIntegerTickUnits() );

    final Color foreground = UIManager.getColor( "Label.foreground" );
    chart.setBackgroundPaint( UIManager.getColor( "Panel.background" ) );
    chart.getXYPlot().setBackgroundPaint( UIManager.getColor( "TextField.background" ) );
    chart.getTitle().setPaint( foreground );
    chart.getXYPlot().getDomainAxis().setTickLabelPaint( foreground );
    chart.getXYPlot().getRangeAxis().setTickLabelPaint( foreground );
    chart.getXYPlot().getDomainAxis().setLabelPaint( foreground );
    chart.getXYPlot().getRangeAxis().setLabelPaint( foreground );
    chart.getXYPlot().setDomainGridlinesVisible( false );
    chart.getXYPlot().setRangeGridlinesVisible( false );
  }


  /**
   * Calculates the average consumption in the last 31 days.
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

  private void setDrawRange()
  {
    chartPanel.setMaximumDrawHeight( GUIObjects.mainframe.getHeight() );
    chartPanel.setMaximumDrawWidth( GUIObjects.mainframe.getWidth() );
    chartPanel.setMinimumDrawWidth( GUIObjects.mainframe.getWidth() );
    chartPanel.setMinimumDrawHeight( GUIObjects.mainframe.getHeight() );
  }

  private void addResizeListener()
  {
    addComponentListener( new ComponentAdapter()
    {
      @Override
      public void componentResized( final ComponentEvent e )
      {
        setDrawRange();
      }
    } );
  }
}