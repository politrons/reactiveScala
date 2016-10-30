package app.impl.scala

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper


/**
  * Created by pabloperezgarcia on 23/10/2016.
  */

object Polimorpishm {

  @JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
  )
  @JsonSubTypes(Array(
    new Type(value = classOf[Cat], name = "cat"),
    new Type(value = classOf[Dog], name = "dog")
  ))
  trait Animal

  case class Dog(name: String, breed: String, leash_color: String) extends Animal

  case class Cat(name: String, favorite_toy: String) extends Animal

  case class Zoo(animals: Iterable[Animal])

  def main(args: Array[String]): Unit = {
    val objectMapper = new ObjectMapper with ScalaObjectMapper
    objectMapper.registerModule(DefaultScalaModule)

    val dogStr = """{"type": "dog", "name": "Spike", "breed": "mutt",  "leash_color": "red"}"""
    val catStr = """{"type": "cat", "name": "Fluffy", "favorite_toy": "spider ring"}"""
    val zooStr = s"""{"animals":[$dogStr, $catStr]}"""

    val zoo = objectMapper.readValue[Zoo](zooStr)

    println(zoo)
    // Prints: Zoo(List(Dog(Spike,mutt,red), Cat(Fluffy,spider ring)))
  }


}

