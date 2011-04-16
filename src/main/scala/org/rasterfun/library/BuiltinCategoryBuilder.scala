package org.rasterfun.library

import org.rasterfun.components.{Noise, Solid, Blend}

/**
 * 
 */

object BuiltinCategoryBuilder {

  def createBuiltins(root: Category) {
    val cat = new Category('Simple)
    root.addCategory(cat)

    cat.addComponent(new Blend())
    cat.addComponent(new Solid())
    cat.addComponent(new Noise())

    cat
  }

}