/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Der DrinkCellRenderer erweitert die ListCellRenderer-Klasse, damit im Getränkemanager die vorhandenen
 * Getränke mit Vorschaubild und Preis dargestellt werden.
 * 
 * @author nwe
 * @since 17.10.2019
 *
 */
public class DrinkCellRenderer implements ListCellRenderer<Object>
{
  private final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder( 2, 2, 2, 2 );
  private final Border FOCUS_BORDER   = BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ),
      BorderFactory.createDashedBorder( Color.WHITE, 1, 1 ) );

  private final JPanel renderComponent = new JPanel();

  private final JLabel drinkNameLabel = new JLabel();
  private final JLabel priceLabel     = new JLabel();
  private final JLabel pictureLabel   = new JLabel();

  /**
   * Der DrinkCellRenderer bestimmt wie eine Cell der Liste aussehen soll. Der Renderer benutzt ein
   * BorderLayout um die Bilder links, den Namen in der Mitte und den Preis rechts darzustellen.
   * Außerdem wird die Schriftgröße und der Background angepasst.
   */
  public DrinkCellRenderer()
  {
    Font font = priceLabel.getFont().deriveFont( 20f );
    renderComponent.setLayout( new BorderLayout() );
    priceLabel.setBorder( new EmptyBorder( 0, 10, 0, 30 ) );
    priceLabel.setFont( font );
    drinkNameLabel.setFont( font );
    pictureLabel.setBorder( new EmptyBorder( 0, 50, 0, 30 ) );
    renderComponent.add( drinkNameLabel, BorderLayout.CENTER );
    renderComponent.add( pictureLabel, BorderLayout.WEST );
    renderComponent.add( priceLabel, BorderLayout.EAST );
    renderComponent.setBackground( UIManager.getColor( "List.background" ) );
  }

  @Override
  public Component getListCellRendererComponent( JList<?> list, Object value, int index,
                                                 boolean isSelected, boolean cellHasFocus )
  {
    priceLabel.setText( String.format( "%.2f €", DrinkInfos.getInstance().getPrice( value ) ) );
    pictureLabel.setIcon( DrinkInfos.getInstance().getIcon( value ) );
    drinkNameLabel.setText( value.toString() );
    if ( cellHasFocus )
    {
      renderComponent.setBorder( FOCUS_BORDER );
    }
    else
    {
      renderComponent.setBorder( DEFAULT_BORDER );
    }
    if ( isSelected )
    {
      renderComponent.setBackground( UIManager.getColor( "Table.selectionBackground" ) );
      priceLabel.setForeground( UIManager.getColor( "Table.selectionForeground" ) );
      drinkNameLabel.setForeground( UIManager.getColor( "Table.selectionForeground" ) );
    }
    else
    {
      renderComponent.setBackground( UIManager.getColor( "Table.background" ) );
      priceLabel.setForeground( UIManager.getColor( "Table.foreground" ) );
      drinkNameLabel.setForeground( UIManager.getColor( "Table.foreground" ) );
    }
    return renderComponent;
  }
}
