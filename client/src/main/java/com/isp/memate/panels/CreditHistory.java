/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.isp.memate.Cache;
import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.GUIObjects;

/**
 * This Panel allows the user to see the history of his balance in the last month.
 *
 * @author nwe
 * @since 28.01.2020
 */
public class CreditHistory extends JPanel
{
  ChartPanel chartPanel;

  public CreditHistory()
  {
    GUIObjects.currentPanel = this;
    setLayout( new BorderLayout() );
    addChartPanel();
    setDrawRange();
    addResizeListener();
  }

  private void addChartPanel()
  {
    JFreeChart lineChart = createLineChart();
    chartPanel = new ChartPanel( lineChart );
    chartPanel.setPreferredSize( new Dimension( 760, 570 ) );
    chartPanel.setMouseZoomable( true, false );
    add( chartPanel );
  }

  private JFreeChart createLineChart()
  {
    JFreeChart chart = ChartFactory.createLineChart( "Guthabenverlauf (in den letzen 30 Tagen)", "Datum", "Guthaben",
        createBalanceDataset(), PlotOrientation.VERTICAL, false, true, false );
    applyColorToChart( chart );
    return chart;
  }

  private void applyColorToChart( JFreeChart lineChart )
  {
    Color foreground = UIManager.getColor( "Label.foreground" );
    lineChart.getCategoryPlot().getRenderer().setSeriesPaint( 0, UIManager.getColor( "AppColor" ) );
    lineChart.setBackgroundPaint( UIManager.getColor( "Panel.background" ) );
    lineChart.getCategoryPlot().setBackgroundPaint( UIManager.getColor( "TextField.background" ) );
    lineChart.getTitle().setPaint( foreground );
    lineChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( foreground );
    lineChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( foreground );
    lineChart.getCategoryPlot().getDomainAxis().setLabelPaint( foreground );
    lineChart.getCategoryPlot().getRangeAxis().setLabelPaint( foreground );
    lineChart.getCategoryPlot().setDomainGridlinesVisible( false );
    lineChart.getCategoryPlot().setRangeGridlinesVisible( false );
  }


  private DefaultCategoryDataset createBalanceDataset()
  {
    final DateFormat dateFormat = new SimpleDateFormat( "dd.MM HH:mm:ss" );
    final DateFormat oldFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" );
    final String[][] historyData = Cache.getInstance().getHistory( dateType.LONG );
    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
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
        if ( data[ 1 ].equals( Cache.getInstance().getUsername() ) )
        {
          if ( action.contains( "Guthaben" ) || action.contains( "getrunken" ) )
          {
            Date date = null;
            final String dateAsString = data[ 4 ];
            try
            {
              date = oldFormat.parse( data[ 4 ] );
            }
            catch ( final ParseException exception )
            {
              ClientLog.newLog( "Das Datum konnte nicht formatiert werden." + exception );
            }
            final ZonedDateTime today = ZonedDateTime.now();
            final ZonedDateTime thirtyDaysAgo = today.minusDays( 30 );
            try
            {
              final Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( dateAsString );
              if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
              {
                if ( data[ 5 ].equals( "false" ) )
                {
                  dataset.addValue( Float.valueOf( data[ 3 ].replace( ",", "." ).substring( 0, data[ 3 ].length() - 1 ) ), "Guthaben",
                      dateFormat.format( date ) );
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
    return dataset;
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