/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.isp.memate.util.ClientLog;
import com.isp.memate.util.MeMateUIManager;

/**
 * @author nwe
 * @since 11.02.2020
 *
 */
class Social extends JPanel
{
  private static JPanel mainPanel             = MeMateUIManager.createJPanel();
  private static JPanel scoreBoardPanel       = MeMateUIManager.createJPanel();
  private static JPanel weeklyScoreBoardPanel = MeMateUIManager.createJPanel();
  private static JPanel activityPanel         = MeMateUIManager.createJPanel();

  /**
   * 
   */
  public Social()
  {
    setLayout( new BorderLayout() );
    mainPanel.setLayout( new GridBagLayout() );
    add( mainPanel, BorderLayout.CENTER );
  }

  /**
   * 
   */
  void update()
  {
    mainPanel.removeAll();
    scoreBoardPanel.removeAll();
    activityPanel.removeAll();
    weeklyScoreBoardPanel.removeAll();
    loadScoreBoardSettings();
    loadActivityPanelSettings();
    loadWeeklyScoreBoardSettings();
    GridBagConstraints scoreBoardPaneLConstraints = new GridBagConstraints();
    scoreBoardPaneLConstraints.gridx = 2;
    scoreBoardPaneLConstraints.gridy = 0;
    GridBagConstraints weeklyScoreBoardPaneLConstraints = new GridBagConstraints();
    weeklyScoreBoardPaneLConstraints.gridx = 0;
    weeklyScoreBoardPaneLConstraints.gridy = 0;
    GridBagConstraints activityConstraints = new GridBagConstraints();
    activityConstraints.gridx = 1;
    activityConstraints.gridy = 0;
    mainPanel.add( scoreBoardPanel, scoreBoardPaneLConstraints );
    mainPanel.add( activityPanel, activityConstraints );
    mainPanel.add( weeklyScoreBoardPanel, weeklyScoreBoardPaneLConstraints );
    mainPanel.revalidate();
    mainPanel.repaint();
  }


