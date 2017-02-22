package app.impl.patterns.behavioral

import java.awt.Color
import java.awt.image.BufferedImage


/**
  * Thanks to type or case operator we can create immutable classes to represent type values in our classes
  * which in general always is much better use than the primitive types that language provide.
  * Increase readability and make our code thread safe
  */
object TypeClass extends App{

  //=============================//
  //        Definition           //
  //=============================//
  type Color = (Float, Float, Float)

  def color: Color = (234, 122, 144)

  case class Size(x:Int, y:Int)

  def size:Size= Size(100, 200)


  // create an image
  val canvas = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_RGB)

  // get Graphics2D for the image
  val g = canvas.createGraphics()

  // clear background
  g.setColor(Color.getHSBColor(color._1, color._2, color._3))



}
