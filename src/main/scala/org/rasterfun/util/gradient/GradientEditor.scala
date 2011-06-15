package org.rasterfun.util.gradient

import simplex3d.math.float._
import simplex3d.math.float.functions._

import org.scalaprops.ui.Editor
import org.scalaprops.Property

/**
 * Interactive editor for gradients.
 */
class GradientEditor(property: Property[Gradient]) extends Editor[Gradient] {

  private def buildUi() {

  }

  protected def onInit(initialValue: Gradient, name: String) {

  }

  protected def onExternalValueChange(oldValue: Gradient, newValue: Gradient) {

  }


}


object GradientEditorFactory {

  def apply(property: Property[Gradient]): Editor[Gradient] = new GradientEditor(property)

}