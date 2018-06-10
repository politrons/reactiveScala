package app.impl.algorithms

import org.junit.Test

class ArrayDS {


  @Test
  def reverseArray: Unit = {
    print(reverseArray(Array(1, 4, 3, 2)).mkString(" "))
  }

  /**
    * We just create a new array where in each iteration we set the elements of original array
    * but in reverse mode.
    * We offer to flavour to this algorithm. One where we set the index as the last element of the original
    * array and we go backwards.
    * And another where iterate from max length to 0 and we use the index already calculated. But then we need to
    * Calculate the index of the original array values.
    *
    */
  def reverseArray(a: Array[Int]): Array[Int] = {
    val reverse = new Array[Int](a.length)
    a.indices foreach (i => {
      val index = (a.length - 1) - i
      reverse(index) = a(i)
    })
    //Another flavour
    //    a.length - 1 to 0 by -1 foreach (i => {
    //      reverse(i) = a((a.length - 1) - i)
    //    })
    reverse
  }

  @Test
  def hourglassSum: Unit = {
    val matrix: Array[Array[Int]] = new Array(6)
    matrix.update(0, Array(-1, -1, 0, -9, -2, -2))
    matrix.update(1, Array(-2, -1, -6, -8, -2, -5))
    matrix.update(2, Array(-1, -1, -1, -2, -3, -4))
    matrix.update(3, Array(-1, -9, -2, -4, -4, -5))
    matrix.update(4, Array(-7, -3, -3, -2, -9, -9))
    matrix.update(5, Array(-1, -3, -1, -2, -4, -5))
    print(hourglassSum(matrix))
  }

  /**
    * The key of this algorithm is to know that we cannot go through the whole array since we have + 3 elements
    * per iteration. So we always have to take into account the -1 of the lenght of the array + 2 of the next
    * two elements that I need to use from my right.
    */
  def hourglassSum(arr: Array[Array[Int]]): Int = {
    var output = Array[Int]()
    0 to arr.length - 3 foreach (i => {
      0 to arr.length - 3 foreach (j => {
        val first = arr(i)(j) + arr(i)(j + 1) + arr(i)(j + 2)
        val second = arr(i + 1)(j + 1)
        val third = arr(i + 2)(j) + arr(i + 2)(j + 1) + arr(i + 2)(j + 2)
        output = output ++ Array(first + second + third)
      })
    })
    output.max
  }

  /**
    * With the equation (index + number_elements - movements) % number_elements we can find the exactly
    * index where we want to move the element.
    */
  @Test
  def leftRotation: Unit = {
    val n = 5
    val d = 4
    val array: Array[Int] = Array(1, 2, 3, 4, 5)
    val output: Array[Int] = new Array(n)
    0 until n foreach (i => {
      output((i + n - d) % n) = array(i)
    })
    println(output.mkString(" "))
  }

  @Test
  def matchingStrings: Unit = {
    print(matchingStrings(Array("aba", "baba", "aba", "xzxb"), Array("aba", "xzxb", "ab")).mkString(" "))
  }

  /**
    * To get the elements duplicated in the string array we can use a counter using the index of the query
    * which means how many elements from that query index has been found.
    */
  def matchingStrings(strings: Array[String], queries: Array[String]): Array[Int] = {
    val stringsFound: Array[Int] = new Array(queries.length)
    queries.indices foreach (i => {
      val query = queries(i)
      strings.foreach(string => {
        if (query == string) {
          stringsFound(i) = stringsFound(i) + 1
        }
      })
    })
    stringsFound
  }


  @Test
  def rotate90: Unit = {
    val matrix: Array[Array[Int]] = new Array(3)
    matrix.update(0, Array(1, 2, 3))
    matrix.update(1, Array(4, 5, 6))
    matrix.update(2, Array(7, 8, 9))
    displayMatrix(rotate90(matrix))
  }

  def rotate90(matrix: Array[Array[Int]]): Array[Array[Int]] = {
    val mLength = matrix.length
    0 to mLength / 2 foreach (x => {
      x until mLength - x foreach (y => {
        //store current cell in temp variable
        val tmp = matrix(x)(y)
        // move values from right to top
        matrix(x)(y) = matrix(y)(mLength - 1 - x)
        // move values from bottom to right
        matrix(y)(mLength - 1 - x) = matrix(mLength - 1 - x)(mLength - 1 - y)
        //move values from left to bottom
        matrix(mLength - 1 - x)(mLength - 1 - y) = matrix(mLength - 1 - y)(x)
        // assign temp to left
        matrix(mLength - 1 - y)(x) = tmp
      })
    })
    matrix
  }

