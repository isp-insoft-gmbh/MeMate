/**
 * © 2020 isp-insoft GmbH
 */
package com.isp.memate.util;

/**
 * Wird verwendet, um auf Änderungen von {@link ObservableValue} zu reagieren.
 *
 * @author nwe
 * @since 09.12.2020
 * @param <T> Typ der Werte
 */
@FunctionalInterface
public interface ValueListener<T>
{
  /**
   * @param oldValue vorheriger Wert
   * @param newValue neuer Wert
   */
  void valueChanged( T oldValue, T newValue );
}