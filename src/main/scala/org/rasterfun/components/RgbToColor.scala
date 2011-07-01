package org.rasterfun.components

import simplex3d.math.float._
import org.rasterfun.util.ColorUtils
import org.rasterfun.component.Comp

/**
 * 
 */

class RgbToColor extends Comp {
   val red   = addInput('red, new SolidIntensity(0.5f))
   val green = addInput('green, new SolidIntensity(0.5f))
   val blue  = addInput('blue, new SolidIntensity(0.5f))
   val alpha = addInput('alpha, new SolidIntensity(1f))

   def rgba(pos: inVec2): Vec4 = {

     val r = red().intensity(pos)
     val g = green().intensity(pos)
     val b = blue().intensity(pos)
     val a = alpha().intensity(pos)

     Vec4(r, g, b, a)
   }

}