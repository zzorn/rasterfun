package org.rasterfun.ui

import java.lang.String
import java.awt._

/**
 * 
 */

class GraphLayoutManager extends LayoutManager2 {

  def addLayoutComponent(name: String, comp: Component) {}

  override def addLayoutComponent(comp: Component, constraints: Object) {}

  def removeLayoutComponent(comp: Component) {}

  def preferredLayoutSize(parent: Container) = new Dimension(300, 300) // TODO: get bounds of all components

  def minimumLayoutSize(parent: Container) = new Dimension(100,200)

  def layoutContainer(parent: Container) {

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