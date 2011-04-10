package org.rasterfun.functions

import org.rasterfun.util.RasterPanel
import org.rasterfun.Area

/**
 * 
 */
case class RenderFun(red: Fun,
                     green: Fun,
                     blue: Fun,
                     alpha: Fun = OneFun,
                     x: Float = -2,
                     y: Float = -2,
                     width: Float = 4,
                     height: Float = 4) extends
    Fun(List('red, 'green, 'blue, 'alpha),
        List('x, 'y, 'width, 'height)) {

  def apply(x: Float, y: Float, sampleSize: Float) = 0f // This Fun renders, doesn't really return anything

  def createPanel(): RasterPanel = {
    new RasterPanel(Area(x, y, width, height), red, green, blue, alpha)
  }

}