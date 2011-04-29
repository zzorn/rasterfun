package org.rasterfun.util

import java.awt.{Component, Dimension, Container, FlowLayout}

/**
 * A FlowLayout subclass that wraps components.
 *
 * Based on http://tips4java.wordpress.com/2008/11/06/wrap-layout/
 */
class WrapLayout(align: Int = FlowLayout.LEFT, hGap: Int = 5, vGap: Int = 5) extends FlowLayout(align, hGap, vGap) {

  private var preferredSize: Dimension = null

  override def layoutContainer(target: Container) {
    def getRootContainer(c: Container): Container = {
      if (c.getParent == null) c
      else getRootContainer(c.getParent)
    }

    val size = preferredLayoutSize(target)
    if (size.equals(preferredSize)) {
      super.layoutContainer(target)
    } else {
      preferredSize = size
      getRootContainer(target).validate()
    }
  }

  override def preferredLayoutSize(target: Container): Dimension = {
    calculateSize(target, _.getPreferredSize)
  }

  override def minimumLayoutSize(target: Container): Dimension =  {
    calculateSize(target, _.getMinimumSize)
  }


  private def calculateSize(target: Container, sizeCalc: Component => Dimension): Dimension  = {
    val insets = target.getInsets
    val hGap = getHgap
    val vGap = getVgap
    val gapAndInsets = insets.left + insets.right + (hGap * 2)
    val maxWidth = if (target.getSize.width > 0) target.getSize.width - gapAndInsets else Integer.MAX_VALUE
    val dimension = new Dimension(0, 0)

    val memberCount = target.getComponentCount
    var width = 0
    var height = 0
    var i = 0
    while (i < memberCount) {
      val m = target.getComponent(i)
      if (m.isVisible) {
        val size = sizeCalc(m)
        if (width + size.width > maxWidth) {
          updateSize(dimension, width, height)
          width = 0
          height = 0
        }
        if (width != 0) {
          width += hGap
        }
        width += size.width
        height = math.max(height, size.height)
      }

      i += 1
    }

    updateSize(dimension, width, height)

    dimension.width += gapAndInsets - hGap - 1
    dimension.height += insets.top + insets.bottom + vGap * 2

    return dimension
  }

  private def updateSize(dim: Dimension , rowWidth: Int, rowHeight: Int) {
    dim.width = math.max(dim.width, rowWidth)
    if (dim.height > 0) dim.height += getVgap
    dim.height += rowHeight
  }

}