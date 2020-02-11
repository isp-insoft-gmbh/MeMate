/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.isp.memate.ServerCommunication.dateType;
import com.isp.memate.util.MeMateUIManager;

/**
 * In diesem Panel kann der Benutzer den Verlauf seines Guthabens des letzten Monats betrachten.
 * 
 * @author nwe
 * @since 28.01.2020
 *
 */
public class CreditHistory extends JPanel
{
  private static final CreditHistory instance = new CreditHistory();

  /**
   * @return static instance of {@link CreditHistory}
   */
  public static CreditHistory getInstance()
  {
    return instance;
  }

  /**
   * Setzt das Layout und fügt die Chart hinzu.
   */
  public CreditHistory()
  {
    setLayout( new GridBagLayout() );
    addChart();
  }

  /**
   * Erzeugt eine neue Chart und fügt diese hinzu.
   */
  public void addChart()
  {
    removeAll();
    JFreeChart lineChart = ChartFactory.createLineChart( "Guthabenverlauf", "Datum", "Guthaben", createBalanceDataset(),
        PlotOrientation.VERTICAL, false, true, false );

    lineChart.getCategoryPlot().getRenderer().setSeriesPaint( 0, UIManager.getColor( "AppColor" ) );
    toggleDarkMode( lineChart );

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

    Mainframe.getInstance().addComponentListener( new ComponentAdapter()
    {
      @Override
      public void componentResized( final ComponentEvent e )
      {
        chartPanel.setMaximumDrawHeight( e.getComponent().getHeight() );
        chartPanel.setMaximumDrawWidth( e.getComponent().getWidth() );
        chartPanel.setMinimumDrawWidth( e.getComponent().getWidth() );
        chartPanel.setMinimumDrawHeight( e.getComponent().getHeight() );
      }
    } );
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

  private void toggleDarkMode( JFreeChart lineChart )
  {
    if ( MeMateUIManager.getDarkModeState() )
    {
      lineChart.setBackgroundPaint( MeMateUIManager.getBackground( "default" ).getDarkColor() );
      lineChart.getCategoryPlot().setBackgroundPaint( new Color( 36, 43, 55 ).brighter() );
      lineChart.getCategoryPlot().setDomainGridlinesVisible( false );
      lineChart.getCategoryPlot().setRangeGridlinesVisible( false );
      lineChart.getTitle().setPaint( Color.white );
      lineChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( Color.white );
      lineChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( Color.white );
      lineChart.getCategoryPlot().getDomainAxis().setLabelPaint( Color.white );
      lineChart.getCategoryPlot().getRangeAxis().setLabelPaint( Color.white );
    }
    else
    {
      lineChart.setBackgroundPaint( MeMateUIManager.getBackground( "default" ).getDayColor() );
      lineChart.getCategoryPlot().setBackgroundPaint( new Color( 192, 192, 192 ) );
      lineChart.getCategoryPlot().setDomainGridlinesVisible( false );
      lineChart.getCategoryPlot().setRangeGridlinesVisible( false );
      lineChart.getTitle().setPaint( Color.black );
      lineChart.getCategoryPlot().getDomainAxis().setTickLabelPaint( Color.black );
      lineChart.getCategoryPlot().getRangeAxis().setTickLabelPaint( Color.black );
      lineChart.getCategoryPlot().getDomainAxis().setLabelPaint( Color.black );
      lineChart.getCategoryPlot().getRangeAxis().setLabelPaint( Color.black );
    }
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
            try
            {
              date = oldFormat.parse( data[ 4 ] );
            }
            catch ( ParseException exception )
            {
              System.out.println( "Das Datum konnt nicht formatiert werden." + exception );
            }
            dataset.addValue( Float.valueOf( data[ 3 ].replace( ",", "." ).substring( 0, data[ 3 ].length() - 1 ) ), "Guthaben",
                dateFormat.format( date ) );
          }
        }
      }
    }
    return dataset;
  }

}
