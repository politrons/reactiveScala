package app.impl.engine

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import zio.ZIO
import RuleEngine._
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object RuleEngine {

  case class Field(value: String) extends AnyVal

  trait Operator

  case class Equals() extends Operator

  case class NotEquals() extends Operator

  case class HigherThan() extends Operator

  case class LowerThan() extends Operator

  case class Value(value: String) extends AnyVal

  case class Rule(field: Field,
                  operator: Operator,
                  value: Value)

  lazy val rules: Map[String, List[Rule]] = {
    Map("rule1" -> List(
      Rule(Field("foo"), Equals(), Value("hello world")),
      Rule(Field("locale.numberField"), HigherThan(), Value("1000"))),
      "rule2" -> List(
        Rule(Field("foo"), NotEquals(), Value("hello world")),
        Rule(Field("numberField"), LowerThan(), Value("1000")))
    )
  }

  lazy val mapper = new ObjectMapper().registerModule(DefaultScalaModule)

  def applyRules(jsons: List[String]): ZIO[Any, Throwable, List[String]] = {
    ZIO.foldLeft(jsons)(List[String]())((prev, next) => {
      val jsonMap = mapper.readValue(next, classOf[Map[String, String]])
//      jsonMap.filter(e => e._1 == )
      ZIO.effect(prev)
    })
  }
}

/**
 * Only for test propose
 */
class RuleEngine {

  val json =
    """
      |{
      |  "countryCode": "ES",
      |  "foo": "hello world",
      |  "locale": {
      |     "numberField":"2000"
      |  }
      |}
      |""".stripMargin

  @Test
  def main(): Unit = {
    val runtime: zio.Runtime[zio.ZEnv] = zio.Runtime.default
    val filterProducts: List[String] = runtime.unsafeRun(RuleEngine.applyRules(List(json)))
    println(filterProducts)
  }

}
