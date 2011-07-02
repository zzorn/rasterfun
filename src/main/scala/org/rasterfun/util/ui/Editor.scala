package org.rasterfun.util.ui

import net.miginfocom.swing.MigLayout
import javax.swing.{JPanel, JComponent}

/**
 * Base class for editor UIs.
 */
abstract class Editor[T] extends JPanel() {

  private var _value: T = initialValue
  private var listeners: Set[(T, T) => Unit] = Set()

  private var uiChangeOngoing = false
  private var externalChangeOngoing = false


  def init() {
    buildUi(_value, onUiChange _)
    updateUi(_value)
  }

  /**
   * Returns the edited value.
   */
  final def value: T = _value

  /**
   * Sets the value in the editor, and notifies the listeners.
   */
  final def value_= (v: T) {
    if (v != _value) {
      val oldValue = _value
      _value = v

      if (!uiChangeOngoing) updateUi(value)
      notifyListeners(oldValue, value)
    }
  }

  /**
   * Adds a listener that is notified with the old and new value when the value changes.
   * The same listener can only be registered once, extra additions are ignored.
   */
  final def addListener(listener: (T, T) => Unit) {listeners += listener}

  /**
   * Removes a listener, if present.
   */
  final def removeListener(listener: (T, T) => Unit) {listeners -= listener}

  /**
   * Should create and setup the UI.
   * @param initialValue the value on startup.  By default it's 0 for numbers and null for refs, and false for booleans.
   * @param uiValueChangeListener should be called when the UI is used to change the value.
   */
  protected def buildUi(initialValue: T, uiValueChangeListener: T => Unit)

  /**
   * Called after buildUi is called,
   * and whenever the value is change externally and the UI should show the new value.
   */
  protected def updateUi(newValue: T)

  /**
   * The initial value in the editor.
   */
  protected def initialValue: T

  /**
   * Should be called when the ui is changed.
   * This method is also passed into buildUi method as a parameter.
   */
  protected final def onUiChange(v: T) {
    // Guard for loops in case UI changes the value on update
    if (!uiChangeOngoing) {
      uiChangeOngoing = true
      value = v
      uiChangeOngoing = false
    }
  }

  private def notifyListeners(oldValue: T, newValue: T) {
    // Guard for potential infinite update loops in case a listener changes the value.
    if (!externalChangeOngoing) {
      externalChangeOngoing = true
      listeners foreach { l => l(oldValue, newValue) }
      externalChangeOngoing = false
    }
  }


}