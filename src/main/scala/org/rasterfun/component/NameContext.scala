package org.rasterfun.component

/**
 * Keeps track of names, to ensure uniqueness.
 */
class NameContext {

  def getUniqueId(id: Symbol): Symbol = id
}