package org.rasterfun.util.gradient

import simplex3d.math.float._
import simplex3d.math.float.functions._

import org.scalaprops.Property
import java.awt.{Dimension, BorderLayout}
import org.scalaprops.ui.{EditorFactory, Editor}

/**
 * Interactive editor for gradients.
 */
class GradientEditor() extends Editor[Gradient] {

  private val view = new GradientView()

  buildUi()

  private def buildUi() {
    setPreferredSize(new Dimension(128, 32))
    setLayout(new BorderLayout())
    add(view, BorderLayout.NORTH)

  }

  protected def onInit(initialValue: Gradient, name: String) {
    view.gradient = initialValue
  }

  protected def onExternalValueChange(oldValue: Gradient, newValue: Gradient) {
    view.gradient = newValue
  }

}


object GradientEditorFactory extends EditorFactory[Gradient] {

  protected def createEditorInstance = new GradientEditor()

}