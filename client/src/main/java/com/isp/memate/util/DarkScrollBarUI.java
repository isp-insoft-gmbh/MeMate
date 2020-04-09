/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * @author dtr
 * @since 06.02.2020
 *
 */
class DarkScrollBarUI extends BasicScrollBarUI
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
    thumbColor = UIManager.getColor( "App.Background" ).brighter();
    thumbHighlightColor = UIManager.getColor( "App.Background" ).brighter();

    thumbLightShadowColor = UIManager.getColor( "App.Background" ).brighter();
    thumbDarkShadowColor = UIManager.getColor( "App.Background" ).brighter();
    trackColor = UIManager.getColor( "App.Background" );
  }
}
