package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.ui.editors.SliderFactory
import org.scalaprops.ui.editors.SliderFactory._

/**
 * Tile or board pattern.
 */
class Blocks extends IntensityComp {

  val borderAmount =  p('borderAmount, 0.2f).editor(new SliderFactory(0f, 1f))
  val tileSizeVertical =  p('tileDensityVertical, 2f).editor(new SliderFactory(0f, 4f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))
  val tileSizeHorizontal =  p('tileDensityHorizontal, 2f).editor(new SliderFactory(0f, 4f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))
  val tileOffset =  p('tileOffset, 0.5f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false))

  protected def basicIntensity(pos: inVec2): Float = {
    val vCell = pos.x % tileSizeHorizontal()
    val hCell = pos.y % tileSizeVertical() + tileOffset()
    
    val edgeDist = max(vCell, hCell)

    edgeDist
  }

}