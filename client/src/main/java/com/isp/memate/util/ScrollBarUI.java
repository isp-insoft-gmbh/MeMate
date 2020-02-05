/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.metal.MetalScrollBarUI;

/**
 * @author nwe
 * @since 19.09.2019
 *
 */
public class ScrollBarUI extends MetalScrollBarUI
{
  protected void paintThumb( final Graphics g, final JComponent c, final Rectangle thumbBounds )
  {
    if ( thumbBounds.isEmpty() || !scrollbar.isEnabled() )
    {
      return;
    }

    int w = thumbBounds.width;
    int h = thumbBounds.height;

    g.translate( thumbBounds.x, thumbBounds.y );

    g.setColor( new Color( 64, 64, 64 ) );
    g.drawRect( 0, 0, w - 1, h - 1 );
    g.setColor( new Color( 64, 64, 64 ) );
    g.fillRect( 0, 0, w - 1, h - 1 );

    g.setColor( new Color( 64, 64, 64 ) );
    g.drawLine( 1, 1, 1, h - 2 );
    g.drawLine( 2, 1, w - 3, 1 );

    g.setColor( new Color( 64, 64, 64 ) );
    g.drawLine( 2, h - 2, w - 2, h - 2 );
    g.drawLine( w - 2, 1, w - 2, h - 3 );

    g.translate( 0 - thumbBounds.x, 0 - thumbBounds.y );
  }

}