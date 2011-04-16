package org.rasterfun.ui

import library.LibraryCompView
import org.rasterfun.component.Comp
import org.rasterfun.components.Empty

/**
 * Used to keep track of insertion of components from library into graph.
 */
class SourceManager {

  private var libSource: LibraryCompView = null

  def setSource(lib: LibraryCompView) {
    if (lib == libSource) {
      if (libSource != null) {
        // Click again to de-select
        libSource.state = Normal
      }
    }
    else {
      if (libSource != null) libSource.state = Normal

      libSource = lib

      if (libSource != null) libSource.state = Source
    }
  }

  def copySourceComponent: Comp = {
    if (libSource == null) new Empty()
    else libSource.comp.copyComponent
  }

  def copySourceTree: Comp = {
    if (libSource == null) new Empty()
    else libSource.comp.copyTree
  }

  def hasSource: Boolean = libSource != null

  def clearSource() {setSource(null)}

}