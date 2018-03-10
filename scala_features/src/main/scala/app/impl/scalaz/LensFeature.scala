package app.impl.scalaz

import org.junit.Test

import scalaz.Lens

class LensFeature {

  case class Name(value: String)

  case class Person(name: Name, age: Int)

  case class Man(person: Person)

  case class Me(man: Man)

  //### Original value ######

  val pablo = Me(Man(Person(Name("Pablo"), 36)))

  //##############
  //### LENS  ####
  //##############
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

  @Test
  def copyNameWithLens(): Unit = {
    println(pablo)
    val lenForName = manLen >=> personLen >=> nameLen
    val paul = lenForName.set(pablo, Name("Paul"))
    println(paul)
  }

  @Test
  def copyAgeWithLens(): Unit = {
    println(pablo)
    val lenForAge = manLen >=> personLen >=> ageLen
    val pabloWithAge = lenForAge.set(pablo, 29)
    println(pabloWithAge)
  }

  @Test
  def copyAgeAndNameWithLens(): Unit = {
    println(pablo)
    val lenForAge = manLen >=> personLen >=> ageLen
    val lenForName = manLen >=> personLen >=> nameLen
    val paulWithAge = lenForAge.set(lenForName.set(pablo, Name("Paul")), 29)
    println(paulWithAge)
  }

  @Test
  def copyNameWithLensAndGetAllLevels(): Unit = {
    println(pablo)
    val lenForName = manLen >=> personLen >=> nameLen
    val paul = lenForName.set(pablo, Name("Paul"))
    val man = lenForName.set(pablo, Name("Paul")).man
    val person = lenForName.set(pablo, Name("Paul")).man.person
    val name = lenForName.set(pablo, Name("Paul")).man.person.name
    println(paul)
    println(man)
    println(person)
    println(name)
  }
}
