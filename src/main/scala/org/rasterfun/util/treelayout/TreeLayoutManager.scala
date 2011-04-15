package org.rasterfun.util.treelayout

import java.lang.String
import java.awt.Component
import java.awt.LayoutManager2
import java.awt.Container
import java.awt.Dimension
import java.util.ArrayList
import collection.JavaConversions._

/**
 * One way for added component to communicate their tree structure is to implement this trait.
 */
trait TreeNodeComponent {
  def parentTreeNode: Component
}

/**
 * Requires added components to be TreeNodeComponent,
 * or add method to provide child components for each component.
 *
 * Uses improved Walkers tree layout algorithm (for linear time performance)
 * Based on http://citeseer.ist.psu.edu/buchheim02improving.html
 */
class TreeLayoutManager(siblingGap: Int = 10, branchGap: Int = 20, layerGap: Int = 30) extends LayoutManager2 {

  private var components: List[Component] = Nil
  private var parents: Map[Component, Component] = Map()
  private var layoutSize: Dimension = null

  override def addLayoutComponent(name: String, comp: Component) {
    addLayoutComponent(comp, name)
  }

  override def addLayoutComponent(comp: Component, parent: Object) {
    components = components ::: List(comp)

    if (parent.isInstanceOf[Component]) {
      parents += comp -> parent.asInstanceOf[Component]
    }
    else if (comp.isInstanceOf[TreeNodeComponent]) {
      parents += comp -> comp.asInstanceOf[TreeNodeComponent].parentTreeNode
    }
  }

  override def removeLayoutComponent(comp: Component) {
    components = components.filterNot(_ == comp)
    parents -= comp
  }

  override def preferredLayoutSize(parent: Container) = calculateLayout
  override def minimumLayoutSize(parent: Container) = calculateLayout
  override def maximumLayoutSize(target: Container) = calculateLayout


  override def layoutContainer(parent: Container) {
    layoutSize = null
    calculateLayout
  }

  override def invalidateLayout(target: Container) {
    layoutSize = null
  }

  override def getLayoutAlignmentY(target: Container) = 0.5f
  override def getLayoutAlignmentX(target: Container) = 0.5f


  private def calculateLayout: Dimension = {
    if (layoutSize == null) {
      // Calculate tree(s)
      val rootNodes = buildTreeRoots(components, parents)

      // Create invisible root for all
      val root = new TreeNode(null)
      rootNodes.foreach {r =>
        root.children.add(r)
        r.parentNode = Some(root)
      }

      // Calculate and set positions
      layoutSize = root.layout()
    }

    layoutSize
  }


  private def uSpacing(a: TreeNode, b: TreeNode, siblings: Boolean): Float = {
    (if (siblings) siblingGap else branchGap) +
    (a.uSize + b.uSize) / 2f
  }



  /**
   * Build trees from component and parent information.
   * The operation is O(n) time complexity.
   * Return all root nodes of the trees.
   */
  private def buildTreeRoots(components: List[Component], parents: Map[Component, Component]): List[TreeNode] = {
    // Create treenodes
    val nodes: Map[Component, TreeNode] = components.map( c => c -> new TreeNode(c)).toMap

    // For each tree node, lookup its parent, and update parent / child relations
    for {
      c <- components
      val node: TreeNode <- nodes.get(c)
      val parentComponent <- parents.get(c)
      val parentNode: TreeNode <- nodes.get(parentComponent)
    } {
      if (node != null && !parentNode.hasParent(node) && node.parentNode == None) {
        node.parentNode = Some(parentNode)
        parentNode.children.add(node)
      }
    }

    // Get tree nodes with no parent, in same order as they were added to the layout
    val orderedTreeNodes: List[TreeNode] = (components flatMap (c => nodes.get(c)))
    orderedTreeNodes filter (_.parentNode == None)
  }


  private class TreeNode(component: Component) {
    var parentNode: Option[TreeNode] = None
    var children: ArrayList[TreeNode] = new ArrayList()

    var leftSibling: TreeNode = null
    var ancestor: TreeNode = this
    var thread: TreeNode = null

    var x: Float = 0
    var y: Float = 0

    var preliminary: Float = 0
    var modifier: Float = 0
    var change: Float = 0
    var shift: Float = 0
    var childNumber: Int = -2
    var depth: Int = 0

    def isLeaf = children.isEmpty

    def uSize: Float = if (component == null) 0 else component.getPreferredSize.width
    def vSize: Float = if (component == null) 0 else component.getPreferredSize.height

    def firstChild = if (children.isEmpty) null else children.get(0)
    def lastChild = if (children.isEmpty) null else children.get(children.size - 1)

    def hasParent(node: TreeNode): Boolean = {
      if (this == node ) true
      else if (parentNode.isEmpty) false
      else parentNode.get.hasParent(node)
    }

    def layout(): Dimension = {
      val maxDepth = initLayout(0)
      val vSizes = new Array[Float](maxDepth + 1)
      firstWalk(0, vSizes)

      val treeArea = new TreeArea()
      secondWalk(-preliminary, 0f, vSizes, treeArea)

      updateComponentPositions(treeArea.xOffset, treeArea.yOffset)
      treeArea.dimension
    }

