package app.impl.scala

import org.junit.Test

/**
  * Universe is the entry point to Scala reflection. A universe provides an interface to all the principal concepts
  * used in reflection, such as Types, Trees, and Annotations
  */
import scala.reflect.runtime.{universe}

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
    def getTypeTag[T: universe.TypeTag](obj: T) = universe.typeTag[T]
    //One of the API is obtain the type of the class
    val theType = getTypeTag(l).tpe
    println(theType)
    //With decls over the TypeTag we can obtain the list of members of this Type, like constructors and methods
    val decls = theType.decls.take(5)
    println(decls)
  }

  //  INSTANTIATING A TYPE AT RUNTIME
  /**
    * All the access to attributes of a class is always done through the [ClassMirror].
    * Previously this class mirror is obtain through a Mirror that represent all ClassLoader together
    * with the TypeTag of the class [Person]
    * Once you have the ClassMirror you can obtain the declared methods of the ClassMirror such as the constructor.
    * Then invoking the constructor you're able to create a new instance of Person
    */
  @Test
  def instantiating(): Unit = {
    //  In the first step we obtain a mirror m which makes all classes and
    //  types available that are loaded by the current classloader, including class Person.
    val mirror = universe.runtimeMirror(getClass.getClassLoader)
    val classPerson = universe.typeOf[Person].typeSymbol.asClass
    //We create a person Mirror as representation of the Person class
    val personMirror = mirror.reflectClass(classPerson)
    //We create a constructor Symbol, which is used together with a mirror to obtain the element
    val personConstructorSymbol = universe.typeOf[Person].decl(universe.termNames.CONSTRUCTOR).asMethod
    val personConstructor = personMirror.reflectConstructor(personConstructorSymbol)
    val person = personConstructor("Mike")
    println(person)
  }

  //  ACCESSING AND INVOKING MEMBERS OF RUNTIME TYPES
  /**
    * All the access to attributes of an instance is always done through the [InstanceMirror].
    * In this case if you have a instance, is much easier to access one of the maybe private access attributes of a class.
    * You just need to obtain a mirror, which you can just use the own instance person to get the classLoader and then
    * Through the universe obtain the mention mirror.
    * Before use that mirror we need a representation of the field we want to access. That is accessible through the TermSymbol
    * Once we have the mirror and the termSymbol, using [reflect] passing the instance we obtain the [InstanceMirror] and with this
    * mirror using reflectField and passing the symbol we obtain the value of the attribute.
    */
  @Test
  def accessing(): Unit = {
    val person = Purchase("Paul Perez", 28232, false)
    //As before we create the mirror using the the classLoader of person class
    val mirror = universe.runtimeMirror(person.getClass.getClassLoader)
    //We get the TermSymbol of the attribute shipped by name.
    val shippingTermSymbol = universe.typeOf[Purchase].decl(universe.TermName("shipped")).asTerm
   //We create an instance Mirror which we will use to extract together with the Symbol the value of attribute.
    val instanceMirror = mirror.reflect(person)
    //A field mirror which already contains the access of the value for the attribute we're looking for.
    val shippingFieldMirror = instanceMirror.reflectField(shippingTermSymbol)
    println(shippingFieldMirror.get)

  }

}

case class Purchase(name: String, orderNumber: Int, var shipped: Boolean)

case class Person(name: String)

