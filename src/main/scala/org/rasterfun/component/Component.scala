package org.rasterfun.component

/**
 * 
 */
trait Component {

  def parameters: List[Symbol]
  def constants: List[Symbol]
  def outputs: List[Symbol]

}