package org.rasterfun.component

import org.rasterfun.functions.Fun

/**
 * 
 */
class BuiltinComponent(val id: Symbol, source: String) extends Component {

  var config = Nil
  var outputs = Nil
  var inputs = Nil

  def toXml: String = {
    "<component id=\""+id.name+"\">" +
    // TODO: Add configs, outputs, and inputs
    "</component>"
  }

  def generateCode: String = {
    // TODO: Generate function body
    // TODO: Replace any input parameters present in code with input values
    source
  }

  def copy(nameContext: NameContext): Component = {
    val bc = new BuiltinComponent(nameContext.getUniqueId(id), source)
    bc.config = config map {_.copy(bc)}
    bc.outputs = outputs map {_.copy(bc)}
    bc.inputs = inputs map {_.copy(bc)}
    bc
  }
}