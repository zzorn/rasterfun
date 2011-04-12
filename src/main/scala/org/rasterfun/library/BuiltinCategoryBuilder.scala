package org.rasterfun.library

import org.rasterfun.component.BuiltinComponent

/**
 * 
 */

object BuiltinCategoryBuilder {

  def createBuiltins: Category = {
    val cat = new Category()

    def add(id: Symbol, source: String) {
      cat.addComponent(new BuiltinComponent(id, source))
    }

    add('add, "$a ")






    cat
  }

}