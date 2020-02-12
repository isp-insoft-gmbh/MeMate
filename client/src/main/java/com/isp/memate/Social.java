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

import com.isp.memate.util.MeMateUIManager;

/**
 * @author nwe
 * @since 11.02.2020
 *
 */
public class Social extends JPanel
{
  private static JPanel mainPanel       = MeMateUIManager.createJPanel();
  private static JPanel scoreBoardPanel = MeMateUIManager.createJPanel();
  private static JPanel activityPanel   = MeMateUIManager.createJPanel();

  /**
   * 
   */
  public Social()
  {
    setLayout( new BorderLayout() );
    add( mainPanel );
  }

  /**
   * 
   */
  public static void update()
  {
    mainPanel.removeAll();
    scoreBoardPanel.removeAll();
    activityPanel.removeAll();
    loadScoreBoardSettings();
    loadActivityPanelSettings();
    mainPanel.add( scoreBoardPanel, BorderLayout.CENTER );
    mainPanel.add( activityPanel, BorderLayout.EAST );
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
            System.out.println( "Das Datum ist out of range." + exception );
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
    scoreBoardPanel.setLayout( new GridBagLayout() );
    JLabel scoreBoardLabel = MeMateUIManager.createJLabel();
    JLabel firstPlaceLabel = new JLabel();
    JLabel secondPlaceLabel = new JLabel();
    JLabel thirdPlaceLabel = new JLabel();
    JLabel fourthPlaceLabel = new JLabel();
    JLabel fifthPlaceLabel = new JLabel();
    scoreBoardLabel.setText( " - Scoreboard - " );
    firstPlaceLabel.setText( "1. " + scoreList.get( 0 ).name + " - " + scoreList.get( 0 ).score );
    secondPlaceLabel.setText( "2. " + scoreList.get( 1 ).name + " - " + scoreList.get( 1 ).score );
    thirdPlaceLabel.setText( "3. " + scoreList.get( 2 ).name + " - " + scoreList.get( 2 ).score );
    fourthPlaceLabel.setText( "4. " + scoreList.get( 3 ).name + " - " + scoreList.get( 3 ).score );
    fifthPlaceLabel.setText( "5. " + scoreList.get( 4 ).name + " - " + scoreList.get( 4 ).score ); //FIXME this will throw out of bounds
    scoreBoardLabel.setFont( new Font( "Courier New", Font.BOLD, 30 ) );
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
    GridBagConstraints scoreBoardLabelConstraints = new GridBagConstraints();
    scoreBoardLabelConstraints.gridx = 0;
    scoreBoardLabelConstraints.gridy = 0;
    scoreBoardLabelConstraints.insets = new Insets( 5, 5, 15, 5 );
    scoreBoardPanel.add( scoreBoardLabel, scoreBoardLabelConstraints );
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.gridx = 0;
    constraints.gridy = 1;
    constraints.insets = new Insets( 0, 0, 5, 0 );
    scoreBoardPanel.add( firstPlaceLabel, constraints );
    constraints.gridy = 2;
    scoreBoardPanel.add( secondPlaceLabel, constraints );
    constraints.gridy = 3;
    scoreBoardPanel.add( thirdPlaceLabel, constraints );
    constraints.gridy = 4;
    scoreBoardPanel.add( fourthPlaceLabel, constraints );
    constraints.gridy = 5;
    scoreBoardPanel.add( fifthPlaceLabel, constraints );
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
