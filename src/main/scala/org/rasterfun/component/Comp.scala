package org.rasterfun.component

import simplex3d.math._
import simplex3d.math.float._
import simplex3d.math.float.functions._
import org.scalaprops.{Property, Bean}
import org.rasterfun.util.ColorUtils
import org.rasterfun.Area
import org.rasterfun.components.Empty
import java.util.HashSet
import collection.JavaConversions._

/**
 * 
 */
// TODO: Cache a value caclulated for a specific coordinate,
//       invalidate cached value when own or any input components properties change
// TODO: Some effects may need to know e.g. max and min intensity of an image.
//       To get that, there may need to be a pre-compute step that computes all
//       pixels of the current tree at some lowish resolution.
// TODO: For wrap around etc, we could specify that original input x and y range from 0 to 1.
// TODO: Better name.  Component and Node are both overloaded to annoyance though, something unique plz.
// TODO: Add parent component reference, so that we can check for cycles
// TODO: Also add link to group that the component is in?  Or it can be retrieved by following parent pointers
trait Comp extends Bean {

  private var _inputNames: List[Symbol] = Nil
  private var _outputName: Symbol = Symbol(null)
  private var _parent: Comp = null

  val name = p('name, getClass.getSimpleName)

  private var _structureListeners: Set[((Comp) => Unit)] = Set()

  def addStructureListener(listener: ((Comp) => Unit)) {_structureListeners += listener}
  def removeStructureListener(listener: ((Comp) => Unit)) {_structureListeners -= listener}

  def outputName: Symbol = _outputName

  def inputNames: List[Symbol] = _inputNames

  def inputComponents: List[Comp] = inputNames flatMap (n => get[Comp](n).filterNot(_ == null))

  protected def addInput(inputName: Symbol, initial: Comp = new Empty): Property[Comp] = {
    require(!_inputNames.contains(inputName), "Input name already exists")
    _inputNames = _inputNames ::: List(inputName)
    initial.setParent(this, inputName)
    p[Comp](inputName, initial).onValueChange{ (oldComp: Comp, newComp: Comp) =>
      oldComp.clearParent()
      newComp.setParent(this, inputName)
    }
  }

  final def parent = _parent

  final def root: Comp = if (_parent == null) this else _parent.root

  private def clearParent() {
    if (_parent != null)
    {
      _parent.removeInput(this)
    }
    _parent = null
    _outputName = Symbol(null)
  }

  private def setParent(p: Comp, inputChannel: Symbol) {
    if (_parent != null)
    {
      _parent.removeInput(this)
    }
    _parent = p
    _outputName = inputChannel
  }

  def removeInput(child: Comp) {
    inputNames find {name => get(name, null) == child} match {
      case Some(n) => set(n, new Empty())
      case None => // Not found, do nothing
    }
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
  def channel(channel: Symbol, pos: inVec2): Float = {
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

  /** Creates a copy of this component, with input references replaced with empty components. */
  def copyComponent: Comp = createCopyWithProperties(false)

  /** Creates a copy of this component including input components. */
  def copyTree: Comp = createCopyWithProperties(true)

  private def createCopyWithProperties(copyInputComponents: Boolean): Comp = {
    val copy = createCopy
    
    properties.foreach{ p =>
      val propName: Symbol = p._1
      val propValue: AnyRef = p._2.get.asInstanceOf[AnyRef]

      if (propValue != null && propValue.isInstanceOf[Comp]) {
        val childComp = if (copyInputComponents) propValue.asInstanceOf[Comp].copyTree else new Empty()
        copy.set[Comp](propName, childComp)
      }
      else {
        // Copy normal property
        copy.set(propName, propValue)
      }
    }

    copy
  }

  /**
   * Create a copy of this component, including any vars and vals.
   * Does not need to copy bean properties.
   * By default creates a new instance by calling a no-argument constructor.
   */
  protected def createCopy: Comp = getClass.newInstance().asInstanceOf[Comp]


  def render(buffer: Array[Int], width: Int, height: Int, area: Area) {
    require(buffer != null)
    require(buffer.length == width * height, "Buffer length should match size")

    def zeroOneToByte(v: Float): Int = {
      if (v >= 1)
      {
        255
      }
      else if (v <= 0)
      {
        0
      }
      else
      {
        (v * 255).toInt
      }
    }

    val xDelta = area.width / width
    val yDelta = area.height / height

    val pos = Vec2(area.minY, area.minX)
    var y = 0
    var i = 0
    while (y < height) {

      pos.x = area.minX
      var x = 0
      while (x < width) {

        val color = rgba(pos)

        // TODO: Checkerboard background for transparent areas
        buffer(i) =
                (0xff << 24) |
                (zeroOneToByte(color.r) << 16) |
                (zeroOneToByte(color.g) << 8) |
                (zeroOneToByte(color.b) << 0)

        x += 1
        i += 1
        pos.x += xDelta
      }

      y += 1
      pos.y += yDelta
    }


  }

  /**
   * Replaces this component with the specified component
   */
  def replaceWith(newComponent: Comp) {
    val oldRoot = root

    if (parent != null) {
      parent.set(outputName, newComponent)
    }

    // TODO: If this is referenced somewhere, replace one of the references with this component and update the other

    // TODO: Free any resources or such, call any listeners

    val newRoot = newComponent.root
    oldRoot._structureListeners foreach {_(newRoot)}

    //deepClear()
  }

  // TODO: addAfter, and replaceAndKeepInputs

  private def deepClear() {

    inputComponents.foreach(_.deepClear())

    _structureListeners = Set()
    _parent = null
  }

  def toXml: String = ""

}


