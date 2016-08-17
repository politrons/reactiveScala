package app.impl.scala

import org.junit.Test


/**
  * In order to use match patter per new classes, scala force us to create a singleton Object per class.
  * This object needs to implement the unapply function, which will return  Option[whatever_you_want]
  *
  */
class Extractor {

  /**
    * This example since we´e passing two implementation of User, it will match the object and return
    * the unapply function value
    */
  @Test
  def userFound(): Unit = {
    val user: User = new PremiumUser("Paul",35)
    val result = user match {
      case FreeUser(freeUser) => "FreeUser:" + freeUser.name + " age:" + freeUser.age
      case PremiumUser(name, age) => "Premium user:" + name + " age:" + age
      case _ => "User not found"
    }
    println(result)

    val user1: User = new FreeUser("Paul1",35)
    val result1 = user1 match {
      case FreeUser(freeUser) => "FreeUser:" + freeUser.name + " age:" + freeUser.age
      case PremiumUser(name, age) => "Premium user:" + name + " age:" + age
      case _ => "User not found"
    }
    println(result1)
  }

  /**
    * In this example, since we are passing an object which does not apply any of match pattern classes return default.
    * Shall print "User not found"
    */
  @Test
  def userNotFound(): Unit = {
    val user = new Object()
    val result = user match {
      case FreeUser(name) => "Hello " + name.name
      case PremiumUser(name, age) => "Premium user:" + name + " age:" + age
      case _ => "User not found"
    }
    println(result)
  }

  trait User {
    def name: String
    def age: Int
  }

  class FreeUser(val name: String, val age:Int) extends User

  class PremiumUser(val name: String, val age:Int) extends User

  object FreeUser {
    def unapply(user: FreeUser): Option[User] = Some(user)
  }

  object PremiumUser {
    def unapply(user: PremiumUser): Option[(String, Int)] = Some(user.name, user.age)
  }

}
