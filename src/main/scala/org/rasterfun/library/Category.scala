package org.rasterfun.library

import org.rasterfun.component.Comp

/**
 * A category of components.
 */
class Category(id: Symbol) {

  private var _subCategories: List[Category] = Nil
  private var _components: List[Comp] = Nil

  def components: List[Comp] = _components
  def subCategories: List[Category] = _subCategories

  def addComponent(component: Comp) {
    if (component != null && !_components.contains(component)) {
      _components ::= component
    }
  }

  def addCategory(category: Category) {
    if (category != null && !_components.contains(category)) {
      _subCategories ::= category
    }
  }

}