package org.rasterfun.component

/**
 * 
 */
class CodeContext {

  private var valueVariableNames: Map[String, String] = Map()

  private def makeKey(componentId: Symbol, outputId: Symbol): String =
    componentId.name + "." + outputId.name

  def getVariableName(componentId: Symbol, outputId: Symbol): String =
    valueVariableNames.get(makeKey(componentId, outputId)).get

  def setVariableName(componentId: Symbol, outputId: Symbol, name: String) =
    valueVariableNames += (makeKey(componentId, outputId) -> name)

}

