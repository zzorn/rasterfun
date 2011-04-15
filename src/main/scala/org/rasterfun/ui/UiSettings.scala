package org.rasterfun.ui

import java.awt.{Color, Dimension}

/**
 * 
 */
object UiSettings {

  val componentWidth = 128
  val componentHeight = 128
  var componentSize = new Dimension(componentWidth, componentHeight)
  var componentColor = Color.LIGHT_GRAY
  var componentViewBackgroundColor = new Color(0.5f, 0.5f, 0.5f)

  val extraScrollSpace = 64

  val scrollUnitSize = 30
  val scrollBlockSize = scrollUnitSize * 10
}