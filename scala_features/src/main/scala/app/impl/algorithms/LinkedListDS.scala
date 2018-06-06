package app.impl.algorithms

import org.junit.Test

/**
  * LinkedList data structure use a syntax of data and nextNode which is another Node, quite similar to tree Data structure.
  * LinkedList as Tree use quite a lot of Recursion since is the most efficient way to iterate over the next Node of the link
  * Without have to duplicate code or do nightmare algorithms.
  * Recursion is a technique where we have a current method state with variables with some values and state. When we do a recursion
  * in that method, the whole state of the method is safe in the stack(about 4 kbytes) and when we decide to stop the recursion.
  * (At some point an [if] is mandatory to avoid stackOverflow) we go backwards recovering the previous state that was safe in the stack.
  * It's important to understand that whatever happen in the next recursions call cannot affect previous one. No state is saved.
  * And only if we change something out of the method it will be state of our actions.
  */
class LinkedListDS {

  class SinglyLinkedListNode(var data: Int, var next: SinglyLinkedListNode = null) {
    override def toString = s"SinglyLinkedListNode($data, $next)"
  }

  @Test
  def insertNodeAtPosition: Unit = {

    val node = new SinglyLinkedListNode(7, null)
    val node1 = new SinglyLinkedListNode(13, node)
    val node2 = new SinglyLinkedListNode(16, node1)
    val node3 = new SinglyLinkedListNode(22, node2)
    print(insertNodeAtPosition(node3, 1, 3))

  }

  /**
    * It's important to remember that one node contains another node and so on, so there's no chain or link
    * is more about Russian doll. If you want to put nodes in the middle just link the old chain into the new one,
    * and this new one with the other right link of the chain.
    */
  def insertNodeAtPosition(node: SinglyLinkedListNode, data: Int, position: Int): SinglyLinkedListNode = {
    if (position > 0) {
      insertNodeAtPosition(node.next, data, position - 1)
    } else {
      val nextNode = new SinglyLinkedListNode(node.data, node.next) //We create a new node which we set the current value of the node that we will override.
      node.data = data
      node.next = nextNode //The previous node it will continue having this node pointer, now with a new value.
    }
    node
  }

  @Test
  def deleteNode: Unit = {
    val node = new SinglyLinkedListNode(9, null)
    val node1 = new SinglyLinkedListNode(15, node)
    val node2 = new SinglyLinkedListNode(4, node1)
    val node3 = new SinglyLinkedListNode(7, node2)
    val node4 = new SinglyLinkedListNode(19, node3)
    val node5 = new SinglyLinkedListNode(2, node4)
    val node6 = new SinglyLinkedListNode(6, node5)
    val node7 = new SinglyLinkedListNode(20, node6)
    print(deleteNode(node7, 3))
  }

  /**
    * To delete node is just easy the tricky part is that since you need to link the A with C since you
    * are deleting B you cannot stop in B index but the previous one A which know B and C, so then we can
    * link A to C
    */
  def deleteNode(llist: SinglyLinkedListNode, position: Int): SinglyLinkedListNode = {
    var node = llist
    if (position == 0) {
      node = node.next
    } else if (position > 1) {
      val pos = position - 1
      deleteNode(node.next, pos)
    } else {
      if (node.next != null) {
        if (node.next.next != null) {
          node.next = node.next.next
        } else {
          node.next = null
        }
      }
    }
    node
  }

  @Test
  def reversePrint: Unit = {
    val node = new SinglyLinkedListNode(6, null)
    val node1 = new SinglyLinkedListNode(5, node)
    val node2 = new SinglyLinkedListNode(4, node1)
    val node3 = new SinglyLinkedListNode(3, node2)
    val node4 = new SinglyLinkedListNode(2, node3)
    val node5 = new SinglyLinkedListNode(1, node4)
    reversePrint(node5)
  }

  /**
    * Reverse print is just so simple like use recursion to do inside out.
    * We go until the last node and then we start the reverse iteration.
    * In order to do not miss the last node we have to add the condition
    * that if next node is null print the last data.
    */
  def reversePrint(llist: SinglyLinkedListNode): Unit = {
    if (llist.next != null) {
      reversePrint(llist.next)
      println(llist.data) // this action it will executed from the last element of the Recursion to the first one.
    } else {
      println(llist.data)
    }
  }

  @Test
  def noReversePrint: Unit = {
    val node = new SinglyLinkedListNode(6, null)
    val node1 = new SinglyLinkedListNode(5, node)
    val node2 = new SinglyLinkedListNode(4, node1)
    val node3 = new SinglyLinkedListNode(3, node2)
    val node4 = new SinglyLinkedListNode(2, node3)
    val node5 = new SinglyLinkedListNode(1, node4)
    noReversePrint(node5)
  }

