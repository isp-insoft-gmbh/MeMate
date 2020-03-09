/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * @author nwe
 * @since 06.03.2020
 *
 */

public class ToggleSwitch extends JPanel
{
  public boolean        activated    = false;
  private Color         switchColor  = new Color( 200, 200, 200 ), buttonColor = new Color( 255, 255, 255 ),
      borderColor = new Color( 50, 50, 50 );
  private BufferedImage puffer;
  private int           borderRadius = 10;
  private Graphics2D    g;

  public ToggleSwitch()
  {
    super();
    setVisible( true );
    setCursor( new Cursor( Cursor.HAND_CURSOR ) );
    setBounds( 0, 0, 41, 21 );
    repaint();
  }

  @Override
  public void paint( Graphics gr )
  {
    if ( g == null || puffer.getWidth() != getWidth() || puffer.getHeight() != getHeight() )
    {
      puffer = (BufferedImage) createImage( getWidth(), getHeight() );
      g = (Graphics2D) puffer.getGraphics();
      RenderingHints rh = new RenderingHints(
          RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON );
      g.setRenderingHints( rh );
    }
    g.setColor( MeMateUIManager.getDarkModeState() ? UIManager.getColor( "App.Background" ) : Color.white );
    g.fillRoundRect( 0, 0, getWidth(), getHeight(), 0, 0 );
    g.setColor( activated ? UIManager.getColor( "AppColor" ) : switchColor );
    g.fillRoundRect( 0, 0, this.getWidth() - 1, getHeight() - 1, 5, borderRadius );
    g.setColor( borderColor );
    g.drawRoundRect( 0, 0, getWidth() - 1, getHeight() - 1, 5, borderRadius );
    g.setColor( buttonColor );
    if ( activated )
    {
      g.fillRoundRect( getWidth() / 2, 1, (getWidth() - 1) / 2 - 2, (getHeight() - 1) - 2, borderRadius, borderRadius );
      g.setColor( borderColor );
      g.drawRoundRect( (getWidth() - 1) / 2, 0, (getWidth() - 1) / 2, (getHeight() - 1), borderRadius, borderRadius );
    }
    else
    {
      g.fillRoundRect( 1, 1, (getWidth() - 1) / 2 - 2, (getHeight() - 1) - 2, borderRadius, borderRadius );
      g.setColor( borderColor );
      g.drawRoundRect( 0, 0, (getWidth() - 1) / 2, (getHeight() - 1), borderRadius, borderRadius );
    }

    gr.drawImage( puffer, 0, 0, null );
  }

  public void setActivated( boolean activated )
  {
    this.activated = activated;
  }
}
