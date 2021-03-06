package org.rasterfun.library

import org.rasterfun.components._
import org.rasterfun.component.Comp
import org.rasterfun.geometry.{Segment, StraightLine}

/**
 * 
 */

object BuiltinCategoryBuilder {

  def createBuiltins(root: Category): Category = {
    def cat(name: Symbol): Category = {
      val c = new Category(name)
      root.addCategory(c)
      c
    }

    val basic = cat('Basic)
    basic.addComponent(new Empty())
    basic.addComponent(new Combine())
    basic.addComponent(new Operation())
    basic.addComponent(new SolidColor())
    basic.addComponent(new Noise())
    basic.addComponent(new Select())
    basic.addComponent(new HslAdjust())
    basic.addComponent(new Displace())
    basic.addComponent(new Stripes())
    basic.addComponent(new Radial())
    basic.addComponent(new SolidIntensity())
    basic.addComponent(new RgbToColor())
    basic.addComponent(new HslToColor())
    basic.addComponent(new HeightToNormalMap())
    basic.addComponent(new Blocks())
    basic.addComponent(new Light())
    basic.addComponent(new Segment())
 
    val examples = cat('Examples)
//    examples.addComponent(buildCoralExample())
    
    basic
  }

/*
  def buildCoralExample(): Comp = {
    val orangey = new Blend(new Noise(2, 3),
                          new SolidColor(1f, 0.5f),
                          new Noise())
    orangey.name := "Orangey"

    val pink = new SolidColor(0.5f, 0.2f, 0.6f)

    val ocean = new SolidColor(0, 0.1f, 0.4f)
    ocean.name := "Ocean"

    val sea = new Blend(pink,
                        new Blend(ocean,
                                  new Noise(1, 2, 123.4f, 12.3f),
                                  new Noise(9, 8, 543123.34f)),
                        new Noise(2, 3))

    val blend = new Blend(orangey, sea, new Noise(0.4f, 2, 98.3f))
    blend.name := "Mystery Coral"
    blend
  }
*/
}