/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

/**
 * Der DrinkCellRenderer erweitert die ListCellRenderer-Klasse, damit im Drinkmanager die
 * vorhandenen Getränke mit Vorschaubild und Preis dargestellt werden.
 * 
 * @author nwe
 * @since 17.10.2019
 *
 */
class DrinkCellRenderer implements ListCellRenderer<Object>
{
  private final JPanel renderComponent = new JPanel();
  private final JLabel drinkNameLabel  = new JLabel();
  private final JLabel priceLabel      = new JLabel();
  private final JLabel pictureLabel    = new JLabel();
  Cache                cache           = Cache.getInstance();

  /**
   * Der DrinkCellRenderer bestimmt wie eine Cell der Liste aussehen soll. Der Renderer benutzt ein
   * BorderLayout um die Bilder links, den Namen in der Mitte und den Preis rechts darzustellen.
   * Außerdem wird die Schriftgröße und der Background angepasst.
   */
  public DrinkCellRenderer()
  {
    Font font = priceLabel.getFont().deriveFont( 20f );
    renderComponent.setLayout( new GridBagLayout() );
    priceLabel.setFont( font );
    drinkNameLabel.setFont( font );

    GridBagConstraints pictureLabelConstraints = new GridBagConstraints();
    pictureLabelConstraints.gridx = 0;
    pictureLabelConstraints.gridy = 0;
    pictureLabelConstraints.weightx = 0;
    pictureLabelConstraints.anchor = GridBagConstraints.LINE_START;
    pictureLabelConstraints.insets = new Insets( 0, 30, 0, 0 );
    renderComponent.add( pictureLabel, pictureLabelConstraints );
    GridBagConstraints drinkNameLabelConstraints = new GridBagConstraints();
    drinkNameLabelConstraints.gridx = 1;
    drinkNameLabelConstraints.gridy = 0;
    drinkNameLabelConstraints.weightx = 1;
    drinkNameLabelConstraints.anchor = GridBagConstraints.LINE_START;
    renderComponent.add( drinkNameLabel, drinkNameLabelConstraints );
    GridBagConstraints priceLabelConstraints = new GridBagConstraints();
    priceLabelConstraints.gridx = 2;
    priceLabelConstraints.gridy = 0;
    priceLabelConstraints.weightx = 0.2;
    priceLabelConstraints.anchor = GridBagConstraints.LINE_END;
    priceLabelConstraints.insets = new Insets( 0, 0, 0, 30 );
    renderComponent.add( priceLabel, priceLabelConstraints );

  }

  @Override
  public Component getListCellRendererComponent( JList<?> list, Object value, int index,
                                                 boolean isSelected, boolean cellHasFocus )
  {
    Float price = cache.getPrice( (String) value );
    if ( price == null )
    {
      return renderComponent;
    }
    NumberFormat formatter = NumberFormat.getCurrencyInstance();
    String format = formatter.format( price.doubleValue() );
    priceLabel.setText( format );

    ImageIcon drinkIcon = cache.getIcon( (String) value );
    Image drinkImage = drinkIcon.getImage();
    Image scaledImage;

    if ( drinkIcon.getIconHeight() > 140 || drinkIcon.getIconWidth() > 150 )
    {
      double scale = 140.0 / drinkIcon.getIconHeight();
      int height = 140;
      int width = (int) (drinkIcon.getIconWidth() * scale);
      if ( width > 150 )
      {
        width = 150;
      }
      scaledImage = drinkImage.getScaledInstance( width, height, Image.SCALE_SMOOTH );
      pictureLabel.setIcon( new ImageIcon( scaledImage ) );
    }
    else
    {
      pictureLabel.setIcon(
          new ImageIcon(
              cache.getIcon( (String) value ).getImage().getScaledInstance( 45, 140, Image.SCALE_SMOOTH ) ) );
    }
    pictureLabel.setPreferredSize( new Dimension( 200, 140 ) );
    drinkNameLabel.setText( value.toString() );
    if ( isSelected )
    {
      renderComponent.setBackground( UIManager.getColor( "Table.selectionBackground" ) );
    }
    else
    {
      renderComponent.setBackground( UIManager.getColor( "Panel.background" ) );
    }
    return renderComponent;
  }
}