  /**
    * No reverse print is even easier. We just need to print the data before the recursion.
    */
  def noReversePrint(llist: SinglyLinkedListNode): Unit = {
    if (llist.next != null) {
      println(llist.data)
      noReversePrint(llist.next)
    }
  }


  /**
    * Find the node value form the tail having a position from the tail.
    *
    * @return
    */
  @Test
  def getNode: Unit = {
    val nodeIndex1 = new SinglyLinkedListNode(1, null)
    val node = new SinglyLinkedListNode(2, nodeIndex1)
    val node1 = new SinglyLinkedListNode(3, node)
    val node2 = new SinglyLinkedListNode(4, node1)
    val node3 = new SinglyLinkedListNode(5, node2)
    println(getNode(node3, 1))
  }

  var count = 0
  var output = 0

  /**
    * Like any other recursive reverse algorithm. Inside out technique. We do recursion until the end, and when we go backwards
    * to the previous steps we start incrementing a counter until is equals to positionFromTail.
    * Once we found the index we set the data into output.
    */
  def getNode(llist: SinglyLinkedListNode, positionFromTail: Int): Int = {
    if (llist.next != null) {
      getNode(llist.next, positionFromTail)
      if (count == positionFromTail) {
        output = llist.data
      }
    } else {
      if (positionFromTail == 0) {
        output = llist.data
      }
    }
    count += 1
    output
  }

  @Test
  def removeDuplicates: Unit = {
    val node1 = new SinglyLinkedListNode(1, null)
    val node2 = new SinglyLinkedListNode(1, node1)
    val node3 = new SinglyLinkedListNode(1, node2)
    val node4 = new SinglyLinkedListNode(2, node3)
    val node5 = new SinglyLinkedListNode(3, node4)
    val node6 = new SinglyLinkedListNode(3, node5)
    val node7 = new SinglyLinkedListNode(3, node6)
    println(removeDuplicates(node7))
  }

  import collection.immutable.List

  var oldNode: List[Int] = List()
  var previous: SinglyLinkedListNode = _

  def removeDuplicates(head: SinglyLinkedListNode): SinglyLinkedListNode = {
    if (head.next != null) {
      if (oldNode.contains(head.data)) {
        previous.next = head.next
      } else {
        oldNode = oldNode ++ List(head.data)
        previous = head
      }
      removeDuplicates(head.next)
    } else if (oldNode.contains(head.data)) {
      previous.next = null
    }
    head
  }


  //  5 13 15 18 20 11 6 7
  //  13 5 18 15 11 20 7 6

  @Test
  def swapNodes: Unit = {
    val node0 = new SinglyLinkedListNode(7, null)
    val node1 = new SinglyLinkedListNode(6
      , node0)
    val node2 = new SinglyLinkedListNode(11, node1)
    val node3 = new SinglyLinkedListNode(20, node2)
    val node4 = new SinglyLinkedListNode(18, node3)
    val node5 = new SinglyLinkedListNode(15, node4)
    val node6 = new SinglyLinkedListNode(13, node5)
    val node7 = new SinglyLinkedListNode(5, node6)
    println(swapNodes(node7).toString)
  }

  def swapNodes(head: SinglyLinkedListNode): SinglyLinkedListNode = {
    if (head != null) {
      val tmp = head
      val node = head.next
      node.next = tmp
      node.next  = swapNodes(tmp.next.next)
    }
    head
  }


  //  @Test
  //  def xxxx: Unit = {
  //    val node = new SinglyLinkedListNode(111, null)
  //    val node1 = new SinglyLinkedListNode(222, node)
  //    val node2 = new SinglyLinkedListNode(2, node1)
  //    val node3 = new SinglyLinkedListNode(666, node2)
  //    val node4 = new SinglyLinkedListNode(1, node3)
  //    val node5 = new SinglyLinkedListNode(2, node4)
  //
  //    aaaa(node5)
  //  }
  //
  //  def aaaa(llist: SinglyLinkedListNode): Unit = {
  //    ReversePrint(llist.next, llist.next.data)
  //  }
  //
  //  def ReversePrint(llist: SinglyLinkedListNode, numbers: Int): Unit = {
  //    if(llist.next == null){
  //      return
  //    }
  //    if (numbers > 0) {
  //      println(llist.next.data)
  //      ReversePrint(llist.next, numbers - 1)
  //    }
  //    if (llist.next != null) {
  //      ReversePrint(llist.next, llist.next.data)
  //    }
  //  }


}