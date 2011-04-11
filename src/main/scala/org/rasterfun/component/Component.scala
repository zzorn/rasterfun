package org.rasterfun.component

/**
 * 
 */
trait Component {

  def id: Symbol

  def inputs: List[ComponentInput]
  def outputs: List[ComponentOutput]
  def config: List[ComponentConfig]

  def toXml: String

  def generateCode: String

  /** Creates a copy of this component without any inputs or outputs assigned. */
  def copy(nameContext: NameContext): Component
}

trait ComponentPart {
  def parent: Component
  def id: Symbol
  def copy(parent: Component): ComponentPart
}

class ComponentConfig(val parent: Component, val id: Symbol) extends ComponentPart {
  var value: AnyRef = null

  def copy(parent: Component): ComponentConfig = {
    val c = new ComponentConfig(parent, id)
    c.value = value
    c
  }
}

class ComponentOutput(val parent: Component, val id: Symbol) extends ComponentPart {
  def copy(parent: Component): ComponentOutput = new ComponentOutput(parent, id)
}

class ComponentInput(val parent: Component, val id: Symbol, val valueRange: ValueRange) extends ComponentPart {
  private var _source: ComponentOutput = null
  private var _constant: Float = 0

  def source = _source
  def source_=(source: ComponentOutput) {
    _source = source
  }

  def constant = _constant
  def constant_=(value: Float) {
    _source = null
    _constant = value
  }

  def copy(parent: Component): ComponentInput = {
    val ci = new ComponentInput(parent, id)
    ci._source = _source
    ci._constant = _constant
    ci
  }
}