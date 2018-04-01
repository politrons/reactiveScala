package app.impl.shapeless

import org.junit.Test
import shapeless.tag
import shapeless.tag._

import scalapb.descriptors.Reads


/**
  * Tags is one of the best features from Shapeless it will help to avoid the boiler plate of create
  * all case class for all primitives types that you want to type.
  * You just need to create your tags, which are simple traits.
  *
  * Once you have your trait defined you just need to assign to a primitive value adding the @@ TagName
  * For now on, that argument cannot be passed as primitive but as a @@[PrimitiveType, TypeName]
  */
class TagsFeature {

  trait Name // this is tag type

  // this code is ok, can use tagged String as a plain one
  def addName(value: String @@ Name): String = s"Tagged with name: $value"

  @Test
  def main(): Unit ={
    //  addName("plain name ") // Compiler error
    val taggedName: @@[String, Name] = tag[Name]("Paul") // tag a string value with JustTag
    val value = addName(taggedName)
    println(value)
  }

}
