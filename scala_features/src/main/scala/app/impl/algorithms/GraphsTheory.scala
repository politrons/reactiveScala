package app.impl.algorithms

import java.util

import org.junit.Test

class GraphsTheory {


  @Test
  def roadsAndLibraries(): Unit = {

    val graph: Array[Array[Int]] = new Array(2)
    graph.update(0, Array(1, 2))
    graph.update(1, Array(1, 3))
    print(roadsAndLibraries(3, 2, 1, graph))
  }

  def roadsAndLibraries(n: Int, c_lib: Int, c_road: Int, cities: Array[Array[Int]]): Int = {

    var total = 0
    if (c_lib < c_road) {
      total = c_lib * n
    } else {
      val visited: Array[Boolean] = new Array(n + 1)
      cities.foreach(nextCities => {
        nextCities.foreach(city => {
          if (!visited(city)) {
            val totalLibCost = nextCities.length * c_lib
            val totalRoadCost = nextCities.length * c_road
            if (totalLibCost < totalRoadCost) {
              total += totalLibCost
            } else {
              total += c_lib + totalRoadCost
            }
          }
        })
      })
    }
    total
  }


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
}
