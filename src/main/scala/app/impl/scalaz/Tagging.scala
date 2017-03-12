package app.impl.scalaz

import scalaz.@@

/**
  * Created by pabloperezgarcia on 09/03/2017.
  */
object Tagging extends App{


  object Tags {
    sealed class Id
  }
  type Id = String @@ Tags.Id
  def Id(s: String) = s.asInstanceOf[Id]

  println(Id("String Id"))

}
