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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXMonthView;
import org.jdesktop.swingx.calendar.DateSelectionModel.SelectionMode;
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
import com.isp.memate.util.MeMateUIManager;


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
  private XYDataset                  dataset;
  private JFreeChart                 chart;
  private final Map<String, Integer> amountMap          = new HashMap<>();
  private final JPanel               pickerPanel        = MeMateUIManager.createJPanel();
  private final JXMonthView          monthView          = new JXMonthView();
  final JLabel                       averageConsumption = MeMateUIManager.createJLabel();
  private JSpinner                   spinnerTimeFrom;
  private JSpinner                   spinnerTimeTo;
  private JButton                    currentDateButton;
  private ChartPanel                 chartPanel;
  private JComboBox<String>          selectDrinkComboBox;
  private String                     selectedDrink      = "Alle";
  private Date                       startDate;
  private Date                       endDate;

  /**
   * Setzt das Layout
   */
  public ConsumptionRate()
  {
    setDefaultDates();
    initComponents();
    addComponents();
    addListener();
    setLayout( new GridBagLayout() );
  }

  private void addComponents()
  {
    add( selectDrinkComboBox, getSelectedDrinkComboboxConstraits() );
    add( chartPanel, getChartPanelConstraits() );
    add( pickerPanel, getDateRangePickerConstraits() );
    add( averageConsumption, getAverageConsumptionConstraints() );
  }

  private void setDefaultDates()
  {
    final ZonedDateTime today = ZonedDateTime.now();
    final ZonedDateTime thirtyDaysAgo = today.minusDays( 30 );
    startDate = Date.from( thirtyDaysAgo.toInstant() );
    endDate = Date.from( today.toInstant() );
  }

  public void initComponents()
  {
    initComboBox();
    initPickerPanel();
    initMonthView();
    initChart();
    averageConsumption.setText( String.format( "Ø %.2f Flaschen/Tag", getAverage() ) );
  }

  private void initChart()
  {
    dataset = createDataset();
    chart = createChart( dataset );
    chartPanel = new ChartPanel( chart );
    chartPanel.setPreferredSize( new Dimension( 760, 570 ) );
    chartPanel.setMouseZoomable( true, false );
    MeMateUIManager.registerPanel( "default", this );
  }

  public void initComboBox()
  {
    selectDrinkComboBox = new JComboBox<>();
    //Needed because otherwise very long drinknames would cause a realy wide ComboBox.
    selectDrinkComboBox.setPrototypeDisplayValue( "This is my maximal length" );
    selectDrinkComboBox.addItem( "Alle" );
    selectDrinkComboBox.setSelectedItem( "Alle" );
    MeMateUIManager.registerComboBox( selectDrinkComboBox );
  }

  private void initMonthView()
  {
    monthView.setSelectionMode( SelectionMode.MULTIPLE_INTERVAL_SELECTION );
    monthView.setShowingLeadingDays( true );
    monthView.setShowingTrailingDays( true );
    monthView.setShowingWeekNumber( true );
    monthView.setTraversable( true );
  }

  private void initPickerPanel()
  {
    spinnerTimeFrom = new JSpinner( new SpinnerDateModel() );
    spinnerTimeFrom.setEditor( new JSpinner.DateEditor( spinnerTimeFrom, "dd.MM.yyyy" ) );
    spinnerTimeTo = new JSpinner( new SpinnerDateModel() );
    spinnerTimeTo.setEditor( new JSpinner.DateEditor( spinnerTimeTo, "dd.MM.yyyy" ) );
    currentDateButton = new JButton( "Heute" );
    currentDateButton.setToolTipText( "Zum/Zur aktuellen Tag/Woche springen" );
    final Insets compInset = new Insets( 3, 5, 0, 5 );

    final GridBagConstraints pickerLabelConstraint = new GridBagConstraints();
    pickerLabelConstraint.anchor = GridBagConstraints.WEST;
    pickerLabelConstraint.insets = compInset;

    final GridBagConstraints pickerCompConstraint = new GridBagConstraints();
    pickerCompConstraint.gridx = 1;
    pickerCompConstraint.gridy = 0;
    pickerCompConstraint.anchor = GridBagConstraints.WEST;
    pickerCompConstraint.fill = GridBagConstraints.HORIZONTAL;
    pickerCompConstraint.weightx = 0.1;
    pickerCompConstraint.insets = compInset;
    pickerPanel.setLayout( new GridBagLayout() );

    pickerPanel.add( new JLabel( "Von: " ), pickerLabelConstraint );
    pickerPanel.add( spinnerTimeFrom, pickerCompConstraint );

    pickerLabelConstraint.gridy = 1;
    pickerCompConstraint.gridy = 1;

    pickerPanel.add( new JLabel( "Bis: " ), pickerLabelConstraint );
    pickerPanel.add( spinnerTimeTo, pickerCompConstraint );

    pickerCompConstraint.gridx = 0;
    pickerCompConstraint.gridy = 2;
    pickerCompConstraint.gridwidth = 2;
    pickerCompConstraint.insets = new Insets( 5, 0, 3, 0 );

    pickerPanel.add( monthView, pickerCompConstraint );

    pickerCompConstraint.insets = compInset;
    pickerCompConstraint.gridy = 3;
    pickerPanel.add( currentDateButton, pickerCompConstraint );

    pickerCompConstraint.gridy = 4;
    pickerCompConstraint.weighty = 0.1;

    pickerPanel.add( Box.createVerticalGlue(), pickerCompConstraint );
  }

  public void updateComboBox()
  {
    selectDrinkComboBox.removeAllItems();
    selectDrinkComboBox.addItem( "Alle" );
    selectDrinkComboBox.setSelectedItem( "Alle" );
    for ( final String drink : ServerCommunication.getInstance().getDrinkNames() )
    {
      selectDrinkComboBox.addItem( drink );
    }
  }

  public void addListener()
  {
    appendComponentListener();
    selectDrinkComboBox.addItemListener( e ->
    {
      selectedDrink = String.valueOf( e.getItem() );
      dataset = createDataset();
      chart = createChart( dataset );
      chartPanel.setChart( chart );
      averageConsumption.setText( String.format( "Ø %.2f Flaschen/Tag", getAverage(), String.valueOf( e.getItem() ) ) );
      MeMateUIManager.updateGraphs();
      repaint();
      revalidate();
    } );
    monthView.addActionListener( __ ->
    {
      final Calendar endDate = Calendar.getInstance();
      endDate.setTime( monthView.getSelection().last() );
      dateRangeChanged( monthView.getSelection().first(), endDate.getTime() );
    } );

    currentDateButton.addActionListener( evt -> showCurrentDate() );
    ((JSpinner.DefaultEditor) spinnerTimeTo.getEditor()).getTextField().addKeyListener( new KeyAdapter()
    {
      @Override
      public void keyReleased( final KeyEvent e )
      {
        if ( e.getKeyCode() == KeyEvent.VK_ENTER )
        {
          changeCustomTime();
        }
      }
    } );

    ((JSpinner.DefaultEditor) spinnerTimeFrom.getEditor()).getTextField().addFocusListener( new FocusAdapter()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        changeCustomTime();
      }
    } );

    ((JSpinner.DefaultEditor) spinnerTimeTo.getEditor()).getTextField().addFocusListener( new FocusAdapter()
    {
      @Override
      public void focusLost( final FocusEvent e )
      {
        changeCustomTime();
      }
    } );
  }

  /**
   * Zeigt den aktuellen Tag / Woche an.
   */
  public void showCurrentDate()
  {
    final Date today = new Date();
    dateRangeChanged( today, today );
  }

  private void changeCustomTime()
  {
    final Calendar start = Calendar.getInstance();
    start.setTimeInMillis( ((SpinnerDateModel) spinnerTimeFrom.getModel()).getDate().getTime() );
    final Calendar end = Calendar.getInstance();
    end.setTimeInMillis( ((SpinnerDateModel) spinnerTimeTo.getModel()).getDate().getTime() );
    updateToEndOfDay( end );

    if ( start.getTimeInMillis() > end.getTimeInMillis() )
    {
      end.setTimeInMillis( start.getTimeInMillis() );
      end.add( Calendar.DAY_OF_YEAR, 1 );
    }
    dateRangeChanged( start.getTime(), end.getTime() );
  }

  public void dateRangeChanged( final Date startDate, final Date endDate )
  {
    this.startDate = startDate;
    this.endDate = endDate;
    monthView.setSelectionInterval( startDate, endDate );
    spinnerTimeFrom.setValue( startDate );
    spinnerTimeTo.setValue( endDate );
  }

  private static Calendar updateToEndOfDay( final Calendar calendar )
  {
    calendar.set( Calendar.HOUR_OF_DAY, 23 );
    calendar.set( Calendar.MINUTE, 59 );
    calendar.set( Calendar.SECOND, 59 );
    calendar.set( Calendar.MILLISECOND, 999 );

    return calendar;
  }

  /**
   * Erstellt einen neuen Datensatz für das angegebene Getränk.
   *
   * @param drink Name des Getränks.
   * @return Datensatz für den Graphen.
   */
  private XYDataset createDataset()
  {
    amountMap.clear();

    final Calendar startDate = Calendar.getInstance();
    startDate.setTime( this.startDate );
    final LocalDateTime selectedStartDate = LocalDateTime.ofInstant( startDate.toInstant(), ZoneId.systemDefault() );

    final Calendar endDate = Calendar.getInstance();
    endDate.setTime( this.endDate );
    final LocalDateTime selectedEndDate = LocalDateTime.ofInstant( endDate.toInstant(), ZoneId.systemDefault() );

    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    int i = 0;
    final String startDateAsString = formatter.format( selectedStartDate ).toString();
    String currentDateAsString = formatter.format( selectedEndDate ).toString();

    //Special case if only 1 day is selected
    if ( currentDateAsString.contentEquals( startDateAsString ) )
    {
      amountMap.put( currentDateAsString, 0 );
    }
    while ( !startDateAsString.equals( currentDateAsString ) )
    {
      currentDateAsString = formatter.format( selectedEndDate.minusDays( i ) ).toString();
      amountMap.put( currentDateAsString, 0 );
      i++;
    }
    //Reset after filling the map
    currentDateAsString = formatter.format( selectedEndDate ).toString();

    final String[][] historyData = ServerCommunication.getInstance().getHistoryData( dateType.SHORT ) == null ? null
        : ServerCommunication.getInstance().getHistoryData( dateType.SHORT ).clone();
    if ( historyData != null )
    {
      for ( final String[] data : historyData )
      {
        final String action = data[ 0 ];
        if ( action.contains( "getrunken" ) )
        {
          if ( selectedDrink.equals( "Alle" ) )
          {
            final String date = data[ 4 ];
            try
            {
              final Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( date );
              if ( !eventDate.toInstant().isBefore( selectedStartDate.atZone( ZoneId.systemDefault() ).toInstant() ) )
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
          else if ( action.contains( selectedDrink ) )
          {
            final String date = data[ 4 ];
            try
            {
              final Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( date );
              if ( !eventDate.toInstant().isBefore( selectedStartDate.atZone( ZoneId.systemDefault() ).toInstant() ) )
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
    }
    final TimeSeries series = new TimeSeries( "DrinksOverTime" );

    int j = 0;

    while ( !startDateAsString.equals( currentDateAsString ) )
    {
      final Day day = new Day( selectedEndDate.minusDays( j ).getDayOfMonth(), selectedEndDate.minusDays( j ).getMonthValue(),
          selectedEndDate.minusDays( j ).getYear() );
      series.add( day, amountMap.get( formatter.format( selectedEndDate.minusDays( j ) ).toString() ) );
      currentDateAsString = formatter.format( selectedEndDate.minusDays( j ) ).toString();
      j++;
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
    MeMateUIManager.registerFreeChart( freeChart );
    return freeChart;
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
      Mainframe.getInstance().removeComponentListener( rateResizeListener );
    }
    catch ( final Exception exception )
    {
      ClientLog.newLog( "Der ComponentListener konnte nicht entfernt werden." );
      ClientLog.newLog( exception.getMessage() );
    }
    Mainframe.getInstance().addComponentListener( rateResizeListener );
  }


  /**
   * Teilt die Anzahl der kosumierten Getränke des letzen Monats durch 31.
   */
  private Float getAverage()
  {
    final Calendar endDate = Calendar.getInstance();
    endDate.setTime( this.endDate );
    updateToEndOfDay( endDate );
    final LocalDateTime selectedEndDate = LocalDateTime.ofInstant( endDate.toInstant(), ZoneId.systemDefault() );
    final Calendar startDate = Calendar.getInstance();
    startDate.setTime( this.startDate );
    updateToEndOfDay( startDate );
    final LocalDateTime selectedStartDate = LocalDateTime.ofInstant( startDate.toInstant(), ZoneId.systemDefault() );
    final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
    final String startDateAsString = formatter.format( selectedStartDate ).toString();
    String currentDateAsString = formatter.format( selectedEndDate ).toString();
    float counter = 0f;
    int i = 0;

    while ( !startDateAsString.equals( currentDateAsString ) )
    {
      counter = counter + amountMap.get( formatter.format( selectedEndDate.minusDays( i ) ).toString() );
      currentDateAsString = formatter.format( selectedEndDate.minusDays( i ) ).toString();
      i++;
    }
    return counter / i;
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

  private GridBagConstraints getDateRangePickerConstraits()
  {
    final GridBagConstraints selectedDrinkComboboxConstraints = new GridBagConstraints();
    selectedDrinkComboboxConstraints.gridx = 1;
    selectedDrinkComboboxConstraints.gridy = 1;
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

  public void updateSettings()
  {
    updateComboBox();
  }
}
