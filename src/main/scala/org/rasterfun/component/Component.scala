package org.rasterfun.component

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.{Property, Bean}
import org.rasterfun.util.ColorUtils

/**
 * 
 */
// TODO: Cache a value caclulated for a specific coordinate,
//       invalidate cached value when own or any input components properties change
// TODO: Some effects may need to know e.g. max and min intensity of an image.
//       To get that, there may need to be a pre-compute step that computes all
//       pixels of the current tree at some lowish resolution.
// TODO: For wrap around etc, we could specify that original input x and y range from 0 to 1.
trait Component extends Bean {

  private var _inputNames: List[Symbol] = Nil

  def name: String = beanName.name.replace("Component", "")

  def inputNames: List[Symbol] = _inputNames

  def inputComponents: List[Component] = inputNames flatMap (n => get[Component](n))

  def addInput(name: Symbol): Property[Component] = {
    require(!_inputNames.contains(name), "Input name already exists")
    _inputNames = _inputNames ::: List(name)
    p[Component](name, null)
  }

  /**
   * Get single intensity channel.
   * Defaults to average of color channel values.
   */
  def intensity(pos: inVec2): Float = {
    val c = rgba(pos)
    (c.r + c.g + c.b) / 3f
  }

  /**
   * Get RGBA channels.
   * @param posSize x and y position, and the sample size
   * @return rgba color at the sampled area.
   */
  def rgba(pos: inVec2): Vec4

  /**
   * Get RGBA channels.
   * @param posSize x and y position, and the sample size
   * @return rgba color at the sampled area.
   */
  def apply(pos: inVec2): Vec4 = rgba(pos)

  /**
   * The available channels.
   */
  def channels: Set[Symbol] = Set('intensity, 'red, 'green, 'blue, 'alpha, 'hue, 'saturation, 'lightness)

  /**
   * Get channel value at specified position of specified name (e.g. height, luminance, specular, etc.).
   * Also supports the standard channels red, green, blue, alpha, hue, sat, lightness.
   * Could also be used for more special channels, such as cartographic channels (height, terrain type, soil parameters, etc..)
   */
  def channel(channel: Symbol, pos: Vec2): Float = {
    channel match {
      case 'intensity => intensity(pos)
      case 'red => rgba(pos).r
      case 'green => rgba(pos).g
      case 'blue => rgba(pos).b
      case 'alpha => rgba(pos).a
      case 'hue => ColorUtils.hue(rgba(pos))
      case 'saturation => ColorUtils.saturation(rgba(pos))
      case 'lightness => ColorUtils.lightness(rgba(pos))
      case _ => 0f
    }
  }

  /** Creates a copy of this component without any properties. */
  def copyComponent(): Component = {
    getClass.newInstance().asInstanceOf[Component]
  }

  /** Creates a copy of this component including input components. */
  def copyTree(): Component = {
    val copy = copyComponent()

    // Copy properties
    properties.foreach{ p =>
      {
        val propName: Symbol = p._1
        if (inputNames.contains(propName) && p._2.get != null) {
          // Copy child tree
          copy.set[Component](propName, get[Component](propName, null).copyTree())
        }
        else {
          // Copy normal property
          copy.set(propName, p._2.get)
        }
      }
    }

    copy
  }


  def toXml: String = ""

}


