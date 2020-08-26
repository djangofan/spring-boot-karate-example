package com.test.perf

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._

class KarateSimulation extends Simulation {

  val DEMO = scenario("DEMO").exec(karateFeature("classpath:features/demo.feature"))

  setUp (
    DEMO.inject(
      constantUsersPerSec(2) during(1 minute)
    )
  )

}
