package org.rasterfun.components

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.ui.editors.SliderFactory
import org.scalaprops.ui.editors.SliderFactory._
import java.util.Random
import org.rasterfun.util.{FastXorShift, SegmentCell, XorShiftRandom}

/**
 * Tile or board pattern.
 */
class Blocks extends IntensityComp {

  val tileWidth =  p('tileWidth, 2f).editor(new SliderFactory(0f, 4f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val tileHeight =  p('tileHeight, 2f).editor(new SliderFactory(0f, 4f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val tileShift =  p('tileShift, 0f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val widthVariation =  p('widthVariation, 0.2f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val heightVariation =  p('heightVariation, 0.2f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val shiftVariation =  p('shiftVariation, 0.2f).editor(new SliderFactory(0f, 1f,restrictNumberFieldMax = false,restrictNumberFieldMin = false)) onChange(updateSettings())
  val seed =  p('seed, 42)

  private val verCell = new SegmentCell()
  private val horCell = new SegmentCell()

  updateSettings()

  val r = new Random()

  private def updateSettings() {
    horCell.size = tileWidth()
    horCell.sizeVariation = widthVariation()
    verCell.size = tileHeight()
    verCell.sizeVariation = heightVariation()
  }

  protected def basicIntensity(pos: inVec2): Float = {

    val intraCellY = verCell.calculateCell(pos.y, seed())

    val shift = tileShift() * tileWidth() * verCell.cellId +
            (if (shiftVariation() > 0) tileWidth() * shiftVariation() * randomMinusOneToOne(verCell.cellId)
             else 0f)

    val intraCellX = horCell.calculateCell(pos.x + shift, verCell.cellId ^ seed())

    // Distance from edges
    val vertDist = 1f - abs(intraCellX - 0.5f) * 2f
    val horDist  = 1f - abs(intraCellY - 0.5f) * 2f
    val edgeDist = min(vertDist, horDist)

    edgeDist * 2f - 1f

//    val xId = horCell.cellId
//    val yId = verCell.cellId

/*
    r.setSeed(xId)
    r.setSeed(r.nextInt() ^ yId)
    r.setSeed(r.nextInt())
    r.nextFloat() * 2f - 1f
*/
  }

  private def randomMinusOneToOne(s: Int): Float = {
    r.setSeed(s)
    r.nextFloat()
    r.setSeed(r.nextLong() ^ seed())
    r.nextFloat()
    r.nextFloat() * 2f - 1f
  }

}