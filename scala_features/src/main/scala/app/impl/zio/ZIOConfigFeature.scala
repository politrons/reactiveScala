package app.impl.zio

import java.util.Properties

import org.junit.Test
import zio.config.ConfigDescriptor._
import zio.config._
import zio.{Layer, ZIO}

class ZIOConfigFeature {

  val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default

  case class UserName(value: String)

  /**
   * ADT to contain the config info we want to load to use in our program
   */
  case class UserConfig(username: UserName,
                        password: String,
                        age: Int,
                        address: Option[String],
                        multiValue: String)

  /**
   * [ConfigDescriptor] Config structure where we can specify which config alias we want to load into our [UserConfig] structure.
   * The way to bond the config with the ADT is by the order of definition.
   *
   * We can define strong type of T for a primitive, (T.apply, T.unapply) after primitive definition.
   * We can define one attribute as Option[T] type, using [optional] operator after definition.
   * We can define one attribute with default value using [default] passing the value for that config, if it does not exit.
   * We can bond one attribute or in case it is not in config to another, using <> operator, which is a [orElse] sugar syntax.
   */
  val userConfig: ConfigDescriptor[String, String, UserConfig] =
    (string("USERNAME")(UserName.apply, UserName.unapply) |@|
      string("PASSWORD") |@|
      int("AGE").default(18) |@|
      string("ADDRESS").optional |@|
      string("VALUE1") <> string("VALUE2")) (UserConfig.apply, UserConfig.unapply)

  /**
   * An example program where we load as Env argument the Config[UserConfig]
   * Once in the program using config[ADT] we can load the config in the program to be used.
   */
  val configProgram: ZIO[Config[UserConfig], Nothing, Unit] = for {
    myConfig <- config[UserConfig]
    _ <- ZIO.succeed(println(myConfig.username))
    _ <- ZIO.succeed(println(myConfig.password))
    _ <- ZIO.succeed(println(myConfig.age))
    _ <- ZIO.succeed(println(myConfig.address))
    _ <- ZIO.succeed(println(myConfig.multiValue))
  } yield ()

  /**
   * We can create ZLayer config using [Config.fromMap] where we have to pass the map with the config info
   * and the ConfigDescriptor [userConfig] to bond the config info into the ADT [UserConfig]
   * To run our program passing the config as Env argument we use [provideCustomLayer] passing the
   * ZLayer configLayer
   */
  @Test
  def configLayerFromMap(): Unit = {
    val map = Map(
      "USERNAME" -> "Politrons",
      "PASSWORD" -> "Rpdiuo34f@rr",
      "VALUE1" -> "Value1",
      "NotUsed" -> "Foo"
    )

    val configLayer: Layer[ReadError[String], Config[UserConfig]] = Config.fromMap(map, userConfig)

    runtime.unsafeRun(configProgram.provideCustomLayer(configLayer))

  }

  /**
   * We can also provide the Config in our ADT using java.util.Properties using [fromProperties]
   */
  @Test
  def configLayerFromProperties(): Unit = {
    val properties = new Properties()
    properties.put("USERNAME", "Politrons")
    properties.put("PASSWORD", "112223344")
    properties.put("AGE", "38")
    properties.put("ADDRESS", "Camilo Jose Cela")
    properties.put("VALUE2", "Value2")


    val configLayer: Layer[ReadError[String], Config[UserConfig]] = Config.fromProperties(properties, userConfig, "")

    runtime.unsafeRun(configProgram.provideCustomLayer(configLayer))

  }

  /**
   * Another ADT to load some env variable in our program
   */
  case class SysEnvConfig(home: String,
                          javaHome: String)

  val sysEnvConfig: ConfigDescriptor[String, String, SysEnvConfig] =
    (string("HOME") |@| string("JAVA_HOME")) (SysEnvConfig.apply, SysEnvConfig.unapply)

  val sysEnvProgram: ZIO[Config[SysEnvConfig], Nothing, Unit] = for {
    myConfig <- config[SysEnvConfig]
    _ <- ZIO.succeed(println(myConfig.home))
    _ <- ZIO.succeed(println(myConfig.javaHome))
  } yield ()

  /**
   * We can also create ZLayer config using [fromSystemEnv] which it will get all System environment of the machine
   * and it will bond with the [ConfigDescriptor] passed in the Config factory
   */
  @Test
  def configLayerFromSysEnv(): Unit = {

    val configLayer: Layer[ReadError[String], Config[SysEnvConfig]] = Config.fromSystemEnv(sysEnvConfig)

    runtime.unsafeRun(sysEnvProgram.provideCustomLayer(configLayer))

  }
}
