package com.features.zio

import zio.{Has, Runtime, ZIO, ZLayer, ZManaged}

/**
 * [ZManaged] is a monad to wrap our resources, and allow us make composition with them, and once we want
 * to use them decide if we want to release it once they are used.
 * ZManaged also works like implicit to be set in the scope of your class and use [ZManaged.service] and [ZManaged.services]
 * to obtain the [Has] dependencies to be passed to your program.
 * ZManaged internally is just using bracket operator, which in case you know how works, allow wrap a resource,
 * pass a function to use it, and another to be executed once you finish to release the resource or do whatever we want.
 */
object ZIOManaged extends App {

  case class ServiceA(message: String)

  case class ServiceB(a: ServiceA)

  case class ServiceC(b: ServiceB)

  zManager()
  zManagerService()
  zManagerServices()

  /**
   * We use [ZManaged.succeed] to wrap just one resource, and once we want to use it in our program,
   * we can use [use] which expect a function extract the resource as ZIO program, and once it finished it will be released.
   * using [useNow] it will extract the ZIO program directly.
   */
  def zManager(): Unit = {
    val manager: ZManaged[Any, Nothing, ServiceA] = ZManaged.succeed(ServiceA("Hello manager"))

    Runtime.global.unsafeRun {
      for {
        message <- manager.use(value => ZIO.succeed(value.message.toUpperCase))
        _ <- ZIO.succeed(println(message))
      } yield ()
    }
  }

  /**
   * ZManaged is the best way of how we can extract dependencies passed into our program inside the program.
   * We initially define in the scope of our class the [ZManaged] where we can define how many dependencies we will managed.
   * We just set in the tag of the [ZManaged.service] the service Type we will pass [ServiceA]
   *
   * Then as usual we define our program with dependencies using [Has] and then internally
   * in our program, instead have to implement a DSL structure functions that get the value
   * of that dependency from the context we can use the [ZManaged] with [use] which it will
   * extract the dependency that we define previously.
   */
  def zManagerService(): Unit = {
    val serviceA = ServiceA("Hello manager")
    val dependencyManaged: ZManaged[Has[ServiceA], Nothing, ServiceA] = ZManaged.service[ServiceA]

    val program: ZIO[Has[ServiceA], Nothing, Unit] = for {
      message <- dependencyManaged.use(value => ZIO.succeed(value.message.toUpperCase))
      _ <- ZIO.succeed(println(message))
    } yield ()
    Runtime.global.unsafeRun(program.provideLayer(ZLayer.succeed(serviceA)))

  }

  /**
   * [Services] just like [service] work exactly the same, extracting the dependencies from the context
   * to be used into the program.
   * ZManaged.services[ServiceA, ServiceB] it will create a [ZManaged] with N [Has] as you define.
   * The way it will extracted it's the same than previous example, but instead to return just one dependency,
   * since they could be N dependencies, it will return a [TupleN]
   */
  def zManagerServices(): Unit = {
    val serviceA = ServiceA("Hello manager")
    val serviceB = ServiceB(serviceA)
    val serviceC = ServiceC(serviceB)

    val dependency: ZManaged[Has[ServiceA] with Has[ServiceB] with Has[ServiceC], Nothing, (ServiceA, ServiceB, ServiceC)] =
      ZManaged.services[ServiceA, ServiceB, ServiceC]

    val program: ZIO[Has[ServiceA] with Has[ServiceB] with Has[ServiceC], Nothing, Unit] = for {
      dependencies <- dependency.useNow
      message <- ZIO.succeed(dependencies._2.a.message.toUpperCase)
      _ <- ZIO.succeed(println(message))
    } yield ()
    Runtime.global.unsafeRun(program.provideLayer {
      ZLayer.succeed(serviceA) ++ ZLayer.succeed(serviceB) ++ ZLayer.succeed(serviceC)
    })
  }
}
