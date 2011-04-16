package org.rasterfun.library

import org.rasterfun.component.Comp
import java.util.ArrayList
import collection.JavaConversions._

/**
 * A category of components.
 */
class Category(val id: Symbol, _library: Library = null) {

  private val _subCategories: ArrayList[Category] = new ArrayList()
  private var _components: ArrayList[Comp] = new ArrayList()
  var parent: Category = null

  def name: String = id.name

  def components: List[Comp] = _components.toList
  def subCategories: List[Category] = _subCategories.toList

  def library: Library = if (_library != null) _library else if (parent != null) parent.library else throw new IllegalStateException("No library defined for category")

  def addComponent(component: Comp) {
    require(component != null)
    require(!_components.contains(component))

    _components.add(component)

    library.notifyListeners(l => l.onComponentAdded(this, component))
  }

  def removeComponent(component: Comp) {
    if (component != null && _components.contains(component)) {
      _components.remove(component)
    }

    library.notifyListeners(l => l.onComponentRemoved(this, component))
  }

  def addCategory(category: Category) {
    require(category != null)
    require(!_components.contains(category))

    _subCategories.add(category)
    category.parent = this

    library.notifyListeners(l => l.onCategoryAdded(category))
  }

  def removeCategory(category: Category) {
    if (category != null && _components.contains(category)) {
      _subCategories.remove(category)
      category.parent = null
    }

    library.notifyListeners(l => l.onCategoryRemoved(category))
  }

  def isLeafCategory = _subCategories.isEmpty
  def subcategoriesCount: Int = _subCategories.size
  def indexOf(subCategory: Category): Int = _subCategories.indexOf(subCategory)
  def subCategoryAt(index: Int): Category = if (index < 0 || index >= subcategoriesCount) null else _subCategories(index)

  override def toString = name
}