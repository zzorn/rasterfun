package org.rasterfun.ui.graph

import net.miginfocom.swing.MigLayout
import org.rasterfun.component.Comp
import simplex3d.math.Vec2i

import org.rasterfun.util.treelayout.TreeNodeComponent
import org.rasterfun.ui.{Preview, UiSettings}
import java.awt.event.{MouseEvent, MouseAdapter}
import org.rasterfun.components.Empty

/**
 * A component in a GroupView
 */
class GroupCompView(val groupView: GroupView,
                    var component: Comp,
               parentView: GroupCompView = null)
        extends Preview(component,
                        showTitle = true,
                        size = UiSettings.graphComponentSize)
                with TreeNodeComponent {

  override def parentTreeNode = parentView

  addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent) {
      if (UiSettings.sourceManager.hasSource) {
        if (!component.isInstanceOf[Empty]) {
          // Ask if we should replace the component if it is not empty

          // TODO
        }

        // Get new component
        val oldComp = comp
        val newComp = UiSettings.sourceManager.copySourceTree

        // Replace component
        oldComp.replaceWith(newComp)

        // Clear source
        UiSettings.sourceManager.clearSource()
      }
    }
  })

}