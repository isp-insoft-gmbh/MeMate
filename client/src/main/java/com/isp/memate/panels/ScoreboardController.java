package com.isp.memate.panels;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.isp.memate.Cache;
import com.isp.memate.HistoryEvents;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ScoreboardController implements Initializable
{
  private final Cache                  cache                  = Cache.getInstance();
  private final Map<String, Integer>   weeklyScoreboardData;
  private final Map<String, Integer>   overallScoreboardData;
  private final Map<Integer, GridPane> acitvityPanesMap       = new HashMap<>();
  private final Map<Integer, Label>    acitvityNameLabelsMap  = new HashMap<>();
  private final Map<Integer, Label>    acitvityDrinkLabelsMap = new HashMap<>();
  private final Map<Integer, Label>    acitvityDateLabelsMap  = new HashMap<>();

  public Label weeklyFirstLabel  = null;
  public Label weeklySecondLabel = null;
  public Label weeklyThirdLabel  = null;
  public Label weeklyFourthLabel = null;
  public Label weeklyFifthLabel  = null;

  public Label overallFirstLabel  = null;
  public Label overallSecondLabel = null;
  public Label overallThirdLabel  = null;
  public Label overallFourthLabel = null;
  public Label overallFifthLabel  = null;

  public GridPane acitvity1Pane = null;
  public GridPane acitvity2Pane = null;
  public GridPane acitvity3Pane = null;
  public GridPane acitvity4Pane = null;
  public GridPane acitvity5Pane = null;

  public Label acitvity1NameLabel = null;
  public Label acitvity2NameLabel = null;
  public Label acitvity3NameLabel = null;
  public Label acitvity4NameLabel = null;
  public Label acitvity5NameLabel = null;

  public Label acitvity1DateLabel = null;
  public Label acitvity2DateLabel = null;
  public Label acitvity3DateLabel = null;
  public Label acitvity4DateLabel = null;
  public Label acitvity5DateLabel = null;

  public Label acitvity1DrinkLabel = null;
  public Label acitvity2DrinkLabel = null;
  public Label acitvity3DrinkLabel = null;
  public Label acitvity4DrinkLabel = null;
  public Label acitvity5DrinkLabel = null;

  public ScoreboardController()
  {
    weeklyScoreboardData = cache.getWeeklyScoreboard();
    overallScoreboardData = cache.getScoreboard();
  }

  @Override
  public void initialize( URL location, ResourceBundle resources )
  {
    initializeWeeklyScoreboard();
    initializeOverallScoreboard();
    initializeActivityPanel();
  }

  private void initializeWeeklyScoreboard()
  {
    final Object[] names = weeklyScoreboardData.keySet().toArray();
    if ( weeklyScoreboardData.size() >= 1 )
    {
      weeklyFirstLabel.setVisible( true );
      weeklyFirstLabel.setText( "1. " + names[ 0 ] + " - " + weeklyScoreboardData.get( names[ 0 ] ) );
    }
    if ( weeklyScoreboardData.size() >= 2 )
    {
      weeklySecondLabel.setVisible( true );
      weeklySecondLabel.setText( "2. " + names[ 1 ] + " - " + weeklyScoreboardData.get( names[ 1 ] ) );
    }
    if ( weeklyScoreboardData.size() >= 3 )
    {
      weeklyThirdLabel.setVisible( true );
      weeklyThirdLabel.setText( "3. " + names[ 2 ] + " - " + weeklyScoreboardData.get( names[ 2 ] ) );
    }
    if ( weeklyScoreboardData.size() >= 4 )
    {
      weeklyFourthLabel.setVisible( true );
      weeklyFourthLabel.setText( "4. " + names[ 3 ] + " - " + weeklyScoreboardData.get( names[ 3 ] ) );
    }
    if ( weeklyScoreboardData.size() >= 5 )
    {
      weeklyFifthLabel.setVisible( true );
      weeklyFifthLabel.setText( "5. " + names[ 4 ] + " - " + weeklyScoreboardData.get( names[ 4 ] ) );
    }
  }

  private void initializeOverallScoreboard()
  {
    final Object[] names = overallScoreboardData.keySet().toArray();
    if ( overallScoreboardData.size() >= 1 )
    {
      overallFirstLabel.setVisible( true );
      overallFirstLabel.setText( "1. " + names[ 0 ] + " - " + overallScoreboardData.get( names[ 0 ] ) );
    }
    if ( overallScoreboardData.size() >= 2 )
    {
      overallSecondLabel.setVisible( true );
      overallSecondLabel.setText( "2. " + names[ 1 ] + " - " + overallScoreboardData.get( names[ 1 ] ) );
    }
    if ( overallScoreboardData.size() >= 3 )
    {
      overallThirdLabel.setVisible( true );
      overallThirdLabel.setText( "3. " + names[ 2 ] + " - " + overallScoreboardData.get( names[ 2 ] ) );
    }
    if ( overallScoreboardData.size() >= 4 )
    {
      overallFourthLabel.setVisible( true );
      overallFourthLabel.setText( "4. " + names[ 3 ] + " - " + overallScoreboardData.get( names[ 3 ] ) );
    }
    if ( overallScoreboardData.size() >= 5 )
    {
      overallFifthLabel.setVisible( true );
      overallFifthLabel.setText( "5. " + names[ 4 ] + " - " + overallScoreboardData.get( names[ 4 ] ) );
    }
  }

  private void initializeActivityPanel()
  {
    acitvityPanesMap.put( 1, acitvity1Pane );
    acitvityPanesMap.put( 2, acitvity2Pane );
    acitvityPanesMap.put( 3, acitvity3Pane );
    acitvityPanesMap.put( 4, acitvity4Pane );
    acitvityPanesMap.put( 5, acitvity5Pane );
    acitvityNameLabelsMap.put( 1, acitvity1NameLabel );
    acitvityNameLabelsMap.put( 2, acitvity2NameLabel );
    acitvityNameLabelsMap.put( 3, acitvity3NameLabel );
    acitvityNameLabelsMap.put( 4, acitvity4NameLabel );
    acitvityNameLabelsMap.put( 5, acitvity5NameLabel );
    acitvityDrinkLabelsMap.put( 1, acitvity1DrinkLabel );
    acitvityDrinkLabelsMap.put( 2, acitvity2DrinkLabel );
    acitvityDrinkLabelsMap.put( 3, acitvity3DrinkLabel );
    acitvityDrinkLabelsMap.put( 4, acitvity4DrinkLabel );
    acitvityDrinkLabelsMap.put( 5, acitvity5DrinkLabel );
    acitvityDateLabelsMap.put( 1, acitvity1DateLabel );
    acitvityDateLabelsMap.put( 2, acitvity2DateLabel );
    acitvityDateLabelsMap.put( 3, acitvity3DateLabel );
    acitvityDateLabelsMap.put( 4, acitvity4DateLabel );
    acitvityDateLabelsMap.put( 5, acitvity5DateLabel );

    final String[][] history = cache.getShortHistory();
    if ( history != null )
    {
      final ZonedDateTime today = ZonedDateTime.now();
      final ZonedDateTime twentyMinutesAgo = today.minusMinutes( 20 );
      int counter = 0;
      for ( final String[] data : history )
      {
        counter++;
        final String action = data[ 0 ];
        final String consumer = data[ 1 ];
        final String date = data[ 2 ];
        final String drinkname = data[ 3 ];
        if ( HistoryEvents.CONSUMED_DRINK == HistoryEvents.valueOf( action ) )
        {
          final Date eventDate = new Date( Long.valueOf( date ) );
          if ( !eventDate.toInstant().isBefore( twentyMinutesAgo.toInstant() ) )
          {
            acitvityPanesMap.get( counter ).setVisible( true );
            acitvityNameLabelsMap.get( counter ).setText( consumer + " trinkt gerade" );
            acitvityDrinkLabelsMap.get( counter ).setText( drinkname );
            acitvityDateLabelsMap.get( counter ).setText( new SimpleDateFormat( "HH:mm" ).format( eventDate ) );
          }
        }
      }
    }
  }
}
