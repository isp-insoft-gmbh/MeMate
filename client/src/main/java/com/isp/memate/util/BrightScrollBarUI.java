/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * @author dtr
 * @since 06.02.2020
 *
 */
public class BrightScrollBarUI extends BasicScrollBarUI
{
  @Override
  protected JButton createDecreaseButton( final int orientation )
  {
    return getInvisibleButton();
  }

  @Override
  protected JButton createIncreaseButton( final int orientation )
  {
    return getInvisibleButton();
  }

  private JButton getInvisibleButton()
  {
    final JButton b = new JButton();
    final Dimension d = new Dimension( 0, 0 );
    b.setMinimumSize( d );
    b.setPreferredSize( d );
    b.setMaximumSize( d );

    return b;
  }

  @Override
  protected void configureScrollBarColors()
  {
    super.configureScrollBarColors();
    thumbColor = new Color( 205, 205, 205 );
    thumbHighlightColor = new Color( 205, 205, 205 );

    thumbLightShadowColor = new Color( 205, 205, 205 );
    thumbDarkShadowColor = new Color( 205, 205, 205 );
    trackColor = Color.white;
  }
}
