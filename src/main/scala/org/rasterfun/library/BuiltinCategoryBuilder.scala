package org.rasterfun.library

import org.rasterfun.components.{Empty, Noise, Solid, Blend}

/**
 * 
 */

object BuiltinCategoryBuilder {

  def createBuiltins(root: Category) {
    var cat = new Category('Simple)
    root.addCategory(cat)

    cat.addComponent(new Blend())
    cat.addComponent(new Solid())
    cat.addComponent(new Noise())

    cat = new Category('Foo)
    root.addCategory(cat)

    cat.addComponent(new Empty())

  }

}