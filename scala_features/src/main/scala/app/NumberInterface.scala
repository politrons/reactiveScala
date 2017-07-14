package app

/**
  * Created by pabloperezgarcia on 2/7/16.
  */
trait NumberInterface {

  def isHigherThan1(obj: Int): Boolean

  def defaultImpl(): String = "This is a default implementation"
}