  def displayMatrix(matrix: Array[Array[Int]]): Unit = {
    matrix.indices foreach (i => {
      matrix.indices foreach (j => {
        print(s"${matrix(i)(j)} ")
      })
      println("")
    })
    println("")
  }

  var previous: Array[Char] = Array()
  var unique: Array[Char] = Array()

  /**
    * For this algorithm we just two arrays, one to keep track of all processed Char
    * and another where we set the unique chars of the String.
    * If we detect a char already processed we remove from unique in case was already there.
    * Otherwise we just add in unique array.
    */
  @Test
  def findFirstNonRepeated(): Unit = {
    val word = "BARBARIAN"
    word.toCharArray.foreach(c => {
      if (!previous.contains(c)) {
        unique = unique ++ Array[Char](c)
      } else {
        unique = unique.filter(uChar => uChar != c)
      }
      previous = previous ++ Array[Char](c)
    })
    print(unique.head)
  }

  @Test
  def dynamicArray: Unit = {
    val array: Array[Array[Int]] = new Array(5)
    array.update(0, Array(1, 0, 5))
    array.update(1, Array(1, 1, 7))
    array.update(2, Array(1, 0, 3))
    array.update(3, Array(2, 1, 0))
    array.update(4, Array(2, 1, 1))
    println(dynamicArray(2, array).mkString(" "))
  }

  def dynamicArray(n: Int, queries: Array[Array[Int]]): Array[Int] = {
    val seqList: Array[Array[Int]] = new Array(n)
    val N = n
    var lastAnswer = 0
    var output: Array[Int] = Array()
    queries.foreach(queryType => {
      val x: Int = queryType(1)
      val y: Int = queryType(2)
      val index = (x ^ lastAnswer) % N
      var seq = seqList(index)
      if (queryType.head == 1) {
        if (seq == null) {
          seqList(index) = Array(y)
        } else {
          seq = seq ++ Array(y)
          seqList(index) = seq
        }
      } else {
        val seqIndex = y % seq.length
        lastAnswer = seq(seqIndex)
        println(lastAnswer)
        output = output ++ Array(lastAnswer)
      }
    })
    output
  }

  @Test
  def binarySearchForRepeatedNumbers(): Unit = {
    println(binarySearchForRepeatedNumbers(Array(2, 3, 4, 5, 5, 5, 6, 7, 8, 8, 9, 10), 5))
  }

  var elements: Array[Int] = Array()
  var count = 0

  def binarySearchForRepeatedNumbers(array: Array[Int], target: Int): Int = {
    if (array.length > 1) {
      val middle = array.length / 2
      if (array(middle) == target) {
        count += 1
      } else {
        val (left, right) = array.splitAt(middle)
        if (target < array(middle)) {
          binarySearchForRepeatedNumbers(left, target)
          if (count > 0) {
            0 to middle foreach (i => {
              if (array(i) == target && !elements.contains(i)) {
                elements = elements ++ Array(i)
                count += 1
              }
            })
          }
        } else {
          binarySearchForRepeatedNumbers(right, target)
          if (count > 0) {
            middle until array.length foreach (i => {
              if (array(i) == target && !elements.contains(i)) {
                elements = elements ++ Array(i)
                count += 1
              }
            })
          }
        }
      }
    }
    count
  }


  @Test
  def binarySearchNoRecursion(): Unit = {
    println(binarySearchNoRecursion(Array(2, 3, 4, 5, 5, 5, 6, 7, 8, 8, 9, 10), 5))
  }

  /**
    * This implementation it will only work with a sorted array.
    * Using binary search without recursion is quite simple, we use a while loop until left is no longer lower than right
    * We calc per iteration loop the middle and we compare if the middle index in the array is equal to target,
    * otherwise we check if the element is lower than target, and if that the situation we need to move the index
    * from the left position to the middle +1 so we process the half right of the array, and if it's not we do
    * in the other way around and we move the right index to the middle to focus in the left side of the array.
    */
  def binarySearchNoRecursion(array: Array[Int], target: Int): Int = {
    var left = 0
    var right = array.length - 1
    var indexFound = -1
    while (left < right) {
      val middle = left + (right - left) / 2
      if (array(middle) == target) {
        indexFound = middle
        right = middle
      } else if (array(middle) > target) {
        right = middle
      } else {
        left = middle + 1
      }
    }
    indexFound
  }

}
