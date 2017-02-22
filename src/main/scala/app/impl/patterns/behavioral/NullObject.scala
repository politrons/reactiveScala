package app.impl.patterns.behavioral

/**
  * Option class give us the feature to wrap whatever value avoiding possible nullPointerExceptions.
  * In case the value wrapped is null, Option class will return None, otherwise will return Option with
  * the value inside
  */
object NullObject extends App {

  def getValue(): Option[String] = {
    Option(value)
  }

  var value: String = _
  println(getValue().isDefined)
  println(getValue())
  value = "no null values works"
  println(getValue().isDefined)
  println(getValue())

}
