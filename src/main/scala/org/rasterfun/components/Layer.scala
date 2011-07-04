package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.ui.editors.SliderFactory
import org.rasterfun.component.Comp

/**
 * 
 */
// TODO: A way to supply parameters to input nodes?  Or to select nodes from library, with certain interface.
class Layer extends Comp {
  val heightMap  = addInput('heightMap, new Noise(_seed = 43432))
  val background = addInput('background, new Noise(_seed = 81324))

  def rgba(pos: _root_.simplex3d.math.float.inVec2) = null
}