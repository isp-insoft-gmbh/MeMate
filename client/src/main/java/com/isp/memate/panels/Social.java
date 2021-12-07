/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.isp.memate.Cache;
import com.isp.memate.HistoryEvents;
import com.isp.memate.util.GUIObjects;

/**
 * Im Social Panel kann man ein wöchentliches Scoreboard, ein Overall Scoreboard und wer grade
 * etwas trinkt sehen.
 * 
 * @author nwe
 * @since 11.02.2020
 */
public class Social extends JPanel
{
  static Cache cache = Cache.getInstance();

  public Social()
  {
    GUIObjects.currentPanel = this;
    setLayout( new BorderLayout() );
    add( getMainPanel(), BorderLayout.CENTER );
  }

  private JPanel getMainPanel()
  {
    final JPanel mainPanel = new JPanel( new GridBagLayout() );

    final GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = GridBagConstraints.RELATIVE;
    mainPanel.add( getWeeklyScoreBoardPanel(), constraints );
    mainPanel.add( getActivityPanel(), constraints );
    mainPanel.add( getScoreBoardPanel(), constraints );

    return mainPanel;
  }

  private JPanel getWeeklyScoreBoardPanel()
  {
    final JPanel weeklyScoreBoardPanel = new JPanel();
    loadScoreBoard( cache.getWeeklyScoreboard(), weeklyScoreBoardPanel, "weekly" );
    return weeklyScoreBoardPanel;
  }


  private JPanel getScoreBoardPanel()
  {
    final JPanel scoreBoardPanel = new JPanel();
    loadScoreBoard( cache.getScoreboard(), scoreBoardPanel, "overall" );
    return scoreBoardPanel;
  }

  private static void loadScoreBoard( Map<String, Integer> map, JPanel panel, String title )
  {
    panel.setLayout( new GridBagLayout() );
    final JLabel scoreBoardLabel = new JLabel();
    final JLabel overallLabel = new JLabel();
    final JLabel firstPlaceLabel = new JLabel();
    final JLabel secondPlaceLabel = new JLabel();
    final JLabel thirdPlaceLabel = new JLabel();
    final JLabel fourthPlaceLabel = new JLabel();
    final JLabel fifthPlaceLabel = new JLabel();
    scoreBoardLabel.setText( " - Scoreboard - " );
    overallLabel.setText( title );
    final Object[] names = map.keySet().toArray();
    if ( map.size() >= 1 )
    {
      firstPlaceLabel.setText( "1. " + names[ 0 ] + " - " + map.get( names[ 0 ] ) );
    }
    if ( map.size() >= 2 )
    {
      secondPlaceLabel.setText( "2. " + names[ 1 ] + " - " + map.get( names[ 1 ] ) );
    }
    if ( map.size() >= 3 )
    {
      thirdPlaceLabel.setText( "3. " + names[ 2 ] + " - " + map.get( names[ 2 ] ) );
    }
    if ( map.size() >= 4 )
    {
      fourthPlaceLabel.setText( "4. " + names[ 3 ] + " - " + map.get( names[ 3 ] ) );
    }
    if ( map.size() >= 5 )
    {
      fifthPlaceLabel.setText( "5. " + names[ 4 ] + " - " + map.get( names[ 4 ] ) );
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
    final GridBagConstraints overallLabelConstraints = new GridBagConstraints();
    overallLabelConstraints.gridx = 0;
    overallLabelConstraints.gridy = 0;
    overallLabelConstraints.insets = new Insets( 5, 0, 0, 0 );
    panel.add( overallLabel, overallLabelConstraints );
    final GridBagConstraints scoreBoardLabelConstraints = new GridBagConstraints();
    scoreBoardLabelConstraints.gridx = 0;
    scoreBoardLabelConstraints.gridy = 1;
    scoreBoardLabelConstraints.insets = new Insets( 0, 5, 15, 5 );
    panel.add( scoreBoardLabel, scoreBoardLabelConstraints );
    final GridBagConstraints constraints = new GridBagConstraints();
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

  /**
   * Schaut sich die letzen 5 Einträge der History an und wenn dabei Einträge von einem Getränkekauf sind
   * so wird dafür ein kleiner Panel generiert.
   */
  private JPanel getActivityPanel()
  {
    final JPanel activityPanel = new JPanel();
    activityPanel.setLayout( new GridBagLayout() );
    int ypos = 0;
    final String[][] history = cache.getShortHistory();
    if ( history != null )
    {
      final ZonedDateTime today = ZonedDateTime.now();
      final ZonedDateTime twentyMinutesAgo = today.minusMinutes( 20 );
      for ( final String[] data : history )
      {
        final String action = data[ 0 ];
        final String consumer = data[ 1 ];
        final String date = data[ 2 ];
        final String drinkname = data[ 3 ];
        if ( HistoryEvents.CONSUMED_DRINK == HistoryEvents.valueOf( action ) )
        {
          final Date eventDate = new Date( Long.valueOf( date ) );
          if ( !eventDate.toInstant().isBefore( twentyMinutesAgo.toInstant() ) )
          {
            final JPanel panel = new JPanel();
            panel.setPreferredSize( new Dimension( 240, 70 ) );
            panel.setLayout( new GridBagLayout() );
            panel.setBackground( UIManager.getColor( "Button.background" ) );
            final JLabel nameLabel = new JLabel();
            nameLabel.setText( consumer + " trinkt gerade" );
            nameLabel.setFont( nameLabel.getFont().deriveFont( 20f ) );
            final GridBagConstraints nameLabelConstraints = new GridBagConstraints();
            nameLabelConstraints.gridx = 0;
            nameLabelConstraints.gridy = 0;
            nameLabelConstraints.insets = new Insets( 5, 5, 5, 0 );
            nameLabelConstraints.weightx = 1;
            nameLabelConstraints.anchor = GridBagConstraints.LINE_START;
            panel.add( nameLabel, nameLabelConstraints );
            final JLabel drinkNameLabel = new JLabel();
            drinkNameLabel.setText( drinkname );
            drinkNameLabel.setFont( nameLabel.getFont().deriveFont( 20f ) );
            final GridBagConstraints drinkNameLabelConstraints = new GridBagConstraints();
            drinkNameLabelConstraints.gridx = 0;
            drinkNameLabelConstraints.gridy = 1;
            drinkNameLabelConstraints.insets = new Insets( 5, 5, 5, 0 );
            drinkNameLabelConstraints.anchor = GridBagConstraints.LINE_START;
            panel.add( drinkNameLabel, drinkNameLabelConstraints );
            final JLabel dateLabel = new JLabel();
            dateLabel.setText( new SimpleDateFormat( "HH:mm" ).format( eventDate ) );
            dateLabel.setFont( nameLabel.getFont().deriveFont( 11f ) );
            final GridBagConstraints dateLabelConstraints = new GridBagConstraints();
            dateLabelConstraints.gridx = 1;
            dateLabelConstraints.gridy = 1;
            dateLabelConstraints.insets = new Insets( 0, 5, 2, 2 );
            dateLabelConstraints.anchor = GridBagConstraints.LAST_LINE_END;
            panel.add( dateLabel, dateLabelConstraints );

            final GridBagConstraints panelConstraints = new GridBagConstraints();
            panelConstraints.gridx = 0;
            panelConstraints.gridy = ypos;
            panelConstraints.insets = new Insets( 5, 5, 5, 5 );
            activityPanel.add( panel, panelConstraints );
            ypos++;
          }
        }
      }
    }
    return activityPanel;
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
}
