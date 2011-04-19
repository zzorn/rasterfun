package org.rasterfun

import components._
import functions._
import library.{Category, Library}
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


    val orangey = new Blend(new Noise(2, 3),
                          new Solid(1f, 0.5f),
                          new Noise())
    orangey.name := "Orangey"

    val pink = new Solid(0.5f, 0.2f, 0.6f)

    val ocean = new Solid(0, 0.1f, 0.4f)
    ocean.name := "Ocean"

    val sea = new Blend(pink,
                        new Blend(ocean,
                                  new Noise(1, 2, 123.4f, 12.3f),
                                  new Noise(9, 8, 543123.34f)),
                        new Noise(2, 3))

    val blend = new Blend(orangey, sea, new Noise(0.4f, 2, 98.3f))
    blend.name := "Mystery Coral"

    val model = new Group(blend)

    val library = new Library()

    val userComps = new Category(Symbol("My Components"))
    library.root.addCategory(userComps)
    val corals = new Category(Symbol("Corals"))
    userComps.addCategory(corals)

    corals.addComponent(model.copyTree)
    corals.addComponent(orangey.copyTree)
    corals.addComponent(sea.copyTree)

    val ui = new RasterfunUi(library)

    ui.setModel(model)

  }

}