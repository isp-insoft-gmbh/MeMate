/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.UIManager;


/**
 * @author nwe
 * @since 01.04.2020
 */
public class InfoIcon implements Icon
{
  private final int   iconSize;
  private final int   circleSize;
  private final Color iconColor;


  /**
   * Der Defaultkonstruktor verwendet als Icon Farbe ein an das L&F angepassten Blauton.
   */
  public InfoIcon()
  {
    this( UIManager.getColor( "AppColor" ), 16, 16 );
  }


  /**
   * Der Defaultkonstruktor verwendet als Icon Farbe ein an das L&F angepassten Blauton.
   *
   * @param iconSize Größe des Icons (Breite und Höhe)
   */
  public InfoIcon( final int iconSize )
  {
    this( UIManager.getColor( "AppColor" ), iconSize, 16 );
  }

  /**
   * @param iconColor Farbe des Icons
   * @param iconSize Größe des Icons (Breite und Höhe)
   * @param circleSize Größe des Kreises
   */
  public InfoIcon( final Color iconColor, final int iconSize, final int circleSize )
  {
    this.iconSize = iconSize;
    this.iconColor = iconColor;
    this.circleSize = circleSize;
  }

  private static final RenderingHints RENDERING_HINTS = new RenderingHints(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON );

  @Override
  public void paintIcon( final Component c, final Graphics g, final int x, final int y )
  {
    final Graphics graphics = g.create();
    if ( graphics instanceof Graphics2D )
    {
      ((Graphics2D) graphics).setRenderingHints( RENDERING_HINTS );
    }

    final Font font = graphics.getFont().deriveFont( Font.BOLD );
    graphics.setFont( font );
    final FontMetrics fontMetrics = graphics.getFontMetrics();
    graphics.setColor( iconColor );

    final int inset = (iconSize - circleSize) / 2;
    graphics.drawRoundRect( x + inset, y + inset, circleSize - 1, circleSize - 1, 4, 4 );
    //Die -1 ist ein bisschen magic, sieht aber zufällig immer besser aus.
    graphics.drawString( "i", x + iconSize / 2 - fontMetrics.charWidth( 'i' ) / 2, y + iconSize / 2 + fontMetrics.getAscent() / 2 - 1 );
  }

  @Override
  public int getIconHeight()
  {
    return iconSize;
  }

  @Override
  public int getIconWidth()
  {
    return iconSize;
  }
}

