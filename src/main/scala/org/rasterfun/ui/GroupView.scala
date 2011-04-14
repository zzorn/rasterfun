package org.rasterfun.ui

import org.rasterfun.components.Group
import org.rasterfun.component.Component
import javax.swing.{JLabel, JPanel}
import java.awt.{Dimension, Color}

/**
 * View of a group of nodes.
 */
class GroupView extends JPanel(new TreeLayoutManager()) {

  private var _group: Group = null
  private var components: Map[Component, ComponentView] = Map[Component, ComponentView]()

  def group = _group

  def group_= (group: Group) {
    // Remove old
    components.values foreach (v => remove(v))

    _group = group

    if (group != null) addComponents(group)
  }

  private def addComponents(group: Group) {
    // New components
    components = (group.components map {(c: Component) => (c, new ComponentView(c))}).toMap

    // Add to UI
    components.values foreach {(v: ComponentView) => add(v) }

    // Arrange
    // TODO

    // Setup connection lines to be drawn
    // TODO

    revalidate()
  }

}

