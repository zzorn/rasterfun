package org.rasterfun.ui

import net.miginfocom.swing.MigLayout
import javax.swing.JPanel
import org.rasterfun.component.Component
import java.awt.Dimension
import simplex3d.math.Vec2i

/**
 * 
 */
class ComponentView(component: Component, parentView: java.awt.Component) extends RichPanel(component.name, true) with TreeNodeComponent {

  val pos: Vec2i = Vec2i(0,0)

  setPreferredSize(UiSettings.componentSize)

  override def parentTreeNode = parentView
}