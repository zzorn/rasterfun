package org.rasterfun.ui.library

import org.rasterfun.component.Comp
import java.awt.event.{MouseEvent, MouseAdapter}
import org.rasterfun.ui.{Source, UiSettings, Preview}

/**
 * 
 */

class LibraryCompView(val component: Comp)
        extends Preview(
          component,
          showTitle = true,
          size = UiSettings.libraryPreviewSize) {

  addMouseListener( new MouseAdapter{
    override def mouseClicked(e: MouseEvent) {
      UiSettings.sourceManager.setSource(LibraryCompView.this)
    }

    override def mouseDragged(e: MouseEvent) {
      // TODO:
    }
  })

}