package app.impl.algorithms

import java.util

import org.junit.Test

class TreeDS {

  class Node(var data: Int, var left: Node, var right: Node) {

    override def toString: String = "Node{" + "data=" + data + ", left=" + left + ", right=" + right + '}'

  }

  //  Print a tree in level order.
  @Test
  def levelOrder() {
    val node4 = new Node(4, null, null)
    val node6 = new Node(6, null, null)
    val node3 = new Node(3, null, node4)
    val node5 = new Node(5, node3, node6)
    val node2 = new Node(2, null, node5)
    val node1 = new Node(1, null, node2)
    levelOrderTree(node1)
  }

  /**
    * This algorithm print from top to bottom level by level left to right.
    * In order to can run through all levels without use reflection we can just add
    * in a Queue our nodes and iterate over them from left to right every level.
    * This algorithm use BFS we visit all nodes from our level before go any deeper.
    */
  def levelOrderTree(node: Node): Unit = {
    val queue: util.LinkedList[Node] = new util.LinkedList()
    queue.add(node)
    while (!queue.isEmpty) {
      val levelNode: Node = queue.poll()
      print(s"${levelNode.data} ")
      if (levelNode.left != null) {
        queue.add(levelNode.left)
      }
      if (levelNode.right != null) {
        queue.add(levelNode.right)
      }
    }
  }

  //      1
  //   2      3
  // 4   7  5    6
  //                8
  /**
    * Is binary tree super balance
    * A Tree is balance always than the branch level never exceed more than one between nodes.
    * If node left go two level deeper, and the right node none means the tree is unbalance.
    * Otherwise can be consider balance.
    */
  @Test
  def binaryTreeBalance(): Unit = {
    val node8 = new Node(8, null, null)
    val node7 = new Node(7, null, null)
    val node6 = new Node(6, null, node8)
    val node5 = new Node(5, null, null)
    val node4 = new Node(4, null, null)
    val node3 = new Node(3, node5, node6)
    val node2 = new Node(2, node4, node7)
    val node1 = new Node(1, node2, node3)
    print(binaryTreeBalance(node1))
  }

  //      1
  //   2      3
  // 4      5    6
  //                7
  //                  8
  /**
    * Is binary tree Unbalance
    */
  @Test
  def binaryTreeUBalance(): Unit = {
    val node8 = new Node(8, null, null)
    val node7 = new Node(7, null, node8)
    val node6 = new Node(6, null, node7)
    val node5 = new Node(5, null, null)
    val node4 = new Node(4, null, null)
    val node3 = new Node(3, node5, node6)
    val node2 = new Node(2, node4, null)
    val node1 = new Node(1, node2, node3)
    print(binaryTreeBalance(node1))
  }

  /**
    * To be efficient we use recursion to check every level of the tree, here we receive the final value and
    * we return true/false depending if is balance >=0 or unbalance -1
    */
  def binaryTreeBalance(node: Node): Boolean = {
    if (nodeHeight(node) > -1) true else false
  }

  /**
    * Here we calc the height of every level, so every time we use recursion we go into a deeper level of the tree.
    * Per level we compare the next things.
    *  - If the node is null we return 0, which means we reach the end of the tree.
    *  - We call by recursion into left and right tree level increasing the height by 1 per level we go depper.
    *  - We check per level if left or right are  -1 telling us that this node were we went was unbalance so we return -1
    *  - We calc the distance branch level between left and right branch, and if the difference is bigger than 1 it means
    * is unbalanace and we return -1
    *  - We compare which branch is longer and we return left or right
    */
  def nodeHeight(node: Node): Int = {
    if (node == null) return 0
    val leftHeight = 1 + nodeHeight(node.left)
    val rightHeight = 1 + nodeHeight(node.right)
    if (leftHeight == -1 || rightHeight == -1) return -1
    //Compare if the difference between left and right is bigger than 1, then we return -1
    if (Math.abs(leftHeight - rightHeight) > 1) return -1
    if (leftHeight > rightHeight) leftHeight else rightHeight
  }

  //Given a binary tree and a number, return true if the tree has a root-to-leaf path.
  //      1
  //   2      3
  // 4   7  5    6
  //                8
  @Test
  def binaryTreeFindSumOfTargetInPath(): Unit = {
    val node8 = new Node(8, null, null)
    val node7 = new Node(7, null, null)
    val node6 = new Node(6, null, node8)
    val node5 = new Node(5, null, null)
    val node4 = new Node(4, null, null)
    val node3 = new Node(3, node5, node6)
    val node2 = new Node(2, node4, node7)
    val node1 = new Node(1, node2, node3)
    print(binaryTreeFindSumOfTargetInPath(node1, 9))
  }

  var foundBranch: Boolean = false

  /**
    * The easiest idea is subtract the taret by the node.data in every level.
    * Then once we reach the last leaf of node, we compare if the target value is 0,
    * that would means the path has the same target number.
    */
  def binaryTreeFindSumOfTargetInPath(node: Node, target: Int): Boolean = {
    if (node == null) {
      target == 0
    } else {
      val subSum = target - node.data
      if (subSum == 0 && node.left == null && node.right == null) { //If we are at the end of a leaf and the target is 0 means is the path == target
        foundBranch = true
      }
      if (node.left != null) {
        binaryTreeFindSumOfTargetInPath(node.left, subSum) //In every new deep level we send the target number reduce it
      }
      if (node.right != null) {
        binaryTreeFindSumOfTargetInPath(node.right, subSum) //In every new deep level we send the target number reduce it
      }
      foundBranch
    }
  }

  //Given a binary tree and an integer S, print all distinct paths from root to leaves which sum to S.
  //      1
  //   2      3
  // 4   7  5    6
  //                8
  @Test
  def binaryTreePrintIfTargetIsInPath(): Unit = {
    val node8 = new Node(8, null, null)
    val node7 = new Node(7, null, null)
    val node6 = new Node(6, null, node8)
    val node5 = new Node(5, null, null)
    val node4 = new Node(4, null, null)
    val node3 = new Node(3, node5, node6)
    val node2 = new Node(2, node4, node7)
    val node1 = new Node(1, node2, node3)
    binaryTreePrintIfTargetIsInPath(node1, 9)
  }

  def binaryTreePrintIfTargetIsInPath(node: Node, target: Int): Boolean = {
    if (node == null) {
      target == 0
    } else {
      val subSum = target - node.data
      if (subSum == 0 && node.left == null && node.right == null) {
        foundBranch = true
      }
      if (node.left != null) {
        val foundBranch = binaryTreePrintIfTargetIsInPath(node.left, subSum)
        if (foundBranch) print(s"${node.data} ")
      }
      if (node.right != null) {
        val foundBranch = binaryTreePrintIfTargetIsInPath(node.right, subSum)
        if (foundBranch) print(s"${node.data} ")

      }
      foundBranch
    }
  }

}
