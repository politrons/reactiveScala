package app.impl.scala

import org.junit.Test

/**
  * Universe is the entry point to Scala reflection. A universe provides an interface to all the principal concepts
  * used in reflection, such as Types, Trees, and Annotations
  */
import scala.reflect.runtime.{universe => ru}

class ReflectionFeature {

  //  INSPECTING A RUNTIME TYPE (INCLUDING GENERIC TYPES AT RUNTIME)
  /**
    * First of all we need to use JavaUniverse in order to investigate the classLoader.
    * Once we have the JavaUniverse we can use typeTag, which we can use to extract information from an instance
    * in Runtime, such as type.
    */
  @Test
  def inspecting(): Unit = {
    val l = List(1, 2, 3)
    // using  ru.typeTag[T] we create a TypeTag of T
    def getTypeTag[T: ru.TypeTag](obj: T) = ru.typeTag[T]
    //One of the API is obtain the type of the class
    val theType = getTypeTag(l).tpe
    println(theType)
    //With decls over the TypeTag we can obtain the list of members of this Type, like constructors and methods
    val decls = theType.decls.take(5)
    println(decls)
  }


  //  INSTANTIATING A TYPE AT RUNTIME
  @Test
  def instantiating(): Unit = {
    //  In the first step we obtain a mirror m which makes all classes and
    //  types available that are loaded by the current classloader, including class Person.
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val classPerson = ru.typeOf[Person].typeSymbol.asClass
    //We create a person Mirror as representation of the Person class
    val personMirror = mirror.reflectClass(classPerson)
    //We create a constructor Symbol, which is used together with a mirror to obtain the element
    val personConstructorSymbol = ru.typeOf[Person].decl(ru.termNames.CONSTRUCTOR).asMethod
    val personConstructor = personMirror.reflectConstructor(personConstructorSymbol)
    val person = personConstructor("Mike")
    println(person)
  }

  //  ACCESSING AND INVOKING MEMBERS OF RUNTIME TYPES
  @Test
  def accessing(): Unit = {
    val person = Purchase("Paul Perez", 28232, false)
    //As before we create the mirror using the the classLoader of person class
    val mirror = ru.runtimeMirror(person.getClass.getClassLoader)
    //We get the TermSymbol of the attribute shipped by name.
    val shippingTermSymb = ru.typeOf[Purchase].decl(ru.TermName("shipped")).asTerm
   //We create an instance Mirror which we will use to extract together with the Symbol the value of attribute.
    val instanceMirror = mirror.reflect(person)
    //A field mirror which already contains the access of the value for the attribute we're looking for.
    val shippingFieldMirror = instanceMirror.reflectField(shippingTermSymb)
    println(shippingFieldMirror.get)

  }

}

case class Purchase(name: String, orderNumber: Int, var shipped: Boolean)

case class Person(name: String)

