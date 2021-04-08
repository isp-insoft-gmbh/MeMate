package com.isp.memate.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;

import com.isp.memate.Cache;
import com.isp.memate.DrinkIngredients;
import com.isp.memate.util.MeMateUIManager;


public class DrinkDetailsToolTip extends JToolTip
{
  JPanel infoPanel = MeMateUIManager.createJPanelWithToolTipBackground();

  public DrinkDetailsToolTip( int drinkID )
  {
    {
      super.createToolTip();
      final JPanel panel = MeMateUIManager.createJPanelWithToolTipBackground();
      panel.setLayout( new GridBagLayout() );
      final GridBagConstraints constraints = new GridBagConstraints();
      constraints.gridx = 1;
      constraints.gridy = 1;
      constraints.fill = GridBagConstraints.BOTH;
      fillInfoPanel( drinkID );
      panel.add( infoPanel, constraints );
      setLayout( new BorderLayout() );
      final Insets insets = getInsets();
      final Dimension panelSize = panel.getPreferredSize();
      panelSize.width += insets.left + insets.right;
      panelSize.height += insets.top + insets.bottom;
      setPreferredSize( panelSize );
      add( panel );
    }
  }

  private void fillInfoPanel( int drinkID )
  {
    JLabel textLabel = new JLabel();
    infoPanel.setLayout( new GridBagLayout() );
    final DrinkIngredients ingredients = Cache.getInstance().getDrinks().get( drinkID ).getDrinkIngredients();
    final String[] ingredientsArray = ingredients.getIngredients().trim().split( "," );
    int maxLength = 40;
    for ( final String string : ingredientsArray )
    {
      if ( string.length() > maxLength )
      {
        maxLength = string.length() + 2;
      }
    }
    int currentLength = 0;
    final StringBuilder listBuilder = new StringBuilder();
    listBuilder.append( "<html><b>Zutaten:</b><br>" );
    for ( final String element : ingredientsArray )
    {
      currentLength += element.length() + 2;
      if ( currentLength > maxLength )
      {
        listBuilder.append( "<br>" );
        currentLength = element.length() + 2;
      }
      listBuilder.append( element ).append( ", " );
    }
    String amountString = "";
    if ( ingredients.getAmount() != 0 )
    {
      amountString = "<br><br><b>Menge:</b> " + String.format( "%.2f", ingredients.getAmount() ) + " Liter";
    }
    textLabel.setText( listBuilder.toString().substring( 0, listBuilder.length() - 2 )
        + amountString + "<br><br><b>Durchschnittlicher Gehalt je 100ml</b><br>" );

    textLabel.setHorizontalAlignment( SwingConstants.LEFT );
    final GridBagConstraints textLabelConstraints = new GridBagConstraints();
    textLabelConstraints.gridx = 0;
    textLabelConstraints.gridy = 0;
    textLabelConstraints.gridwidth = 3;
    textLabelConstraints.gridheight = 2;
    textLabelConstraints.anchor = GridBagConstraints.LINE_START;
    textLabelConstraints.insets = new Insets( 0, 2, 0, 2 );
    infoPanel.add( textLabel, textLabelConstraints );
    addPanel( "Energie", 3, infoPanel, ingredients );
    addPanel( "Fett", 4, infoPanel, ingredients );
    addPanel( "davon gesättigte Fettsäuren", 5, infoPanel, ingredients );
    addPanel( "Kohlenhydrate", 6, infoPanel, ingredients );
    addPanel( "Zucker", 7, infoPanel, ingredients );
    addPanel( "Eiweiß", 8, infoPanel, ingredients );
    addPanel( "Salz", 9, infoPanel, ingredients );
  }

  private void addPanel( final String ingredient, final int y, final JPanel mainPanel,
                         final DrinkIngredients ingredients )
  {
    final JPanel panel = MeMateUIManager.createJPanelWithToolTipBackground();
    panel.setLayout( new GridBagLayout() );

    final JLabel label = new JLabel();
    label.setText( ingredient + " " );
    final GridBagConstraints labelConstraints = new GridBagConstraints();
    labelConstraints.gridx = 0;
    labelConstraints.gridy = 0;
    labelConstraints.weightx = 0;
    labelConstraints.anchor = GridBagConstraints.LAST_LINE_START;
    panel.add( label, labelConstraints );

    final JComponent separator = new JComponent()
    {
      @Override
      public void paintComponent( final Graphics g )
      {
        for ( int x = 0; x < getWidth(); x += 4 )
        {
          g.drawLine( x, 0, x + 1, 0 );
        }
      }
    };
    final GridBagConstraints seperatorConstraints = new GridBagConstraints();
    seperatorConstraints.gridx = 1;
    seperatorConstraints.gridy = 0;
    seperatorConstraints.weightx = 1;
    seperatorConstraints.insets = new Insets( 0, 0, 3, 0 );
    seperatorConstraints.anchor = GridBagConstraints.SOUTH;
    seperatorConstraints.fill = GridBagConstraints.HORIZONTAL;
    panel.add( separator, seperatorConstraints );

    final JLabel amountLabel = new JLabel();
    switch ( ingredient )
    {
      case "Salz":
        amountLabel.setText( String.format( " %.2fg", ingredients.getSalt() ) );
        break;
      case "Eiweiß":
        amountLabel.setText( String.format( " %.1fg", ingredients.getProtein() ) );
        break;
      case "Energie":
        amountLabel.setText( " " + ingredients.getEnergy_kJ() + " kJ (" + ingredients.getEnergy_kcal() + " kcal)" );
        break;
      case "Fett":
        amountLabel.setText( String.format( " %.1fg", ingredients.getFat() ) );
        break;
      case "davon gesättigte Fettsäuren":
        amountLabel.setText( String.format( " %.1fg", ingredients.getFatty_acids() ) );
        break;
      case "Kohlenhydrate":
        amountLabel.setText( String.format( " %.1fg", ingredients.getCarbs() ) );
        break;
      case "Zucker":
        amountLabel.setText( String.format( " %.1fg", ingredients.getSugar() ) );
      default :
        break;
    }
    final GridBagConstraints amountLabelConstraints = new GridBagConstraints();
    amountLabelConstraints.gridx = 2;
    amountLabelConstraints.gridy = 0;
    amountLabelConstraints.weightx = 0;
    amountLabelConstraints.anchor = GridBagConstraints.LAST_LINE_END;
    panel.add( amountLabel, amountLabelConstraints );

    final GridBagConstraints panelConstraints = new GridBagConstraints();
    panelConstraints.gridx = 0;
    panelConstraints.gridy = y;
    panelConstraints.gridwidth = 3;
    panelConstraints.fill = GridBagConstraints.HORIZONTAL;
    panelConstraints.insets = new Insets( 0, 2, 0, 2 );
    infoPanel.add( panel, panelConstraints );
  }
}
