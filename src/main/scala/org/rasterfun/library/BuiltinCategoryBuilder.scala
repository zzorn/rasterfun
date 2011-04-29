package org.rasterfun.library

import org.rasterfun.components.{Empty, Noise, Solid, Blend}

/**
 * 
 */

object BuiltinCategoryBuilder {

  def createBuiltins(root: Category): Category = {
    val basic = new Category('Basic)
    root.addCategory(basic)

    basic.addComponent(new Empty())
    basic.addComponent(new Blend())
    basic.addComponent(new Solid())
    basic.addComponent(new Noise())

    basic
  }

}