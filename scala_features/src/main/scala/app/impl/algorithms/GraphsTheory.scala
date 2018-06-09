package app.impl.algorithms

import java.util

import org.junit.Test

class GraphsTheory {

  @Test
  def breadthFirstSearch(): Unit = {
    val graph: Array[Array[Int]] = new Array(4)
    graph.update(0, Array(1, 2))
    graph.update(1, Array(2))
    graph.update(2, Array(0, 3))
    graph.update(3, Array(3))
    breadthFirstSearch(graph, 2, 4)
  }

  /**
    * Breadth first search is based in a matrix where every first array index means the node Id of one node
    * and the Array that contains that index is the nodes where that node has direct access(edges to nodes)
    *
    * The algorithm works using a Queue where we set the node where we are visiting all the time.
    * We have another collection where we set the nodes already visited, to avoid go again.
    *
    * The we use the current node to use as the index of the 2D array to get all nodes where I have direct access,
    * and then we iterate over them and we visit and set as visited. And then we add the nodes visited into the queue
    * to be go to the next nodes, once I visit all them.
    */
  def breadthFirstSearch(nodes: Array[Array[Int]], initVertex: Int, numberOfNodes: Int): Unit = {
    // Mark all the vertices as not visited(By default
    val visited: Array[Boolean] = new Array(numberOfNodes)
    // Create a queue for BFS
    val queue: util.LinkedList[Int] = new util.LinkedList()
    // Mark the current node as visited and enqueue it
    visited(initVertex) = true
    queue.add(initVertex)
    while (!queue.isEmpty) { // Dequeue a vertex from queue and print it
      val currentNode = queue.poll
      System.out.print(s"$currentNode ")
      nodes(currentNode) foreach (node => {
        if (!visited(node)) {
          visited(node) = true
          queue.add(node)
        }
      })
    }
  }

  @Test
  def breadthDepthSearch(): Unit = {
    val graph: Array[Array[Int]] = new Array(4)
    graph.update(0, Array(1, 2))
    graph.update(1, Array(2))
    graph.update(2, Array(0, 3))
    graph.update(3, Array(3))
    breadthDepthSearch(graph, 2, 4)
  }


  /**
    * Breadth Depth search is based in a matrix where every first array index means the node Id of one node
    * and the Array that contains that index is the nodes where that node has direct access(edges to nodes)
    *
    * Breadth Depth search is even easier than BFS since the only thing we have to do is just iterate over
    * the list of nodes from where I have access and for every single one of them it will try to do
    * the same.
    * This means that unlike BFS we dont visit all near nodes before we move to another node to their visiting neighbour's.
    */
  def breadthDepthSearch(nodes: Array[Array[Int]], initVertex: Int, numberOfNodes: Int): Unit = {
    val visited: Array[Boolean] = new Array(numberOfNodes)
    searchNewNode(initVertex, nodes, visited)
  }

  def searchNewNode(node: Int, nodes: Array[Array[Int]], visited: Array[Boolean]): Unit = {
    print(s"$node ")
    visited(node) = true
    nodes(node) foreach (vertx => {
      if (!visited(vertx)) {
        searchNewNode(vertx, nodes, visited)
      }
    })
  }

  val totalRow = 3
  val totalCol = 3

  /**
    * This rowN and colN combination (rowN-colN) are all cell index around a particular cell in the matrix
    * (-1,-1),(-1,0)(-1,1)(0,-1) and so on.
    */
  val rowN: Array[Int] = Array(-1, -1, -1, 0, 0, 1, 1, 1)
  val colN: Array[Int] = Array(-1, 0, 1, -1, 1, -1, 0, 1)

  @Test
  def findIsland(): Unit = {
    val graph: Array[Array[Int]] = new Array(3)
    graph.update(0, Array(1, 1, 0))
    graph.update(1, Array(0, 0, 1))
    graph.update(2, Array(1, 0, 1))
    print(s"Number of Islands:${findIsland(graph)}")
  }

  /**
    * Find island algorithm teach is how we can check around the position of the matrix all around that value,
    * and then recursively to find spot of the seam island.
    */
  def findIsland(graph: Array[Array[Int]]): Int = {
    val visited: Array[Array[Boolean]] = Array.ofDim(totalRow, totalCol)
    var count = 0
    0 until totalRow foreach (row => {
      0 until totalCol foreach (col => {
        if (isAnIslandSpotNotVisited(graph, visited, row, col)) {
          // if value 1 is not visited yet, then new island found, Visit all
          // cells in this island and then increment the island count
          // Then in next iterations only those not marked as visited are consider as new islands.
          DFS(graph, row, col, visited)
          count += 1
        }
      })
    })
    count
  }

  /**
    * A utility function to do Depth first search for a 2D boolean matrix.
    * It only considers the 8 neighbors as adjacent vertices
    */
  def DFS(graph: Array[Array[Int]], row: Int, col: Int, visited: Array[Array[Boolean]]): Unit = {
    visited(row)(col) = true
    0 until 8 foreach (i => {
      val neighbourRow = row + rowN(i)
      val neighbourCol = col + colN(i)
      if (isValidCell(graph, neighbourRow, neighbourCol, visited)) {
        DFS(graph, neighbourRow, neighbourCol, visited)
      }
    })
  }

  /**
    * A function to check if a given cell(row, col) can be used in the recursive call of DFS
    */
  def isValidCell(graph: Array[Array[Int]], row: Int, col: Int, visited: Array[Array[Boolean]]): Boolean = {
    (row >= 0) && //Means the new neighbour row cannot be out of the Array as negative
      (row < totalRow) && //Means I'm not out of the row Array
      (col >= 0) && //Means the new neighbour cell cannot be out of the Array as negative
      (col < totalCol) && //Means I'm not out of the cell Array
      isAnIslandSpotNotVisited(graph, visited, row, col) //Check if that spot is an island and has not visited yet.
  }

  /**
    * Check if the cell(row, col) is an island spot, and has not being visited yet.
    */
  private def isAnIslandSpotNotVisited(graph: Array[Array[Int]], visited: Array[Array[Boolean]], row: Int, col: Int) = {
    graph(row)(col) == 1 && !visited(row)(col)
  }

}
