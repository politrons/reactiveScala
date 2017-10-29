package app.impl.di

import org.junit.Test

/**
  * Created by pabloperezgarcia on 26/10/2017.
  */
class CakePattern {

  trait SimpleTrait {
    def addOne(x: Int): Int = {
      x + 1
    }
  }

  class SimpleClass extends SimpleTrait

  val addOne = new SimpleClass

  trait UserRepositoryComponent {

    def userLocator: UserLocator

    def userUpdater: UserUpdater

    trait UserLocator {
      def findAll: List[String]
    }

    trait UserUpdater {
      def save(user: String): Unit
    }

  }

  trait UserRepositoryJPAComponent extends UserRepositoryComponent {

    def userLocator = new UserLocatorJPA()

    def userUpdater = new UserUpdaterJPA()

    class UserLocatorJPA() extends UserLocator {
      def findAll = List("Paul")
    }

    class UserUpdaterJPA() extends UserUpdater {
      def save(user: String) {
        println(user)
      }
    }

  }


  trait UserServiceComponent {

    def userService: UserService

    trait UserService {
      def findAll: List[String]

      def save(user: String)
    }

  }

  trait DefaultUserServiceComponent extends UserServiceComponent {

    this: UserRepositoryComponent =>

    def userService = new DefaultUserService

    class DefaultUserService extends UserService {

      def findAll = userLocator.findAll

      def save(user: String) {
        userUpdater.save(user: String)
      }
    }

  }

  object ApplicationLive {
    val userServiceComponent = new DefaultUserServiceComponent with UserRepositoryJPAComponent
    val userService = userServiceComponent.userService
  }

  @Test
  def mainTest(): Unit = {
    val userService = ApplicationLive.userService
    userService.findAll.foreach(value => println(value))
    userService.save("George")
  }

}