    private def initLayout(d: Int): Int = {
      leftSibling = null
      ancestor= this
      thread = null

      preliminary = 0
      modifier = 0
      change = 0
      shift = 0
      childNumber = 0
      depth = d

      var maxDepth = depth
      var sibling: TreeNode = null
      children foreach { c =>
        val childDepth = c.initLayout(depth + 1)
        maxDepth = maxDepth max childDepth

        c.leftSibling = sibling
        sibling = c
      }

      maxDepth
    }

    private def firstWalk(num: Int, vSizes: Array[Float]) {
      childNumber = num

      // Update layer depth
      vSizes(depth) = vSizes(depth) max vSize

      if (isLeaf) {
        if (leftSibling != null) {
          preliminary = leftSibling.preliminary + uSpacing(this, leftSibling, true)
        }
        else {
          preliminary = 0
        }
      } else {
        var defaultAncestor = firstChild

        var i = 0
        children foreach {c =>
          c.firstWalk(i, vSizes)
          defaultAncestor = c.apportion(defaultAncestor)
          i += 1
        }

        executeShifts()

        val midPoint = (firstChild.preliminary + lastChild.preliminary) / 2

        if (leftSibling != null) {
          preliminary = leftSibling.preliminary + uSpacing(leftSibling, this, true)
          modifier = preliminary - midPoint
        }
        else {
          preliminary = midPoint
        }
      }
    }

    private def apportion(a: TreeNode): TreeNode = {
      var defaultAncestor = a
      if (leftSibling != null) {
        var vip = this
        var vop = this
        var vim = leftSibling
        var vom = vip.parentNode.get.firstChild

        var sip = vip.modifier
        var sop = vop.modifier
        var sim = vim.modifier
        var som = vom.modifier

        var vimNextRight = vim.nextRight
        var vipNextLeft = vip.nextLeft
        while (vimNextRight != null &&
               vipNextLeft != null) {
          vim = vimNextRight
          vip = vipNextLeft
          vom = vom.nextLeft
          vop = vop.nextRight
          vop.ancestor = this

          val shift = (vim.preliminary + sim) - (vip.preliminary + sip) + uSpacing(vim, vip, false)
          if (shift > 0) {
            moveSubtree(ancestor(vim, defaultAncestor), this, shift)
            sip += shift
            sop += shift
          }

          sim += vim.modifier
          sip += vip.modifier
          som += vom.modifier
          sop += vop.modifier

          vimNextRight = vim.nextRight
          vipNextLeft = vip.nextLeft
        }

        if (vimNextRight != null && vop.nextRight == null) {
          vop.thread = vimNextRight
          vop.modifier += sim - sop
        }

        if (vipNextLeft != null && vom.nextLeft == null) {
          vom.thread = vipNextLeft
          vom.modifier += sip - som
          defaultAncestor = this
        }
      }
      
      defaultAncestor
    }

    private def nextLeft: TreeNode = {
      if (!isLeaf) firstChild
      else thread
    }

    private def nextRight: TreeNode = {
      if (!isLeaf) lastChild
      else thread
    }

    private def moveSubtree(wm: TreeNode, wp: TreeNode, shift: Float) {
      val subTrees = wp.childNumber - wm.childNumber
      wp.change -= shift / subTrees
      wp.shift += shift
      wm.change += shift / subTrees
      wp.preliminary += shift
      wp.modifier += shift
    }

    private def executeShifts() {
      var shift = 0f
      var change = 0f
      
      var i = children.size - 1
      while (i >= 0) {
        val child = children.get(i)

        child.preliminary += shift
        child.modifier += shift
        change += child.change
        shift += child.shift + change

        i -= 1
      }
    }

    private def ancestor(a: TreeNode, default: TreeNode): TreeNode = {
      if (a.ancestor.parentNode == parentNode) a.ancestor
      else default
    }

    private def secondWalk(uOffset: Float, vOffset: Float, vSizes: Array[Float], area: TreeArea) {

      val u = preliminary + uOffset
      val v = vOffset
      val xSize = uSize
      val ySize = vSize

      // TODO: We can support different orientations here if desired
      x = u
      y = v

      // We treat depth 0 specially, because it is always the artificially added root element that we do not want to show up.
      if (depth > 0) area.addArea(x, y, xSize, ySize)
      val childV = if (depth == 0) 0f else vOffset + vSizes(depth) + layerGap

      children foreach {c => c.secondWalk(uOffset + modifier, childV, vSizes, area)}
    }

    private def updateComponentPositions(xOffset: Float, yOffset: Float) {

      if (component != null) {
        val xPos = (x + xOffset).toInt
        val yPos = (y + yOffset).toInt
        component.setLocation(xPos, yPos)
        component.setSize(component.getPreferredSize)
      }

      children foreach {c => c.updateComponentPositions(xOffset, yOffset)}
    }

  }


  private class TreeArea() {
    private var empty = true
    private var x1: Float = 0
    private var x2: Float = 0
    private var y1: Float = 0
    private var y2: Float = 0

    def addArea(x: Float, y: Float, w: Float, h: Float) {
      if (empty) {
        x1 = x
        y1 = y
        x2 = x + w
        y2 = y + h
        empty = false
      }
      else {
        x1 = x1 min x
        y1 = y1 min y
        x2 = x2 max (x + w)
        y2 = y2 max (y + h)
      }
    }

    def clear() {
      empty = true
      x1 = 0
      y1 = 0
      x2 = 0
      y2 = 0
    }

    def xOffset = -x1
    def yOffset = -y1
    def width = x2 - x1
    def height = y2 - y1

    def dimension: Dimension = new Dimension(width.toInt, height.toInt)
  }

}