package org.rasterfun.ui

import org.rasterfun.components.Group
import org.rasterfun.component.Component
import java.awt.{Dimension, Color}
import javax.swing.{BorderFactory, JLabel, JPanel}

/**
 * View of a group of nodes.
 */
class GroupView extends JPanel(new TreeLayoutManager()) {

  private var _group: Group = null
  private var components: Map[Component, ComponentView] = Map[Component, ComponentView]()

  setBorder(BorderFactory.createLineBorder(Color.RED)) // For debugging layout

  def group = _group

  def group_= (group: Group) {
    // Remove old
    components.values foreach (v => remove(v))

    _group = group

    if (group != null) addComponents(group)
  }

  private def addComponents(group: Group) {

    def addWithChildren(comp: Component, parent: ComponentView) {
      if (comp != null) {

        // Create view
        val view = new ComponentView(comp, parent)

        // Store reference
        components += comp -> view

        // Add to UI
        add(view)

        // Add children
        comp.inputComponents foreach {c => addWithChildren(c, view) }
      }
    }

    addWithChildren(group.root, null)

    revalidate()
  }



}

