package org.rasterfun.util.treelayout

import java.awt.Dimension

/**
 * Orientation of the tree created with TreeLayoutManager.
 */
sealed abstract class TreeOrientation(vertical : Boolean, ux: Float, uy: Float, vx: Float, vy: Float) {

  def x(u: Float, v: Float): Float = u * ux + v * vx
  def y(u: Float, v: Float): Float = v * vy + u * uy

  def uSize(dimension: Dimension): Float = if (vertical) dimension.getWidth.toFloat else dimension.getHeight.toFloat
  def vSize(dimension: Dimension): Float = if (vertical) dimension.getHeight.toFloat else dimension.getWidth.toFloat

}

case object TopToBottom extends TreeOrientation(true, 1, 0, 0, 1)
case object BottomToTop extends TreeOrientation(true, 1, 0, 0, -1)
case object LeftToRight extends TreeOrientation(false, 0, -1, 1, 0)
case object RightToLeft extends TreeOrientation(false, 0, -1, -1, 0)
