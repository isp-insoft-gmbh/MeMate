/**
 * © 2019 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.awt.Dimension;

import javax.swing.JComponent;

/**
 * Contains utility methods for interacting with Swing components.
 * 
 * @author nwe
 * @since 21.10.2019
 */
public class SwingUtil
{
  /**
   * Updates the preferred width of a component while keeping the height.
   * 
   * @param width width to be set
   * @param component JComponent to set the width for
   */
  public static void setPreferredWidth( int width, JComponent component )
  {
    Dimension originalPreferredSize = component.getPreferredSize();
    component.setPreferredSize( new Dimension( width, originalPreferredSize.height ) );
  }
}
