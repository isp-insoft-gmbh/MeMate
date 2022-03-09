/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Erzeugt ein value vom Typ T auf welches Listener angemeldet werden können.
 * Sollte sich die Value ändern, so werden alle aktiven Listener durch ein Event
 * benachrichtigt.
 *
 * @author nwe
 * @param <T> Elementtyp der Value.
 * @since 09.12.2020
 */
public class ObservableValue<T>
{
  private final List<ValueListener<T>> listeners;
  private T                            value;

  /**
   * @param value initialer Wert
   */
  public ObservableValue( final T value )
  {
    listeners = new ArrayList<>();
    this.value = value;
  }

  /**
   * Fügt einen {@link ValueListener} hinzu, welcher bei Änderungen benachrichtigt wird.
   *
   * @param listener der neue {@link ValueListener}
   */
  public void addListener( final ValueListener<T> listener )
  {
    listeners.add( listener );
  }

  /**
   * Entfernt einen {@link ValueListener}, damit dieser bei Änderungen nicht mehr benachrichtigt wird.
   *
   * @param listener der zu entfernende {@link ValueListener}
   */
  public void removeListener( final ValueListener<T> listener )
  {
    listeners.remove( listener );
  }

  /**
   * Wirft ein Event an alle aktiven Listener
   *
   * @param oldValue vorheriger Wert
   * @param newValue neuer Wert
   *
   */
  public void fireValueUpdate( final T oldValue, final T newValue )
  {
    //Defensive copy um potentielle ConcurrentModificationException zu verhindern.
    for ( final ValueListener<T> listener : new ArrayList<>( listeners ) )
    {
      listener.valueChanged( oldValue, newValue );
    }
  }

  /**
   * Updated den neuen und alten Wert und benachrichtigt alle Listener
   *
   * @param newValue der zu setzende Wert
   */
  public void setValue( final T newValue )
  {
    if ( !Objects.equals( value, newValue ) )
    {
      final T oldValue = value;
      value = newValue;
      fireValueUpdate( oldValue, newValue );
    }
  }

  /**
   * @return der aktuelle wert
   */
  public T getValue()
  {
    return value;
  }
}