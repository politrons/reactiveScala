package gatling

import io.gatling.core.Predef.scenario
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.core.Predef._
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.concurrent.duration._
import scala.concurrent.duration.{Duration, SECONDS}

class SimulationGatling extends Simulation{


  def create(): ScenarioBuilder = {
    scenario("name")
      .exec(http("HealthCheck")
        .get(s"http://localhost:8080"))
      .pause(Duration(1, SECONDS))
  }

  private val step: OpenInjectionStep = rampUsers(10).during(10.seconds)
  create().inject(step)
}
