package org.rasterfun.ui

import java.lang.String
import java.awt.Component
import java.awt.LayoutManager2
import java.awt.Container
import java.awt.Dimension

trait TreeNodeComponent {
  def children: List[Component]
}

/**
 * Requires added components to be TreeNodeComponent,
 * or add method to provide child components for each component.
 */
class TreeLayoutManager extends LayoutManager2 {

  private case class TreeNode(component: Component, children: List[TreeNode])

  private var roots: List[TreeNode] = Nil
  private var nodes: Map[Component, List[Component]] = Map()

  def addLayoutComponent(name: String, comp: Component) {}

  override def addLayoutComponent(comp: Component, constraints: Object) {
    if (constraints.isInstanceOf[Iterable[Component]]) {
      nodes += (comp -> constraints.asInstanceOf[Iterable[Component]].toList)
    }
    else if (comp.isInstanceOf[TreeNodeComponent]) {
      nodes += (comp -> comp.asInstanceOf[TreeNodeComponent].children)
    }
    else {
      nodes += (comp -> Nil)
    }
  }

  def removeLayoutComponent(comp: Component) {
    nodes -= comp
  }

  def preferredLayoutSize(parent: Container) = new Dimension(300, 300) // TODO: get bounds of all components

  def minimumLayoutSize(parent: Container) = new Dimension(100,200)

  def layoutContainer(parent: Container) {

    // Find root(s)
    

    // Calculate tree(s)



    // Position each
    var i = 0
    parent.getComponents foreach {c =>
      c.setLocation(i * 80, i * 0)
      c.setSize(c.getPreferredSize)
      i += 1
    }

  }

  def invalidateLayout(target: Container) {}

  def getLayoutAlignmentY(target: Container) = 0.5f

  def getLayoutAlignmentX(target: Container) = 0.5f

  def maximumLayoutSize(target: Container) = new Dimension(80, 60)


}