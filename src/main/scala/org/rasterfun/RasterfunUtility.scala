package org.rasterfun

import functions._
import util.{RasterPanel, SimpleFrame}

/**
 * 
 */
object RasterfunUtility {

  def main(args: Array[ String ])
  {
    val h = AddFun(ScaleFun(NoiseFun(1, 0.2, 0, 2134),
                            NoiseFun(0.2, 0.1, 0, 12.3)),
                   NoiseFun(2.3, 0.04, 0, 1241.3))
    val s = NoiseFun(0.4, 1, 0, 3123.123)
    val l = NoiseFun(0.7, 0.25, 0.5)

    val renderFun = new RenderFun(
      new RedFun(h, s, l),
      new GreenFun(h, s, l),
      new BlueFun(h, s, l))

    println(renderFun.toXml)

    val frame = new SimpleFrame("Rasterfun", renderFun.createPanel)

  }

}