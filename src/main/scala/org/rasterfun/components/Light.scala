package org.rasterfun.components

import org.rasterfun.component.Comp
import java.awt.Color
import org.scalaprops.Property
import org.scalaprops.ui.editors.SliderFactory._
import org.scalaprops.ui.editors.{ColoredSliderBackgroundPainter, SliderFactory}
import simplex3d.math.float._
import java.lang.Math
import java.text.Normalizer
import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._

/**
 * Directed light.
 */
class Light extends Comp {

  private def makeProp(name: Symbol, value: Float, color: Color): Property[Float] = {
    property(name, value)
            .onValueChange{ (o: Float, n: Float) => updateColor()}
            .editor(new SliderFactory(0f, 1f, backgroundPainter =
                new ColoredSliderBackgroundPainter(Color.WHITE, color)))
  }

  val colorMap  = addInput('colorMap, new SolidColor())
  val normalMap = addInput('normalMap, new Noise())

  val red    = makeProp('red,   1f, new Color(0.7f, 0.2f, 0.2f))
  val green  = makeProp('green, 1f, new Color(0.2f, 0.7f, 0.2f))
  val blue   = makeProp('blue,  1f, new Color(0.2f, 0.2f, 0.7f))
  val brightness = makeProp('brightness, 1f, new Color(0.5f, 0.5f, 0.5f))

  val lightX= p('lightX, 0.5f)
  val lightY= p('lightY, 0.5f)
  val lightZ= p('lightZ, 0.5f)


  private var _lightColor: Vec4 = Vec4(1,0,0,1)

  updateColor()


  private def updateColor() {
    _lightColor = Vec4(red(), green(), blue(), 1f)
  }

  def rgba(pos: inVec2): Vec4 = {

    val baseColor = colorMap().rgba(pos)
    val surfaceNormal = normalMap().rgba(pos)
    val lightNormal = normalize(Vec3(lightX(), lightY(), lightZ()))

    val overlap = max(0f, dot(surfaceNormal.xyz, lightNormal))

    baseColor * (_lightColor * overlap * brightness())
  }


}

