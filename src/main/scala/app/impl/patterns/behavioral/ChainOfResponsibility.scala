package app.impl.patterns.behavioral

/**
  * We found  one more time PartialFunction really handy in our designs, it give us the chance
  * that if the case of our patter match does not fit, we can invoke another handler using orElse operator
  * which will pass the event to the next handler defined.
  */
object ChainOfResponsibility extends App {

  case class Event(source: String)

  type EventHandler = PartialFunction[Event, Unit]

  val defaultHandler: EventHandler = PartialFunction(_ => println("no events found"))

  val keyboardHandler: EventHandler = {
    case Event("keyboard") => println("This is a key event")
  }

  def mouseHandler(delay: Int): EventHandler = {
    case Event("mouse") => println(s"This is a mouse event with delay $delay")
  }

  private val function = keyboardHandler.orElse(mouseHandler(100)).orElse(defaultHandler)
  function.apply(Event("mouse"))
  function.apply(Event("keyboard"))
  function.apply(Event(""))

}
