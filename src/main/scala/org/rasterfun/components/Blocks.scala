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
    val w: Float = if (tileSizeHorizontal() <= 0) 1f else tileSizeHorizontal()
    val h: Float = if (tileSizeVertical() <= 0) 1f else tileSizeVertical()
    val offs: Float = tileOffset()

    val cellX = (scala.math.floor(pos.x / w)).toInt
    val cellY = (scala.math.floor(pos.y / h)).toInt

    val xPos: Float = pos.x - w * offs * cellY

    var intraCellX: Float = (xPos / w) % 1f
    var intraCellY: Float = (pos.y / h) % 1f
    if (intraCellX < 0) intraCellX = 1f + intraCellX
    if (intraCellY < 0) intraCellY = 1f + intraCellY

    val vertDist = 1f - abs(intraCellX - 0.5f) * 2f
    val horDist  = 1f - abs(intraCellY - 0.5f) * 2f

    val edgeDist = min(vertDist, horDist)

    edgeDist * 2f - 1f
  }

}