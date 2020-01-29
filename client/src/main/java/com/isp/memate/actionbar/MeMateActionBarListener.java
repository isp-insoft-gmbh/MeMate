package com.isp.memate.actionbar;


import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * @author dtr
 * @since 28.01.2020
 *
 */
public class MeMateActionBarListener extends MouseAdapter
{
  /** Die Komponente, in der der die Maustaste gedrückt wurde */
  private static Component componentByMousePressed;
  /** Die Komponente, in der der die Maustaste los gelassen wurde */
  private static Component lastComponentByMouseEvent;

  private MeMateActionBarButton button;
  private Runnable              exited;
  private Runnable              entered;

  /**
   * @param button {@link ActionBarButton}
   * @param exited Aktion bei {@link MouseListener#mouseExited(MouseEvent)}.
   * @param entered Aktion bei {@link MouseListener#mouseEntered(MouseEvent)}.
   */
  public MeMateActionBarListener( final MeMateActionBarButton button, final Runnable exited, final Runnable entered )
  {
    this.button = button;
    this.exited = exited;
    this.entered = entered;
  }

  @Override
  public void mousePressed( final MouseEvent mouseEvent )
  {
    if ( !(button.isEnabled()) )
    {
      return;
    }

    if ( mouseEvent.getButton() == MouseEvent.BUTTON1 )
    {
      componentByMousePressed = mouseEvent.getComponent();
      lastComponentByMouseEvent = mouseEvent.getComponent();

      button.showPressedStyle();
    }
  }

  @Override
  public void mouseReleased( final MouseEvent mouseEvent )
  {
    if ( !(button.isEnabled()) )
    {
      return;
    }

    if ( isEventValid( mouseEvent ) )
    {
      if ( button.getRunnable() != null )
      {
        button.getRunnable().run();
      }
    }
    button.hidePressedStyle();

    componentByMousePressed = null;
    lastComponentByMouseEvent = null;
  }

  @Override
  public void mouseExited( final MouseEvent e )
  {
    lastComponentByMouseEvent = null;
    if ( !(button.isEnabled()) )
    {
      return;
    }

    button.hidePressedStyle();
    exited.run();
  }

  @Override
  public void mouseEntered( final MouseEvent e )
  {
    lastComponentByMouseEvent = e.getComponent();

    if ( !(button.isEnabled()) )
    {
      return;
    }

    entered.run();
    if ( lastComponentByMouseEvent.equals( componentByMousePressed ) )
    {
      button.showPressedStyle();
    }
  }

  private boolean isEventValid( final MouseEvent event )
  {
    if ( componentByMousePressed == null )
    {
      return false;
    }

    if ( !componentByMousePressed.equals( lastComponentByMouseEvent ) )
    {
      return false;
    }

    //Keine Aktion ausführen wenn der Tastendruck oder der Buttonklick gepuffert waren, daher länger als 200 Millisekunden her sind:
    final long currentTime = System.currentTimeMillis();
    final long difference = currentTime - event.getWhen();
    return difference <= 200;
  }
}

