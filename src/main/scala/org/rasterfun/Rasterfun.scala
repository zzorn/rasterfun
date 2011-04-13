package org.rasterfun

import components._
import functions._
import ui.RasterfunUi
import util.{RasterPanel, SimpleFrame}

/**
 * 
 */
object Rasterfun {

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


    val noiseA = new Noise()
    noiseA.scale := 2
    noiseA.detail := 3

    val noiseB = new Noise()
    val color = new Solid()
    color.red := 1f
    color.green := 0.5f

    val blend = new Blend()
    blend.foreground := noiseA
    blend.background := color
    blend.selector := noiseB

    val model = new Group(blend, List(blend, noiseA, noiseB, color))


    val ui = new RasterfunUi()

    ui.setModel(model)

  }

}