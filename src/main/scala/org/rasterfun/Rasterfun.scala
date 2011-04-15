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
    noiseA.setBeanName('noiseA)
    noiseA.scale := 2
    noiseA.detail := 3

    val noiseB = new Noise()
    noiseB.setBeanName('noiseB)

    val color = new Solid()
    color.red := 1f
    color.green := 0.5f

    val blend = new Blend()
    blend.foreground := noiseA
    blend.background := color
    blend.selector := noiseB

    val noiseC = new Noise()
    noiseC.setBeanName('noiseC)

    val noiseD = new Noise()
    noiseD.setBeanName('noiseD)
    noiseD.scale := 2
    noiseD.detail := 3

    val noiseE = new Noise()
    noiseE.setBeanName('noiseE)

    val noiseF = new Noise()
    noiseF.setBeanName('noiseF)

    val blend3 = new Blend()
    blend3.setBeanName('blend3)
    blend3.foreground := noiseD
    blend3.selector := noiseF

    val blend2 = new Blend()
    blend2.foreground := blend
    blend2.background := noiseC
    blend2.selector := blend3

    val model = new Group(blend2)


    val ui = new RasterfunUi()

    ui.setModel(model)

  }

}