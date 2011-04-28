package org.rasterfun.ui

import graph.GroupCompView
import library.LibraryCompView
import org.rasterfun.component.Comp
import org.rasterfun.components.Empty


/**
 * Used to keep track of the selected component.
 */
class SelectionManager {

  private var _selectionListeners: List[(GroupCompView) => Unit] = Nil

  private var _selection: GroupCompView = null

  def selection: GroupCompView = _selection

  def setSelection(c: GroupCompView ) {
    if (c != _selection) {
      if (_selection != null) _selection.state = Normal

      _selection = c

      if (_selection != null) _selection.state = Selected

      _selectionListeners foreach {_(_selection)}
    }
  }

  def addSelectionListener(listener: (GroupCompView) => Unit) { _selectionListeners ::= listener}
  def removeSelectionListener(listener: (GroupCompView) => Unit) { _selectionListeners = _selectionListeners.filterNot(_ == listener)}

  def copyComponent: Comp = {
    if (selection == null) new Empty()
    else selection.comp.copyComponent
  }

  def copyTree: Comp = {
    if (selection == null) new Empty()
    else selection.comp.copyTree
  }

  def hasSelection: Boolean = _selection != null

  def clearSelection() {setSelection(null)}

}