  /**
   * 
   */
  private static void loadActivityPanelSettings()
  {
    activityPanel.setLayout( new GridBagLayout() );
    int ypos = 0;
    String[][] history = ServerCommunication.getInstance().getShortHistory();
    if ( history != null )
    {
      ZonedDateTime today = ZonedDateTime.now();
      ZonedDateTime twentyMinutesAgo = today.minusMinutes( 20 );
      for ( String[] data : history )
      {
        String action = data[ 0 ];
        String consumer = data[ 1 ];
        String date = data[ 2 ];
        String drinkname = action.substring( 0, action.length() - 10 );
        if ( action.contains( "getrunken" ) )
        {
          try
          {
            Date eventDate = new SimpleDateFormat( "yyyy-MM-dd HH:mm" ).parse( date );
            if ( !eventDate.toInstant().isBefore( twentyMinutesAgo.toInstant() ) )
            {
              JPanel panel = new JPanel();
              panel.setPreferredSize( new Dimension( 240, 70 ) );
              panel.setLayout( new GridBagLayout() );
              JLabel nameLabel = MeMateUIManager.createJLabel();
              nameLabel.setText( consumer + " trinkt gerade" );
              nameLabel.setFont( nameLabel.getFont().deriveFont( 20f ) );
              GridBagConstraints nameLabelConstraints = new GridBagConstraints();
              nameLabelConstraints.gridx = 0;
              nameLabelConstraints.gridy = 0;
              nameLabelConstraints.insets = new Insets( 5, 5, 5, 0 );
              nameLabelConstraints.weightx = 1;
              nameLabelConstraints.anchor = GridBagConstraints.LINE_START;
              panel.add( nameLabel, nameLabelConstraints );
              JLabel drinkNameLabel = MeMateUIManager.createJLabel();
              drinkNameLabel.setText( drinkname );
              drinkNameLabel.setFont( nameLabel.getFont().deriveFont( 20f ) );
              GridBagConstraints drinkNameLabelConstraints = new GridBagConstraints();
              drinkNameLabelConstraints.gridx = 0;
              drinkNameLabelConstraints.gridy = 1;
              drinkNameLabelConstraints.insets = new Insets( 5, 5, 5, 0 );
              drinkNameLabelConstraints.anchor = GridBagConstraints.LINE_START;
              panel.add( drinkNameLabel, drinkNameLabelConstraints );
              JLabel dateLabel = MeMateUIManager.createJLabel();
              dateLabel.setText( date.substring( date.length() - 6, date.length() ) );
              dateLabel.setFont( nameLabel.getFont().deriveFont( 11f ) );
              GridBagConstraints dateLabelConstraints = new GridBagConstraints();
              dateLabelConstraints.gridx = 1;
              dateLabelConstraints.gridy = 1;
              dateLabelConstraints.insets = new Insets( 0, 5, 2, 2 );
              dateLabelConstraints.anchor = GridBagConstraints.LAST_LINE_END;
              panel.add( dateLabel, dateLabelConstraints );

              GridBagConstraints panelConstraints = new GridBagConstraints();
              panelConstraints.gridx = 0;
              panelConstraints.gridy = ypos;
              panelConstraints.insets = new Insets( 5, 5, 5, 5 );
              MeMateUIManager.registerPanel( "adminButton", panel );
              activityPanel.add( panel, panelConstraints );
              ypos++;
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

  private static void loadScoreBoardSettings()
  {
    final String[] userNames = ServerCommunication.getInstance().getAllUsers();
    final Map<String, Integer> scoreMap = new HashMap<>();
    for ( String username : userNames )
    {
      scoreMap.put( username, 0 );
    }
    String[][] history = ServerCommunication.getInstance().getScoreboard();
    if ( history != null )
    {
      for ( String[] data : history )
      {
        scoreMap.put( data[ 1 ], scoreMap.get( data[ 1 ] ) + 1 );
      }
    }
    List<Score> scoreList = new ArrayList<>();
    for ( String name : scoreMap.keySet() )
    {
      scoreList.add( new Score( name, scoreMap.get( name ) ) );
    }
    Collections.sort( scoreList, Comparator.comparing( Score::getScore ) );
    Collections.reverse( scoreList );
    loadScoreBoard( scoreList, scoreBoardPanel, "overall" );
  }

  /**
   * @param scoreList
   * @param scoreBoardPanel2
   */
  private static void loadScoreBoard( List<Score> scoreList, JPanel panel, String title )
  {
    panel.setLayout( new GridBagLayout() );
    JLabel scoreBoardLabel = MeMateUIManager.createJLabel();
    JLabel overallLabel = MeMateUIManager.createJLabel();
    JLabel firstPlaceLabel = new JLabel();
    JLabel secondPlaceLabel = new JLabel();
    JLabel thirdPlaceLabel = new JLabel();
    JLabel fourthPlaceLabel = new JLabel();
    JLabel fifthPlaceLabel = new JLabel();
    scoreBoardLabel.setText( " - Scoreboard - " );
    overallLabel.setText( title );
    if ( scoreList.size() >= 1 )
    {
      firstPlaceLabel.setText( "1. " + scoreList.get( 0 ).name + " - " + scoreList.get( 0 ).score );
    }
    if ( scoreList.size() >= 2 )
    {
      secondPlaceLabel.setText( "2. " + scoreList.get( 1 ).name + " - " + scoreList.get( 1 ).score );
    }
    if ( scoreList.size() >= 3 )
    {
      thirdPlaceLabel.setText( "3. " + scoreList.get( 2 ).name + " - " + scoreList.get( 2 ).score );
    }
    if ( scoreList.size() >= 4 )
    {
      fourthPlaceLabel.setText( "4. " + scoreList.get( 3 ).name + " - " + scoreList.get( 3 ).score );
    }
    if ( scoreList.size() >= 5 )
    {
      fifthPlaceLabel.setText( "5. " + scoreList.get( 4 ).name + " - " + scoreList.get( 4 ).score );
    }
    scoreBoardLabel.setFont( new Font( "Courier New", Font.BOLD, 30 ) );
    overallLabel.setFont( new Font( "Courier New", Font.BOLD, 19 ) );
    firstPlaceLabel.setFont( new Font( "Courier New", Font.BOLD, 25 ) );
    secondPlaceLabel.setFont( new Font( "Courier New", Font.BOLD, 23 ) );
    thirdPlaceLabel.setFont( new Font( "Courier New", Font.BOLD, 21 ) );
    fourthPlaceLabel.setFont( new Font( "Courier New", Font.BOLD, 19 ) );
    fifthPlaceLabel.setFont( new Font( "Courier New", Font.BOLD, 17 ) );
    firstPlaceLabel.setForeground( new Color( 255, 215, 0 ) );
    secondPlaceLabel.setForeground( new Color( 192, 192, 192 ) );
    thirdPlaceLabel.setForeground( new Color( 204, 142, 52 ) );
    fourthPlaceLabel.setForeground( new Color( 127, 118, 121 ) );
    fifthPlaceLabel.setForeground( new Color( 127, 118, 121 ) );
    GridBagConstraints overallLabelConstraints = new GridBagConstraints();
    overallLabelConstraints.gridx = 0;
    overallLabelConstraints.gridy = 0;
    overallLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    panel.add( overallLabel, overallLabelConstraints );
    GridBagConstraints scoreBoardLabelConstraints = new GridBagConstraints();
    scoreBoardLabelConstraints.gridx = 0;
    scoreBoardLabelConstraints.gridy = 1;
    scoreBoardLabelConstraints.insets = new Insets( 0, 5, 15, 5 );
    panel.add( scoreBoardLabel, scoreBoardLabelConstraints );
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 2;
    constraints.insets = new Insets( 0, 0, 5, 0 );
    panel.add( firstPlaceLabel, constraints );
    constraints.gridy = 3;
    panel.add( secondPlaceLabel, constraints );
    constraints.gridy = 4;
    panel.add( thirdPlaceLabel, constraints );
    constraints.gridy = 5;
    panel.add( fourthPlaceLabel, constraints );
    constraints.gridy = 6;
    panel.add( fifthPlaceLabel, constraints );
  }

  private static void loadWeeklyScoreBoardSettings()
  {
    final String[] userNames = ServerCommunication.getInstance().getAllUsers();
    final Map<String, Integer> scoreMap = new HashMap<>();
    for ( String username : userNames )
    {
      scoreMap.put( username, 0 );
    }
    String[][] history = ServerCommunication.getInstance().getScoreboard();
    if ( history != null )
    {
      ZonedDateTime today = ZonedDateTime.now();
      DayOfWeek day = today.getDayOfWeek();
      ZonedDateTime xDaysAgo = today;
      switch ( day )
      {
        case MONDAY:
          xDaysAgo = today.minusDays( 1 );
          break;
        case TUESDAY:
          xDaysAgo = today.minusDays( 2 );
          break;
        case WEDNESDAY:
          xDaysAgo = today.minusDays( 3 );
          break;
        case THURSDAY:
          xDaysAgo = today.minusDays( 4 );
          break;
        case FRIDAY:
          xDaysAgo = today.minusDays( 5 );
          break;
        case SATURDAY:
          xDaysAgo = today.minusDays( 6 );
          break;
        case SUNDAY:
          xDaysAgo = today.minusDays( 7 );
          break;
      }
      for ( String[] data : history )
      {
        String date = data[ 2 ].substring( 0, 10 );
        try
        {
          Date eventDate = new SimpleDateFormat( "yyyy-MM-dd" ).parse( date );
          if ( !eventDate.toInstant().isBefore( xDaysAgo.toInstant() ) )
          {
            scoreMap.put( data[ 1 ], scoreMap.get( data[ 1 ] ) + 1 );
          }
        }
        catch ( ParseException exception )
        {
          ClientLog.newLog( "Das Datum ist out of range." + exception );
        }
      }
    }
    List<Score> scoreList = new ArrayList<>();
    for ( String name : scoreMap.keySet() )
    {
      scoreList.add( new Score( name, scoreMap.get( name ) ) );
    }
    Collections.sort( scoreList, Comparator.comparing( Score::getScore ) );
    Collections.reverse( scoreList );
    loadScoreBoard( scoreList, weeklyScoreBoardPanel, "weekly" );
  }
}

class Score
{
  String name;
  int    score;

  public Score( String name, int score )
  {
    this.name = name;
    this.score = score;
  }

  public int getScore()
  {
    return score;
  }
}
