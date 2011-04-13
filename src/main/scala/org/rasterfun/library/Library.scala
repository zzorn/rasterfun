package org.rasterfun.library

/**
 * 
 */
class Library {
  val root: Category = new Category('root)
  root.addCategory(BuiltinCategoryBuilder.createBuiltins)

}