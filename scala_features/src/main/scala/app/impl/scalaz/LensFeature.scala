package app.impl.scalaz

import org.junit.Test

import scalaz.Lens

/**
  * In scala when we manage immutable class and we want to "modify" a value of this class, we create a new class since
  * the other it´s immutable and we copy every value from one to another but the new value which it´s set in the new class.
  * When we have  multiple nested classes the thing get complicated and verbose a little bit.
  *
  * That´s when Scalaz Lens comes to help us. Basically the Lens pattern it's meant to create "links of copy" like if the
  * class and the nested classes would be a chain, where every link it's able to modify a value of that chain.
  *
  * Then the idea is that you can reuse those links and compose chains together to modify values of the chain.
  */
class LensFeature {

  case class Name(value: String)

  case class Person(name: Name, age: Int)

  case class Man(person: Person)

  case class Me(man: Man)

  //### Original value ######
  val pablo = Me(Man(Person(Name("Pablo"), 36)))
  val john = Me(Man(Person(Name("Johny"), 30)))


  //##############
  //### LENS  ####
  //##############
  /**
    * Here we define our lens in order to be reused in future copy from our immutable class.
    * We define a link in the chain per len, between the class that contains the value to copy [ContainerClass]
    * and the value to be copy T. Lens[ContainerClass, T]
    * In here we have a len which will copy the class Name inside the class Person [Person, Name]
    */
  val nameLen: Lens[Person, Name] = Lens.lensu[Person, Name](
    (person, _name) => person.copy(name = _name), _.name
  )

  val ageLen: Lens[Person, Int] = Lens.lensu[Person, Int](
    (person, _age) => person.copy(age = _age), _.age
  )

  val personLen: Lens[Man, Person] = Lens.lensu[Man, Person](
    (man, _person) => man.copy(person = _person), _.person
  )

  val manLen: Lens[Me, Man] = Lens.lensu[Me, Man](
    (me, _man) => me.copy(man = _man), _.man
  )

  /**
    * This is an example how the copy of one value from one case class it would be.
    */
  @Test
  def normalCopy(): Unit = {
    println(pablo)
    val paul = pablo
      .copy(man = pablo.man
        .copy(person = pablo.man.person
          .copy(name = pablo.man.person.name
            .copy("Paul"), pablo.man.person.age)))
    println(paul)
  }


  /**
    * Here instead we use lens for the Name class, as you can see it´s less verbose and most important the already created lens
    * are reusable.
    * Here we combine lens as links to create a chain [val chain = link >=> link >=> link]  form the origin to the link that contains
    * the attribute to change.
    * Once that we have the chain of lens we use it passing the original instance and the value that we want to modify from the
    * original instance.
    * This lens internally will make all the verbose copies and it will return a new instance with the copied values and the new one
    * that you want to modify.
    */
  @Test
  def copyNameWithLens(): Unit = {
    println(pablo)
    val lensForName = manLen >=> personLen >=> nameLen
    val paul = lensForName.set(pablo, Name("Paul"))
    println(paul)
  }

  @Test
  def copyAgeWithLens(): Unit = {
    println(pablo)
    val lensForAge = manLen >=> personLen >=> ageLen
    val pabloWithAge = lensForAge.set(pablo, 29)
    println(pabloWithAge)
  }

  /**
    * In this example we see that we can use multiple chains of lens in one instance at the same time, to return
    * a new instance with all changes at once.
    */
  @Test
  def copyAgeAndNameWithLens(): Unit = {
    println(pablo)
    val lensForPerson = manLen >=> personLen
    val lensForAge = lensForPerson >=> ageLen
    val lensForName = lensForPerson >=> nameLen
    val paulWithAge = lensForAge.set(lensForName.set(pablo, Name("Paul")), 29)
    println(paulWithAge)
  }

  @Test
  def copyNameWithLensAndGetAllLevels(): Unit = {
    println(pablo)
    val lensForName = manLen >=> personLen >=> nameLen
    val paul = lensForName.set(pablo, Name("Paul"))
    val man = lensForName.set(pablo, Name("Paul")).man
    val person = lensForName.set(pablo, Name("Paul")).man.person
    val name = lensForName.set(pablo, Name("Paul")).man.person.name
    println(paul)
    println(man)
    println(person)
    println(name)
  }

  /**
    * You can also not only set value and create new class but get values from the existing one.
    */
  @Test
  def getWithLens(): Unit = {
    println(pablo)
    val lensForPerson = manLen >=> personLen
    val lensForAge = lensForPerson >=> ageLen
    val lensForName = lensForPerson >=> nameLen
    val age = lensForAge.get(pablo)
    val name = lensForName.get(john)
    println(age)
    println(name)
  }
}
