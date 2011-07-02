package org.rasterfun.components

import org.rasterfun.component.Comp
import java.awt.Color
import org.scalaprops.Property
import org.scalaprops.ui.editors.SliderFactory._
import org.scalaprops.ui.editors.{ColoredSliderBackgroundPainter, SliderFactory}
import java.lang.Math
import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.rasterfun.util.coloreditor.ColorEditorFactory

/**
 * Directed light.
 */
class Light extends Comp {

  private def makeProp(name: Symbol, value: Float, color: Color, max: Float): Property[Float] = {
    property(name, value)
            .editor(new SliderFactory(0f, max, backgroundPainter =
                new ColoredSliderBackgroundPainter(Color.WHITE, color)))
  }

  val colorMap  = addInput('colorMap, new SolidColor())
  val normalMap = addInput('normalMap, new Noise())

  val lightColor = p('lightColor, Vec4(1, 1, 1, 1)).editor(new ColorEditorFactory(false))
  val ambientColor = p('ambientColor, Vec4(1, 1, 1, 1)).editor(new ColorEditorFactory(false))

  val brightness = makeProp('brightness, 1f, new Color(0.5f, 0.5f, 0.5f), 3f)

  val lightX= p('lightX, 0.5f)
  val lightY= p('lightY, 0.5f)
  val lightZ= p('lightZ, 0.5f)


  def rgba(pos: inVec2): Vec4 = {

    val baseColor = colorMap().rgba(pos)
    val surfaceNormal = normalMap().rgba(pos)
    val lightNormal = normalize(Vec3(lightX(), lightY(), lightZ()))

    val overlap = max(0f, dot(surfaceNormal.xyz, lightNormal))

    baseColor * (lightColor() * overlap * brightness())
  }


}

