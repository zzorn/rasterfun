package org.rasterfun.library

import org.rasterfun.components.{Noise, Solid, Blend}

/**
 * 
 */

object BuiltinCategoryBuilder {

  def createBuiltins: Category = {
    val cat = new Category('Simple)

    cat.addComponent(new Blend())
    cat.addComponent(new Solid())
    cat.addComponent(new Noise())

    cat
  }

}