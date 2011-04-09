package org.rasterfun

import functions._
import util.{RasterPanel, SimpleFrame}

/**
 * 
 */

object RasterfunUtility {

  def main(args: Array[ String ])
  {
    val fun = RasterFunction(Map())

    val h = ScaleFun(NoiseFun(1, 1, 0, 2134),
                     NoiseFun(0.2, 1, 0, 12.3))
    val s = NoiseFun(1, 1, 0, 3123.123)
    val l = NoiseFun(4, 0.25, 0.5)

    fun.set('red, new RedFun(h, s, l))
    fun.set('green, new GreenFun(h, s, l))
    fun.set('blue, new BlueFun(h, s, l))

    val view = new RasterPanel(fun)
    val frame = new SimpleFrame("Rasterfun", view)
  }

}