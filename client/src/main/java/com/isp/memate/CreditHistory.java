/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;

/**
 * In diesem Panel kann der Benutzer den Verlauf seines Guthabens des letzten Monats betrachten.
 * 
 * @author nwe
 * @since 28.01.2020
 *
 */
class CreditHistory extends JPanel
{

  /**
   * Setzt das Layout und fügt die Chart hinzu.
   */
  public CreditHistory()
  {
    setLayout( new GridBagLayout() );
  }

  /**
   * Erzeugt eine neue Chart und fügt diese hinzu.
   */
  void addChart()
  {
    removeAll();
    JFreeChart lineChart = ChartFactory.createLineChart( "Guthabenverlauf", "Datum", "Guthaben", createBalanceDataset(),
        PlotOrientation.VERTICAL, false, true, false );

    lineChart.getCategoryPlot().getRenderer().setSeriesPaint( 0, UIManager.getColor( "AppColor" ) );
    MeMateUIManager.registerLineChart( lineChart );
    ChartPanel chartPanel = new ChartPanel( lineChart );
    loadChartpanelSettingsAndAddChartPanel( chartPanel );
    apendComponentListener( chartPanel );
    repaint();
    revalidate();
  }

  /**
   * Meldet einen {@link ComponentListener} an, damit sich die Chart richtig verkleinert/vergrößert.
   */
  private void apendComponentListener( ChartPanel chartPanel )
  {
    chartPanel.setMaximumDrawHeight( 1000 );
    chartPanel.setMaximumDrawWidth( 1000 );
    chartPanel.setMinimumDrawWidth( 10 );
    chartPanel.setMinimumDrawHeight( 10 );

    ComponentListener creditResizeListener = new ComponentAdapter()
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
      Mainframe.getInstance().removeComponentListener( creditResizeListener );
    }
    catch ( Exception exception )
    {
      ClientLog.newLog( "Der ComponentListener konnte nicht entfernt werden." );
      ClientLog.newLog( exception.getMessage() );
    }
    Mainframe.getInstance().addComponentListener( creditResizeListener );
  }

  /**
   * Layout für das Chartpanel.
   */
  private void loadChartpanelSettingsAndAddChartPanel( ChartPanel chartPanel )
  {
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
  }

  private DefaultCategoryDataset createBalanceDataset()
  {
    DateFormat dateFormat = new SimpleDateFormat( "dd-MMM HH:mm:ss" );
    DateFormat oldFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" );
    String[][] historyData = ServerCommunication.getInstance().getHistoryData( dateType.LONG );
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    if ( historyData != null )
    {
      for ( int i = 0; i < historyData.length / 2; i++ )
      {
        String[] temp = historyData[ i ];
        historyData[ i ] = historyData[ historyData.length - i - 1 ];
        historyData[ historyData.length - i - 1 ] = temp;
      }

      for ( String[] data : historyData )
      {
        String action = data[ 0 ];
        if ( data[ 1 ].equals( ServerCommunication.getInstance().currentUser ) )
        {
          if ( action.contains( "Guthaben" ) || action.contains( "getrunken" ) )
          {
            Date date = null;
            String dateAsString = data[ 4 ];
            try
            {
              date = oldFormat.parse( data[ 4 ] );
            }
            catch ( ParseException exception )
            {
              ClientLog.newLog( "Das Datum konnt nicht formatiert werden." + exception );
            }
            ZonedDateTime today = ZonedDateTime.now();
            ZonedDateTime thirtyDaysAgo = today.minusDays( 30 );
            try
            {
              Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( dateAsString );
              if ( !eventDate.toInstant().isBefore( thirtyDaysAgo.toInstant() ) )
              {
                if ( data[ 5 ].equals( "false" ) )
                {
                  dataset.addValue( Float.valueOf( data[ 3 ].replace( ",", "." ).substring( 0, data[ 3 ].length() - 1 ) ), "Guthaben",
                      dateFormat.format( date ) );
                }
              }
            }
            catch ( ParseException exception )
            {
              ClientLog.newLog( "Das Datum ist out of range." + exception );
            }
          }
        }
      }
    }
    return dataset;
  }
}
